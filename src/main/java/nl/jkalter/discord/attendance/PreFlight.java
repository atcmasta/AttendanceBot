package nl.jkalter.discord.attendance;

import nl.jkalter.discord.attendance.configuration.BotProperties;
import nl.jkalter.discord.attendance.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PreFlight {
    public static final String CONFIG_DIR = "config";

    private static final Logger LOGGER = LoggerFactory.getLogger(PreFlight.class);

    private PreFlight()  { /* Hiding public constructor */ }

    public static void execute() throws IOException {
        createDirectory(CONFIG_DIR);
        createDirectory(AttendanceService.ATTENDANCE_DIR);
        createFile(CONFIG_DIR + File.separatorChar + BotProperties.BOT_PROPERTIES);
    }

    private static boolean createDirectory(String path) {
        boolean result = false;
        File file = new File(path);
        if (file.isFile()) {
            LOGGER.info(String.format("File %s prevents creation of a required directory", file.getPath()));
            result = false;
        } else if (file.exists() && file.isDirectory()) {
            LOGGER.debug(String.format("Directory %s already exists, skipping.", file.getPath()));
            result = true;
        } else if (file.mkdir()) {
            LOGGER.info(String.format("Created directory %s", file.getPath()));
            result = true;
        } else {
            LOGGER.warn(String.format("Could not create directory %s", file.getPath()));
            result = false;
        }

        return result;
    }

    private static boolean createFile(String fileName) throws IOException {
        boolean result = false;
        File f = new File(fileName);
        if (f.exists()) {
            result = f.isFile();
        } else {
            result = f.createNewFile();
        }
        return result;
    }
}
