package dev.langchain4j.example;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Agent 工具调用详解：展示 AI 如何选择和调用工具。
 *
 * 核心机制：
 * 1. @Tool 注解的描述会发送给 AI，让 AI 知道每个工具的功能
 * 2. AI 根据用户问题和工具描述，决定调用哪个工具（及参数）
 * 3. 多个工具时，AI 通过描述匹配最合适的工具
 */
public class AgentDetailedExample {

    // ==================== 工具定义 ====================

    // 数学计算工具
    static class Calculator {
        @Tool("计算两个数字的和。参数: a-第一个数, b-第二个数")
        public int add(int a, int b) {
            System.out.println("  [工具调用] Calculator.add(" + a + ", " + b + ") = " + (a + b));
            return a + b;
        }

        @Tool("计算两个数字的乘积。参数: a-第一个数, b-第二个数")
        public int multiply(int a, int b) {
            System.out.println("  [工具调用] Calculator.multiply(" + a + ", " + b + ") = " + (a * b));
            return a * b;
        }

        @Tool("计算两个数字的差。参数: a-被减数, b-减数")
        public int subtract(int a, int b) {
            System.out.println("  [工具调用] Calculator.subtract(" + a + ", " + b + ") = " + (a - b));
            return a - b;
        }
    }

    // 天气查询工具
    static class WeatherService {
        @Tool("查询指定城市的天气。参数: city-城市名称")
        public String getWeather(String city) {
            System.out.println("  [工具调用] WeatherService.getWeather(" + city + ")");
            // 模拟返回天气数据
            Map<String, String> mockWeather = Map.of(
                "北京", "晴天，温度 18°C",
                "上海", "多云，温度 22°C",
                "广州", "小雨，温度 25°C"
            );
            return mockWeather.getOrDefault(city, "未找到该城市的天气信息");
        }
    }

    // 时间查询工具
    static class TimeService {
        @Tool("获取当前时间和日期")
        public String getCurrentTime() {
            System.out.println("  [工具调用] TimeService.getCurrentTime()");
            return java.time.LocalDateTime.now().toString();
        }
    }

    // ==================== 示例 1: 使用 AiServices ====================

    public static void simpleExample() {
        System.out.println("\n========== 示例 1: AiServices 基础用法 ==========\n");

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)   // 打印请求，查看工具描述如何发送给 AI
                .logResponses(true)  // 打印响应，查看 AI 的工具调用决策
                .build();

        interface Assistant {
            String chat(String userMessage);
        }

        // 注册多个工具类
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .tools(new Calculator())   // 注册计算器
                .tools(new WeatherService()) // 注册天气服务
                .tools(new TimeService())    // 注册时间服务
                .build();

        // 测试不同类型的问题，观察 AI 如何选择工具
        String[] questions = {
            "北京今天天气怎么样？",           // 应该调用 WeatherService
            "123 加上 456 等于多少？",        // 应该调用 Calculator.add
            "现在几点了？",                   // 应该调用 TimeService
            "100 减去 30 再乘以 2 是多少？"   // 可能多次调用 Calculator
        };

