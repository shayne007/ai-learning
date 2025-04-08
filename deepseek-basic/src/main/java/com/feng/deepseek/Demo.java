package com.feng.deepseek;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Demonstration class for basic DeepSeek API integration.
 * Shows how to make direct HTTP requests to DeepSeek's chat completion endpoint.
 *
 * @since 2025/4/1
 */
public class Demo {
    private static final String API_KEY = System.getenv("DEEPSEEK_API_KEY");
    private static final String API_BASE_URL = "https://api.deepseek.com";

    public static void main(String[] args) throws IOException, InterruptedException {
        var body = """
                {
                    "model": "deepseek-chat",
                    "messages": [{
                    "role": "user",
                    "content": "how to start a business as a 35 years old java developer?"
                    }]
                }]   
                """;
        var request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/chat/completions")).
                header("Authorization", "Bearer " + API_KEY).
                header("Content-Type", "application/json").
                POST(HttpRequest.BodyPublishers.ofString(body)).build();
        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        Object json = objectMapper.readValue(response.body(), Object.class);
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        System.out.println(prettyJson);
    }
}
