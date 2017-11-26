package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.Image;

import java.util.*;

public class AvatarModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttendModule.class);
    private static final Map<String, String> EXTENSION_TYPE_MAPPING = getMapping();

    private final Command command;
    private Calendar nextUpdate;

    public AvatarModule() {
        command = new Command(CommandName.AVATAR);
        rescheduleNextUpdate();
    }

    private static Map<String, String> getMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put(".jpg", "image/jpeg");
        mapping.put(".jpeg", "image/jpeg");
        mapping.put(".png", "image/png");
        mapping.put(".webp", "image/webp");
        mapping.put(".svg", "image/svg+xml");
        return mapping;
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String messageContent = message.getContent().trim().toLowerCase();

        if (command.isMyCommand(messageContent)) {
            messageContent = command.removeCommand(messageContent);
            IUser author = message.getAuthor();
            String authorName = author.getName();
            long authorId = author.getLongID();

            if (Calendar.getInstance().after(nextUpdate)) {
                LOGGER.debug("Trying to set avatar for {} ({})", authorName, authorId);
                final List<IRole> rolesForAuthor = event.getGuild().getRolesForUser(author);
                if (command.isAuthorizedRole(rolesForAuthor)) {
                    final String[] lists = messageContent.split(" ");
                    if (lists.length > 0) {
                        String url = lists[0];

                        if (isUrl(url)) {
                            String mimeType = determineType(url);
                            if (mimeType == null) {
                                event.getChannel().sendMessage(String.format("I do not think I can make an avatar of that %s ;)", authorName));
                            } else {
                                event.getClient().changeAvatar(Image.forUrl(mimeType, url));

                                rescheduleNextUpdate();

                                event.getChannel().sendMessage(String.format("I am trying to adjust the avatar for you %s", authorName));
                            }
                        }
                    }
                } else {
                    LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
                }
            } else {
                event.getChannel().sendMessage(String.format("It is not time yet %s wait for it..", authorName));
            }
        }
    }

    private void rescheduleNextUpdate() {
        nextUpdate = Calendar.getInstance();
        nextUpdate.add(Calendar.MINUTE, 1);
    }

    protected final String determineType(String url) {
        String type = null;

        final Optional<String> match = EXTENSION_TYPE_MAPPING.keySet().stream().filter(url::endsWith).findFirst();
        if (match.isPresent()) {
            String key = match.get();
            type = EXTENSION_TYPE_MAPPING.get(key);
        }
        return type;
    }

    private boolean isUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }
}
