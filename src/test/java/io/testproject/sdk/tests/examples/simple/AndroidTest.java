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

package io.testproject.sdk.tests.examples.simple;

import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.testproject.sdk.drivers.android.AndroidDriver;
import io.testproject.sdk.tests.capabilities.AppiumOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.CapabilityType;

import java.util.concurrent.TimeUnit;

/**
 * Android Test Basic Example.
 */
public final class AndroidTest {

    /**
     * Default implicit timeout.
     */
    public static final int TIMEOUT = 5;

    /**
     * Main executable method.
     * @param args N/A
     * @throws Exception is thrown when driver initialization fails.
     */
    public static void main(final String[] args) throws Exception {
        AppiumOptions capabilities = new AppiumOptions();

        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
        capabilities.setCapability(MobileCapabilityType.UDID, "{YOUR_DEVICE_UDID}");
        capabilities.setCapability(CapabilityType.BROWSER_NAME, "");
        capabilities.setCapability(MobileCapabilityType.APP,
                "https://github.com/testproject-io/android-demo-app/raw/master/APK/testproject-demo-app.apk");

        AndroidDriver<MobileElement> driver = new AndroidDriver<>(capabilities);
        driver.manage().timeouts().implicitlyWait(TIMEOUT, TimeUnit.SECONDS);

        // Reset App
        driver.resetApp();

        // Login using provided credentials
        driver.findElement(By.id("name")).sendKeys("John Smith");
        driver.findElement(By.id("password")).sendKeys("12345");
        driver.findElement(By.id("login")).click();

        boolean passed = driver.findElement(By.id("logout")).isDisplayed();
        if (passed) {
            System.out.println("Test Passed");
        } else {
            System.out.println("Test Failed");
        }

        driver.quit();
    }

    private AndroidTest() { }
}
