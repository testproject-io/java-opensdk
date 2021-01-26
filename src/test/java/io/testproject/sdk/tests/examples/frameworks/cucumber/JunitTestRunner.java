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

package io.testproject.sdk.tests.examples.frameworks.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * Example JUnit Cucumber runner.
 * Inside the CucumberOptions:
 *      features = path/to/feature/files/directory
 *      glue = package/of/step/definitions/implementations
 *      plugins = package/of/testproject/cucumber/reporting/plugin
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/java/io/testproject/sdk/tests/examples/frameworks/cucumber/features/",
        glue = "io.testproject.sdk.tests.examples.frameworks.cucumber.stepdefinitions",
        plugin = "io.testproject.sdk.internal.reporting.extensions.cucumber.CucumberReporter")
final class JunitTestRunner {

    /**
     * Default constructor.
     */
    private JunitTestRunner() {

    }

    /**
     * Will be executed before the feature files.
     */
    @BeforeClass
    public static void setUp() {
        System.out.println("Starting feature test run");
    }

    /**
     * Will be executed after the feature files.
     */
    @AfterClass
    public static void tearDown() {
        System.out.println("Finishing feature test run");
    }
}
