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

import io.testproject.sdk.internal.reporting.inferrers.InferrerFactory;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.messages.TestReport;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.testproject.sdk.internal.helpers.RedactHelper.redactCommand;
import static org.openqa.selenium.remote.ErrorCodes.SUCCESS;

/**
 * Interface implemented by custom executors.
 * Allows to skip reporting for "internal" commands, such as those used during redaction.
 */
public interface ReportingCommandsExecutor {

    /**
     * Logger instance.
     */
    Logger LOG = LoggerFactory.getLogger(ReportingCommandsExecutor.class);

    /**
     * Successful response state returned by the server (Selenium / Appium).
     */
    String STATE_SUCCESS = "success";

    /**
     * Executes a command.
     *
     * @param command       Command to execute
     * @param skipReporting Flag to control reporting
     * @return Server response
     */
    Response execute(Command command, boolean skipReporting);

    /**
     * Getter for {@link AgentClient} instance.
     *
     * @return value of {@link AgentClient} field
     */
    AgentClient getAgentClient();

    /**
     * Getter for <em>stashed commands</em> hashmap.
     *
     * @return value of <em>stashed commands</em> hashmap.
     */
    StashedCommands getStashedCommands();

    /**
     * Getter for <em>reportsDisabled</em> field.
     *
     * @return value of <em>reportsDisabled</em> field
     */
    boolean isReportsDisabled();

    /**
     * Setter for <em>reportsDisabled</em> field.
     *
     * @param disable True to disable or False to enable.
     */
    void setReportsDisabled(boolean disable);

    /**
     * Getter for <em>commandReportsDisabled</em> field.
     *
     * @return value of <em>commandReportsDisabled</em> field
     */
    boolean isCommandReportsDisabled();

    /**
     * Setter for <em>commandReportsDisabled</em> field.
     *
     * @param disable True to disable or False to enable.
     */
    void setCommandReportsDisabled(boolean disable);

    /**
     * Getter for <em>testReportsDisabled</em> field.
     *
     * @return value of <em>testReportsDisabled</em> field
     */
    boolean isTestAutoReportsDisabled();

    /**
     * Setter for <em>testReportsDisabled</em> field.
     *
     * @param disable True to disable or False to enable.
     */
    void setTestAutoReportsDisabled(boolean disable);

    /**
     * Getter for <em>redactDisabled</em> field.
     *
     * @return value of <em>redactDisabled</em> field
     */
    boolean isRedactionDisabled();

    /**
     * Setter for <em>redactDisabled</em> field.
     *
     * @param disable True to disable or False to enable.
     */
    void setRedactionDisabled(boolean disable);

    /**
     * Getter for <em>CURRENT_TEST</em> field.
     *
     * @return value of <em>CURRENT_TEST</em> field
     */
    AtomicReference<String> getCurrentTest();

    /**
     * Reports a command to the Agent.
     *
     * @param command  Command to report.
     * @param response Response to the reported command.
     */
    default void reportCommand(final Command command, final Response response) {
        boolean isQuitCommand = command.getName().equals(DriverCommand.QUIT);
        List<StackTraceElement> traces = Arrays.asList(Thread.currentThread().getStackTrace());

        // Report Tests
        if (!isTestAutoReportsDisabled()) {
            reportTest(traces, isQuitCommand);
        }

        if (isQuitCommand) {
            // Do not report quit to avoid creating new test in reports
            return;
        }

        // Check if executed from a FluentWait loop
        boolean isFluentWait = traces.stream().anyMatch(t -> t.getClassName().equals(FluentWait.class.getName()));
        if (isFluentWait) {
            // Stash command - same one might follow with different response (result)
            // Only the last one executed will be stashed, having the "final" result
            getStashedCommands().add(new StashedCommand(command, response));
        } else {
            // Report any stashed commands
            clearStash();

            // Report the command that was just executed
            if (!reportCommand(getAgentClient(), command, response, this)) {
                LOG.error("Failed reporting command: {}", command);
            }
        }
    }

    /**
     * Report Test to Agent.
     *
     * @param traces Call Stack to analyze when searching for Test name.
     * @param force  True if called just before session is getting closed,
     *               but test hasn't changed, to force reporting. Otherwise False.
     */
    default void reportTest(List<StackTraceElement> traces, boolean force) {
        // Check if test context has changed
        String testName = inferTestName(traces);

        // Set first test name
        if (getCurrentTest().get() == null) {
            getCurrentTest().set(testName);
        }

        if (testName == null) {
            return;
        }

        if (!testName.equals(getCurrentTest().get()) || force) {
            if (isReportsDisabled()) {
                LOG.trace("Test [{}] - [Passed]", getCurrentTest().get());
                return;
            }

            // Report finished test
            if (!getAgentClient().reportTest(new TestReport(getCurrentTest().get(), true, null))) {
                LOG.error("Failed reporting test [{}] to the Agent", getCurrentTest().get());
            }

            // Set tracking object to the current Test name.
            getCurrentTest().set(testName);
        }
    }

    /**
     * Infer Test name from call stack.
     *
     * @param traces Call Stack to analyze.
     * @return Test name.
     */
    default String inferTestName(List<StackTraceElement> traces) {
        return InferrerFactory.getInferrer(traces).inferTestName();
    }

    /**
     * Reports a command executed by the driver and the result.
     * If the response is an Element, extracts its ID and sends as the result.
     *
     * @param agentClient {@link AgentClient} instance to use for reporting
     * @param command     Command executed by the driver.
     * @param response    Response provided by the driver.
     * @param executor    Executor instance that executed the command
     * @return True if successfully reported, otherwise False.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // Only failures have specific flows
    default boolean reportCommand(final AgentClient agentClient, final Command command,
                                  final Response response, final ReportingCommandsExecutor executor) {
        boolean passed = isCommandPassed(response);

        if (executor.isReportsDisabled() || executor.isCommandReportsDisabled()) {
            LOG.trace("Command [{}] - [{}]", command.getName(), passed ? "Passed" : "Failed");
            return true;
        }

        return agentClient.reportCommand(
                isRedactionDisabled() ? command : redactCommand(executor, command), extractResponse(response),
                passed);
    }

    /**
     * Determine command result based on response using state and status.
     * @param response Command execution response.
     * @return True if passed, otherwise False.
     */
    private boolean isCommandPassed(Response response) {
        return response.getState().equalsIgnoreCase(STATE_SUCCESS)
                || (response.getStatus() != null && response.getStatus() == SUCCESS);
    }

    /**
     * Extracts exception message from server response, omitting noisy information.
     *
     * @param response Response received from the server.
     * @return Exception message (in case of an exception response), or original response.
     */
    default Object extractResponse(final Response response) {
        if (response.getValue() != null && Exception.class.isAssignableFrom(response.getValue().getClass())) {
            return ((Exception) response.getValue()).getMessage();
        }

        return response.getValue();
    }

    /**
     * Reports any outstanding commands before the driver quits and session is closed.
     * Should be always called before session ends, to avoid unreported command in FluentWait sequence.
     */
    default void clearStash() {
        // Report any outstanding commands
        for (StashedCommand stash : getStashedCommands().list()) {
            if (!reportCommand(getAgentClient(), stash.getCommand(), stash.getResponse(), this)) {
                LOG.error("Failed reporting stashed command: {}", stash.getCommand());
            }
        }

        // Empty the queue
        getStashedCommands().clear();
    }
}
