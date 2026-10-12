package com.nextogram.demo;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_CHAT_ID = "com.nextogram.demo.CHAT_ID";

    private ChatStore chatStore;
    private String chatId;
    private MessageAdapter messageAdapter;
    private EditText messageInput;
    private TextView chatStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        chatStore = new ChatStore(this);
        ChatItem chat = chatId == null ? null : chatStore.getChat(chatId);
        if (chat == null) {
            Toast.makeText(this, R.string.chat_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatStore.markChatRead(chatId);
        ((TextView) findViewById(R.id.chatTitle)).setText(chat.getTitle());
        chatStatus = findViewById(R.id.chatStatus);
        updateStatus(chat);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
        messageInput = findViewById(R.id.messageInput);
        ImageButton sendButton = findViewById(R.id.sendButton);
        RecyclerView messagesList = findViewById(R.id.messagesList);
        messagesList.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter();
        messagesList.setAdapter(messageAdapter);
        sendButton.setOnClickListener(view -> sendMessage(messagesList));
        renderMessages(messagesList);
    }

    private void updateStatus(ChatItem chat) {
        chatStatus.setText(chat.isOnline() ? R.string.demo_online : R.string.demo_offline);
        chatStatus.setTextColor(chat.isOnline() ? 0xFFB9E7C8 : 0xFF999999);
    }

    private void sendMessage(RecyclerView messagesList) {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }
        chatStore.appendOutgoingMessage(chatId, text);
        messageInput.setText("");
        renderMessages(messagesList);
    }

    private void renderMessages(RecyclerView messagesList) {
        ChatItem chat = chatStore.getChat(chatId);
        if (chat == null) {
            throw new IllegalStateException("The open demo chat is no longer available.");
        }
        messageAdapter.submitMessages(chat.getMessages());
        if (messageAdapter.getItemCount() > 0) {
            messagesList.scrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }
}
