package com.example.attendance_app.Model;

import java.util.ArrayList;
import java.util.List;

public class MyAttendance {
    private String date;
    private ArrayList<customAttendance> customAttendanceList;

    public MyAttendance(String date, ArrayList<customAttendance> customAttendanceList) {
        this.date = date;
        this.customAttendanceList = customAttendanceList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<customAttendance> getCustomAttendanceList() {
        return customAttendanceList;
    }

    public void setCustomAttendanceList(ArrayList<customAttendance> customAttendanceList) {
        this.customAttendanceList = customAttendanceList;
    }
}
