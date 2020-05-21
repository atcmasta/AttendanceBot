package nl.jkalter.discord.attendance.service;

public class UserAttendance implements IAttendance {
    static final String[] MAPPING = new String[] { "UserId", "Attendance"};

    private long userId;
    private Attendance attendance;

    public UserAttendance() {
        this.userId = -1;
        this.attendance = Attendance.UNKNOWN;
    }

    public UserAttendance(long userId, Attendance attendance) {
        this.userId = userId;
        this.attendance = attendance;
    }
    public long getUserId() {
        return userId;
    }

    @SuppressWarnings("unused")
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserAttendance
                && this.userId == ((UserAttendance)obj).getUserId()
                && this.getAttendance().equals(((UserAttendance)obj).getAttendance());
    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + attendance.hashCode();
        return result;
    }
}