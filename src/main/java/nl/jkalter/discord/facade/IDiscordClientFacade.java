package nl.jkalter.discord.facade;

public interface IDiscordClientFacade {

    void login();

    boolean isLoggedIn();

    void logout();

    String getApplicationClientID();

    IEventDispatcherFacade getDispatcher();
}
