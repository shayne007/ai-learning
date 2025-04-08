package com.feng;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * TODO
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
    public String chat() {
        System.out.println("start chatting with Ollama");
        return chatClient.prompt()
                .user("how many r's in the word strawberry?")
                .call()
                .content();
    }

    @GetMapping("/ollama/chat-stream")
    public Flux<String> chatStream() {
        System.out.println("start streaming chatting with Ollama");
        return chatClient.prompt()
                .user("how many r's in the word strawberry?")
                .stream()
                .content();

    }
}
