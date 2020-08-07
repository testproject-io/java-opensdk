/*
 * Copyright (c) 2020 TestProject LTD. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.testproject.sdk.internal.rest;

import com.google.gson.*;
import io.testproject.sdk.internal.addons.ActionProxy;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.internal.reporting.inferrers.GenericInferrer;
import io.testproject.sdk.internal.reporting.inferrers.InferrerFactory;
import io.testproject.sdk.internal.rest.messages.*;
import io.testproject.sdk.internal.rest.serialization.DriverExclusionStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public final class AgentClient implements Closeable {

    /**
     * Maximum amount of time to wait in seconds before forcibly terminating the queue.
     */
    public static final int REPORTS_QUEUE_TIMEOUT = 10;

    /**
     * Constant for a custom capability name used to track AgentClient instances.
     */
    private static final String TP_GUID = "tp:guid";

    /**
     * Constant for environment variable name that may store Agent base URL.
     */
    private static final String TP_AGENT_URL = "TP_AGENT_URL";

    /**
     * Constant for environment variable name that may store the Development token.
     */
    private static final String TP_DEV_TOKEN = "TP_DEV_TOKEN";

    /**
     * Default Agent API base URL address.
     */
    private static final String AGENT_DEFAULT_API_ADDRESS = "http://localhost:8585";

    /**
     * HTTP connection timeout in milliseconds.
     */
    private static final int CONNECTION_TIMEOUT_MS = 5 * 1000;

    /**
     * HTTP connection request timeout in milliseconds.
     */
    private static final int CONNECTION_REQUEST_TIMEOUT_MS = 5 * 1000;

    /**
     * New Session HTTP connection request timeout in milliseconds.
     */
    private static final int NEW_SESSION_SOCKET_TIMEOUT_MS = 120 * 1000;

    /**
     * Addon execution HTTP connection request timeout in milliseconds.
     */
    private static final int ADDON_EXECUTION_SOCKET_TIMEOUT_MS = 60 * 1000;

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AgentClient.class);

    /**
     * AgentClient instance used by active driver.
     */
    private static AgentClient instance;

    /**
     * An instance of the Google JSON serializer to serialize and deserialize objects.
     */
    private static final Gson GSON = new GsonBuilder().setExclusionStrategies(new DriverExclusionStrategy()).create();

    /**
     * Reports executor service with a single thread.
     */
    private final ExecutorService reportsExecutorService = Executors.newSingleThreadExecutor();

    /**
     * Class member to store actual Agent API address.
     */
    private final URL remoteAddress;

    /**
     * Class member to store actual Development token.
     */
    private final String token;

    /**
     * Class member to store HTTP client instance.
     */
    private final CloseableHttpClient httpClient;

    /**
     * Future to keep the async task of starting the reports queue.
     */
    private Future<?> reportsQueueFuture;

    /**
     * Reference to the reports queue instance.
     */
    private ReportsQueue reportsQueue;

    /**
     * Class member to store Agent session details.
     */
    private AgentSession session;

    /**
     * Class member to hold an instance of a TCP socket between the SDK and the Agent.
     */
    private Socket socket;

    /**
     * Project name to report.
     */
    private String projectName;

    /**
     * Job name to report.
     */
    private String jobName;

    /**
     * Creates a new instance of the class.
     * Initiates a development session with the Agent.
     *
     * @param remoteAddress  Agent API base URL<br>
     *                       If not provided, will attempt to get the value from <b>TP_AGENT_URL</b>
     *                       environment variable.<br>
     *                       If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * @param token          Development token, required to initialize a session with the Agent.
     *                       If not provided, will attempt to get the value from <b>TP_DEV_TOKEN</b>
     *                       environment variable.<br>
     *                       It can be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page.
     * @param capabilities   capabilities to use for initializing the driver.
     * @param reportSettings {@link ReportSettings} with Project and Job names to report
     * @param disableReports True to enable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    private AgentClient(final URL remoteAddress, final String token, final Capabilities capabilities,
                        final ReportSettings reportSettings, final boolean disableReports)
            throws MalformedURLException, InvalidTokenException, AgentConnectException,
            ObsoleteVersionException {

        // Determine Agent API address
        if (remoteAddress != null) {
            this.remoteAddress = remoteAddress;
        } else {
            if (!StringUtils.isEmpty(System.getenv(TP_AGENT_URL))) {
                this.remoteAddress = new URL(System.getenv(TP_AGENT_URL));
            } else {
                this.remoteAddress = new URL(AGENT_DEFAULT_API_ADDRESS);
            }
        }

        // Determine Development Token
        if (!StringUtils.isEmpty(token)) {
            this.token = token;
        } else if (!StringUtils.isEmpty(System.getenv(TP_DEV_TOKEN))) {
            this.token = System.getenv(TP_DEV_TOKEN);
        } else {
            throw new InvalidTokenException("No token has been provided.");
        }

        // Initialize HTTP Client
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .addInterceptorLast((HttpRequestInterceptor) (request, context) -> {
                    request.setHeader(HttpHeaders.AUTHORIZATION, this.token);
                    request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
                    request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
                });
        this.httpClient = httpClientBuilder.build();

        // Start Session
        ReportSettings sessionReportSettings = disableReports ? null : inferReportSettings(reportSettings);
        startSession(capabilities, sessionReportSettings);

        // Start reports queue
        if (!disableReports) {
            this.reportsQueue = new ReportsQueue(this.httpClient);
            this.reportsQueueFuture = reportsExecutorService.submit(this.reportsQueue);
        }

        // Make sure to exit gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    /**
     * Creates (or searches for an existing) instance of {@link AgentClient} using provided capabilities.
     *
     * @param capabilities {@link Capabilities} to be used for creating {@link AgentClient} or finding a cached one
     * @return An instance of an AgentClient class.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public static AgentClient getClient(final Capabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException, ObsoleteVersionException {
        return getClient(null, null, capabilities, null, false);
    }

    /**
     * Creates (or searches for an existing) instance of {@link AgentClient} using provided capabilities.
     *
     * @param capabilities   {@link Capabilities} to be used for creating {@link AgentClient} or finding a cached one
     * @param reportSettings {@link ReportSettings} with Project and Job names to report
     * @return An instance of an AgentClient class.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public static AgentClient getClient(final Capabilities capabilities, final ReportSettings reportSettings)
            throws AgentConnectException, InvalidTokenException, MalformedURLException, ObsoleteVersionException {
        return getClient(null, null, capabilities, reportSettings, true);
    }

    /**
     * Creates (or searches for an existing) instance of {@link AgentClient} using provided token and capabilities.
     *
     * @param token        Development token, required to initialize a session with the Agent.
     *                     If not provided, will attempt to get the value from <b>TP_DEV_TOKEN</b>
     *                     environment variable.<br>
     *                     It can be obtained from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page.
     * @param capabilities {@link Capabilities} to be used for creating {@link AgentClient} or finding a cached one
     * @return An instance of an AgentClient class.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public static AgentClient getClient(final String token, final Capabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException, ObsoleteVersionException {
        return getClient(null, token, capabilities, null, false);
    }

    /**
     * Creates (or searches for an existing) instance of {@link AgentClient} using provided token and capabilities.
     *
     * @param token          Development token, required to initialize a session with the Agent.
     *                       If not provided, will attempt to get the value from <b>TP_DEV_TOKEN</b>
     *                       environment variable.<br>
     *                       It can be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page.
     * @param capabilities   {@link Capabilities} to be used for creating {@link AgentClient} or finding a cached one
     * @param reportSettings {@link ReportSettings} with Project and Job names to report
     * @return An instance of an AgentClient class.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public static AgentClient getClient(final String token, final Capabilities capabilities,
                                        final ReportSettings reportSettings)
            throws AgentConnectException, InvalidTokenException, MalformedURLException, ObsoleteVersionException {
        return getClient(null, token, capabilities, reportSettings, true);
    }

    /**
     * Creates (or searches for an existing) instance of {@link AgentClient}
     * using provided remoteAddress and capabilities.
     *
     * @param remoteAddress  Agent API base URL<br>
     *                       If not provided, will attempt to get the value from <b>TP_AGENT_URL</b>
     *                       environment variable.<br>
     *                       If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * @param capabilities   {@link Capabilities} to be used for creating {@link AgentClient} or finding a cached one
     * @param reportSettings {@link ReportSettings} with Project and Job names to report
     * @return An instance of an AgentClient class.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public static AgentClient getClient(final URL remoteAddress, final Capabilities capabilities,
                                        final ReportSettings reportSettings)
            throws AgentConnectException, InvalidTokenException, MalformedURLException, ObsoleteVersionException {
        return getClient(remoteAddress, null, capabilities, reportSettings, true);
    }

    /**
     * Creates (or searches for an existing) instance of {@link AgentClient}
     * using provided remoteAddress and capabilities.
     *
     * @param remoteAddress Agent API base URL<br>
     *                      If not provided, will attempt to get the value from <b>TP_AGENT_URL</b>
     *                      environment variable.<br>
     *                      If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * @param capabilities  {@link Capabilities} to be used for creating {@link AgentClient} or finding a cached one
     * @return An instance of an AgentClient class.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public static AgentClient getClient(final URL remoteAddress, final Capabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException, ObsoleteVersionException {
        return getClient(remoteAddress, null, capabilities, null, false);
    }

    /**
     * Creates (or searches for an existing) instance of {@link AgentClient}
     * using provided remoteAddress, token and capabilities.
     *
     * @param remoteAddress  Agent API base URL<br>
     *                       If not provided, will attempt to get the value from <b>TP_AGENT_URL</b>
     *                       environment variable.<br>
     *                       If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * @param token          Development token, required to initialize a session with the Agent.
     *                       If not provided, will attempt to get the value from <b>TP_DEV_TOKEN</b>
     *                       environment variable.<br>
     *                       It can be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page.
     * @param capabilities   {@link Capabilities} to be used for creating {@link AgentClient} or finding a cached one
     * @param reportSettings {@link ReportSettings} with Project and Job names to report
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @return An instance of an AgentClient class.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public static AgentClient getClient(final URL remoteAddress, final String token,
                                        // Below is an ugly trick to make sure TP_GUID capability is always set
                                        // It changed the method parameter 'capabilities' and hence it's not final
                                        @SuppressWarnings("checkstyle:finalparameters") Capabilities capabilities,
                                        final ReportSettings reportSettings,
                                        final boolean disableReports)
            throws AgentConnectException, InvalidTokenException, MalformedURLException, ObsoleteVersionException {

        // If capabilities object doesn't have a custom session tracking capability - set it here
        if (!capabilities.asMap().containsKey(TP_GUID)) {
            MutableCapabilities newCapabilities = new MutableCapabilities();
            newCapabilities.setCapability(TP_GUID, UUID.randomUUID().toString());
            capabilities = capabilities.merge(newCapabilities);
        }

        // Synchronized to avoid possible multiple threads race condition
        synchronized (AgentClient.class) {

            // Check if an instance of an AgentClient class has been already cached
            if (instance == null || !instance.getSession().getCapabilities().getCapability(TP_GUID).equals(
                    capabilities.getCapability(TP_GUID))) {


                // No instance yet or it's for another driver and needs to be re-initialized
                instance = new AgentClient(remoteAddress, token, capabilities, reportSettings, disableReports);
            }
        }

        return instance;
    }

    /**
     * Closes the cached instance and removes it.
     */
    public static void cleanup() {
        Objects.requireNonNull(instance).close();
    }

    private RequestConfig getDefaultHttpConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(CONNECTION_TIMEOUT_MS)
                .build();
    }

    /**
     * Starts a new session with the Agent.<br>
     * Sends a request to Agent's RESTful API with required capabilities.
     *
     * @param capabilities   capabilities with requested driver details.
     * @param reportSettings {@link ReportSettings} with Project and Job names to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    private void startSession(final Capabilities capabilities, final ReportSettings reportSettings)
            throws InvalidTokenException, AgentConnectException, ObsoleteVersionException {
        LOG.info("Initializing new session...");
        LOG.trace("Initializing new session with capabilities: {}", GSON.toJson(capabilities));

        // Extract TP_GUID capability
        String guid = Objects.requireNonNull(capabilities.getCapability(TP_GUID)).toString();

        // Initialize POST request to Agent API
        HttpPost httpPost = new HttpPost(remoteAddress + Routes.DEVELOPMENT_SESSION);

        // New session initialization can take up to 120s
        // This is why this config is unique and other calls use getDefaultConfig() method
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(NEW_SESSION_SOCKET_TIMEOUT_MS)
                .build();
        httpPost.setConfig(config);

        // Prepare Payload
        SessionRequest request = new SessionRequest(reportSettings, capabilities.asMap());

        // Save report settings
        this.projectName = request.getProjectName();
        this.jobName = request.getJobName();

        StringEntity entity = new StringEntity(GSON.toJson(request), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // Send POST request
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new AgentConnectException("Failed communicating with the Agent at " + this.remoteAddress, e);
        }

        // Handle unsuccessful response (not 2xx)
        if (response.getStatusLine().getStatusCode() < HttpURLConnection.HTTP_OK
                || response.getStatusLine().getStatusCode() >= HttpURLConnection.HTTP_MULT_CHOICE) {
            handleSessionStartFailure(response);
            return;
        }

        // Read Response
        String responseBody;
        try {
            responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            LOG.error("Failed reading Agent response", e);
            throw new AgentConnectException("Failed reading Agent response", e);
        }

        LOG.trace("Session initialized: {}", responseBody);

        SessionResponse agentResponse;
        try {
            agentResponse = GSON.fromJson(responseBody, SessionResponse.class);
        } catch (JsonSyntaxException e) {
            LOG.error("Failed to parse Agent response", e);
            throw new AgentConnectException("Failed to parse Agent response", e);
        }

        // Process Response
        try {
            MutableCapabilities mutableCapabilities =
                    new MutableCapabilities(agentResponse.getCapabilities());

            // There's a chance that driver ignored our custom tracking capability
            // And now does't return it with the actual driver capabilities
            // It has to be set again to avoid initializing an new AgentClient instance for these capabilities again
            mutableCapabilities.setCapability(TP_GUID, guid);
            this.session = new AgentSession(
                    new URL(agentResponse.getServerAddress()),
                    agentResponse.getSessionId(),
                    Dialect.valueOf(agentResponse.getDialect()),
                    mutableCapabilities);
        } catch (MalformedURLException e) {
            LOG.error("Agent returned an invalid server URL: [{}]", agentResponse.getServerAddress(), e);
            throw new AgentConnectException("Failed initializing a session", e);
        }

        // Connect to Agent TCP socket
        try {
            socket = new Socket(this.remoteAddress.getHost(), agentResponse.getDevSocketPort());
        } catch (IOException e) {
            LOG.error("Failed connecting to Agent socket at {}:{}",
                    this.remoteAddress.getHost(), agentResponse.getDevSocketPort(), e);
            throw new AgentConnectException("Failed connecting to Agent socket", e);
        }
    }

    /**
     * Infer Project and Job names from call stack.
     *
     * @return {@link ReportSettings} instance with discovered Project and Job names.
     * @param reportSettings Project and Job names provided explicitly.
     */
    private ReportSettings inferReportSettings(final ReportSettings reportSettings) {

        if (reportSettings != null
                && !StringUtils.isEmpty(reportSettings.getProjectName())
                && !StringUtils.isEmpty(reportSettings.getJobName())) {
            LOG.trace("Project and Job names were explicitly set, skipping inferring.");
            return reportSettings;
        }

        LOG.trace("Report settings were not provided or incomplete, trying to infer...");

        // Grab stack traces to analyze callers and infer Project/Job names
        List<StackTraceElement> traces = Arrays.asList(Thread.currentThread().getStackTrace());

        // Try to infer Project and Job names from Unit Testing FWs annotations
        ReportSettings inferredReportSettings = InferrerFactory.getInferrer(traces).inferReportSettings();

        // Inferrer returned empty ReportSettings
        if (inferredReportSettings == null) {
            inferredReportSettings = new GenericInferrer(traces).inferReportSettings();
        }

        LOG.info("Inferred [{}] and [{}] for Project and Job names accordingly.",
                inferredReportSettings.getProjectName(), inferredReportSettings.getJobName());

        ReportSettings result;
        if (reportSettings != null) {
            // Explicitly provided names override inferred names
            result = new ReportSettings(
                    !StringUtils.isEmpty(reportSettings.getProjectName())
                            ? reportSettings.getProjectName() : inferredReportSettings.getProjectName(),
                    !StringUtils.isEmpty(reportSettings.getJobName())
                            ? reportSettings.getJobName() : inferredReportSettings.getJobName());
        } else {
            // Nothing provided, using only inferred names
            result = inferredReportSettings;
        }

        LOG.info("Using [{}] and [{}] for Project and Job names accordingly.",
                result.getProjectName(), result.getJobName());

        return result;
    }

    /**
     * Handle a scenario when Agent session initialization fails.
     *
     * @param response Response to the RESTful endpoint call sent to Agent
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    private void handleSessionStartFailure(final CloseableHttpResponse response)
            throws InvalidTokenException, ObsoleteVersionException, AgentConnectException {
        String statusMessage = null;

        try {
            String responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
            JsonObject json = (JsonObject) new JsonParser().parse(responseBody);
            JsonElement message = json.get("message");
            statusMessage = message != null && !message.isJsonNull() ? message.getAsString() : null;
        } catch (IOException e) {
            LOG.error("Failed reading Agent response", e);
        }

        // Inspect Response Status Code
        switch (response.getStatusLine().getStatusCode()) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                LOG.error("Failed to initialize a session with the Agent - token is invalid");
                throw new InvalidTokenException();
            case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
                LOG.error("Failed to initialize a session with the Agent - obsolete SDK version");
                throw new ObsoleteVersionException(statusMessage);
            default:
                LOG.error("Failed to initialize a session with the Agent");
                throw new AgentConnectException("Agent responded with status "
                        + response.getStatusLine().getStatusCode() + ": [" + statusMessage + "]");
        }
    }

    /**
     * Getter for {@link #session} field.
     *
     * @return value of {@link #session} field
     */
    public AgentSession getSession() {
        return session;
    }

    /**
     * Getter for {@link #projectName}.
     *
     * @return value of {@link #projectName} field
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Getter for {@link #jobName}.
     *
     * @return value of {@link #jobName} field
     */
    public String getJobName() {
        return this.jobName;
    }

    /**
     * Getter for {@link #projectName} and {@link #jobName} as {@link ReportSettings}.
     *
     * @return value of {@link #projectName} and {@link #jobName} fields wrapped in {@link ReportSettings}
     */
    public ReportSettings getReportSetting() {
        if (StringUtils.isEmpty(projectName) || StringUtils.isEmpty(jobName)) {
            return null;
        }
        return new ReportSettings(this.projectName, this.jobName);
    }

    /**
     * Implementation of {@link Closeable Closable} interface.
     * Disconnects the TCP socket between the SDK and the Agent to stop the development session.
     */
    @Override
    public void close() {
        if (reportsQueueFuture != null && !reportsQueueFuture.isDone()) {
            reportsQueue.stop();
            try {
                reportsQueueFuture.get(REPORTS_QUEUE_TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOG.error("Reports queue was interrupted while sending reports.");
            } catch (ExecutionException e) {
                LOG.error("Reports queue has thrown an exception", ExceptionUtils.getRootCause(e));
            } catch (TimeoutException e) {
                LOG.error("Reports queue didn't finish uploading reports in a timely manner and was terminated.");
            }

            if (!reportsQueueFuture.isDone()) {
                LOG.warn("Terminating reports queue forcibly...");
                reportsQueueFuture.cancel(true);
            }
        }

        if (!reportsExecutorService.isTerminated()) {
            reportsExecutorService.shutdown();
        }

        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
                LOG.info("Session closed.");
            } catch (IOException e) {
                LOG.error("Failed closing socket connected to the Agent at {}", this.remoteAddress, e);
            }
        }
    }

    /**
     * Reports a driver command execution to the Agent.
     *
     * @param command Command executed by the driver.
     * @param result  Command result formatted as String
     * @param passed  Boolean flag to indicate command successful execution or failure.
     * @return True if successfully reported, otherwise False.
     */
    public boolean reportCommand(final Command command, final Object result, final boolean passed) {
        // Initialize POST request to Agent API
        HttpPost httpPost = new HttpPost(remoteAddress + Routes.REPORT_COMMAND);
        httpPost.setConfig(getDefaultHttpConfig());

        // Prepare payload
        DriverCommandReport report =
                new DriverCommandReport(command.getName(), command.getParameters(), result, passed);
        StringEntity entity = new StringEntity(GSON.toJson(report), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // Send POST request
        this.reportsQueue.submit(httpPost, report);
        return true;
    }

    /**
     * Reports a step to the Agent.
     *
     * @param report Report to submit.
     * @return True is successful, otherwise False.
     */
    public boolean reportStep(final StepReport report) {
        // Initialize POST request to Agent API
        HttpPost httpPost = new HttpPost(remoteAddress + Routes.REPORT_STEP);
        httpPost.setConfig(getDefaultHttpConfig());

        // Prepare payload
        StringEntity entity = new StringEntity(GSON.toJson(report), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // Send POST request
        this.reportsQueue.submit(httpPost, report);
        return true;
    }

    /**
     * Reports a test to the Agent.
     *
     * @param report Report to submit.
     * @return True is successful, otherwise False.
     */
    public boolean reportTest(final TestReport report) {
        // Initialize POST request to Agent API
        HttpPost httpPost = new HttpPost(remoteAddress + Routes.REPORT_TEST);
        httpPost.setConfig(getDefaultHttpConfig());

        // Prepare payload
        StringEntity entity = new StringEntity(GSON.toJson(report), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // Send POST request
        this.reportsQueue.submit(httpPost, report);
        return true;
    }

    /**
     * Executes a Action proxy from an Addon installed in an Account.
     *
     * @param action  An instance of an action that extends the {@link ActionProxy} class.
     * @param timeout maximum amount of time allowed to wait for action execution to complete.
     * @return Execution result in form of {@link ActionExecutionResponse}.
     * @throws WebDriverException when execution fails.
     */
    public ActionExecutionResponse executeProxy(final ActionProxy action, final int timeout) throws WebDriverException {
        // Initialize POST request to Agent API
        HttpPost httpPost = new HttpPost(remoteAddress + Routes.EXECUTE_ACTION_PROXY);

        // Addon execution can take time,
        // This is why this config is unique and other calls use getDefaultConfig() method
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(timeout > 0 ? timeout : ADDON_EXECUTION_SOCKET_TIMEOUT_MS)
                .build();
        httpPost.setConfig(config);

        // Action fields should be provided as ProxyDescriptor parameters.
        // Using Gson as a workaround to create a HashMap of objects declared fields.
        @SuppressWarnings("unchecked") // Using raw type as can't specify generic parameters for Gson
                HashMap<String, Object> parameters = GSON.fromJson(GSON.toJsonTree(action).toString(), HashMap.class);
        action.getDescriptor().setParameters(parameters);

        // Prepare payload
        String request = GSON.toJson(action.getDescriptor());
        LOG.trace("Sending action proxy request: {}", request);
        StringEntity entity = new StringEntity(request, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // Send POST request
        CloseableHttpResponse response;
        try {
            response = this.httpClient.execute(httpPost);
        } catch (IOException e) {
            LOG.error("Failed to execute action proxy: [{}]", action, e);
            throw new WebDriverException("Failed to execute action proxy: [" + action + "]", e);
        }

        // Handle unsuccessful response
        if (response != null && response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
            LOG.error("Agent responded with an unexpected status {} to action proxy execution: [{}]",
                    response.getStatusLine().getStatusCode(), action);
        }

        if (response == null) {
            LOG.error("Agent response is empty");
            throw new WebDriverException("Failed to execute action proxy: [" + action + "]");
        }

        // Read Response
        String responseBody;
        try {
            responseBody = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            LOG.error("Failed reading action proxy execution response", e);
            throw new WebDriverException("Failed reading action proxy execution response", e);
        }

        return GSON.fromJson(responseBody, ActionExecutionResponse.class);
    }

    /**
     * Internal class used to store Agent API routes.
     */
    private static class Routes {
        /**
         * Development endpoint address.
         */
        static final String DEVELOPMENT_SESSION = "/api/development/session";

        /**
         * Command reporting endpoint address.
         */
        static final String REPORT_COMMAND = "/api/development/report/command";

        /**
         * Step reporting endpoint address.
         */
        static final String REPORT_STEP = "/api/development/report/step";

        /**
         * Test reporting endpoint address.
         */
        static final String REPORT_TEST = "/api/development/report/test";

        /**
         * Action proxy execution endpoint address.
         */
        static final String EXECUTE_ACTION_PROXY = "/api/addons/executions";
    }
}
