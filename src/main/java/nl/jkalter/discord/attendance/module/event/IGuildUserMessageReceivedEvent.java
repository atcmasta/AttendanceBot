package nl.jkalter.discord.attendance.module.event;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public interface IGuildUserMessageReceivedEvent {

    IUser getAuthor();

    IGuild getGuild();
}
