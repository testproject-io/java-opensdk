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

import io.testproject.sdk.drivers.web.RemoteWebDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;

/**
 * Runs tests on {@link RemoteWebDriver}.
 * Providing different capabilities for all Desktop browsers
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Remote Web Driver")
class RemoteWebDriverTest {

    @Test
    @DisplayName("Chrome Test")
    void testExampleChrome()
            throws MalformedURLException, InvalidTokenException, AgentConnectException, ObsoleteVersionException {
        org.openqa.selenium.WebDriver driver = new RemoteWebDriver(new ChromeOptions(),
                "Examples", null);
        AutomationFlows.runFlow(driver);
        driver.quit();
    }

    @Test
    @DisplayName("Firefox Test")
    void testExampleFirefox()
            throws MalformedURLException, InvalidTokenException, AgentConnectException, ObsoleteVersionException {
        org.openqa.selenium.WebDriver driver = new RemoteWebDriver(new FirefoxOptions(),
                "Examples", null);
        AutomationFlows.runFlow(driver);
        driver.quit();
    }

    @Test
    @EnabledOnOs(OS.MAC)
    @DisplayName("Safari Test")
    void testExampleSafari()
            throws MalformedURLException, InvalidTokenException, AgentConnectException, ObsoleteVersionException {
        org.openqa.selenium.WebDriver driver = new RemoteWebDriver(new SafariOptions(),
                "Examples", null);
        AutomationFlows.runFlow(driver);
        driver.quit();
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("Internet Explorer Test")
    void testExampleInternetExplorer()
            throws MalformedURLException, InvalidTokenException, AgentConnectException, ObsoleteVersionException {
        org.openqa.selenium.WebDriver driver = new RemoteWebDriver(new InternetExplorerOptions(),
                "Examples", null);
        AutomationFlows.runFlow(driver);
        driver.quit();
    }

    @Test
    @DisplayName("Edge Test")
    void testExampleEdge()
            throws MalformedURLException, InvalidTokenException, AgentConnectException, ObsoleteVersionException {
        org.openqa.selenium.WebDriver driver = new RemoteWebDriver(new EdgeOptions(),
                "Examples", null);
        AutomationFlows.runFlow(driver);
        driver.quit();
    }
}
