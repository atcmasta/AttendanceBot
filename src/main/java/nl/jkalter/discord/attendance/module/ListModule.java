package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateListModule.class);

    private final Command command;

    public ListModule() {
        command = new Command(CommandName.LISTS, "");
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {

        IMessage message = event.getMessage();
        String messageContent = message.getContent().trim().toLowerCase();

        if (command.isMyCommand(messageContent)) {
            IUser author = message.getAuthor();
            String authorName = author.getName();
            long authorId = author.getLongID();

            LOGGER.debug("Trying to show attendance for {} ({})", authorName, authorId);
            final List<IRole> rolesForAuthor = event.getGuild().getRolesForUser(author);
            if (command.isAuthorizedRole(rolesForAuthor)) {
                final Collection<String> lists = AttendanceService.listAttendanceLists();

                if (lists.isEmpty()) {
                    event.getChannel().sendMessage("There are no lists available");
                } else if (lists.size() == 1) {
                    event.getChannel().sendMessage(String.format("There is one list available called %s", lists.iterator().next()));
                } else {
                    event.getChannel().sendMessage(String.format("The following lists are available: %s", lists.stream().collect(Collectors.joining(", "))));
                }
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }
        }
    }
}
