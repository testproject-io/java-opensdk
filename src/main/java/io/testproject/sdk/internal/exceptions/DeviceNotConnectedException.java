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

package io.testproject.sdk.internal.exceptions;

/**
 * Exception object thrown when requested device not found.
 */
public class DeviceNotConnectedException extends Exception {
    /**
     * Creates a new instance of the class with a default message.
     */
    public DeviceNotConnectedException() {
        this("Requested device is not connected");
    }

    /**
     * Creates a new instance of the class with a provided message.
     *
     * @param message message to be set
     */
    public DeviceNotConnectedException(final String message) {
        super(message);
    }

    /**
     * Creates a new instance of the class with a provided message and cause.
     *
     * @param message message to be set
     * @param cause another exception that caused this one
     */
    public DeviceNotConnectedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
