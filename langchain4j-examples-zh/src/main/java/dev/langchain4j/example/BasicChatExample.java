package dev.langchain4j.example;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 基础聊天示例：展示如何初始化模型并发送第一条消息。
 *
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选) 如果使用代理或中转，请设置此项
 */
public class BasicChatExample {

    public static void main(String[] args) throws Exception {
        //basicChat();
        System.out.println("\n");
        streamingChat();
    }

    /**
     * 基础聊天示例：同步发送消息并获取完整回复
     */
    public static void basicChat() {
        // 1. 初始化聊天模型
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 发送消息并获取回复
        String userMessage = "你好，LangChain4j！请简要介绍一下你自己。";
        String response = model.chat(userMessage);

        // 3. 打印结果
        System.out.println("========================================");
        System.out.println("【基础聊天示例 - 同步模式】");
        System.out.println("用户: " + userMessage);
        System.out.println("AI: " + response);
        System.out.println("========================================");
    }

    /**
     * 流式聊天示例：实时接收并打印 AI 回复
     */
    public static void streamingChat() throws Exception {
        // 1. 初始化支持流式输出的聊天模型
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 使用 CompletableFuture 等待流式响应完成
        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();

        //String userMessage = "请用一句话解释什么是流式输出？";
        String userMessage = "使用 java 实现一个冒泡排序, 然后比较下常见的几种排序算法, 比如效率, 资源占用, 是否稳定等";

        System.out.println("========================================");
        System.out.println("【流式聊天示例 - 实时输出】");
        System.out.println("用户: " + userMessage);
        System.out.print("AI: ");

        // 3. 发送消息并流式接收回复
        model.chat(userMessage, new StreamingChatResponseHandler() {

            private final StringBuilder responseBuilder = new StringBuilder();

            @Override
            public void onPartialResponse(String partialResponse) {
                // 每收到一个部分响应就立即打印并记录
                System.out.print(partialResponse);
                System.out.flush();
                responseBuilder.append(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                // 流式输出完成
                System.out.println();
                System.out.println("【完整响应内容】: " + responseBuilder.toString());
                System.out.println("========================================");
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                // 处理错误
                System.out.println("\n[错误] " + error.getMessage());
                error.printStackTrace();
                futureResponse.completeExceptionally(error);
            }
        });

        // 4. 等待流式响应完成（最多等待 60 秒）
        ChatResponse response = futureResponse.get(600, TimeUnit.SECONDS);
        System.out.println("【Token 使用量】: " + response.tokenUsage());
    }
}
