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

import java.util.Collection;

public class ListModule implements ICommandModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListModule.class);

    private final Command command;

    public ListModule() {
        command = new Command(CommandName.LISTS, "");
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

        LOGGER.debug("Trying to show attendance for {} ({})", authorName, authorId);
        if (command.isAuthorizedRole(event)) {
            final Collection<String> lists = AttendanceService.listAttendanceLists();

            if (lists.isEmpty()) {
                event.sendMessage("There are no lists available");
            } else if (lists.size() == 1) {
                event.sendMessage(String.format("There is one list available called %s", lists.iterator().next()));
            } else {
                event.sendMessage(String.format("The following lists are available: %s", String.join(", ", lists)));
            }
        } else {
            LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
        }
    }

    @Override
    public ICommand getCommand() {
        return command;
    }
}
