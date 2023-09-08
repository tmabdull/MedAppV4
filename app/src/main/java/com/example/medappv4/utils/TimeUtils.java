package com.example.medappv4.utils;

import java.util.List;
import java.util.Locale;

public class TimeUtils {
    public static String formatTime(int hour, int minute) {
        return String.format(Locale.US, "%02d:%02d", hour, minute);
    }

    public static String formatDays(List<Boolean> daysOfWeek) {
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        StringBuilder medicineDays = new StringBuilder();
        for (int i = 0; i < daysOfWeek.size(); i++) {
            if (daysOfWeek.get(i)) {
                medicineDays.append(days[i]).append(", ");
            }
        }
        if (medicineDays.length() > 0) {
            medicineDays.delete(medicineDays.length() - 2, medicineDays.length());
        }
        return medicineDays.toString();
    }
}
