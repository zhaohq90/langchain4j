---
sidebar_position: 27
---
# MCP (模型上下文协议)

**MCP (Model Context Protocol)** 是一种开放协议，旨在标准化大型语言模型（LLM）与外部工具、服务和系统之间的通信。在 LangChain4j 中，MCP 提供了一种机制，使得 Agent 能够以统一的方式发现、描述和调用各种外部能力，无论这些能力是本地函数、远程 API 还是其他 Agent。

## 为什么需要 MCP

随着 LLM 应用的复杂性增加，Agent 需要与越来越多的外部系统交互。如果没有一个标准化的协议，每个集成都需要定制开发，导致集成成本高、可维护性差。MCP 解决了这些问题：

*   **标准化通信**：提供统一的接口和消息格式，简化 LLM 与外部工具的集成。
*   **能力发现**：允许 Agent 动态发现可用的工具及其功能描述。
*   **跨语言/平台**：协议独立于具体的编程语言和平台，促进异构系统间的互操作性。
*   **可扩展性**：易于添加新的工具和服务，无需修改 Agent 核心逻辑。

## MCP 的核心概念

MCP 的核心在于其定义了一套消息类型和交互模式，使得 Agent 和工具之间能够进行结构化的对话。

| 概念 | 说明 |
|---|---|
| **Agent** | 发送请求并处理响应的实体（通常是 LLM 或其包装器） |
| **Tool** | 接收请求并执行特定操作的外部能力 |
| **消息** | Agent 与 Tool 之间交换的结构化数据，包括请求、响应、事件等 |
| **传输** | 承载 MCP 消息的通信机制，例如 HTTP、WebSocket、STDIO 等 |

## LangChain4j 中的 MCP 实现

LangChain4j 提供了对 MCP 协议的支持，允许你构建与 MCP 兼容的工具和 Agent。这通常涉及：

1.  **实现 MCP 传输**：选择合适的传输方式（如 HTTP、WebSocket）来发送和接收 MCP 消息。
2.  **创建 MCP 工具**：将你的 Java 函数包装成 MCP 兼容的工具，使其能够被远程 Agent 调用。
3.  **配置 MCP Agent**：让你的 Agent 能够通过 MCP 传输发现并调用远程工具。

### 示例：配置 MCP 传输

LangChain4j 提供了多种 MCP 传输实现，例如基于 HTTP 的 `StreamableHttpMcpTransport` 和基于 WebSocket 的 `WebSocketMcpTransport`。

**Streamable HTTP 传输：**

```java
McpTransport transport = StreamableHttpMcpTransport.builder()
        .url("http://localhost:3001/mcp") // MCP 服务器的 POST 端点
        .logRequests(true) // 是否记录请求日志
        .logResponses(true) // 是否记录响应日志
        .build();
```

**_注意:_** `StreamableHttpMcpTransport` 可以选择性地开启一个基于 GET 的 SSE 流，用于接收服务器发起的通知和请求。通过 `.subsidiaryChannel(true)` 启用，默认禁用。如果服务器不支持，传输会记录警告并继续。如果流建立后中断，传输会自动重连（遵循服务器的 `retry` 值，默认为 5 秒）。

**WebSocket 传输：**

```java
McpTransport transport = WebSocketMcpTransport.builder()
        .url("ws://localhost:3001/mcp/ws") // MCP WebSocket 端点
        .logResponses(true)
        .logRequests(true)
        .build();
```

**传统 HTTP 传输：**

对于传统的 HTTP 传输，有两个 URL，一个用于启动 SSE 通道，一个用于通过 `POST` 提交命令。后者由服务器动态提供，前者需要使用 `sseUrl` 方法指定：

```java
McpTransport transport = HttpMcpTransport.builder()
    .sseUrl("http://localhost:3001/sse")
    .logRequests(true) // 如果你想在日志中看到流量
    .logResponses(true)
    .build();
```

**Docker stdio 传输：**

首先，你需要向 `pom.xml` 添加一个模块：

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-mcp-docker</artifactId>
</dependency>
```

然后，你需要创建一个 Docker 传输：

```java
// 示例代码，具体实现请参考官方文档
McpTransport transport = DockerMcpTransport.builder()
    .imageName("my-mcp-tool-image")
    .build();
```

## MCP 与 Tools/Agents 的关系

MCP 可以看作是 Tools 和 Agents 的一个更通用、更解耦的扩展。它允许你的 Agent 不仅仅调用本地 Java 方法，还能通过网络调用任何遵循 MCP 协议的外部服务。这为构建分布式、可扩展的 Agent 系统提供了强大的基础。

## 学习建议

*   **理解协议本身**：MCP 的核心是协议。理解其消息格式和交互模式，有助于你更好地设计和集成 MCP 兼容的工具。
*   **从简单传输开始**：可以从 HTTP 传输开始，逐步过渡到 WebSocket 或其他更复杂的传输方式。
*   **结合实际场景**：思考你的业务中哪些外部服务可以通过 MCP 协议暴露给 Agent，从而实现更强大的自动化能力。

掌握 MCP 将使你的 LangChain4j 应用能够无缝集成各种外部能力，构建出更强大、更灵活的 Agent 系统。
