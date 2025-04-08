package com.feng.deepseek.ds;

/**
 * Main application class for DeepSeek API client demonstration.
 * Showcases chat completion functionality using the DeepSeek client implementation.
 *
 * @since 2025/4/1
 */
public class DeepSeekApplication {
    public static void main(String[] args) {

        try {
            var apiKey = System.getenv("DEEPSEEK_API_KEY");
            if (apiKey == null) {
                throw new IllegalStateException("Please set the DEEPSEEK_API_KEY environment variable.");
            }
            var client = new DeepSeekClient(new DeepSeekModels.Config(apiKey, null));
            DeepSeekModels.ChatResponse
                    result = client.chat("deepseek-chat", "how to start a new business");
            if (!result.choices().isEmpty()) {
                var message = result.choices().get(0).message();
                System.out.println(message.content());
                System.out.println(result.usage().totalTokens());
            }
        } catch (DeepSeekException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
