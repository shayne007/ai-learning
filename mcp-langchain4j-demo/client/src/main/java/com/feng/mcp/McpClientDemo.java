package com.feng.mcp;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import java.util.List;

/**
 * TODO
 *
 * @since 2025/4/30
 */
public class McpClientDemo {

  public static void main(String[] args) {
    OpenAiChatModel model = OpenAiChatModel.builder()
        .apiKey(System.getenv("DEEPSEEK_API_KEY"))
        .baseUrl("https://api.deepseek.com")
        .modelName("deepseek-chat")
        .build();

    McpTransport transport = new HttpMcpTransport.Builder()
        .sseUrl("http://0.0.0.0:45450/sse")
        .logRequests(true) // if you want to see the traffic in the log
        .logResponses(true)
        .build();

    McpClient mcpClient = new DefaultMcpClient.Builder()
        .transport(transport)
        .build();

    ToolProvider toolProvider = McpToolProvider.builder()
        .mcpClients(List.of(mcpClient))
        .build();

    mcpClient.listTools().forEach(System.out::println);

    WeatherAssistant meteo = AiServices.builder(WeatherAssistant.class)
        .chatLanguageModel(model)
        .toolProvider(toolProvider)
        .build();

    List.of(
        "Hello!",
        "What's the weather like in Paris today?"
    ).forEach((String q) -> {
      System.out.println(q);
      System.out.println(meteo.request(q));
    });
  }

}
