package com.example.attendance_app.admin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance_app.Model.MyAttendance;
import com.example.attendance_app.Model.customAttendance;
import com.example.attendance_app.R;
import com.example.attendance_app.admin.adapter.FireBaseAdapter_ViewAllAttendance;
import com.example.attendance_app.Model.Attendance;
import com.example.attendance_app.student.adapter.FireBaseAdapter_ViewAttendance;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAllAttendanceActivity extends AppCompatActivity {

    private FireBaseAdapter_ViewAllAttendance adapter;
    private RecyclerView recyclerView;

    private DatabaseReference mAttendanceDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_attendance);

        recyclerView = findViewById(R.id.rec_ViewAllAttendance);

        Log.wtf("-this", "48 Line");

        mAttendanceDatabase = FirebaseDatabase.getInstance().getReference().child("Attendance");

        Query query = mAttendanceDatabase.orderByKey();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getAttendanceData();


    }

    private void getAttendanceData() {
        mAttendanceDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<MyAttendance> myAttendanceArrayList=new ArrayList<>();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey(); // Get the date from the snapshot key
                    ArrayList<customAttendance> attendanceList = new ArrayList<>();

                    for (DataSnapshot rollSnapshot : dateSnapshot.getChildren()) {
                        String rollNumber = rollSnapshot.getKey();
                        Attendance attendance = rollSnapshot.getValue(Attendance.class);
                      //  Log.d("-this", "onDataChange: attendance " + attendance.getIsPresent());
                        // Add each attendance entry for a specific roll number on the date
                        attendanceList.add(new customAttendance(date, rollNumber, attendance.getIsPresent()));
                    }

                    myAttendanceArrayList.add(new MyAttendance(date,attendanceList));




                }
                Log.d("-this", "onDataChange: "+myAttendanceArrayList.get(1).getCustomAttendanceList().size());

                adapter = new FireBaseAdapter_ViewAllAttendance(myAttendanceArrayList, ViewAllAttendanceActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(ViewAllAttendanceActivity.this, AdminHomeActivity.class));
        finish();
    }
}