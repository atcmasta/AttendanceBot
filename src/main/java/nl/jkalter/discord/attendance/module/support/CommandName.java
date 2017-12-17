package nl.jkalter.discord.attendance.module.support;

public enum CommandName {
    ATTEND("attend"),
    AVATAR("avatar"),
    CLEAR("clear"),
    CREATE("create"),
    HELP("help"),
    LIST("list"),
    LISTS("lists"),
    REMOVE("remove"),
    EXIT("exit");

    private final String command;

    CommandName(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return this.command;
    }
}
