package nl.jkalter.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public class AttendanceFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceFile.class);
    private static final String CSV_FORMAT = "\"%s\",\"%s\",\"%s\"\r\n";
    private static final String ATTENDANCE_FILE = "attendance.csv";

    public static void writeAttendance(Collection<UserAttendance> attendanceCollection) {
        Path path = Paths.get(ATTENDANCE_FILE);

        //Use try-with-resource to get auto-closeable writer instance
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE_NEW)) {
            writer.write(String.format(CSV_FORMAT, "user id", "user name", "attendance"));

            for (UserAttendance attendance : attendanceCollection) {
                writer.write(String.format(CSV_FORMAT, attendance.getUser().getLongID(),attendance.getUser().getName(),attendance.getAttendance().name()));
            }
        } catch (IOException ioe) {
            LOGGER.error(String.format("Error writing %s, unable to save attendance.", ATTENDANCE_FILE), ioe);
        }
    }

    public static Collection<UserAttendance> readAttendance() {
        return null;
    }

}
