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

import io.testproject.sdk.drivers.ReportType;

import java.util.Objects;

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
     * Report type = cloud, local ort both.
     */
    private ReportType reportType;

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
     * Getter for {@link #reportType} field.
     *
     * @return value of {@link #reportType} field
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * Creates a new instance of the class.
     *
     * @param projectName Project name to report
     * @param jobName     Job name to report
     */
    public ReportSettings(final String projectName, final String jobName) {
        this.projectName = projectName;
        this.jobName = jobName;
        this.reportType = ReportType.CLOUD_AND_LOCAL;
    }

    /**
     * Creates a new instance of the class.
     *
     * @param projectName Project name to report
     * @param jobName     Job name to report
     * @param reportType  Report type to produce - cloud, local or both.
     */
    public ReportSettings(final String projectName, final String jobName, final ReportType reportType) {
        this.projectName = projectName;
        this.jobName = jobName;
        this.reportType = reportType;
    }

    /**
     * Check if two objects are equal.
     *
     * @param object instance.
     * @return True if equals, otherwise False.
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        ReportSettings that = (ReportSettings) object;
        return Objects.equals(projectName, that.projectName)
                && Objects.equals(jobName, that.jobName);
    }

    /**
     * Calculate object hashcode.
     *
     * @return Hashcode integer.
     */
    @Override
    public int hashCode() {
        return Objects.hash(projectName, jobName);
    }
}
