package nl.jkalter.discord.facade;

public interface IEventDispatcherFacade {

    /**
     * Registers a listener for messages
     * @param listener the listener for which the onMessageReceivedEvent() will be called.
     */
    void onMessageCreateEvent(IMessageCreateEventListener listener);
}
