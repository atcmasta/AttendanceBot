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

public class LeaveVoiceModule implements ICommandModule, IMessageCreateEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaveVoiceModule.class);

    private final Command command;
    private final AudioModuleManager manager;

    public LeaveVoiceModule(AudioModuleManager manager) {
        this.manager = manager;
        command = new Command(CommandName.LEAVE, "");
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

            manager.leaveVoice();
            event.sendMessage(String.format("Leaving the voice channel for %s", authorName));
        } else {
            LOGGER.info("User {} ({}) is not authorized to use the {} command.", event.getAuthor().getAuthorName(), event.getAuthor().getAuthorId(), command);
        }
    }

}
