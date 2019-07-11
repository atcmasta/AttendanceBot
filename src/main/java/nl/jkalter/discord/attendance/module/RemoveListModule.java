package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.event.WrappedMessageReceivedEvent;
import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.attendance.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveListModule implements ICommandHelpModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveListModule.class);
    private static final int LIST_NAME_LENGTH = 16;
    private static final String LIST_REGEX = "[a-zA-Z0-9]+([a-zA-Z0-9-_]*[a-zA-Z0-9])*";

    private final Command command;

    public RemoveListModule() {
        command = new Command(CommandName.REMOVE);
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        final WrappedMessageReceivedEvent wrappedEvent = new WrappedMessageReceivedEvent(event);

        if (command.isMyCommand(wrappedEvent)) {

            final String authorName = wrappedEvent.getAuthorName();
            final long authorId = wrappedEvent.getAuthorId();

            LOGGER.debug("Trying to remove list(s) for {} ({})", authorName, authorId);
            if (command.isAuthorizedRole(wrappedEvent)) {
                String messageContent = command.removeCommand(wrappedEvent);

                final String[] lists = messageContent.split(" ");
                final List<String> removedLists = new ArrayList<>();

                for (String list : lists) {
                    if (list.length() > LIST_NAME_LENGTH) {
                        LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it is longer than {} characters.", list, authorName, authorId, LIST_NAME_LENGTH);
                    } else if (!list.matches(LIST_REGEX)) {
                        LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it contains characters other than a-z, A-Z, 0-9.", list, authorName, authorId);
                    } else if (AttendanceService.removeAttendance(list)) {
                        LOGGER.info("Removed list with name {} for {} ({})", list, authorName, authorId);
                        removedLists.add(list);
                    }
                }

                respond(event, authorName, removedLists);
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }
        }
    }

    private void respond(MessageReceivedEvent event, String authorName, List<String> removed) {
        if (removed.isEmpty()) {
            event.getChannel().sendMessage(String.format("I did not remove any lists %s ;)", authorName));
        } else if (removed.size() == 1) {
            event.getChannel().sendMessage(String.format("I removed the %s list for you %s.", String.join(", ", removed), authorName));
        } else {
            event.getChannel().sendMessage(String.format("I removed %s lists (%s) for you %s.", removed.size(), String.join(", ", removed), authorName));
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
