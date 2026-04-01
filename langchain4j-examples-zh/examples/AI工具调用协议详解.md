# AI 工具调用协议详解

## 一、工具调用流程的正确理解

### 用户发起查询天气请求时的完整流程

```
用户请求: "北京天气怎么样？"
    ↓
┌─────────────────────────────────────────────┐
│ LangChain4j 构建请求                         │
│ messages: [UserMessage("北京天气怎么样？")]   │
│ tools: [getWeather 定义, 其他工具...]         │  ← 独立字段，不是提示词的一部分
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│ 大模型完成第一轮推理                          │
│ 判断: 需要调用 getWeather 工具               │
│ 输出: tool_calls 而不是文本                  │  ← 不是"中断推理"，而是正常决策输出
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│ LangChain4j 接收响应                         │
│ 检测到 tool_calls → 解析 name 和 arguments   │
│ 自动查找并执行 getWeather("北京")            │  ← 框架自动执行，无需用户手动调用
│ 结果: "晴天，温度 18°C"                      │
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│ LangChain4j 构建第二轮请求                   │
│ messages = 原消息 + AiMessage(tool_calls)    │
│           + ToolExecutionResultMessage       │  ← 加入对话历史，不是"提示词后面"
└─────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────┐
│ 大模型第二轮推理                             │
│ 输入: 对话历史（含工具结果）                  │
│ 输出: 最终文本回答                           │
│ "北京今天天气晴朗，温度 18°C"                 │
└─────────────────────────────────────────────┘
    ↓
返回给用户
```

### 关键澄清点

| 常见理解 | 正确表述 |
|---------|---------|
| "中断推理" | 不是中断，而是模型完成第一轮推理后，决定输出 `tool_calls` |
| "工具定义作为提示词" | 工具定义是 API 请求的独立 `tools` 字段，不是提示词的一部分 |
| "结果加入提示词后面" | 结果作为 `ToolExecutionResultMessage` 加入对话历史（messages 数组） |

### 消息类型结构

```
messages 数组（对话历史）:
  1. UserMessage: "北京天气怎么样？"
  2. AiMessage (含 tool_calls): {"name": "getWeather", "arguments": {...}}
  3. ToolExecutionResultMessage: "晴天，温度 18°C"  ← 工具执行结果
  4. AiMessage (最终回答): "北京今天天气晴朗，温度 18°C..."
```

### 多轮工具调用场景

复杂任务可能需要多次工具调用：

```
用户: "北京和上海天气对比，然后计算两地温差"

第1轮: AI → tool_calls: getWeather("北京")
       框架执行 → 结果: "18°C"

第2轮: AI → tool_calls: getWeather("上海")
       框架执行 → 结果: "22°C"

第3轮: AI → tool_calls: subtract(22, 18)
       框架执行 → 结果: "4"

第4轮: AI → 最终回答: "北京18°C，上海22°C，温差4°C"
```

---

## 二、tool_calls 协议层次

### 协议架构

`tool_calls` 属于 **AI 服务提供商的应用层 API 协议**，不是 HTTP 协议本身。

```
协议层次：
┌─────────────────────────────────────────┐
│ HTTP 协议（传输层）                       │  ← 标准 RFC 协议
│ - GET/POST 方法                          │
│ - Headers, Body                          │
└─────────────────────────────────────────┘
              ↓ 承载
┌─────────────────────────────────────────┐
│ AI API 协议（应用层）                     │  ← 各厂商自定义
│ - OpenAI Chat Completions API            │
│ - Anthropic Messages API                 │
│ - Google Gemini API                      │
└─────────────────────────────────────────┘
              ↓ 包含
┌─────────────────────────────────────────┐
│ Function Calling 格式                    │  ← 厂商定义的字段结构
│ - tool_calls (OpenAI)                    │
│ - tool_use (Anthropic)                   │
│ - functionCall (Google)                  │
└─────────────────────────────────────────┘
```

### 协议定义方

| 问题 | 答案 |
|-----|------|
| 谁定义的协议 | OpenAI 首先定义 `tools` 和 `tool_calls` 格式，其他厂商为兼容性纷纷支持 |
| 是否事实标准 | ✓ 是，大多数厂商兼容 OpenAI 格式 |
| 框架的作用 | LangChain4j 适配不同厂商协议，让用户用统一 Java API 调用不同模型 |

---

## 三、不同厂商协议对比

### OpenAI 格式（事实标准）

2023年6月 OpenAI 首先发布 Function Calling 功能，成为业界事实标准。

**请求（定义工具）**：
```json
{
  "model": "gpt-4",
  "messages": [{"role": "user", "content": "北京天气"}],
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "getWeather",
        "description": "查询城市天气",
        "parameters": {
          "type": "object",
          "properties": {
            "city": {"type": "string"}
          },
          "required": ["city"]
        }
      }
    }
  ]
}
```

**响应（工具调用决策）**：
```json
{
  "choices": [{
    "message": {
      "role": "assistant",
      "content": null,
      "tool_calls": [
        {
          "id": "call_abc123",
          "type": "function",
          "function": {
            "name": "getWeather",
            "arguments": "{\"city\": \"北京\"}"    // ← JSON 字符串格式
          }
        }
      ]
    }
  }]
}
```

---

### Anthropic Claude 格式（自有协议）

Anthropic 使用不同的字段命名和结构。

