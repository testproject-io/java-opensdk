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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface to be implemented by classes that infer Project and Job names.
 * Different unit testing frameworks (JUnit or TestNG) are inferred differently.
 */
public interface ReportSettingsInferrer {

    /**
     * Logger instance.
     */
    Logger LOG = LoggerFactory.getLogger(ReportSettingsInferrer.class);

    /**
     * Infers Project and Job names from specific unit testing framework annotations.
     *
     * @return An instance of {@link ReportSettings} containing Project and Job names.
     */
    ReportSettings inferReportSettings();

    /**
     * A default response for any inferrer, with 'Unnamed' Project and Job.
     *
     * @return An instance of {@link ReportSettings} containing 'Unnamed' Project and Job names.
     */
    default ReportSettings getUnnamedEntries() {
        LOG.info("Failed to infer Project and Job names, will use default 'Unnamed' values.");
        return new ReportSettings("Unnamed Project", "Unnamed Job");
    }

    /**
     * Infers Test name using specific Unit Test framework annotations.
     * @return A name of the Test.
     */
    String inferTestName();
}
