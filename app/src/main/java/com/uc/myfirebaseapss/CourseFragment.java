package com.uc.myfirebaseapss;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.myfirebaseapss.adapter.EnrollAdapter;
import com.uc.myfirebaseapss.model.Course;

import java.util.ArrayList;

public class CourseFragment extends Fragment {

    DatabaseReference dbCourse;
    ArrayList<Course> listCourse = new ArrayList<>();
    RecyclerView rv_enroll_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_course, container, false);

        dbCourse = FirebaseDatabase.getInstance().getReference("course");
        rv_enroll_data = v.findViewById(R.id.rv_course_adapter);

        dbCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCourse.clear();
                rv_enroll_data.setAdapter(null);
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course = childSnapshot.getValue(Course.class);
                    listCourse.add(course);
                }
                showEnrollData(listCourse);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }

    private void showEnrollData(ArrayList<Course> list){
        rv_enroll_data.setLayoutManager(new LinearLayoutManager(CourseFragment.this.getActivity()));
        EnrollAdapter enrollCourseAdapter = new EnrollAdapter(CourseFragment.this.getActivity());
        enrollCourseAdapter.setListCourse(list);
        rv_enroll_data.setAdapter(enrollCourseAdapter);

        final Observer<Course> courseToAddObserver = new Observer<Course>() {
            @Override
            public void onChanged(Course course) {
                FirebaseDatabase.getInstance().getReference().child("student").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("course")
                        .child(course.getId()).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Add Course Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Add Course Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        enrollCourseAdapter.getCourseToAdd().observe(this , courseToAddObserver);
    }
}

