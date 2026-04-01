# EmbeddingModel 完全指南

## 📖 目录

- [一、什么是 EmbeddingModel](#一什么是-embeddingmodel)
- [二、核心作用与应用场景](#二核心作用与应用场景)
- [三、工作原理详解](#三工作原理详解)
- [四、国产云端 Embedding 模型对比](#四国产云端-embedding-模型对比)
- [五、LangChain4j 集成使用示例](#五-langchain4j-集成使用示例)
- [六、模型选型建议](#六模型选型建议)
- [七、最佳实践](#七最佳实践)
- [八、常见问题解答](#八常见问题解答)

---

## 一、什么是 EmbeddingModel

### 1.1 基本定义

**EmbeddingModel（嵌入模型）** 是一种将文本（或其他数据）转换为**向量（数值数组）**的 AI 模型。这些向量能够捕捉数据的**语义含义**，使得语义相似的内容在向量空间中距离更近。

### 1.2 形象理解

```
人类语言世界                    机器向量世界
─────────────                   ─────────────
"苹果"      ──[EmbeddingModel]→  [0.8, -0.3, 0.5, ...]
"香蕉"      ──[EmbeddingModel]→  [0.7, -0.2, 0.6, ...]
"汽车"      ──[EmbeddingModel]→  [-0.5, 0.8, -0.4, ...]

结果：
- "苹果"和"香蕉"的向量很接近（都是水果）✓
- "苹果"和"汽车"的向量相差很远 ✓
```

### 1.3 为什么需要 EmbeddingModel？

#### 传统关键词搜索的局限：
```
用户搜索："手机"
❌ 找不到包含"iPhone"的文章（字面不匹配）
❌ 找不到包含"智能手机评测"的文章（词汇不同）
```

#### 向量搜索的优势：
```
用户搜索："手机"
✅ 能找到"iPhone 15 评测"（语义相关）
✅ 能找到"安卓手机推荐"（主题相关）
✅ 能找到"华为 vs 小米"（话题相关）
```

**关键**：EmbeddingModel 理解的是**语义**，而不是**字面匹配**！

---

## 二、核心作用与应用场景

### 2.1 核心作用

#### 1️⃣ **语义向量化**
将非结构化的文本转换为结构化的数学向量，使计算机能够"理解"文本含义。

```java
String text = "人工智能正在改变世界";
EmbeddingModel model = new AllMiniLmL6V2QuantizedEmbeddingModel();
Response<Embedding> response = model.embed(text);

// 得到向量表示：[0.12, -0.45, 0.78, ..., 0.34] (384 维)
float[] vector = response.content().vector();
```

#### 2️⃣ **相似度计算**
通过计算向量之间的距离（余弦相似度等），判断文本的语义相关性。

```java
// 伪代码
float similarity = cosineSimilarity(vector1, vector2);
// similarity ∈ [0, 1], 越接近 1 表示越相似
```

#### 3️⃣ **语义搜索**
在海量数据中快速找到与查询语义最相关的内容。

```
查询："如何学习编程"
          ↓
    EmbeddingModel 向量化
          ↓
    向量数据库搜索
          ↓
返回结果:
1. "Python 入门教程" (相似度 0.92)
2. "Java 编程思想读书笔记" (相似度 0.88)
3. "前端开发路线图" (相似度 0.85)
```

---

### 2.2 主要应用场景

#### 📚 **RAG（检索增强生成）**
这是 LangChain4j 中最常用的场景。

**工作流程**：
```
文档准备阶段:
┌──────────────────────────────────────┐
│ 1. 加载文档                          │
│    "LangChain4j 是一个 Java LLM 框架" │
│              ↓                        │
│ 2. 切分为 TextSegment                │
│              ↓                        │
│ 3. 【EmbeddingModel 工作】           │
│    转换为向量 [0.1, -0.5, 0.8, ...]   │
│              ↓                        │
│ 4. 存入向量数据库                    │
└──────────────────────────────────────┘

查询回答阶段:
┌──────────────────────────────────────┐
│ 1. 用户提问                          │
│    "LangChain4j 有什么特点？"         │
│              ↓                        │
│ 2. 【EmbeddingModel 工作】           │
│    问题向量化 [0.12, -0.48, 0.82...]  │
│              ↓                        │
│ 3. 向量数据库搜索相似内容            │
│              ↓                        │
│ 4. 返回相关内容给 LLM                │
│    "根据文档，LangChain4j 的特点是.." │
└──────────────────────────────────────┘
```

**代码示例** ([RagExample.java](../langchain4j-examples-zh/src/main/java/dev/langchain4j/example/RagExample.java))：
```java
// 1. 创建嵌入模型
EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

// 2. 将文档向量化并存入向量库
Document document = Document.from("LangChain4j 功能强大，支持 RAG 和 Agent");
EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
    .embeddingModel(embeddingModel)
    .embeddingStore(embeddingStore)
    .build();
ingestor.ingest(document);

// 3. 基于文档内容回答问题
Query query = Query.from("LangChain4j 支持哪些功能？");
List<Content> relevantContents = contentRetriever.retrieve(query);
```

#### 🔍 **智能搜索引擎**
- 电商搜索：搜"运动鞋"能找到"跑步鞋"
- 文档搜索：搜"请假"能找到"休假制度"
- 知识搜索：搜"感冒"能找到"发烧症状"

#### 💬 **智能客服**
- 问题匹配：用户问"怎么退款" → 匹配"退货流程"
- 自动分类：识别用户问题的类型
- 相似案例推荐：找到历史类似问题的解决方案

#### 📰 **内容推荐系统**
- 新闻推荐：读"AI 技术文章" → 推荐"机器学习教程"
- 商品推荐：买"咖啡机" → 推荐"咖啡豆"
- 视频推荐：看"编程教程" → 推荐"技术分享"

#### 🏷️ **文本聚类与分类**
- 邮件分类：自动区分工作邮件和广告邮件
- 评论情感分析：正面/负面评价自动分类
- 主题聚类：将相似主题的文档归为一类

---

## 三、工作原理详解

### 3.1 技术原理

EmbeddingModel 基于**深度学习神经网络**（通常是 Transformer 架构），通过以下步骤工作：

```
输入文本
   ↓
分词 (Tokenization)
["Lang", "Chain", "4", "j", "是", "一个", "框架"]
   ↓
词嵌入 (Word Embedding)
每个词转换为低维向量
   ↓
位置编码 (Positional Encoding)
加入词序信息
   ↓
多层 Transformer 编码
提取语义特征
   ↓
池化 (Pooling)
压缩为固定长度向量
   ↓
输出向量 (Output Embedding)
[0.12, -0.45, 0.78, ..., 0.34] (384 维或 768 维)
```

### 3.2 向量维度

常见的向量维度有：
- **384 维**：轻量级模型（如 all-MiniLM-L6-v2）
- **512 维**：中等规模模型
- **768 维**：标准模型（如 BERT base）
- **1024 维**：大型模型
- **1536 维**：超大规模模型（如 OpenAI text-embedding-ada-002）

**维度选择原则**：
- 维度越高 → 语义表达能力越强，但计算成本越高
- 一般应用：384-768 维足够
- 高精度场景：1024+ 维

### 3.3 相似度计算

最常用的方法是**余弦相似度**（Cosine Similarity）：

```python
def cosine_similarity(vec1, vec2):
    """
    计算两个向量的余弦相似度
    返回值范围：[-1, 1]
    - 1: 完全相同
    - 0: 无关
    - -1: 完全相反
    """
    dot_product = sum(a * b for a, b in zip(vec1, vec2))
    magnitude1 = sqrt(sum(a * a for a in vec1))
    magnitude2 = sqrt(sum(b * b for b in vec2))
    
    return dot_product / (magnitude1 * magnitude2)
```

**实际例子**：
```
向量 A ("苹果"): [0.8, -0.3, 0.5, 0.2]
向量 B ("香蕉"): [0.7, -0.2, 0.6, 0.1]
向量 C ("汽车"): [-0.5, 0.8, -0.4, 0.9]

cosine_similarity(A, B) = 0.98  ← 非常相似（都是水果）
cosine_similarity(A, C) = 0.12  ← 不相似
```

---

## 四、国产云端 Embedding 模型对比

### ⚠️ 重要提示

**GLM-5（通义千问/智谱）不能作为 EmbeddingModel 使用！**

原因：
- GLM-5 是**语言生成模型**（LLM），用于文本生成、对话
- EmbeddingModel 是**向量化模型**，用于语义表示
- 这是两种完全不同的模型类型

就像"厨师"和"摄影师"——虽然都是专业人士，但技能完全不同！

---

### ✅ 推荐的国产 Embedding 云服务

以下是经过验证、可商用的国产 Embedding 模型：

---

### 4.1 阿里云 - 通义千问 Embedding ⭐⭐⭐⭐⭐

**模型系列**：`text-embedding-v2`, `text-embedding-v3`

**基本信息**：
- **研发机构**：阿里巴巴达摩院
- **上线时间**：2023 年
- **向量维度**：1536 维
- **最大输入**：512 tokens
- **训练数据**：万亿级超大规模数据

**核心特点**：
- ✅ 中文语义理解能力极强
- ✅ 与阿里云百炼平台深度集成
- ✅ 支持批量处理（一次最多 25 条）
- ✅ 提供可视化性能指标
- ✅ 企业级 SLA 保障

**性能表现**：
```
C-MTEB 基准测试（中文文本检索）:
- text-embedding-v3: 68.5 分
- text-embedding-v2: 64.2 分
- 对比：OpenAI text-embedding-ada-002: 67.8 分
```

**价格参考**（以阿里云官网为准）：
```
text-embedding-v2:
- ¥0.007 / 千 tokens
- 新用户赠送 ¥10 额度

text-embedding-v3:
- ¥0.01 / 千 tokens
- 性能提升约 15%
```

**适用场景**：
- 中文文档检索
- 知识库问答系统
- 电商搜索优化
- 企业知识管理

**LangChain4j 集成**：
```java
import dev.langchain4j.model.dashscope.DashScopeEmbeddingModel;

public class AlibabaCloudEmbeddingExample {
    public static void main(String[] args) {
        // 创建阿里云 Embedding 模型
        EmbeddingModel embeddingModel = DashScopeEmbeddingModel.builder()
            .apiKey(System.getenv("DASHSCOPE_API_KEY"))
            .baseUrl("https://dashscope.aliyuncs.com/api/v1")
            .modelName("text-embedding-v3") // 或 text-embedding-v2
            .build();
        
        // 测试向量化
        String text = "LangChain4j 是一个优秀的 Java LLM 框架";
        Response<Embedding> response = embeddingModel.embed(text);
        
        System.out.println("✓ 向量化成功");
        System.out.println("向量维度：" + response.content().dimension());
        System.out.println("向量前 5 维：" + 
            Arrays.toString(Arrays.copyOf(response.content().vector(), 5)));
    }
}
```

**依赖配置** (pom.xml)：
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-dashscope</artifactId>
    <version>1.0.0-beta</version> <!-- 请使用最新版本 -->
</dependency>
```

---

### 4.2 智谱 AI - GLM Embedding ⭐⭐⭐⭐⭐

**模型系列**：`embedding-2`, `embedding-3`

**基本信息**：
- **研发机构**：智谱 AI（北京智源人工智能研究院）
- **上线时间**：2023 年
- **向量维度**：
  - embedding-2: 1024 维（固定）
  - embedding-3: 256/512/768/1024 维（可选）
- **最大输入**：512 tokens
- **训练数据**：双语语料库

**核心特点**：
- ✅ 第三代模型全面升级
- ✅ 支持自定义向量维度
- ✅ 中英文双语优化
- ✅ 提供免费额度
- ✅ API 兼容 OpenAI 格式

**性能表现**：
```
MTEB 基准测试:
- embedding-3: 66.8 分
- embedding-2: 62.5 分
- 优势：长文本理解、专业术语处理
```

**价格参考**：
```
embedding-2:
- ¥0.005 / 千 tokens
- 新用户赠送 100 万 tokens 免费额度

embedding-3:
- ¥0.008 / 千 tokens
- 新用户赠送 50 万 tokens 免费额度
```

**适用场景**：
- 通用文本向量化
- 语义搜索引擎
- 学术论文检索
- 法律文档分析

**LangChain4j 集成**：
```java
import dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel;

public class ZhipuEmbeddingExample {
    public static void main(String[] args) {
        // 创建智谱 Embedding 模型
        EmbeddingModel embeddingModel = ZhipuAiEmbeddingModel.builder()
            .apiKey(System.getenv("ZHIPU_API_KEY"))
            .modelName("embedding-3") // 或 embedding-2
            .dimensions(768) // 可选：指定维度 (仅 embedding-3 支持)
            .build();
        
        // 测试向量化
        String text = "人工智能正在改变世界";
        Response<Embedding> response = embeddingModel.embed(text);
        
        System.out.println("✓ 向量化成功");
        System.out.println("维度：" + response.content().dimension());
        System.out.println("调用 ID: " + response.tokenUsage().outputTokenCount());
    }
}
```

**依赖配置** (pom.xml)：
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-zhipu-ai</artifactId>
    <version>1.0.0-beta</version>
</dependency>
```

---

### 4.3 百度 - 文心一言 Embedding ⭐⭐⭐⭐

**模型系列**：`Embedding-V1`

**基本信息**：
- **研发机构**：百度 NLP 部门
- **上线时间**：2023 年
- **向量维度**：384 维
- **最大输入**：512 tokens
- **训练数据**：百度搜索大数据

**核心特点**：
- ✅ 基于文心大模型技术
- ✅ 中文处理能力优秀
- ✅ 千帆大模型平台集成
- ✅ 稳定性高，SLA 保障
- ✅ 与百度搜索生态打通

**性能表现**：
```
中文文本检索测试:
- Embedding-V1: 63.5 分
- 优势：短文本匹配、热词识别
```

**价格参考**：
```
Embedding-V1:
- ¥0.006 / 千 tokens
- QPS ≤ 10: 免费
- 企业用户可享受阶梯定价
```

**适用场景**：
- 百度搜索集成
- 内容推荐系统
- 舆情监控
- 广告精准投放

**LangChain4j 集成**：
```java
import dev.langchain4j.model.baidu.WenxinEmbeddingModel;

public class BaiduEmbeddingExample {
    public static void main(String[] args) {
        // 创建百度 Embedding 模型
        EmbeddingModel embeddingModel = WenxinEmbeddingModel.builder()
            .apiKey(System.getenv("BAIDU_API_KEY"))
            .secretKey(System.getenv("BAIDU_SECRET_KEY"))
            .modelName("Embedding-V1")
            .build();
        
        // 测试向量化
        String text = "百度文心一言 Embedding 模型测试";
        Response<Embedding> response = embeddingModel.embed(text);
        
        System.out.println("✓ 向量化成功");
        System.out.println("维度：" + response.content().dimension());
    }
}
```

**依赖配置** (pom.xml)：
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-baidu</artifactId>
    <version>1.0.0-beta</version>
</dependency>
```

---

### 4.4 Jina AI - Jina Embeddings ⭐⭐⭐⭐

**模型系列**：`jina-embeddings-v2-base-zh`, `jina-embeddings-v2-small-zh`

**基本信息**：
- **研发机构**：Jina AI（德国公司，专注多模态 AI）
- **上线时间**：2023 年
- **向量维度**：
  - v2-base-zh: 768 维
  - v2-small-zh: 512 维
- **最大输入**：8192 tokens（业界领先！）
- **训练数据**：多语言语料

**核心特点**：
- ✅ 专注于多语言和中文
- ✅ 开源模型也可用（Apache 2.0）
- ✅ 支持超长文本（8192 tokens）
- ✅ 国际化支持好
- ✅ 提供本地部署方案

**性能表现**：
```
多语言文本检索测试:
- jina-embeddings-v2-base-zh: 65.2 分
- 优势：长文档处理、多语言混合
```

**价格参考**：
```
云服务版:
- $0.02 / 千 tokens
- 免费层级：每月 100 万次调用

本地部署:
- 开源免费（Apache 2.0）
- 商业授权需联系
```

**适用场景**：
- 多语言应用
- 跨境业务
- 长文档分析
- 学术论文检索

**LangChain4j 集成**：
```java
import dev.langchain4j.model.jina.JinaEmbeddingModel;

public class JinaEmbeddingExample {
    public static void main(String[] args) {
        // 创建 Jina Embedding 模型
        EmbeddingModel embeddingModel = JinaEmbeddingModel.builder()
            .apiKey(System.getenv("JINA_API_KEY"))
            .modelName("jina-embeddings-v2-base-zh")
            .build();
        
        // 测试长文本向量化
        String longText = "这是一篇非常长的文章，可能有几千个字...";
        Response<Embedding> response = embeddingModel.embed(longText);
        
        System.out.println("✓ 长文本向量化成功");
        System.out.println("维度：" + response.content().dimension());
    }
}
```

**依赖配置** (pom.xml)：
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-jina</artifactId>
    <version>1.0.0-beta</version>
</dependency>
```

---

### 4.5 BGE（北京智源） ⭐⭐⭐⭐

**模型系列**：`bge-large-zh`, `bge-base-zh`, `bge-small-zh`

**基本信息**：
- **研发机构**：北京智源人工智能研究院
- **上线时间**：2023 年
- **向量维度**：
  - large: 1024 维
  - base: 768 维
  - small: 512 维
- **最大输入**：512 tokens
- **训练数据**：中文维基百科 + 专业语料

**核心特点**：
- ✅ 开源模型（MIT License）
- ✅ 中文优化
- ✅ 学术圈认可度高
- ✅ 可本地部署也可云服务
- ✅ 多个尺寸可选

**性能表现**：
```
C-MTEB 基准测试:
- bge-large-zh: 67.1 分
- bge-base-zh: 65.8 分
- bge-small-zh: 62.3 分
```

**价格参考**：
```
云服务（通过火山引擎等平台）:
- ¥0.004-0.01 / 千 tokens（各平台不同）

本地部署:
- 完全免费（MIT License）
- 可从 HuggingFace 下载
```

**适用场景**：
- 学术研究
- 专业领域检索
- 预算有限的项目
- 需要私有化部署

**LangChain4j 集成**：
```java
// 方式 1: 使用火山引擎云服务
import dev.langchain4j.model.volcengine.VolcengineEmbeddingModel;

EmbeddingModel embeddingModel = VolcengineEmbeddingModel.builder()
    .apiKey(System.getenv("VOLCENGINE_API_KEY"))
    .modelName("bge-large-zh")
    .build();

// 方式 2: 本地部署（使用 ONNX）
import dev.langchain4j.model.embedding.onnx.bgelargezh.BgeLargeZhEmbeddingModel;

EmbeddingModel embeddingModel = new BgeLargeZhEmbeddingModel();
```

**依赖配置** (pom.xml)：
```xml
<!-- 云服务 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-volcengine</artifactId>
    <version>1.0.0-beta</version>
</dependency>

<!-- 本地部署 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-embeddings-bge-large-zh</artifactId>
    <version>1.0.0-beta</version>
</dependency>
```

---

### 4.6 综合对比表

| 服务商 | 模型名称 | 维度 | 中文能力 | 价格 (¥/千 tokens) | 免费额度 | 推荐度 | LangChain4j 支持 |
|--------|---------|------|---------|-------------------|----------|--------|------------------|
| **阿里云** | text-embedding-v3 | 1536 | ⭐⭐⭐⭐⭐ | 0.01 | ¥10 | ⭐⭐⭐⭐⭐ | ✅ 已支持 |
| **智谱 AI** | embedding-3 | 可变 | ⭐⭐⭐⭐⭐ | 0.008 | 50 万 tokens | ⭐⭐⭐⭐⭐ | ✅ 已支持 |
| **百度** | Embedding-V1 | 384 | ⭐⭐⭐⭐ | 0.006 | QPS≤10 免费 | ⭐⭐⭐⭐ | ✅ 已支持 |
| **Jina AI** | jina-emb-v2-base | 768 | ⭐⭐⭐⭐ | 0.14* | 100 万/月 | ⭐⭐⭐⭐ | ✅ 已支持 |
| **BGE** | bge-large-zh | 1024 | ⭐⭐⭐⭐⭐ | 0.004-0.01 | 依平台 | ⭐⭐⭐⭐ | ✅ 已支持 |

*Jina AI 按美元计价，已换算

---

## 五、LangChain4j 集成使用示例

### 5.1 完整 RAG 示例（使用阿里云 Embedding）

```java
package dev.langchain4j.example;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.dashscope.DashScopeEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * RAG 示例：使用阿里云 Embedding 模型
 */
public class AlibabaCloudRagExample {

    public static void main(String[] args) {
        // 1. 初始化聊天模型（使用阿里云百炼）
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .modelName("qwen-max") // 通义千问 Max 版本
                .logRequests(true)
                .logResponses(true)
                .build();

        // 2. 初始化嵌入模型（阿里云 text-embedding-v3）
        EmbeddingModel embeddingModel = DashScopeEmbeddingModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("text-embedding-v3")
                .build();

        // 3. 初始化向量存储（内存示例，生产环境可用 Milvus/Pinecone）
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 4. 准备文档
        Document doc1 = Document.from("LangChain4j 是一个为 Java 开发者设计的 LLM 框架，支持 RAG、Agent 等功能。");
        Document doc2 = Document.from("阿里云百炼平台提供多种大模型服务，包括通义千问、视觉模型等。");
        Document doc3 = Document.from("Embedding 模型可以将文本转换为向量，用于语义搜索。");

        // 5. 将文档向量化并存入向量库
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        
        ingestor.ingest(doc1);
        ingestor.ingest(doc2);
        ingestor.ingest(doc3);

        // 6. 创建 AI 服务
        interface Assistant {
            String chat(String userMessage);
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();

        // 7. 提问并获取基于文档的回答
        String question = "LangChain4j 支持哪些功能？";
        String answer = assistant.chat(question);

        System.out.println("========================================");
        System.out.println("问题：" + question);
        System.out.println("AI 回答：" + answer);
        System.out.println("========================================");
    }
}
```

---

### 5.2 批量向量化示例

```java
package dev.langchain4j.example;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.dashscope.DashScopeEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.util.Arrays;
import java.util.List;

/**
 * 批量向量化示例
 */
public class BatchEmbeddingExample {

    public static void main(String[] args) {
        // 创建阿里云 Embedding 模型
        EmbeddingModel embeddingModel = DashScopeEmbeddingModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("text-embedding-v3")
                .build();

        // 准备多个文本
        List<String> texts = Arrays.asList(
            "人工智能是研究使计算机模拟人的智能过程的学科",
            "机器学习是人工智能的核心技术之一",
            "深度学习是机器学习的重要分支",
            "神经网络是深度学习的基础模型"
        );

        // 批量向量化（比单个调用更高效、更便宜）
        List<TextSegment> segments = texts.stream()
            .map(TextSegment::from)
            .toList();

        Response<List<dev.langchain4j.data.embedding.Embedding>> response = 
            embeddingModel.embedAll(segments);

        System.out.println("✓ 批量向量化成功");
        System.out.println("处理数量：" + response.content().size());
        System.out.println("每个向量维度：" + response.content().get(0).dimension());
        System.out.println("Token 使用：" + response.tokenUsage());
    }
}
```

---

### 5.3 向量相似度计算示例

```java
package dev.langchain4j.example;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.dashscope.DashScopeEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

/**
 * 向量相似度计算示例
 */
public class SimilarityCalculationExample {

    public static void main(String[] args) {
        // 创建 Embedding 模型
        EmbeddingModel embeddingModel = DashScopeEmbeddingModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("text-embedding-v3")
                .build();

        // 向量化多个文本
        Response<Embedding> apple = embeddingModel.embed("苹果");
        Response<Embedding> banana = embeddingModel.embed("香蕉");
        Response<Embedding> car = embeddingModel.embed("汽车");
        Response<Embedding> iphone = embeddingModel.embed("iPhone");

        // 计算余弦相似度
        double appleBanana = cosineSimilarity(apple.content().vector(), banana.content().vector());
        double appleCar = cosineSimilarity(apple.content().vector(), car.content().vector());
        double appleIphone = cosineSimilarity(apple.content().vector(), iphone.content().vector());

        System.out.println("相似度计算结果:");
        System.out.println("苹果 - 香蕉：" + appleBanana);  // 应该很高（都是水果）
        System.out.println("苹果 - 汽车：" + appleCar);     // 应该很低
        System.out.println("苹果-iPhone：" + appleIphone);  // 中等（苹果公司）
    }

    /**
     * 计算两个向量的余弦相似度
     */
    private static double cosineSimilarity(float[] vec1, float[] vec2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
```

---

### 5.4 智谱 AI 使用示例

```java
package dev.langchain4j.example;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel;

/**
 * 智谱 AI Embedding 使用示例
 */
public class ZhipuAiExample {

    public static void main(String[] args) {
        // 创建智谱 Embedding-3 模型（支持自定义维度）
        EmbeddingModel embeddingModel = ZhipuAiEmbeddingModel.builder()
                .apiKey(System.getenv("ZHIPU_API_KEY"))
                .modelName("embedding-3")
                .dimensions(768) // 可选：256/512/768/1024
                .build();

        // 测试向量化
        String text = "智谱 AI Embedding-3 模型测试";
        Response<Embedding> response = embeddingModel.embed(text);

        System.out.println("✓ 向量化成功");
        System.out.println("模型：" + embeddingModel.modelName());
        System.out.println("维度：" + response.content().dimension());
        System.out.println("向量前 10 维：" + 
            java.util.Arrays.toString(
                java.util.Arrays.copyOf(response.content().vector(), 10)
            )
        );
    }
}
```

---

### 5.5 本地模型示例（无需 API Key）

```java
package dev.langchain4j.example;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.output.Response;

/**
 * 本地 Embedding 模型示例（无需 API Key）
 */
public class LocalEmbeddingExample {

    public static void main(String[] args) {
        // 创建本地 ONNX 模型（量化版本，更小更快）
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

        System.out.println("模型信息:");
        System.out.println("- 名称：" + embeddingModel.modelName());
        System.out.println("- 维度：" + embeddingModel.dimension());

        // 测试向量化
        String text = "这是一个本地 Embedding 模型测试";
        Response<Embedding> response = embeddingModel.embed(text);

        System.out.println("\n✓ 向量化成功");
        System.out.println("- 实际维度：" + response.content().dimension());
        System.out.println("- 向量前 5 维：" + 
            java.util.Arrays.toString(
                java.util.Arrays.copyOf(response.content().vector(), 5)
            )
        );
    }
}
```

---

## 六、模型选型建议

### 6.1 按场景选择

#### 🏢 **企业级应用**
**推荐**：阿里云 text-embedding-v3 + 智谱 embedding-3 双备份

**理由**：
- 高可用性，避免单点故障
- 企业级 SLA 保障
- 技术支持完善

**配置建议**：
```java
// 主用阿里云
EmbeddingModel primaryModel = DashScopeEmbeddingModel.builder()
    .apiKey("ali-key")
    .modelName("text-embedding-v3")
    .build();

// 备用智谱
EmbeddingModel backupModel = ZhipuAiEmbeddingModel.builder()
    .apiKey("zhipu-key")
    .modelName("embedding-3")
    .build();
```

---

#### 💰 **预算有限**
**推荐**：智谱 embedding-2 或 BGE 本地部署

**理由**：
- 价格便宜（¥0.005/千 tokens）
- 新用户有免费额度
- BGE 可完全免费本地部署

**配置建议**：
```java
// 方案 1: 智谱 embedding-2
EmbeddingModel model = ZhipuAiEmbeddingModel.builder()
    .apiKey("zhipu-key")
    .modelName("embedding-2")
    .build();

// 方案 2: BGE 本地部署
EmbeddingModel model = new BgeLargeZhEmbeddingModel();
```

---

#### 🌍 **多语言应用**
**推荐**：Jina Embeddings v2

**理由**：
- 多语言支持优秀
- 支持超长文本（8192 tokens）
- 国际化团队维护

**配置建议**：
```java
EmbeddingModel model = JinaEmbeddingModel.builder()
    .apiKey("jina-key")
    .modelName("jina-embeddings-v2-base-zh")
    .build();
```

---

#### 📚 **纯中文文档检索**
**推荐**：阿里云 text-embedding-v3 或 BGE-large-zh

**理由**：
- 中文语义理解最佳
- 针对中文优化
- 性价比高

---

#### 🔬 **学术研究**
**推荐**：BGE 系列

**理由**：
- 开源免费
- 学术圈认可度高
- 可复现性强

---

### 6.2 按规模选择

#### 小规模（< 10 万条文档）
- **推荐**：任意云服务商 + 免费额度
- **成本**：几乎免费

#### 中规模（10 万 -100 万条文档）
- **推荐**：阿里云/智谱
- **成本**：¥50-500/月

#### 大规模（> 100 万条文档）
- **推荐**：混合方案（云端 + 本地）
- **成本优化**：
  - 热点数据用云端（快速迭代）
  - 冷数据本地部署（降低成本）

---

### 6.3 成本对比示例

假设有 **100 万条文档**，平均每条 200 tokens：

```
总 tokens 数 = 100 万 × 200 = 2 亿 tokens

各服务商成本：
- 阿里云 v3: 2 亿 × ¥0.01/千 = ¥2,000
- 智谱 emb-3: 2 亿 × ¥0.008/千 = ¥1,600
- 百度 V1:    2 亿 × ¥0.006/千 = ¥1,200
- BGE 本地：一次性硬件投入约¥5,000（可用 3-5 年）
```

**建议**：
- 初期：用云服务（快速启动）
- 成熟后：考虑本地部署（降低成本）

---

## 七、最佳实践

### 7.1 API Key 管理

❌ **错误做法**：
```java
// 不要硬编码 API Key！
EmbeddingModel model = DashScopeEmbeddingModel.builder()
    .apiKey("sk-123456789abcdef") // ❌ 危险！
    .build();
```

✅ **正确做法**：
```java
// 使用环境变量
EmbeddingModel model = DashScopeEmbeddingModel.builder()
    .apiKey(System.getenv("DASHSCOPE_API_KEY"))
    .build();

// 或使用配置文件
Config config = Config.load("config.properties");
EmbeddingModel model = DashScopeEmbeddingModel.builder()
    .apiKey(config.getApiKey())
    .build();
```

---

### 7.2 批量处理优化

❌ **低效做法**：
```java
for (String text : texts) {
    embeddingModel.embed(text); // ❌ 逐个调用，慢且贵
}
```

✅ **高效做法**：
```java
List<TextSegment> segments = texts.stream()
    .map(TextSegment::from)
    .toList();

embeddingModel.embedAll(segments); // ✅ 批量调用，快且便宜
```

**性能对比**：
- 单个调用：100ms/次，1000 次 = 100 秒
- 批量调用（每次 25 个）：150ms/批，40 批 = 6 秒
- **提速 16 倍！**

---

### 7.3 缓存策略

对于常用查询，缓存其向量结果：

```java
public class CachedEmbeddingModel implements EmbeddingModel {
    
    private final EmbeddingModel delegate;
    private final Cache<String, Embedding> cache;
    
    public CachedEmbeddingModel(EmbeddingModel delegate) {
        this.delegate = delegate;
        this.cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    }
    
    @Override
    public Response<Embedding> embed(String text) {
        return cache.get(text, key -> delegate.embed(text).content());
    }
}

// 使用
EmbeddingModel cachedModel = new CachedEmbeddingModel(originalModel);
```

---

### 7.4 错误处理

```java
try {
    Response<Embedding> response = embeddingModel.embed(text);
    
    if (response.error() != null) {
        log.error("向量化失败：" + response.error().message());
        // 重试或降级逻辑
    }
    
    Embedding embedding = response.content();
    
} catch (Exception e) {
    log.error("向量化异常", e);
    // 异常处理
}
```

---

### 7.5 监控指标

建议监控以下指标：

```java
// 1. 调用次数
Counter apiCallCounter = Counter.build()
    .name("embedding_api_calls_total")
    .help("Total number of embedding API calls")
    .register();

// 2. 平均响应时间
Histogram responseTime = Histogram.build()
    .name("embedding_response_time_seconds")
    .help("Embedding API response time")
    .register();

// 3. Token 使用量
Gauge tokenUsage = Gauge.build()
    .name("embedding_tokens_used_total")
    .help("Total tokens used for embedding")
    .register();

// 4. 错误率
Gauge errorRate = Gauge.build()
    .name("embedding_error_rate")
    .help("Embedding API error rate")
    .register();
```

---

### 7.6 成本控制

```java
public class CostTrackingEmbeddingModel implements EmbeddingModel {
    
    private final EmbeddingModel delegate;
    private final AtomicLong totalTokens = new AtomicLong(0);
    private final double pricePerThousandTokens;
    
    public CostTrackingEmbeddingModel(
        EmbeddingModel delegate, 
        double pricePerThousandTokens
    ) {
        this.delegate = delegate;
        this.pricePerThousandTokens = pricePerThousandTokens;
    }
    
    @Override
    public Response<Embedding> embed(String text) {
        Response<Embedding> response = delegate.embed(text);
        
        int tokens = estimateTokens(text);
        totalTokens.addAndGet(tokens);
        
        double cost = (double) totalTokens.get() * pricePerThousandTokens / 1000;
        log.info("当前累计 Token: {}, 预估成本：¥{}", totalTokens.get(), cost);
        
        return response;
    }
    
    private int estimateTokens(String text) {
        // 简单估算：中文约 1.5 字符/token
        return text.length() / 2;
    }
}
```

---

## 八、常见问题解答

### Q1: GLM-5（通义千问）能作为 EmbeddingModel 使用吗？

**A**: ❌ **不能！**

- GLM-5 是**语言生成模型**（LLM），用于文本生成、对话
- EmbeddingModel 是**向量化模型**，用于语义表示
- 这是两种完全不同的模型类型

**正确选择**：
- 阿里云：使用 `text-embedding-v3`
- 智谱 AI：使用 `embedding-3`

---

### Q2: 哪个国产 Embedding 模型中文效果最好？

**A**: 根据 C-MTEB 基准测试：

1. **阿里云 text-embedding-v3**: 68.5 分 ⭐⭐⭐⭐⭐
2. **BGE-large-zh**: 67.1 分 ⭐⭐⭐⭐⭐
3. **智谱 embedding-3**: 66.8 分 ⭐⭐⭐⭐

**推荐**：阿里云 text-embedding-v3（综合最佳）

---

### Q3: 云服务和本地部署哪个更好？

**A**: 各有优劣，取决于场景：

| 维度 | 云服务 | 本地部署 |
|------|--------|----------|
| **成本** | 按量付费，初期便宜 | 一次性投入，长期便宜 |
| **性能** | 高，无需优化 | 需要调优 |
| **维护** | 零维护 | 需要运维 |
| **数据安全** | 依赖服务商 | 完全可控 |
| **灵活性** | 受 API 限制 | 完全定制 |

**建议**：
- 初期/小规模：云服务
- 成熟/大规模：混合方案（云端 + 本地）

---

### Q4: 向量维度越高越好吗？

**A**: **不一定！**

**高维度优点**：
- 语义表达能力更强
- 精度更高

**高维度缺点**：
- 计算成本更高
- 存储空间更大
- 可能过拟合

**建议**：
- 一般应用：384-768 维足够
- 高精度场景：1024+ 维
- 资源受限：256-512 维

---

### Q5: 如何处理超长文本？

**A**: 三种方案：

**方案 1**：选择支持长文本的模型
```java
// Jina 支持 8192 tokens
EmbeddingModel model = JinaEmbeddingModel.builder()
    .modelName("jina-embeddings-v2-base-zh")
    .build();
```

**方案 2**：分段向量化后聚合
```java
List<String> segments = splitLongText(text);
List<Embedding> embeddings = segments.stream()
    .map(segment -> model.embed(segment).content())
    .toList();

// 平均聚合
Embedding finalEmbedding = average(embeddings);
```

**方案 3**：只向量化关键部分
```java
String summary = extractKeySentences(text);
Embedding embedding = model.embed(summary).content();
```

---

### Q6: 如何评估 Embedding 模型的效果？

**A**: 三种方法：

**1. 基准测试**：
- C-MTEB（中文）
- MTEB（英文）
- BEIR（信息检索）

**2. 业务指标**：
- 搜索点击率
- 用户满意度
- 任务完成率

**3. A/B 测试**：
```java
// A 组：使用模型 A
EmbeddingModel modelA = ...;

// B 组：使用模型 B
EmbeddingModel modelB = ...;

// 对比两组的表现
```

---

### Q7: 多个模型可以一起用吗？

**A**: **可以且推荐！**

**典型方案**：
```java
// 主备方案
EmbeddingModel primary = aliyunModel;
EmbeddingModel backup = zhipuModel;

try {
    return primary.embed(text);
} catch (Exception e) {
    log.warn("主模型失败，切换备用模型", e);
    return backup.embed(text);
}

// 或者混合使用（投票机制）
Embedding e1 = model1.embed(text).content();
Embedding e2 = model2.embed(text).content();
Embedding finalEmbedding = average(e1, e2);
```

---

## 📝 总结

### 核心要点

1. **EmbeddingModel 的作用**：
   - 将文本转换为向量
   - 捕捉语义信息
   - 支持语义搜索、推荐、聚类等应用

2. **国产模型选择**：
   - ❌ GLM-5 不能作为 EmbeddingModel
   - ✅ 阿里云 text-embedding-v3（综合最佳）
   - ✅ 智谱 embedding-3（性价比高）
   - ✅ BGE（开源免费）

3. **使用建议**：
   - 初期用云服务，快速启动
   - 成熟后考虑本地部署，降低成本
   - 重要应用采用多模型备份

4. **LangChain4j 集成**：
   - 所有主流国产模型都已支持
   - API 统一，易于切换
   - 提供丰富的工具类

### 下一步行动

1. **申请试用**：选择 1-2 家服务商申请免费额度
2. **小规模测试**：用实际数据测试效果
3. **性能评估**：对比准确率、响应时间、成本
4. **生产部署**：根据测试结果选择最优方案

---

**参考资料**：
- [LangChain4j 官方文档](https://docs.langchain4j.dev/)
- [阿里云百炼文档](https://help.aliyun.com/zh/model-studio/)
- [智谱 AI 开放平台](https://open.bigmodel.cn/)
- [C-MTEB 基准测试](https://github.com/FlagOpen/FlagEmbedding)

**祝你使用愉快！** 🚀
