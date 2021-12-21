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

package io.testproject.sdk.drivers;

import io.testproject.sdk.internal.addons.AddonsHelper;
import org.openqa.selenium.WebDriver;

/**
 * Extends the ReportingDriver with support for addons execution.
 * @param <D> Driver type that actions must support.
 */
public interface ActionRunner<D extends WebDriver> extends ReportingDriver {
    /**
     * Provides access to the addons functionality.
     *
     * @return {@link io.testproject.sdk.internal.addons.AddonsHelper} instance.
     */
    default AddonsHelper<D> addons() {
        return new AddonsHelper<>((D) this, getReportingCommandExecutor().getAgentClient());
    }
}
