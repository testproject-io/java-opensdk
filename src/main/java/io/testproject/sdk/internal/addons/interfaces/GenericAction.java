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

import io.testproject.sdk.internal.reporting.Reporter;

/**
 * Represents an action that can be uploaded and executed inside a recorder test and doesn't require a driver to run.
 */
public interface GenericAction {
    /**
     * Implement of the action code.
     * @param reporter Reporter allowing us to report action result.
     * @return True if action passed.
     */
    boolean run(Reporter reporter);
}
