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
import io.testproject.sdk.internal.exceptions.FailedReportException;
import io.testproject.sdk.internal.rest.messages.Report;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.concurrent.*;

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
    protected static final Gson GSON = new GsonBuilder().create();

    /**
     * Progress report delay in seconds.
     * Print the number of the remaining reports to send every 3 seconds.
     */
    private static final int PROGRESS_REPORT_DELAY = 3;

    /**
     * Maximum attempts to try sending a report to the Agent.
     */
    protected static final int MAX_REPORT_FAILURE_ATTEMPTS = 4;

    /**
     * Queue to synchronize reports sent to Agent.
     * Queue depth defined as 10K - assuming that even with very high latency, the queue won't get full.
     */
    private final LinkedBlockingQueue<QueueItem> queue = new LinkedBlockingQueue<>(1024 * 10);

    /**
     * HTTP client to submit reports to the Agent.
     */
    private final CloseableHttpClient httpClient;

    /**
     * Driver session ID.
     */
    private final String sessionId;

    /**
     * Flag to keep running the loop of taking items from the queue.
     */
    private boolean running;

    /**
     * Future to report remaining reports in queue.
     */
    private Future<?> progressFuture;

    /**
     * A flag that is raised when all attempts submitting a report fail.
     */
    private boolean stopReports = false;

    /**
     * Initializes a new instance of the class.
     *
     * @param httpClient HTTP client ot use for communicating with the Agent.
     * @param sessionId  Driver session ID.
     */
    public ReportsQueue(final CloseableHttpClient httpClient, final String sessionId) {
        this.httpClient = httpClient;
        this.sessionId = sessionId;
    }

    /**
     * Getter method for {@link #queue}.
     * @return the reports queue.
     */
    protected LinkedBlockingQueue<QueueItem> getQueue() {
        return queue;
    }

    /**
     * Getter method for {@link #httpClient}.
     * @return the httpClient.
     */
    protected CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Adds a report to the queue.
     *
     * @param request Request to be sent over HTTP.
     * @param report  Report that this request contains.
     */
    void submit(final HttpEntityEnclosingRequestBase request, final Report report) {
        if (!this.stopReports) {
            this.queue.add(new QueueItem(request, report));
        }
    }

    /**
     * For lower versions than 3.1.0 -> Send standalone reports.
     * @throws InterruptedException in case reports queue was interrupted
     * @throws FailedReportException in case of 4 failures to send reports to the agent
     */
    void handleReport() throws InterruptedException, FailedReportException {
        sendReport(this.queue.take());
    }

    /**
     * Runnable flow that looks into the queue and waits for new items.
     */
    @Override
    public void run() {
        this.running = true;
        while (this.running || !this.queue.isEmpty()) {
            try {
                handleReport();
            } catch (InterruptedException e) {
                LOG.error("Reports queue was interrupted");
                break;
            } catch (FailedReportException e) {
                this.stopReports = true;
                LOG.warn("Reports are disabled due to multiple failed attempts of sending reports to the agent.");
            }
        }

        LOG.trace("Reports queue for session [{}] has been stopped.", sessionId);

        if (!this.queue.isEmpty()) {
            LOG.warn("There are {} unreported items in the queue", this.queue.size());
        }
    }

    /**
     * Submits a report to the Agent via HTTP RESTFul API endpoint.
     * @param item Item retrieved from the queue (report & HTTP request).
     * @throws FailedReportException in case of 4 failures to send reports to the agent
     */
    void sendReport(final QueueItem item) throws FailedReportException {
        if (item.getRequest() == null && item.getReport() == null) {
            if (this.running) {
                // There nulls are not OK, something went wrong preparing the report/request.
                LOG.error("Empty report and request were submitted to the queue!");
            }

            // These nulls are OK - they were added by stop() method on purpose.
            return;
        }

        int reportAttemptsCount;
        CloseableHttpResponse response = null;
        // Send the report to the agent.
        // In case of failure - make 3 more attempts.
        for (reportAttemptsCount = MAX_REPORT_FAILURE_ATTEMPTS; reportAttemptsCount > 0; reportAttemptsCount--) {
            try {
                response = this.getHttpClient().execute(item.getRequest());
            } catch (IOException e) {
                LOG.error("Failed to submit report: [{}]", item.getReport(), e);
            } finally {
                // Consume response to release the resources
                if (response != null) {
                    EntityUtils.consumeQuietly(response.getEntity());
                }
            }

            if (response != null) {
                // If the reports were sent successfully, there is no need to continue to the rest of the code
                // since it's handling unsuccessful response.
                if (Response.Status.Family.familyOf(response.getStatusLine().getStatusCode())
                    == Response.Status.Family.SUCCESSFUL) {
                    return;
                }

                // Handle unsuccessful response
                LOG.warn("Agent responded with an unexpected status {} to report: [{}]",
                        response.getStatusLine().getStatusCode(), item.getReport());
                LOG.info("Failed to send a report to the Agent, {} attempts remaining...", reportAttemptsCount - 1);
            }
        }

        // In case all attempts to send the report are failed.
        if (reportAttemptsCount == 0) {
            LOG.error("All {} attempts to send report {} have failed", MAX_REPORT_FAILURE_ATTEMPTS, item.getReport());
            throw new FailedReportException("All " + MAX_REPORT_FAILURE_ATTEMPTS
                    + " attempts to send report " + item.getReport() + " have failed");
        }
    }

    /**
     * Stops the runnable and the queue processing.
     */
    public void stop() {
        LOG.trace("Raising flag to stop reports queue for session [{}]", sessionId);
        this.running = false;

        // Feed the queue with one more (null) object.
        // This is required to to let it proceed with the loop to evaluate the condition (running?) again.
        // Note: Sending null as QueueItem is not possible since ArrayBlockingQueue prohibit null elements.
        this.queue.add(new QueueItem(null, null));

        // Start a scheduled future when stopping the queue to log to console the remaining items left to be reported.
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        if (progressFuture == null) {
            progressFuture = scheduler.scheduleAtFixedRate(() -> {
                        Thread.currentThread().setName("Queue-Progress-Report");
                        LOG.info("There are [{}] outstanding reports that should be transmitted to the Agent before"
                                + " the process exits.", queue.size());
                        if (queue.isEmpty()) {
                            LOG.trace("Reporting queue is empty, stopping progress report...");
                            progressFuture.cancel(true);
                            scheduler.shutdown();
                        }
                    },
                    0, PROGRESS_REPORT_DELAY, TimeUnit.SECONDS);
        }
    }

    /**
     * Internal class to keep the HTTP request and contained report together in the queue.
     */
    static class QueueItem {

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
         *
         * @param request HTTP request to be transmitted to the Agent.
         * @param report  Report that the request contains.
         */
        QueueItem(final HttpEntityEnclosingRequestBase request, final Report report) {
            this.request = request;
            this.report = report;
        }
    }
}
