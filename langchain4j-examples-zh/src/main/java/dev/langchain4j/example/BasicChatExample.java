package dev.langchain4j.example;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * 基础聊天示例：展示如何初始化模型并发送第一条消息。
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选) 如果使用代理或中转，请设置此项
 */
public class BasicChatExample {

    public static void main(String[] args) {
        // 1. 初始化聊天模型
        // 这里以 OpenAI 为例，你可以根据需要替换为 Ollama, DashScope 等
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .baseUrl(System.getenv("OPENAI_BASE_URL")) // 默认为 https://api.openai.com/v1
                .logRequests(true)  // 开启请求日志，方便调试
                .logResponses(true) // 开启响应日志
                .build();

        // 2. 发送消息并获取回复
        String userMessage = "你好，LangChain4j！请简要介绍一下你自己。";
        String response = model.generate(userMessage);

        // 3. 打印结果
        System.out.println("========================================");
        System.out.println("用户: " + userMessage);
        System.out.println("AI: " + response);
        System.out.println("========================================");
    }
}
