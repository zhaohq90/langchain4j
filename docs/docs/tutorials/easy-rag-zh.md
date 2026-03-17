---
sidebar_position: 11
---
# 简易 RAG (Easy RAG)

LangChain4j 提供了一个“简易 RAG”（Easy RAG）功能，旨在尽可能简化 RAG 的入门体验。你无需学习嵌入、选择向量存储、寻找合适的嵌入模型、弄清楚如何解析和分割文档等复杂细节。只需指向你的文档，LangChain4j 就会自动完成其余工作。

## 为什么选择简易 RAG

简易 RAG 的核心价值在于**降低门槛**和**快速验证**。对于初学者或需要快速构建概念验证（PoC）的场景，它提供了一条捷径，让你能够迅速体验 RAG 的能力，而无需深入了解其内部机制。

| 优势 | 说明 |
|---|---|
| **快速上手** | 无需手动配置复杂的 RAG 组件，一步到位 |
| **简化流程** | 自动处理文档加载、分割、嵌入和存储 |
| **概念验证** | 适合快速验证 RAG 在特定数据集上的效果 |

## 如何使用简易 RAG

简易 RAG 的使用非常直观。你通常只需要提供文档的来源，LangChain4j 就会为你处理好文档的索引和检索过程。

```java
// 假设你有一个包含文档的目录
Path documentPath = Paths.get("path/to/your/documents");

// 创建一个简易 RAG 聊天模型
ChatModel easyRagChatModel = EasyRagChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .modelName("gpt-4o-mini")
    .documentPath(documentPath) // 指向你的文档路径
    .build();

// 现在你可以像使用普通聊天模型一样使用它
String answer = easyRagChatModel.chat("我的文档中提到了什么？");
System.out.println(answer);
```

:::note
请注意，上述代码是一个概念性示例。实际的 `EasyRagChatModel` 可能需要更具体的配置，例如指定文档类型、嵌入模型等。请查阅 LangChain4j 的最新文档和示例以获取准确的用法。
:::

如果你使用的是 Quarkus，甚至有更简单的方式来实现简易 RAG。请阅读 [Quarkus 文档](https://docs.quarkiverse.io/quarkus-langchain4j/dev/rag-easy-rag.html)。

## 简易 RAG 的局限性

当然，“简易 RAG”的质量通常会低于经过精心调优的 RAG 设置。它在灵活性、性能和准确性方面可能不如自定义的 RAG 解决方案。

然而，这是开始学习 RAG 和/或制作概念验证的最简单方法。之后，你将能够从简易 RAG 平稳过渡到更高级的 RAG，逐步调整和定制更多方面。

## 学习建议

*   **从简易 RAG 开始**：如果你是 RAG 新手，先从简易 RAG 开始，快速建立对 RAG 工作原理的直观感受。
*   **理解其自动化过程**：尝试思考简易 RAG 在背后为你做了哪些事情（文档加载、分割、嵌入、存储、检索）。
*   **为进阶 RAG 做准备**：当简易 RAG 无法满足你的需求时，它为你提供了平滑过渡到“核心 RAG API”和“高级 RAG”的基础。
