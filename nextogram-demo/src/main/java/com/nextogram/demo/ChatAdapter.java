package com.nextogram.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    public interface Listener {
        void onChatClick(ChatItem chat);
        void onChatLongClick(ChatItem chat);
    }

    private final List<ChatItem> items = new ArrayList<>();
    private final Listener listener;

    public ChatAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<ChatItem> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem item = items.get(position);
        DemoMessage lastMessage = item.getLastMessage();
        holder.avatarText.setText(item.getAvatarText());
        holder.titleText.setText(item.getTitle());
        holder.subtitleText.setText(lastMessage == null ? "" :
                (lastMessage.isOutgoing() ? "You: " : "") + lastMessage.getText());
        holder.timeText.setText(lastMessage == null ? "" : DemoTime.formatChatTime(lastMessage.getSentAt()));
        holder.unreadText.setVisibility(item.getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
        holder.unreadText.setText(String.valueOf(item.getUnreadCount()));
        holder.pinnedText.setVisibility(item.isPinned() ? View.VISIBLE : View.GONE);
        holder.statusText.setText(item.isOnline() ? "● online" : "○ offline");
        holder.statusText.setTextColor(item.isOnline() ? 0xFFB9E7C8 : 0xFF858585);
        holder.itemView.setOnClickListener(view -> listener.onChatClick(item));
        holder.itemView.setOnLongClickListener(view -> {
            listener.onChatLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        final TextView avatarText;
        final TextView titleText;
        final TextView subtitleText;
        final TextView timeText;
        final TextView unreadText;
        final TextView pinnedText;
        final TextView statusText;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarText = itemView.findViewById(R.id.avatarText);
            titleText = itemView.findViewById(R.id.titleText);
            subtitleText = itemView.findViewById(R.id.subtitleText);
            timeText = itemView.findViewById(R.id.timeText);
            unreadText = itemView.findViewById(R.id.unreadText);
            pinnedText = itemView.findViewById(R.id.pinnedText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}
