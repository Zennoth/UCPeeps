package com.uc.myfirebaseapss.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uc.myfirebaseapss.AddCourse;
import com.uc.myfirebaseapss.Glovar;
import com.uc.myfirebaseapss.R;
import com.uc.myfirebaseapss.model.Course;

import java.util.ArrayList;

import static com.uc.myfirebaseapss.Glovar.*;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CardViewViewHolder> {

    private Context context;
    private ArrayList<Course> listCourse;
    Button btn_edit, btn_delete;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    Dialog dialog;
    DatabaseReference dbCourse;

    private ArrayList<Course> getListCourse() {
        return listCourse;
    }
    public void setListCourse(ArrayList<Course> listCourse) {
        this.listCourse = listCourse;
    }

    public CourseAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CourseAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_adapter, parent, false);
        return new CourseAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.CardViewViewHolder holder, int position) {
        final Course course = getListCourse().get(position);
        dbCourse = FirebaseDatabase.getInstance().getReference("course");

        holder.lbl_subject_course.setText(course.getSubject());
        holder.lbl_lecturer_course.setText(course.getLecturer());
        holder.lbl_date_course.setText(course.getDay());
        holder.lbl_start_course.setText(course.getStart());
        holder.lbl_end_course.setText(course.getEnd());
        dialog = loadingDialog(context);

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setIcon(R.drawable.ic_android_goldtrans_24dp)
                        .setMessage("Are you sure to delete "+ course.getSubject()+" data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        dbCourse.child(course.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Toast.makeText(context, "Delete Success!", Toast.LENGTH_SHORT).show();
                                                dialogInterface.cancel();
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
        });
    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView lbl_subject_course, lbl_lecturer_course, lbl_date_course, lbl_start_course, lbl_end_course;
        CourseAdapter.OnCardListener onCardListener;
        Button btn_edit, btn_delete;

        CardViewViewHolder(View itemView) {
            super(itemView);
            lbl_subject_course = itemView.findViewById(R.id.lbl_subject_course);
            lbl_lecturer_course = itemView.findViewById(R.id.lbl_lecturer_course);
            lbl_date_course = itemView.findViewById(R.id.lbl_date_course);
            lbl_start_course = itemView.findViewById(R.id.lbl_start_course);
            lbl_end_course = itemView.findViewById(R.id.lbl_end_course);
            btn_edit = itemView.findViewById(R.id.btn_edit_course);
            btn_delete = itemView.findViewById(R.id.btn_delete_course);

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, AddCourse.class);
                    intent.putExtra("action", "edit");
                    intent.putExtra("edit_data_course", listCourse.get(position));
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            onCardListener.OnCardClick(getAdapterPosition());
        }
    }

    public interface OnCardListener{
        void OnCardClick (int position);
    }
}

