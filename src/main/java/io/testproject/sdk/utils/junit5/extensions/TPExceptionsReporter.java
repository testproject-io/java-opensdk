/*
 * Copyright 2020 TestProject LTD. and/or its affiliates
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


package io.testproject.sdk.utils.junit5.extensions;

import io.testproject.sdk.internal.reporting.Reporter;
import io.testproject.sdk.utils.junit5.extensions.internal.ExtendedReporter;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriverException;

/**
 * JUnit 5 TestProject Extension to report failure assertions and any other exceptions
 * Not including WebDriverException.
 *
 * If a test throws exception that is not WebDriverException, it is not reported to
 * the cloud. With this JUnit 5 extension, any exception will be reported also.
 */
public class TPExceptionsReporter implements AfterTestExecutionCallback {


    /**
     * After test method finish execute, check here if an AssertionError exception has be throw
     * And in such a case, report the agent the failure.
     */
    @Override
    public void afterTestExecution(final ExtensionContext context) throws Exception {

        // Get the TestProject reporter
        if (Reporter.getInstance() == null) {
            return;
        }

        ExtendedReporter reporter = new ExtendedReporter(Reporter.getInstance());

        // Check if the test failed
        if (context.getExecutionException().isPresent()) {

            // Do not handle any exception from the driver.
            // These exceptions are handled anyway by the native TestProject reporter inside the driver.
            if (!(context.getExecutionException().get() instanceof WebDriverException)) {

                // All exceptions, including AssertionError form JUnit 5 extends Throwable.
                // So cast it to Throwable.
                Throwable throwable = (Throwable) context.getExecutionException().get();

                // Do not handle any exceptions from the SDK.
                if (!throwable.getClass().getPackage().getName().startsWith("io.testproject.sdk.internal.exceptions")) {

                    // Default result message
                    String resultMessage = "Test Failed";

                    // Get the message from the exception if available
                    if (throwable.getMessage() != null) {
                        resultMessage = throwable.getMessage();
                    }

                    // Report the final result to the Agent
                    reporter.stepOnly(resultMessage, "", false, false);

                }

            }
        }
    }
}
