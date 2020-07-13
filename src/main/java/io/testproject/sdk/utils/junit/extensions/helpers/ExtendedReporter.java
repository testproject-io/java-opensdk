package io.testproject.sdk.utils.junit.extensions.helpers;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.internal.reporting.Reporter;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.messages.StepReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedReporter extends Reporter {


    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Reporter.class);


    /**
     * Initializes a new instance using provided driver and agentClient.
     *
     * @param driver      Driver instance to be used for taking screenshots.
     * @param agentClient {@link AgentClient} instance to submit reports to the Agent.
     */
    public ExtendedReporter(ReportingDriver driver, AgentClient agentClient) {
        super(driver, agentClient);
    }

    public ExtendedReporter(io.testproject.sdk.internal.reporting.Reporter reporter) {
        super(reporter);
    }

    /**
     * Custom step reporter that used by the TestProject Junit assertion reporting extension
     *
     * @param description Step description.
     * @param message     Step message.
     * @param passed      True to mark step as Passed, otherwise False.
     * @param screenshot  True to take a screenshot, otherwise False.
     */
    public void stepOnly(final String description,
                     final String message,
                     final boolean passed,
                     final boolean screenshot) {

        StepReport report = new StepReport(description, message, passed,
                screenshot ? driver.getScreenshot() : null);

        if (driver.getReportingCommandExecutor().isReportsDisabled()) {
            LOG.trace("Reporting test as failed to TestProject cloud report");
            return;
        }

        if (!agentClient.reportStep(report)) {
            LOG.error("Failed reporting the test failure to Agent");
        }
    }


}
