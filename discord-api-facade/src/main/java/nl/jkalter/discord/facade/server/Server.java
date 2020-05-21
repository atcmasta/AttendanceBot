package nl.jkalter.discord.facade.server;

import discord4j.core.object.entity.Guild;

public class Server implements IServer {

    private final Guild guild;

    public Server(Guild guild) {
        this.guild = guild;
    }

    public long getServerId() {
        return guild.getId().asLong();
    }
}
