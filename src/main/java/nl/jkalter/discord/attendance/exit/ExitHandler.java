package nl.jkalter.discord.attendance.exit;

import nl.jkalter.discord.attendance.module.ICommandModule;
import nl.jkalter.discord.attendance.module.event.WrappedMessageReceivedEvent;
import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

public class ExitHandler implements ICommandModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExitHandler.class);

    private final Command command;
    private final ExitSignal exitSignal;

    public ExitHandler(ExitSignal exitSignal) {
        command = new Command(CommandName.EXIT, "");
        this.exitSignal = exitSignal;
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        final WrappedMessageReceivedEvent wrappedEvent = new WrappedMessageReceivedEvent(event);

        if (command.isMyCommand(wrappedEvent)) {

            final String authorName = wrappedEvent.getAuthorName();
            final long authorId = wrappedEvent.getAuthorId();

            if (command.isAuthorizedRole(wrappedEvent)) {
                LOGGER.info("Exit commandName received from {} ({}), shutting down.", authorName, authorId);
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

    @Override
    public ICommand getCommand() {
        return command;
    }
}
