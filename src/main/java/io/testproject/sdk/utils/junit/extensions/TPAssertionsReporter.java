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


package io.testproject.sdk.utils.junit.extensions;

import io.testproject.sdk.internal.reporting.Reporter;
import io.testproject.sdk.utils.junit.extensions.helpers.ExtendedReporter;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 TestProject Extension to report failure assertions
 */
public class TPAssertionsReporter implements AfterTestExecutionCallback {


    /**
     * After test method finish execute, check here if an AssertionError exception has be throw
     * And in such a case, report the agent the failure
     */
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {

        // Get the TestProject reporter
        if (Reporter.getInstance() == null)
            return;

        ExtendedReporter reporter = new ExtendedReporter(Reporter.getInstance());

        // Check if the test failed
        if (context.getExecutionException().isPresent()) {

            // Handle only AssertionError
            if (context.getExecutionException().get() instanceof AssertionError) {

                // Default result message
                String resultMessage = "Test Failed";

                AssertionError failedError = (AssertionError) context.getExecutionException().get();

                // Get the message from the exception if available
                if (failedError.getMessage() != null)
                    resultMessage = failedError.getMessage();

                // Report the final result to the Agent
                reporter.stepOnly(resultMessage,"",false,false);

            }
        }
    }
}
