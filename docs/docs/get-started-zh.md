---
sidebar_position: 5
stableVersion: 1.12.2
betaVersion: 1.12.2-beta22
---
# 快速开始

如果你希望尽快跑通一个 LangChain4j 最小示例，本页是最佳起点。对于绝大多数 Java 开发者而言，最先需要完成的事情通常只有三步：引入对应模型供应商依赖、配置 API Key、创建模型实例并发起一次聊天调用。

:::note
如果你使用的是 Quarkus，请优先参考 Quarkus 集成文档。
如果你使用的是 Spring Boot，请优先参考 Spring Boot 集成文档。
如果你使用的是 Helidon，也建议直接查看对应框架集成教程。
:::

## 第一步：引入依赖

LangChain4j 对多种 LLM 提供商、嵌入模型与向量存储都提供了集成模块。你需要根据实际使用的模型供应商引入相应依赖。下面以 OpenAI 为例。

### Maven

在 `pom.xml` 中加入 OpenAI 集成依赖：

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>1.12.2</version>
</dependency>
```

如果你还希望使用高层的 **AI Services** API，则通常还需要加入主模块依赖：

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>1.12.2</version>
</dependency>
```

### Gradle

在 `build.gradle` 中可写为：

```groovy
implementation 'dev.langchain4j:langchain4j-open-ai:1.12.2'
implementation 'dev.langchain4j:langchain4j:1.12.2'
```

## 第二步：可选地使用 BOM 管理版本

如果项目中需要同时使用多个 LangChain4j 模块，建议引入 BOM 统一版本管理。这样可以减少多模块版本不一致导致的问题。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>dev.langchain4j</groupId>
            <artifactId>langchain4j-bom</artifactId>
            <version>1.12.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

需要注意的是，BOM 会统一管理 LangChain4j 各模块的推荐版本，但某些模块在版本演进过程中可能仍处于 beta 版本序列。因此，在生产环境升级时，仍应关注对应模块的发布说明与兼容性变化。

## 第三步：配置 API Key

模型调用通常需要 API Key。建议通过环境变量管理密钥，避免在代码仓库中直接暴露。

```java
String apiKey = System.getenv("OPENAI_API_KEY");
```

这种方式最适合本地开发、CI/CD 与生产环境配置，也更符合常见的安全治理实践。

## 第四步：创建模型实例

完成依赖引入与密钥配置后，就可以创建一个聊天模型实例。以下示例使用 `OpenAiChatModel`：

```java
OpenAiChatModel model = OpenAiChatModel.builder()
    .apiKey(apiKey)
    .modelName("gpt-4o-mini")
    .build();
```

这个对象就是你后续与模型交互的基础入口。

## 第五步：发起第一次聊天调用

模型实例创建好之后，就可以直接调用聊天方法：

```java
String answer = model.chat("Say 'Hello World'");
System.out.println(answer); // Hello World
```

到这里，你已经完成了 LangChain4j 的最小闭环：**引入依赖、配置密钥、初始化模型、执行一次聊天调用**。

## 如果暂时没有自己的 API Key

如果你当前只是为了体验 LangChain4j，也可以临时使用文档中提供的演示方式。不过演示方式通常有模型限制与额度限制，更适合快速试用，而不适合正式开发。

```java
OpenAiChatModel model = OpenAiChatModel.builder()
    .baseUrl("http://langchain4j.dev/demo/openai/v1")
    .apiKey("demo")
    .modelName("gpt-4o-mini")
    .build();
```

## 下一步应该学什么

完成快速开始后，通常建议按以下顺序继续学习。

| 学习顺序 | 推荐主题 | 说明 |
|---|---|---|
| 1 | Chat and Language Models | 先理解底层聊天模型与消息体系 |
| 2 | AI Services | 学习如何以更高层方式声明聊天助手 |
| 3 | Tools | 让模型具备调用业务函数的能力 |
| 4 | Chat Memory | 让对话具备多轮上下文记忆 |
| 5 | RAG | 让模型结合私有知识进行回答 |
| 6 | Agents / Skills / MCP | 进入更复杂的任务编排与智能体系统 |

如果你的目标是“尽快做出一个能聊天的应用”，那么下一站通常应该直接进入 **Chat and Language Models** 与 **AI Services**。如果你的目标是“做企业知识库问答”，那么完成基础聊天之后应尽快进入 **Embedding、向量存储与 RAG**。如果你的目标是“做会调用工具和系统的智能体”，则后续重点应放在 **Tools、Agents、MCP 与 Skills**。
