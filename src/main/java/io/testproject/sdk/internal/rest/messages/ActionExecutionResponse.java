/*
 * Copyright 2020 TestProject LTD. and/or its affiliates
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
/**
 * Addons related classes.
 */

package io.testproject.sdk.internal.rest.messages;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

/**
 * Agent response to an Action proxy execution request.
 */
public class ActionExecutionResponse {

    /**
     * Execution result (Failed / Passed).
     */
    private ExecutionResultType resultType;

    /**
     * Execution result message.
     */
    private String message;

    /**
     * Addon fields (inputs / outputs).
     */
    private List<ResultField> fields;

    /**
     * Getter for {@link #resultType} field.
     *
     * @return value of {@link #resultType} field
     */
    public ExecutionResultType getResultType() {
        return resultType;
    }

    /**
     * Getter for {@link #message} field.
     *
     * @return value of {@link #message} field
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for {@link #fields} field.
     *
     * @return value of {@link #fields} field
     */
    public List<ResultField> getFields() {
        return fields;
    }

    /**
     * Action execution result.
     */
    public enum ExecutionResultType {
        /**
         * Passed result.
         */
        Passed,

        /**
         * Failed result.
         */
        Failed,

        /**
         * Skipped result.
         */
        Skipped
    }

    /**
     * Action proxy execution result field.
     * Returns as part of the Agent response with potentially updated output fields.
     */
    public static class ResultField {

        /**
         * Field name.
         */
        @SuppressFBWarnings("UWF_UNWRITTEN_FIELD") // Gson writes the value during deserialization
        private String name;

        /**
         * Field value.
         */
        @SuppressFBWarnings("UWF_UNWRITTEN_FIELD") // Gson writes the value during deserialization
        private Object value;

        /**
         * Output / Input indicator.
         */
        private boolean output;

        /**
         * Getter for {@link #name} field.
         *
         * @return value of {@link #name} field
         */
        public String getName() {
            return name;
        }

        /**
         * Getter for {@link #value} field.
         *
         * @return value of {@link #value} field
         */
        public Object getValue() {
            return value;
        }

        /**
         * Getter for {@link #output} field.
         *
         * @return value of {@link #output} field
         */
        public boolean isOutput() {
            return output;
        }
    }
}
