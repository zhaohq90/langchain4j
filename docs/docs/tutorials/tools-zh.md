---
sidebar_position: 25
---
# 工具 (Tools)

在 LangChain4j 中，**工具（Tools）** 允许大型语言模型（LLM）与外部世界进行交互。这意味着 LLM 不再仅仅是一个文本生成器，它可以通过调用你提供的函数来执行特定动作，例如查询数据库、调用 API、发送邮件或执行计算。

## 为什么工具很重要

LLM 擅长理解和生成自然语言，但它们本身无法直接执行代码或访问实时信息。工具弥补了这一差距，使得 LLM 能够：

*   **获取实时信息**：例如查询当前天气、最新新闻或股票价格。
*   **执行特定动作**：例如发送电子邮件、更新日历、预订航班或执行复杂的计算。
*   **与外部系统集成**：将 LLM 的智能与你的业务系统（CRM、ERP、数据库等）连接起来。

## 如何定义工具

在 LangChain4j 中，定义工具非常简单。你只需创建一个普通的 Java 类，并在需要暴露给 LLM 的方法上使用 `@Tool` 注解。这些方法可以是静态的，也可以是实例方法。

### 示例：一个简单的工具

假设我们想让 LLM 能够查询一个人的名字长度：

```java
import dev.langchain4j.agent.tool.Tool;

class NameService {

    @Tool("返回给定名称的长度")
    public int getNameLength(String name) {
        return name.length();
    }
}
```

在这个例子中：

*   `@Tool` 注解标记了 `getNameLength` 方法为一个工具。
*   注解中的字符串 `"返回给定名称的长度"` 是工具的描述。这个描述非常重要，因为它会被 LLM 用来理解工具的功能，并决定何时调用它。
*   工具方法的参数（例如 `String name`）也会被 LLM 理解，并用于从用户输入中提取必要的信息。

### 带有参数描述的工具

如果工具方法的参数需要更详细的描述，可以使用 `@P` 注解：

```java
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;

class Calculator {

    @Tool("计算两个数字的和")
    public int add(@P("第一个数字") int a, @P("第二个数字") int b) {
        return a + b;
    }

    @Tool("计算两个数字的差")
    public int subtract(@P("第一个数字") int a, @P("第二个数字") int b) {
        return a - b;
    }
}
```

`@P` 注解中的字符串提供了参数的描述，这有助于 LLM 更准确地理解每个参数的含义。

## 如何使用工具

定义好工具后，你需要将其注册到 `AiServices` 中，以便 LLM 能够发现并使用它们。

```java
interface Assistant {
    String chat(String userMessage);
}

public class Demo {
    public static void main(String[] args) {
        NameService nameService = new NameService();
        Calculator calculator = new Calculator();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(OpenAiChatModel.builder()
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .modelName("gpt-4o-mini")
                        .build())
                .tools(nameService, calculator) // 注册工具
                .build();

        String answer1 = assistant.chat("我的名字叫 Alice，请问我的名字有多长？");
        System.out.println(answer1); // 输出: 你的名字有 5 个字符。

        String answer2 = assistant.chat("请帮我计算 10 加 5 等于多少？");
        System.out.println(answer2); // 输出: 10 加 5 等于 15。
    }
}
```

在这个例子中，`AiServices` 会自动扫描 `nameService` 和 `calculator` 对象中带有 `@Tool` 注解的方法，并将它们暴露给底层 LLM。当用户提出问题时，LLM 会根据工具的描述和参数信息，决定是否调用这些工具来完成任务。

## 工具的进阶用法

*   **并行工具调用**：某些 LLM 支持一次性调用多个工具。LangChain4j 也支持这种能力，允许模型同时执行多个独立的操作。
*   **工具选择**：你可以配置模型在某些情况下强制使用或不使用工具，或者让模型自行决定。
*   **工具与 Agent**：工具是构建 Agent 的基石。Agent 通过组合和编排工具来完成更复杂的、多步骤的任务。

## 学习建议

*   **清晰的工具描述**：为你的工具提供清晰、准确的描述，这是 LLM 正确调用工具的关键。
*   **简洁的工具参数**：设计简洁、易于理解的工具参数，避免过于复杂的输入结构。
*   **错误处理**：考虑工具执行失败时的错误处理机制，并告知 LLM 如何处理这些错误。
*   **安全性**：由于工具可以访问外部系统，请务必谨慎设计和实现工具，确保安全性。

掌握工具的使用，将使你的 LangChain4j 应用从简单的问答系统升级为能够执行实际任务的智能助手，是迈向 Agent 驱动型应用的关键一步。
