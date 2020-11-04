package com.uc.myfirebaseapss;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uc.myfirebaseapss.adapter.LecturerAdapter;
import com.uc.myfirebaseapss.adapter.StudentAdapter;
import com.uc.myfirebaseapss.model.Lecturer;
import com.uc.myfirebaseapss.model.Student;

import java.util.ArrayList;

public class StudentData extends AppCompatActivity {

    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    Toolbar bar;
    DatabaseReference dbStudent;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    ArrayList<Student> listStudent = new ArrayList<>();
    RecyclerView rv_stud_data;
    String action="";
    Student student;
    Dialog dialog;
    TextView userEmail;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_data);
        bar = findViewById(R.id.tb_stud_data);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dbStudent = FirebaseDatabase.getInstance().getReference("student");
        rv_stud_data = findViewById(R.id.rv_stud_data);
        dialog = Glovar.loadingDialog(StudentData.this);
        userEmail = findViewById(R.id.lbl_email_stud_adp);

        fetchStudentData();

        Intent intent = getIntent();
        pos = intent.getIntExtra("position", 0);
        action = intent.getStringExtra("action");
        student = intent.getParcelableExtra("edit_data_stud");
        if (action.equalsIgnoreCase("delete")){

            new AlertDialog.Builder(StudentData.this)
                    .setTitle("Konfirmasi")
                    .setIcon(R.drawable.ic_android_goldtrans_24dp)
                    .setMessage("Are you sure to delete "+student.getName()+" data?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {

                            dialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    firebaseAuth.signInWithEmailAndPassword(student.getEmail(), student.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            firebaseAuth.getCurrentUser().delete();
                                            dbStudent.child(listStudent.get(pos).getUid()).removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                                                    Intent in = new Intent(StudentData.this, StudentData.class);
                                                    in.putExtra("action", "niggy");
                                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    Toast.makeText(StudentData.this, "Delete success!", Toast.LENGTH_SHORT).show();
                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentData.this);
                                                    startActivity(in, options.toBundle());
                                                    finish();
                                                    dialogInterface.cancel();
                                                }
                                            });
                                        }
                                    });

                                }
                            }, 2000);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        }



    }



    public void fetchStudentData(){
        dbStudent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listStudent.clear();
                rv_stud_data.setAdapter(null);
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    Student student = childSnapshot.getValue(Student.class);
                    listStudent.add(student);
                }
                showStudentData(listStudent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showStudentData(final ArrayList<Student> list) {
        rv_stud_data.setLayoutManager(new LinearLayoutManager(StudentData.this));
        StudentAdapter studentAdapter = new StudentAdapter(StudentData.this);
        studentAdapter.setListStudent(list);
        rv_stud_data.setAdapter(studentAdapter);

        ItemClickSupport.addTo(rv_stud_data).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                v.startAnimation(klik);
                Intent intent = new Intent(StudentData.this, RegisterStudent.class);
                Student student = new Student(list.get(position).getUid(), list.get(position).getEmail(), list.get(position).getPassword(), list.get(position).getName(), list.get(position).getNim(), list.get(position).getGender(), list.get(position).getAge(), list.get(position).getAddress());
                intent.putExtra("data_student", student);
                intent.putExtra("position", position);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentData.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            Intent intent;
            intent = new Intent(StudentData.this, RegisterStudent.class);
            intent.putExtra("action", "add");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentData.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(StudentData.this, RegisterStudent.class);
        intent.putExtra("action", "add");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentData.this);
        startActivity(intent, options.toBundle());
        finish();
    }


}

