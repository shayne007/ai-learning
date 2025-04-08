package com.feng;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures.SyncPromptSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncResourceSpecification;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.Prompt;
import io.modelcontextprotocol.spec.McpSchema.PromptArgument;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.util.List;

/**
 * TODO
 *
 * @since 2025/4/7
 */
public class DemoServer {

  private static McpSyncServer syncServer;
  private static volatile boolean running = true;

  public static void main(String[] args) {
    try {
      // 1. Initialize server
      StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(
          new ObjectMapper());

      syncServer = McpServer.sync(transportProvider)
          .serverInfo("my-server", "1.0.0")
          .capabilities(getServerCapabilities())
          .build();

      // 2. Register server components
      registerServerComponents();

      // 3. Add shutdown hook
      Runtime.getRuntime().addShutdownHook(new Thread(DemoServer::shutdown));

//      System.out.println("MCP Server started. Press Ctrl+C to stop...");

      // 4. Main server loop
      while (running) {
        try {
          Thread.sleep(1000); // Keep server alive
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    } catch (Exception e) {
      System.err.println("Server error:");
      e.printStackTrace();
    } finally {
      shutdown();
    }
  }

  private static void registerServerComponents() {
    // Register tools
    syncServer.addTool(new SyncToolSpecification(
        new Tool("add", "Adds two numbers",
            "{\"type\":\"object\",\"properties\":{\"a\":{\"type\":\"number\"},\"b\":{\"type\":\"number\"}},\"required\":[\"a\",\"b\"]}"),
        (exchange, arguments) -> {
          double a = ((Number) arguments.get("a")).doubleValue();
          double b = ((Number) arguments.get("b")).doubleValue();
          return new CallToolResult(
              List.of(new TextContent(String.valueOf(a + b))),
              false
          );
        }
    ));

    // Register resources
    syncServer.addResource(new SyncResourceSpecification(
        new Resource("test://static/resource/1", "Test Resource", "Example resource",
            "text/plain", null),
        (exchange, request) -> new ReadResourceResult(
            List.of(new TextResourceContents("test://static/resource/1", "text/plain",
                "Sample resource content"))
        )
    ));

    // Register prompts
    syncServer.addPrompt(new SyncPromptSpecification(
        new Prompt("greeting", "Generates a greeting",
            List.of(new PromptArgument("name", "Name to greet", true))),
        (exchange, request) -> {
          String name = (String) request.arguments().get("name");
          return new GetPromptResult(
              "Greeting prompt",
              List.of(new PromptMessage(Role.USER,
                  new TextContent("Hello " + name + "!")))
          );
        }
    ));
  }

  private static void shutdown() {
    running = false;
    if (syncServer != null) {
      try {
        syncServer.close();
        System.out.println("Server shut down gracefully");
      } catch (Exception e) {
        System.err.println("Error during shutdown:");
        e.printStackTrace();
      }
    }
  }

  private static ServerCapabilities getServerCapabilities() {
    return ServerCapabilities.builder()
        .resources(true, true)
        .tools(true)
        .prompts(true)
        .logging()
        .build();
  }
}
