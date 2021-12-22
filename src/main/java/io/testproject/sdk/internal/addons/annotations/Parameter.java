/*
 * Copyright (c) 2021 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.internal.addons.annotations;

import io.testproject.sdk.internal.addons.ParameterDirection;

import java.lang.annotation.*;

/**
 * Marks a field in an action as a parameter that can be specified at runtime.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Parameter {
    /**
     * A short description of the parameter, that will be displayed in the TestProject Test.
     * @return Parameter description.
     */
    String description() default "";

    /**
     * Indicates whether the parameter is an input or output parameter.
     * @return If parameter is input/output.
     */
    ParameterDirection direction() default ParameterDirection.INPUT;

    /**
     * Parameter's default value. This is optional and should be reinforced in code as well.
     * @return Parameter default value.
     */
    String defaultValue() default "";

    /**
     * Indicates this parameter is optional.
     * @return If parameter is optional.
     */
    boolean optional() default false;
}
