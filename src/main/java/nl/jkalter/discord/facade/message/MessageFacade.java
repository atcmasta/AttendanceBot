package nl.jkalter.discord.facade.message;

import discord4j.core.object.entity.Message;

public class MessageFacade implements IMessage {

    private final Message message;

    public MessageFacade(Message message) {
        this.message = message;
    }

    public String getContent() {
        return message.getContent().orElse("");
    }

}
