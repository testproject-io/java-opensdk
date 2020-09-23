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

package io.testproject.sdk.drivers;

import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.internal.helpers.GenericCommandExecutor;
import io.testproject.sdk.internal.helpers.ReportingCommandsExecutor;
import io.testproject.sdk.internal.reporting.Reporter;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.ReportSettings;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;

import java.io.IOException;
import java.net.URL;

/**
 * Generic Driver that can be used to perform a non-UI automation.
 */
public final class GenericDriver implements ReportingDriver {

    /**
     * Minimum Agent version that supports Generic drivers.
     */
    private static final String MIN_GENERIC_DRIVER_SUPPORTED_VERSION = "0.64.40";

    /**
     * Steps reporter instance.
     */
    private final Reporter reporter;

    /**
     * A "dummy" command executor to allow reporting.
     * There are no actual commands that are being executed by this executor,
     * but this allows to keep the same structure as other Selenium drivers have.
     */
    private ReportingCommandsExecutor reportingCommandExecutor;

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
    public GenericDriver()
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(false);
    }

    /**
     * Initiates a new session with the Agent using default token and URL and reports commands.
     * <p>
     * Default <em>Agent URL</em> can be set using <b>TP_AGENT_URL</b> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Default <em>token</em> can be set using <b>TP_DEV_TOKEN</b> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final boolean disableReports)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, null, null, disableReports);
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
     * @param projectName Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final String projectName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, projectName, null, false);
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
     * @param projectName Project name to report
     * @param jobName     Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final String projectName,
                         final String jobName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, null, projectName, jobName, false);
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
     * @param projectName Project name to report
     * @param jobName     Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final String token, final String projectName, final String jobName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(null, token, projectName, jobName, false);
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
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final URL remoteAddress)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(remoteAddress, null);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL, default token, Project and Job names.
     * <p>
     * Default token can be set using <em>TP_DEV_TOKEN</em> environment variable.
     * You can get a token from <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param projectName   Project name to report
     * @param jobName       Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final URL remoteAddress,
                         final String projectName,
                         final String jobName)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(remoteAddress, null, projectName, jobName, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and token.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param token         Development token that should be obtained from
     *                      <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final URL remoteAddress,
                         final String token)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {
        this(remoteAddress, token, null, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL, token, Project and Job names.
     *
     * @param remoteAddress  Agent API base URL (e.g. http://localhost:8585/)
     * @param token          Development token that should be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param projectName    Project name to report
     * @param jobName        Job name to report
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws IOException              if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public GenericDriver(final URL remoteAddress,
                         final String token,
                         final String projectName,
                         final String jobName,
                         final boolean disableReports)
            throws InvalidTokenException, AgentConnectException, IOException,
            ObsoleteVersionException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PLATFORM_NAME, Platform.ANY);

        String version = AgentClient.getVersion(remoteAddress);
        if (new ComparableVersion(version).compareTo(
                new ComparableVersion(MIN_GENERIC_DRIVER_SUPPORTED_VERSION)) < 0) {
            StringBuilder message = new StringBuilder()
                    .append("Target Agent version").append(" [").append(version).append("] ")
                    .append("doesn't support Generic driver. ")
                    .append("Upgrade the Agent to the latest version and try again.");
            throw new AgentConnectException(message.toString());
        }

        AgentClient agentClient = AgentClient.getClient(remoteAddress, token, capabilities,
                        new ReportSettings(projectName, jobName), disableReports);

        this.reporter = new Reporter(this, AgentClient.getClient(capabilities));

        reportingCommandExecutor = new GenericCommandExecutor(agentClient);
        reportingCommandExecutor.setReportsDisabled(disableReports);
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

    @Override
    public void stop() {
        // Nothing here...
    }

    /**
     * Returns a local command executor.
     */
    @Override
    public ReportingCommandsExecutor getReportingCommandExecutor() {
        return reportingCommandExecutor;
    }

    /**
     * Quits the driver and reports test completion.
     */
    public void quit() {
        if (!reportingCommandExecutor.isReportsDisabled()) {
            Command command = new Command(null, DriverCommand.QUIT);
            reportingCommandExecutor.reportCommand(command, null);
        }
    }
}
