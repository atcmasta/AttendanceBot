package nl.jkalter.discord.attendance.module.support;

public enum CommandName {
    ATTEND("attend"),
    AVATAR("avatar"),
    CLEAR("clear"),
    CREATE("create"),
    HELP("help"),
    LEAVE("leave"),
    LIST("list"),
    LISTS("lists"),
    PLAY("play"),
    QUEUE("queue"),
    NEXT("next"),
    REMOVE("remove"),
    PAUSE("pause"),
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
