package com.nextogram.demo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DemoTime {
    private DemoTime() {
    }

    public static String formatTime(long timestamp) {
        return DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                .format(new Date(timestamp));
    }

    public static String formatChatTime(long timestamp) {
        Date messageDate = new Date(timestamp);
        Date now = new Date();
        SimpleDateFormat day = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        if (day.format(messageDate).equals(day.format(now))) {
            return formatTime(timestamp);
        }

        long elapsed = now.getTime() - timestamp;
        if (elapsed < 7L * 24 * 60 * 60 * 1000) {
            return new SimpleDateFormat("EEE", Locale.getDefault()).format(messageDate);
        }
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(messageDate);
    }
}
