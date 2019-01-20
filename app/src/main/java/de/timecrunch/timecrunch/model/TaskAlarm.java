package de.timecrunch.timecrunch.model;

public class TaskAlarm {

    private int year, month, hour, minute, day, repeatNo;
    private String repeatType;
    private boolean repeat;

    public TaskAlarm(int year, int month, int hour, int minute, int day, boolean repeat,
                     int repeatNo, String repeatType) {
        this.year = year;
        this.month = month;
        this.hour = hour;
        this.minute = minute;
        this.day = day;
        this.repeat = repeat;
        this.repeatNo = repeatNo;
        this.repeatType = repeatType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getRepeatNo() {
        return repeatNo;
    }

    public void setRepeatNo(int repeatNo) {
        this.repeatNo = repeatNo;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}
