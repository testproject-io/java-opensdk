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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Extension of the original {@link org.openqa.selenium.remote.RemoteWebDriver RemoteWebDriver}
 * Instead of initializing a new session, it starts it in the TestProject Agent and then reconnects to it.
 */
@SuppressFBWarnings(
        value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS",
        justification = "Minimize changes required in any migrated tests")
@SuppressWarnings("WeakerAccess") // Prevent compiler complaining about unused overloaded constructors
public class RemoteWebDriver extends org.openqa.selenium.remote.RemoteWebDriver implements ReportingDriver {

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
     * Creates a new instance based on {@code capabilities}.
     *
     * @param capabilities take a look at {@link Capabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final Capabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, null, capabilities);
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
     * @param capabilities   take a look at {@link Capabilities}
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final Capabilities capabilities,
                           final boolean disableReports)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, null, capabilities, null, null, disableReports);
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
     * @param capabilities take a look at {@link Capabilities}
     * @param projectName  Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final Capabilities capabilities,
                           final String projectName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, null, capabilities, projectName, null, false);
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
     * @param capabilities take a look at {@link Capabilities}
     * @param projectName  Project name to report
     * @param jobName      Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final Capabilities capabilities,
                           final String projectName,
                           final String jobName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, null, capabilities, projectName, jobName, false);
    }

    /**
     * Initiates a new session with the Agent using provided token and default URL.
     * <p>
     * Default Agent URL can be set using <em>TP_AGENT_URL</em> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param token        Development token that should be obtained from
     *                     <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities take a look at {@link Capabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final String token,
                           final Capabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, null, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided token and default URL and Project name.
     * <p>
     * Default Agent URL can be set using <em>TP_AGENT_URL</em> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param token        Development token that should be obtained from
     *                     <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities take a look at {@link Capabilities}
     * @param projectName  Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final String token,
                           final Capabilities capabilities,
                           final String projectName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, projectName, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided token and default URL, Project and Job names.
     * <p>
     * Default Agent URL can be set using <em>TP_AGENT_URL</em> environment variable.
     * If the environment variable is not set, default URL <b>http://localhost:8585</b> is used.
     * <p>
     * Creates a new instance based on {@code capabilities}.
     *
     * @param token        Development token that should be obtained from
     *                     <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities take a look at {@link Capabilities}
     * @param projectName  Project name to report
     * @param jobName      Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final String token,
                           final Capabilities capabilities,
                           final String projectName,
                           final String jobName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, projectName, jobName, false);
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
     * @param capabilities  take a look at {@link Capabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final URL remoteAddress,
                           final Capabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, null, null, false);
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
     * @param capabilities  take a look at {@link Capabilities}
     * @param projectName   Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final URL remoteAddress,
                           final Capabilities capabilities,
                           final String projectName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, projectName, null, false);
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
     * @param capabilities  take a look at {@link Capabilities}
     * @param projectName   Project name to report
     * @param jobName       Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final URL remoteAddress,
                           final Capabilities capabilities,
                           final String projectName,
                           final String jobName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, projectName, jobName, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and token.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param token         Development token that should be obtained from
     *                      <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities  take a look at {@link Capabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final URL remoteAddress,
                           final String token,
                           final Capabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, token, capabilities, null, null, false);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL, token, Project and Job names.
     *
     * @param remoteAddress  Agent API base URL (e.g. http://localhost:8585/)
     * @param token          Development token that should be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities   take a look at {@link Capabilities}
     * @param projectName    Project name to report
     * @param jobName        Job name to report
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public RemoteWebDriver(final URL remoteAddress,
                           final String token,
                           final Capabilities capabilities,
                           final String projectName,
                           final String jobName,
                           final boolean disableReports)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        super(AgentClient.getClient(remoteAddress, token, capabilities,
                new ReportSettings(projectName, jobName), disableReports)
                .getSession().getCapabilities());

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
}
