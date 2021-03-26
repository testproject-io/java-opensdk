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

package io.testproject.sdk.internal.rest.messages;

import io.testproject.sdk.drivers.ReportType;
import io.testproject.sdk.internal.rest.ReportSettings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Payload object sent to the Agent to start a development session.
 */
public class SessionRequest {

    /**
     * Capabilities that should be sent to the Agent for driver initialization.
     */
    private final Map<String, Object> capabilities;
    /**
     * This SDK version.
     * Can be overridden for <b>debugging</b> the SDK by setting a value for TP_DEBUG_SDK_VERSION environment variable
     */
    private String sdkVersion;
    /**
     * This SDK language, obviously Java.
     */
    private final String language = "Java";
    /**
     * Project name to report.
     */
    private String projectName;
    /**
     * Job name to report.
     */
    private String jobName;
    /**
     * Type of report to produce - cloud, local or both.
     */
    private ReportType reportType;

    /**
     * Creates a new instance using provided capabilities.
     *
     * @param reportSettings Report settings with Project and Job names to report
     * @param capabilities   capabilities that should be sent to the Agent for driver initialization
     */
    public SessionRequest(final ReportSettings reportSettings, final Map<String, Object> capabilities) {
        if (reportSettings != null) {
            this.projectName = reportSettings.getProjectName();
            this.jobName = reportSettings.getJobName();
            this.reportType = reportSettings.getReportType();
        }

        // Retrieves the version sent bu gradle when creating the JAR
        this.sdkVersion = getClass().getPackage().getImplementationVersion();

        // Version is not available when running SDK from source
        // Checking environment variable TP_DEBUG_SDK_VERSION
        if (StringUtils.isEmpty(this.sdkVersion)) {
            String debugVersion = System.getenv("TP_DEBUG_SDK_VERSION");
            Logger log = LoggerFactory.getLogger(SessionRequest.class);
            log.debug("Using value [{}] from TP_DEBUG_SDK_VERSION environment variable", debugVersion);
            this.sdkVersion = debugVersion;
        }

        this.capabilities = capabilities;
    }

    /**
     * Creates a new instance with a specified job name.
     *
     * @param jobName of the session request.
     */
    public SessionRequest(final String jobName) {
        this.jobName = jobName;
        this.capabilities = new HashMap<>();
    }

    /**
     * Getter for {@link #capabilities} field.
     *
     * @return value of {@link #capabilities} field
     */
    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    /**
     * Getter for {@link #sdkVersion} field.
     *
     * @return value of {@link #sdkVersion} field
     */
    public String getSdkVersion() {
        return sdkVersion;
    }

    /**
     * Getter for {@link #language} field.
     *
     * @return value of {@link #language} field
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Getter for {@link #projectName} field.
     *
     * @return value of {@link #projectName} field
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Getter for {@link #projectName} field.
     *
     * @return value of {@link #projectName} field
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
}
