package dev.langchain4j.example;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * 用户档案文件加载示例：展示如何从本地文本文件加载用户背景信息。
 * 
 * 特点：
 * 1. 从 user-profile.txt 文件加载用户静态信息
 * 2. 自动将用户信息转换为系统消息提供给 AI
 * 3. 适合需要预加载用户背景信息的场景
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选)
 */
public class UserProfileMemoryExample {

    public static void main(String[] args) {
        // 1. 初始化聊天模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 从文件加载用户档案 (./user-profile.txt)
        UserProfileFileStore profileStore = new UserProfileFileStore("./user-profile.txt");
        
        // 3. 使用用户档案的聊天记忆 (保留最近 10 条消息)
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id("default-user")
                .maxMessages(10)
                .chatMemoryStore(profileStore)
                .build();

        // 4. 创建 AI 服务 (声明式接口)
        interface Assistant {
            String chat(String userMessage);
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .build();

        // 5. 进行对话 - AI 已经知道用户的背景信息
        System.out.println("=== 测试对话 ===");
        
        String userMessage1 = "你好！";
        String response1 = assistant.chat(userMessage1);
        System.out.println("用户：" + userMessage1);
        System.out.println("AI: " + response1);

        System.out.println("\n=== 询问个人信息 ===");
        String userMessage2 = "请问我叫什么名字？";
        String response2 = assistant.chat(userMessage2);
        System.out.println("用户：" + userMessage2);
        System.out.println("AI: " + response2);

        System.out.println("\n=== 询问职业信息 ===");
        String userMessage3 = "我是做什么工作的？";
        String response3 = assistant.chat(userMessage3);
        System.out.println("用户：" + userMessage3);
        System.out.println("AI: " + response3);

        System.out.println("\n=== 询问兴趣爱好 ===");
        String userMessage4 = "我平时喜欢做什么？";
        String response4 = assistant.chat(userMessage4);
        System.out.println("用户：" + userMessage4);
        System.out.println("AI: " + response4);

        System.out.println("\n========================================");
        System.out.println("当前记忆内容：" + chatMemory.messages());
        System.out.println("========================================");
    }
}
