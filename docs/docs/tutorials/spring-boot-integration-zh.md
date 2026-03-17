---
sidebar_position: 27
---
# Spring Boot 集成

LangChain4j 为以下内容提供了 [Spring Boot starter](https://github.com/langchain4j/langchain4j-spring)：

*   流行的集成
*   声明式 [AI Services](/tutorials/ai-services-zh)

## Spring Boot Starters

Spring Boot starter 有助于通过属性创建和配置 [语言模型](/category/language-models)、[嵌入模型](/category/embedding-models)、[嵌入存储](/category/embedding-stores) 和其他核心 LangChain4j 组件。

要使用其中一个 [Spring Boot starter](https://github.com/langchain4j/langchain4j-spring)，请导入相应的依赖。

Spring Boot starter 依赖的命名约定是：`langchain4j-{integration-name}-spring-boot-starter`。

例如，对于 OpenAI (`langchain4j-open-ai`)，依赖名称将是 `langchain4j-open-ai-spring-boot-starter`：

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
    <version>1.12.2-beta22</version>
</dependency>
```

然后，你可以在 `application.properties` 文件中配置模型参数，如下所示：

```properties
langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}
langchain4j.open-ai.chat-model.model-name=gpt-4o
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true
...
```

在这种情况下，`OpenAiChatModel`（`ChatModel` 的一个实现）的实例将自动创建，你可以在需要的地方自动装配它：

```java
@RestController
public class ChatController {
    ChatModel chatModel;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/chat")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return chatModel.chat(message);
    }
}
```

如果你需要 `StreamingChatModel` 的实例，请使用 `streaming-chat-model` 而不是 `chat-model` 属性：

```properties
langchain4j.open-ai.streaming-chat-model.api-key=${OPENAI_API_KEY}
...
```

## 声明式 AI Services 的 Spring Boot starter

LangChain4j 为自动配置 [AI Services](/tutorials/ai-services-zh)、[RAG](/tutorials/rag-zh)、[Tools](/tutorials/tools-zh) 等提供了 Spring Boot starter。

假设你已经导入了其中一个集成 starter（见上文），导入 `langchain4j-spring-boot-starter`：

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-spring-boot-starter</artifactId>
    <version>1.12.2-beta22</version>
</dependency>
```

你现在可以定义 AI Service 接口并使用 `@AiService` 注解：

```java
@AiService
interface Assistant {
    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);
}
```

把它想象成一个标准的 Spring Boot `@Service`，但具有 AI 能力。当应用程序启动时，LangChain4j starter 将扫描类路径并找到所有带有 `@AiService` 注解的接口。对于找到的每个 AI Service，它将使用应用程序上下文中所有可用的 LangChain4j 组件创建此接口的实现，并将其注册为 bean，以便你可以自动装配它：

```java
@RestController
public class AssistantController {

    private final Assistant assistant;

    public AssistantController(Assistant assistant) {
        this.assistant = assistant;
    }

    @GetMapping("/assistant")
    public String chat(@RequestParam(defaultValue = "Hello") String message) {
        return assistant.chat(message);
    }
}
```

### 配置 AI Service

你可以通过 `application.properties` 配置 AI Service 的行为。例如，要配置 `Assistant` 的 `chatMemory`：

```properties
langchain4j.ai-service.assistant.chat-memory.max-messages=10
```

这将为 `Assistant` 注入一个 `MessageWindowChatMemory`，并将其最大消息数设置为 10。

### 更多示例

*   [Spring Boot 示例](https://github.com/langchain4j/langchain4j-examples/tree/main/spring-boot-example)
*   [Spring Boot WebFlux 示例](https://github.com/langchain4j/langchain4j-examples/tree/main/spring-boot-webflux-example)
