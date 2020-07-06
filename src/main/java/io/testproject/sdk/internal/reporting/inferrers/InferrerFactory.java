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

package io.testproject.sdk.internal.reporting.inferrers;

import java.util.List;

import static io.testproject.sdk.internal.reporting.FrameworksNames.JUNIT_PACKAGE_NAME_PREFIX;
import static io.testproject.sdk.internal.reporting.FrameworksNames.TESTNG_PACKAGE_NAME_PREFIX;

/**
 * Utility class used to create specific types of inferrers.
 */
public final class InferrerFactory {

    /**
     * Private default constructor to prevent instance initialization of this utility class.
     */
    private InferrerFactory() {
    }

    /**
     * Initializes an inferrer specific type based on provided stack traces.
     * @param traces Stack traces list to analyze in order to determine specific inferrer type.
     * @return An instance of an inferrer compatible with the Unit Testing framework used.
     */
    public static ReportSettingsInferrer getInferrer(final List<StackTraceElement> traces) {
        if (traces.stream().anyMatch(p -> p.getClassName().startsWith(JUNIT_PACKAGE_NAME_PREFIX))) {
            return new JUnitInferrer(traces);
        } else if (traces.stream().anyMatch(p -> p.getClassName().startsWith(TESTNG_PACKAGE_NAME_PREFIX))) {
            return new TestNGInferrer(traces);
        } else {
            return new GenericInferrer(traces);
        }
    }
}
