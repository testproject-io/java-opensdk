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

package io.testproject.sdk.tests.ci.drivers;

import io.testproject.sdk.drivers.TestProjectCapabilityType;
import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Runs tests on {@link ChromeDriver}.
 */
@DisplayName("Cloud Driver")
class CloudDriverTest {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        Assertions.assertNotNull(System.getenv().get("TP_DEV_TOKEN"));
        Assertions.assertNotNull(System.getenv().get("TP_CLOUD_URL"));

        ChromeOptions options = new ChromeOptions();

        // Set SouceLab options to be recognized as W3C driver.
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("name", "test");
        options.setCapability("sauce:options", sauceOptions);
        options.setCapability(TestProjectCapabilityType.CLOUD_URL, System.getenv().get("TP_CLOUD_URL"));

        driver = new ChromeDriver(options, "CI - Java");
    }

    @Test
    @DisplayName("Example Test on Cloud Driver")
    void testExample() {
        AutomationFlows.runWebFlow(driver);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
