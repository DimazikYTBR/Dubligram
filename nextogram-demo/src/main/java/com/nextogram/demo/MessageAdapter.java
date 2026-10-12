package com.nextogram.demo;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<DemoMessage> messages = new ArrayList<>();

    public void submitMessages(List<DemoMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        DemoMessage message = messages.get(position);
        holder.messageText.setText(message.getText());
        holder.timeText.setText(DemoTime.formatTime(message.getSentAt()));
        holder.bubble.setBackgroundResource(message.isOutgoing()
                ? R.drawable.message_bubble_sent : R.drawable.message_bubble_received);
        holder.messageText.setTextColor(message.isOutgoing() ? 0xFF111111 : 0xFFF5F5F5);
        holder.row.setGravity(message.isOutgoing() ? Gravity.END : Gravity.START);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout row;
        final LinearLayout bubble;
        final TextView messageText;
        final TextView timeText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.messageRow);
            bubble = itemView.findViewById(R.id.messageBubble);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.messageTime);
        }
    }
}
