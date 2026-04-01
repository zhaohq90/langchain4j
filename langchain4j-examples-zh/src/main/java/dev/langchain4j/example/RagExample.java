package dev.langchain4j.example;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * RAG (检索增强生成) 示例：展示如何加载文档、向量化并基于文档内容回答问题。
 * 
 * 运行前请确保设置了环境变量：
 * OPENAI_API_KEY: 你的 API 密钥
 * OPENAI_BASE_URL: (可选)
 */
public class RagExample {

    public static void main(String[] args) {
        // 1. 初始化聊天模型
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .baseUrl(System.getenv("OPENAI_BASE_URL"))
                .build();

        // 2. 初始化嵌入模型 (用于将文本转换为向量)
        // 这里使用本地运行的 ONNX 模型，无需 API Key
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

        // 3. 初始化向量存储 (这里使用内存存储，实际生产可替换为 Milvus, Pinecone 等)
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 4. 加载并导入文档
        // 假设当前目录下有一个 example.txt 文件
        // 如果没有，可以手动创建一个或修改路径
        Path documentPath = Paths.get("example.txt");
        // 模拟创建一个文档内容 (如果文件不存在)
        String content = "LangChain4j 是一个为 Java 开发者设计的 LLM 框架，支持 RAG、Agent 等高级功能。";
        Document document = Document.from(content);
        
        // 将文档切分、向量化并存入向量数据库
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);

        // 5. 创建 AI 服务 (声明式接口)
        interface Assistant {
            String chat(String userMessage);
        }

        Assistant assistant = AiServices.builder(Assistant.class)
               // .chatLanguageModel(chatModel)
                .chatModel(chatModel)
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        // 6. 提问并获取基于文档的回答
        String question = "LangChain4j 是什么？";
        String answer = assistant.chat(question);

        System.out.println("========================================");
        System.out.println("问题: " + question);
        System.out.println("AI (基于文档): " + answer);
        System.out.println("========================================");
    }
}
