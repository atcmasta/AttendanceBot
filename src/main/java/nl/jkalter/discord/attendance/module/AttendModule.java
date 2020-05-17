package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.attendance.service.Attendance;
import nl.jkalter.discord.attendance.service.AttendanceService;
import nl.jkalter.discord.facade.author.IAuthor;
import nl.jkalter.discord.facade.IEventDispatcherFacade;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.IMessageCreateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AttendModule implements ICommandHelpModule, IMessageCreateEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendModule.class);
    private static final int LIST_NAME_LENGTH = 16;
    private static final String LIST_REGEX = "[a-zA-Z0-9]+([a-zA-Z0-9-_]*[a-zA-Z0-9])*";

    private final Command command;

    public AttendModule() {
        command = new Command(CommandName.ATTEND);
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

            LOGGER.debug("Trying to change attendance for {} ({})", authorName, authorId);

            if (command.isAuthorizedRole(event)) {
                String messageContent = command.removeCommand(event);
                final String[] arguments = messageContent.split(" ");

                String listName = findExistingListName(arguments, author);
                Attendance attendance = determineAttendance(arguments);

                updateAttendance(event, listName, attendance);
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Exception occurred handling message (%s)", event.getMessage().getContent()), e);
            event.getAuthor().sendPrivateMessage("Something went horribly wrong, maybe try again later or inform someone.");
        }
    }

    private void updateAttendance(IMessageCreateEventFacade event, String listName, Attendance attendance) throws IOException {
        IAuthor author = event.getAuthor();
        String authorName = author.getAuthorName();

        if (listName == null && attendance == null) {
            event.sendMessage(String.format("Try %s list name (%s).", command.getFullCommandName(), Attendance.list()));
        } else if (listName == null) {
            event.sendMessage(String.format("Sorry, I am unable to find that list %s.", authorName));
        } else if (attendance == null) {
            event.sendMessage(String.format("Sorry, I do not recognize your attendance %s try one of %s.", authorName, Attendance.list()));
        } else if (AttendanceService.setAttendance(author.getAuthorId(), listName, attendance)) {
            event.sendMessage(String.format("I put you down for %s on the %s list %s.", attendance, listName, authorName));
        }
    }

    private Attendance determineAttendance(String[] arguments) {
        Attendance attendance = null;

        if (arguments.length > 1) {
            String attendanceInput = arguments[1].substring(0, Math.min(arguments[1].length(), Attendance.MAX_ATTENDANCE_LENGTH)).toUpperCase();
            attendance = parseAttendance(attendanceInput);
        }
        return attendance;
    }

    private String findExistingListName(String[] arguments, IAuthor author) {
        String listName = null;
        if (arguments.length > 0) {
            String list = arguments[0].substring(0, Math.min(arguments[0].length(), LIST_NAME_LENGTH));
            if (!list.matches(LIST_REGEX)) {
                LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it contains characters other than a-z, A-Z, 0-9.", list, author.getAuthorName(), author.getAuthorId());
            } else if (AttendanceService.listExists(list)) {
                listName = list;
            } else {
                LOGGER.info("Unable to find list {}, it most likely does not exist.", list);
            }
        }
        return listName;
    }

    private Attendance parseAttendance(String attendanceInput) {
        Attendance attendance = null;
        try {
            attendance = Attendance.valueOf(attendanceInput);
        } catch (IllegalArgumentException e) {
            LOGGER.info(String.format("Unable to attendance type for input %s, it most likely does not exist.", attendanceInput), e);
        }
        return attendance;
    }

    @Override
    public ICommand getCommand() {
        return command;
    }

    @Override
    public String getHelp() {
        return String.format("%s %s %s", getCommand().getFullCommandName(), AttendanceService.listAttendanceLists().stream().collect(Collectors.joining(", ", "(", ")")),
                Arrays.stream(Attendance.values()).map(value -> value.name().toLowerCase())
                        .collect(Collectors.joining(", ", "(", ")")));
    }
}
