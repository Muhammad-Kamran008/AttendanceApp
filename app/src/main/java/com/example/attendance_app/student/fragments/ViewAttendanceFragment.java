package com.example.attendance_app.student.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance_app.R;
import com.example.attendance_app.Model.Attendance;
import com.example.attendance_app.Model.Student;
import com.example.attendance_app.student.adapter.FireBaseAdapter_ViewAttendance;
import com.example.attendance_app.student.ui.StudentHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewAttendanceFragment extends Fragment implements View.OnClickListener{

    private FireBaseAdapter_ViewAttendance adapter;
    private RecyclerView mAttendanceList;
    private TextView mAttendanceLabel;

    private DatabaseReference mAttendanceDatabase;
    private DatabaseReference mStudentsDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    public static String mCurrent_user_id;

    private String currentDate = getCurrentDate();


    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;

    public ViewAttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_attendance, container, false);

        mAttendanceList = view.findViewById(R.id.recyclerView_ViewAttendance);
        CardView backPage = view.findViewById(R.id.backFromEmployeesIdA);
        backPage.setOnClickListener(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAttendanceDatabase = FirebaseDatabase.getInstance().getReference().child("Attendance");
        mStudentsDatabase = FirebaseDatabase.getInstance().getReference().child("Students");

        mAttendanceList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the student object for the current user
        getStudentObject(currentUser.getUid());

        return view;
    }

    private void getStudentObject(String userId) {
        Log.d("-this", "getStudentObject: "+userId);
        mStudentsDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if (student != null) {
                        String rollNumber = student.getRoll_number();
                        Log.d("-this", "onDataChange: "+rollNumber);
                        // Fetch attendance data for the current date and the student's roll number
                        getAttendanceData(rollNumber);
                    }
                }
                else{
                    Log.d("-this", "onDataChange:  not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backFromEmployeesIdA) {
            ((StudentHomeActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new MarkAttendanceFragment();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment, "EMPLOYEE_FRAGMENT");
            fragmentTransaction.commit();
        }


    }
    private void getAttendanceData(String rollNumber) {
        mAttendanceDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Attendance> attendanceList = new ArrayList<>();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    if (dateSnapshot.hasChild(rollNumber)) {
                        // Assuming that if rollNumber exists as a child node under a date,
                        // the student is present on that date

                      //  Log.d("-this", "onDataChange date: "+date);
                        attendanceList.add(new Attendance("Present",date));
                    } else {
                        // If the rollNumber doesn't exist as a child node under a date,
                        // the student is absent on that date
                        attendanceList.add(new Attendance("Absent",date));
                    }
                }

                adapter = new FireBaseAdapter_ViewAttendance(attendanceList, getContext());
                mAttendanceList.setAdapter(adapter);
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

    private String getCurrentDate() {
        Date cd = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return df.format(cd);
    }
}