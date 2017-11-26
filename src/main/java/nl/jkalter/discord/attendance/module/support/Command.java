package nl.jkalter.discord.attendance.module.support;

import nl.jkalter.discord.attendance.configuration.BotProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IRole;

import java.util.List;
import java.util.Optional;

public class Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);
    protected static final String PREFIX = "!";
    protected final CommandName commandName;
    protected final String postfix;

    public Command(CommandName commandName) {
        this(commandName, " ");
    }

    public Command(CommandName commandName, String postfix) {
        this.commandName = commandName;
        this.postfix = postfix;
    }

    public String removeCommand(String messageContent) {
        return messageContent.replaceFirst(getFullCommandName(), "").trim();
    }

    public String getFullCommandName() {
        return PREFIX + commandName;
    }

    public boolean isMyCommand(String messageContent) {
        return messageContent.startsWith(PREFIX + commandName + postfix);
    }

    public boolean isAuthorizedRole(List<IRole> rolesForAuthor) {
        boolean authorized = false;

        final String authorizedRole = getRoleForCommand();
        if (authorizedRole !=  null) {
            LOGGER.info("Found the authorized role {} for the {} command", authorizedRole, this);
            final Optional<IRole> first = rolesForAuthor.stream().filter(iRole -> iRole.getName().equalsIgnoreCase(authorizedRole)).findFirst();
            authorized = first.isPresent();
            LOGGER.info("Found the authorized role {} for the {} command", authorizedRole, this);
        }
        return authorized;
    }

    private String getRoleForCommand() {
        return BotProperties.loadProperties().getProperty(getCommandName().toString());
    }

    private CommandName getCommandName() {
        return commandName;
    }

    @Override
    public String toString() {
        return commandName.toString();
    }
}
