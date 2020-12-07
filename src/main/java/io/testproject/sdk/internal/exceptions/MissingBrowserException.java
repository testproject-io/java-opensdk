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
 * Exception object thrown when requested browser not installed on machine.
 */
public class MissingBrowserException extends Exception {

    /**
     * Creates a new instance of the class with a default message.
     */
    public MissingBrowserException() {
        this("Requested browser not installed");
    }

    /**
     * Creates a new instance of the class with a provided message.
     *
     * @param message message to be set
     */
    public MissingBrowserException(final String message) {
        super(message);
    }

    /**
     * Creates a new instance of the class with a provided message and cause.
     *
     * @param message message to be set
     * @param cause another exception that caused this one
     */
    public MissingBrowserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
