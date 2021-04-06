/*
 * Copyright (c) 2021 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.internal.reporting.extensions.junit4;

import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.messages.StepReport;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit4 extension that takes care of reporting failed assertions.
 */
public class ExceptionsReporter extends RunListener {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionsReporter.class);

    /**
     * Event listener for test failure.
     * Will create and report assertion errors.
     *
     * @param failure object of the current test.
     */
    public void testFailure(final Failure failure) {

        AgentClient reportingClient = AgentClient.getInstance();

        if (reportingClient == null) {
            LOG.error("No reporting client available, please make sure you have a TestProject driver initialized");
            return;
        }

        // Report the error.
        // Proceed only if error is instance of assertion error.
        if (!(failure.getException() instanceof AssertionError)) {
            return;
        }

        // Get the assertion error message.
        String resultDescription = failure.getException().getMessage();
        // Proceed only if message is not empty.
        if (resultDescription.isEmpty()) {
            return;
        }

        // Skip reporting when disabled, just log it.
        if (reportingClient.getReportsDisabled()) {
            LOG.trace("Step [{}] - [{}]", resultDescription, false);
            return;
        }

        // Finally, submit the report
        StepReport report = new StepReport(resultDescription, null, false, null);
        if (!reportingClient.reportStep(report)) {
            LOG.error("Failed reporting exception: [{}]", report);
        }

    }
}
