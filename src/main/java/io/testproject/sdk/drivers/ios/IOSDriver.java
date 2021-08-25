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

package io.testproject.sdk.drivers.ios;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.testproject.sdk.drivers.ReportType;
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
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Extension of the original {@link io.appium.java_client.ios.IOSDriver IOSDriver}
 * Instead of initializing a new session, it starts it in the TestProject Agent and then reconnects to it.
 *
 * @param <T> the required type of class which implement
 *            {@link org.openqa.selenium.WebElement}.
 *            Instances of the defined type will be returned via findElement* and findElements*.
 *            Warning (!!!). Allowed types:
 *            {@link org.openqa.selenium.WebElement}
 *            {@link org.openqa.selenium.remote.RemoteWebElement}
 *            {@link io.appium.java_client.MobileElement}
 *            {@link io.appium.java_client.ios.IOSElement}
 */
@SuppressFBWarnings(
        value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS",
        justification = "Minimize changes required in any migrated tests")
// Prevent compiler complaining about unused overloaded constructors
// Prevent compiler complaining about type safety caused by raw types
@SuppressWarnings({"WeakerAccess", "unchecked"})
public class IOSDriver<T extends WebElement>
        extends io.appium.java_client.ios.IOSDriver<T> implements ReportingDriver {

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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities) throws InvalidTokenException, AgentConnectException,
            MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, null, null, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param reportType   A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities, final ReportType reportType)
            throws InvalidTokenException, AgentConnectException, MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, null, null, false, reportType);
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
     * @param capabilities   take a look at {@link MutableCapabilities}
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities,
                     final boolean disableReports) throws InvalidTokenException, AgentConnectException,
            MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, null, null, disableReports, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities   take a look at {@link MutableCapabilities}
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @param reportType     A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities,
                     final boolean disableReports,
                     final ReportType reportType) throws InvalidTokenException, AgentConnectException,
            MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, null, null, disableReports, reportType);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities,
                     final String projectName)
            throws InvalidTokenException, AgentConnectException,
            MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, projectName, null, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @param reportType   A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities,
                     final String projectName,
                     final ReportType reportType)
            throws InvalidTokenException, AgentConnectException,
            MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, projectName, null, false, reportType);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @param jobName      Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName)
            throws InvalidTokenException, AgentConnectException,
            MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, projectName, jobName, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @param jobName      Job name to report
     * @param reportType   A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName,
                     final ReportType reportType)
            throws InvalidTokenException, AgentConnectException,
            MalformedURLException, ObsoleteVersionException {
        this(null, null, capabilities, projectName, jobName, false, reportType);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final String token,
                     final MutableCapabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, null, null, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param reportType   A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final String token,
                     final MutableCapabilities capabilities,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, null, null, false, reportType);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final String token,
                     final MutableCapabilities capabilities,
                     final String projectName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, projectName, null, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @param reportType    A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final String token,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, projectName, null, false, reportType);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @param jobName      Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final String token,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, projectName, jobName, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities take a look at {@link MutableCapabilities}
     * @param projectName  Project name to report
     * @param jobName      Job name to report
     * @param reportType   A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final String token,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(null, token, capabilities, projectName, jobName, false, reportType);
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
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final MutableCapabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, null, null, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param reportType    A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final MutableCapabilities capabilities,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, null, null, false, reportType);
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
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param projectName   Project name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final MutableCapabilities capabilities,
                     final String projectName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, projectName, null, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param projectName   Project name to report
     * @param reportType    A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, projectName, null, false, reportType);
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
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param projectName   Project name to report
     * @param jobName       Job name to report
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, projectName, jobName, false, ReportType.CLOUD_AND_LOCAL);
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
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param projectName   Project name to report
     * @param jobName       Job name to report
     * @param reportType    A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, null, capabilities, projectName, jobName, false, reportType);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and token.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param token         Development token that should be obtained from
     *                      <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final String token,
                     final MutableCapabilities capabilities)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, token, capabilities, null, null, false, ReportType.CLOUD_AND_LOCAL);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and token.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param token         Development token that should be obtained from
     *                      <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param reportType    A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final String token,
                     final MutableCapabilities capabilities,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, token, capabilities, null, null, false, reportType);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and token.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param token         Development token that should be obtained from
     *                      <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param projectName   The project name to display in the report
     * @param reportType    A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                         final String token,
                         final MutableCapabilities capabilities,
                         final String projectName,
                         final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, token, capabilities, projectName, null, false, reportType);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL and token.
     *
     * @param remoteAddress Agent API base URL (e.g. http://localhost:8585/)
     * @param token         Development token that should be obtained from
     *                      <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities  take a look at {@link MutableCapabilities}
     * @param projectName   The project name to display in the report
     * @param jobName       The job name to display in the report
     * @param reportType    A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                         final String token,
                         final MutableCapabilities capabilities,
                         final String projectName,
                         final String jobName,
                         final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, token, capabilities, projectName, jobName, false, reportType);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL, token, Project and Job names.
     *
     * @param remoteAddress  Agent API base URL (e.g. http://localhost:8585/)
     * @param token          Development token that should be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities   take a look at {@link MutableCapabilities}
     * @param projectName    Project name to report
     * @param jobName        Job name to report
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @param reportType     A type of report to produce - cloud, local or both.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final String token,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName,
                     final boolean disableReports,
                     final ReportType reportType)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        this(remoteAddress, token, capabilities, projectName, jobName, disableReports, reportType,
                null,
                null);
    }

    /**
     * Initiates a new session with the Agent using provided Agent URL, token, Project and Job names.
     *
     * @param remoteAddress  Agent API base URL (e.g. http://localhost:8585/)
     * @param token          Development token that should be obtained from
     *                       <a href="https://app.testproject.io/#/integrations/sdk">SDK</a> page
     * @param capabilities   take a look at {@link MutableCapabilities}
     * @param projectName    Project name to report
     * @param jobName        Job name to report
     * @param disableReports True to disable automatic reporting of driver commands and tests, otherwise False.
     * @param reportType     A type of report to produce - cloud, local or both.
     * @param reportName     The name of the generated report.
     * @param reportPath     The path to the generated report.
     * @throws AgentConnectException    if Agent is not responding or responds with an error
     * @throws InvalidTokenException    if the token provided is invalid
     * @throws MalformedURLException    if the Agent API base URL provided is malformed
     * @throws ObsoleteVersionException if the SDK version is incompatible with the Agent
     */
    public IOSDriver(final URL remoteAddress,
                     final String token,
                     final MutableCapabilities capabilities,
                     final String projectName,
                     final String jobName,
                     final boolean disableReports,
                     final ReportType reportType,
                     final String reportName,
                     final String reportPath)
            throws AgentConnectException, InvalidTokenException, MalformedURLException,
            ObsoleteVersionException {
        super(DriverHelper.getHttpCommandExecutor(
                AgentClient.getClient(remoteAddress, token, capabilities,
                        new ReportSettings(projectName, jobName, reportType, reportName, reportPath),
                        disableReports), true),
                AgentClient.getClient(capabilities).getSession().getCapabilities());

        this.reporter = new Reporter(this, AgentClient.getClient((MutableCapabilities) this.getCapabilities()));
        this.getReportingCommandExecutor().setReportsDisabled(disableReports);

        ShutdownThreadManager.getInstance().addDriver(this, this::stop);
    }

    /**
     * Sets capabilities and sessionId obtained from the Agent when creating the original session.
     */
    @Override
    protected void startSession(final Capabilities capabilities) {
        try {
            DriverHelper.setCapabilities(this, capabilities);
            setSessionId(AgentClient.getClient((MutableCapabilities) capabilities).getSession().getSessionId());
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
     * Stops the session with the Agent and cleans up after itself.
     */
    @Override
    public void stop() {
        // Report any outstanding stashed commands
        ReportingCommandsExecutor executor = (ReportingCommandsExecutor) this.getCommandExecutor();
        executor.clearStash();

        // It will only trigger test reporting if required.
        // Actual mobile session must be preserved for re-use.
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
