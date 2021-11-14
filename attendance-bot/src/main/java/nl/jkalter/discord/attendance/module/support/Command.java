package nl.jkalter.discord.attendance.module.support;

import nl.jkalter.discord.attendance.configuration.BotProperties;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.role.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public String removeCommand(IMessageCreateEventFacade event) {
        return removeCommand(event.getSanitizedMessageContent());
    }

    private String removeCommand(String messageContent) {
        return messageContent.replaceFirst(getFullCommandName(), "").trim();
    }

    public String getFullCommandName() {
        return PREFIX + commandName;
    }

    public boolean isMyCommand(IMessageCreateEventFacade event) {
        return isMyCommand(event.getSanitizedMessageContent());
    }

    private boolean isMyCommand(String messageContent) {
        return messageContent.startsWith(PREFIX + commandName + postfix);
    }

    public boolean isAuthorizedRole(IMessageCreateEventFacade event) {
        boolean authorized = false;
        final List<Role> rolesForAuthor = getRolesForUser(event);

        final String authorizedRole = getRoleForCommand();
        if (authorizedRole !=  null) {
            LOGGER.info("Found the authorized role '{}' for the '{}' command", authorizedRole, this);
            final Optional<Role> first = rolesForAuthor.stream()
                    .filter(role -> role.getName().equalsIgnoreCase(authorizedRole))
                    .findFirst();
            authorized = first.isPresent();
        } else {
            LOGGER.info("Did not find an authorized role for the '{}' command", this);
        }
        return authorized;
    }

    private static List<Role> getRolesForUser(IMessageCreateEventFacade event) {
        return event.getAuthor() != null ? event.getAuthor().getRoles() : Collections.emptyList();
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
