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

import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility object to store executed command ans it's response.
 */
class StashedCommand {
    /**
     * Driver command.
     */
    private final Command command;

    /**
     * Command response.
     */
    private final Response response;

    /**
     * Getter for {@link #command} field.
     *
     * @return value of {@link #command} field
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Getter for {@link #response} field.
     *
     * @return value of {@link #response} field
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Initializes a new instance of the class by cloning provided command and response.
     * @param command Command object.
     * @param response Command response object.
     */
    StashedCommand(final Command command, final Response response) {
        Map<String, Object> clonedParameters = new HashMap<>();
        command.getParameters().forEach(clonedParameters::put);

        this.command = new Command(command.getSessionId(), command.getName(), clonedParameters);
        this.response = new Response(new SessionId(response.getSessionId()));
        this.response.setState(response.getState());
        this.response.setValue(response.getValue());
    }
}
