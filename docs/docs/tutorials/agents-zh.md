---
sidebar_position: 26
---
# 智能体 (Agents)

在 LangChain4j 中，**智能体（Agents）** 代表着从“模型回答问题”到“模型理解任务、拆解步骤、调用能力并推进执行”的关键演进。它不仅仅是一个单一的功能点，而是一种更高层的系统设计范式，旨在让大型语言模型（LLM）能够自主地规划和执行任务。

## Agent 的核心思想

一个 Agent 的核心能力在于其**自主决策和执行**。它能够：

*   **理解目标**：将用户的自然语言请求转化为明确的任务目标。
*   **规划步骤**：根据目标和可用工具，制定一系列执行计划。
*   **调用工具**：在需要时，选择并调用一个或多个外部工具来获取信息或执行动作。
*   **处理结果**：根据工具的执行结果，调整计划或继续推进任务。
*   **整合输出**：将多步执行的中间结果整合为最终的、有意义的输出。

这意味着 Agent 本质上更像一个“任务协调者”或“流程自动化器”，而不是简单的问答接口。

| 能力维度 | 说明 |
|---|---|
| **目标理解** | 理解用户意图，将模糊需求转化为具体任务 |
| **步骤规划** | 动态生成或选择执行路径，以达成目标 |
| **能力调用** | 灵活使用工具、检索器、其他 Agent 等外部能力 |
| **状态推进** | 根据中间反馈迭代执行，直到任务完成 |
| **结果整合** | 将分散的执行结果汇总为连贯的最终答案 |

## 为什么 Agent 很重要

许多现实世界的业务问题需要多步骤的推理和执行，例如“帮我调研一家公司、总结风险并给出行动建议”、“帮我检查知识库回答是否缺少证据”或“帮我完成一个需要搜索、提取、判断和写作的任务”。这些任务都更像是流程，而不是单个问题。

Agent 正是在这种背景下出现的。它让大模型从“语言生成器”向“面向任务的执行协调者”演进，极大地扩展了 LLM 的应用边界。

## LangChain4j 中的 Agent

LangChain4j 提供了构建 Agent 的抽象和工具。一个典型的 Agent 通常会包含：

*   **`ChatModel`**：作为 Agent 的“大脑”，负责理解、推理和决策。
*   **`Tools`**：作为 Agent 的“手脚”，提供与外部世界交互的能力。
*   **`ChatMemory`**：作为 Agent 的“记忆”，维护对话历史和上下文。

### 示例：构建一个简单的 Agent

```java
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

// 定义一个工具类
class Calculator {
    @Tool("计算两个数字的和")
    public int add(int a, int b) {
        return a + b;
    }
}

interface Assistant {
    String chat(String userMessage);
}

public class AgentDemo {
    public static void main(String[] args) {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(OpenAiChatModel.builder()
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .modelName("gpt-4o-mini")
                        .build())
                .chatMemory(MessageWindowChatMemory.with  MaxMessages(10)) // 为 Agent 提供记忆
                .tools(new Calculator()) // 注册工具
                .build();

        System.out.println(assistant.chat("请问 123 加上 456 等于多少？"));
        // 预期输出：123 加上 456 等于 579。

        System.out.println(assistant.chat("再帮我算一下，上一个结果减去 79 是多少？"));
        // 预期输出：579 减去 79 等于 500。
    }
}
```

在这个例子中，`Assistant` 被配置了一个 `ChatModel`、`ChatMemory` 和 `Calculator` 工具。当用户提问时，Agent 会自主判断是否需要调用 `Calculator` 工具来完成计算，并利用 `ChatMemory` 记住之前的对话上下文。

## 多 Agent 与 Subagent

当单个 Agent 难以覆盖全部职责时，系统就会进入**多 Agent** 模式。此时不同 Agent 可以承担不同角色，例如一个负责规划，一个负责检索，一个负责执行，一个负责审校。

从工程角度看，**Subagent** 可以理解为被上层 Agent 调用或编排的下级代理。它通常承担某个更专门、更聚焦的职责，而不是直接面向最终用户。这样的分层有助于降低单一代理的职责复杂度，也更有利于复用与治理。

| 角色类型 | 典型职责 |
|---|---|
| **主 Agent** | 接收总体目标、分派任务、汇总结果 |
| **检索 Agent** | 专注知识查询与证据召回 |
| **工具 Agent** | 专注调用外部系统或业务工具 |
| **审校 Agent** | 负责校验答案、检查结构与风险 |
| **Subagent** | 作为子能力单元，由上层 Agent 在流程中调用 |

### 为什么要学习 Subagent

你提到的 subagent，实际上是从“一个大而全的代理”走向“职责分解与分层协作”的关键概念。对于复杂业务系统来说，这种设计非常重要。因为它可以把搜索、推理、执行、审校等职责拆成更独立的单元，从而提升系统稳定性、可维护性与可观测性。

## 学习建议

如果你已经完成基础聊天、AI Services 和 Tools，那么进入 Agents 阶段时，建议不要一开始就追求过于复杂的多 Agent 架构。更好的方式是先理解**单 Agent 如何结合工具与检索完成任务**，再逐步演进到**主 Agent + 子 Agent 的层次结构**，最后再考虑多 Agent 协作、MCP 与更复杂的模式化工作流。

Agent 是 LangChain4j 中最能体现其“编排”思想的部分，掌握它将使你能够构建出高度智能和自主的应用程序。
