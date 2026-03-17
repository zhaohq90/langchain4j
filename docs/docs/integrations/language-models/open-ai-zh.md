---
sidebar_position: 1
---
# OpenAI 集成

LangChain4j 提供了与 OpenAI 模型的深度集成，支持其聊天模型、嵌入模型、图像生成模型等。本页将重点介绍如何配置和使用 OpenAI 的聊天模型。

## 聊天模型

LangChain4j 提供了 `OpenAiChatModel` 用于与 OpenAI 的聊天模型（如 GPT-4o、GPT-3.5-turbo 等）进行同步交互，以及 `OpenAiStreamingChatModel` 用于流式交互。

### 创建 `OpenAiChatModel`

#### 纯 Java

```java
ChatModel model = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY")) // 你的 OpenAI API Key
    .modelName("gpt-4o-mini") // 使用的模型名称
    .temperature(0.7) // 控制输出的随机性，0.0 到 2.0
    .timeout(Duration.ofSeconds(60)) // 请求超时时间
    .logRequests(true) // 记录请求日志
    .logResponses(true) // 记录响应日志
    .build();
```

你还可以使用 `ChatRequestParameters` 或 `OpenAiChatRequestParameters` 来指定默认的聊天请求参数：

```java
ChatModel model = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .defaultRequestParameters(OpenAiChatRequestParameters.builder()
        .modelName("gpt-4o-mini")
        .temperature(0.7)
        .build())
    .build();
```

#### Spring Boot

在 `application.properties` 中添加配置：

```properties
# 必填属性:
langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}
langchain4j.open-ai.chat-model.model-name=gpt-4o-mini
# 可选属性:
langchain4j.open-ai.chat-model.base-url=https://api.openai.com/v1
langchain4j.open-ai.chat-model.temperature=0.7
langchain4j.open-ai.chat-model.timeout=60s
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true
# ... 更多参数，如 top-p, frequency-penalty, presence-penalty 等
```

### 结构化输出

OpenAI 的结构化输出功能同时支持 [工具调用](/tutorials/tools) 和 [响应格式](/tutorials/ai-services#json-mode)。

#### 用于工具调用的结构化输出

要为工具启用结构化输出功能，在构建模型时设置 `.strictTools(true)`：

```java
OpenAiChatModel.builder()
    ...
    .strictTools(true)
    .build();
```

请注意，这将自动使所有工具参数变为强制性（在 JSON Schema 中为 `required`），并为 JSON Schema 中的每个 `object` 设置 `additionalProperties=false`。这是由于当前的 OpenAI 限制。

#### 用于响应格式的结构化输出

在使用 AI Services 时，要为响应格式启用结构化输出功能，在构建模型时设置 `.supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)` 和 `.strictJsonSchema(true)`：

```java
OpenAiChatModel.builder()
    ...
    .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
    .strictJsonSchema(true)
    .build();
```

在这种情况下，AI Service 将自动从给定的 POJO 生成 JSON Schema 并将其传递给 LLM。

### 思维/推理 (Thinking / Reasoning)

此设置适用于 [DeepSeek](https://api-docs.deepseek.com/guides/reasoning_model)。

当在构建 `OpenAiChatModel` 或 `OpenAiStreamingChatModel` 时启用 `returnThinking` 参数，DeepSeek API 响应的 `reasoning_content` 字段将被解析并返回到 `AiMessage.thinking()` 中。

当为 `OpenAiStreamingChatModel` 启用 `returnThinking` 参数时，当 DeepSeek API 流式传输 `reasoning_content` 时，将调用 `StreamingChatResponseHandler.onPartialThinking()` 和 `TokenStream.onPartialThinking()` 回调。

以下是配置思维的示例：

```java
ChatModel model = OpenAiChatModel.builder()
        .baseUrl("https://api.deepseek.com/v1")
        .apiKey(System.getenv("DEEPSEEK_API_KEY"))
        .modelName("deepseek-reasoner")
        .returnThinking(true)
        .build();
```

当在构建 `OpenAiChatModel` 或 `OpenAiStreamingChatModel` 时启用 `sendThinking` 参数，`AiMessage.thinking()` 将在请求中发送到 DeepSeek API。
字段的名称可以通过使用 `sendThinking(boolean, String)` 构建器方法进行配置。
默认情况下，使用 `reasoning_content` 字段名称。

### 创建 `OpenAiStreamingChatModel`

#### 纯 Java

```java
StreamingChatModel model = OpenAiStreamingChatModel.builder()
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .modelName("gpt-4o-mini")
        .build();
// 你也可以使用 ChatRequestParameters 或 OpenAiChatRequestParameters 指定默认聊天请求参数
StreamingChatModel model = OpenAiStreamingChatModel.builder()
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .defaultRequestParameters(OpenAiChatRequestParameters.builder()
                .modelName("gpt-4o-mini")
                .build())
        .build();
```

#### Spring Boot

添加到 `application.properties`：

```properties
# 必填属性:
langchain4j.open-ai.streaming-chat-model.api-key=${OPENAI_API_KEY}
langchain4j.open-ai.streaming-chat-model.model-name=gpt-4o-mini
# 可选属性:
langchain4j.open-ai.streaming-chat-model.base-url=...
langchain4j.open-ai.streaming-chat-model.custom-headers=...
langchain4j.open-ai.streaming-chat-model.frequency-penalty=...
langchain4j.open-ai.streaming-chat-model.log-requests=...
langchain4j.open-ai.streaming-chat-model.log-responses=...
langchain4j.open-ai.streaming-chat-model.logit-bias=...
langchain4j.open-ai.streaming-chat-model.max-retries=...
langchain4j.open-ai.streaming-chat-model.max-completion-tokens=...
langchain4j.open-ai.streaming-chat-model.max-tokens=...
langchain4j.open-ai.streaming-chat-model.metadata=...
langchain4j.open-ai.streaming-chat-model.organization-id=...
langchain4j.open-ai.streaming-chat-model.parallel-tool-calls=...
langchain4j.open-ai.streaming-chat-model.presence-penalty=...
langchain4j.open-ai.streaming-chat-model.project-id=...
langchain4j.open-ai.streaming-chat-model.reasoning-effort=...
langchain4j.open-ai.streaming-chat-model.response-format=...
langchain4j.open-ai.streaming-chat-model.return-thinking=...
langchain4j.open-ai.streaming-chat-model.seed=...
langchain4j.open-ai.streaming-chat-model.service-tier=...
langchain4j.open-ai.streaming-chat-model.stop=...
langchain4j.open-ai.streaming-chat-model.store=...
langchain4j.open-ai.streaming-chat-model.strict-schema=...
langchain4j.open-ai.streaming-chat-model.strict-tools=...
langchain4j.open-ai.streaming-chat-model.temperature=...
langchain4j.open-ai.streaming-chat-model.timeout=...
langchain4j.open-ai.streaming-chat-model.top-p=...
```
