---
sidebar_position: 32
---
# 技能 (Skills)

:::note
技能 API 仍处于实验阶段。未来的版本中，API 和行为可能仍会发生变化。
:::

**技能（Skills）** 是一种为大型语言模型（LLM）配备可重用、自包含行为指令的机制。一个技能捆绑了名称、简短描述和指令主体（其 *内容*），以及可选的资源（例如，参考资料、资产、模板等）。LLM 会按需加载技能，从而保持初始上下文较小，并且只在实际需要时才拉取详细指令。

:::note
技能是根据 [Agent Skills 规范](https://agentskills.io) 设计的。
:::

## 创建技能

### 从文件系统创建

通常，每个技能都位于其自己的目录中，其中包含一个 `SKILL.md` 文件。该文件必须以 YAML Front Matter 块开头，声明技能的 `name` 和 `description`。Front Matter 下面的所有内容都将成为技能的内容——当 LLM 激活该技能时，提供给 LLM 的指令。

```
skills/
├── docx/
│   ├── SKILL.md
│   └── references/
│       └── tracked-changes.md   ← 作为资源加载
└── data-analysis/
    └── SKILL.md
```

示例 `SKILL.md`：

```markdown
---
name: docx
description: 使用修订模式编辑和审阅 Word 文档
---
当用户要求你编辑 Word 文档时：
1. 始终使用修订模式，以便可以审阅编辑。
   ...
```

技能目录中的任何文件（`SKILL.md` 本身和 `scripts/` 子目录下的文件除外）都会自动作为 `SkillResource` 加载，LLM 可以按需读取。使用 `langchain4j-skills` 模块中的 `FileSystemSkillLoader` 从文件系统加载技能：

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-skills</artifactId>
    <version>1.12.2-beta22</version>
</dependency>
```

```java
// 加载在直接子目录中找到的所有技能：
List<FileSystemSkill> skills = FileSystemSkillLoader.loadSkills(Path.of("skills/"));
// 或者通过其目录加载单个技能：
FileSystemSkill skill = FileSystemSkillLoader.loadSkill(Path.of("skills/docx"));
```

### 以编程方式创建

技能不一定必须基于文件系统。你可以使用构建器 API 从任何来源（数据库、远程 API、运行时生成）创建它们：

```java
Skill skill = Skill.builder()
        .name("incident-response")
        .description("诊断和解决生产事故的逐步操作手册")
        .content("""
                当生产警报触发时：
                1. 调用 `fetchRecentLogs(serviceName)` 检索最近 5 分钟的日志。
                2. 调用 `checkServiceHealth(serviceName)` 获取当前健康指标。
                3. 根据发现，调用 `createIncidentTicket(summary, severity)`。
                4. 如果严重性为 CRITICAL，则同时调用 `pageOnCall(incidentId)`。
                """)
        .build();
```

你也可以以编程方式附加资源：

```java
SkillResource reference = SkillResource.builder()
        .relativePath("references/tone-guide.md")
        .content("使用热情、简洁的语言。避免行话。")
        .build();
```

## Skills 与 Tools/Agents 的关系

Skills 可以被视为 Tools 的一种更高级、更具结构化的封装。它允许你将一组相关的工具、指令和资源打包成一个可复用的单元。当 Agent 需要执行一个复杂任务时，它可以“激活”一个技能，从而获得完成该任务所需的所有上下文和能力。

## 学习建议

*   **理解 Skills 的封装性**：Skills 的核心在于其封装性，它将复杂的行为和知识打包成一个易于管理和复用的单元。
*   **设计可复用的 Skills**：尝试将你的业务逻辑或常见任务抽象为 Skills，以提高 Agent 的模块化和可维护性。
*   **结合 Agent 使用**：Skills 最能发挥其价值的场景是与 Agent 结合使用，让 Agent 能够根据任务动态加载和利用所需的技能。

掌握 Skills 将使你能够构建更智能、更模块化、更易于扩展的 Agent 系统，特别是在处理复杂和多领域的任务时。
