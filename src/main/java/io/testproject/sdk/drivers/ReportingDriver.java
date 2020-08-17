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

package io.testproject.sdk.drivers;

import io.testproject.sdk.internal.addons.AddonsHelper;
import io.testproject.sdk.internal.helpers.ReportingCommandsExecutor;
import io.testproject.sdk.internal.reporting.Reporter;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

/**
 * Interface to reference drivers reporting commands execution.
 */
public interface ReportingDriver {

    /**
     * Extension method to get an instance of the reporter initialized by the driver.
     * @return Reporter instance.
     */
    Reporter report();

    /**
     * Takes a screenshot using the driver.
     * @return Screenshot taken (PNG) as base64 string.
     */
    default String getScreenshot() {
        RemoteWebDriver rwd = (RemoteWebDriver) this;
        Command command = new Command(rwd.getSessionId(), DriverCommand.SCREENSHOT);
        Response response = getReportingCommandExecutor().execute(command, true);
        return response.getValue().toString();
    }

    /**
     * Returns driver's command executor.
     * @return An instance of {@link ReportingCommandsExecutor} used by the driver.
     */
    default ReportingCommandsExecutor getReportingCommandExecutor() {
        RemoteWebDriver rwd = (RemoteWebDriver) this;
        return (ReportingCommandsExecutor) rwd.getCommandExecutor();
    }

    /**
     * Stops the driver and perform necessary cleanup.
     */
    void stop();

    /**
     * Provides access to the addons functionality.
     *
     * @return {@link io.testproject.sdk.internal.addons.AddonsHelper} instance.
     */
    default AddonsHelper addons() {
        return new AddonsHelper(getReportingCommandExecutor().getAgentClient());
    }
}
