/*
 * Copyright 2021 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.internal.reporting.extensions.testng;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.internal.rest.messages.StepReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG extension that takes care of reporting failed assertions.
 */
public class ExceptionsReporter implements ITestListener {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionsReporter.class);

    /**
     * Report thrown exceptions after test execution.
     *
     * @param result TestNG test result.
     */
    @Override
    public void onTestFailure(final ITestResult result) {

        // Find the reporting driver.
        if (!(io.testproject.sdk.interfaces.testng.ExceptionsReporter.class.isAssignableFrom(result.getInstance()
                .getClass()))) {
            LOG.error("Class is not implementing the ExceptionReporter interface");
            return;
        }

        ReportingDriver driver = ((io.testproject.sdk.interfaces.testng.ExceptionsReporter) result.getInstance())
                .getDriver();
        if (driver == null) {
            LOG.error("Could not find reporting driver");
            return;
        }

        // Report the error.
        // Proceed only if error is instance of assertion error.
        if (!(result.getThrowable() instanceof AssertionError)) {
            return;
        }

        // Get the assertion error message.
        String resultDescription = result.getThrowable().getMessage();
        // Proceed only if message is not empty.
        if (resultDescription.isEmpty()) {
            return;
        }

        // Skip reporting when disabled, just log it.
        if (driver.getReportingCommandExecutor().isReportsDisabled()) {
            LOG.trace("Step [{}] - [{}]", resultDescription, false);
            return;
        }

        // Finally, submit the report
        StepReport report = new StepReport(resultDescription, null, false, null);
        if (!driver.getReportingCommandExecutor().getAgentClient().reportStep(report)) {
            LOG.error("Failed reporting exception: [{}]", report);
        }

    }
}
