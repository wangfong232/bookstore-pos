

package entity;

import java.sql.Time;


public class AttendanceView {
    private int attendanceId;
    private String fullName;
    private String shiftName;
    private Time startTime;
    private Time endTime;
    private Time checkIn;
    private Time checkOut;
    /** Trạng thái thời gian ca: NOT_YET | ACTIVE | EXPIRED | WORKING | COMPLETED */
    private String shiftStatus;

    public AttendanceView() {
    }

    public AttendanceView(int attendanceId, String fullName, String shiftName, Time startTime, Time endTime,
            Time checkIn, Time checkOut) {
        this.attendanceId = attendanceId;
        this.fullName = fullName;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Time getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Time checkIn) {
        this.checkIn = checkIn;
    }

    public Time getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Time checkOut) {
        this.checkOut = checkOut;
    }
    //Thêm attribute shiftStatus
    public String getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(String shiftStatus) {
        this.shiftStatus = shiftStatus;
    }
}
