package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.event.WrappedMessageReceivedEvent;
import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.attendance.service.Attendance;
import nl.jkalter.discord.attendance.service.AttendanceService;
import nl.jkalter.discord.attendance.service.IAttendance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListAttendance implements ICommandHelpModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListAttendance.class);
    private static final int LIST_NAME_LENGTH = 16;

    private final Command command;

    public ListAttendance() {
        command = new Command(CommandName.LIST);
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        try {
            final WrappedMessageReceivedEvent wrappedEvent = new WrappedMessageReceivedEvent(event);

            if (command.isMyCommand(wrappedEvent)) {
                LOGGER.debug("Trying to list attendance for {} ({})", wrappedEvent.getAuthorName(), wrappedEvent.getAuthorId());
                if (command.isAuthorizedRole(wrappedEvent)) {
                    String messageContent = command.removeCommand(wrappedEvent);

                    final String[] arguments = messageContent.split(" ");

                    String sanitizedListName = null;
                    if (arguments.length > 0) {
                        String listName = arguments[0];
                        sanitizedListName = getSanitizedInput(listName, LIST_NAME_LENGTH);
                    }

                    Collection<IAttendance> attendees = getAttendeesForList(sanitizedListName);

                    final Attendance attendance;
                    if (arguments.length > 1) {
                        String attendanceType = arguments[1];
                        String sanitizedAttendanceType = getSanitizedInput(attendanceType, Attendance.MAX_ATTENDANCE_LENGTH).toUpperCase();
                        attendance = parseAttendance(sanitizedAttendanceType);
                    } else {
                        attendance = null;
                    }

                    Stream<IAttendance> attendanceStream = getFilteredAttendanceStream(attendees, attendance);

                    respond(event, sanitizedListName, wrappedEvent.getAuthorName(), attendanceStream, attendance);
                } else {
                    LOGGER.info("User {} ({}) is not authorized to use the {} command.", wrappedEvent.getAuthorName(), wrappedEvent.getAuthorId(), command);
                }
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Exception occurred handling message (%s)", event.getMessage().getContent()), e);
            event.getAuthor().getOrCreatePMChannel().sendMessage("Something went horribly wrong, maybe try again later or inform someone.");
        }
    }

    /**
     * Provides a stream of all attendees that have the supplied attendance, if no attendance is supplied all
     * attendees will be in the stream. Returns null for an empty collection of attendees
     * @param attendees a collection of attendees of type IAttendance
     * @param attendance the type of attendance we are interested is
     * @return a steam of attendees that have the supplies attendance, or all supplies attendees
     * if there is no attendance supplied.
     */
    private Stream<IAttendance> getFilteredAttendanceStream(Collection<IAttendance> attendees, Attendance attendance) {
        final Stream<IAttendance> attendanceStream;

        if (attendees != null) {
            if (attendance == null) {
                attendanceStream = attendees.stream();
            } else {
                attendanceStream = attendees.stream().filter(iAttendance -> iAttendance.getAttendance().equals(attendance));
            }
        } else {
            attendanceStream = null;
        }

        return attendanceStream;
    }

    private String getSanitizedInput(String listName, int listNameLength) {
        return listName.substring(0, Math.min(listName.length(), listNameLength));
    }

    private Collection<IAttendance> getAttendeesForList(String list) throws IOException {
        Collection<IAttendance> attendees = null;
        if (list != null) {
            if (AttendanceService.listExists(list)) {
                attendees = AttendanceService.readAttendance(list);
            } else {
                LOGGER.info("Unable to find list {}, it most likely does not exist.", list);
            }
        }
        return attendees;
    }

    private Attendance parseAttendance(String option) {
        Attendance attendance = null;
        try {
            attendance = Attendance.valueOf(option);
        } catch (IllegalArgumentException e) {
            LOGGER.info(String.format("Unable to attendance type for input %s, it most likely does not exist.", option), e);
        }
        return attendance;
    }

    private void respond(MessageReceivedEvent event, String list, String authorName, Stream<IAttendance> attendees, Attendance attendanceFilter) {
        if (attendees == null) {
            event.getChannel().sendMessage(String.format("I could not find that list for you %s ;)", authorName));
        } else {
            List<IAttendance> attendance = new ArrayList<>();
            attendees.forEach(attendance::add);

            StringBuilder sb = new StringBuilder();
            if (attendanceFilter != null) {
                sb.append(String.format("Attendance list for %s: %s (%s)%n", attendanceFilter.name().toLowerCase(), list, attendance.isEmpty() ? "empty" : attendance.size()));
            } else {
                sb.append(String.format("Attendance list: %s (%s)%n", list, attendance.isEmpty() ? "empty" : attendance.size()));
            }
            for (IAttendance att : attendance) {
                sb.append(String.format("%s %s%n", att.getAttendance(), event.getClient().getUserByID(att.getUserId()).getName()));
            }
            event.getChannel().sendMessage(sb.toString());
        }
    }

    @Override
    public ICommand getCommand() {
        return command;
    }

    @Override
    public String getHelp() {
        return String.format("%s %s %s", getCommand().getFullCommandName(),
                AttendanceService.listAttendanceLists().stream().collect(Collectors.joining(", ", "(", ")")),
                Arrays.stream(Attendance.values()).map(value -> value.name().toLowerCase())
                        .collect(Collectors.joining(", ", "(", ")")));
    }
}
