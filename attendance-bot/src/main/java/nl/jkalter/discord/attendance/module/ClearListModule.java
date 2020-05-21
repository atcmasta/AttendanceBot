package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.attendance.service.AttendanceService;
import nl.jkalter.discord.facade.IEventDispatcherFacade;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.IMessageCreateEventListener;
import nl.jkalter.discord.facade.author.IAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClearListModule implements ICommandHelpModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClearListModule.class);
    private static final int LIST_NAME_LENGTH = 16;
    private static final String LIST_REGEX = "[a-zA-Z0-9]+([a-zA-Z0-9-_]*[a-zA-Z0-9])*";

    private final Command command;

    public ClearListModule() {
        command = new Command(CommandName.CLEAR);
    }

    @Override
    public void enable(IEventDispatcherFacade eventDispatcherFacade) {
        eventDispatcherFacade.onMessageCreateEvent(this);
    }

    public void onMessageReceivedEvent(IMessageCreateEventFacade event) {
        try {
            if (!command.isMyCommand(event)) {
                return;
            }

            IAuthor author = event.getAuthor();
            final String authorName = author.getAuthorName();
            final long authorId = author.getAuthorId();

            LOGGER.debug("Trying to clear attendance for {} ({})", authorName, authorId);
            if (command.isAuthorizedRole(event)) {
                String messageContent = command.removeCommand(event);
                final String[] lists = messageContent.split(" ");
                final List<String> clearedLists = new ArrayList<>();

                for (String list : lists) {
                    if (list.length() > LIST_NAME_LENGTH) {
                        LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it is longer than {} characters.", list, authorName, authorId, LIST_NAME_LENGTH);
                    } else if (!list.matches(LIST_REGEX)) {
                        LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it contains characters other than a-z, A-Z, 0-9.", list, authorName, authorId);
                    } else if (AttendanceService.clearAttendance(list)) {
                        LOGGER.info("Cleared list with name {} for {} ({})", list, authorName, authorId);
                        clearedLists.add(list);
                    }
                }
                respond(event, authorName, clearedLists);
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }

        } catch (IOException e) {
            LOGGER.error(String.format("Exception occurred handling message (%s)", event.getMessage().getContent()), e);
            event.getAuthor().sendPrivateMessage("Something went horribly wrong, maybe try again later or inform someone.");
        }
    }

    private void respond(IMessageCreateEventFacade event, String authorName, List<String> cleared) {
        if (cleared.isEmpty()) {
            event.sendMessage(String.format("I did not clear any lists for you %s ;)", authorName));
        } else if (cleared.size() == 1) {
            event.sendMessage(String.format("I cleared the %s list for you %s.", String.join(", ", cleared), authorName));
        } else {
            event.sendMessage(String.format("I cleared %s lists (%s) for you %s.", cleared.size(), String.join(", ", cleared), authorName));
        }
    }

    @Override
    public ICommand getCommand() {
        return command;
    }

    @Override
    public String getHelp() {
        return String.format("%s %s", getCommand().getFullCommandName(),
                AttendanceService.listAttendanceLists().stream().collect(Collectors.joining(", ", "(", ")")));
    }
}
