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

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.Dialect;

import java.net.URL;

public final class AgentSession {

    /**
     * Agent API base URL.
     */
    private final URL remoteAddress;

    /**
     * Remote SessionID of the driver created by the Agent.
     */
    private final String sessionId;

    /**
     * Dialect of the session.
     */
    private final Dialect dialect;

    /**
     * Actual capabilities returned by the driver after it has been created.
     */
    private final Capabilities capabilities;

    /**
     * Default constructor.
     *
     * @param remoteAddress Agent API base URL
     * @param sessionId     Remote SessionID of the driver created by the Agent
     * @param dialect       Dialect of the session
     * @param capabilities  Actual capabilities returned by the driver after it has been created
     */
    AgentSession(final URL remoteAddress,
                 final String sessionId,
                 final Dialect dialect,
                 final Capabilities capabilities) {
        this.remoteAddress = remoteAddress;
        this.sessionId = sessionId;
        this.dialect = dialect;
        this.capabilities = capabilities;
    }

    /**
     * Getter for {@link #remoteAddress} field.
     *
     * @return value of {@link #remoteAddress} field
     */
    public URL getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * Getter for {@link #capabilities} field.
     *
     * @return value of {@link #capabilities} field
     */
    public Capabilities getCapabilities() {
        return capabilities;
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
    public Dialect getDialect() {
        return dialect;
    }
}
