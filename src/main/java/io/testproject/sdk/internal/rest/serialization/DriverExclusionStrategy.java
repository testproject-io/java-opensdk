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

package io.testproject.sdk.internal.rest.serialization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.openqa.selenium.WebDriver;

/**
 * This exclusion strategy makes sure to skip serialization of any classes implementing the WebDriver interface.
 * Since this object is redundant when reporting command parameters and doesn’t pass serialization - we exclude it.
 * See https://github.com/google/gson/issues/1540 for more details on the serialization problem
 */
public class DriverExclusionStrategy implements ExclusionStrategy {


    /**
     * Determines whether the class implements a {@link WebDriver} interface and should be ignored.
     *
     * @param clazz the class object that is under test
     * @return true if the class should be ignored; otherwise false
     */
    @Override
    public boolean shouldSkipClass(final Class<?> clazz) {
        return WebDriver.class.isAssignableFrom(clazz);
    }

    /**
     * Determined whether a filed should be skipped.
     * It has no effect in this exclusion strategy as there is no need for it.
     * Note: It is not used / implemented in this strategy as it is redundant.
     *
     * @param f the field object that is under test
     * @return true if the field should be ignored; otherwise false
     */
    @Override
    public boolean shouldSkipField(final FieldAttributes f) {
        return false;
    }
}
