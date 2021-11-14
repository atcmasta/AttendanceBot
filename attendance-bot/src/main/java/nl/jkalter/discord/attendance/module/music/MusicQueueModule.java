package nl.jkalter.discord.attendance.module.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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

import java.util.List;

public class MusicQueueModule implements ICommandModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicQueueModule.class);
    public static final int MAX_DISPLAYABLE_QUEUE = 10;

    private final Command command;
    private final AudioModuleManager manager;

    public MusicQueueModule(AudioModuleManager manager) {
        this.manager = manager;
        command = new Command(CommandName.QUEUE, "");
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

        if (command.isAuthorizedRole(event)) {
            IAuthor author = event.getAuthor();
            final String authorName = author.getAuthorName();

            List<AudioTrack> queue = manager.getQueue();
            StringBuilder queueMessage = new StringBuilder();

            queueMessage.append("Queue (");
            queueMessage.append(queue.size());
            queueMessage.append("):");
            queueMessage.append(System.lineSeparator());

            for (int i = 0; i < queue.size() && i < MAX_DISPLAYABLE_QUEUE; i++) {
                queueMessage.append(String.format("[%s] %s %s", i+1, queue.get(i).getInfo().title, System.lineSeparator()));
            }

            if (queue.size() >  MAX_DISPLAYABLE_QUEUE) {
                queueMessage.append("..and ");
                queueMessage.append(queue.size() - MAX_DISPLAYABLE_QUEUE);
                queueMessage.append(" more.");
                queueMessage.append(System.lineSeparator());
            }

            event.sendMessage(queueMessage.toString());
        } else {
            LOGGER.info("User {} ({}) is not authorized to use the {} command.", event.getAuthor().getAuthorName(), event.getAuthor().getAuthorId(), command);
        }
    }

}
