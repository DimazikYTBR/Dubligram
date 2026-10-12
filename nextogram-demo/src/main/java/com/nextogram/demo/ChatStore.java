package com.nextogram.demo;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatStore {
    private static final String PREFERENCES = "nextogram_demo_chats";
    private static final String CHATS_KEY = "chats";
    private final SharedPreferences preferences;

    public ChatStore(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        if (!preferences.contains(CHATS_KEY)) {
            saveChats(createSampleChats());
        }
    }

    public List<ChatItem> getChats() {
        String serialized = preferences.getString(CHATS_KEY, null);
        if (serialized == null) {
            throw new IllegalStateException("Demo chat data is missing.");
        }

        try {
            JSONArray storedChats = new JSONArray(serialized);
            List<ChatItem> chats = new ArrayList<>(storedChats.length());
            for (int i = 0; i < storedChats.length(); i++) {
                chats.add(readChat(storedChats.getJSONObject(i)));
            }
            return chats;
        } catch (JSONException exception) {
            throw new IllegalStateException("Unable to read saved demo chats.", exception);
        }
    }

    public ChatItem getChat(String chatId) {
        for (ChatItem chat : getChats()) {
            if (chat.getId().equals(chatId)) {
                return chat;
            }
        }
        return null;
    }

    public void appendOutgoingMessage(String chatId, String text) {
        List<ChatItem> chats = getChats();
        ChatItem chat = requireChat(chats, chatId);
        List<DemoMessage> messages = chat.getMessages();
        messages.add(new DemoMessage(text, System.currentTimeMillis(), true));
        replaceChat(chats, chat, new ChatItem(chat.getId(), chat.getTitle(), chat.getAvatarText(),
                chat.isOnline(), chat.getUnreadCount(), chat.isPinned(), messages));
        saveChats(chats);
    }

    public void markChatRead(String chatId) {
        List<ChatItem> chats = getChats();
        ChatItem chat = requireChat(chats, chatId);
        chat.setUnreadCount(0);
        saveChats(chats);
    }

    public boolean togglePinned(String chatId) {
        List<ChatItem> chats = getChats();
        ChatItem chat = requireChat(chats, chatId);
        chat.setPinned(!chat.isPinned());
        saveChats(chats);
        return chat.isPinned();
    }

    private static ChatItem requireChat(List<ChatItem> chats, String chatId) {
        for (ChatItem chat : chats) {
            if (chat.getId().equals(chatId)) {
                return chat;
            }
        }
        throw new IllegalArgumentException("Unknown demo chat: " + chatId);
    }

    private static void replaceChat(List<ChatItem> chats, ChatItem oldChat, ChatItem newChat) {
        int index = chats.indexOf(oldChat);
        if (index < 0) {
            throw new IllegalStateException("Demo chat disappeared while updating.");
        }
        chats.set(index, newChat);
    }

    private void saveChats(List<ChatItem> chats) {
        JSONArray serializedChats = new JSONArray();
        try {
            for (ChatItem chat : chats) {
                JSONObject serializedChat = new JSONObject();
                serializedChat.put("id", chat.getId());
                serializedChat.put("title", chat.getTitle());
                serializedChat.put("avatar", chat.getAvatarText());
                serializedChat.put("online", chat.isOnline());
                serializedChat.put("unread", chat.getUnreadCount());
                serializedChat.put("pinned", chat.isPinned());

                JSONArray serializedMessages = new JSONArray();
                for (DemoMessage message : chat.getMessages()) {
                    JSONObject serializedMessage = new JSONObject();
                    serializedMessage.put("text", message.getText());
                    serializedMessage.put("sentAt", message.getSentAt());
                    serializedMessage.put("outgoing", message.isOutgoing());
                    serializedMessages.put(serializedMessage);
                }
                serializedChat.put("messages", serializedMessages);
                serializedChats.put(serializedChat);
            }
        } catch (JSONException exception) {
            throw new IllegalStateException("Unable to serialize demo chats.", exception);
        }

        if (!preferences.edit().putString(CHATS_KEY, serializedChats.toString()).commit()) {
            throw new IllegalStateException("Unable to save demo chats on this device.");
        }
    }

    private static ChatItem readChat(JSONObject serializedChat) throws JSONException {
        JSONArray serializedMessages = serializedChat.getJSONArray("messages");
        List<DemoMessage> messages = new ArrayList<>(serializedMessages.length());
        for (int i = 0; i < serializedMessages.length(); i++) {
            JSONObject serializedMessage = serializedMessages.getJSONObject(i);
            messages.add(new DemoMessage(serializedMessage.getString("text"),
                    serializedMessage.getLong("sentAt"),
                    serializedMessage.getBoolean("outgoing")));
        }
        return new ChatItem(serializedChat.getString("id"), serializedChat.getString("title"),
                serializedChat.getString("avatar"), serializedChat.getBoolean("online"),
                serializedChat.getInt("unread"), serializedChat.getBoolean("pinned"), messages);
    }

    private static List<ChatItem> createSampleChats() {
        long now = System.currentTimeMillis();
        List<ChatItem> chats = new ArrayList<>();
        chats.add(sample("arianna", "Arianna", "A", true, 3, true,
                new DemoMessage("Hi! I can send the revised mockup at 6pm.", now - 18 * 60_000L, false),
                new DemoMessage("That works, thanks!", now - 12 * 60_000L, true)));
        chats.add(sample("lina", "Lina Park", "L", true, 1, true,
                new DemoMessage("The launch assets are ready.", now - 30 * 60_000L, false)));
        chats.add(sample("design-team", "Design Team", "D", false, 12, false,
                new DemoMessage("Sync on the glass motion spec.", now - 90 * 60_000L, false)));
        chats.add(sample("nora", "Nora", "N", true, 0, false,
                new DemoMessage("Let’s review the new app icon too.", now - 3 * 60 * 60_000L, false)));
        chats.add(sample("studio-notes", "Studio Notes", "S", false, 5, false,
                new DemoMessage("Voice note + quick summary attached.", now - 26 * 60 * 60_000L, false)));
        chats.add(sample("travel-club", "Travel Club", "T", false, 0, false,
                new DemoMessage("Room booked for Friday evening.", now - 3 * 24 * 60 * 60_000L, false)));
        return chats;
    }

    private static ChatItem sample(String id, String title, String avatar, boolean online,
                                   int unread, boolean pinned, DemoMessage... messages) {
        List<DemoMessage> messageList = new ArrayList<>();
        Collections.addAll(messageList, messages);
        return new ChatItem(id, title, avatar, online, unread, pinned, messageList);
    }
}
