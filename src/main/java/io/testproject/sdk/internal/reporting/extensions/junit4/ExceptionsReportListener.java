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

package io.testproject.sdk.internal.reporting.extensions.junit4;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * JUnit4 Test class runner using the Exceptions reporter.
 * The test runner class must be specified in a @RunWith annotation
 * to enable assertion reporting.
 */
public class ExceptionsReportListener extends BlockJUnit4ClassRunner {

    /**
     * Default constructor.
     * @param testClass
     * @throws InitializationError
     */
    public ExceptionsReportListener(final Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    /**
     * Register test case failure event listener.
     * @param notifier
     */
    @Override
    public void run(final RunNotifier notifier) {
        notifier.addListener(new ExceptionsReporter());
        super.run(notifier);
    }
}
