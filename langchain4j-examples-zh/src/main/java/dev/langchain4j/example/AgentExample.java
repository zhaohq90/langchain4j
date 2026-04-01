package dev.langchain4j.example;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Agent (智能体) 示例：展示如何让 AI 调用外部工具。
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选)
 */
public class AgentExample {

    // 1. 定义一个简单的工具类
    static class Calculator {
        @Tool("计算两个数字的和")
        public int add(int a, int b) {
            System.out.println("AI 正在调用工具：计算 " + a + " + " + b);
            return a + b;
        }

        @Tool("计算两个数字的乘积")
        public int multiply(int a, int b) {
            System.out.println("AI 正在调用工具：计算 " + a + " * " + b);
            return a * b;
        }
    }

    public static void main(String[] args) {
        // 2. 初始化聊天模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .baseUrl(System.getenv("OPENAI_BASE_URL"))
                .build();

        // 3. 创建 AI 服务 (声明式接口)
        interface Assistant {
            String chat(String userMessage);
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .tools(new Calculator()) // 注册工具
                .build();

        // 4. 提问并获取 AI 调用工具后的回答
        String question = "请计算 123 加上 456 的结果，然后再乘以 2。";
        String answer = assistant.chat(question);

        System.out.println("========================================");
        System.out.println("问题: " + question);
        System.out.println("AI (调用工具后): " + answer);
        System.out.println("========================================");
    }
}
