package entity;

public class AttendanceStats {

    private int employeeId;
    private String fullName;
    private long totalMinutes; // tổng số phút đã làm (tính từ CheckIn → CheckOut)
    private int workDays;      // số ngày có mặt (có cả CheckIn và CheckOut)

    public AttendanceStats() {
    }

    public AttendanceStats(int employeeId, String fullName, long totalMinutes, int workDays) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.totalMinutes = totalMinutes;
        this.workDays = workDays;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public int getWorkDays() {
        return workDays;
    }

    public void setWorkDays(int workDays) {
        this.workDays = workDays;
    }

    /**
     * Trả về chuỗi định dạng "X giờ Y phút" từ totalMinutes.
     */
    public String getTotalHoursFormatted() {
        long hours = totalMinutes / 60;
        long mins  = totalMinutes % 60;
        return hours + " giờ " + mins + " phút";
    }
}
