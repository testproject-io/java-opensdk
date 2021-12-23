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

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.tests.examples.addons.actions.TypeWithEnterAction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Addon With Parameters")
public class AddonsWithParameterTest {
    /**
     * Password to type.
     */
    public static final String PASSWORD_TEXT = "12345";
    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver(new ChromeOptions(), "Examples");
    }

    /**
     * Executes the action on TestProject Example Website.
     */
    @Test
    public void executeTest() {
        driver.navigate().to("http://example.testproject.io");
        driver.findElement(By.cssSelector("#name")).sendKeys("John Smith");
        TypeWithEnterAction action = new TypeWithEnterAction();
        action.setText(PASSWORD_TEXT);
        driver.addons().run(action, By.cssSelector("#password"));
    }
}
