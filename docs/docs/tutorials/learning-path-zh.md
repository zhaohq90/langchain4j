# LangChain4j 中文学习路线

本教程面向希望系统学习 LangChain4j 的 Java 开发者，目标不是仅仅告诉你“某个 API 怎么调用”，而是帮助你建立一条从**基础聊天能力**到**RAG**、从**工具调用**到**Agent / 多 Agent / Subagent** 的连续学习路径。

为了便于真正落地，本教程会强调三个维度：第一，每个阶段应该先学什么；第二，该阶段在官方文档中对应哪些章节；第三，相关示例代码通常可以去哪里找。这样你不仅能“看懂文档”，还能更快找到仓库中对应的可运行参考。

## 一、学习总览

建议把 LangChain4j 的学习拆成五个阶段：基础模型调用、高层 AI Services、知识增强与结构化能力、执行型智能体能力，以及工程化与生态扩展能力。这个顺序并不是唯一正确答案，但对于大多数业务开发者来说，是相对平滑且投入产出比最高的路径。

| 阶段 | 学习目标 | 关键词 |
|---|---|---|
| 第一阶段 | 跑通最小聊天示例，理解模型调用基础 | ChatModel、消息、模型集成 |
| 第二阶段 | 用更少代码构建业务型 AI 接口 | AI Services、System Message、参数控制 |
| 第三阶段 | 让系统具备知识与结构化处理能力 | Chat Memory、Structured Outputs、RAG |
| 第四阶段 | 让系统具备执行与协作能力 | Tools、Agents、MCP、Skills |
| 第五阶段 | 走向复杂系统与生产落地 | 多 Agent、Subagent、Guardrails、Observability |

## 二、第一阶段：基础功能入门

这一阶段的目标非常明确：先完成一次真实的模型调用，并理解底层交互对象是什么。很多人一上来就想做 Agent，但如果连消息、模型实例、上下文和供应商集成都没有搞清楚，后面会非常容易混乱。

### 2.1 先跑通最小聊天能力

你首先应该阅读 `get-started`，它会告诉你如何引入依赖、配置 API Key、创建模型实例并执行最小示例。完成这一页后，你应达到的状态是：**已经可以在本地通过某个模型供应商发起一次聊天调用**。

| 推荐阅读 | 中文文件 | 作用 |
|---|---|---|
| `docs/docs/get-started.md` | `docs/docs/get-started-zh.md` | 完成项目最小启动闭环 |
| `README.md` | `README-zh.md` | 理解项目定位与整体能力 |
| `docs/docs/intro.md` | `docs/docs/intro-zh.md` | 了解两层抽象与模块结构 |

### 2.2 理解底层聊天模型抽象

跑通第一个示例之后，不要急着进入高级封装。建议先阅读 `chat-and-language-models`。这一章决定了你是否真正理解 LangChain4j 的底层世界：`ChatModel` 是什么，`UserMessage`、`AiMessage`、`SystemMessage` 又扮演什么角色，多轮对话为什么需要上下文，多模态输入又是怎样组织的。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/chat-and-language-models.md` | `docs/docs/tutorials/chat-and-language-models-zh.md` | 消息模型、上下文、多模态 |

### 2.3 核心模型参数与行为控制

理解模型的基本调用后，你需要掌握如何通过参数控制模型的行为，以及如何处理流式响应和日志。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/model-parameters.md` | `docs/docs/tutorials/model-parameters-zh.md` | temperature, topP, maxTokens 等参数对模型输出的影响 |
| `docs/docs/tutorials/response-streaming.md` | `docs/docs/tutorials/response-streaming-zh.md` | 如何实现实时流式输出，提升用户体验 |
| `docs/docs/tutorials/logging.md` | `docs/docs/tutorials/logging-zh.md` | 如何配置日志记录，监控请求与响应 |
| `docs/docs/tutorials/customizable-http-client.md` | `docs/docs/tutorials/customizable-http-client-zh.md` | 如何定制底层 HTTP 客户端 |
| `docs/docs/tutorials/observability.md` | `docs/docs/tutorials/observability-zh.md` | 通过监听器机制实现可观测性 |

