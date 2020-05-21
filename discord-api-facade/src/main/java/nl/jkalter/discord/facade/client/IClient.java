package nl.jkalter.discord.facade.client;

import nl.jkalter.discord.facade.client.member.IMember;

public interface IClient {

    IMember getMember(long serverId, long userId);

    void setAvatar(String url);
}
