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

package io.testproject.sdk.internal.helpers;

import io.appium.java_client.MobileCommand;
import io.appium.java_client.remote.AppiumW3CHttpCommandCodec;
import io.testproject.sdk.internal.rest.AgentClient;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandCodec;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.W3CHttpCommandCodec;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import static io.testproject.sdk.internal.helpers.DriverHelper.FIELD_COMMAND_CODEC;
import static io.testproject.sdk.internal.helpers.DriverHelper.FIELD_RESPONSE_CODEC;

/**
 * A custom Appium commands executor for Appium drivers.
 * Extends the original functionality by restoring driver session initiated by the Agent.
 * Reports commands executed to Agent.
 */
public final class CustomAppiumCommandExecutor
        extends io.appium.java_client.remote.AppiumCommandExecutor
        implements ReportingCommandsExecutor {

    /**
     * Agent client cached instance.
     */
    private final AgentClient agentClient;

    /**
     * Commands and responses stash to keep FluentWait attempts before reporting them.
     */
    private final StashedCommands stashedCommands = new StashedCommands();

    /**
     * Flag to enable/disable any reports.
     */
    private boolean reportsDisabled;

    /**
     * Flag to enable/disable command reports.
     */

    private boolean commandReportsDisabled;

    /**
     * Flag to enable/disable test reports.
     */
    private boolean testReportsDisabled;

    /**
     * Flag to enable/disable commands reports redaction.
     */
    private boolean redactionDisabled;

    /**
     * Current Test name tracking object.
     */
    private final AtomicReference<String> currentTest = new AtomicReference<>(null);

    /**
     * Initializes a new instance of this an Executor restoring command/response codecs.
     *
     * @param agentClient           an instance of {@link AgentClient} used to pen the original driver session.
     * @param addressOfRemoteServer URL of the remote Appium server managed by the Agent
     */
    public CustomAppiumCommandExecutor(final AgentClient agentClient, final URL addressOfRemoteServer) {
        super(MobileCommand.commandRepository, addressOfRemoteServer);
        this.agentClient = agentClient;

        // Usually this is happening when the NEW_SESSION command is handled
        // Here we mimic the same logic using reflection, setting missing codecs and commands
        CommandCodec<HttpRequest> commandCodec = agentClient.getSession().getDialect().getCommandCodec();
        if (commandCodec instanceof W3CHttpCommandCodec) {
            commandCodec = new AppiumW3CHttpCommandCodec();
        }
        DriverHelper.setPrivateFieldValue(this, FIELD_COMMAND_CODEC, commandCodec);
        DriverHelper.setPrivateFieldValue(this, FIELD_RESPONSE_CODEC,
                agentClient.getSession().getDialect().getResponseCodec());
        getAdditionalCommands().forEach(this::defineCommand);
    }

    @Override
    public Response execute(final Command command) throws WebDriverException {
        return execute(command, false);
    }

    /**
     * Extended command execution method.
     * Allows skipping reporting for "internal" commands, for example:
     * - Taking screenshot for manual step reporting.
     * - Inspecting element type to determine whether redaction is required.
     * @param command       Command to execute
     * @param skipReporting Flag to control reporting
     * @return Command execution response.
     */
    @Override
    public Response execute(final Command command, final boolean skipReporting) {
        Response response = null;

        if (!command.getName().equals(DriverCommand.QUIT)) {
            // Preserve the mobile session, agent got /change custom Appium endpoint
            response = super.execute(command);
        }

        if (!skipReporting) {
            reportCommand(command, response);
        }
        return response;
    }

    @Override
    public AgentClient getAgentClient() {
        return this.agentClient;
    }

    @Override
    public StashedCommands getStashedCommands() {
        return stashedCommands;
    }

    /**
     * Getter for {@link #reportsDisabled} field.
     *
     * @return value of {@link #reportsDisabled} field
     */
    public boolean isReportsDisabled() {
        return reportsDisabled;
    }

    /**
     * Setter for {@link #reportsDisabled} field.
     *
     * @param disable True to disable or False to enable.
     */
    public void setReportsDisabled(final boolean disable) {
        this.reportsDisabled = disable;
    }

    @Override
    public boolean isCommandReportsDisabled() {
        return this.commandReportsDisabled;
    }

    @Override
    public void setCommandReportsDisabled(final boolean disable) {
        this.commandReportsDisabled = disable;
    }

    /**
     * Getter for {@link #testReportsDisabled} field.
     *
     * @return value of {@link #testReportsDisabled} field
     */
    public boolean isTestAutoReportsDisabled() {
        return testReportsDisabled;
    }

    /**
     * Setter for {@link #testReportsDisabled} field.
     *
     * @param disable True to disable or False to enable.
     */
    public void setTestAutoReportsDisabled(final boolean disable) {
        this.testReportsDisabled = disable;
    }

    /**
     * Getter for {@link #redactionDisabled} field.
     *
     * @return value of {@link #redactionDisabled} field
     */
    public boolean isRedactionDisabled() {
        return redactionDisabled;
    }

    /**
     * Setter for {@link #redactionDisabled} field.
     *
     * @param disable True to disable or False to enable.
     */
    public void setRedactionDisabled(final boolean disable) {
        this.redactionDisabled = disable;
    }

    /**
     * Getter for <em>CURRENT_TEST</em> field.
     *
     * @return value of <em>CURRENT_TEST</em> field
     */
    public AtomicReference<String> getCurrentTest() {
        return currentTest;
    }
}
