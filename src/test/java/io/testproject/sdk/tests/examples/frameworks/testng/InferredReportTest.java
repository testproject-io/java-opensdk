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

package io.testproject.sdk.tests.examples.frameworks.testng;

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Runs tests on {@link ChromeDriver}.
 */
public class InferredReportTest {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeClass
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        if (StringUtils.isEmpty(System.getenv().get("TP_DEV_TOKEN"))) {
            throw new SkipException("TP_DEV_TOKEN Environment Variable is not defined or empty");
        }
        driver = new ChromeDriver(new ChromeOptions()); // Project & Job names are inferred
    }

    @Test(testName = "Example Test #1")
    void testExample1() {
        AutomationFlows.runWebFlow(driver);
    }

    @Test(testName = "Example Test #2")
    void testExample2() {
        AutomationFlows.runWebFlow(driver);
    }

    @Test(testName = "Example Test #3")
    void testExample3() {
        AutomationFlows.runWebFlow(driver);
    }

    @AfterClass
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
