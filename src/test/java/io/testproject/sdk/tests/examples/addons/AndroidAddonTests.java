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

import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import io.testproject.sdk.drivers.ActionRunner;
import io.testproject.sdk.drivers.android.AndroidDriver;
import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.examples.addons.actions.ClearFieldsAndroid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * Example of using a web action defined for multiple browsers.
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Android Addon")
public class AndroidAddonTests {
    /**
     * Default implicit timeout.
     */
    public static final int TIMEOUT = 5;

    /**
     * Runs the action on and android device.
     * @throws InvalidTokenException
     * @throws MalformedURLException
     * @throws ObsoleteVersionException
     * @throws AgentConnectException
     */
    @Test
    public void androidTest()
            throws InvalidTokenException, MalformedURLException, ObsoleteVersionException, AgentConnectException {
        DesiredCapabilities capabilities = new DesiredCapabilities();

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
        runAction(driver);
    }

    /**
     * Attempts to run the action on chrome driver. We expect this to fail.
     * @throws InvalidTokenException
     * @throws ObsoleteVersionException
     * @throws AgentConnectException
     * @throws IOException
     */
    @Test
    public void chromeTest()
            throws InvalidTokenException, ObsoleteVersionException, AgentConnectException, IOException {
        ChromeDriver driver = new ChromeDriver(new ChromeOptions(), "Examples");
        Assertions.assertThrows(InvalidArgumentException.class, () -> runAction(driver));
    }

    private <D extends RemoteWebDriver> void runAction(final ActionRunner<D> runner) {
        ClearFieldsAndroid action = new ClearFieldsAndroid();
        runner.addons().run(action);
    }
}
