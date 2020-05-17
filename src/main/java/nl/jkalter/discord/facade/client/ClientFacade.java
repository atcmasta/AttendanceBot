package nl.jkalter.discord.facade.client;

import discord4j.core.DiscordClient;
import discord4j.core.object.util.Image;
import discord4j.core.object.util.Snowflake;
import nl.jkalter.discord.facade.client.member.IMember;
import nl.jkalter.discord.facade.client.member.MemberFacade;

public class ClientFacade implements IClient {

    private final DiscordClient client;

    public ClientFacade(DiscordClient client) {
        this.client = client;
    }

    public IMember getMember(long serverId, long userId) {
        return new MemberFacade(client.getMemberById(Snowflake.of(serverId), Snowflake.of(userId)).block());
    }

    public void setAvatar(String url) {
        client.edit(userEditSpec -> userEditSpec.setAvatar(Image.ofUrl(url).block())).block();
    }

}
