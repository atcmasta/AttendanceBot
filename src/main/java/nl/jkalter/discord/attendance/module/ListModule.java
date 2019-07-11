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

import java.util.Collection;

public class ListModule implements ICommandModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateListModule.class);

    private final Command command;

    public ListModule() {
        command = new Command(CommandName.LISTS, "");
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        final WrappedMessageReceivedEvent wrappedEvent = new WrappedMessageReceivedEvent(event);

        if (command.isMyCommand(wrappedEvent)) {

            final String authorName = wrappedEvent.getAuthorName();
            final long authorId = wrappedEvent.getAuthorId();

            LOGGER.debug("Trying to show attendance for {} ({})", authorName, authorId);
            if (command.isAuthorizedRole(wrappedEvent)) {
                final Collection<String> lists = AttendanceService.listAttendanceLists();

                if (lists.isEmpty()) {
                    event.getChannel().sendMessage("There are no lists available");
                } else if (lists.size() == 1) {
                    event.getChannel().sendMessage(String.format("There is one list available called %s", lists.iterator().next()));
                } else {
                    event.getChannel().sendMessage(String.format("The following lists are available: %s", String.join(", ", lists)));
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
