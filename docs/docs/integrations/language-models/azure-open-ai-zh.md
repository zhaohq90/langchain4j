---
sidebar_position: 2
---
# Azure OpenAI 集成

LangChain4j 提供了与 Azure OpenAI 服务的集成，支持其聊天模型、嵌入模型和图像生成模型。本页将重点介绍如何配置和使用 Azure OpenAI 的聊天模型。

## 聊天模型

LangChain4j 提供了 `AzureOpenAiChatModel` 用于与 Azure OpenAI 的聊天模型进行同步交互，以及 `AzureOpenAiStreamingChatModel` 用于流式交互。

### 创建 `AzureOpenAiChatModel`

#### 纯 Java

```java
ChatModel model = AzureOpenAiChatModel.builder()
    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT")) // 你的 Azure OpenAI 端点
    .apiKey(System.getenv("AZURE_OPENAI_KEY")) // 你的 Azure OpenAI API Key
    .deploymentName("gpt-4o") // 部署的模型名称
    .temperature(0.7) // 控制输出的随机性，0.0 到 2.0
    .timeout(Duration.ofSeconds(60)) // 请求超时时间
    .logRequests(true) // 记录请求日志
    .logResponses(true) // 记录响应日志
    .build();
```

你还可以使用 `ChatRequestParameters` 或 `AzureOpenAiChatRequestParameters` 来指定默认的聊天请求参数：

```java
ChatModel model = AzureOpenAiChatModel.builder()
    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
    .apiKey(System.getenv("AZURE_OPENAI_KEY"))
    .defaultRequestParameters(AzureOpenAiChatRequestParameters.builder()
        .deploymentName("gpt-4o")
        .temperature(0.7)
        .build())
    .build();
```

#### Spring Boot

在 `application.properties` 中添加配置：

```properties
# 必填属性:
langchain4j.azure-open-ai.chat-model.endpoint=${AZURE_OPENAI_ENDPOINT}
langchain4j.azure-open-ai.chat-model.api-key=${AZURE_OPENAI_KEY}
langchain4j.azure-open-ai.chat-model.deployment-name=gpt-4o
# 可选属性:
langchain4j.azure-open-ai.chat-model.temperature=0.7
langchain4j.azure-open-ai.chat-model.timeout=60s
langchain4j.azure-open-ai.chat-model.log-requests=true
langchain4j.azure-open-ai.chat-model.log-responses=true
# ... 更多参数，如 top-p, frequency-penalty, presence-penalty 等
```

### 使用 Azure 凭据创建 `AzureOpenAiChatModel`

API 密钥可能存在一些安全问题（可能被提交到代码库、可能被泄露等）。如果你想提高安全性，建议使用 Azure 凭据代替。
为此，需要向项目添加 `azure-identity` 依赖：

```xml
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-identity</artifactId>
    <scope>compile</scope>
</dependency>
```

然后，你可以使用 [DefaultAzureCredentialBuilder](https://learn.microsoft.com/en-us/java/api/com.azure.identity.defaultazurecredentialbuilder?view=azure-java-stable) API 创建 `AzureOpenAiChatModel`：

```java
ChatModel model = AzureOpenAiChatModel.builder()
        .deploymentName("gpt-4o")
        .endpoint(System.getenv("AZURE_OPENAI_URL"))
        .tokenCredential(new DefaultAzureCredentialBuilder().build())
        .build();
```

:::note
请注意，你需要使用托管标识部署模型。有关更多信息，请查看 [Azure CLI 部署脚本](https://github.com/langchain4j/langchain4j-examples/blob/main/azure-open-ai-examples/src/main/script/deploy-azure-openai-security.sh)。
:::

## 工具

工具，也称为“函数调用”，受到支持并允许模型调用 Java 代码中的方法，包括并行工具调用。
“函数调用”在 OpenAI 文档中 [此处](https://platform.openai.com/docs/guides/function-calling) 进行了描述。

:::note
有关如何在 LangChain4j 中使用“函数调用”的完整教程，请参见 [此处](/tutorials/tools/)。
:::

函数可以使用 `ToolSpecification` 类指定，或者更简单地使用 `@Tool` 注解，如下例所示：

```java
class StockPriceService {
    private Logger log = Logger.getLogger(StockPriceService.class.getName());
    @Tool("根据股票代码获取公司的股票价格")
    public double getStockPrice(@P("公司股票代码") String ticker) {
        log.info("获取 " + ticker + " 的股票价格");
        if (Objects.equals(ticker, "MSFT")) {
            return 400.0;
        } else {
            return 0.0;
        }
    }
}
```

然后，你可以在 AI `Assistant` 中使用 `StockPriceService`，如下所示：

```java
interface Assistant {
    String chat(String userMessage);
}
public class Demo {
    String functionCalling(Model model) {
        String question = "微软目前的股价是否高于 450 美元？";
        StockPriceService stockPriceService = new StockPriceService();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(stockPriceService)
                .build();
        String answer = assistant.chat(question);
        model.addAttribute("answer", answer);
        return "demo";
    }
}
```

## 结构化输出

结构化输出确保模型的响应符合 JSON Schema。

:::note
有关在 LangChain4j 中使用结构化输出的文档可在 [此处](/tutorials/structured-outputs) 找到，以下部分将提供 Azure OpenAI 特定的信息。
:::

模型需要配置 `strictJsonSchema` 参数为 `true`，以强制遵守 JSON Schema：

```java
ChatModel model = AzureOpenAiChatModel.builder()
        .endpoint(System.getenv("AZURE_OPENAI_URL"))
        .apiKey(System.getenv("AZURE_OPENAI_KEY"))
        .deploymentName("gpt-4o")
        .strictJsonSchema(true)
        .supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA))
        .build();
```

:::note
如果 `strictJsonSchema` 设置为 `false` 并且你提供了 JSON Schema，模型仍会尝试生成符合 Schema 的响应，但如果响应不符合 Schema，它不会失败。这样做的一个原因是获得更好的性能。
:::

然后，你可以将此模型与高级 `Assistant` API 或低级 `ChatModel` API 一起使用，如下所述。
当与高级 `Assistant` API 一起使用时，配置 `supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA))` 以启用带有 JSON Schema 的结构化输出。

### 使用高级 `Assistant` API

与上一节中的工具一样，结构化输出可以自动与 AI `Assistant` 一起使用：

```java
interface PersonAssistant {
    Person extractPerson(String message);
}
class Person {
    private final String name;
    private final List<String> favouriteColors;
    public Person(String name, List<String> favouriteColors) {
        this.name = name;
        this.favouriteColors = favouriteColors;
    }
    public String getName() {
        return name;
    }
    public List<String> getFavouriteColors() {
        return favouriteColors;
    }
}
```

这个 `Assistant` 将确保响应符合与 `Person` 类对应的 JSON Schema，如下例所示：
