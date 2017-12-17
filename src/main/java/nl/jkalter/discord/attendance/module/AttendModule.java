package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.attendance.service.Attendance;
import nl.jkalter.discord.attendance.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AttendModule implements ICommandHelpModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttendModule.class);
    private static final int LIST_NAME_LENGTH = 16;
    private static final String LIST_REGEX = "[a-zA-Z0-9]+([a-zA-Z0-9-_]*[a-zA-Z0-9])*";

    private final Command command;

    public AttendModule() {
        command = new Command(CommandName.ATTEND);
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

                LOGGER.debug("Trying to change attendance for {} ({})", authorName, authorId);

                if (command.isAuthorizedRole(event)) {

                    messageContent = command.removeCommand(messageContent);

                    final String[] arguments = messageContent.split(" ");

                    String listName = null;
                    Attendance attendance = null;

                    if (arguments.length > 0) {
                        String list = arguments[0].substring(0, Math.min(arguments[0].length(), LIST_NAME_LENGTH));
                        if (!list.matches(LIST_REGEX)) {
                            LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it contains characters other than a-z, A-Z, 0-9.", list, authorName, authorId);
                        } else if (AttendanceService.listExists(list)) {
                            listName = list;
                        } else {
                            LOGGER.info("Unable to find list {}, it most likely does not exist.", list);
                        }
                    }

                    if (arguments.length > 1) {
                        String attendanceInput = arguments[1].substring(0, Math.min(arguments[1].length(), Attendance.MAX_ATTENDANCE_LENGTH)).toUpperCase();
                        attendance = parseAttendance(attendanceInput);
                    }

                    if (listName == null && attendance == null) {
                        event.getChannel().sendMessage(String.format("Try %s listname (%s).", command.getFullCommandName(), Attendance.list()));
                    } else if (listName == null) {
                        event.getChannel().sendMessage(String.format("Sorry, I am unable to find that list %s.", authorName));
                    } else if (attendance == null) {
                        event.getChannel().sendMessage(String.format("Sorry, I do not recognize your attendance %s try one of %s.", authorName, Attendance.list()));
                    } else if (AttendanceService.setAttendance(authorId, listName, attendance)) {
                        event.getChannel().sendMessage(String.format("I put you down for %s on the %s list %s.", attendance, listName, authorName));
                    }
                } else {
                    LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
                }
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Exception occurred handling message (%s)", event.getMessage().getContent()), e);
            event.getAuthor().getOrCreatePMChannel().sendMessage("Something went horribly wrong, maybe try again later or inform someone.");
        }
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
