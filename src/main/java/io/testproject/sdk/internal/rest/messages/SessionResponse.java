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

import java.util.List;
import java.util.Map;

/**
 * Payload object sent by the Agent in response to starting development session request.
 */
public class SessionResponse {

    /**
     * Port number that Agent is listening on for the SDK to connect.
     */
    private int devSocketPort;

    /**
     * Remote address of a Selenium / Appium server for the driver to communicate with.
     */
    private String serverAddress;

    /**
     * ID of a session that has been initialized by the Agent.
     */
    private String sessionId;

    /**
     * Dialect of the session that has been initialized by the Agent.
     */
    private String dialect;

    /**
     * Capabilities of the session that has been initialized by the Agent.
     */
    private Map<String, Object> capabilities;

    /**
     * Agent version.
     */
    private String version;

    /**
     * Local Report File Path.
     */
    private String localReport;

    /**
     * Local Report URL from remote execution.
     */
    private String localReportUrl;

    /**
     * Agent connection validation uuid.
     */
    private String uuid;

    /**
     * Warnings sent by the Agent for this development session.
     */
    private List<String> warnings;

    /**
     * Getter for {@link #devSocketPort} field.
     *
     * @return value of {@link #devSocketPort} field
     */
    public int getDevSocketPort() {
        return devSocketPort;
    }

    /**
     * Getter for {@link #serverAddress} field.
     *
     * @return value of {@link #serverAddress} field
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Getter for {@link #sessionId} field.
     *
     * @return value of {@link #sessionId} field
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Getter for {@link #dialect} field.
     *
     * @return value of {@link #dialect} field
     */
    public String getDialect() {
        return dialect;
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
     * Getter for {@link #version} field.
     *
     * @return value of {@link #version} field
     */
    public String getVersion() {
        return version;
    }

    /**
     * Getter for {@link #localReportUrl} field.
     *
     * @return value of {@link #localReportUrl} field
     */
    public String getLocalReportUrl() {
        return localReportUrl;
    }

    /**
     * Getter for {@link #localReport} field.
     *
     * @return value of {@link #localReport} field
     */
    public String getLocalReport() {
        return localReport;
    }

    /**
     * Getter for {@link #uuid} field.
     *
     * @return value of {@link #localReport} field
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Getter for {@link #warnings} field.
     *
     * @return value of {@link #warnings} field
     */
    public List<String> getWarnings() {
        return warnings;
    }
}
