---
sidebar_position: 1
---
# 语言模型集成概览

LangChain4j 提供了与众多大语言模型（LLM）提供商的集成。下表总结了每个集成所支持的功能，包括聊天模型、嵌入模型、多模态输入、工具调用、流式响应、结构化输出等。

| 提供商 | 聊天模型 | 嵌入模型 | 图像生成 | 音频转文本 | 文本转语音 | 支持的多模态输入 | 工具调用 | 流式响应 | 结构化输出 | 思维/推理 |
|---|---|---|---|---|---|---|---|---|---|---|
| [Amazon Bedrock](/integrations/language-models/amazon-bedrock) | ✅ | ✅ | | | | 文本 | ✅ | ✅ | ✅ | |
| [Anthropic](/integrations/language-models/anthropic) | ✅ | | | | | 文本, 图像 | ✅ | ✅ | ✅ | |
| [Azure OpenAI](/integrations/language-models/azure-open-ai) | ✅ | ✅ | ✅ | | | 文本, 图像 | ✅ | ✅ | ✅ | |
| [ChatGLM](/integrations/language-models/chatglm) | ✅ | | | | | 文本 | ✅ | ✅ | ✅ | |
| [DashScope](/integrations/language-models/dashscope) | ✅ | ✅ | ✅ | | | 文本, 图像 | ✅ | ✅ | ✅ | |
| [GitHub Models](/integrations/language-models/github-models) | ✅ | ✅ | | | | 文本 | ✅ | ✅ | ✅ | |
| [Google AI Gemini](/integrations/language-models/google-ai-gemini) | ✅ | ✅ | | | | 文本, 图像 | ✅ | ✅ | ✅ | |
| [Google Vertex AI Anthropic](/integrations/language-models/google-vertex-ai-anthropic) | ✅ | | | | | 文本, 图像 | ✅ | ✅ | ✅ | |
| [Google Vertex AI Gemini](/integrations/language-models/google-vertex-ai-gemini) | ✅ | ✅ | | | | 文本, 图像 | ✅ | ✅ | ✅ | |
| [GPU Llama3 Java](/integrations/language-models/gpullama3-java) | ✅ | | | | | 文本 | | | | |
| [Hugging Face](/integrations/language-models/hugging-face) | ✅ | ✅ | | | | 文本 | | | | |
| [Jlama](/integrations/language-models/jlama) | ✅ | | | | | 文本 | | | | |
| [LocalAI](/integrations/language-models/local-ai) | ✅ | ✅ | | | | 文本 | ✅ | ✅ | ✅ | |
| [Mistral AI](/integrations/language-models/mistral-ai) | ✅ | | | | | 文本 | ✅ | ✅ | ✅ | |
| [Ollama](/integrations/language-models/ollama) | ✅ | ✅ | | | | 文本 | ✅ | ✅ | ✅ | |
| [OpenAI](/integrations/language-models/open-ai) | ✅ | ✅ | ✅ | ✅ | ✅ | 文本, 图像, 音频, PDF | ✅ | ✅ | ✅ | ✅ |
| [OpenAI 兼容](/integrations/language-models/openai-compatible) | ✅ | ✅ | | | | 文本 | ✅ | ✅ | ✅ | 参见 [OpenAI 兼容语言模型](./openai-compatible.md) |
| [Oracle Cloud Infrastructure GenAI](/integrations/language-models/oci-genai) | ✅ | ✅ | | | | 文本, 图像 | | | | |
| [Qianfan](/integrations/language-models/qianfan) | ✅ | ✅ | | | | 文本 | | | | |
| [Cloudflare Workers AI](/integrations/language-models/workers-ai) | | | | | | 文本 | | | | |
| [Zhipu AI](/integrations/language-models/zhipu-ai) | ✅ | ✅ | | | | 文本, 图像 | ✅ | | | |
| [watsonx.ai](/integrations/language-models/watsonx) | ✅ | ✅ | ✅ | | ✅ (Granite) | 文本, 图像 | ✅ | ✅ | | |

图例：
- ✅ 表示“支持”
- 🆘 表示“尚未支持；请帮助我们实现”
- 🔜 表示“正在实现中；请等待”
- ❌ 表示“LLM 提供商不支持”
- 无标记表示“不确定，需要再次确认”