### 2.4 模型集成

LangChain4j 支持多种 LLM 提供商。你需要了解如何集成你选择的模型。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/integrations/language-models/index.md` | `docs/docs/integrations/language-models/index-zh.md` | 语言模型集成概览 |
| `docs/docs/integrations/language-models/open-ai.md` | `docs/docs/integrations/language-models/open-ai-zh.md` | OpenAI 聊天模型与嵌入模型集成 |
| `docs/docs/integrations/language-models/azure-open-ai.md` | `docs/docs/integrations/language-models/azure-open-ai-zh.md` | Azure OpenAI 聊天模型与嵌入模型集成 |

### 2.5 这一阶段建议实践什么

建议你至少完成两个小练习。第一个练习是接入一个模型，实现单轮聊天。第二个练习是手工传入历史消息，实现一个最简单的两轮对话。然后尝试调整 `temperature` 参数，观察模型输出的变化。最后，尝试集成你选择的模型，并配置日志记录。

## 三、第二阶段：用 AI Services 快速构建业务能力

当你理解底层模型后，最应该进入的就是 `AI Services`。这是 LangChain4j 最适合业务开发者的高层入口。它的优势是，你可以像定义普通 Java 接口一样定义一个 AI 助手，然后让框架帮你完成提示词封装、消息映射和结果解析。

### 3.1 为什么这一阶段很关键

绝大多数真实项目并不需要你处处手工操作 `ChatModel`。团队更需要的是“一个可以被业务层稳定调用的 AI 能力接口”。AI Services 正是这个角色。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/ai-services.md` | `docs/docs/tutorials/ai-services-zh.md` | 接口式 AI 能力、与 Tools / Memory / RAG 的结合 |

### 3.2 这一阶段建议实践什么

建议你实现一个最简单的 `Assistant` 接口，例如 `String chat(String message)`，并把它接到你自己的模型配置上。然后逐步尝试：给助手增加系统角色设定、限制回答风格、控制输出长度。完成这些之后，你基本就拥有了一个真正可用的“业务型聊天接口”。

## 四、第三阶段：让系统具备知识能力与结构化能力

这一阶段是从“能聊天”走向“能支撑业务”的关键阶段。你需要学习三个主题：**Chat Memory、Structured Outputs、RAG**。

### 4.1 Chat Memory：支持多轮上下文

当你的助手开始承接真实对话场景时，多轮上下文几乎是必需品。这个阶段的重点不是记住某个类名，而是理解会话上下文如何管理、会话如何隔离、历史消息如何裁剪。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/chat-memory.md` | `docs/docs/tutorials/chat-memory-zh.md` | 多轮对话、上下文管理、会话隔离 |

建议实践：为 AI Services 挂接 Chat Memory，让助手能记住用户刚刚说过的话。

### 4.2 Structured Outputs：让结果进入业务流程

如果你的系统需要做信息抽取、分类、标签判断、工单生成或 API 参数组装，那么结构化输出就是必须掌握的能力。它让模型输出从自然语言过渡为 Java 对象或其他稳定数据结构。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/structured-outputs.md` | `docs/docs/tutorials/structured-outputs-zh.md` | 对象映射、字段化输出、可消费结果 |

建议实践：从一段用户输入中抽取姓名、公司、需求类型等字段，映射为 Java 对象。

### 4.3 RAG：让回答基于你的私有知识

