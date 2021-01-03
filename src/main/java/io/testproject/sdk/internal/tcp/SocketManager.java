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

/**
 * Extended Web driver for Desktop Browsers.
 */

package io.testproject.sdk.internal.tcp;

import io.testproject.sdk.internal.exceptions.AgentConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Manages the development TCP socket connection.
 */
public final class SocketManager {

    /**
     * Socket timeout in milliseconds.
     */
    private static final int TIMEOUT_MILLISECONDS = 5 * 1000;

    /**
     * SocketManager singleton instance.
     */
    private static SocketManager instance;

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SocketManager.class);

    /**
     * Holds an instance of a TCP socket connection between the SDK and the Agent.
     */
    private Socket socket;

    /**
     * Private constructor to prevent creating more than one instance.
     */
    private SocketManager() {
    }

    /**
     * Static method to obtain a singleton instance of the class.
     *
     * @return SocketManager instance.
     */
    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }

        return instance;
    }

    /**
     * Closes the TCP socket connection to the Agent.
     */
    public void closeSocket() {
        if (isOpen()) {
            LOG.debug("Disconnecting TCP development socket...");
            try {
                socket.close();
                socket = null;
                LOG.debug("Development socket closed");
            } catch (IOException e) {
                LOG.error("Failed closing development socket connected to the Agent", e);
            }
        }
    }

    /**
     * Opens a TCP socket connection to the Agent using provided host and port.
     * Does effectively nothing if already connected.
     *
     * @param host Host to connect.
     * @param port Port to connect.
     * @throws AgentConnectException When connection fails.
     */
    public void openSocket(final String host, final int port) throws AgentConnectException {
        if (socket != null && socket.isConnected()) {
            LOG.debug("Development socket is already connected.");
            return;
        }

        try {
            LOG.trace("Connecting to Agent socket: {}:{}", host, port);
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), TIMEOUT_MILLISECONDS);
            LOG.debug("Development socket connected");
        } catch (IOException e) {
            LOG.error("Failed connecting to Agent socket at {}:{}", host, port, e);
            throw new AgentConnectException("Failed connecting to Agent socket", e);
        }
    }

    /**
     * Checks whether the socket is open or closed.
     * @return True if open, otherwise False.
     */
    public boolean isOpen() {
        return socket != null && socket.isConnected();
    }
}
