package nl.jkalter.discord.facade;

import nl.jkalter.discord.facade.author.IAuthor;
import nl.jkalter.discord.facade.client.IClient;
import nl.jkalter.discord.facade.message.IMessage;
import nl.jkalter.discord.facade.server.IServer;

public interface IMessageCreateEventFacade {

    String getSanitizedMessageContent();

    IAuthor getAuthor();

    void sendMessage(String message);

    IMessage getMessage();

    IClient getClient();

    IServer getServer();
}
