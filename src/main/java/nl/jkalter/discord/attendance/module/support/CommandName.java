package nl.jkalter.discord.attendance.module.support;

public enum CommandName {
    ATTEND("attend"), AVATAR("avatar"), CLEAR("clear"), CREATE("create"), LIST("list"), LISTS("lists"), REMOVE("remove"), EXIT("exit");

    private String command;

    private CommandName(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return this.command;
    }
}
