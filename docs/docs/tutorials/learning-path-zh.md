# LangChain4j 中文学习路线

本教程面向希望系统学习 LangChain4j 的 Java 开发者，目标不是仅仅告诉你“某个 API 怎么调用”，而是帮助你建立一条从**基础聊天能力**到**RAG**、从**工具调用**到**Agent / 多 Agent / Subagent** 的连续学习路径。

为了便于真正落地，本教程特别新增了 **`langchain4j-examples-zh`** 模块，提供了带详细中文注释的可运行示例代码。

## 一、学习总览

建议把 LangChain4j 的学习拆成五个阶段：基础模型调用、高层 AI Services、知识增强与结构化能力、执行型智能体能力，以及工程化与生态扩展能力。

| 阶段 | 学习目标 | 关键词 | 对应示例代码 (在 `langchain4j-examples-zh` 中) |
|---|---|---|---|
| 第一阶段 | 跑通最小聊天示例，理解模型调用基础 | ChatModel、消息、模型集成 | `BasicChatExample.java` |
| 第二阶段 | 用更少代码构建业务型 AI 接口 | AI Services、System Message | `MemoryExample.java` (含 AI Services) |
| 第三阶段 | 让系统具备知识与结构化处理能力 | Chat Memory、RAG | `MemoryExample.java`, `RagExample.java` |
| 第四阶段 | 让系统具备执行与协作能力 | Tools、Agents | `AgentExample.java` |
| 第五阶段 | 走向复杂系统与生产落地 | 多 Agent、Subagent | 见下文进阶指引 |

---

## 二、第一阶段：基础功能入门

### 2.1 先跑通最小聊天能力
你首先应该阅读 `get-started-zh.md`。完成这一页后，你应该尝试运行示例代码。

*   **对应示例**：`langchain4j-examples-zh/src/main/java/dev/langchain4j/example/BasicChatExample.java`
*   **学习重点**：如何初始化 `ChatLanguageModel`，如何使用 `generate` 方法。

### 2.2 理解底层聊天模型抽象
阅读 `chat-and-language-models-zh.md`。理解 `UserMessage`、`AiMessage` 和 `SystemMessage`。

---

## 三、第二阶段：用 AI Services 快速构建业务能力

阅读 `ai-services-zh.md`。这是 LangChain4j 最核心的高层封装。

*   **对应示例**：`langchain4j-examples-zh/src/main/java/dev/langchain4j/example/MemoryExample.java`
*   **学习重点**：如何定义一个接口并使用 `AiServices.builder()` 将其转为 AI 助手。

---

## 四 : 第三阶段：让系统具备知识能力与结构化能力

### 4.1 Chat Memory：支持多轮上下文
阅读 `chat-memory-zh.md`。

*   **对应示例**：`langchain4j-examples-zh/src/main/java/dev/langchain4j/example/MemoryExample.java`
*   **学习重点**：`ChatMemory` 如何存储对话历史，以及如何在多轮对话中自动携带上下文。

### 4.2 RAG：让回答基于你的私有知识
阅读 `rag-zh.md` 和 `easy-rag-zh.md`。

*   **对应示例**：`langchain4j-examples-zh/src/main/java/dev/langchain4j/example/RagExample.java`
*   **学习重点**：文档加载 (`Document`)、向量化 (`EmbeddingModel`)、存储 (`EmbeddingStore`) 和检索增强的完整链路。

---

## 五、第四阶段：让系统具备执行能力

### 5.1 Tools：让模型调用业务函数
阅读 `tools-zh.md`。

*   **对应示例**：`langchain4j-examples-zh/src/main/java/dev/langchain4j/example/AgentExample.java`
*   **学习重点**：使用 `@Tool` 注解定义工具，并让 AI 根据用户意图自动选择并调用这些工具。

### 5.2 Agents：进入任务驱动范式
阅读 `agents-zh.md`。理解 Agent 如何利用工具完成复杂任务。

---

## 六、第五阶段：学习多 Agent 与 Subagent

### 6.1 多 Agent 的本质：职责分工
当单体 Agent 复杂度过高时，需要将其拆分为多个具备专门职责的 Agent。

*   **Planner Agent**: 负责拆解任务。
*   **Executor Agent**: 负责执行具体工具调用。
*   **Reviewer Agent**: 负责检查结果。

### 6.2 Subagent 如何用
Subagent 是被主 Agent 调用的“下属”。在 LangChain4j 中，你可以通过在一个 Agent 的工具集中注册另一个 AI Service（即另一个 Agent）来实现 Subagent 模式。

*   **进阶建议**：参考 `langchain4j-examples-zh/src/main/java/dev/langchain4j/example/AgentExample.java` 中的工具注册逻辑，尝试将另一个 `AiServices` 实例作为工具注入。

---

## 七、如何运行示例代码

1.  **环境准备**：确保安装了 JDK 17+ 和 Maven。
2.  **设置环境变量**：
    *   `OPENAI_API_KEY`: 你的 OpenAI API 密钥。
    *   `OPENAI_BASE_URL`: (可选) 如果使用代理，请设置此项。
3.  **运行**：进入 `langchain4j-examples-zh` 目录，使用 IDE 运行对应的 `main` 方法即可。

---

## 八、更多参考位置

| 类型 | 仓库路径 |
|---|---|
| **集成测试** | `langchain4j/src/test/java/dev/langchain4j/service/` (包含大量 AI Services 进阶用法) |
| **Easy RAG 测试** | `langchain4j-easy-rag/src/test/java/dev/langchain4j/rag/easy/EasyRagIT.java` |
| **核心抽象测试** | `langchain4j-core/src/test/java/dev/langchain4j/` |
