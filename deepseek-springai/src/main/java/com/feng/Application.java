package com.feng;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * spring boot启动类
 *
 * @since 2025/4/1
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     *  CommandLineRunner 实现类，用于启动聊天客户端
     *
     * @param builder spring boot 使用默认的Builder创建聊天客户端
     * @return
     */
    @Bean
    public CommandLineRunner commandLineRunner(ChatClient.Builder builder) {
        return args -> {
            System.out.println("Starting chat client...");
            var chatClient = builder.build();
            String content = chatClient.prompt()
                    .user("how many r's in the word strawberry?")
                    .call()
                    .content();
            System.out.println(content);
        };
    }
}
