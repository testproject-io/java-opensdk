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

package io.testproject.sdk.internal.helpers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.openqa.selenium.remote.ErrorCodes.SUCCESS;

/**
 * Utility class to handle redaction of values input into secured elements.
 */
public final class RedactHelper {

    /**
     * Private default constructor to prevent instance initialization of this utility class.
     */
    private RedactHelper() {
    }

    /**
     * Redacts sensitive data from commands reports.
     * Redacts only if sent to a secured element.
     *
     * @param executor Executor instance that executed the command
     * @param command  Command sent to the Driver
     * @return Redacted command or the original one
     */
    static Command redactCommand(final ReportingCommandsExecutor executor, final Command command) {
        // Redaction should be performed only for these commands
        if (command.getName().equals(DriverCommand.SEND_KEYS_TO_ELEMENT)
                || command.getName().equals(DriverCommand.SEND_KEYS_TO_ACTIVE_ELEMENT)) {

            String elementId = command.getParameters().get("id").toString();
            if (!isRedactRequired(executor, command, elementId)) {
                return command;
            }

            // Clone Parameters
            Map<String, Object> clonedParameters = new HashMap<>();
            command.getParameters().forEach(clonedParameters::put);
            clonedParameters.put("value", "***");

            // Clone the command with redacted input parameter
            return new Command(command.getSessionId(), command.getName(), clonedParameters);
        }

        // Return original
        return command;
    }

    /**
     * Checks whether redaction is required.
     *
     * @param executor  Executor instance that executed the command
     * @param command   Command sent to the Driver
     * @param elementId The ID of the element that the command was executed on.
     * @return True if it is required, otherwise False;
     */
    private static boolean isRedactRequired(final ReportingCommandsExecutor executor,
                                            final Command command, final String elementId) {
        Capabilities capabilities = executor.getAgentClient().getSession().getCapabilities();
        String platformName = capabilities.getCapability(CapabilityType.PLATFORM_NAME).toString();

        // Check if element is a mobile password element
        if (platformName.equalsIgnoreCase(Platform.ANDROID.name())) {
            // Making sure it's not Mobile Web
            if (StringUtils.isEmpty(capabilities.getCapability(CapabilityType.BROWSER_NAME))) {
                return isAndroidPasswordElement(executor, command.getSessionId(), elementId);
            }
        }

        // HTML & iOS work with attribute, check if attribute 'type' == 'password'
        return isSecuredElement(executor, command.getSessionId(), elementId);

    }

    /**
     * Checks whether the element command invoked on, is a password element.
     * For example: android.widget.TextView with password attribute 'true'
     *
     * @param executor  Executor instance that executed the command
     * @param sessionId Session ID where the command was executed
     * @param elementId The ID of the element that the command was executed on.
     * @return True if the element is a password element, otherwise False.
     */
    private static boolean isAndroidPasswordElement(final ReportingCommandsExecutor executor,
                                                    final SessionId sessionId,
                                                    final String elementId) {
        Map<String, Object> getAttributeParams = new HashMap<>();
        getAttributeParams.put("id", elementId);
        getAttributeParams.put("name", "password");
        Response response = executor.execute(new Command(sessionId,
                DriverCommand.GET_ELEMENT_ATTRIBUTE, getAttributeParams), true);
        return response.getStatus() == SUCCESS
                && Boolean.parseBoolean(Objects.requireNonNullElse(response.getValue(), "").toString());
    }

    /**
     * Checks whether the element command invoked on, is a secure element.
     * For example:
     * HTML input with 'type' attribute set to 'password'
     * With XCUITest, on iOS an element type of 'XCUIElementTypeSecureTextField'
     *
     * @param executor  Executor instance that executed the command
     * @param sessionId Session ID where the command was executed
     * @param elementId The ID of the element that the command was executed on.
     * @return True if the element is a secure element, otherwise False.
     */
    private static boolean isSecuredElement(final ReportingCommandsExecutor executor,
                                            final SessionId sessionId,
                                            final String elementId) {
        Map<String, Object> getAttributeParams = new HashMap<>();
        getAttributeParams.put("id", elementId);
        getAttributeParams.put("name", "type");
        Response response = executor.execute(new Command(sessionId,
                DriverCommand.GET_ELEMENT_ATTRIBUTE, getAttributeParams), true);
        String inputType = response.getStatus() == SUCCESS
                ? Objects.requireNonNullElse(response.getValue(), "").toString() : "";

        return inputType.equals("password") || inputType.equals("XCUIElementTypeSecureTextField");
    }
}
