package nl.jkalter.discord.facade;

import discord4j.core.DiscordClientBuilder;

public class DiscordClientBuilderFacade {

    private DiscordClientBuilder builder;

    public DiscordClientBuilderFacade withToken(String token) {
        if (builder == null) {
            builder = DiscordClientBuilder.create(token);
        }
        return this;
    }

public IDiscordClientFacade build() {
        return new DiscordClientFacade(builder.build());
    }
}
