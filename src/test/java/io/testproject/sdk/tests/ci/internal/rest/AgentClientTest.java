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

package io.testproject.sdk.tests.ci.internal.rest;

import io.testproject.sdk.internal.exceptions.AgentConnectException;
import io.testproject.sdk.internal.exceptions.InvalidTokenException;
import io.testproject.sdk.internal.exceptions.ObsoleteVersionException;
import io.testproject.sdk.internal.rest.AgentClient;
import io.testproject.sdk.internal.rest.ReportSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AgentClient")
class AgentClientTest {

    /**
     * Constant for invalid token placeholder.
     */
    private static final String INVALID_TOKEN = "INVALID_TOKEN";

    /**
     * Regular expression for non-empty string.
     */
    private static final String REGEX_NOT_EMPTY = ".*?";

    /**
     * Regular expression for an empty string.
     */
    private static final String REGEX_EMPTY = "^$";

    @Test
    @DisplayName("Empty token")
    @DisabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = REGEX_NOT_EMPTY)
    void testEmptyToken() {
        assertThrows(InvalidTokenException.class, () ->
                AgentClient.getClient(new ChromeOptions(), new ReportSettings("CI - Java", null)));
    }

    @Test
    @DisplayName("Empty token in TP_DEV_TOKEN")
    @EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = REGEX_EMPTY)
    void testEmptyTokenEnv() {
        assertThrows(InvalidTokenException.class, () ->
                AgentClient.getClient(new ChromeOptions(), new ReportSettings("CI - Java", null)));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "TP_AGENT_URL", matches = REGEX_EMPTY)
    @DisplayName("Malformed URL in TP_AGENT_URL")
    void testMalformedRemoteAddress() {
        assertThrows(MalformedURLException.class, () ->
                AgentClient.getClient(new ChromeOptions(), new ReportSettings("CI - Java", null)));
    }

    @Test
    @DisplayName("Unknown hostname")
    @EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = REGEX_NOT_EMPTY)
    void testUnknownRemoteAddress() {
        assertThrows(AgentConnectException.class, () -> {
            AgentClient.getClient(new URL("http://no-such-host"), new ChromeOptions(),
                    new ReportSettings("CI - Java", null));
        });
    }

    @Test
    @DisplayName("Invalid hostname")
    @EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = REGEX_NOT_EMPTY)
    void testInvalidRemoteAddress() {
        assertThrows(AgentConnectException.class, () ->
                AgentClient.getClient(new URL("http://localhost:0"), new ChromeOptions(),
                        new ReportSettings("CI - Java", null)));
    }

    @Test
    @DisplayName("Invalid non-empty Token")
    @DisabledIfEnvironmentVariable(named = "TP_AGENT_URL", matches = REGEX_NOT_EMPTY)
    void testInvalidToken() {
        assertThrows(InvalidTokenException.class, () -> {
            try (AgentClient agentClient = AgentClient.getClient(INVALID_TOKEN, new ChromeOptions(),
                    new ReportSettings("CI - Java", null))) {
                assertNull(agentClient);
            }
        });
    }

    @Test
    @DisplayName("Default remote address")
    @EnabledIfEnvironmentVariable(named = "TP_DEV_TOKEN", matches = REGEX_NOT_EMPTY)
    @DisabledIfEnvironmentVariable(named = "TP_AGENT_URL", matches = REGEX_NOT_EMPTY)
    void testDefaultRemoteAddress() throws
            InvalidTokenException,
            AgentConnectException,
            MalformedURLException,
            ObsoleteVersionException {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        AgentClient.getClient(options, new ReportSettings("CI - Java", null));
        AgentClient.cleanup();
    }
}
