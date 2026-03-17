# 聊天模型与语言模型

本章介绍 LangChain4j 中最基础也最重要的一层：**聊天模型与语言模型抽象**。如果你希望真正理解 LangChain4j 的底层工作方式，而不仅仅是调用一个高级封装接口，那么这一章是学习的起点。

在 LangChain4j 中，模型交互并不只是“传一段字符串，返回一段字符串”这么简单。框架把消息、角色、上下文、多模态输入与响应结果组织成统一的数据结构，从而让不同模型供应商之间具备尽可能一致的开发体验。

## 核心概念

LangChain4j 中最常用的底层抽象是 `ChatModel`。它代表一个可以进行对话式交互的模型。开发者通常会使用某个具体供应商的实现类，例如 `OpenAiChatModel`、`AzureOpenAiChatModel`、`OllamaChatModel` 等，但在设计层面，这些实现都建立在统一的聊天模型抽象之上。

与模型交互时，你需要理解以下几个核心对象。

| 概念 | 说明 |
|---|---|
| `ChatModel` | 最基本的聊天能力入口，用于发送消息并获取响应 |
| `UserMessage` | 用户消息，表示应用发给模型的输入 |
| `AiMessage` | 模型返回的消息 |
| `SystemMessage` | 系统级提示消息，用于约束模型行为 |
| `ChatResponse` | 更完整的响应对象，通常包含回复与附加元信息 |

当你只进行最简单的调用时，代码往往像这样：

```java
String answer = model.chat("介绍一下 Java 17 的新特性");
```

这种方式非常直接，适合快速实验。但如果你开始处理多轮对话、系统提示、多模态输入或工具调用，就需要转向更明确的消息对象模型。

## 多轮对话的本质

聊天模型本身通常是**无状态**的。也就是说，模型不会自动记住之前发生过什么。每一次请求中，如果你希望模型知道此前的上下文，就必须把上下文消息一并发送过去。

例如，如果用户先说“我叫 Klaus”，下一轮又问“我叫什么名字”，那么第二次调用必须显式带上第一次的对话消息，否则模型没有上下文可用。

```java
UserMessage firstUserMessage = UserMessage.from("Hello, my name is Klaus");
AiMessage firstAiMessage = model.chat(firstUserMessage).aiMessage();

UserMessage secondUserMessage = UserMessage.from("What is my name?");
AiMessage secondAiMessage = model.chat(firstUserMessage, firstAiMessage, secondUserMessage).aiMessage();
```

这也是后续 **Chat Memory** 存在的原因。因为手工维护每一轮消息会非常繁琐，框架因此提供了专门的记忆能力来管理对话上下文。

## 消息角色与系统提示

在构建实际应用时，除了用户消息与模型回复之外，往往还需要一个系统提示来定义模型的角色、风格与边界。例如，你可以要求模型始终以技术顾问身份回答问题，或者要求它输出更正式、更简短或更结构化的内容。

系统提示的作用，是为后续整段对话提供行为约束。它通常对企业级应用非常关键，因为它决定了模型输出的整体风格与任务边界。

## 多模态输入

LangChain4j 的 `UserMessage` 不仅可以包含纯文本，还可以包含图像、音频、视频或 PDF 等内容。这意味着当底层模型供应商支持多模态时，你可以把文本和媒体内容组合到同一个请求中。

| 内容类型 | 对应类型 |
|---|---|
| 文本 | `TextContent` |
| 图片 | `ImageContent` |
| 音频 | `AudioContent` |
| 视频 | `VideoContent` |
| PDF 文件 | `PdfFileContent` |

下面是一个“文本 + 图片”联合输入的示例：

```java
UserMessage userMessage = UserMessage.from(
    TextContent.from("Describe the following image"),
    ImageContent.from("https://example.com/cat.jpg")
);
ChatResponse response = model.chat(userMessage);
```

在支持视觉能力的模型中，这种调用方式非常适合做图像理解、截图分析、文档识别等任务。

## 为什么先学底层模型抽象

很多初学者会直接跳到 `AI Services`，这当然没有问题，因为它上手更快。但如果你希望后续学习 Tools、RAG、Agent 或多 Agent 系统，那么理解底层模型与消息结构会带来明显收益。

原因在于，几乎所有高级能力最终都建立在这些原语之上。无论是让模型调用工具、在上下文中插入检索结果，还是让多个代理在不同阶段传递消息，本质上都离不开消息、提示、上下文与模型调用这几类基本构件。

## 这一章之后应该学习什么

完成本章后，通常有两条学习路线。

如果你更关注“尽快做出可用功能”，应继续进入 **AI Services**，因为它能以极低的代码量快速构建一个聊天接口。

如果你更关注“做复杂且可控的底层编排”，那么建议同时配合学习 **Chat Memory** 与 **Tools**，以便更早进入真实业务系统的开发模式。
