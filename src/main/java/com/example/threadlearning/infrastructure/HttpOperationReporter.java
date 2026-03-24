package com.example.threadlearning.infrastructure;

import com.example.threadlearning.application.dto.OperationExecutionResult;
import com.example.threadlearning.application.port.OperationReporter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpOperationReporter implements OperationReporter {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String endpointUrl;

    public HttpOperationReporter(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Override
    public void report(OperationExecutionResult result) {
        String body = String.format(
                "{\"memberId\":\"%s\",\"type\":\"%s\",\"amount\":%d,\"success\":%b}",
                result.memberId(), result.operationType(), result.amount(), result.success()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // System.out.println("HTTP " + response.statusCode() + " | " + response.body().substring(0, 100));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to report operation", e);
        }
    }
}
