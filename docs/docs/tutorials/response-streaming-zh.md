---
sidebar_position: 20
---
# 流式响应

在与大语言模型（LLM）交互时，尤其是生成较长的回答时，用户体验的一个关键因素是**响应速度**。如果用户需要等待模型完整生成所有内容才能看到第一个字，这会带来明显的延迟感。**流式响应（Streaming Response）** 机制正是为了解决这个问题而存在。

## 什么是流式响应

流式响应允许模型在生成内容的同时，将部分结果逐步发送给客户端。这意味着用户可以实时看到模型“思考”和“输出”的过程，而不是等待最终结果。这不仅提升了用户体验，也使得一些需要实时反馈的应用场景成为可能。

在 LangChain4j 中，流式响应通常通过回调函数或响应式编程范式来实现。当模型有新的 token 生成时，这些回调函数会被触发，从而允许你处理这些部分结果。

## 为什么流式响应很重要

| 优势 | 说明 |
|---|---|
| **提升用户体验** | 减少用户等待时间，提供即时反馈 |
| **实时交互** | 适用于需要实时更新界面的聊天应用、代码生成器等 |
| **感知性能** | 即使总生成时间不变，用户也会觉得响应更快 |
| **降低延迟** | 客户端可以更早地开始处理部分数据 |

## 如何使用流式响应

LangChain4j 提供了多种方式来实现流式响应。最常见的方式是使用 `StreamingChatModel` 接口及其实现，并提供一个 `StreamingResponseHandler`。

### 示例：使用 `StreamingResponseHandler`

```java
StreamingChatModel model = OpenAiStreamingChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .modelName("gpt-4o-mini")
    .build();

model.stream("请用中文写一个关于未来科技的短故事", new StreamingResponseHandler<AiMessage>() {
    @Override
    public void onNext(String token) {
        // 每当模型生成一个 token 时，此方法会被调用
        System.out.print(token);
    }

    @Override
    public void onComplete(Response<AiMessage> response) {
        // 当模型完成所有生成时，此方法会被调用
        System.out.println("\n--- 生成完成 ---");
    }

    @Override
    public void onError(Throwable error) {
        // 如果在流式生成过程中发生错误，此方法会被调用
        error.printStackTrace();
    }
});

// 阻塞主线程，直到流式响应完成（在实际应用中，你可能需要更复杂的异步处理）
// 注意：在某些框架（如 Spring WebFlux）中，可能不需要手动阻塞
try {
    Thread.sleep(60_000); // 等待最多60秒，以便流式响应完成
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

在这个例子中，`onNext` 方法会在模型每生成一个词或一个片段时被调用，你可以实时将这些片段展示给用户。`onComplete` 则在整个响应完成后触发，`onError` 用于处理流式过程中的异常。

### 更高级的流式处理

除了基本的 `StreamingResponseHandler`，LangChain4j 还支持更复杂的流式处理场景，例如：

*   **流式工具调用（Streaming Tool Calls）**：模型在流式生成过程中决定调用工具时，可以实时通知客户端。
*   **流式结构化输出（Streaming Structured Outputs）**：逐步解析并构建结构化对象。
*   **响应式编程集成**：与 Reactor 或 RxJava 等响应式库结合，实现更强大的异步流处理。

这些高级特性使得 LangChain4j 能够构建出高度交互和响应迅速的 AI 应用。

## 学习建议

流式响应是构建现代化 AI 应用不可或缺的一部分。在学习这一章时，建议你：

1.  **跑通一个基本流式示例**：亲身体验流式输出与非流式输出在用户体验上的差异。
2.  **理解回调机制**：掌握 `onNext`、`onComplete`、`onError` 的触发时机和作用。
3.  **考虑异步处理**：在实际项目中，流式响应通常与异步编程模型（如 Java 的 `CompletableFuture`、Kotlin 的协程或响应式框架）结合使用，以避免阻塞主线程。

掌握流式响应，将使你的 AI 应用在交互性和响应速度上迈上一个新台阶。
