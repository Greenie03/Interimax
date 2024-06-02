package com.example.interimax.models;

import java.util.List;

public class Conversation {
    private String conversationId;
    private List<String> participants;
    private String lastMessage;
    private long timestamp;
    private int unreadCount;
    private String userName;
    private String profileImageUrl;

    public Conversation() {
        // Constructeur vide requis pour Firebase
    }

    public Conversation(String conversationId, List<String> participants, String lastMessage, long timestamp, int unreadCount, String userName, String profileImageUrl) {
        this.conversationId = conversationId;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and setters for all fields

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
