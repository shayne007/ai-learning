package com.feng;

import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;

/**
 * Spring Boot application that demonstrates MCP client integration.
 * Configures and manages MCP client connections and interactions.
 *
 * @since 2025/4/7
 */
@SpringBootApplication
public class DemoClientApp {
    
    public static void main(String[] args) {
        SpringApplication.run(DemoClientApp.class, args);
    }
    
    @Value("${ai.user.input:What is Spring AI?}")
    private String userInput;

    @Bean
    public CommandLineRunner commandLineRunner(ChatClient.Builder chatClientBuilder,McpSyncClient mcpClient, ConfigurableApplicationContext context) {
        return args -> {
            SyncMcpToolCallbackProvider tools = new SyncMcpToolCallbackProvider(mcpClient);
            var chatClient = chatClientBuilder
                .defaultTools(tools)
                .build();
            List.of(tools.getToolCallbacks()).stream()
                .forEach(tool -> System.out.println("Registered tool: " + tool.getToolMetadata()));
            System.out.println("Running predefined questions with AI model responses:\n");
      
            // Question 1
            String question1 = "Can you explain the content of the target/spring-ai-mcp-overview.txt file?";
            System.out.println("QUESTION: " + question1);
            System.out.println("ASSISTANT: " + chatClient.prompt(question1).call().content());
      
            // Question 2
            String question2 = "Pleses summarize the content of the target/spring-ai-mcp-overview.txt file and store it a new target/summary.md as Markdown format?";
            System.out.println("\nQUESTION: " + question2);
            System.out.println("ASSISTANT: " +
                chatClient.prompt(question2).call().content());
      
            context.close();
      
          };
    }


  @Bean(destroyMethod = "close")
  public McpSyncClient mcpClient() {

    // based on
    // https://github.com/modelcontextprotocol/servers/tree/main/src/filesystem
    var stdioParams = ServerParameters.builder("npx")
        .args("-y", "@modelcontextprotocol/server-filesystem", "/Users/fengshiyi/Downloads/shayne/learning/LLM/ai-learning/springai-mcp-demo/target")
        .build();

    var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
        .requestTimeout(Duration.ofSeconds(10)).build();

    var init = mcpClient.initialize();

    System.out.println("MCP Initialized: " + init);

    return mcpClient;

  }

}