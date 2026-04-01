package dev.langchain4j.example;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于文件的聊天记忆存储实现。
 * 可以从本地文本文件加载和保存聊天历史。
 * 
 * 文件格式：每行一个 JSON 格式的消息记录
 * 格式：memoryId|messageIndex|jsonContent
 */
public class FileChatMemoryStore implements ChatMemoryStore {
    
    private final String filePath;
    private final Map<Object, List<ChatMessage>> memoryCache = new ConcurrentHashMap<>();
    
    public FileChatMemoryStore(String filePath) {
        this.filePath = filePath;
        loadFromFile();
    }
    
    /**
     * 从文件加载所有聊天记录到内存缓存中。
     */
    private void loadFromFile() {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            // 文件不存在，创建一个空文件
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException("无法创建文件：" + filePath, e);
            }
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    Object memoryId = parts[0];
                    String jsonContent = parts[2];
                    
                    try {
                        ChatMessage message = ChatMessageDeserializer.messageFromJson(jsonContent);
                        memoryCache.computeIfAbsent(memoryId, k -> new ArrayList<>()).add(message);
                    } catch (Exception e) {
                        System.err.println("解析消息失败：" + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("从文件加载失败：" + filePath, e);
        }
    }
    
    /**
     * 将当前缓存保存到文件。
     */
    private void saveToFile() {
        Path path = Paths.get(filePath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Map.Entry<Object, List<ChatMessage>> entry : memoryCache.entrySet()) {
                Object memoryId = entry.getKey();
                List<ChatMessage> messages = entry.getValue();
                
                for (int i = 0; i < messages.size(); i++) {
                    String json = ChatMessageSerializer.messageToJson(messages.get(i));
                    writer.write(memoryId + "|" + i + "|" + json);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("保存到文件失败：" + filePath, e);
        }
    }
    
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return memoryCache.computeIfAbsent(memoryId, k -> new ArrayList<>());
    }
    
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        memoryCache.put(memoryId, new ArrayList<>(messages));
        saveToFile(); // 每次更新都保存到文件
    }
    
    @Override
    public void deleteMessages(Object memoryId) {
        memoryCache.remove(memoryId);
        saveToFile();
    }
    
    /**
     * 手动触发保存（如果需要批量操作后统一保存）。
     */
    public void flush() {
        saveToFile();
    }
}
