package com.nextogram.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ChatAdapter.Listener {

    private RecyclerView chatList;
    private ChatAdapter chatAdapter;
    private ChatStore chatStore;
    private EditText searchInput;
    private TextView emptyState;
    private LinearLayout tabChats;
    private LinearLayout tabContacts;
    private LinearLayout tabCalls;
    private LinearLayout tabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatStore = new ChatStore(this);
        chatList = findViewById(R.id.chatList);
        emptyState = findViewById(R.id.emptyState);
        searchInput = findViewById(R.id.searchInput);
        ImageButton searchButton = findViewById(R.id.searchButton);
        ImageButton topSearchButton = findViewById(R.id.topSearchButton);

        tabChats = findViewById(R.id.tabChats);
        tabContacts = findViewById(R.id.tabContacts);
        tabCalls = findViewById(R.id.tabCalls);
        tabSettings = findViewById(R.id.tabSettings);

        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this);
        chatList.setAdapter(chatAdapter);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                showChats(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bindTab(tabChats);
        tabChats.setOnClickListener(v -> bindTab(tabChats));
        tabContacts.setOnClickListener(v -> showDemoTab(tabContacts));
        tabCalls.setOnClickListener(v -> showDemoTab(tabCalls));
        tabSettings.setOnClickListener(v -> showDemoTab(tabSettings));
        searchButton.setOnClickListener(v -> toggleSearch());
        topSearchButton.setOnClickListener(v -> toggleSearch());
        showChats("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chatStore != null) {
            showChats(searchInput.getText().toString());
        }
    }

    private void toggleSearch() {
        if (searchInput.getVisibility() == View.VISIBLE) {
            searchInput.setText("");
            searchInput.setVisibility(View.GONE);
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        } else {
            searchInput.setVisibility(View.VISIBLE);
            searchInput.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showChats(String query) {
        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        List<ChatItem> filteredChats = new ArrayList<>();
        for (ChatItem chat : chatStore.getChats()) {
            if (matchesQuery(chat, normalizedQuery)) {
                filteredChats.add(chat);
            }
        }
        filteredChats.sort(Comparator
                .comparing(ChatItem::isPinned).reversed()
                .thenComparingLong(chat -> {
                    DemoMessage lastMessage = chat.getLastMessage();
                    return lastMessage == null ? 0L : -lastMessage.getSentAt();
                }));

        chatAdapter.submitList(filteredChats);
        boolean empty = filteredChats.isEmpty();
        chatList.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        emptyState.setText(normalizedQuery.isEmpty()
                ? getString(R.string.empty_chats)
                : getString(R.string.no_search_results, query.trim()));
    }

    private boolean matchesQuery(ChatItem chat, String query) {
        if (query.isEmpty() || chat.getTitle().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        for (DemoMessage message : chat.getMessages()) {
            if (message.getText().toLowerCase(Locale.ROOT).contains(query)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onChatClick(ChatItem chat) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CHAT_ID, chat.getId());
        startActivity(intent);
    }

    @Override
    public void onChatLongClick(ChatItem chat) {
        boolean pinned = chatStore.togglePinned(chat.getId());
        showChats(searchInput.getText().toString());
        Toast.makeText(this, pinned ? R.string.chat_pinned : R.string.chat_unpinned,
                Toast.LENGTH_SHORT).show();
    }

    private void showDemoTab(LinearLayout selectedTab) {
        bindTab(selectedTab);
        Toast.makeText(this, R.string.demo_tab_notice, Toast.LENGTH_SHORT).show();
    }

    private void bindTab(LinearLayout selectedTab) {
        tabChats.setBackgroundResource(selectedTab == tabChats ? R.drawable.tab_selected : 0);
        tabContacts.setBackgroundResource(selectedTab == tabContacts ? R.drawable.tab_selected : 0);
        tabCalls.setBackgroundResource(selectedTab == tabCalls ? R.drawable.tab_selected : 0);
        tabSettings.setBackgroundResource(selectedTab == tabSettings ? R.drawable.tab_selected : 0);

        updateTabText(tabChats, selectedTab == tabChats);
        updateTabText(tabContacts, selectedTab == tabContacts);
        updateTabText(tabCalls, selectedTab == tabCalls);
        updateTabText(tabSettings, selectedTab == tabSettings);
    }

    private void updateTabText(LinearLayout tab, boolean selected) {
        if (tab.getChildCount() == 0) {
            return;
        }
        View child = tab.getChildAt(0);
        if (child instanceof TextView) {
            TextView text = (TextView) child;
            text.setTextColor(selected ? 0xFF111111 : 0xFFE7E7E7);
            text.setTextSize(selected ? 12.5f : 12f);
            text.setTypeface(selected ? text.getTypeface() :
                    android.graphics.Typeface.create(text.getTypeface(), android.graphics.Typeface.NORMAL));
        }
    }
}
