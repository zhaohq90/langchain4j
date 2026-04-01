package dev.langchain4j.example;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * 文件持久化存储示例：展示如何使用本地文件来持久化聊天记忆。
 * 
 * 特点：
 * 1. 聊天记录会保存到本地文件 (chat-memory.txt)
 * 2. 程序重启后可以从文件恢复之前的对话
 * 3. 适合需要长期保存对话历史的场景
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选)
 */
public class PersistentFileMemoryExample {

    public static void main(String[] args) {
        // 1. 初始化聊天模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 创建文件存储 (数据会自动保存到 ./chat-memory.txt)
        FileChatMemoryStore fileStore = new FileChatMemoryStore("./chat-memory.txt");
        
        // 3. 使用文件存储的聊天记忆 (保留最近 10 条消息)
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id("user-001")
                .maxMessages(10)
                .chatMemoryStore(fileStore)
                .build();

        // 4. 创建 AI 服务 (声明式接口)
        interface Assistant {
            String chat(String userMessage);
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .build();

        // 5. 进行多轮对话
        System.out.println("=== 第一轮对话 ===");
        String userMessage1 = "你好，我叫张三。";
        String response1 = assistant.chat(userMessage1);
        System.out.println("用户：" + userMessage1);
        System.out.println("AI: " + response1);

        System.out.println("\n=== 第二轮对话 ===");
        String userMessage2 = "请问我叫什么名字？";
        String response2 = assistant.chat(userMessage2);
        System.out.println("用户：" + userMessage2);
        System.out.println("AI: " + response2);

        System.out.println("\n========================================");
        System.out.println("当前记忆内容：" + chatMemory.messages());
        System.out.println("========================================");
        
        // 6. 程序退出前确保数据已保存（其实每次更新都会自动保存）
        fileStore.flush();
        System.out.println("\n✓ 聊天记录已保存到：./chat-memory.txt");
        System.out.println("提示：重新运行此程序可以读取之前的对话历史！");
    }
}
