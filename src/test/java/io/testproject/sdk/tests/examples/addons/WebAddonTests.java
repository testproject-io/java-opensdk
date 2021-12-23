/*
 * Copyright (c) 2021 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.tests.examples.addons;

import io.testproject.sdk.drivers.ActionRunner;
import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.drivers.web.FirefoxDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.examples.addons.actions.ClearFieldsWeb;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;

/**
 * Example of using a web action defined for multiple browsers.
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Web Addon")
public class WebAddonTests {
    /**
     * Runs Clear Fields action on chrome.
     * @throws InvalidTokenException
     * @throws ObsoleteVersionException
     * @throws AgentConnectException
     * @throws IOException
     */
    @Test
    public void chromeTest()
            throws InvalidTokenException, ObsoleteVersionException, AgentConnectException, IOException {
        ChromeDriver driver = new ChromeDriver(new ChromeOptions(), "Examples");
        runAction(driver);
    }

    /**
     * Runs Clear Fields action on firefox.
     * @throws InvalidTokenException
     * @throws ObsoleteVersionException
     * @throws AgentConnectException
     * @throws IOException
     */
    @Test
    public void firefoxTest()
            throws InvalidTokenException, ObsoleteVersionException, AgentConnectException, IOException {
        FirefoxDriver driver = new FirefoxDriver(new FirefoxOptions(), "Examples");
        runAction(driver);
    }

    private <D extends RemoteWebDriver> void runAction(final ActionRunner<D> runner) {
        ClearFieldsWeb action = new ClearFieldsWeb();
        runner.addons().run(action);
    }
}
