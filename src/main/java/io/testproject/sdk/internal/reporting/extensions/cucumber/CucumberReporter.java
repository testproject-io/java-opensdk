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

package io.testproject.sdk.internal.reporting.extensions.cucumber;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.messages.StepReport;
import io.testproject.sdk.internal.rest.messages.TestReport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cucumber reporter that takes care of reporting Cucumber annotations when running Feature files with
 * Gherkin syntax.
 */
public class CucumberReporter implements EventListener {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CucumberReporter.class);

    /**
     * Updated job name.
     */
    private String updatedJobName;

    /**
     * Cucumber requires empty constructor.
     */
    public CucumberReporter() {
        LOG.info("Initializing Cucumber reporter");
        // Disable auto reporting for tests and driver commands.
        System.setProperty("TP_DISABLE_AUTO_REPORTS", "true");
    }

    /**
     * Event publisher defines which methods are used when certain events occur.
     *
     * @param publisher event publisher.
     */
    @Override
    public void setEventPublisher(final EventPublisher publisher) {
        // Report each step in the test.
        publisher.registerHandlerFor(TestStepFinished.class, this::reportCucumberStepFinished);
        // Report when a test finishes (Scenario).
        publisher.registerHandlerFor(TestCaseFinished.class, this::reportCucumberTestFinished);
        // Update the job name to the feature file name after test execution.
        publisher.registerHandlerFor(TestRunFinished.class, this::updateCucumberJobName);
    }

    /**
     * Will submit a test report after a scenario ends.
     *
     * @param testCaseFinished event for scenario finishing.
     */
    private void reportCucumberTestFinished(final TestCaseFinished testCaseFinished) {
        // Do not report if no active driver instance.
        if (AgentClient.getInstance() == null) {
            if (AgentClient.isWarned()) {
                LOG.warn("No active AgentClient instance, skipped reporting test");
            }
            return;
        }

        // Extract feature file name to override job name during runtime.
        // Example: in 'C:/user/feat_tests/my_job.feature' will match between two last '/' and and give 'feat_tests'.
        String uri = testCaseFinished.getTestCase().getUri().toString();
        Pattern pattern = Pattern.compile("/([^/]*)/[^/]*$");
        Matcher matcher = pattern.matcher(uri);
        // Set a job name to use to override existing one during runtime.
        if (matcher.find() && matcher.groupCount() > 0) {
            updatedJobName = matcher.group(1);
        }

        // Submit test report of this scenario.
        boolean testCasePassed = testCaseFinished.getResult().getStatus().isOk();
        TestReport test = new TestReport(testCaseFinished.getTestCase().getName(), testCasePassed, null);

        if (!AgentClient.getInstance().reportTest(test)) {
            LOG.error("Failed reporting test: [{}]", test);
        }
    }

    /**
     * Receives events when a step finishes, and reports the step.
     *
     * @param event of current step finish.
     */
    private void reportCucumberStepFinished(final TestStepFinished event) {
        // Check if step is Cucumber annotated method (Given, When, Then, And, But).
        if (event.getTestStep() instanceof PickleStepTestStep) {
            // Convert to step.
            final PickleStepTestStep cucumberStep = (PickleStepTestStep) event.getTestStep();
            reportCucumberStep(cucumberStep, event.getResult());
        }
    }

    /**
     * Analyze the Cucumber step and report it.
     *
     * @param cucumberStep the Cucumber test step.
     * @param result       of the Cucumber test step.
     */
    private void reportCucumberStep(final PickleStepTestStep cucumberStep, final Result result) {
        // Do not report if no active driver instance.
        if (AgentClient.getInstance() == null) {
            if (AgentClient.isWarned()) {
                LOG.warn("No active AgentClient instance, skipped reporting test step");
            }
            return;
        }

        // Create the step report details.
        String keyword = cucumberStep.getStep().getKeyword();
        String stepDescription = String.format("%s %s", keyword, cucumberStep.getStep().getText());
        String stepArguments = getCucumberStepArguments(cucumberStep);
        String stepMessage = "";
        if (StringUtils.isNotBlank(stepArguments)) {
            stepMessage = stepDescription + (" using parameters {" + stepArguments + "}");
        }
        boolean stepPassed = false;
        if (result.getStatus() == Status.PASSED) {
            stepPassed = true;
        } else {
            String errorMessage = (result.getError() != null) ? System.lineSeparator()
                    + result.getError().getMessage() : "";
            stepMessage = String.format("%s %s", stepMessage, errorMessage);
        }

        // Report the step.
        StepReport report = new StepReport(stepDescription, stepMessage, stepPassed, null);
        if (!AgentClient.getInstance().reportStep(report)) {
            LOG.error("Failed reporting step: [{}]", report);
        }
    }

    /**
     * Retrieve step arguments from currently running Cucumber step.
     *
     * @param cucumberStep the current Cucumber step.
     * @return the step arguments if any.
     */
    private String getCucumberStepArguments(final PickleStepTestStep cucumberStep) {
        return StringUtils.join(cucumberStep.getDefinitionArgument().stream().map(Argument::getValue).toArray(),
                ",");
    }

    /**
     * Will update the report job name if not specified.
     *
     * @param testRunFinished event for test run finishing.
     */
    private void updateCucumberJobName(final TestRunFinished testRunFinished) {
        // Do not report if no active driver instance.
        if (AgentClient.getInstance() == null) {
            if (AgentClient.isWarned()) {
                LOG.warn("No active AgentClient instance, skipped reporting test step");
            }
            return;
        }

        // Update the JobName by calling a REST endpoint in the Agent.
        if (AgentClient.getInstance().getSkipInferring()) {
            AgentClient.getInstance().updateJobName(updatedJobName);
        }
    }
}
