# AI Services

`AI Services` 是 LangChain4j 中最具代表性的高层 API 之一。它的设计目标非常明确：让开发者使用**普通 Java 接口**来声明一个 AI 能力，而由框架负责把接口调用转换为提示词、消息、模型请求与响应解析过程。

如果说底层 `ChatModel` 更像是“直接操作模型”，那么 `AI Services` 更像是“声明一个具备 AI 能力的业务接口”。对于大多数应用开发者而言，这是最容易快速落地业务功能的方式。

## AI Services 的核心思路

使用 `AI Services` 时，你通常会先定义一个接口，例如：

```java
interface Assistant {
    String chat(String userMessage);
}
```

然后通过 `AiServices.builder(...)` 绑定底层模型实例，生成该接口的运行时实现：

```java
Assistant assistant = AiServices.builder(Assistant.class)
    .chatModel(model)
    .build();
```

之后你就可以像调用普通 Java 对象一样调用它：

```java
String answer = assistant.chat("请解释一下什么是 RAG");
```

这样做的最大价值在于：**业务接口、模型能力与底层调用细节实现了解耦**。对业务团队来说，代码的表达方式会更像是在编写面向领域的服务，而不是在堆叠提示词字符串。

## 为什么 AI Services 很重要

AI Services 之所以是 LangChain4j 学习中的关键章节，是因为它承接了大多数实际业务的第一步。很多团队的目标并不是研究底层模型细节，而是尽快构建一个“能问答、能提取、能分类、能调用工具”的服务接口。

在这种场景下，AI Services 的优势非常明显。

| 优势 | 说明 |
|---|---|
| 开发效率高 | 通过接口声明即可构建 AI 能力 |
| 样板代码少 | 无需每次手工组装消息与解析输出 |
| 易于维护 | 业务代码结构更接近传统 Java 服务设计 |
| 易于扩展 | 能自然接入 Tools、Memory、RAG、结构化输出等高级能力 |

## 与底层 ChatModel 的关系

AI Services 并不是独立于底层模型存在的。相反，它建立在底层 `ChatModel` 等组件之上。你仍然需要选择具体模型供应商、配置 API Key、决定使用哪一种聊天模型实现。

换句话说，**ChatModel 是基础能力层，AI Services 是面向业务开发的高级封装层**。理解这两者的关系有助于你在项目中合理选择抽象层级。

## AI Services 可以做什么

在 LangChain4j 中，AI Services 不只是“聊天接口生成器”。随着配置增强，它可以逐步承担更复杂的职责。例如，它可以配合系统消息定义角色，可以使用 Chat Memory 支持多轮会话，可以结合 Tools 扩展模型执行能力，也可以通过 Retrieval Augmentor 接入 RAG，甚至可以把结果映射为结构化对象。

因此，从学习路径上看，AI Services 往往是连接多个高级主题的中枢。很多教程中的高级功能，最终都会回到“如何把这一能力接到 AI Services 上”。

## 典型学习顺序

如果你正在用 AI Services 学习 LangChain4j，通常推荐按下面的顺序渐进：先学最简单的接口式聊天；再学系统提示与参数控制；接着引入 Chat Memory 处理多轮会话；再把 Tools 接入，让模型能触发业务函数；之后接入 Retrieval Augmentor 支持 RAG；最后学习结构化输出与复杂 Agent 组合方式。

| 阶段 | 目标 |
|---|---|
| 入门 | 用接口方式实现聊天助手 |
| 进阶一 | 增加系统提示、参数控制与多轮记忆 |
| 进阶二 | 加入 Tools，让模型可以触发业务逻辑 |
| 进阶三 | 接入 RAG，让回答建立在私有知识上 |
| 高阶 | 结合 Agents、Skills、MCP 构建更复杂系统 |

## 在项目中的定位

从工程实践看，AI Services 很适合成为业务层的 AI 入口。你可以把它理解为“AI 版本的 Service 接口”。在传统 Java 项目中，控制器会依赖业务 Service；在引入 LangChain4j 后，很多 AI 相关业务逻辑就可以通过 AI Services 封装成独立接口，然后再由上层应用调用。

## 学习建议

如果你的目标是快速交付一个具备聊天能力的功能，那么可以把 AI Services 当作主入口。如果你的目标是后续做 RAG、Agent 或多 Agent 协作，那么也建议先把 AI Services 学扎实，因为后面很多高级能力最终都会与它集成。
