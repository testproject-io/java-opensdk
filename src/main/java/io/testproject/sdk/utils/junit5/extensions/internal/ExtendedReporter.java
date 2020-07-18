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

package io.testproject.sdk.utils.junit5.extensions.internal;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.internal.reporting.Reporter;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.messages.StepReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ExtendedReporter that used by TestProject JUnit 5 extensions.
 */
public class ExtendedReporter extends Reporter {


    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ExtendedReporter.class);


    /**
     * Initializes a new instance using provided driver and agentClient.
     *
     * @param driver      Driver instance to be used for taking screenshots.
     * @param agentClient {@link AgentClient} instance to submit reports to the Agent.
     */
    public ExtendedReporter(final ReportingDriver driver, final AgentClient agentClient) {
        super(driver, agentClient);
    }

    /**
     * Initializes a new instance using provided Reporter instance.
     *
     * @param reporter {@link Reporter} instance.
     */
    public ExtendedReporter(final Reporter reporter) {
        super(reporter);
    }

    /**
     * Custom step reporter that used by the TestProject Junit 5 extensions.
     *
     * @param description Step description.
     * @param message     Step message.
     */
    public void reportTestFailure(final String description, final String message) {

        StepReport report = new StepReport(description, message, false, null);

        if (getDriver().getReportingCommandExecutor().isReportsDisabled()) {
            LOG.trace("Reporting test as failed to TestProject cloud report");
            return;
        }

        if (!getAgentClient().reportStep(report)) {
            LOG.error("Failed reporting the test failure to Agent");
        }
    }


}
