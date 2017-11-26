package nl.jkalter.discord.attendance.configuration;

import nl.jkalter.discord.attendance.PreFlight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;

public class BotProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotProperties.class);
    private static final String DEFAULT_PROPERTIES = "default.properties";
    public static final String BOT_PROPERTIES = "bot.properties";

    private BotProperties() { /* Hiding public constructor */ }

    public static Properties loadProperties() {
        Properties botProps = new Properties();

        try {
            Properties defaultProps = new Properties();
            InputStream is = BotProperties.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES);
            defaultProps.load(is);

            botProps = new Properties(defaultProps);
            File file = new File(Paths.get(PreFlight.CONFIG_DIR, BOT_PROPERTIES).toString());
            if (file.isFile()) {
               botProps.load(new FileInputStream(file.getPath()));
            }
        } catch (Exception e) {
            LOGGER.warn("Encountered a problem loading the default or bot properties.", e);
        }
        return botProps;
    }

    public static void storeProperties(Properties properties) throws IOException {
        properties.store(new FileWriter(new File(Paths.get(PreFlight.CONFIG_DIR, BOT_PROPERTIES).toString())), null);
    }

}
