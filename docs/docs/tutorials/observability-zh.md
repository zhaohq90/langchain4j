---
sidebar_position: 35
---
# 可观测性

在构建基于 LLM 的应用程序时，理解模型如何响应、请求的细节以及潜在的错误至关重要。LangChain4j 提供了**可观测性（Observability）** 功能，允许你监控和调试与 LLM 的交互。

## 监听器（Listeners）

LangChain4j 通过**监听器（Listeners）** 机制提供可观测性。你可以注册一个或多个监听器来捕获 LLM 请求的生命周期事件，包括请求发送前、响应接收后以及发生错误时。

### `ChatModelListener`

`ChatModelListener` 接口允许你监听 `ChatModel` 的事件。它提供了以下方法：

*   `onRequest(ChatModelRequestContext requestContext)`：在向 LLM 提供商发送请求之前调用。
*   `onResponse(ChatModelResponseContext responseContext)`：在从 LLM 提供商收到成功响应之后调用。
*   `onError(ChatModelErrorContext errorContext)`：在与 LLM 提供商交互过程中发生错误时调用。

这些方法提供了丰富的上下文信息，包括请求消息、模型参数、响应消息、元数据、token 使用情况以及任何发生的错误。

#### 示例：实现一个 `ChatModelListener`

```java
ChatModelListener listener = new ChatModelListener() {
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        ChatRequest chatRequest = requestContext.chatRequest();
        System.out.println("onRequest: " + chatRequest);
        // 访问请求消息
        List<ChatMessage> messages = chatRequest.messages();
        System.out.println(messages);
        // 访问请求参数
        ChatRequestParameters parameters = chatRequest.parameters();
        System.out.println(parameters.modelName());
        System.out.println(parameters.temperature());
        // ... 更多参数

        // 访问模型提供商信息
        System.out.println(requestContext.modelProvider());

        // 可以在请求上下文中传递自定义属性
        Map<Object, Object> attributes = requestContext.attributes();
        attributes.put("my-attribute", "my-value");
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        ChatResponse chatResponse = responseContext.chatResponse();
        System.out.println("onResponse: " + chatResponse);
        // 访问 AI 消息
        AiMessage aiMessage = chatResponse.aiMessage();
        System.out.println(aiMessage);
        // 访问响应元数据
        ChatResponseMetadata metadata = chatResponse.metadata();
        System.out.println(metadata.id());
        System.out.println(metadata.modelName());
        // ... 更多元数据

        // 访问 token 使用情况
        TokenUsage tokenUsage = metadata.tokenUsage();
        System.out.println(tokenUsage.inputTokenCount());
        System.out.println(tokenUsage.outputTokenCount());
        System.out.println(tokenUsage.totalTokenCount());

        // 访问原始请求
        ChatRequest chatRequest = responseContext.chatRequest();
        System.out.println(chatRequest);

        // 访问模型提供商信息
        System.out.println(responseContext.modelProvider());

        // 访问请求上下文中传递的自定义属性
        Map<Object, Object> attributes = responseContext.attributes();
        System.out.println(attributes.get("my-attribute"));
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Throwable error = errorContext.error();
        error.printStackTrace();
        System.out.println("onError: " + error.getMessage());
        // 访问原始请求
        ChatRequest chatRequest = errorContext.chatRequest();
        System.out.println(chatRequest);
        // 访问模型提供商信息
        System.out.println(errorContext.modelProvider());
        // 访问请求上下文中传递的自定义属性
        Map<Object, Object> attributes = errorContext.attributes();
        System.out.println(attributes.get("my-attribute"));
    }
};

ChatModel model = OpenAiChatModel.builder()
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .modelName("gpt-4o-mini")
        .listeners(List.of(listener)) // 注册监听器
        .build();

model.chat("讲一个关于 Java 的笑话");
```

`attributes` map 允许在同一个 `ChatModelListener` 的 `onRequest`、`onResponse` 和 `onError` 方法之间，以及在多个 `ChatModelListener` 之间传递信息。

### 监听器的工作原理

*   监听器被指定为 `List<ChatModelListener>`，并按照迭代顺序调用。
*   监听器是同步调用的，并且在同一个线程中。有关流式情况的更多详细信息，请参见下文。第二个监听器不会在第一个监听器返回之前被调用。
*   `ChatModelListener.onRequest()` 方法在调用 LLM 提供商 API 之前调用。
*   `ChatModelListener.onRequest()` 方法每个请求只调用一次。如果在调用 LLM 提供商 API 时发生错误并进行重试，`ChatModelListener.onRequest()` **不会**为每次重试调用。
*   `ChatModelListener.onResponse()` 方法只调用一次，在从 LLM 提供商收到成功响应后立即调用。
*   `ChatModelListener.onError()` 方法只调用一次。如果在调用 LLM 提供商 API 时发生错误并进行重试，`ChatModelListener.onError()` **不会**为每次重试调用。

## 总结

可观测性是构建健壮 LLM 应用程序的关键。通过利用 LangChain4j 的监听器机制，你可以深入了解模型交互的内部工作原理，从而更好地调试、优化和维护你的 AI 应用。
