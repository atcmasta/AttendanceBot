package nl.jkalter.discord.facade;

import discord4j.core.DiscordClient;

import java.util.Objects;

public class DiscordClientFacade implements IDiscordClientFacade {

    private final DiscordClient client;

    public DiscordClientFacade(DiscordClient client) {
        this.client = client;
    }

    @Override
    public void login() {
        client.login().subscribe();
    }

    @Override
    public EventDispatcherFacade getDispatcher() {
        return new EventDispatcherFacade(client.getEventDispatcher());
    }

    @Override
    public boolean isLoggedIn() {
        return client.isConnected();
    }

    @Override
    public void logout() {
        client.logout().block();
    }

    @Override
    public String getApplicationClientID() {
        return Objects.requireNonNull(client.getApplicationInfo().block()).getId().asString();
    }
}