当你希望系统能回答企业内部资料、产品文档、FAQ 或知识库相关问题时，就该进入 RAG。学习这一阶段时，不要只关注“怎么查向量库”，而要从完整链路理解问题：文档从哪里来、如何解析、如何切分、如何嵌入、如何存储、如何检索、如何把结果喂给模型。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/rag.md` | `docs/docs/tutorials/rag-zh.md` | RAG 全链路、检索增强、知识问答 |
| `docs/docs/tutorials/embedding-stores.md` | `docs/docs/tutorials/embedding-stores-zh.md` | 向量存储通用概念与集成 |
| `docs/docs/tutorials/easy-rag.md` | `docs/docs/tutorials/easy-rag-zh.md` | 简易 RAG 入门 |
| `docs/docs/integrations/embedding-models/open-ai.md` | `docs/docs/integrations/embedding-models/open-ai-zh.md` | OpenAI 嵌入模型集成 |
| `docs/docs/integrations/embedding-models/azure-open-ai.md` | `docs/docs/integrations/embedding-models/azure-open-ai-zh.md` | Azure OpenAI 嵌入模型集成 |
| `docs/docs/integrations/document-loaders/*` | 暂未翻译 | 文档导入来源 |
| `docs/docs/integrations/document-parsers/*` | 暂未翻译 | 文档格式解析 |
| `docs/docs/integrations/embedding-stores/*` | 暂未翻译 | 具体向量库集成 |

建议实践：选择一个简单知识源，例如 Markdown 文档或 FAQ 文本，做一个最小 RAG 问答助手。完成后，再替换为真实向量库与更复杂文档源。

## 五、第四阶段：让系统具备执行能力

从这一阶段开始，你会从“知识型系统”进入“执行型系统”。重点主题是 **Tools、Agents、MCP、Skills**。

### 5.1 Tools：让模型调用业务函数

Tools 是执行型 AI 应用的起点。它让模型不再只是回答问题，而可以在需要时调用你提供的函数。例如查询订单、检索数据库、访问搜索服务、触发某个业务流程。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/tools.md` | `docs/docs/tutorials/tools-zh.md` | 函数调用、工具暴露、业务执行 |

建议实践：实现一个订单查询工具或天气查询工具，并让 AI Services 注册这个工具。

### 5.2 Agents：进入任务驱动范式

当系统开始需要多步推进任务，而不是单次回答时，就该进入 Agent 学习。Agent 关注的是：如何理解目标、如何规划下一步、何时调用工具、何时检索知识、如何整合结果。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/agents.md` | `docs/docs/tutorials/agents-zh.md` | Agent 思维、任务推进、执行协作 |

建议实践：构建一个简单任务代理，例如“收集信息 -> 提取关键点 -> 输出报告”。先从单 Agent 开始，不要一开始就进入多 Agent。

### 5.3 MCP：用协议方式接入外部能力

当工具能力不再局限于本地 Java 方法，而是来自外部服务、独立进程或通用工具服务器时，MCP 就非常重要。它帮助你理解“工具能力如何标准化接入”。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/mcp.md` | `docs/docs/tutorials/mcp-zh.md` | 工具协议化接入、外部能力统一连接 |

建议实践：先把 MCP 理解为“外部工具标准接入层”，等你对 Tools 足够熟悉后，再深入实际实现细节。

### 5.4 Skills：组织更高层能力模块

Skills 适合放在 Tools 和 Agent 之后学习。因为只有当你已经理解“单点工具”和“任务型代理”后，才能真正明白 Skill 作为“更高层可复用能力封装”的价值。

| 推荐阅读 | 中文文件 | 重点关注 |
|---|---|---|
| `docs/docs/tutorials/skills.md` | `docs/docs/tutorials/skills-zh.md` | 能力封装、复用、模块化设计 |

建议实践：把“检索 + 总结 + 输出引用答案”视作一个 Skill，尝试从系统架构角度定义它的边界。

## 六、第五阶段：学习多 Agent 与 Subagent

你特别提到“多个 agent 怎么做、subagent 如何用”，这一部分建议放在 **Tools、RAG、单 Agent** 都较熟悉之后再学。否则很容易在概念上堆得很高，但实际上没有抓住重点。

### 6.1 多 Agent 的本质

多 Agent 并不是“把多个模型放在一起”这么简单。它的本质是**职责分工**。当一个单体代理同时负责规划、检索、执行、写作和审校时，系统复杂度会迅速上升。此时，把职责拆给多个代理，会更容易治理。

| Agent 类型 | 典型职责 |
|---|---|
| Planner Agent | 分析目标、拆解步骤、制定执行计划 |
| Retrieval Agent | 负责知识检索、证据收集 |
| Tool Agent | 负责调用外部工具与业务系统 |
| Writer Agent | 负责生成面向用户的结果表达 |
| Reviewer Agent | 负责审校、质检与风险检查 |

### 6.2 Subagent 如何理解

Subagent 可以理解为被上层 Agent 编排调用的下级代理。它并不直接承担完整用户目标，而是围绕某一类子任务提供专门能力。例如，一个主 Agent 在处理“生成市场调研报告”时，可以调用一个专门负责网页检索的 subagent、一个专门负责证据抽取的 subagent，以及一个专门负责成稿的 subagent。

这种模式的好处在于职责清晰、可复用、可替换，也更有利于监控与效果调优。

### 6.3 多 Agent 的学习顺序建议

建议按以下顺序学习，而不要跳步：

| 顺序 | 建议内容 |
|---|---|
| 1 | 单 Agent + Tools |
| 2 | 单 Agent + RAG + Tools |
| 3 | 主 Agent 调用一个专用子能力单元 |
| 4 | 主 Agent + 多个 Subagent 分工协作 |
| 5 | 再进入更复杂的 A2A、MCP、Pattern 化流程 |

### 6.4 什么时候真的需要多 Agent

并不是所有场景都需要多 Agent。只有当单 Agent 已经因为职责过多而难以控制，或者你明确需要规划、执行、审校三类能力分层时，多 Agent 才真正带来收益。对于很多业务项目而言，**一个具备 Tools 和 RAG 的单 Agent** 已经足够解决大量问题。

## 七、示例代码应该去哪里找

官方教程会给出许多方向说明，但真正写代码时，最实用的通常是以下几类位置。

| 位置 | 说明 |
|---|---|
| `docs/docs/tutorials/*.md` | 官方概念教程与入门说明 |
| `docs/docs/integrations/*` | 各模型、向量库、文档源、解析器的具体接入文档 |
| `integration-tests/` | 很多真实可参考的用法会出现在集成测试中 |
| 各模块 `src/test/java` | 查找某项能力的最小可运行示例非常有帮助 |
| 官方 examples 仓库 | 适合寻找更完整的演示项目 |

如果你在学习某一主题时想快速找到代码入口，建议优先使用以下策略：先看对应教程文档中的类名、方法名和组件名，再在仓库里搜索这些关键词，通常能快速定位到测试代码或模块示例。

## 八、建议的学习节奏

为了避免学到一半失焦，建议你采用“每阶段一个可运行结果”的方式推进。也就是说，每学完一个阶段，都要落一个最小项目，而不是只读文档。

| 阶段 | 建议产出 |
|---|---|
| 基础阶段 | 一个最小聊天应用 |
| AI Services 阶段 | 一个接口式聊天助手 |
| Memory / Structured Outputs 阶段 | 一个可多轮对话且能输出对象结果的助手 |
| RAG 阶段 | 一个基于私有文档的问答系统 |
| Tools / Agent 阶段 | 一个可调用业务工具的任务型助手 |
| 多 Agent / Subagent 阶段 | 一个具备职责拆分的复杂任务系统 |

## 九、建议你现在怎么学

如果你的目标是尽快掌握 LangChain4j 并落地业务，我建议你按照下面这条最实用路径来走：先完成 `Get Started`，然后学 `Chat and Language Models`，接着进入 `AI Services`；之后补上 `Chat Memory` 和 `Structured Outputs`；再进入 `RAG`；最后学习 `Tools`、`Agents`、`MCP` 和 `Skills`，并在这个基础上逐步进入多 Agent 与 subagent 设计。

这条路线的好处在于，每一步都建立在前一步之上，不会出现“概念很高级，但缺乏落地抓手”的问题。
