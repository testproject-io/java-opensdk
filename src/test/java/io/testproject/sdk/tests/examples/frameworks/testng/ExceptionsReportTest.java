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

package io.testproject.sdk.tests.examples.frameworks.testng;

import io.testproject.sdk.drivers.ReportingDriver;
import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.interfaces.testng.ExceptionsReporter;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;


/**
 * Demonstrates the use of ExceptionsReporter to include TestNG assertions in reports.
 */
@Listeners(io.testproject.sdk.internal.reporting.extensions.testng.ExceptionsReporter.class)
public class ExceptionsReportTest implements ExceptionsReporter {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeClass
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        if (StringUtils.isEmpty(System.getenv().get("TP_DEV_TOKEN"))) {
            throw new SkipException("TP_DEV_TOKEN Environment Variable is not defined or empty");
        }
        driver = new ChromeDriver(new ChromeOptions(), "Examples", "TestNG Assertions Example");
    }

    @Test(testName = "Example Test #1 - Report failure using fail() assertion")
    void testExample() {
        driver.report().step("Simple Step");
        fail("This test failed");
    }

    @Test(testName = "Example Test #2 - Report failing assertion using assertEquals()")
    void testExample2() {
        driver.navigate().to("http://example.testproject.io/");
        String title = driver.getTitle();
        assertEquals("another title", title);
    }

    @Test(testName = "Example Test #3 - Report AssertionError exception")
    void testExample3() {
        driver.navigate().to("http://example.testproject.io/");
        String title = driver.getTitle();
        if (!title.equals("another title")) {
            throw new AssertionError("This test failed because the title is not expected");
        }
    }

    @AfterClass
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Override
    public final ReportingDriver getDriver() {
        return driver;
    }

}
