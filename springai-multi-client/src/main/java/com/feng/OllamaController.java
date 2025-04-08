package com.feng;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

/**
 * Controller for handling chat interactions with Ollama AI model.
 * Provides endpoints for both synchronous and streaming chat responses.
 *
 * @since 2025/4/1
 */
@RestController
public class OllamaController {
    private final ChatClient chatClient;

    public OllamaController(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ollama/chat")
    public String chat(@RequestParam String message) {
        System.out.println("start chatting with Ollama");
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/ollama/chat-stream")
    public Flux<String> chatStream(@RequestParam String message) {
        System.out.println("start streaming chatting with Ollama");
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
