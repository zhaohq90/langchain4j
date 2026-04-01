package dev.langchain4j.example;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * RAG (检索增强生成) 示例：展示如何加载文档、向量化并基于文档内容回答问题。
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选)
 */
public class RagExample {

    public static void main(String[] args) {
        // 1. 初始化聊天模型（使用阿里云百炼平台）
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey("sk-sp-064ecd47b4d94efd981296ea84acff75")
                .baseUrl("https://coding.dashscope.aliyuncs.com/v1")
                .modelName("glm-5")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 初始化嵌入模型（用于将文本转换为向量）
        // 使用阿里云的 text-embedding-v3 模型
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey("sk-8c436851e6d544cfae426544c9c8f414")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName("text-embedding-v3")
                .logRequests(true)
                .logResponses(true)
                .build();

        // 3. 初始化向量存储（这里使用内存存储，实际生产可替换为 Milvus, Pinecone 等）
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 4. 准备文档
        String content = "LangChain4j 是一个为 Java 开发者设计的 LLM 框架，支持 RAG、Agent 等高级功能。";
        Document document = Document.from(content);
        
        // 5. 创建 EmbeddingStoreIngestor，用于将文档向量化并存入向量库
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        // 6. 创建 AI 服务（声明式接口）
        interface Assistant {
            String chat(String userMessage);
        }

        // 重要：ContentRetriever 需要同时配置 embeddingStore 和 embeddingModel
        // embeddingModel 用于将用户问题转换为向量进行检索
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingStore(embeddingStore)
                        .embeddingModel(embeddingModel)  // 必须传入 embeddingModel 用于查询向量化
                        .maxResults(3)                   // 返回最相似的 3 个结果
                        .minScore(0.5)                   // 最小相似度阈值
                        .build())
                .build();

        // 7. 提问并获取基于文档的回答
        String question = "LangChain4j 是什么？";
        System.out.println("\n正在向量化文档...");
                
        // 8. 执行向量化并存入向量库（这一步会实际调用 EmbeddingModel）
        var ingestionResult = ingestor.ingest(document);
        System.out.println("✓ 文档向量化完成！");
        System.out.println("  - Token 使用量：" + ingestionResult.tokenUsage());
        System.out.println("  - 向量库中文档数量：1");
        
        // 9. 获取基于文档的回答
        String answer = assistant.chat(question);
        
        System.out.println("\n========================================");
        System.out.println("问题：" + question);
        System.out.println("AI (基于文档): " + answer);
        System.out.println("========================================");
    }
}
