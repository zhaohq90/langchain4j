# Agent 工具调用机制详解

## 一、核心原理

LangChain4j 的 Agent 工具调用基于 **Function Calling** 机制，流程如下：

```
用户问题 → AI 模型 → 工具描述列表 → AI 决策 → 返回 tool_calls → 执行工具 → 结果返回 AI → 最终回答
```

**关键点**：`@Tool("描述")` 注解会被转换成 JSON Schema 格式的工具定义，发送给 AI 模型。

---

## 二、工具定义如何发送给 AI

### 代码中的工具定义

```java
static class Calculator {
    @Tool("计算两个数字的和。参数: a-第一个数, b-第二个数")
    public int add(int a, int b) {
        return a + b;
    }

    @Tool("计算两个数字的乘积。参数: a-第一个数, b-第二个数")
    public int multiply(int a, int b) {
        return a * b;
    }
}
```

### 发送给 AI 的 JSON Schema（简化版）

```json
{
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "add",
        "description": "计算两个数字的和。参数: a-第一个数, b-第二个数",
        "parameters": {
          "type": "object",
          "properties": {
            "a": { "type": "integer", "description": "第一个数" },
            "b": { "type": "integer", "description": "第二个数" }
          },
          "required": ["a", "b"]
        }
      }
    },
    {
      "type": "function",
      "function": {
        "name": "multiply",
        "description": "计算两个数字的乘积。参数: a-第一个数, b-第二个数",
        "parameters": {
          "type": "object",
          "properties": {
            "a": { "type": "integer" },
            "b": { "type": "integer" }
          },
          "required": ["a", "b"]
        }
      }
    }
  ]
}
```

**AI 通过 description 字段理解工具的功能，通过 parameters 理解如何调用。**

---

## 三、AI 的决策过程

### 输入

```
用户问题: "请计算 123 加上 456 的结果"
工具定义列表: [add, multiply, subtract, ...]
```

### AI 推理（内部过程）

1. 分析用户问题意图 → 需要做加法运算
2. 匹配工具描述 → `add` 的描述是"计算两个数字的和"
3. 提取参数 → a=123, b=456
4. 返回决策 → 调用 `add(123, 456)`

### AI 返回的 tool_calls

```json
{
  "tool_calls": [
    {
      "id": "call_abc123",
      "type": "function",
      "function": {
        "name": "add",
        "arguments": "{\"a\": 123, \"b\": 456}"
      }
    }
  ]
}
```

---

## 四、多工具场景

当注册多个工具类时，AI 通过描述自动匹配最合适的工具：

```java
Assistant assistant = AiServices.builder(Assistant.class)
    .chatModel(chatModel)
    .tools(new Calculator())      // 数学计算
    .tools(new WeatherService())  // 天气查询
    .tools(new TimeService())     // 时间查询
    .build();
```

| 用户问题 | AI 选择的工具 | 原因 |
|---------|--------------|------|
| "北京天气怎么样？" | `getWeather` | 描述匹配"查询城市天气" |
| "123+456等于多少？" | `add` | 描述匹配"计算两个数字的和" |
| "现在几点了？" | `getCurrentTime` | 描述匹配"获取当前时间" |

---

## 五、完整调用流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                        用户发起请求                              │
│                   "请计算 123 + 456 的结果"                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   LangChain4j 构建请求                           │
│   - 用户消息                                                     │
│   - 工具定义列表 (从 @Tool 注解自动生成 JSON Schema)              │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                     AI 模型处理请求                              │
│   1. 理解用户意图                                                │
│   2. 检查可用工具                                                │
│   3. 决定是否需要调用工具                                        │
│   4. 选择合适的工具 + 提取参数                                   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   AI 返回响应                                    │
│   如果需要工具调用:                                              │
│     - tool_calls: [{name: "add", arguments: {a:123, b:456}}]    │
│   如果不需要工具:                                                │
│     - 直接返回文本回答                                           │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                 LangChain4j 处理响应                             │
│   如果有 tool_calls:                                            │
│     1. 解析工具名称和参数                                        │
│     2. 执行对应的 Java 方法                                      │
│     3. 将结果作为 ToolExecutionResultMessage 加入对话历史        │
│     4. 再次请求 AI（带上工具执行结果）                           │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   AI 生成最终回答                                │
│   基于工具执行结果，生成用户可理解的回答                          │
│   "123 加上 456 的结果是 579"                                    │
└─────────────────────────────────────────────────────────────────┘
```

---

## 六、如何查看 AI 的推理过程

启用日志记录：

```java
OpenAiChatModel chatModel = OpenAiChatModel.builder()
    .apiKey("your-api-key")
    .baseUrl("your-base-url")
    .modelName("your-model")
    .logRequests(true)   // 查看发送给 AI 的完整请求（含工具定义）
    .logResponses(true)  // 查看 AI 的响应（含 tool_calls 决策）
    .build();
```

**日志输出示例**：

```
[Request] {
  "messages": [{"role": "user", "content": "请计算 123+456"}],
  "tools": [...工具定义列表...]
}

[Response] {
  "tool_calls": [{"function": {"name": "add", "arguments": "{\"a\":123,\"b\":456}"}}]
}
```

---

## 七、工具调用的限制

### 1. 模型必须支持 Function Calling

不是所有模型都支持工具调用，需要确认模型能力：
- OpenAI: GPT-3.5-turbo, GPT-4 系列
- DashScope: 需要确认具体模型是否支持
- Ollama: 部分模型支持

### 2. 工具描述要准确

AI 完全依赖 `@Tool("描述")` 来理解工具功能：

```java
// ❌ 不好的描述 - AI 可能无法正确匹配
@Tool("计算")
public int add(int a, int b);

// ✅ 好的描述 - 清晰说明功能
@Tool("计算两个数字的和。参数: a-第一个加数, b-第二个加数")
public int add(int a, int b);
```

### 3. 参数类型限制

支持的参数类型：
- 基本类型: int, long, double, boolean, String
- 集合类型: List, Set, Map
- 自定义对象（需要 Jackson 注解）

---

## 八、示例代码

完整示例请参考：`AgentDetailedExample.java`

运行方式：
```bash
mvn compile exec:java -Dexec.mainClass=dev.langchain4j.example.AgentDetailedExample
```

---

## 九、常见问题

### Q1: AI 没有调用工具，直接回答了？

可能原因：
1. 模型不支持 Function Calling
2. 工具描述不够清晰，AI 无法匹配
3. 用户问题不需要工具就能回答

### Q2: AI 调用了错误的工具？

检查工具描述是否准确，避免描述模糊或有歧义。

### Q3: 工具参数解析错误？

确保参数类型与 AI 返回的 JSON 匹配，复杂对象需要 Jackson 注解。

---

## 十、总结

| 概念 | 说明 |
|-----|------|
| @Tool | 定义工具及其描述 |
| AiServices.tools() | 注册工具实例 |
| JSON Schema | 工具定义的传输格式 |
| tool_calls | AI 的工具调用决策 |
| ToolExecutionResultMessage | 工具执行结果的对话消息 |

**核心要点**：AI 不直接执行代码，而是返回"调用意图"（tool_calls），LangChain4j 解析后执行对应的 Java 方法。