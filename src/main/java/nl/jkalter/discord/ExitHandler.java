package nl.jkalter.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;

import java.util.LinkedList;
import java.util.List;

public class ExitHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(ExitHandler.class);
    private final List<Long> owners = new LinkedList<Long>();
    private final Object exitSignal;

    public ExitHandler(Object exitSignal) {
        this.exitSignal = exitSignal;
        owners.add(340769613828587522l);
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        if (isOwner(event) && event.getMessage().getContent().startsWith("exit")) {
            LOGGER.info(String.format("Exit command received from %s (%s), shutting down.", event.getAuthor().getName(), event.getAuthor().getLongID()));
            for (IChannel channel : event.getClient().getChannels(false)) {
                channel.sendMessage(String.format("Exit received from %s", event.getAuthor().getName()));
            }
            synchronized (exitSignal) {
                exitSignal.notify();
            }
        }
    }

    private boolean isOwner(MessageReceivedEvent event) {
        return owners.contains(event.getAuthor().getLongID());
    }
}
