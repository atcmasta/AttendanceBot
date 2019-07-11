package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.event.WrappedMessageReceivedEvent;
import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelpModule implements ICommandHelpModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendModule.class);

    private final Command command;
    private final Collection<Object> modules;

    public HelpModule(Collection<Object> modules) {
        command = new Command(CommandName.HELP, "");
        this.modules = modules;
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        final WrappedMessageReceivedEvent wrappedEvent = new WrappedMessageReceivedEvent(event);

        if (command.isMyCommand(wrappedEvent)) {

            final String authorName = wrappedEvent.getAuthorName();
            final long authorId = wrappedEvent.getAuthorId();


            LOGGER.debug("Trying to help {} ({})", authorName, authorId);

            if (command.isAuthorizedRole(wrappedEvent)) {
                String messageContent = command.removeCommand(wrappedEvent);
                final String[] arguments = messageContent.split(" ");

                if (arguments.length <= 1 && arguments[0].equals("")) {
                    // list commands
                    String commandList = modules.stream().filter(module -> module instanceof ICommandModule)
                            .map(module -> ((ICommandModule) module).getCommand().getFullCommandName())
                            .collect(Collectors.joining(", "));
                    event.getChannel().sendMessage(String.format("Try: %s.", commandList));
                } else {
                    // find + elaborate on specific command
                    final String commandName = arguments[0];
                    Optional<Object> match = modules.stream().filter(module -> moduleMatch(commandName, module)).findFirst();
                    match.ifPresent(module -> {
                        ICommandHelpModule mod = ((ICommandHelpModule) module);
                        String help = mod.getHelp();
                        event.getChannel().sendMessage(String.format("%s.", help));
                    });
                }
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }
        }
    }

    private static boolean moduleMatch(String command, Object module) {
        boolean result = false;

        if (module instanceof ICommandHelpModule) {
            ICommand com = ((ICommandHelpModule) module).getCommand();
            result = com.getFullCommandName().equalsIgnoreCase(command)
                    || com.getCommandName().name().equalsIgnoreCase(command);
        }

        return result;
    }

    @Override
    public ICommand getCommand() {
        return command;
    }


    @Override
    public String getHelp() {
        return String.format("I would suggest: %s", "https://psychcentral.com/");
    }

}
