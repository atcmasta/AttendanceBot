package nl.jkalter.discord.attendance.exit;

import nl.jkalter.discord.attendance.module.ICommandModule;
import nl.jkalter.discord.attendance.module.support.Command;
import nl.jkalter.discord.attendance.module.support.CommandName;
import nl.jkalter.discord.attendance.module.support.ICommand;
import nl.jkalter.discord.facade.author.IAuthor;
import nl.jkalter.discord.facade.IEventDispatcherFacade;
import nl.jkalter.discord.facade.IMessageCreateEventFacade;
import nl.jkalter.discord.facade.IMessageCreateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExitHandler implements ICommandModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExitHandler.class);

    private final Command command;
    private final ExitSignal exitSignal;

    public ExitHandler(ExitSignal exitSignal) {
        command = new Command(CommandName.EXIT, "");
        this.exitSignal = exitSignal;
    }

    @Override
    public void enable(IEventDispatcherFacade eventDispatcherFacade) {
        eventDispatcherFacade.onMessageCreateEvent(this);
    }

    public void onMessageReceivedEvent(IMessageCreateEventFacade event) {

        if (command.isMyCommand(event)) {

            IAuthor author = event.getAuthor();
            final String authorName = author.getAuthorName();
            final long authorId = author.getAuthorId();

            if (command.isAuthorizedRole(event)) {
                LOGGER.info("Exit commandName received from {} ({}), shutting down.", authorName, authorId);

                exitSignal.setExit(true);

                synchronized (exitSignal) {
                    exitSignal.notifyAll();
                }
            } else {
                LOGGER.info("User {} ({}) is not authorized to use the {} command.", authorName, authorId, command);
            }
        }
    }

    @Override
    public ICommand getCommand() {
        return command;
    }
}
