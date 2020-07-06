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

package io.testproject.sdk.internal.reporting;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.messages.TestReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

public final class ClosableTestReport implements Closeable {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClosableTestReport.class);

    /**
     * {@link AgentClient} instance to submit reports to the Agent.
     */
    private final AgentClient agentClient;

    /**
     * Underlying TestReport object.
     */
    private final TestReport report;

    /**
     * Driver used for test automation and reporting.
     */
    private final ReportingDriver driver;

    /**
     * Flag to avoid reporting Test twice from {@link #submit} and {@link #close()}.
     */
    private boolean submitted;

    /**
     * Initializes a new instance of a Test Report.
     *
     * @param agentClient Agent client instance to use for reporting.
     * @param driver      Driver used for test automation and reporting.
     * @param name        Test name.
     * @param passed      True if passed, otherwise False.
     * @param message     Result message.
     */
    public ClosableTestReport(final AgentClient agentClient,
                              final ReportingDriver driver,
                              final String name,
                              final boolean passed,
                              final String message) {
        this.agentClient = agentClient;
        this.driver = driver;
        this.report = new TestReport(name, passed, message);
    }

    /**
     * Set report result (passed / failed).
     *
     * @param passed True if passed, otherwise False.
     */
    public void setResult(final boolean passed) {
        this.report.setPassed(passed);
    }

    /**
     * Set report message.
     *
     * @param message Massage to be set.
     */
    public void setMessage(final String message) {
        this.report.setMessage(message);
    }

    /**
     * Submits the report to the Agent.
     * Calling this method is effectively the same as calling {@link #close()}.
     */
    public void submit() {
        close();
    }

    /**
     * Submits the report to the Agent.
     */
    @Override
    public void close() {
        // Do not report if already submitted.
        if (submitted) {
            return;
        }

        submitted = true;

        if (driver.getReportingCommandExecutor().isReportsDisabled()) {
            LOG.trace("Test [{}] - [{}]", report.getName(), report.isPassed() ? "Passed" : "Failed");
            return;
        }

        if (!agentClient.reportTest(report)) {
            LOG.error("Failed reporting test to Agent");
        }
    }
}
