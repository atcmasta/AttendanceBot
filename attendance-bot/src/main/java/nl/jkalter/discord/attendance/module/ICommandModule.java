package nl.jkalter.discord.attendance.module;

import nl.jkalter.discord.attendance.module.support.ICommand;

public interface ICommandModule extends IModule {

    ICommand getCommand();
}
