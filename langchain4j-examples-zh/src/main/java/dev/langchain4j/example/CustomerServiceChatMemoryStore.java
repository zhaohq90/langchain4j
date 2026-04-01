package dev.langchain4j.example;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客服系统专用存储：结合用户档案 + 会话历史记录
 * 
 * 特点：
 * 1. 从文件加载用户静态信息作为系统消息
 * 2. 支持多个独立会话，每个会话有独立的聊天记录
 * 3. 会话记录可选择性地持久化到文件
 * 
 * 适用场景：客服系统、多会话管理等
 */
public class CustomerServiceChatMemoryStore implements ChatMemoryStore {
    
    private final String userProfileFilePath;
    private final Map<Object, List<ChatMessage>> sessionCache = new ConcurrentHashMap<>();
    private SystemMessage userSystemMessage;
    
    /**
     * @param userProfileFilePath 用户档案文件路径
     */
    public CustomerServiceChatMemoryStore(String userProfileFilePath) {
        this.userProfileFilePath = userProfileFilePath;
        loadUserProfile();
    }
    
    /**
     * 从文件加载用户档案。
     */
    private void loadUserProfile() {
        Path path = Paths.get(userProfileFilePath);
        
        if (!Files.exists(path)) {
            System.err.println("⚠️  用户档案文件不存在：" + userProfileFilePath);
            return;
        }
        
        StringBuilder profileBuilder = new StringBuilder();
        profileBuilder.append("【客户信息】\n");
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                profileBuilder.append("- ").append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("读取用户档案失败：" + userProfileFilePath, e);
        }
        
        String profileText = profileBuilder.toString();
        this.userSystemMessage = SystemMessage.from(profileText);
        
        System.out.println("✓ 已加载客户档案：" + userProfileFilePath);
    }
    
    /**
     * 为指定会话创建初始上下文（包含用户档案）。
     * 在会话开始时调用此方法。
     * 
     * @param sessionId 会话 ID
     */
    public void initSession(Object sessionId) {
        if (userSystemMessage != null) {
            List<ChatMessage> initialMessages = new ArrayList<>();
            initialMessages.add(userSystemMessage);
            sessionCache.put(sessionId, initialMessages);
            System.out.println("✓ 已初始化会话：" + sessionId);
        } else {
            sessionCache.put(sessionId, new ArrayList<>());
        }
    }
    
    /**
     * 获取指定会话的所有消息。
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return sessionCache.computeIfAbsent(memoryId, k -> new ArrayList<>());
    }
    
    /**
     * 更新指定会话的消息。
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        sessionCache.put(memoryId, new ArrayList<>(messages));
    }
    
    /**
     * 删除指定会话的所有消息。
     */
    @Override
    public void deleteMessages(Object memoryId) {
        sessionCache.remove(memoryId);
    }
    
    /**
     * 获取当前所有活跃的会话 ID。
     */
    public Set<Object> getAllSessionIds() {
        return sessionCache.keySet();
    }
    
    /**
     * 清除所有会话数据。
     */
    public void clearAllSessions() {
        sessionCache.clear();
    }
}
