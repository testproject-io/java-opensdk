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
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

/**
 * Runs tests suite on {@link ChromeDriver}.
 * Reports tests finish automatically.
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Chrome Suite - Automatic")
class AutomaticReporting {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver(new ChromeOptions());
    }

    @Test
    @DisplayName("Example Test #1")
    void testExample1() {
        AutomationFlows.runWebFlow(driver);
    }

    @Test
    @DisplayName("Example Test #2 with Exception")
    void testExample2() {
        AutomationFlows.runWebFlow(driver);

        // Fail the test on purpose by searching for a non-existing element
        driver.findElement(By.id("NO_SUCH_ELEMENT")).click();
    }

    @Test
    @DisplayName("Example Test #3")
    void testExample3() {
        AutomationFlows.runWebFlow(driver);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
