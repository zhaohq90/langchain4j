---
sidebar_position: 3
---
# Azure OpenAI 嵌入模型集成

:::note
这是 `Azure OpenAI` 集成的文档，它使用 Microsoft 的 Azure SDK，如果你正在使用 Microsoft Java 技术栈，包括高级 Azure 身份验证机制，则最适合使用它。

LangChain4j 提供了 3 种不同的 OpenAI 集成用于使用嵌入模型，这是其中之一：

*   [OpenAI](/integrations/language-models/open-ai) 使用 OpenAI REST API 的自定义 Java 实现，最适合与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）一起使用。
*   [OpenAI 官方 SDK](/integrations/language-models/open-ai-official) 使用官方的 OpenAI Java SDK。
*   [Azure OpenAI](/integrations/language-models/azure-open-ai) 使用 Microsoft 的 Azure SDK，如果你正在使用 Microsoft Java 技术栈，包括高级 Azure 身份验证机制，则最适合使用它。
:::

Azure OpenAI 提供了一些嵌入模型（`text-embedding-3-small`、`text-embedding-ada-002` 等），可用于将文本或图像转换为维度向量空间。

## Maven 依赖

### 纯 Java

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-azure-open-ai</artifactId>
    <version>1.12.2</version>
</dependency>
```

### Spring Boot

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-azure-open-ai-spring-boot-starter</artifactId>
    <version>1.12.2-beta22</version>
</dependency>
```

## 创建 `AzureOpenAiEmbeddingModel`

### 纯 Java

```java
EmbeddingModel model = AzureOpenAiEmbeddingModel.builder()
        .apiKey(System.getenv("AZURE_OPENAI_KEY"))
        .deploymentName("text-embedding-3-small")
        .endpoint("https://langchain4j.openai.azure.com/")
        ...
        .build();
```

### Spring Boot

添加到 `application.properties`：

```properties
langchain4j.azure-open-ai.embedding-model.endpoint=https://langchain4j.openai.azure.com/
langchain4j.azure-open-ai.embedding-model.service-version=...
langchain4j.azure-open-ai.embedding-model.api-key=${AZURE_OPENAI_KEY}
langchain4j.azure-open-ai.embedding-model.deployment-name=text-embedding-3-small
langchain4j.azure-open-ai.embedding-model.timeout=...
langchain4j.azure-open-ai.embedding-model.max-retries=...
langchain4j.azure-open-ai.embedding-model.log-requests-and-responses=...
langchain4j.azure-open-ai.embedding-model.user-agent-suffix=...
langchain4j.azure-open-ai.embedding-model.dimensions=...
langchain4j.azure-open-ai.embedding-model.customHeaders=...
```

## 示例

*   [AzureOpenAiEmbeddingModelExamples](https://github.com/langchain4j/langchain4j-examples/blob/main/azure-open-ai-examples/src/main/java/AzureOpenAiEmbeddingModelExamples.java)
