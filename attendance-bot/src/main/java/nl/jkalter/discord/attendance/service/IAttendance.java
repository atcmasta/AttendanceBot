package nl.jkalter.discord.attendance.service;

public interface IAttendance {

    long getUserId();

    Attendance getAttendance();

    void setAttendance(Attendance attendance);

}
