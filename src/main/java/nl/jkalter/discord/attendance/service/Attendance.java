package nl.jkalter.discord.attendance.service;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Attendance {
    UNKNOWN, YES, NO, LATE, VACATION;

    @Override
    public String toString() {
        return name();
    }

    public static String list() {
        return Arrays.stream(Attendance.values()).map(att -> att.toString().toLowerCase()).collect(Collectors.joining(", "));
    }
}
