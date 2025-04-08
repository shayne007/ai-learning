package com.feng;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * TODO
 *
 * @since 2025/4/1
 */
@RestController
public class DeepSeekStreamController {
    private final OpenAiChatModel chatModel;

    @Autowired
    public DeepSeekStreamController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @GetMapping("/deepseek/chat-stream2")
    public SseEmitter chatStream2(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(60_000L);
        Prompt prompt = new Prompt(new UserMessage(message));
        chatModel.stream(prompt).subscribe(chatResponse -> {
                    try {
                        String content = chatResponse.getResult().getOutput().getText();
                        System.out.print(content);
                        // 发送 SSE 事件
                        emitter.send(SseEmitter.event()
                                .data(content)
                                .id(String.valueOf(System.currentTimeMillis()))
                                .build());
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete);
        // 处理客户端断开连接
        emitter.onCompletion(() -> {
            // 可在此处释放资源
            System.out.println("SSE connection completed");
        });
        emitter.onTimeout(() -> {
            emitter.complete();
            System.out.println("SSE connection timed out");
        });
        return emitter;
    }
}
