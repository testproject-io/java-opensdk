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

import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.testproject.sdk.drivers.ios.IOSDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Runs Web tests on {@link IOSDriver} in Safari.
 */
@EnabledIfEnvironmentVariable(named = "TP_IOS_DUT_UDID", matches = ".*?")
@DisplayName("iOS Driver and Safari")
class IOSSafariDriverTest {

    /**
     * Driver instance.
     */
    private static IOSDriver<WebElement> driver;
    /**
     * UDID of the device to run the test.
     */
    private static final String DUT_UDID = System.getenv("TP_IOS_DUT_UDID");

    /**
     * Name of the device under test (DUT).
     */
    private static final String DUT_NAME = System.getenv("TP_IOS_DUT_NAME");

    @BeforeAll
    private static void setup()
            throws InvalidTokenException, AgentConnectException, MalformedURLException, ObsoleteVersionException {
        assertNotNull(DUT_UDID, "TP_ANDROID_DUT_UDID environment variable is not set");
        assertNotNull(DUT_NAME, "TP_IOS_DUT_NAME environment variable is not set");

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
        capabilities.setCapability(MobileCapabilityType.UDID, DUT_UDID);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, DUT_NAME);
        capabilities.setCapability(CapabilityType.BROWSER_NAME, BrowserType.SAFARI);

        driver = new IOSDriver<>(capabilities, "Examples");
    }

    @Test
    @DisplayName("Example Test")
    void basicTest() {
        AutomationFlows.runFlow(driver);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
