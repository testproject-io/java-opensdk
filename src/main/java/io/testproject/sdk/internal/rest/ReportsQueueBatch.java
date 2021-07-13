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

import io.testproject.sdk.internal.exceptions.FailedReportException;
import io.testproject.sdk.internal.rest.messages.Report;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class ReportsQueueBatch extends ReportsQueue {
    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ReportsQueueBatch.class);

    /**
     * remote url to send the reports to.
     */
    private final URL remoteAddress;

    /**
     * The default batch report size is a maximum of 10 reports.
     */
    private static final int MAX_REPORTS_BATCH_SIZE = 10;

    /**
     * Constant for environment variable name that may store the max batch size.
     */
    private static final String TP_MAX_BATCH_SIZE_VARIABLE_NAME = "TP_MAX_REPORTS_BATCH_SIZE";

    /**
     * Class member to store actual max batch size.
     */
    private final int maxBatchSize;

    /**
     * Initializes a new instance of the class.
     *
     * @param httpClient HTTP client ot use for communicating with the Agent.
     * @param sessionId  Driver session ID.
     * @param remoteAddress Reports remote address.
     */
    ReportsQueueBatch(final CloseableHttpClient httpClient, final String sessionId, final URL remoteAddress) {
        super(httpClient, sessionId);
        this.remoteAddress = remoteAddress;

        // Try to get maximum report batch size from env variable.
        int maxBatchSizeValueToSet;
        try {
            String tpMaxBatchSizeEnvVarValue = System.getenv(TP_MAX_BATCH_SIZE_VARIABLE_NAME);
            maxBatchSizeValueToSet = (tpMaxBatchSizeEnvVarValue != null)
                    ? Integer.parseInt(tpMaxBatchSizeEnvVarValue) : MAX_REPORTS_BATCH_SIZE;
        } catch (Exception e) {
            LOG.info("Failed to retrieve the value of environment variable {}. "
                            + "Maximum reports batch size is set to the default value: {}.",
                    TP_MAX_BATCH_SIZE_VARIABLE_NAME, MAX_REPORTS_BATCH_SIZE);
            maxBatchSizeValueToSet = MAX_REPORTS_BATCH_SIZE;
        }
        this.maxBatchSize = maxBatchSizeValueToSet;
    }

    /**
     * From version 3.1.0 -> send reports in batches.
     * Collect reports from reports queue and build reports batch.
     * Send the reports batch to the agent.
     * @throws InterruptedException in case reports queue was interrupted
     * @throws FailedReportException in case of 4 failures to send reports to the agent
     */
    @Override
    void handleReport() throws InterruptedException, FailedReportException {
        // Linked list to store the reports batch before sending them.
        // Those reports will be extract from the queue.
        List<Report> batchReports = new LinkedList<>();

        // Extract and remove up to {this.maxBatchSize} items or till queue is empty from queue - without blocking it.
        while (!getQueue().isEmpty() && batchReports.size() < this.maxBatchSize) {
            // Get the first item in the queue without blocking it.
            QueueItem item = getQueue().poll();

            if (null != item && item.getReport() != null) {
                batchReports.add(item.getReport());
            }
        }

        if (batchReports.isEmpty()) {
            return;
        }

        // Create json from reports list
        String json = null;
        try {
            json = this.GSON.toJson(batchReports);
        } catch (Exception e) {
            LOG.error("Failed to create json from list: [{}]", batchReports, e);
            return;
        }

        // Parse batch report to json string
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);

        // Create httpPost and execute
        HttpPost httpPost = new HttpPost(this.remoteAddress + AgentClient.Routes.REPORT_BATCH);
        httpPost.setEntity(entity);

        int reportAttemptsCount;
        CloseableHttpResponse response = null;
        // Send the report to the agent.
        // In case of failure - make 3 more attempts.
        for (reportAttemptsCount = MAX_REPORT_FAILURE_ATTEMPTS; reportAttemptsCount > 0; reportAttemptsCount--) {
            try {
                response = this.getHttpClient().execute(httpPost);
            } catch (IOException e) {
                LOG.error("Failed to submit {} reports.", batchReports.size(), e);
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
                LOG.warn("Agent responded with an unexpected status {} to send {} reports",
                        response.getStatusLine().getStatusCode(), batchReports.size());
                LOG.info("Attempt to send {} reports again to the Agent. {} more attempts are left.",
                        batchReports.size(), reportAttemptsCount - 1);
            }
        }

        // In case all attempts to send the report are failed.
        if (reportAttemptsCount == 0) {
            LOG.error("All {} attempts to send {} reports have failed",
                    MAX_REPORT_FAILURE_ATTEMPTS, batchReports.size());
            throw new FailedReportException("All " + MAX_REPORT_FAILURE_ATTEMPTS
                    + " attempts to send " + batchReports.size() + " reports have failed");
        }
    }
}
