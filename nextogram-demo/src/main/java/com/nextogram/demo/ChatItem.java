package com.nextogram.demo;

import java.util.ArrayList;
import java.util.List;

public class ChatItem {
    private final String id;
    private final String title;
    private final String avatarText;
    private final boolean online;
    private final List<DemoMessage> messages;
    private int unreadCount;
    private boolean pinned;

    public ChatItem(String id, String title, String avatarText, boolean online,
                    int unreadCount, boolean pinned, List<DemoMessage> messages) {
        this.id = id;
        this.title = title;
        this.avatarText = avatarText;
        this.online = online;
        this.unreadCount = unreadCount;
        this.pinned = pinned;
        this.messages = new ArrayList<>(messages);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAvatarText() {
        return avatarText;
    }

    public boolean isOnline() {
        return online;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public List<DemoMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public DemoMessage getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }
}
