package dev.langchain4j.example;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * 记忆系统示例：展示如何让 AI 记住之前的对话内容。
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选)
 */
public class MemoryExample {

    public static void main(String[] args) {
        // 1. 初始化聊天模型
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .baseUrl(System.getenv("OPENAI_BASE_URL"))
                .build();

        // 2. 初始化聊天记忆 (这里使用内存存储，保留最近 10 条消息)
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // 3. 创建 AI 服务 (声明式接口)
        interface Assistant {
            String chat(String userMessage);
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(chatMemory)
                .build();

        // 4. 进行多轮对话
        String userMessage1 = "你好，我叫张三。";
        String response1 = assistant.chat(userMessage1);
        System.out.println("用户: " + userMessage1);
        System.out.println("AI: " + response1);

        String userMessage2 = "请问我叫什么名字？";
        String response2 = assistant.chat(userMessage2);
        System.out.println("用户: " + userMessage2);
        System.out.println("AI: " + response2);

        System.out.println("========================================");
        System.out.println("记忆内容: " + chatMemory.messages());
        System.out.println("========================================");
    }
}
