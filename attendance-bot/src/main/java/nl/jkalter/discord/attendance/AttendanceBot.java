package nl.jkalter.discord.attendance;

import nl.jkalter.discord.attendance.exit.ExitHandler;
import nl.jkalter.discord.attendance.exit.ExitSignal;
import nl.jkalter.discord.attendance.files.TokenFile;
import nl.jkalter.discord.attendance.module.*;
import nl.jkalter.discord.attendance.module.music.*;
import nl.jkalter.discord.attendance.module.music.manager.AudioModuleManager;
import nl.jkalter.discord.facade.DiscordClientBuilderFacade;
import nl.jkalter.discord.facade.IDiscordClientFacade;
import nl.jkalter.discord.facade.IEventDispatcherFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class AttendanceBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceBot.class);
    private static final ExitSignal exitSignal = new ExitSignal();
    private static AudioModuleManager manager;

    public static void main(String[] args) throws InterruptedException, IOException {
        IDiscordClientFacade client = null;
        try {
            executePreFlight();

            manager = new AudioModuleManager();

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

    private static IDiscordClientFacade buildClient() throws IOException {
        LOGGER.debug("Building client.");
        IDiscordClientFacade client = new DiscordClientBuilderFacade().withToken(TokenFile.getToken()).build();
        LOGGER.trace("Built client.");
        return client;
    }

    private static void registerModules(IDiscordClientFacade client) {
        LOGGER.debug("Registering modules.");
        IEventDispatcherFacade dispatcher = client.getDispatcher();
        Collection<IModule> modules = getEnabledModules();
        modules.forEach(module -> module.enable(dispatcher));
        LOGGER.trace("Registered modules.");
    }

    private static void executePreFlight() throws IOException {
        LOGGER.debug("Executing pre flight.");
        PreFlight.execute();
        LOGGER.trace("Executed pre flight.");
    }

    private static void loginClient(IDiscordClientFacade client) {
        LOGGER.info("Logging in.");
        client.login();
        LOGGER.info("Please use the following URL to add your bot to a server " +
                        "https://discordapp.com/oauth2/authorize?&client_id={}&scope=bot&permissions=0",
                client.getApplicationClientID());
        LOGGER.info("Logged in.");
    }

    private static void logOutClient(IDiscordClientFacade client) {
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

    private static Collection<IModule> getEnabledModules() {
        Collection<IModule> enabledModules = new LinkedList<>();

        enabledModules.add(new AttendModule());
        enabledModules.add(new CreateListModule());
        enabledModules.add(new RemoveListModule());
        enabledModules.add(new ClearListModule());
        enabledModules.add(new LeaveVoiceModule(manager));
        enabledModules.add(new ListAttendance());
        enabledModules.add(new ListModule());
        enabledModules.add(new NextMusicModule(manager));
        enabledModules.add(new PlayMusicModule(manager));
        enabledModules.add(new PauseMusicModule(manager));
        enabledModules.add(new MusicQueueModule(manager));
        enabledModules.add(new AvatarModule());
        enabledModules.add(new HelpModule(enabledModules));
        enabledModules.add(new ExitHandler(exitSignal));

        return enabledModules;
    }
}
