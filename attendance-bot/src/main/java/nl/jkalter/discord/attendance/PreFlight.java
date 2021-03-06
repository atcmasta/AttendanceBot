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

    private static void createDirectory(String path) {
        File file = new File(path);
        if (file.isFile()) {
            LOGGER.info("File {} prevents creation of a required directory", file.getPath());
        } else if (file.exists() && file.isDirectory()) {
            LOGGER.debug("Directory {} already exists, skipping.", file.getPath());
        } else if (file.mkdir()) {
            LOGGER.info("Created directory {}", file.getPath());
        } else {
            LOGGER.warn("Could not create directory {}", file.getPath());
        }
    }

    private static void createFile(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            f.createNewFile();
        }
    }
}
