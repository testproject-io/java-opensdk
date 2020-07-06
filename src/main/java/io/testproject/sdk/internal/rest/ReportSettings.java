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

package io.testproject.sdk.internal.rest;

/**
 * Report settings model provided to the Agent upon session initialization.
 */
public class ReportSettings {
    /**
     * Project name to report.
     */
    private final String projectName;
    /**
     * Job name to report.
     */
    private final String jobName;

    /**
     * Getter for {@link #projectName} field.
     *
     * @return value of {@link #projectName} field
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Getter for {@link #jobName} field.
     *
     * @return value of {@link #jobName} field
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Creates a new instance of the class.
     * @param projectName Project name to report
     * @param jobName Job name to report
     */
    public ReportSettings(final String projectName, final String jobName) {
        this.projectName = projectName;
        this.jobName = jobName;
    }
}
