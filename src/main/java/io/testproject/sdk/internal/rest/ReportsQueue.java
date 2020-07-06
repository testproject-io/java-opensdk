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

package io.testproject.sdk.internal.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.testproject.sdk.internal.rest.messages.Report;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A runnable class to manage reports queue.
 */
public class ReportsQueue implements Runnable {

    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ReportsQueue.class);

    /**
     * An instance of the Google JSON serializer to serialize and deserialize objects.
     */
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Queue to synchronize reports sent to Agent.
     */
    private final ArrayBlockingQueue<QueueItem> queue = new ArrayBlockingQueue<>(1024);

    /**
     * HTTP client to submit reports to the Agent.
     */
    private final CloseableHttpClient httpClient;

    /**
     * Flag to keep running the loop of taking items from the queue.
     */
    private boolean running;

    /**
     * Initializes a new instance of the class.
     * @param httpClient HTTP client ot use for communicating with the Agent.
     */
    public ReportsQueue(final CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Adds a report to the queue.
     * @param request Request to be sent over HTTP.
     * @param report Report that this request contains.
     */
    void submit(final HttpEntityEnclosingRequestBase request, final Report report) {
        this.queue.add(new QueueItem(request, report));
    }

    /**
     * Runnable flow that looks into the queue and waits for new items.
     */
    @Override
    public void run() {
        this.running = true;

        while (this.running || queue.size() > 0) {
            try {
                sendReport(queue.take());
            } catch (InterruptedException e) {
                LOG.error("Reports queue was interrupted");
                break;
            }
        }

        LOG.trace("Reports queue has been stopped.");

        if (!this.queue.isEmpty()) {
            LOG.warn("There are {} unreported items in the queue", this.queue.size());
        }
    }

    /**
     * Submits a report to the Agent via HTTP RESTFul API endpoint.
     * @param item Item retrieved from the queue (report & HTTP request).
     */
    private void sendReport(final QueueItem item) {
        if (item.getRequest() == null && item.getReport() == null) {
            if (this.running) {
                // There nulls are not OK, something went wrong preparing the report/request.
                LOG.error("Empty report and request were submitted to the queue!");
            }

            // These nulls are OK - they were added by stop() method on purpose.
            return;
        }

        CloseableHttpResponse response = null;
        try {
            response = this.httpClient.execute(item.getRequest());
        } catch (IOException e) {
            LOG.error("Failed to submit report: [{}]", item.getReport(), e);
            return;
        } finally {
            // Consume response to release the resources
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        // Handle unsuccessful response
        if (response != null && response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
            LOG.error("Agent responded with an unexpected status {} to report: [{}]",
                    response.getStatusLine().getStatusCode(), item.getReport());
        }
    }

    /**
     * Stops the runnable and the queue processing.
     */
    public void stop() {
        LOG.trace("Raising flag to stop reports queue.");
        this.running = false;

        // Feed the queue with one more (null) object.
        // This is required to to let it proceed with the loop to evaluate the condition (running?) again.
        // Note: Sending null as QueueItem is not possible since ArrayBlockingQueue prohibit null elements.
        this.queue.add(new QueueItem(null, null));
    }

    /**
     * Internal class to keep the HTTP request and contained report together in the queue.
     */
    private static class QueueItem {

        /**
         * HTTP request.
         */
        private final HttpEntityEnclosingRequestBase request;

        /**
         * Report that the request will transmit.
         */
        private final Report report;

        /**
         * Getter for {@link #request} field.
         *
         * @return value of {@link #request} field
         */
        HttpEntityEnclosingRequestBase getRequest() {
            return request;
        }

        /**
         * Getter for {@link #report} field.
         *
         * @return value of {@link #report} field
         */
        Report getReport() {
            return report;
        }

        /**
         * Initializes a new instance of the class.
         * @param request HTTP request to be transmitted to the Agent.
         * @param report Report that the request contains.
         */
        QueueItem(final HttpEntityEnclosingRequestBase request, final Report report) {
            this.request = request;
            this.report = report;
        }
    }
}
