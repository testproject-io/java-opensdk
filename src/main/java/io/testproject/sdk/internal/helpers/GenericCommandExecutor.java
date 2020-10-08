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

import io.testproject.sdk.internal.rest.AgentClient;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Response;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A custom commands executor for Generic driver.
 */
public final class GenericCommandExecutor
        implements ReportingCommandsExecutor {

    /**
     * Agent client cached instance.
     */
    private final AgentClient agentClient;

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
     * Commands and responses stash to keep FluentWait attempts before reporting them.
     */
    private final StashedCommands stashedCommands = new StashedCommands();

    /**
     * Initializes a new instance of this an Executor restoring command/response codecs.
     *
     * @param agentClient an instance of {@link AgentClient} used to pen the original driver session.
     */
    public GenericCommandExecutor(final AgentClient agentClient) {
        this.agentClient = agentClient;
    }


    /**
     * Extended command execution method.
     * Allows skipping reporting for "internal" commands, for example:
     * - Taking screenshot for manual step reporting.
     * - Inspecting element type to determine whether redaction is required.
     *
     * @param command       Command to execute
     * @param skipReporting Flag to control reporting
     * @return Command execution response.
     */
    @Override
    public Response execute(final Command command, final boolean skipReporting) {
        throw new NotImplementedException("Not implemented for Generic Driver");
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
