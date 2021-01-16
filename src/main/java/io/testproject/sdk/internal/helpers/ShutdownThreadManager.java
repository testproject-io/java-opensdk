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

package io.testproject.sdk.internal.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;

/**
 * Will be used to manage the driver and AgentClient shutdown threads to ensure
 * the AgentClient will not close before the driver finishes reporting.
 */
public final class ShutdownThreadManager extends Thread {

    /**
     * Instance of the class.
     */
    private static ShutdownThreadManager instance;

    /**
     * Store the AgentClient shutdown thread.
     */
    private Runnable agentClientShutdownThread;

    /**
     * Hash map to store the shutdown threads of all the active drivers.
     */
    private HashMap<Object, Runnable> driverShutdownThreads;

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownThreadManager.class);

    /**
     * Add Agent Client shutdown thread to the manager.
     *
     * @param shutdownThread of the agent client.
     */
    public void addAgentClient(final Runnable shutdownThread) {
        agentClientShutdownThread = shutdownThread;
    }

    /**
     * Remove Agent Client shutdown thread from the manager.
     */
    public void removeAgentClient() {
        agentClientShutdownThread = null;
    }

    /**
     * Add a driver shutdown thread to the manager.
     *
     * @param driver added to the manager.
     * @param driverShutdownThread the shutdown thread.
     */
    public void addDriver(final Object driver, final Runnable driverShutdownThread) {
        driverShutdownThreads.put(driver.hashCode(), driverShutdownThread);
    }

    /**
     * Remote the driver shutdown thread from the manager.
     *
     * @param driver removed from the manager.
     */
    public void removeDriver(final Object driver) {
        driverShutdownThreads.remove(driver.hashCode());
    }


    /**
     * Class constructor will create the tree map and register itself
     * as a shutdown hook.
     */
    private ShutdownThreadManager() {
        driverShutdownThreads = new HashMap<>();
        Runtime.getRuntime().addShutdownHook(this);
    }

    /**
     * Singleton will return the current instance of the class.
     *
     * @return instance of the class.
     */
    public static ShutdownThreadManager getInstance() {
        if (instance == null) {
            instance = new ShutdownThreadManager();
        }
        return instance;
    }

    /**
     * Run each shutdown hook in order starting from the drivers then the Agent Client.
     * Called when the program finishes to ensure drivers finish before
     * the AgentClient.
     */
    @Override
    public void run() {
        Collection<Runnable> runnables = driverShutdownThreads.values();
        try {
            for (Runnable runnable : runnables) {
                runnable.run();
            }
            if (agentClientShutdownThread != null) {
                agentClientShutdownThread.run();
            }
        } catch (Throwable e) {
            LOG.error("Failed running shutdown thread", e);
        }
    }
}
