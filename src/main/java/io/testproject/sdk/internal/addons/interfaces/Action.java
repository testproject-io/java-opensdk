/*
 * Copyright 2021 TestProject LTD. and/or its affiliates
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

package io.testproject.sdk.internal.addons.interfaces;

import org.openqa.selenium.WebDriver;

/**
 * Represents an addon action that can be uploaded and executed inside a recorder test.
 * @param <D> Driver type supported by this action.
 */
public interface Action<D extends WebDriver> {
    /**
     * Implementation of the action code.
     * @param driver The driver used by the action.
     * @return True if action passed.
     */
    boolean run(D driver);
}
