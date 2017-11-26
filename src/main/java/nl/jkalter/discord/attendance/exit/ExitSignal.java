package nl.jkalter.discord.attendance.exit;

public class ExitSignal {

    boolean exit = false;

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

}
