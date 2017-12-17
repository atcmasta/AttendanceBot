package nl.jkalter.discord.attendance.module;

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
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListAttendance implements ICommandHelpModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateListModule.class);
    private static final int LIST_NAME_LENGTH = 16;

    private final Command command;

    public ListAttendance() {
        command = new Command(CommandName.LIST);
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        try {
            IMessage message = event.getMessage();
            String messageContent = message.getContent().trim().toLowerCase();

            if (command.isMyCommand(messageContent)) {
                IUser author = message.getAuthor();
                String authorName = author.getName();
                long authorId = author.getLongID();

                LOGGER.debug("Trying to list attendance for {} ({})", authorName, authorId);
                if (command.isAuthorizedRole(event)) {
                    messageContent = command.removeCommand(messageContent);

                    final String[] arguments = messageContent.split(" ");
                    String list = null;
                    Collection<IAttendance> attendees = null;
                    if (arguments.length > 0) {
                        list = arguments[0].substring(0, Math.min(arguments[0].length(), LIST_NAME_LENGTH));

                        if (AttendanceService.listExists(list)) {
                            attendees = AttendanceService.readAttendance(list);
                        } else {
                            LOGGER.info("Unable to find list {}, it most likely does not exist.", list);
                        }
                    }

                    Attendance attendance = null;
                    if (arguments.length > 1) {
                        String option = arguments[1].substring(0, Math.min(arguments[1].length(), Attendance.MAX_ATTENDANCE_LENGTH)).toUpperCase();
                        attendance = parseAttendance(option);
                    }

                    Stream<IAttendance> attendanceStream = null;
                    if (attendees != null && attendance != null) {
                        final Attendance att = attendance;
                        attendanceStream = attendees.stream().filter(iAttendance -> iAttendance.getAttendance().equals(att));
                    } else if (attendees != null) {
                        attendanceStream = attendees.stream();
                    }

                    respond(event, list, authorName, attendanceStream, attendance);
                } else {
                    LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
                }
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Exception occurred handling message (%s)", event.getMessage().getContent()), e);
            event.getAuthor().getOrCreatePMChannel().sendMessage("Something went horribly wrong, maybe try again later or inform someone.");
        }
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
