package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.facade.IEventDispatcherFacade;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.IMessageCreateEventListener;
import nl.jkalter.discord.facade.author.IAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Calendar;

public class AvatarModule implements ICommandHelpModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarModule.class);
    private static final int AVATAR_URL_LENGTH_LIMIT = 2083;

    private final Command command;
    private Calendar nextUpdate;

    public AvatarModule() {
        command = new Command(CommandName.AVATAR);
        rescheduleNextUpdate();
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

        if (Calendar.getInstance().after(nextUpdate)) {
            LOGGER.debug("Trying to set avatar for {} ({})", authorName, authorId);

            if (command.isAuthorizedRole(event)) {
                updateAvatar(event);
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }
        } else {
            final Duration between = Duration.between( Calendar.getInstance().toInstant(), nextUpdate.toInstant());
            event.sendMessage(String.format("It is not time yet %s, wait %s more seconds.", authorName, between.getSeconds()));
        }
    }

    private void updateAvatar(IMessageCreateEventFacade event) {
        String authorName = event.getAuthor().getAuthorName();
        String messageContent = command.removeCommand(event);

        final String[] lists = messageContent.split(" ");
        if (lists.length > 0) {
            String url = lists[0];

            if (isUrl(url)) {
                event.getClient().setAvatar(url);

                rescheduleNextUpdate();

                event.sendMessage(String.format("I am trying to adjust the avatar for you %s", authorName));
            }
        }
    }

    private void rescheduleNextUpdate() {
        nextUpdate = Calendar.getInstance();
        nextUpdate.add(Calendar.MINUTE, 1);
    }

    private boolean isUrl(String url) {
        return url.length() <= AVATAR_URL_LENGTH_LIMIT && (url.startsWith("http://") || url.startsWith("https://"));
    }

    @Override
    public ICommand getCommand() {
        return command;
    }

    @Override
    public String getHelp() {
        return String.format("%s %s (rate limited)", getCommand().getFullCommandName(), "http(s)://example.com/image.(png|jpg|jpeg|webp|svg)");
    }
}
