package nl.jkalter.discord.attendance.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public enum Attendance {
    UNKNOWN, YES, NO, LATE, VACATION;

    public static final int MAX_ATTENDANCE_LENGTH = Arrays.stream(Attendance.values())
            .map(value -> value.name().length()).max(Comparator.naturalOrder()).get();

    @Override
    public String toString() {
        return name();
    }

    public static String list() {
        return Arrays.stream(Attendance.values()).map(att -> att.toString().toLowerCase()).collect(Collectors.joining(", "));
    }
}
