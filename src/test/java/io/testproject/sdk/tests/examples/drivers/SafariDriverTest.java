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

package io.testproject.sdk.tests.examples.drivers;

import io.testproject.sdk.drivers.web.SafariDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.openqa.selenium.safari.SafariOptions;

import java.io.IOException;

/**
 * Runs tests on {@link SafariDriver}.
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@EnabledOnOs(OS.MAC)
@DisplayName("Safari Driver with Report Settings")
class SafariDriverTest {

    /**
     * Driver instance.
     */
    private static SafariDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new SafariDriver(new SafariOptions(), "Examples");
    }

    @Test
    @DisplayName("Example Test")
    void testExample() {
        AutomationFlows.runFlow(driver);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
