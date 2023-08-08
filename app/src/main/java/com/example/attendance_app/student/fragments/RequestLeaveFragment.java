package com.example.attendance_app.student.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance_app.R;
import com.example.attendance_app.Model.Leave;
import com.example.attendance_app.student.adapter.FireBaseAdapter_ReqLeave;
import com.example.attendance_app.student.ui.RequestLeaveActivity;
import com.example.attendance_app.student.ui.StudentHomeActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class RequestLeaveFragment extends Fragment implements View.OnClickListener {

    private FireBaseAdapter_ReqLeave adapter;
    private RecyclerView mReqLeaveList;
    private FloatingActionButton mMakeReqBtn;

    private DatabaseReference mLeaveDatabase;


    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private FirebaseAuth mAuth;

    public static String mCurrent_user_id;

    private SharedPreferences prefs;

    public RequestLeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_leave, container, false);
        CardView backPage = view.findViewById(R.id.backFromEmployeesIdA);
        backPage.setOnClickListener(this);
        mReqLeaveList = view.findViewById(R.id.recView_ReqLeaveFrg);
        mMakeReqBtn = view.findViewById(R.id.makeReqBtn_ReqLeaveFrg);

        prefs = getActivity().getSharedPreferences("MY_PREFS_NAME", getActivity().MODE_PRIVATE);
        String roll_number = prefs.getString("roll_number", "Empty");

        mAuth = FirebaseAuth.getInstance();
        //    mAuth = FirebaseAuth.getInstance();
        //    mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mLeaveDatabase = FirebaseDatabase.getInstance().getReference().child("leaves");

        Query query = mLeaveDatabase.orderByChild("roll_number").startAt(roll_number).endAt(roll_number);

        mReqLeaveList.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Leave>().setQuery(
                        query, Leave.class
                ).build();

        adapter = new FireBaseAdapter_ReqLeave(options, getContext());

        mReqLeaveList.setAdapter(adapter);

        mMakeReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RequestLeaveActivity.class));
                getActivity().finish();
            }
        });

        return view;
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
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}