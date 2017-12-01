package nl.jkalter.discord.attendance;

import nl.jkalter.discord.attendance.exit.ExitHandler;
import nl.jkalter.discord.attendance.exit.ExitSignal;
import nl.jkalter.discord.attendance.files.TokenFile;
import nl.jkalter.discord.attendance.module.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class AttendanceBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceBot.class);
    private static final ExitSignal exitSignal = new ExitSignal();

    public static void main(String[] args) throws InterruptedException, IOException {
        IDiscordClient client = null;
        try {
            executePreFlight();

            client = buildClient();
            registerModules(client);
            loginClient(client);

            waitForExit();
        } finally {
            logOutClient(client);
        }

        LOGGER.info("Exiting in 500ms.");
        Thread.sleep(500);
    }

    private static IDiscordClient buildClient() throws IOException {
        LOGGER.debug("Building client.");
        IDiscordClient client = new ClientBuilder().withToken(TokenFile.getToken()).build();
        LOGGER.trace("Built client.");
        return client;
    }

    private static void registerModules(IDiscordClient client) {
        LOGGER.debug("Registering modules.");
        EventDispatcher dispatcher = client.getDispatcher(); // Gets the EventDispatcher instance for this client instance
        getEnabledModules().forEach(dispatcher::registerListener);
        LOGGER.trace("Registered modules.");
    }

    private static void executePreFlight() throws IOException {
        LOGGER.debug("Executing pre flight.");
        PreFlight.execute();
        LOGGER.trace("Executed pre flight.");
    }

    private static void loginClient(IDiscordClient client) {
        LOGGER.info("Logging in.");
        client.login();
        LOGGER.info("Please use the following URL to add your bot to a server https://discordapp.com/oauth2/authorize?&client_id={}&scope=bot&permissions=0", client.getApplicationClientID());
        LOGGER.info("Logged in.");
    }

    private static void logOutClient(IDiscordClient client) {
        if (client != null && client.isLoggedIn()) {
            LOGGER.info("Logging out.");
            client.logout();
        }
    }

    private static void waitForExit() throws InterruptedException {
        LOGGER.debug("Waiting for the client to exit.");
        synchronized (exitSignal) {
            while (!exitSignal.isExit()) {
                exitSignal.wait();
            }
        }
        LOGGER.trace("Exit received in main thread.");
    }

    private static Collection<Object> getEnabledModules() {
        Collection<Object> enabledModules = new LinkedList<>();

        enabledModules.add(new AttendModule());
        enabledModules.add(new CreateListModule());
        enabledModules.add(new RemoveListModule());
        enabledModules.add(new ClearListModule());
        enabledModules.add(new ListAttendance());
        enabledModules.add(new ListModule());
        enabledModules.add(new AvatarModule());
        enabledModules.add(new ExitHandler(exitSignal));

        return enabledModules;
    }
}
