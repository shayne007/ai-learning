package com.feng.deepseek.ds;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * HTTP client implementation for DeepSeek API interactions.
 * Handles authentication, request formatting, and response parsing for chat completions.
 *
 * @since 2025/4/1
 */
public class DeepSeekClient {
    static final String HTTPS_API_OPENAI_COM = "https://api.openai.com";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final String apiKey;
    private final String apiUrl;
    private final ObjectMapper objectMapper;

    public DeepSeekClient(DeepSeekModels.Config config) {
        this.apiKey = config.apiKey();
        this.apiUrl = config.baseUrl();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(DEFAULT_TIMEOUT)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public DeepSeekModels.ChatResponse chat(String model, String prompt) throws DeepSeekException {
        var request = new DeepSeekModels.ChatRequest(model, List.of(new DeepSeekModels.Message("user", prompt)));

        try {
            var httpRequest = HttpRequest.newBuilder().uri(URI.create(apiUrl + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(DEFAULT_TIMEOUT)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request)))
                    .build();
            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new DeepSeekException("Failed to get response from API: " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), DeepSeekModels.ChatResponse.class);
        } catch (DeepSeekException | IOException | InterruptedException e) {
            throw new DeepSeekException("Failed to serialize request", e);
        }
    }
}
