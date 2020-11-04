package com.uc.myfirebaseapss;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.myfirebaseapss.adapter.ScheduleAdapter;
import com.uc.myfirebaseapss.model.Course;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    RecyclerView rv_schedule_data;
    DatabaseReference dbSchedule;
    ArrayList<Course> listCourse = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        dbSchedule = FirebaseDatabase.getInstance()
                .getReference("student")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("course");

        dbSchedule.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCourse.clear();
                rv_schedule_data.setAdapter(null);
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course = childSnapshot.getValue(Course.class);
                    listCourse.add(course);
                }
                showScheduleData(listCourse);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        rv_schedule_data = view.findViewById(R.id.rv_schedule_fragment);
        return view;
    }

    private void showScheduleData(ArrayList<Course>list){
        rv_schedule_data.setLayoutManager(new LinearLayoutManager(ScheduleFragment.this.getActivity()));
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(ScheduleFragment.this.getActivity());
        scheduleAdapter.setListCourse(list);
        rv_schedule_data.setAdapter(scheduleAdapter);
    }
}

