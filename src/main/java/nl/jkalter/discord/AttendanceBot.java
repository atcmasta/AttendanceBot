package nl.jkalter.discord;

import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.io.IOException;

public class AttendanceBot {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AttendanceHandler.class);

    private static final Object exitSignal = new Object();

    public static void main(String[] args) throws DiscordException, RateLimitException, InterruptedException, IOException {
        IDiscordClient client = null;
        try {
            client = new ClientBuilder().withToken(TokenFile.getToken()).build();

            EventDispatcher dispatcher = client.getDispatcher(); // Gets the EventDispatcher instance for this client instance
            dispatcher.registerListener(new AttendanceHandler());
            dispatcher.registerListener(new ExitHandler(exitSignal));
            LOGGER.info("Logging in.");
            client.login();

            synchronized (exitSignal) {
                LOGGER.info("Waiting for the client to exit.");
                exitSignal.wait();
            }
        } finally {
            if (client != null) {
                LOGGER.info("Logging out.");
                client.logout();
            }
        }

        LOGGER.info("Exiting in 500ms.");
        Thread.sleep(500);
    }
}
