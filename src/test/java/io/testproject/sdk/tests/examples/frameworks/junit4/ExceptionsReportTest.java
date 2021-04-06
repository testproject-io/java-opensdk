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

package io.testproject.sdk.tests.examples.frameworks.junit4;

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.internal.reporting.extensions.junit4.ExceptionsReportListener;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

import static org.junit.Assert.fail;


/**
 * Demonstrates the use of Assertions and Exceptions in a JUnit4 test class.
 */
@RunWith(ExceptionsReportListener.class)
public class ExceptionsReportTest {


    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    /**
     * Setup method to construct the driver used in the test.
     *
     * @throws InvalidTokenException
     * @throws AgentConnectException
     * @throws ObsoleteVersionException
     * @throws IOException
     */
    @BeforeClass
    public static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException,
            IOException {
        driver = new ChromeDriver(new ChromeOptions(), "Examples", "JUnit4 Assertions Example");
    }

    /**
     * Use fail() method to fail the test.
     */
    @Test
    public void testExample() {
        driver.report().step("Simple Step");
        fail("This test failed");
    }

    /**
     * Fail the test using an assertion that will be false.
     */
    @Test
    public void testExample2() {
        driver.navigate().to("http://example.testproject.io/");
        String title = driver.getTitle();
        Assert.assertEquals("another title", title);
    }

    /**
     * Throw an assertion error to fail the test.
     */
    @Test
    public void testExample3() {
        driver.navigate().to("http://example.testproject.io/");
        String title = driver.getTitle();
        if (!title.equals("another title")) {
            throw new AssertionError("This test failed because the title is not expected");
        }
    }

    /**
     * Quit the driver after the session.
     */
    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
