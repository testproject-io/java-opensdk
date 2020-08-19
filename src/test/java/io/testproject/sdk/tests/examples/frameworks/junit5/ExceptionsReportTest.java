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

package io.testproject.sdk.tests.examples.frameworks.junit5;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.interfaces.junit5.ExceptionsReporter;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Demonstrates the use of ExceptionsReporter to include JUNit assertions in reports.
 */
@EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = ".*?")
@DisplayName("Chrome Driver")
class ExceptionsReportTest implements ExceptionsReporter {


    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver(new ChromeOptions(), "Examples", "JUnit5 Assertions Example");
    }

    @Test
    @DisplayName("Example Test #1 - Report failure using fail() assertion")
    void testExample() {
        driver.report().step("Simple Step");
        fail("This test failed");
    }

    @Test
    @DisplayName("Example Test #2 - Report failing assertion using assertEquals()")
    void testExample2() {
        driver.navigate().to("http://example.testproject.io/");
        String title = driver.getTitle();
        assertEquals("another title", title);
    }

    @Test
    @DisplayName("Example Test #3 - Report AssertionError exception")
    void testExample3() {
        driver.navigate().to("http://example.testproject.io/");
        String title = driver.getTitle();
        if (!title.equals("another title")) {
            throw new AssertionError("This test failed because the title is not expected");
        }
    }

    @Test
    @DisplayName("Example Test #4 - Report standard exception")
    void testExample4() throws Exception {
        driver.navigate().to("http://example.testproject.io/");
        driver.report().step("Some Step");
        throw new Exception("Even standard exception is reported");
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Getter for a reporting driver instance.
     *
     * @return ReportingDriver instance.
     */
    @Override
    public ReportingDriver getDriver() {
        return driver;
    }

}
