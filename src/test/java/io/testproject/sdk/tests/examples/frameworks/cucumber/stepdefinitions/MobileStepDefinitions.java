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

package io.testproject.sdk.tests.examples.frameworks.cucumber.stepdefinitions;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.testproject.sdk.drivers.android.AndroidDriver;
import io.testproject.sdk.drivers.ios.IOSDriver;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;

import java.util.concurrent.TimeUnit;

/**
 * Step definitions for android_login.feature and ios_login.feature.
 */
public class MobileStepDefinitions {

    /**
     * Appium Driver.
     */
    private AppiumDriver driver;

    /**
     * Driver implicit wait timeout.
     */
    private static final int TIMEOUT = 15000;

    /**
     * Initializes the correct driver per the specified device type.
     * and sets the class osType parameter.
     *
     * @param osType type of driver to create.
     * @throws Exception when failed to initialize driver.
     */
    @Given("^I open the (.*) App$")
    public void initializeDriver(final String osType) throws Exception {
        if (!StringUtils.equalsIgnoreCase(osType, Platform.ANDROID.toString())
                && !StringUtils.equalsIgnoreCase(osType, Platform.IOS.toString())) {
            throw new IllegalArgumentException("Specified driver type was not Android or iOS !");
        }

        MutableCapabilities capabilities = new MutableCapabilities();
        if (StringUtils.equals(osType, "Android")) {
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
            capabilities.setCapability(MobileCapabilityType.UDID, "{YOUR_DEVICE_UDID}");
            capabilities.setCapability(MobileCapabilityType.APP,
                    "https://github.com/testproject-io/android-demo-app/raw/master/APK/testproject-demo-app.apk");
            driver = new AndroidDriver(capabilities, "Cucumber", "Android Test");
        } else if (StringUtils.equals(osType, "iOS")) {
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
            capabilities.setCapability(MobileCapabilityType.UDID, "{YOUR_DEVICE_UDID}");
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "{YOUR_DEVICE_NAME}");
            // Compile and deploy the App from source https://github.com/testproject-io/ios-demo-app
            capabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "io.testproject.Demo");
            driver = new IOSDriver(capabilities, "Cucumber", "iOS Test");
        }

        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.MILLISECONDS);

        // Reset App
        String appCapability = AndroidDriver.class.isAssignableFrom(driver.getClass())
                ? AndroidMobileCapabilityType.APP_PACKAGE
                : IOSMobileCapabilityType.BUNDLE_ID;
        String bundleId = (String) driver.getCapabilities().getCapability(appCapability);
        ((InteractsWithApps) driver).terminateApp(bundleId);
        ((InteractsWithApps) driver).activateApp(bundleId);
    }

    /**
     * Perform a login using the driver corresponding to the device OS type specified
     * when creating the driver.
     */
    @When("I login using my credentials")
    public void loginToApplication() {
        driver.findElement(By.id("name")).sendKeys("John Smith");
        driver.findElement(By.id("password")).sendKeys("12345");
        driver.findElement(By.id("login")).click();
    }

    /**
     * Validate that login was successful by checking if the
     * logout button is displayed.
     */
    @Then("I will see a logout button")
    public void validateLogoutButtonDisplayed() {
        Assertions.assertTrue(driver.findElement(By.id("logout")).isDisplayed());
    }

    /**
     * Close the mobile application.
     */
    @And("I close the app")
    public void closeApplication() {
        String appCapability = AndroidDriver.class.isAssignableFrom(driver.getClass())
                ? AndroidMobileCapabilityType.APP_PACKAGE
                : IOSMobileCapabilityType.BUNDLE_ID;

        String bundleId = (String) driver.getCapabilities().getCapability(appCapability);
        ((InteractsWithApps) driver).terminateApp(bundleId);
    }

}
