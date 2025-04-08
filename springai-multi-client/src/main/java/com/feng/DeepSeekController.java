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
public class DeepSeekController {
    private final ChatClient chatClient;

    public DeepSeekController(@Qualifier("openaiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/deepseek/chat")
    public String chat() {
        return chatClient.prompt()
                .user("how many r's in the word strawberry?")
                .call()
                .content();
    }

    @GetMapping("/deepseek/chat-stream")
    public Flux<String> chatStream() {
        return chatClient.prompt()
                .user("how many r's in the word strawberry?")
                .stream()
                .content();

    }

}
