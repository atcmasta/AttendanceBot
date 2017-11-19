package nl.jkalter.discord;

import sx.blah.discord.handle.obj.IUser;

public class UserAttendance {
    private IUser user;
    private Attendance attendance;

    public UserAttendance(IUser user, Attendance attendance) {
        this.user = user;
        this.attendance = attendance;
    }
    public IUser getUser() {
        return user;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public boolean setAttendance(Attendance attendance) {
        boolean result = false;
        if (!this.attendance.equals(attendance)) {
            this.attendance = attendance;
            result = true;
        }
        return result;
    }
}