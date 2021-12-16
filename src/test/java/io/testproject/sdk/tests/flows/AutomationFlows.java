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

package io.testproject.sdk.tests.flows;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.testproject.sdk.drivers.android.AndroidDriver;
import io.testproject.sdk.drivers.ios.IOSDriver;
import io.testproject.sdk.tests.flows.objects.android.ProfilePage;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Flows to automate example Website and Apps.
 */
public final class AutomationFlows {

    /**
     * Name.
     */
    public static final String FULL_NAME = "John Smith";

    /**
     * Password.
     */
    public static final String PASSWORD = "12345";

    /**
     * Country.
     */
    public static final String COUNTRY_NAME = "United States";

    /**
     * Address.
     */
    public static final String ADDRESS = "Street number and name";

    /**
     * Email.
     */
    public static final String EMAIL = "john.smith@somewhere.tld";

    /**
     * Phone.
     */
    private static final String PHONE = "+1 555 555 55";

    /**
     * Default resolution width.
     */
    public static final int DEFAULT_WIDTH = 1920;

    /**
     * Default resolution height.
     */
    public static final int DEFAULT_HEIGHT = 1080;

    /**
     *
     */
    public static final String APPIUM_CAPABILITY_PREFIX = "appium:";

    /**
     * Private default constructor to prevent instance initialization of this utility class.
     */
    private AutomationFlows() { }

    /**
     * Executes a simple test flow on the example website.
     * @param driver WebDriver to use for autoamting the browser
     */
    public static void runWebFlow(final WebDriver driver) {
        // Navigate to TestProject Example website
        driver.navigate().to("https://example.testproject.io/web/");

        // Login using provided credentials
        io.testproject.sdk.tests.flows.objects.web.LoginPage loginPage =
                PageFactory.initElements(driver,
                        io.testproject.sdk.tests.flows.objects.web.LoginPage.class);
        loginPage.login(FULL_NAME, PASSWORD);

        // Complete profile forms and save it
        io.testproject.sdk.tests.flows.objects.web.ProfilePage profilePage =
                new io.testproject.sdk.tests.flows.objects.web.ProfilePage(driver);
        profilePage.updateProfile(COUNTRY_NAME, ADDRESS, EMAIL, PHONE);

        // Make sure profile is saved
        Assertions.assertTrue(profilePage.isSaved());
    }

    /**
     * Executes a simple test flow on the example Android App.
     * @param driver AndroidDriver to use for automating the application
     */
    public static void runAndroidFlow(final AndroidDriver driver) {
        // Reset app to pristine state
        String appId = (String) driver.getCapabilities().getCapability(AndroidMobileCapabilityType.APP_PACKAGE);
        if (null == appId) {
            appId = (String) driver.getCapabilities().getCapability(
                    APPIUM_CAPABILITY_PREFIX.concat(AndroidMobileCapabilityType.APP_PACKAGE));
        }
        driver.terminateApp(appId);
        driver.activateApp(appId);

        // Login using provided credentials
        io.testproject.sdk.tests.flows.objects.android.LoginPage loginPage =
                new io.testproject.sdk.tests.flows.objects.android.LoginPage(driver);
        loginPage.login(FULL_NAME, PASSWORD);

        // Complete profile forms and save it
        ProfilePage profilePage =
                new ProfilePage(driver);
        profilePage.updateProfile(COUNTRY_NAME, ADDRESS, EMAIL, PHONE);

        // Make sure profile is saved
        Assertions.assertTrue(profilePage.isSaved());
    }

    /**
     * Executes a simple test flow on the example iOS App.
     * @param driver IOSDriver to use for automating the application
     */
    public static void runIOSFlow(final IOSDriver driver) {
        // Reset app to pristine state
        String bundleId = (String) driver.getCapabilities().getCapability(IOSMobileCapabilityType.BUNDLE_ID);
        if (null == bundleId) {
            bundleId = (String) driver.getCapabilities().getCapability(
                    APPIUM_CAPABILITY_PREFIX.concat(IOSMobileCapabilityType.BUNDLE_ID));
        }
        driver.terminateApp(bundleId);
        driver.activateApp(bundleId);


        // Login using provided credentials
        io.testproject.sdk.tests.flows.objects.ios.LoginPage loginPage =
                new io.testproject.sdk.tests.flows.objects.ios.LoginPage(driver);
        loginPage.login(FULL_NAME, PASSWORD);

        // Complete profile forms and save it
        io.testproject.sdk.tests.flows.objects.ios.ProfilePage profilePage =
                new io.testproject.sdk.tests.flows.objects.ios.ProfilePage(driver);
        profilePage.updateProfile(COUNTRY_NAME, ADDRESS, EMAIL, PHONE);

        // Make sure profile is saved
        Assertions.assertTrue(profilePage.isSaved());
    }
}
