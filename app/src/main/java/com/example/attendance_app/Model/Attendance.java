package com.example.attendance_app.Model;

public class Attendance {
    public String isPresent;
    public String date;

    public Attendance(String isPresent, String date) {
        this.isPresent = isPresent;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Attendance() {
    }

    public Attendance(String isPresent) {
        this.isPresent = isPresent;
    }

    public String getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(String isPresent) {
        this.isPresent = isPresent;
    }
}