        for (String question : questions) {
            System.out.println("\n用户问题: " + question);
            System.out.println("--- AI 推理过程 ---");
            String answer = assistant.chat(question);
            System.out.println("AI 回答: " + answer);
        }
    }

    // ==================== 示例 2: 手动实现工具调用（展示完整流程） ====================

    public static void manualToolExecutionExample() throws Exception {
        System.out.println("\n========== 示例 2: 手动工具调用流程详解 ==========\n");
        System.out.println("这个示例展示 AI 工具调用的完整流程：");
        System.out.println("1. AI 收到用户问题 + 工具描述列表");
        System.out.println("2. AI 决定调用哪个工具（返回 tool_calls）");
        System.out.println("3. 执行工具，将结果返回给 AI");
        System.out.println("4. AI 根据工具结果生成最终回答\n");

        ChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 模拟工具描述（实际由 LangChain4j 自动生成）
        String systemPrompt = """
            你是一个智能助手，可以使用以下工具：

            工具列表：
            1. add(a, b) - 计算两个数字的和
            2. multiply(a, b) - 计算两个数字的乘积
            3. subtract(a, b) - 计算两个数字的差

            当用户问题需要使用工具时，请调用相应的工具。
            """;

        String userQuestion = "计算 (100 + 50) * 3 的结果";

        System.out.println("用户问题: " + userQuestion);
        System.out.println("\n--- 第 1 步: AI 决策 ---");
        System.out.println("AI 收到: 用户问题 + 工具描述列表");
        System.out.println("AI 需要决定: 是否调用工具？调用哪个？参数是什么？");

        // 第一次请求：AI 决定调用工具
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(SystemMessage.from(systemPrompt));
        messages.add(UserMessage.from(userQuestion));

        ChatResponse response1 = chatModel.chat(ChatRequest.builder().messages(messages).build());
        AiMessage aiMessage1 = response1.aiMessage();

        System.out.println("\nAI 第一次响应:");
        if (aiMessage1.hasToolExecutionRequests()) {
            System.out.println("AI 决定调用工具！");
            for (ToolExecutionRequest toolRequest : aiMessage1.toolExecutionRequests()) {
                System.out.println("  - 工具名: " + toolRequest.name());
                System.out.println("  - 参数: " + toolRequest.arguments());
            }

            // 添加 AI 的工具调用请求到消息历史
            messages.add(aiMessage1);

            // 执行工具并将结果返回给 AI
            System.out.println("\n--- 第 2 步: 执行工具 ---");
            Calculator calculator = new Calculator();

            for (ToolExecutionRequest toolRequest : aiMessage1.toolExecutionRequests()) {
                // 解析参数并执行工具（简化版，实际由 LangChain4j 自动处理）
                String result = executeToolManually(calculator, toolRequest);

                // 将工具执行结果添加到消息历史
                ToolExecutionResultMessage toolResult = ToolExecutionResultMessage.from(
                    toolRequest.id(),
                    toolRequest.name(),
                    result
                );
                messages.add(toolResult);
            }

            System.out.println("\n--- 第 3 步: AI 根据工具结果生成回答 ---");
            ChatResponse response2 = chatModel.chat(ChatRequest.builder().messages(messages).build());
            System.out.println("AI 最终回答: " + response2.aiMessage().text());
        } else {
            System.out.println("AI 直接回答（无工具调用）: " + aiMessage1.text());
        }
    }

    // 手动执行工具（简化版，仅演示概念）
    private static String executeToolManually(Calculator calculator, ToolExecutionRequest request) {
        String name = request.name();
        String args = request.arguments();

        System.out.println("执行工具: " + name + " 参数: " + args);

        // 简化的参数解析（实际由 LangChain4j 自动处理）
        // args 格式通常是 JSON: {"a": 100, "b": 50}
        try {
            // 简单提取数字值
            int[] values = extractNumbers(args);
            int a = values[0];
            int b = values[1];

            if ("add".equals(name)) {
                return String.valueOf(calculator.add(a, b));
            } else if ("multiply".equals(name)) {
                return String.valueOf(calculator.multiply(a, b));
            } else if ("subtract".equals(name)) {
                return String.valueOf(calculator.subtract(a, b));
            }
        } catch (Exception e) {
            return "工具执行错误: " + e.getMessage();
        }
        return "未知工具";
    }

    // 简单从 JSON 字符串中提取数字
    private static int[] extractNumbers(String jsonArgs) {
        // 格式: {"a": 100, "b": 50} 或 {"a":100,"b":50}
        List<Integer> numbers = new ArrayList<>();
        StringBuilder numBuilder = new StringBuilder();
        boolean inNumber = false;

        for (char c : jsonArgs.toCharArray()) {
            if (Character.isDigit(c) || (c == '-' && !inNumber)) {
                inNumber = true;
                numBuilder.append(c);
            } else if (inNumber) {
                numbers.add(Integer.parseInt(numBuilder.toString()));
                numBuilder.setLength(0);
                inNumber = false;
            }
        }
        if (inNumber) {
            numbers.add(Integer.parseInt(numBuilder.toString()));
        }

        return new int[]{numbers.get(0), numbers.get(1)};
    }

    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        LangChain4j Agent 工具调用机制详解                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        // 运行示例
        simpleExample();

        // 如需查看完整手动流程，取消下面注释：
        // manualToolExecutionExample();
    }
}