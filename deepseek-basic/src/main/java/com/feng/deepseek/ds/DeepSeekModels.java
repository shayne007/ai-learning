package com.feng.deepseek.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * TODO
 *
 * @since 2025/4/1
 */
public class DeepSeekModels {

    public record Config(String apiKey, String baseUrl) {
        public Config {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("API key cannot be null or empty.");
            }
            baseUrl = baseUrl == null || baseUrl.isBlank() ? DeepSeekClient.HTTPS_API_OPENAI_COM : baseUrl;
        }
    }

    public record Message(String role, String content) {
    }

    public record ChatRequest(String model, List<Message> message) {
    }

    public record ChatResponse(List<Choice> choices, Usage usage) {
    }

    public record Choice(@JsonProperty("message") Message message) {
    }

    public record Usage(@JsonProperty("prompt_tokens") int promptTokens,
                        @JsonProperty("completion_tokens") int completionTokens,
                        @JsonProperty("total_tokens") int totalTokens) {
    }
}
