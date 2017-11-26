package nl.jkalter.discord.attendance.exit;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class ExitHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExitHandler.class);

    private final Command command;
    private final ExitSignal exitSignal;

    public ExitHandler(ExitSignal exitSignal) {
        command = new Command(CommandName.EXIT, "");
        this.exitSignal = exitSignal;
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        final IMessage message = event.getMessage();
        String messageContent = message.getContent().trim().toLowerCase();

        if (command.isMyCommand(messageContent)) {
            final IUser author = message.getAuthor();
            String authorName = author.getName();
            long authorId = author.getLongID();

            final List<IRole> rolesForAuthor = event.getGuild().getRolesForUser(author);
            if (command.isAuthorizedRole(rolesForAuthor)) {
                LOGGER.info("Exit commandName received from %s (%s), shutting down.", authorName, authorId);
                for (IChannel channel : event.getClient().getChannels(false)) {
                    channel.sendMessage(String.format("Exit received from %s", authorName));
                }

                exitSignal.setExit(true);

                synchronized (exitSignal) {
                    exitSignal.notifyAll();
                }
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }
        }
    }

}
