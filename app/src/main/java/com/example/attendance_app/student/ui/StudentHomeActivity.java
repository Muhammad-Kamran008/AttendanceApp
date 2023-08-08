package com.example.attendance_app.student.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.attendance_app.MainActivity;
import com.example.attendance_app.R;
import com.example.attendance_app.student.fragments.MarkAttendanceFragment;
import com.example.attendance_app.student.fragments.RequestLeaveFragment;
import com.example.attendance_app.student.fragments.ViewAttendanceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentHomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private ImageView markAttendance, requestLeave, viewAttendance;


    public BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    // private ImageView profile, threeDots;

    // private Button signOutBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mStudentDatabase;

    public static String mCurrent_user_id;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);


//        signOutBtn = findViewById(R.id.signOut_studentHome);
//        profile = findViewById(R.id.profile_StudentHome);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mStudentDatabase = FirebaseDatabase.getInstance().getReference().child("Students");

        mStudentDatabase.child(mCurrent_user_id).child("roll_number")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String reg_no = snapshot.getValue().toString();

                        SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                        editor.putString("roll_number", reg_no);
                        editor.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        bottomNavigationView = findViewById(R.id.bottomNavigationID);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setBackground(null);

        fragment = new MarkAttendanceFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();


    }


    private String getCurrentDate() {
        Date cd = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return df.format(cd);
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    public void dummyClick(View view) {
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.markAttendance_Home:
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                fragment = new MarkAttendanceFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
                break;
            case R.id.viewAttendance_Home:
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                fragment = new ViewAttendanceFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
                break;
            case R.id.requestLeave_Home:
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                fragment = new RequestLeaveFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, fragment);
                fragmentTransaction.commit();
                break;

            case R.id.profile_studentHome:

                startActivity(new Intent(StudentHomeActivity.this, ProfileActivity.class));
                finish();
                break;


        }
        return true;




    }

}