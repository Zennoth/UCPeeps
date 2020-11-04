package com.uc.myfirebaseapss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.myfirebaseapss.model.Student;

import java.util.HashMap;
import java.util.Map;

public class RegisterStudent extends AppCompatActivity implements TextWatcher {

    Toolbar bar;
    Dialog dialog;
    TextInputLayout input_email, input_pass, input_name, input_nim, input_age, input_address;
    RadioGroup rg_gender;
    RadioButton radioButton;
    Button btn_register;
    String uid="", email="", pass="", name="", nim="", age="", gender="male", address="", action="";
    Student student;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);
        bar = findViewById(R.id.tb_reg_student);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = Glovar.loadingDialog(RegisterStudent.this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("student");

        input_email = findViewById(R.id.input_email_reg_student);
        input_pass = findViewById(R.id.input_password_reg_student);
        input_name = findViewById(R.id.input_name_reg_student);
        input_nim = findViewById(R.id.input_nim_reg_student);
        input_age = findViewById(R.id.input_age_reg_student);
        input_address = findViewById(R.id.input_address_reg_student);

        //implements text watcher
        input_email.getEditText().addTextChangedListener(this);
        input_pass.getEditText().addTextChangedListener(this);
        input_name.getEditText().addTextChangedListener(this);
        input_nim.getEditText().addTextChangedListener(this);
        input_age.getEditText().addTextChangedListener(this);
        input_address.getEditText().addTextChangedListener(this);

        btn_register = findViewById(R.id.btn_reg_student);
        rg_gender = findViewById(R.id.radg_gender_reg_student);
        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                gender = radioButton.getText().toString();
            }
        });

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        student = intent.getParcelableExtra("data_student");


        if(action.equalsIgnoreCase("add")){
            btn_register.setText(R.string.regstudent);
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addStudent();
                }
            });
        }else if (action.equalsIgnoreCase("edit") || action.equalsIgnoreCase("login")){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            bar.setTitle("Edit Student");
            btn_register.setText("Edit");

            input_email.setEnabled(false);

            input_name.getEditText().setText(student.getName());
            input_email.getEditText().setText(student.getEmail());
            input_pass.getEditText().setText(student.getPassword());
            input_nim.getEditText().setText(student.getNim());
            if(student.getGender().equalsIgnoreCase("male")){
                rg_gender.check(R.id.rad_male_reg_student);
            }else{
                rg_gender.check(R.id.rad_female_reg_student);
            }
            input_age.getEditText().setText(student.getAge());
            input_address.getEditText().setText(student.getAddress());
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.equalsIgnoreCase("add")){
                    addStudent();
                }else if (action.equalsIgnoreCase("edit") || (action.equalsIgnoreCase("login"))){
                    editStudent();
                }
            }
        });

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                if (!action.equalsIgnoreCase("login")) {
                    intent = new Intent(RegisterStudent.this, MainActivity.class);
                } else {
                    intent = new Intent(RegisterStudent.this, StudentMain.class);
                    intent.putExtra("action", "login");
                }
                startActivity(intent);
                finish();

            }
        });
    }

    public void addStudent(){
        getFormValue();
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(RegisterStudent.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            dialog.cancel();
                            uid = mAuth.getCurrentUser().getUid();
                            Student student = new Student(uid,email,pass,name,nim,gender,age,address);
                            mDatabase.child(uid).setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterStudent.this, "Student register successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                            mAuth.signOut();
                        }else{
                            try {
                                throw task.getException();
                            }catch(FirebaseAuthInvalidCredentialsException malFormed){
                                Toast.makeText(RegisterStudent.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                            }catch(FirebaseAuthUserCollisionException existEmail){
                                Toast.makeText(RegisterStudent.this, "Email already registered!", Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                Toast.makeText(RegisterStudent.this, "Register failed!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.cancel();
                        }
                    }
                });
    }

    public void editStudent(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        name = input_name.getEditText().getText().toString().trim();
        email = input_email.getEditText().getText().toString().trim();
        pass = input_pass.getEditText().getText().toString().trim();
        nim = input_nim.getEditText().getText().toString().trim();
        age = input_age.getEditText().getText().toString().trim();
        address = input_address.getEditText().getText().toString().trim();

        Map<String,Object> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("pass", pass);
        params.put("nim", nim);
        params.put("age", age);
        params.put("address", address);
        mDatabase.child("student").child(student.getUid()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                            dialog.cancel();
                Intent intent;
                if (action.equalsIgnoreCase("login")){
                    intent = new Intent(RegisterStudent.this, StudentMain.class);
                    intent.putExtra("action", "login");
                }else{
                    intent = new Intent(RegisterStudent.this, StudentData.class);
                    intent.putExtra("action", "edit");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudent.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!action.equalsIgnoreCase("login")) {
            getMenuInflater().inflate(R.menu.student_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.student_list){
            Intent intent;
            intent = new Intent(RegisterStudent.this, StudentData.class);
            intent.putExtra("action", "not_delete");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudent.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(RegisterStudent.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudent.this);
        startActivity(intent, options.toBundle());
        finish();
    }

    public void getFormValue(){
        email = input_email.getEditText().getText().toString().trim();
        pass = input_pass.getEditText().getText().toString().trim();
        name = input_name.getEditText().getText().toString().trim();
        nim = input_nim.getEditText().getText().toString().trim();
        age = input_age.getEditText().getText().toString().trim();
        address = input_address.getEditText().getText().toString().trim();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        getFormValue();
        if (!email.isEmpty() && !pass.isEmpty() && !name.isEmpty() && !nim.isEmpty()
                && !age.isEmpty() && !address.isEmpty()){
            btn_register.setEnabled(true);
        }else{
            btn_register.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}