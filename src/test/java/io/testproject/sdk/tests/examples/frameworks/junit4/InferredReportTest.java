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

package io.testproject.sdk.tests.examples.frameworks.junit4;

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

/**
 * Runs tests on {@link ChromeDriver}.
 */
class InferredReportTest {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeClass
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver(new ChromeOptions()); // Project & Job names are inferred
    }

    @Test
    void testExample() {
        AutomationFlows.runWebFlow(driver);
    }

    @Test
    void testExample2() {
        AutomationFlows.runWebFlow(driver);
    }

    @Test
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
