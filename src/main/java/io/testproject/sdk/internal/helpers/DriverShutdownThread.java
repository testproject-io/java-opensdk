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

import io.testproject.sdk.drivers.ReportingDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread used to handle a graceful shutdown and driver closure.
 */
public class DriverShutdownThread extends Thread {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DriverShutdownThread.class);

    /**
     * Driver to quit.
     */
    private final ReportingDriver driver;

    /**
     * Initializes a new thread.
     * @param driver Driver to close.
     */
    public DriverShutdownThread(final ReportingDriver driver) {
        super();
        this.driver = driver;
    }

    /**
     * Makes sure to close the driver.
     */
    @Override
    public void run() {
        LOG.info("Closing driver gracefully...");
        this.driver.stop();
    }
}
