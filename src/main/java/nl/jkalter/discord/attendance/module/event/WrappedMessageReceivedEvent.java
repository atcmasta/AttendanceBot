package nl.jkalter.discord.attendance.module.event;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class WrappedMessageReceivedEvent implements ISanitizedMessageReceivedEvent, IGuildUserMessageReceivedEvent {

    private final MessageReceivedEvent event;
    private final IUser author;

    public WrappedMessageReceivedEvent(MessageReceivedEvent event) {
        this.event = event;
        this.author = event.getAuthor();
    }

    public String getAuthorName() {
        return author.getName();
    }

    public long getAuthorId() {
        return author.getLongID();
    }

    @Override
    public String getSanitizedMessageContent() {
        IMessage message = event.getMessage();
        return message.getContent().trim().toLowerCase();
    }

    @Override
    public IUser getAuthor() {
        return author;
    }

    @Override
    public IGuild getGuild() {
        return event.getGuild();
    }
}
