package com.example.attendance_app.admin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance_app.Model.customAttendance;
import com.example.attendance_app.R;
import com.example.attendance_app.Model.Attendance;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FireBaseChildAdapter_ViewAllAttendance extends RecyclerView.Adapter<
        FireBaseChildAdapter_ViewAllAttendance.ViewAllAttendanceChildViewHolder> {
    private static Context context;
    private ArrayList<customAttendance> attendanceList;

    public FireBaseChildAdapter_ViewAllAttendance(ArrayList<customAttendance> attendanceList, Context context) {
        this.attendanceList = attendanceList;
        this.context = context;
    }

    @NotNull
    @Override
    public ViewAllAttendanceChildViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_all_attendance_child_item, parent, false);
        return new FireBaseChildAdapter_ViewAllAttendance.ViewAllAttendanceChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FireBaseChildAdapter_ViewAllAttendance.ViewAllAttendanceChildViewHolder holder, int position) {
        customAttendance attendance = attendanceList.get(position);
        String roll_number = attendance.getRollNumber();
        holder.setRollNumberView(roll_number);
        holder.setStatusView(attendance.getStatus());

        // Get student's name from Firebase
        getStudentNameFromFirebase(roll_number, new OnGetNameListener() {
            @Override
            public void onGetName(String name) {
                holder.setNameView(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }

    public static class ViewAllAttendanceChildViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView rollNumberView, nameView, statusView;

        public ViewAllAttendanceChildViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.wtf("-this", "Child View Holder");
            mView = itemView;
            rollNumberView = mView.findViewById(R.id.rollNumber_AllAttendance_Child_item);
            nameView = mView.findViewById(R.id.name_AllAttendance_Child_item);
            statusView = mView.findViewById(R.id.status_AllAttendance_Child_item);
        }

        public void setNameView(String name) {
            this.nameView.setText(name);
        }

        public void setRollNumberView(String rollNumber) {
            this.rollNumberView.setText(rollNumber);
        }

        public void setStatusView(String status) {

            if (status.contentEquals("Present")) {
                statusView.setTextColor(ContextCompat.getColor(context, R.color.light_green));
                this.statusView.setText("Status: " + status);
            } else if (status.contentEquals("Absent")) {
                statusView.setTextColor(ContextCompat.getColor(context, R.color.red));
                this.statusView.setText("Status: " + status);
            } else if (status.contentEquals("Leave")) {
                statusView.setTextColor(ContextCompat.getColor(context, R.color.orange));
                this.statusView.setText("Status: " + status);
            }


        }
    }

    // Interface to listen for the student's name
    public interface OnGetNameListener {
        void onGetName(String name);
    }

    // Function to get the student's name from Firebase
    private void getStudentNameFromFirebase(String rollNumber, OnGetNameListener listener) {
        DatabaseReference mStudentDatabase = FirebaseDatabase.getInstance().getReference().child("Students");

        mStudentDatabase.orderByChild("roll_number").equalTo(rollNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null) {
                        listener.onGetName(name);
                        break; // We got the name, no need to continue the loop
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }

}
