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

import io.testproject.sdk.drivers.web.ChromeDriver;
import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.utils.junit.TPJunit5Extension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Runs tests on {@link ChromeDriver}.
 */
@ExtendWith({TPJunit5Extension.class}) // This will make the fail() method and AssertionError to be reported to TestProject
public class TPJUnitExtension {

    /**
     * Driver instance.
     */
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() throws InvalidTokenException, AgentConnectException, ObsoleteVersionException, IOException {
        driver = new ChromeDriver("E_i-Y-jtcTkGkLLmCvXpPncR1QVZTR6mptItwFvlyGw1", new ChromeOptions()); // Project & Job names are inferred
    }

    @Test
    @DisplayName("Test assertion using fail() method")
    void testExample() {
        driver.report().step("Simple Step");
        fail("This test failed");
    }

    @Test
    @DisplayName("Example assertion using assertEquals")
    void testExample2() {
        driver.navigate().to("http://example.testproject.io/");

        String title = driver.getTitle();

        assertEquals("another title", title);

    }

    @Test
    @DisplayName("Example assertion using throw AssertionError exception")
    void testExample3() {
        driver.navigate().to("http://example.testproject.io/");

        String title = driver.getTitle();

        if (!title.equals("another title"))
            throw new AssertionError("This test failed because the title is not expected");

    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
