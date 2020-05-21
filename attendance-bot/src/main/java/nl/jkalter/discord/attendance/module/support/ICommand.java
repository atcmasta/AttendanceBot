package nl.jkalter.discord.attendance.module.support;

public interface ICommand {

    String getFullCommandName();

    CommandName getCommandName();
}
