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

package io.testproject.sdk.drivers.web;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.internal.helpers.DriverHelper;
import io.testproject.sdk.internal.helpers.ReportingCommandsExecutor;
import io.testproject.sdk.internal.helpers.ShutdownThreadManager;
import io.testproject.sdk.internal.reporting.Reporter;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.ReportSettings;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Extension of the original {@link org.openqa.selenium.firefox.FirefoxDriver FirefoxDriver}
 * Instead of initializing a new session, it starts it in the TestProject Agent and then reconnects to it.
 */
@SuppressFBWarnings(
        value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS",
        justification = "Minimize changes required in any migrated tests")
@SuppressWarnings("WeakerAccess") // Prevent compiler complaining about unused overloaded constructors
public class FirefoxDriver extends org.openqa.selenium.firefox.FirefoxDriver implements ReportingDriver {

    /**
     * Steps reporter instance.
     */
    private Reporter reporter;

    /**
     * Initiates a new session with the Agent using default token and URL.
     * <p>
     * Default <em>Agent URL</em> can be set using <b>TP_AGENT_URL</b> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Default <em>token</em> can be set using <b>TP_DEV_TOKEN</b> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver()
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, new FirefoxOptions());
    }

    /**
     * Initiates a new session with the Agent using default token and URL.
     * <p>
     * Default <em>Agent URL</em> can be set using <b>TP_AGENT_URL</b> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Default <em>token</em> can be set using <b>TP_DEV_TOKEN</b> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param options take a look at {@link FirefoxOptions}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final FirefoxOptions options)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, options);
    }

    /**
     * Initiates a new session with the Agent using default token and URL.
     * <p>
     * Default <em>Agent URL</em> can be set using <b>TP_AGENT_URL</b> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Default <em>token</em> can be set using <b>TP_DEV_TOKEN</b> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param options        take a look at {@link FirefoxOptions}
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final FirefoxOptions options,
                         final boolean disableReports)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, options, null, null, disableReports);
    }

    /**
     * Initiates a new session with the Agent using default token and URL with Project name.
     * <p>
     * Default <em>Agent URL</em> can be set using <b>TP_AGENT_URL</b> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Default <em>token</em> can be set using <b>TP_DEV_TOKEN</b> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param options     take a look at {@link FirefoxOptions}
     * @param projectName Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final FirefoxOptions options,
                         final String projectName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, options, projectName, null, false);
    }

    /**
     * Initiates a new session with the Agent using default token and URL, Project and Job names.
     * <p>
     * Default <em>Agent URL</em> can be set using <b>TP_AGENT_URL</b> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Default <em>token</em> can be set using <b>TP_DEV_TOKEN</b> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param options     take a look at {@link FirefoxOptions}
     * @param projectName Project name to report
     * @param jobName     Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final FirefoxOptions options,
                         final String projectName,
                         final String jobName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, options, projectName, jobName, false);
    }

    /**
     * Initiates a new session with the Agent using provided token and default URL.
     * <p>
     * Default Agent URL can be set using <em>TP_AGENT_URL</em> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param token   Development token that should be obtained from
     *                <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param options take a look at {@link FirefoxOptions}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final String token,
                         final FirefoxOptions options)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, token, options, null, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided token and default URL and Project name.
     * <p>
     * Default Agent URL can be set using <em>TP_AGENT_URL</em> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param token       Development token that should be obtained from
     *                    <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param options     take a look at {@link FirefoxOptions}
     * @param projectName Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final String token,
                         final FirefoxOptions options,
                         final String projectName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, token, options, projectName, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided token and default URL, Project and Job names.
     * <p>
     * Default Agent URL can be set using <em>TP_AGENT_URL</em> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param token       Development token that should be obtained from
     *                    <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param options     take a look at {@link FirefoxOptions}
     * @param projectName Project name to report
     * @param jobName     Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final String token,
                         final FirefoxOptions options,
                         final String projectName,
                         final String jobName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, token, options, projectName, jobName, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and default token.
     * <p>
     * Default token can be set using <em>TP_DEV_TOKEN</em> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param options       take a look at {@link FirefoxOptions}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final URL remoteAddress,
                         final FirefoxOptions options)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(remoteAddress, null, options, null, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL, default token and Project name.
     * <p>
     * Default token can be set using <em>TP_DEV_TOKEN</em> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param options       take a look at {@link FirefoxOptions}
     * @param projectName   Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final URL remoteAddress,
                         final FirefoxOptions options,
                         final String projectName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(remoteAddress, null, options, projectName, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and default token, Project and Job names.
     * <p>
     * Default token can be set using <em>TP_DEV_TOKEN</em> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param options       take a look at {@link FirefoxOptions}
     * @param projectName   Project name to report
     * @param jobName       Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final URL remoteAddress,
                         final FirefoxOptions options,
                         final String projectName,
                         final String jobName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(remoteAddress, null, options, projectName, jobName, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and token.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param token         Development token that should be obtained from
     *                      <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param options       take a look at {@link FirefoxOptions}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final URL remoteAddress,
                         final String token,
                         final FirefoxOptions options)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(remoteAddress, token, options, null, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL, token, Project and Job names.
     *
     * @param remoteAddress  Agent API base URL (e.g. http://localhost:8585/)
     * @param token          Development token that should be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param options        take a look at {@link FirefoxOptions}
     * @param projectName    Project name to report
     * @param jobName        Job name to report
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public FirefoxDriver(final URL remoteAddress,
                         final String token,
                         final FirefoxOptions options,
                         final String projectName,
                         final String jobName,
                         final boolean disableReports)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        super(new FakeDriverService(), new FirefoxOptions(AgentClient
                .getClient(remoteAddress, token, options,
                        new ReportSettings(projectName, jobName), disableReports)
                .getSession().getCapabilities()));

        this.reporter = new Reporter(this, AgentClient.getClient(this.getCapabilities()));
        this.getReportingCommandExecutor().setReportsDisabled(disableReports);

        ShutdownThreadManager.getInstance().addDriver(this, this::stop);
    }

    /**
     * Sets capabilities and sessionId obtained from the Agent when creating the original session.
     */
    @Override
    protected void startSession(final Capabilities capabilities) {
        try {
            AgentClient agentClient = AgentClient.getClient(capabilities);
            DriverHelper.setCapabilities(this, capabilities);
            setSessionId(agentClient.getSession().getSessionId());
            setCommandExecutor(DriverHelper.getHttpCommandExecutor(agentClient, false));
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }

    /**
     * Removes shutdown hook and calls {@link #stop()}.
     */
    @Override
    public void quit() {
        // Avoid performing graceful shutdown more than once
        ShutdownThreadManager.getInstance().removeDriver(this);

        // Stop the driver
        stop();
    }

    /**
     * Quits the driver and stops the session with the Agent, cleaning up after itself.
     */
    @Override
    public void stop() {
        // Report any outstanding stashed commands
        ReportingCommandsExecutor executor = (ReportingCommandsExecutor) this.getCommandExecutor();
        executor.clearStash();

        // Quit the driver to close Selenium session
        super.quit();
    }

    /**
     * Provides access to the reporting functionality and settings.
     *
     * @return {@link Reporter} instance.
     */
    @Override
    public Reporter report() {
        return reporter;
    }

    /**
     * Fake DriverService to avoid searching for driver executable.
     */
    static class FakeDriverService extends GeckoDriverService {

        /**
         * Initiates a new DriverService with dummy data.
         *
         * @throws IOException If an I/O error occurs.
         */
        FakeDriverService() throws IOException {
            super(new File(""), 0, null, null);
        }
    }
}
