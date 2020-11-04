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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.myfirebaseapss.model.Lecturer;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddLecturer extends AppCompatActivity implements TextWatcher {

    Toolbar bar;
    Dialog dialog;
    TextInputLayout input_name, input_expertise;
    RadioGroup rg_gender;
    RadioButton radioButton;
    Button btn_add;
    String name="", expertise="", gender="male", action="";
    Lecturer lecturer;
    private DatabaseReference mDatabase;    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecturer);
        dialog = Glovar.loadingDialog(AddLecturer.this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        bar = findViewById(R.id.tb_lecturer);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        input_name = findViewById(R.id.input_name_lecturer);
        input_expertise = findViewById(R.id.input_expertise_lecturer);
        input_name.getEditText().addTextChangedListener(this);
        input_expertise.getEditText().addTextChangedListener(this);
        btn_add = findViewById(R.id.btn_add_lecturer);
        rg_gender = findViewById(R.id.radg_gender_lecturer);
        rg_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                gender = radioButton.getText().toString();
            }
        });

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if(action.equals("add")){
            getSupportActionBar().setTitle(R.string.addlecturer);
            btn_add.setText(R.string.addlecturer);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = input_name.getEditText().getText().toString().trim();
                    expertise = input_expertise.getEditText().getText().toString().trim();
                    addLecturer(name, gender, expertise);
                }
            });
        }else{ //saat activity dari lecturer detail & mau mengupdate data
            getSupportActionBar().setTitle(R.string.editlecturer);
            lecturer = intent.getParcelableExtra("edit_data_lect");
            input_name.getEditText().setText(lecturer.getName());
            input_expertise.getEditText().setText(lecturer.getExpertise());
            if(lecturer.getGender().equalsIgnoreCase("male")){
                rg_gender.check(R.id.rad_male_lecturer);
            }else{
                rg_gender.check(R.id.rad_female_lecturer);
            }
            btn_add.setText(R.string.editlecturer);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    name = input_name.getEditText().getText().toString().trim();
                    expertise = input_expertise.getEditText().getText().toString().trim();
                    Map<String,Object> params = new HashMap<>();
                    params.put("name", name);
                    params.put("expertise", expertise);
                    params.put("gender", gender);
                    mDatabase.child("lecturer").child(lecturer.getId()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            Intent intent;
                            intent = new Intent(AddLecturer.this, LecturerData.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturer.this);
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    });
                }
            });
        }


    }

    public void addLecturer(String mnama, String mgender, String mexpertise){
        dialog.show();
        String mid = mDatabase.child("lecturer").push().getKey();
        Lecturer lecturer = new Lecturer(mid, mnama, mgender, mexpertise);
        mDatabase.child("lecturer").child(mid).setValue(lecturer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.cancel();
                Toast.makeText(AddLecturer.this, "Add Lecturer Successfully", Toast.LENGTH_SHORT).show();
                input_name.getEditText().setText("");
                input_expertise.getEditText().setText("");
                rg_gender.check(R.id.rad_male_lecturer);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.cancel();
                Toast.makeText(AddLecturer.this, "Add Lecturer Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        name = input_name.getEditText().getText().toString().trim();
        expertise = input_expertise.getEditText().getText().toString().trim();
        if (!name.isEmpty() && !expertise.isEmpty()){
            btn_add.setEnabled(true);
        }else{
            btn_add.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lecturer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            Intent intent;
            intent = new Intent(AddLecturer.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturer.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }else if(id == R.id.lecturer_list){
            Intent intent;
            intent = new Intent(AddLecturer.this, LecturerData.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturer.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(AddLecturer.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturer.this);
        startActivity(intent, options.toBundle());
        finish();
    }

}