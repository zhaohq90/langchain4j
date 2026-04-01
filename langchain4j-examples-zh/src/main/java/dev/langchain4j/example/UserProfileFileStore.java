package dev.langchain4j.example;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 从文本文件加载用户背景信息的存储实现。
 * 文件格式：每行一个 "键：值" 格式的信息
 * 
 * 示例文件内容 (user-profile.txt)：
 * # 这是注释行
 * 姓名：张三
 * 年龄：30 岁
 * 城市：北京市
 * 职业：软件工程师
 * 爱好：读书、旅行、编程
 */
public class UserProfileFileStore implements ChatMemoryStore {
    
    private final String userProfileFilePath;
    private final Map<Object, List<ChatMessage>> memoryCache = new ConcurrentHashMap<>();
    private boolean profileLoaded = false;
    
    public UserProfileFileStore(String userProfileFilePath) {
        this.userProfileFilePath = userProfileFilePath;
        loadUserProfile();
    }
    
    /**
     * 从文件加载用户档案，并转换为初始的系统消息。
     */
    private void loadUserProfile() {
        Path path = Paths.get(userProfileFilePath);
        
        if (!Files.exists(path)) {
            System.err.println("用户档案文件不存在：" + userProfileFilePath);
            return;
        }
        
        StringBuilder profileBuilder = new StringBuilder();
        profileBuilder.append("用户档案信息：\n");
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // 跳过空行和注释
                }
                profileBuilder.append("- ").append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("读取用户档案失败：" + userProfileFilePath, e);
        }
        
        // 将用户档案作为系统消息存入默认 memory
        String profileText = profileBuilder.toString();
        SystemMessage systemMessage = SystemMessage.from(profileText);
        
        List<ChatMessage> initialMessages = Arrays.asList(systemMessage);
        memoryCache.put("default-user", initialMessages);
        profileLoaded = true;
        
        System.out.println("✓ 已加载用户档案：" + userProfileFilePath);
        System.out.println(profileText);
    }
    
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return memoryCache.computeIfAbsent(memoryId, k -> new ArrayList<>());
    }
    
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        memoryCache.put(memoryId, new ArrayList<>(messages));
    }
    
    @Override
    public void deleteMessages(Object memoryId) {
        memoryCache.remove(memoryId);
    }
    
    /**
     * 检查用户档案是否已成功加载。
     */
    public boolean isProfileLoaded() {
        return profileLoaded;
    }
}
