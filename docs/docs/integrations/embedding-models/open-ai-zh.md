---
sidebar_position: 15
---
# OpenAI 嵌入模型集成

:::note
这是 `OpenAI` 集成的文档，它使用 OpenAI REST API 的自定义 Java 实现，最适合与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）一起使用。

LangChain4j 提供了 3 种不同的 OpenAI 集成用于使用嵌入模型，这是其中之一：

*   [OpenAI](/integrations/language-models/open-ai) 使用 OpenAI REST API 的自定义 Java 实现，最适合与 Quarkus（因为它使用 Quarkus REST 客户端）和 Spring（因为它使用 Spring 的 RestClient）一起使用。
*   [OpenAI 官方 SDK](/integrations/language-models/open-ai-official) 使用官方的 OpenAI Java SDK。
*   [Azure OpenAI](/integrations/language-models/azure-open-ai) 使用 Microsoft 的 Azure SDK，如果你正在使用 Microsoft Java 技术栈，包括高级 Azure 身份验证机制，则最适合使用它。
:::

*   [https://platform.openai.com/docs/guides/embeddings](https://platform.openai.com/docs/guides/embeddings)
*   [https://platform.openai.com/docs/api-reference/embeddings](https://platform.openai.com/docs/api-reference/embeddings)

## Maven 依赖

### 纯 Java

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>1.12.2</version>
</dependency>
```

### Spring Boot

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai-spring-boot-starter</artifactId>
    <version>1.12.2-beta22</version>
</dependency>
```

## 创建 `OpenAiEmbeddingModel`

### 纯 Java

```java
EmbeddingModel model = OpenAiEmbeddingModel.builder()
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .modelName("text-embedding-3-small")
        .build();
```

### Spring Boot

添加到 `application.properties`：

```properties
# 必填属性:
langchain4j.open-ai.embedding-model.api-key=${OPENAI_API_KEY}
langchain4j.open-ai.embedding-model.model-name=text-embedding-3-small
# 可选属性:
langchain4j.open-ai.embedding-model.base-url=...
langchain4j.open-ai.embedding-model.custom-headers=...
langchain4j.open-ai.embedding-model.dimensions=...
langchain4j.open-ai.embedding-model.log-requests=...
langchain4j.open-ai.embedding-model.log-responses=...
langchain4j.open-ai.embedding-model.max-retries=...
langchain4j.open-ai.embedding-model.organization-id=...
langchain4j.open-ai.embedding-model.project-id=...
langchain4j.open-ai.embedding-model.timeout=...
langchain4j.open-ai.embedding-model.user=...
```

## 示例

*   [OpenAiEmbeddingModelExamples](https://github.com/langchain4j/langchain4j-examples/blob/main/open-ai-examples/src/main/java/OpenAiEmbeddingModelExamples.java)
