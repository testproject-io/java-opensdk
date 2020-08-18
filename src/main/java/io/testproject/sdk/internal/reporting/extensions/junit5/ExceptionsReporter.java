/*
 * Copyright 2020 TestProject LTD. and/or its affiliates
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


package io.testproject.sdk.internal.reporting.extensions.junit5;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.internal.rest.messages.StepReport;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * JUnit5 extension that takes care of reporting failed assertions.
 */
public class ExceptionsReporter implements AfterTestExecutionCallback {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionsReporter.class);

    /**
     * TestProject package name prefix.
     */
    public static final String TESTPROJECT_TLD = "io.testproject";

    /**
     * Report thrown exceptions after test execution.
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {

        Optional<?> test = context.getTestInstance();
        if (test.isEmpty()) {
            LOG.warn("Test instance is required in order to obtain a reporting driver");
            return;
        }

        // Check if Test class implements the ExceptionsReporter interface
        if (!io.testproject.sdk.interfaces.junit5.ExceptionsReporter.class.isAssignableFrom(test.get().getClass())) {
            LOG.error("Class [{}] is using extension directly, but instead must implement [{}] interface.",
                    test.get().getClass().getName(),
                    io.testproject.sdk.interfaces.junit5.ExceptionsReporter.class.getName());
            return;
        }

        // Get a driver to use for reporting
        ReportingDriver driver = ((io.testproject.sdk.interfaces.junit5.ExceptionsReporter) test.get()).getDriver();
        if (driver == null) {
            LOG.error("Failed to obtain an instance of [{}] from [{}] to be used for reporting",
                    ReportingDriver.class.getName(),
                    ((io.testproject.sdk.interfaces.junit5.ExceptionsReporter) test.get()).getClass().getName());
            return;
        }

        if (context.getExecutionException().isEmpty()) {
            // Proceed only if an exception was thrown
            return;
        }

        Throwable exception = context.getExecutionException().get();
        // Do NOT report WebDriverException as it is already handled by the reporting executor
        // Do NOT report SDK internal exceptions
        if (exception instanceof WebDriverException
                || exception.getClass().getPackage().getName().startsWith(TESTPROJECT_TLD)) {
            return;
        }

        // Get exception details
        String resultDescription = null;
        if (exception.getMessage() != null) {
            String message = exception.getMessage();
            resultDescription = StringUtils.isEmpty(message)
                    ? exception.getClass().getName() : exception.getMessage();
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
