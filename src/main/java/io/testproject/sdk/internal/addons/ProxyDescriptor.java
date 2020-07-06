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

package io.testproject.sdk.internal.addons;

import org.openqa.selenium.By;

import java.util.HashMap;

/**
 * Describes an Addon and an Action to be executed via the Agent.
 */
public class ProxyDescriptor {

    /**
     * Addon GUID.
     */
    private final String guid;

    /**
     * Action class name.
     */
    private final String className;

    /**
     * target element (optional) locator.
     */
    private By by;

    /**
     * Action parameters (fields).
     */
    private HashMap<String, Object> parameters;

    /**
     * Initializes a new Proxy Descriptor.
     *
     * @param guid      Addon GUID.
     * @param className Action class name.
     */
    public ProxyDescriptor(final String guid, final String className) {
        this.guid = guid;
        this.className = className;
    }

    /**
     * Getter for {@link #guid} field.
     *
     * @return value of {@link #guid} field
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Getter for {@link #className} field.
     *
     * @return value of {@link #className} field
     */
    public String getClassName() {
        return className;
    }

    /**
     * Getter for {@link #by} field.
     *
     * @return value of {@link #by} field
     */
    public By getBy() {
        return by;
    }

    /**
     * Setter for <em>by</em> field.
     *
     * @param by By (element locator) to set.
     */
    public void setBy(final By by) {
        this.by = by;
    }

    /**
     * Getter for {@link #parameters} field.
     *
     * @return value of {@link #parameters} field
     */
    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Setter for <em>parameters</em> field.
     *
     * @param parameters Parameters to set.
     */
    public void setParameters(final HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }
}