**请求**：
```json
{
  "model": "claude-3-5-sonnet-20241022",
  "max_tokens": 1024,
  "messages": [{"role": "user", "content": "北京天气"}],
  "tools": [
    {
      "name": "getWeather",
      "description": "查询城市天气",
      "input_schema": {                           // ← 使用 input_schema
        "type": "object",
        "properties": {
          "city": {"type": "string"}
        },
        "required": ["city"]
      }
    }
  ]
}
```

**响应**：
```json
{
  "content": [
    {
      "type": "tool_use",                         // ← 使用 tool_use
      "id": "toolu_abc123",
      "name": "getWeather",
      "input": {"city": "北京"}                   // ← 直接是 JSON 对象，不是字符串
    }
  ],
  "stop_reason": "tool_use"
}
```

---

### Google Gemini 格式（自有协议）

Google 使用 `functionDeclarations` 和 `functionCall`。

**请求**：
```json
{
  "contents": [{"parts": [{"text": "北京天气"}]}],
  "tools": {
    "functionDeclarations": [                     // ← 使用 functionDeclarations
      {
        "name": "getWeather",
        "description": "查询城市天气",
        "parameters": {
          "type": "object",
          "properties": {
            "city": {"type": "string"}
          },
          "required": ["city"]
        }
      }
    ]
  }
}
```

**响应**：
```json
{
  "candidates": [{
    "content": {
      "parts": [
        {
          "functionCall": {                       // ← 使用 functionCall
            "name": "getWeather",
            "args": {"city": "北京"}
          }
        }
      ]
    }
  }]
}
```

---

### 协议差异对比表

| 字段 | OpenAI | Claude | Gemini |
|-----|--------|--------|--------|
| 工具定义字段 | `tools` | `tools` | `tools.functionDeclarations` |
| 工具参数定义 | `parameters` (JSON Schema) | `input_schema` | `parameters` |
| 工具调用类型 | `tool_calls` | `tool_use` | `functionCall` |
| 参数格式 | `arguments` (JSON 字符串) | `input` (JSON 对象) | `args` (JSON 对象) |
| 调用 ID 前缀 | `call_xxx` | `toolu_xxx` | 无特定格式 |
| 消息结构 | `choices[].message` | `content[]` | `candidates[].content.parts[]` |

---

## 四、厂商兼容性现状

| 厂商 | 是否支持 OpenAI 格式 | 备注 |
|-----|---------------------|------|
| OpenAI | ✓ 原生定义 | `tools` + `tool_calls` |
| Azure OpenAI | ✓ 完全兼容 | 与 OpenAI 一致 |
| DashScope (阿里) | ✓ 兼容 | 支持 OpenAI 格式 |
| Moonshot (月之暗面) | ✓ 兼容 | 支持 OpenAI 格式 |
| DeepSeek | ✓ 兼容 | 支持 OpenAI 格式 |
| 智谱 GLM | ✓ 兼容 | 支持 OpenAI 格式 |
| Ollama | ✓ 兼容 | 支持 OpenAI 格式 |
| Anthropic Claude | ✗ 自有格式 | `tool_use` 格式不同 |
| Google Gemini | ✗ 自有格式 | `functionCall` 格式不同 |

---

## 五、LangChain4j 适配层设计

LangChain4j 通过适配层统一处理不同厂商协议：

```
┌────────────────────────────────────────────────────────────┐
│                    用户代码层                               │
│   @Tool("描述") public int add(int a, int b)               │
└────────────────────────────────────────────────────────────┘
                           ↓
┌────────────────────────────────────────────────────────────┐
│                 LangChain4j 统一抽象层                      │
│   ToolExecution, ToolExecutionRequest, ToolSpecification   │
└────────────────────────────────────────────────────────────┘
                           ↓
┌────────────────────────────────────────────────────────────┐
│                    各厂商适配层                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │ OpenAI      │ │ Anthropic   │ │ Gemini      │          │
│  │ Adapter     │ │ Adapter     │ │ Adapter     │          │
│  │ tool_calls  │ │ tool_use    │ │ functionCall│          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└────────────────────────────────────────────────────────────┘
                           ↓
┌────────────────────────────────────────────────────────────┐
│                    HTTP 传输层                              │
│   发送各厂商特定格式的 JSON 请求                             │
└────────────────────────────────────────────────────────────┘
```

**核心价值**：
- 用户只写 `@Tool` 注解的 Java 方法
- LangChain4j 自动转换为对应厂商的协议格式
- 切换模型时无需修改业务代码

---

## 六、OpenAI 格式成为事实标准的原因

| 原因 | 说明 |
|-----|------|
| 先发优势 | 2023年最早提出 Function Calling，生态最先成熟 |
| API 简洁 | 结构清晰，易于理解和实现 |
| SDK 生态 | 各语言 SDK 都优先支持 OpenAI 格式 |
| 兼容成本低 | 其他厂商只需适配一个格式即可吸引开发者 |
| 用户惯性 | 开发者已习惯 OpenAI 格式，迁移成本高 |

---

## 七、总结

### 工具调用流程要点

1. 工具定义通过 API 独立 `tools` 字段发送，不是提示词的一部分
2. 模型完成推理后输出 `tool_calls`，是正常决策而非"中断"
3. 框架自动解析 `tool_calls` 并执行对应 Java 方法
4. 工具结果作为 `ToolExecutionResultMessage` 加入对话历史
5. 复杂任务可能触发多轮工具调用

### 协议要点

1. 协议是厂商定义的，不是框架定义的
2. OpenAI 格式是当前事实标准，大多数厂商兼容
3. Anthropic 和 Google 使用自有格式
4. LangChain4j 通过适配层屏蔽协议差异