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

package io.testproject.sdk.tests.examples.reports;

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.internal.reporting.ClosableTestReport;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;

/**
 * Runs tests suite on {@link ChromeDriver}.
 * Reports tests finish explicitly.
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Chrome Suite - Manual")
class ManualReporting {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver(new ChromeOptions());

        // Disabling automatic tests reporting, tests will be reported manually.
        driver.report().disableTestAutoReports(true);
    }

    /**
     * If this test will succeed it will reach the reporting line.
     * Reaching the test reporting line, will report test as passed (explicitly set).
     */
    @Test
    @DisplayName("Example Test")
    void testExample1() {
        AutomationFlows.runWebFlow(driver);
        driver.report().test("Test #1", true).submit();
    }

    /**
     * This test throws an exception.
     * By default, ClosableTestReport result is set to <em>false</em> reporting the test as failed.
     * But since it's wrapped in a try() block, ClosableTestReport will still be reported.
     */
    @Test
    @DisplayName("Example Test with Exception")
    void testExample2() {
        try (ClosableTestReport testReport = driver.report().test("Example Test with Exception")) {
            AutomationFlows.runWebFlow(driver);

            // Fail the test on purpose by searching for a non-existing element
            driver.findElement(By.id("NO_SUCH_ELEMENT")).click();
        }
    }

    /**
     * If this test will succeed it will reach the reporting lines.
     * Report result is explicitly set as <em>passed</em>.
     * If any exception will happen before the report is submitted, it will be reported as failed.
     */
    @Test
    @DisplayName("Example Test #3")
    @SuppressFBWarnings(
        value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE",
        justification = "Java 11 spotbugs issue: https://github.com/spotbugs/spotbugs/issues/756")
    void testExample3() {
        try (ClosableTestReport testReport = driver.report().test("Example Test with result override")) {
            AutomationFlows.runWebFlow(driver);

            testReport.setResult(true);
        }
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
