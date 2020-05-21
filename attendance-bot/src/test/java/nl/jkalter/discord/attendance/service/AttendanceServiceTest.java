package nl.jkalter.discord.attendance.service;

import nl.jkalter.discord.attendance.service.Attendance;
import nl.jkalter.discord.attendance.service.AttendanceService;
import nl.jkalter.discord.attendance.service.IAttendance;
import nl.jkalter.discord.attendance.service.UserAttendance;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class AttendanceServiceTest {

    @Test
    public void writeEmptyAttendance() throws IOException {
        final String listName = "writeEmptyAttendance";
        Assert.assertFalse(String.format("Expecting %s to not exist prior to the test", listName), AttendanceService.listExists(listName));

        final Collection<IAttendance> attendance = new ArrayList<>();
        AttendanceService.writeAttendance(listName, attendance);

        Assert.assertTrue(String.format("Expecting %s to exist after the test.", listName), AttendanceService.listExists(listName));
        Assert.assertTrue(String.format("Expecting %s to be deletable after the test", listName), AttendanceService.removeAttendance(listName));
    }

    @Test
    public void writeReadAttendance() throws IOException {
        final String listName = "writeReadAttendance";
        Assert.assertFalse(String.format("Expecting %s to not exist prior to the test", listName), AttendanceService.removeAttendance(listName));

        final Collection<IAttendance> beforeAttendance = new ArrayList<>();
        beforeAttendance.add(new UserAttendance(ThreadLocalRandom.current().nextLong(), Attendance.UNKNOWN));
        beforeAttendance.add(new UserAttendance(ThreadLocalRandom.current().nextLong(), Attendance.YES));
        beforeAttendance.add(new UserAttendance(ThreadLocalRandom.current().nextLong(), Attendance.NO));

        AttendanceService.writeAttendance(listName, beforeAttendance);
        Assert.assertTrue(String.format("Expecting %s to exist after the test", listName), AttendanceService.listExists(listName));

        final Collection<IAttendance> afterAttendance = AttendanceService.readAttendance(listName);

        Assert.assertArrayEquals("Expecting the written and read attendance to be the same.", beforeAttendance.toArray(), afterAttendance.toArray());
        Assert.assertTrue(String.format("Expecting %s to be deletable after the test", listName), AttendanceService.removeAttendance(listName));
    }

    @Test
    public void createAttendance() throws IOException {
        final String listName = "createAttendance";

        Assert.assertTrue(String.format("Expecting to be able to create a new list %s", listName), AttendanceService.createAttendance(listName));
        Assert.assertTrue(String.format("Expecting %s to exist after creation", listName), AttendanceService.listExists(listName));
        Assert.assertFalse(String.format("Expecting to be able to create a new list (%s) only once", listName), AttendanceService.createAttendance(listName));

        Assert.assertTrue(String.format("Expecting to be able to remove the created list %s", listName), AttendanceService.removeAttendance(listName));
    }

    @Test
    public void setAttendance() throws IOException {
        final String listName = "setAttendance";
        final long userId1 = 1L;
        final long userId2 = 2L;

        Assert.assertTrue(String.format("Expecting to be able to create a new list %s", listName), AttendanceService.createAttendance(listName));
        Assert.assertTrue(String.format("Expecting list %s to exist after creation", listName), AttendanceService.listExists(listName));
        Assert.assertTrue(String.format("Expecting to be able to set attendance for user %s to %s in list %s", userId1, Attendance.LATE, listName), AttendanceService.setAttendance(userId1, listName, Attendance.LATE));
        Collection<IAttendance> attendance = AttendanceService.readAttendance(listName);
        Optional<IAttendance> any1 = attendance.stream().filter(iAttendance -> iAttendance.getUserId() == userId1).findAny();
        Assert.assertTrue(String.format("Expecting to find user with id %s", userId1), any1.isPresent());
        Assert.assertEquals(String.format("Expecting to find user with id %s to have the correct attendance", userId1), Attendance.LATE, any1.get().getAttendance());
        Assert.assertTrue(String.format("Expecting to be able to set attendance for user %s to %s in list %s", userId2, Attendance.YES, listName), AttendanceService.setAttendance(userId2, listName, Attendance.YES));
        attendance = AttendanceService.readAttendance(listName);
        any1 = attendance.stream().filter(iAttendance -> iAttendance.getUserId() == userId1).findAny();
        Assert.assertTrue(String.format("Expecting to find user with id %s", userId1), any1.isPresent());
        Assert.assertEquals(String.format("Expecting to find user with id %s to have the same attendance", userId1), Attendance.LATE, any1.get().getAttendance());
        Optional<IAttendance> any2 = attendance.stream().filter(iAttendance -> iAttendance.getUserId() == userId2).findAny();
        Assert.assertTrue(String.format("Expecting to find user with id %s", userId1), any2.isPresent());
        Assert.assertEquals(String.format("Expecting to find user with id %s to have the same attendance", userId2), Attendance.YES, any2.get().getAttendance());
        Assert.assertTrue(String.format("Expecting to be able override the attendance for user %s to %s in list %s.", userId1, Attendance.VACATION, listName), AttendanceService.setAttendance(userId1, listName, Attendance.VACATION));
        attendance = AttendanceService.readAttendance(listName);
        any1 = attendance.stream().filter(iAttendance -> iAttendance.getUserId() == userId1).findAny();
        Assert.assertTrue(String.format("Expecting to find user with id %s", userId1), any1.isPresent());
        Assert.assertEquals(String.format("Expecting to find user with id %s to have the same attendance", userId1), Attendance.VACATION, any1.get().getAttendance());
        any2 = attendance.stream().filter(iAttendance -> iAttendance.getUserId() == userId2).findAny();
        Assert.assertTrue(String.format("Expecting to find user with id %s", userId1), any2.isPresent());
        Assert.assertEquals(String.format("Expecting to find user with id %s to have the same attendance", userId2), Attendance.YES, any2.get().getAttendance());

        Assert.assertTrue(String.format("Expecting to be able to remove list %s", listName), AttendanceService.removeAttendance(listName));
    }

    @Test
    public void removeAttendance() throws IOException {
        final String listName = "removeAttendance";

        Assert.assertTrue(String.format("Expecting to be able to create a new list %s", listName), AttendanceService.createAttendance(listName));
        Assert.assertTrue(String.format("Expecting %s to exist after creation", listName), AttendanceService.listExists(listName));
        Assert.assertTrue(String.format("Expecting to be able to remove list %s", listName), AttendanceService.removeAttendance(listName));
        Assert.assertFalse(String.format("Expecting %s to not exist after removal", listName), AttendanceService.listExists(listName));
        Assert.assertFalse(String.format("Expecting to be able to remove list %s only once", listName), AttendanceService.removeAttendance(listName));
    }

    @Test
    public void listAttendanceLists() throws Exception {
        final String listName = "listAttendanceLists";

        // determine initial situation
        final Collection<String> initial = AttendanceService.listAttendanceLists();
        Assert.assertTrue("Test needs to be able to create a new attendance list", AttendanceService.createAttendance(listName));
        final Collection<String> increased = AttendanceService.listAttendanceLists();
        Assert.assertEquals("Expecting the list of attendance lists to have grown by one", initial.size() + 1, increased.size());
        Assert.assertTrue("Expecting the list of attendance lists to have grown by one", increased.contains(listName));

        Assert.assertTrue("Test needs to be able to remove the created attendance list", AttendanceService.removeAttendance(listName));
        final Collection<String> decreased = AttendanceService.listAttendanceLists();
        Assert.assertEquals("Expecting the list of attendance lists to have one entry less.", increased.size() - 1, decreased.size());
        Assert.assertFalse("Expecting the list of attendance lists to not contain the one we created here", decreased.contains(listName));
    }

}