package com.uc.myfirebaseapss;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.myfirebaseapss.model.Student;

public class ProfileFragment extends Fragment {

    TextView pName, pNim, pEmail, pGender, pAge, pAddress;
    Button logout;
    FirebaseUser firebaseUser;
    DatabaseReference dbReference;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        pName = view.findViewById(R.id.lbl_name_stud_adp);
        pNim = view.findViewById(R.id.lbl_nim_stud_adp);
        pEmail = view.findViewById(R.id.lbl_email_stud_adp);
        pGender = view.findViewById(R.id.lbl_gender_stud_adp);
        pAge = view.findViewById(R.id.lbl_age_stud_adp);
        pAddress = view.findViewById(R.id.lbl_address_stud_adp);
        logout = view.findViewById(R.id.btn_logout);
        dialog = Glovar.loadingDialog(getActivity());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("student").child(firebaseUser.getUid());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                pName.setText(student.getName());
                pNim.setText(student.getNim());
                pEmail.setText(student.getEmail());
                pGender.setText(student.getGender());
                pAge.setText(student.getAge());
                pAddress.setText(student.getAddress());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
