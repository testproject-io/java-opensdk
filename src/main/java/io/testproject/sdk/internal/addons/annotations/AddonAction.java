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

import io.testproject.sdk.internal.addons.Platform;

import java.lang.annotation.*;

/**
 * Adds information to a TestProject action regarding what platforms it can run on and how it is displayed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface AddonAction {
    /**
     * List of platforms this action supports.
     * @return List of platforms.
     */
    Platform[] platforms();
    /**
     * Alternative action name.
     * @return Action name.
     */
    String name() default "";

    /**
     * Short summary explaining what the action does.
     * @return Action summary.
     */
    String summary() default "";

    /**
     * The full description of the action. This is how the action step will be shown in the logs.
     * You can enhance this by adding step parameters or step element marked with double-brackets (e.g. {{element}}).
     * @return Action description.
     */
    String description() default "";
}
