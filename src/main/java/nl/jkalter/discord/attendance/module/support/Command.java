package nl.jkalter.discord.attendance.module.support;

import nl.jkalter.discord.attendance.configuration.BotProperties;
import nl.jkalter.discord.attendance.module.event.IGuildUserMessageReceivedEvent;
import nl.jkalter.discord.attendance.module.event.ISanitizedMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IRole;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Command implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);
    private static final String PREFIX = "!";
    private final CommandName commandName;
    private final String postfix;

    public Command(CommandName commandName) {
        this(commandName, " ");
    }

    public Command(CommandName commandName, String postfix) {
        this.commandName = commandName;
        this.postfix = postfix;
    }

    public String removeCommand(ISanitizedMessageReceivedEvent event) {
        return removeCommand(event.getSanitizedMessageContent());
    }

    private String removeCommand(String messageContent) {
        return messageContent.replaceFirst(getFullCommandName(), "").trim();
    }

    public String getFullCommandName() {
        return PREFIX + commandName;
    }

    public boolean isMyCommand(ISanitizedMessageReceivedEvent event) {
        return isMyCommand(event.getSanitizedMessageContent());
    }

    private boolean isMyCommand(String messageContent) {
        return messageContent.startsWith(PREFIX + commandName + postfix);
    }

    public boolean isAuthorizedRole(IGuildUserMessageReceivedEvent event) {
        boolean authorized = false;
        final List<IRole> rolesForAuthor = getRolesForUser(event);

        final String authorizedRole = getRoleForCommand();
        if (authorizedRole !=  null) {
            LOGGER.info("Found the authorized role '{}' for the '{}' command", authorizedRole, this);
            final Optional<IRole> first = rolesForAuthor.stream().filter(iRole -> iRole.getName().equalsIgnoreCase(authorizedRole)).findFirst();
            authorized = first.isPresent();
        }
        return authorized;
    }

    private static List<IRole> getRolesForUser(IGuildUserMessageReceivedEvent event) {
        return event.getGuild() != null ? event.getGuild().getRolesForUser(event.getAuthor()) : Collections.emptyList();
    }

    private String getRoleForCommand() {
        return BotProperties.loadProperties().getProperty(getCommandName().toString());
    }

    public CommandName getCommandName() {
        return commandName;
    }

    @Override
    public String toString() {
        return commandName.toString();
    }
}
