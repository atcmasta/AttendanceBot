package nl.jkalter.discord.facade;

public interface IMessageCreateEventListener {

    /**
     * Method that is called when a message is created
     * @param event the event containing the message
     */
    void onMessageReceivedEvent(IMessageCreateEventFacade event);

}
