package com.dihadi.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Utility for tracking and evaluating user activity and login timestamps.
 * Provides standard formatting and inactivity threshold calculations.
 */
public class UserActivityUtil {
    public static final int INACTIVITY_THRESHOLD_DAYS = 30;
    public static final DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Returns the current date and time formatted in the standard format.
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(STANDARD_FORMATTER);
    }

    /**
     * Returns a formatted timestamp offset by the specified number of days in the past.
     * Useful for test/benchmark mock datasets.
     */
    public static String getPastTimestamp(int daysAgo) {
        return LocalDateTime.now().minusDays(daysAgo).format(STANDARD_FORMATTER);
    }

    /**
     * Parses a date or datetime string into a LocalDateTime object.
     * Supports multiple common datetime formats safely.
     */
    public static LocalDateTime parseDateTime(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return null;
        }
        String clean = timestamp.trim();
        List<DateTimeFormatter> formatters = List.of(
                STANDARD_FORMATTER,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        for (DateTimeFormatter fmt : formatters) {
            try {
                if (fmt.toString().contains("dd/MM/yyyy") && !fmt.toString().contains("HH")) {
                    return LocalDate.parse(clean, fmt).atStartOfDay();
                }
                if (clean.length() == 10 && clean.contains("-")) {
                    return LocalDate.parse(clean, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                }
                return LocalDateTime.parse(clean, fmt);
            } catch (Exception ignored) {}
        }
        return null;
    }

    /**
     * Returns true if the user has been inactive for 30 or more days (or has no recorded activity).
     */
    public static boolean isInactive(String lastLoginStr) {
        if (lastLoginStr == null || lastLoginStr.isBlank()) {
            return true;
        }
        LocalDateTime loginTime = parseDateTime(lastLoginStr);
        if (loginTime == null) {
            return true;
        }
        long days = ChronoUnit.DAYS.between(loginTime, LocalDateTime.now());
        return days >= INACTIVITY_THRESHOLD_DAYS;
    }

    /**
     * Returns the number of days since the user's last login.
     * Returns 999 if no login record exists.
     */
    public static long getInactiveDays(String lastLoginStr) {
        if (lastLoginStr == null || lastLoginStr.isBlank()) {
            return 999;
        }
        LocalDateTime loginTime = parseDateTime(lastLoginStr);
        if (loginTime == null) {
            return 999;
        }
        long days = ChronoUnit.DAYS.between(loginTime, LocalDateTime.now());
        return Math.max(0, days);
    }

    /**
     * Formats the last login timestamp into a readable label (e.g. "05 Sep 2026, 02:30 PM").
     */
    public static String formatDisplayDate(String lastLoginStr) {
        if (lastLoginStr == null || lastLoginStr.isBlank()) {
            return "No recorded activity";
        }
        LocalDateTime dt = parseDateTime(lastLoginStr);
        if (dt != null) {
            return dt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        }
        return lastLoginStr;
    }
}
