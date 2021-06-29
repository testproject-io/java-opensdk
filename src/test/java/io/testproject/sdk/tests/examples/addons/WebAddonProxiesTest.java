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

package io.testproject.sdk.tests.examples.addons;

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.examples.addons.proxies.JavaWebExampleAddon;
import io.testproject.sdk.tests.flows.AutomationFlows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;

/**
 * Runs tests on {@link ChromeDriver}.
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Addon Proxies")
class WebAddonProxiesTest {

    /**
     * Password.
     */
    private static final String WRONG_PASSWORD = "54321";

    /**
     * Phone number element CSS locator.
     */
    public static final String PHONE_NUMBER_ELEMENT_LOCATOR = "#phone";

    /**
     * Maximum digits in the random phone number to generate.
     */
    public static final int RANDOM_PHONE_MAX_DIGITS = 7;

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver(new ChromeOptions(), "Examples");
    }

    @Test
    @DisplayName("Example Test")
    void testExample() {
        // Setting window size to avoid element being obscured in headless mode
        driver.manage().window().setSize(new Dimension(
                AutomationFlows.DEFAULT_WIDTH, AutomationFlows.DEFAULT_HEIGHT));

        // Navigate to TestProject Example website
        driver.navigate().to("https://example.testproject.io/web/");

        // Login using provided credentials
        io.testproject.sdk.tests.flows.objects.web.LoginPage loginPage =
                PageFactory.initElements(driver,
                        io.testproject.sdk.tests.flows.objects.web.LoginPage.class);
        loginPage.login(AutomationFlows.FULL_NAME, WRONG_PASSWORD);

        // Use Addon proxy to invoke 'Clear Fields' Action
        driver.addons().execute(JavaWebExampleAddon.getClearFieldsAction());

        // Login using correct credentials
        loginPage.login(AutomationFlows.FULL_NAME, AutomationFlows.PASSWORD);

        // Complete profile form with an empty phone number
        io.testproject.sdk.tests.flows.objects.web.ProfilePage profilePage =
                new io.testproject.sdk.tests.flows.objects.web.ProfilePage(driver);
        profilePage.updateProfile(
                AutomationFlows.COUNTRY_NAME,
                AutomationFlows.ADDRESS,
                AutomationFlows.EMAIL,
                "");

        // Use Addon proxy to invoke 'Type Random Phone' Action
        // Notice how the action parameters are provided using an action proxy convenience method
        driver.addons().execute(
                JavaWebExampleAddon.typeRandomPhoneAction("44", RANDOM_PHONE_MAX_DIGITS),
                // Passing a 'By' instance, provides an element action with it's target
                By.cssSelector(PHONE_NUMBER_ELEMENT_LOCATOR));

        // Save the profile form
        profilePage.save();

        // Take screenshot
        driver.report().step("Profile completed",
                profilePage.isSaved(), true);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
