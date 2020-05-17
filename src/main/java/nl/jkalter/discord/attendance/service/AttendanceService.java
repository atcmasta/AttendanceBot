package nl.jkalter.discord.attendance.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AttendanceService {
    public static final String ATTENDANCE_DIR = "attendance";

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceService.class);
    private static final char SEPARATOR = ',';
    private static final char QUOTE = '\'';

    private AttendanceService() { /* Hiding public constructor */ }

    public static Collection<IAttendance> readAttendance(String list) throws IOException {
        LOGGER.debug("Reading attendance ({})", list);
        Collection<IAttendance> attendances = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(getAttendanceListPath(list).toString()), SEPARATOR, QUOTE)) {
            HeaderColumnNameMappingStrategy<UserAttendance> beanStrategy = new HeaderColumnNameMappingStrategy<>();
            beanStrategy.setType(UserAttendance.class);

            CsvToBean<UserAttendance> csvToBean = new CsvToBean<>();
            final List<UserAttendance> userAttendance = csvToBean.parse(beanStrategy, reader);
            attendances.addAll(userAttendance);
        }

        return attendances;
    }

    public static boolean createAttendance(String list) throws IOException {
        boolean result = false;

        if (!listExists(list)) {
            LOGGER.debug("Creating attendance ({})", list);
            result = writeAttendance(list, new ArrayList<>());
        }

        return result;
    }

    public static boolean setAttendance(long userId, String list, Attendance attendance) throws IOException {
        boolean result = false;
        if (listExists(list)) {
            final Collection<IAttendance> iAttendances = readAttendance(list);
            final Optional<IAttendance> existingAttendance = iAttendances.stream().filter(iAttendance -> iAttendance.getUserId() == userId).findFirst();

            if (existingAttendance.isPresent()) {
                IAttendance attendee = existingAttendance.get();
                attendee.setAttendance(attendance);
            } else {
                iAttendances.add(new UserAttendance(userId, attendance));
            }
            result = writeAttendance(list, iAttendances);
        }
        return result;
    }

    public static boolean clearAttendance(String list) throws IOException {
        return writeAttendance(list, new ArrayList<>());
    }

    public static boolean writeAttendance(String list, Collection<IAttendance> attendances) throws IOException {
        boolean result;
        LOGGER.debug("Writing attendance ({})", list);
        if (LOGGER.isTraceEnabled()) {
            attendances.forEach( att -> LOGGER.trace("Attendance for {} is {}", att.getUserId(), att.getAttendance()));
        }

        final File attendanceFile = new File(getAttendanceListPath(list).toAbsolutePath().toString());

        if (attendanceFile.getParentFile().mkdirs()) {
            LOGGER.info("Created directories needed to store file {}.", attendanceFile.getAbsolutePath());
        }

        FileWriter writer = new FileWriter(attendanceFile);

        try (CSVWriter csvWriter = new CSVWriter(writer, SEPARATOR, QUOTE)) {
            List<String[]> data = toStringArray(attendances);
            csvWriter.writeAll(data);
            result = !csvWriter.checkError();
        }
        return result;
    }

    public static Collection<String> listAttendanceLists() {
        Collection<String> lists = new LinkedList<>();

        File folder = new File(ATTENDANCE_DIR);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                String fileName = file.getName();

                if (fileName.endsWith(".csv")) {
                    fileName = fileName.substring(0, fileName.indexOf(".csv"));
                    lists.add(fileName);
                }
            }
        } else {
            LOGGER.warn("Directory of attendance files {} cannot be listed.", folder.getAbsoluteFile());
        }

        return lists;
    }

public static boolean removeAttendance(String list) {
        LOGGER.debug("Removing attendance ({})", list);

        boolean result = false;
        final File listFile = getListFile(list);
        if (listFile.exists()) {
            final Path path = Paths.get(listFile.getAbsolutePath());
            try {
                Files.delete(path);
                result = true;
            } catch (IOException e) {
                LOGGER.warn("Error removing list file {}", path.toString(), e);
                result = false;
            }
        }
        return result;
    }

    public static boolean listExists(String list) {
        return getListFile(list).exists();
    }

    private static File getListFile(String list) {
        return new File(getAttendanceListPath(list).toString());
    }

    private static Path getAttendanceListPath(String list) {
        return Paths.get(ATTENDANCE_DIR, list + ".csv");
    }

    private static List<String[]> toStringArray(Collection<IAttendance> attendances) {
        List<String[]> records = new ArrayList<>();

        // adding header record
        records.add(UserAttendance.MAPPING);

        for (IAttendance attendance : attendances) {
            records.add(new String[]{Long.toString(attendance.getUserId()), attendance.getAttendance().toString()});
        }

        return records;
    }

}