package nl.jkalter.discord.facade.author;

import discord4j.core.object.VoiceState;
import nl.jkalter.discord.facade.role.Role;

import java.util.List;

public interface IAuthor {

    String getAuthorName();

    long getAuthorId();

    List<Role> getRoles();

    void sendPrivateMessage(String message);

    VoiceState getVoiceState();

}
