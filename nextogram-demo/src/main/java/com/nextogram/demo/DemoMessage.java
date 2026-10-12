package com.nextogram.demo;

public class DemoMessage {
    private final String text;
    private final long sentAt;
    private final boolean outgoing;

    public DemoMessage(String text, long sentAt, boolean outgoing) {
        this.text = text;
        this.sentAt = sentAt;
        this.outgoing = outgoing;
    }

    public String getText() {
        return text;
    }

    public long getSentAt() {
        return sentAt;
    }

    public boolean isOutgoing() {
        return outgoing;
    }
}
