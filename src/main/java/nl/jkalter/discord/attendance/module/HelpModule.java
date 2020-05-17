package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.facade.author.IAuthor;
import nl.jkalter.discord.facade.IEventDispatcherFacade;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.IMessageCreateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelpModule implements ICommandHelpModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpModule.class);

    private final Command command;
    private final Collection<IModule> modules;

    public HelpModule(Collection<IModule> modules) {
        command = new Command(CommandName.HELP, "");
        this.modules = modules;
    }

    @Override
    public void enable(IEventDispatcherFacade eventDispatcherFacade) {
        eventDispatcherFacade.onMessageCreateEvent(this);
    }

    public void onMessageReceivedEvent(IMessageCreateEventFacade event) {
        if (!command.isMyCommand(event)) {
            return;
        }

        IAuthor author = event.getAuthor();
        final String authorName = author.getAuthorName();
        final long authorId = author.getAuthorId();

        LOGGER.debug("Trying to help {} ({})", authorName, authorId);

        if (command.isAuthorizedRole(event)) {
            String messageContent = command.removeCommand(event);
            final String[] arguments = messageContent.split(" ");

            if (arguments.length <= 1 && arguments[0].equals("")) {
                // list commands
                String commandList = modules.stream().filter(module -> module instanceof ICommandModule)
                        .map(module -> ((ICommandModule) module).getCommand().getFullCommandName())
                        .collect(Collectors.joining(", "));
                event.sendMessage(String.format("Try: %s.", commandList));
            } else {
                // find + elaborate on specific command
                final String commandName = arguments[0];
                Optional<IModule> match = modules.stream().filter(module -> moduleMatch(commandName, module)).findFirst();
                match.ifPresent(module -> {
                    ICommandHelpModule mod = ((ICommandHelpModule) module);
                    String help = mod.getHelp();
                    event.sendMessage(String.format("%s.", help));
                });
            }
        } else {
            LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
        }
    }

    private static boolean moduleMatch(String command, IModule module) {
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
