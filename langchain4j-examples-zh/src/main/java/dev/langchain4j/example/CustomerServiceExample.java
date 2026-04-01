package dev.langchain4j.example;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.*;

/**
 * 客服系统示例：展示如何同时使用用户档案 + 多会话记忆管理。
 * 
 * 场景说明：
 * - 客户每次进来时加载其静态基础信息（姓名、会员等级等）
 * - 每个会话有独立的记忆，讨论不同的主题
 * - 会话 1：讨论订单 1 的发货进度
 * - 会话 2：咨询网站会员机制
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选)
 */
public class CustomerServiceExample {

    // AI 助手接口（支持指定会话 ID）
    interface CustomerServiceAssistant {
        String chat(String sessionId, String userMessage);
    }

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   客服系统 - 多会话管理示例");
        System.out.println("===========================================\n");

        // 1. 初始化聊天模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 创建客服系统存储（加载用户档案）
        CustomerServiceChatMemoryStore customerStore = new CustomerServiceChatMemoryStore("./user-profile.txt");

        // 3. 定义两个不同的会话
        String session1Id = "order-session-001";  // 订单咨询会话
        String session2Id = "vip-session-002";    // 会员咨询会话

        // 4. 为每个会话创建独立的聊天记忆
        ChatMemory session1Memory = MessageWindowChatMemory.builder()
                .id(session1Id)
                .maxMessages(20)
                .chatMemoryStore(customerStore)
                .build();

        ChatMemory session2Memory = MessageWindowChatMemory.builder()
                .id(session2Id)
                .maxMessages(20)
                .chatMemoryStore(customerStore)
                .build();

        // 5. 初始化会话（加载用户档案到每个会话）
        System.out.println("\n--- 初始化会话 ---");
        customerStore.initSession(session1Id);
        customerStore.initSession(session2Id);

        // 6. 创建 AI 服务（共享同一个存储，但不同会话）
        CustomerServiceAssistant assistant = AiServices.builder(CustomerServiceAssistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(id -> id.equals(session1Id) ? session1Memory : session2Memory)
                .build();

        // =========================================
        // 会话 1：讨论订单 1 的发货进度
        // =========================================
        System.out.println("\n" + "=".repeat(50));
        System.out.println("【会话 1】订单咨询 - Session ID: " + session1Id);
        System.out.println("=".repeat(50));

        String msg1_1 = "你好，我想查询一下我的订单发货情况。";
        String reply1_1 = assistant.chat(session1Id, msg1_1);
        System.out.println("客户：" + msg1_1);
        System.out.println("客服：" + reply1_1);

        String msg1_2 = "我的订单号是 ORDER-2024-001。";
        String reply1_2 = assistant.chat(session1Id, msg1_2);
        System.out.println("\n客户：" + msg1_2);
        System.out.println("客服：" + reply1_2);

        String msg1_3 = "这个订单什么时候能发货？";
        String reply1_3 = assistant.chat(session1Id, msg1_3);
        System.out.println("\n客户：" + msg1_3);
        System.out.println("客服：" + reply1_3);

        // =========================================
        // 会话 2：咨询网站会员机制
        // =========================================
        System.out.println("\n" + "=".repeat(50));
        System.out.println("【会话 2】会员咨询 - Session ID: " + session2Id);
        System.out.println("=".repeat(50));

        String msg2_1 = "你好，我想了解一下你们的会员制度。";
        String reply2_1 = assistant.chat(session2Id, msg2_1);
        System.out.println("客户：" + msg2_1);
        System.out.println("客服：" + reply2_1);

        String msg2_2 = "我现在的会员等级是什么？有什么权益？";
        String reply2_2 = assistant.chat(session2Id, msg2_2);
        System.out.println("\n客户：" + msg2_2);
        System.out.println("客服：" + reply2_2);

        String msg2_3 = "怎么升级到更高级的会员？";
        String reply2_3 = assistant.chat(session2Id, msg2_3);
        System.out.println("\n客户：" + msg2_3);
        System.out.println("客服：" + reply2_3);

        // =========================================
        // 查看会话状态
        // =========================================
        System.out.println("\n" + "=".repeat(50));
        System.out.println("【会话状态总览】");
        System.out.println("=".repeat(50));

        System.out.println("\n当前活跃的会话 ID: " + customerStore.getAllSessionIds());

        System.out.println("\n--- 会话 1 记忆内容 ---");
        System.out.println("消息数量：" + session1Memory.messages().size());
        ChatMessage lastMsg1 = session1Memory.messages().get(session1Memory.messages().size() - 1);
        System.out.println("最后一条消息：" + lastMsg1.toString());

        System.out.println("\n--- 会话 2 记忆内容 ---");
        System.out.println("消息数量：" + session2Memory.messages().size());
        ChatMessage lastMsg2 = session2Memory.messages().get(session2Memory.messages().size() - 1);
        System.out.println("最后一条消息：" + lastMsg2.toString());

        // =========================================
        // 演示跨会话上下文隔离
        // =========================================
        System.out.println("\n" + "=".repeat(50));
        System.out.println("【测试上下文隔离】");
        System.out.println("=".repeat(50));

        String msg1_4 = "刚才我们聊的是什么订单？";
        String reply1_4 = assistant.chat(session1Id, msg1_4);
        System.out.println("\n会话 1 - 客户：" + msg1_4);
        System.out.println("会话 1 - 客服：" + reply1_4);

        String msg2_4 = "刚才我们聊的是什么话题？";
        String reply2_4 = assistant.chat(session2Id, msg2_4);
        System.out.println("\n会话 2 - 客户：" + msg2_4);
        System.out.println("会话 2 - 客服：" + reply2_4);

        System.out.println("\n✓ 可以看到两个会话的上下文是完全独立的！");
        System.out.println("✓ 每个会话都知道自己讨论的内容，互不干扰。");

        System.out.println("\n===========================================");
        System.out.println("   示例结束");
        System.out.println("===========================================\n");
    }
}
