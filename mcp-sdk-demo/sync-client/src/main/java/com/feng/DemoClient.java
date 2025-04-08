package com.feng;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.ClientMcpTransport;
import io.modelcontextprotocol.spec.McpTransport;

import static io.modelcontextprotocol.spec.McpSchema.*;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 *
 * @since 2025/4/7
 */
public class DemoClient {

  private static final Duration INIT_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(300);

  public static void main(String[] args) {

    try {
      // 1. Create client with extended timeout
      McpSyncClient client = McpClient.sync(getMcpTransport())
          .requestTimeout(REQUEST_TIMEOUT)
          .initializationTimeout(INIT_TIMEOUT)
          .capabilities(getClientCapabilities())
          .sampling(getSamplingHandler())
          .build();

      // 2. Initialize with retry logic
      initializeWithRetry(client, 3);

      // 3. Verify initialization before making calls
      // step2 should be blocked

      // 4. Make API calls
      executeClientOperations(client);

    } catch (Exception e) {
      System.err.println("Error in MCP client:");
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void initializeWithRetry(McpSyncClient client, int maxRetries) {
    for (int i = 0; i < maxRetries; i++) {
      try {
        System.out.println("Initializing client (attempt " + (i + 1) + ")");
        client.initialize();
        System.out.println("Client initialized successfully");
        return;
      } catch (Exception e) {
        if (i == maxRetries - 1) {
          throw e;
        }
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ie) {
        }
      }
    }
  }

  private static void executeClientOperations(McpSyncClient client) {
    try {
      // List tools
      System.out.println("\nListing tools...");
      ListToolsResult tools = client.listTools();
      System.out.println("Available tools: " + tools.tools());

      // Call a tool
      System.out.println("\nCalling 'add' tool...");
      CallToolResult result = client.callTool(new CallToolRequest("add", Map.of("a", 2, "b", 3)));
      System.out.println("Addition result: " + result.content());
      // List resources
      System.out.println("\nListing resources...");
      ListResourcesResult resources = client.listResources();
      System.out.println("Available resources: " + resources.resources());

      // Close client
      System.out.println("\nClosing client...");
      client.closeGracefully();

    } catch (Exception e) {
      System.err.println("Error during client operations:");
      e.printStackTrace();
    }
  }

  @NotNull
  private static ClientMcpTransport getMcpTransport() {
    // npx -y @modelcontextprotocol/server-everything dir
    // 启动MCP服务器
    // 注意：需要安装Node.js环境
//    ServerParameters params = ServerParameters.builder("npx")
//        .args("-y", "@modelcontextprotocol/server-everything", "dir")
//        .build();

    String serverJarPath = "/Users/fengshiyi/Downloads/shayne/learning/LLM/ai-learning/mcp-sdk-demo/sync-server/target/sync-server-0.0.1-SNAPSHOT.jar";

    System.out.println("Starting MCP server from: " + serverJarPath);

    ServerParameters params = ServerParameters.builder("java")
        .args("-jar", serverJarPath)
        .build();

    return new StdioClientTransport(params);
  }

  @NotNull
  private static Function<CreateMessageRequest, CreateMessageResult> getSamplingHandler() {
    return request -> {
      // 调用LLM
//       String response = sendPromptToLLM(request.systemPrompt());
//       return CreateMessageResult.builder().role(Role.ASSISTANT).message(response).build();
      return CreateMessageResult.builder().role(Role.ASSISTANT)
          .message(request.systemPrompt() + "Hello, world!").build();
    };
  }

  private static ClientCapabilities getClientCapabilities() {
    return ClientCapabilities.builder().roots(true).sampling().build();
  }
}
