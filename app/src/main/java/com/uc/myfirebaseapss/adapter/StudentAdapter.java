package com.uc.myfirebaseapss.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.uc.myfirebaseapss.LecturerDetail;
import com.uc.myfirebaseapss.R;
import com.uc.myfirebaseapss.RegisterStudent;
import com.uc.myfirebaseapss.StudentData;
import com.uc.myfirebaseapss.model.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.CardViewViewHolder> {


    private Context context;
    private ArrayList<Student> listStudent;
    private ArrayList<Student> getListStudent(){
        return listStudent;
    }




    public void setListStudent(ArrayList<Student> listStudent){
        this.listStudent = listStudent;
    }

    public StudentAdapter(Context context){
        this.context = context;
    }


    @NonNull
    @Override
    public CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_adapter, parent, false);
        return new StudentAdapter.CardViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewViewHolder holder, int position) {
        final Student student = getListStudent().get(position);
        holder.lbl_name.setText(student.getName());
        holder.lbl_nim.setText(student.getNim());
        holder.lbl_email.setText(student.getEmail());
        holder.lbl_gender.setText(student.getGender());
        holder.lbl_age.setText(student.getAge());
        holder.lbl_address.setText(student.getAddress());
    }

    @Override
    public int getItemCount() {
        return getListStudent().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView lbl_name, lbl_nim, lbl_email, lbl_gender, lbl_age, lbl_address;
        Button button_edit, button_del;
        OnCardListener onCardListener;

        CardViewViewHolder(View itemView) {
            super(itemView);
            lbl_name = itemView.findViewById(R.id.lbl_name_stud_adp);
            lbl_nim = itemView.findViewById(R.id.lbl_nim_stud_adp);
            lbl_email = itemView.findViewById(R.id.lbl_email_stud_adp);
            lbl_gender = itemView.findViewById(R.id.lbl_gender_stud_adp);
            lbl_age = itemView.findViewById(R.id.lbl_age_stud_adp);
            lbl_address = itemView.findViewById(R.id.lbl_address_stud_adp);

            this.onCardListener = onCardListener;

            button_edit = itemView.findViewById(R.id.btn_edit_stud_adp);
            button_del = itemView.findViewById(R.id.btn_delete_stud_adp);

            button_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, RegisterStudent.class);
                    intent.putExtra("action", "edit");
                    intent.putExtra("edit_data_stud", listStudent.get(position));
                    intent.putExtra("position", position);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentAdapter.this);
                    context.startActivity(intent);
                }
            });

            button_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, StudentData.class);
                    intent.putExtra("action", "delete");
                    intent.putExtra("edit_data_stud", listStudent.get(position));
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });

        }

        @Override
        public void onClick(View view) {
            onCardListener.onCardClick((getAdapterPosition()));
        }
    }

    public interface OnCardListener {
        void onCardClick (int position);
        void onEditClick (int position);
        void onDeleteClick (int position);
    }
}

