package nl.jkalter.discord.facade;

import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class EventDispatcherFacade implements IEventDispatcherFacade {

    private final EventDispatcher eventDispatcher;

    public EventDispatcherFacade(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Registers a listener for messages
     * @param listener the listener for which the onMessageReceivedEvent() will be called.
     */
    public void onMessageCreateEvent(IMessageCreateEventListener listener) {
        eventDispatcher.on(MessageCreateEvent.class, event ->  {
            listener.onMessageReceivedEvent(new MessageCreateEventFacade(event));
            return Mono.empty();
        }).subscribe();
    }
}
