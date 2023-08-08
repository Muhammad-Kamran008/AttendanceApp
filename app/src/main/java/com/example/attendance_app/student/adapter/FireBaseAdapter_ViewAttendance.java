package com.example.attendance_app.student.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance_app.R;
import com.example.attendance_app.Model.Attendance;


import java.util.ArrayList;

public class FireBaseAdapter_ViewAttendance extends RecyclerView.Adapter<FireBaseAdapter_ViewAttendance.AttendanceViewHolder> {

    private ArrayList<Attendance> attendanceList;
    private Context context;

    public FireBaseAdapter_ViewAttendance(ArrayList<Attendance> attendanceList, Context context) {
        this.attendanceList = attendanceList;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_attendance_item, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);

        String isPresent = attendance.getIsPresent();

        holder.setStatus(isPresent);
        holder.setDate(attendance.getDate());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView statusView, dateView;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            statusView = mView.findViewById(R.id.statusViewAttendance);
            dateView = mView.findViewById(R.id.dateViewAttendance);
        }

        public void setStatus(String status) {
            if (status.equals("Absent")) {
                statusView.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else if (status.equals("Present")) {
                statusView.setTextColor(ContextCompat.getColor(context, R.color.light_green));
            } else if (status.equals("Leave")) {
                statusView.setTextColor(ContextCompat.getColor(context, R.color.orange));
            }
            statusView.setText("Status: " + status);
        }
        public void setDate(String date) {
            dateView.setText("Date: " + date);
        }
    }
}
