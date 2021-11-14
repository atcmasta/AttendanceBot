package nl.jkalter.discord.attendance.module.music;

import nl.jkalter.discord.attendance.module.ICommandModule;
import nl.jkalter.discord.attendance.module.music.manager.AudioModuleManager;
import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.facade.IEventDispatcherFacade;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.IMessageCreateEventListener;
import nl.jkalter.discord.facade.author.IAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayMusicModule implements ICommandModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayMusicModule.class);

    private static final int MUSIC_URL_LENGTH_LIMIT = 2083;
    private final Command command;
    private final AudioModuleManager manager;

    public PlayMusicModule(AudioModuleManager manager) {
        this.manager = manager;
        this.command = new Command(CommandName.PLAY);
    }

    @Override
    public ICommand getCommand() {
        return command;
    }

    @Override
    public void enable(IEventDispatcherFacade eventDispatcherFacade) {
        eventDispatcherFacade.onMessageCreateEvent(this);
    }

    @Override
    public void onMessageReceivedEvent(IMessageCreateEventFacade event) {
        if (!command.isMyCommand(event)) {
            return;
        }

        IAuthor author = event.getAuthor();
        final String authorName = author.getAuthorName();
        final long authorId = author.getAuthorId();

        playSong(event);
    }

    private void playSong(IMessageCreateEventFacade event) {
        String authorName = event.getAuthor().getAuthorName();
        String messageContent = command.removeCommand(event);

        if (command.isAuthorizedRole(event)) {
            final String[] lists = messageContent.split(" ");
            if (lists.length > 0) {
                String url = lists[0];

                if (isUrl(url)) {
                    LOGGER.info("Joining voice channel");
                    manager.joinVoice(event);
                    LOGGER.info("Loading item for {}", url);
                    manager.loadItem(url);
                    event.sendMessage(String.format("Planning to play %s for you %s", url, authorName));
                }
            }
        } else {
            LOGGER.info("User {} ({}) is not authorized to use the {} command.", event.getAuthor().getAuthorName(), event.getAuthor().getAuthorId(), command);
        }
    }

    private boolean isUrl(String url) {
        return url.length() <= MUSIC_URL_LENGTH_LIMIT && (url.startsWith("http://") || url.startsWith("https://"));
    }
}
