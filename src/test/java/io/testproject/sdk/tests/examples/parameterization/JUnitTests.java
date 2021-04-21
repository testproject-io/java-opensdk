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

package io.testproject.sdk.tests.examples.parameterization;

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.interfaces.parameterization.TestProjectParameterizer;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
public final class JUnitTests {
    /**
     * A simple parameterized test.
     * @param username The username.
     * @param password The password
     * @throws InvalidTokenException If token is invalid.
     * @throws AgentConnectException If unable to connect to agent.
     * @throws ObsoleteVersionException If SDK version is obsolete.
     * @throws IOException On any IO error.
     */
    @ParameterizedTest
    @ArgumentsSource(TestProjectParameterizer.class)
    @EnabledIfEnvironmentVariable(named = "TP_TEST_DATA_PROVIDER", matches = ".*?")
    public void paramTest(final String username, final String password)
            throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        ChromeDriver driver = new ChromeDriver(new ChromeOptions());

        // Navigate to TestProject Example website
        driver.navigate().to("https://example.testproject.io/web/");

        // Login using provided credentials
        driver.findElement(By.cssSelector("#name")).sendKeys(username);
        driver.findElement(By.cssSelector("#password")).sendKeys(password);
        driver.findElement(By.cssSelector("#login")).click();

        boolean passed = driver.findElement(By.cssSelector("#logout")).isDisplayed();
        if (passed) {
            System.out.println("Test Passed");
        } else {
            System.out.println("Test Failed");
        }

        driver.quit();
    }
}
