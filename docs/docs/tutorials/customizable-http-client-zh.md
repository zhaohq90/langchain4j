---
sidebar_position: 33
---
# 可定制 HTTP 客户端

LangChain4j 的一些模块（目前是 OpenAI 和 Ollama）支持定制用于调用 LLM 提供商 API 的 HTTP 客户端。

`langchain4j-http-client` 模块实现了 `HttpClient` SPI，这些模块使用它来调用 LLM 提供商的 REST API。这意味着底层 HTTP 客户端可以被定制，并且任何其他 HTTP 客户端都可以通过实现 `HttpClient` SPI 来集成。

目前，有 3 种开箱即用的实现：

*   `JdkHttpClient` 在 `langchain4j-http-client-jdk` 模块中。
    当使用受支持的模块（例如 `langchain4j-open-ai`）时，它被默认使用。
*   `SpringRestClient` 在 `langchain4j-http-client-spring-restclient` 中。
    当使用受支持模块的 Spring Boot Starter（例如 `langchain4j-open-ai-spring-boot-starter`）时，它被默认使用。
*   `ApacheHttpClient` 在 `langchain4j-http-client-apache` 模块中。
    可以通过向项目添加 `langchain4j-http-client-apache` 依赖来使用它。

## 定制 JDK 的 `HttpClient`

```java
HttpClient.Builder httpClientBuilder = HttpClient.newBuilder()
        .sslContext(...);
JdkHttpClientBuilder jdkHttpClientBuilder = JdkHttpClient.builder()
        .httpClientBuilder(httpClientBuilder);
OpenAiChatModel model = OpenAiChatModel.builder()
        .httpClientBuilder(jdkHttpClientBuilder)
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .modelName("gpt-4o-mini")
        .build();
```

## 定制 Spring 的 `RestClient`

```java
RestClient.Builder restClientBuilder = RestClient.builder()
        .requestFactory(new HttpComponentsClientHttpRequestFactory());
SpringRestClientBuilder springRestClientBuilder = SpringRestClient.builder()
        .restClientBuilder(restClientBuilder)
        .streamingRequestExecutor(new VirtualThreadTaskExecutor());
OpenAiChatModel model = OpenAiChatModel.builder()
        .httpClientBuilder(springRestClientBuilder)
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .modelName("gpt-4o-mini")
        .build();
```

## 定制 Apache 的 `HttpClient`

```java
org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder = org.apache.hc.client5.http.impl.classic.HttpClientBuilder.create();
ApacheHttpClientBuilder apacheHttpClientBuilder = ApacheHttpClient.builder()
        .httpClientBuilder(httpClientBuilder);
OpenAiChatModel model = OpenAiChatModel.builder()
        .httpClientBuilder(apacheHttpClientBuilder)
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .modelName("gpt-4o-mini")
        .build();
```
