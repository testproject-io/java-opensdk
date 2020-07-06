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

import io.testproject.sdk.internal.rest.AgentClient;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Field;

/**
 * Helper class to restore drivers and their properties.
 */
public final class DriverHelper {

    /**
     * Constant for the private field <em>capabilities</em> name in
     * {@link org.openqa.selenium.remote.RemoteWebDriver RemoteWebDriver}.
     */
    static final String FIELD_CAPABILITIES = "capabilities";

    /**
     * Constant for the private field <em>responseCodec</em> name in
     * {@link org.openqa.selenium.remote.HttpCommandExecutor HttpCommandExecutor}.
     */
    static final String FIELD_RESPONSE_CODEC = "responseCodec";

    /**
     * Constant for the private field <em>commandCodec</em> name in
     * {@link org.openqa.selenium.remote.HttpCommandExecutor HttpCommandExecutor}.
     */
    static final String FIELD_COMMAND_CODEC = "commandCodec";

    /**
     * Private default constructor to prevent instance initialization of this utility class.
     */
    private DriverHelper() {
    }

    /**
     * Gets private field from the specified class using reflection.
     *
     * @param clazz     Class to reflect
     * @param fieldName Name of the field to reflect
     * @return field reflected
     */
    private static Field getPrivateField(final Class<?> clazz, final String fieldName) {
        Class<?> superclass = clazz.getSuperclass();
        Throwable recentException = null;
        while (superclass != Object.class) {
            try {
                return superclass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                recentException = e;
            }
            superclass = superclass.getSuperclass();
        }
        throw new WebDriverException(recentException);
    }

    /**
     * Sets a new value in a field of provided object, using reflection.
     *
     * @param object    Object to which the field belongs
     * @param fieldName Field name, to set the value for
     * @param value     the value to set
     */
    static void setPrivateFieldValue(final Object object, final String fieldName, final Object value) {
        try {
            Field field = getPrivateField(object.getClass(), fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new WebDriverException(e);
        }
    }

    /**
     * Sets capabilities in a driver instance when reconnecting it to an existing session.
     *
     * @param driver       a driver to set the capabilities in
     * @param capabilities capabilities to be set
     */
    public static void setCapabilities(final WebDriver driver, final Capabilities capabilities) {
        try {
            Field capabilitiesField = RemoteWebDriver.class.getDeclaredField(FIELD_CAPABILITIES);
            capabilitiesField.setAccessible(true);
            capabilitiesField.set(driver, capabilities);
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }

    /**
     * Initializes an executor for future commands execution.
     *
     * @param agentClient an instance of an Agent client to provide the remote address and codecs info
     * @param appium      indicates whether an Appium executor is requires for Appium driver
     * @return a new instance of the HttpCommandExecutor with all configuration set
     */
    public static HttpCommandExecutor getHttpCommandExecutor(final AgentClient agentClient, final boolean appium) {
        if (appium) {
            return new CustomAppiumCommandExecutor(agentClient,
                    agentClient.getSession().getRemoteAddress());
        } else {
            return new CustomHttpCommandExecutor(agentClient,
                    agentClient.getSession().getRemoteAddress());
        }
    }
}
