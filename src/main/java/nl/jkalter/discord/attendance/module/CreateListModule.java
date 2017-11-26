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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateListModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateListModule.class);
    private static final int LIST_NAME_LENGTH = 16;
    private static final String LIST_REGEX = "[a-zA-Z0-9]+([a-zA-Z0-9-_]*[a-zA-Z0-9])*";

    private final Command command;

    public CreateListModule() {
        command = new Command(CommandName.CREATE);
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

                LOGGER.debug("Trying to add list(s) for {} ({})", authorName, authorId);
                final List<IRole> rolesForAuthor = event.getGuild().getRolesForUser(author);
                if (command.isAuthorizedRole(rolesForAuthor)) {
                    messageContent = command.removeCommand(messageContent);

                    final String[] lists = messageContent.split(" ");
                    final List<String> addedLists = new ArrayList<>();

                    for (String list : lists) {
                        if (list.length() > LIST_NAME_LENGTH) {
                            LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it is longer than {} characters.", list, authorName, authorId, LIST_NAME_LENGTH);
                        } else if (!list.matches(LIST_REGEX)) {
                            LOGGER.info("List with name {} suggested by {} ({}) is not allowed as it contains characters other than a-z, A-Z, 0-9.", list, authorName, authorId);
                        } else if (AttendanceService.createAttendance(list)) {
                            LOGGER.info("Added list with name {} for {} ({})", list, authorName, authorId);
                            addedLists.add(list);
                        }
                    }
                    respond(event, authorName, addedLists);
                } else {
                    LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
                }
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Exception occured handling message (%s)", event.getMessage().getContent()), e);
            event.getAuthor().getOrCreatePMChannel().sendMessage("Something went horribly wrong, maybe try again later or inform someone.");
        }
    }

    private void respond(MessageReceivedEvent event, String authorName, List<String> added) {
        if (added.isEmpty()) {
            event.getChannel().sendMessage(String.format("I did not create any lists for you %s ;)", authorName));
        } else if (added.size() == 1) {
            event.getChannel().sendMessage(String.format("I created the %s list for you %s.", added.stream().collect(Collectors.joining(", ")), authorName));
        } else {
            event.getChannel().sendMessage(String.format("I created %s lists (%s) for you %s.", added.size(), added.stream().collect(Collectors.joining(", ")), authorName));
        }
    }

}
