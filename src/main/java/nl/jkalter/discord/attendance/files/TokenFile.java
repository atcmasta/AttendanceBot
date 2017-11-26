package nl.jkalter.discord.attendance.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TokenFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenFile.class);
    public static final String TOKEN_FILE = "token.txt";

    private TokenFile() { /* hide constructor */ }


    /**
     * Retrieves the token using the default token file location {@link TokenFile#TOKEN_FILE}
     * @return the Discord API token
     * @throws IOException if the token file can not be read
     */
    public static String getToken() throws IOException {
        return getToken(TOKEN_FILE);
    }

    /**
     * Retrieves the token using the specified token file location
     * @param tokenFile location of the file containing the Discord API token
     * @return the Discord API token
     * @throws IOException if the token file can not be read
     */
    public static String getToken(String tokenFile) throws IOException  {
        String token = null;
        File f = new File(TOKEN_FILE);
        if(!f.exists() && !f.isDirectory()) {
            boolean result = false;
            try {
                result = f.createNewFile();
            } catch (IOException e) {
                LOGGER.error(getCannotCreateTokenMessage(tokenFile), e);
            }

            if (result) {
                LOGGER.warn("{} created, please add a discord bot token before continuing (https://discordapp.com/developers/applications/me)", tokenFile);
                System.exit(1);
            } else {
                LOGGER.error(getCannotCreateTokenMessage(tokenFile), tokenFile);
            }

        } else {
            StringBuilder sb = new StringBuilder();
            Files.readAllLines(Paths.get(tokenFile)).forEach(sb::append);
            token = sb.toString();
        }
        return token;
    }

    private static String getCannotCreateTokenMessage(String tokenFile) {
        return String.format("Cannot create file %s to hold the discord bot token, unable to proceed until this is resolved.", tokenFile);
    }
}
