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

import io.testproject.sdk.internal.rest.ReportSettings;

import java.util.List;

/**
 * Generic inferrer for Project and Job names.
 */
public class GenericInferrer implements ReportSettingsInferrer {

    /**
     * Stack traces list to analyze in order to infer Project and Job names.
     */
    private final List<StackTraceElement> traces;

    /**
     * Initializes a new Generic inferrer.
     *
     * @param traces Stack traces list to analyze.
     */
    public GenericInferrer(final List<StackTraceElement> traces) {
        this.traces = traces;
    }

    /**
     * Returns Project and Job names using package and class names.
     * <p>
     * Method uses the package name and the class names of the first entry in stack,
     * for Project and Job names accordingly.
     *
     * @return An instance of {@link ReportSettings} containing Project and Job names.
     */
    @Override
    public ReportSettings inferReportSettings() {
        StackTraceElement firstTrace = traces.get(traces.size() - 1);
        Class<?> clazz;
        try {
            clazz = Class.forName(firstTrace.getClassName());
        } catch (ClassNotFoundException e) {
            LOG.error("Failed to create an instance of a class", e);
            return getUnnamedEntries();
        }

        String projectName = getPackageName(clazz);
        String jobName = firstTrace.getClassName();

        return new ReportSettings(projectName, jobName);
    }

    /**
     * Infers Test name using specific Unit Test framework logic.
     *
     * @return A name of the Test.
     */
    @Override
    public String inferTestName() {
        return traces.get(traces.size() - 1).getMethodName();
    }
}
