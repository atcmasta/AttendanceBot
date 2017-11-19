package nl.jkalter.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TokenFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenFile.class);
    public static final String TOKEN_FILE = "token.txt";

    public static String getToken() throws IOException {
        return getToken(TOKEN_FILE);
    }
    public static String getToken(String tokenFile) throws IOException {
        String token = null;
        File f = new File(TOKEN_FILE);
        if(!f.exists() && !f.isDirectory()) {
            f.createNewFile();
            LOGGER.warn(String.format("%s created, please add the bot token there before continuing", tokenFile));
            System.exit(1);
        } else {
            StringBuilder sb = new StringBuilder();
            Files.readAllLines(Paths.get(tokenFile)).forEach((line) -> sb.append(line));
            token = sb.toString();
        }
        return token;
    }
}
