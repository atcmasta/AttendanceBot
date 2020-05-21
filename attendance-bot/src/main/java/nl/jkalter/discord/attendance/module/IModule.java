package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.facade.IEventDispatcherFacade;

public interface IModule {

    /**
     * Instructs a module to enable itself
     * @param eventDispatcher the event dispatcher, in order to be able to subscribe to events
     */
    void enable(IEventDispatcherFacade eventDispatcher);

}
