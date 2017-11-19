package nl.jkalter.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.HashMap;
import java.util.Map;

public class AttendanceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceHandler.class);
    private static final String COMMAND = "!attend";
    private String command;

    private Map<Long, UserAttendance> attendance = new HashMap<Long, UserAttendance>();

    public AttendanceHandler() {
        this(COMMAND);
    }

    public AttendanceHandler(String command) {
        this.command = command;
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        LOGGER.info(String.format("MessageReceivedEvent: %s", event));

        IMessage message = event.getMessage();
        IUser author = message.getAuthor();
        String messageContent = message.getContent().trim().toLowerCase();

        if (isAttendanceMessage(messageContent)) {
            messageContent = messageContent.replaceFirst(command, "").trim();

            if (messageContent.length() == 0 || messageContent.startsWith("help") || messageContent.startsWith("info")) {
                sendAttendanceHelp(author);
            } else if (messageContent.startsWith("yes")) {
                if (findAttendance(author).setAttendance(Attendance.YES)) {
                    AttendanceFile.writeAttendance(attendance.values());
                }
            } else if (messageContent.startsWith("no")) {
                if (findAttendance(author).setAttendance(Attendance.NO)) {
                    AttendanceFile.writeAttendance(attendance.values());
                }
            } else if (messageContent.startsWith("late")) {
                if (findAttendance(author).setAttendance(Attendance.LATE)) {
                    AttendanceFile.writeAttendance(attendance.values());
                }
            } else if (messageContent.startsWith("vacation")) {
                if (findAttendance(author).setAttendance(Attendance.VACATION)) {
                    AttendanceFile.writeAttendance(attendance.values());
                }
            } else if (messageContent.startsWith("unknown")) {
                if (findAttendance(author).setAttendance(Attendance.UNKNOWN)) {
                    AttendanceFile.writeAttendance(attendance.values());
                }
            } else if (messageContent.startsWith("clear")) {
                clearAttendance(message);
                AttendanceFile.writeAttendance(attendance.values());
            } else if (messageContent.startsWith("list")) {
                listAttendance(message);
            }
        }
    }

    private void clearAttendance(IMessage message) {
        LOGGER.info("Clearing attendance.");
        attendance.clear();
        message.getChannel().sendMessage("Cleared the attendance list");
    }

    private void listAttendance(IMessage message) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Attendance list (%s)\r\n", attendance.size()));
        attendance.forEach((id, userAttendance) -> sb.append(String.format("%s %s\r\n", userAttendance.getUser().getName(), userAttendance.getAttendance().name())));
        message.getChannel().sendMessage(sb.toString());
    }

    private void sendAttendanceHelp(IUser author) {
        String version = getClass().getPackage().getImplementationVersion();
        author.getOrCreatePMChannel().sendMessage(
                String.format("!attend yes no list clear help (%s)", version == null ? "dev" : version));
    }

    private UserAttendance findAttendance(IUser author) {
        attendance.putIfAbsent(author.getLongID(), new UserAttendance(author, Attendance.UNKNOWN));
        return attendance.get(author.getLongID());
    }

    private boolean isAttendanceMessage(String messageContent) {
        return messageContent.startsWith(command);
    }


}
