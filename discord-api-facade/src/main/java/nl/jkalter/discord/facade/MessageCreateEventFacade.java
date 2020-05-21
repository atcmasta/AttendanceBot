package nl.jkalter.discord.facade;

import discord4j.core.event.domain.message.MessageCreateEvent;
import nl.jkalter.discord.facade.author.Author;
import nl.jkalter.discord.facade.author.IAuthor;
import nl.jkalter.discord.facade.client.ClientFacade;
import nl.jkalter.discord.facade.client.IClient;
import nl.jkalter.discord.facade.message.IMessage;
import nl.jkalter.discord.facade.message.MessageFacade;
import nl.jkalter.discord.facade.server.IServer;
import nl.jkalter.discord.facade.server.Server;

import java.util.Objects;

public class MessageCreateEventFacade implements IMessageCreateEventFacade {

    private final MessageCreateEvent event;
    private final IAuthor author;
    private final IMessage message;
    private final IClient client;
    private final IServer server;

    public MessageCreateEventFacade(MessageCreateEvent event) {
        this.event = event;
        this.author = event.getMember().map(Author::new).orElse(null);
        this.message = new MessageFacade(event.getMessage());
        this.client = new ClientFacade(event.getClient());
        this.server = event.getGuild().map(Server::new).block();
    }

    public void sendMessage(String message) {
        Objects.requireNonNull(event.getMessage().getChannel().block())
                .createMessage(message).block();
    }

    @Override
    public IMessage getMessage() {
        return message;
    }

    @Override
    public String getSanitizedMessageContent() {
        return event.getMessage()
                .getContent()
                .map(m -> m.trim().toLowerCase())
                .orElse("");
    }

    @Override
    public IAuthor getAuthor() {
        return author;
    }

    public IClient getClient() {
        return client;
    }

    @Override
    public IServer getServer() {
        return server;
    }

}
