package com.uc.myfirebaseapss.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uc.myfirebaseapss.R;
import com.uc.myfirebaseapss.model.Lecturer;

import java.util.ArrayList;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Lecturer> listLecturer;
    private ArrayList<Lecturer> getListLecturer() {
        return listLecturer;
    }
    public void setListLecturer(ArrayList<Lecturer> listLecturer) {
        this.listLecturer = listLecturer;
    }
    public LecturerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LecturerAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lecturer_adapter, parent, false);
        return new LecturerAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final LecturerAdapter.CardViewViewHolder holder, int position) {
        final Lecturer lecturer = getListLecturer().get(position);
        holder.lbl_name.setText(lecturer.getName());
        holder.lbl_gender.setText(lecturer.getGender());
        holder.lbl_expertise.setText(lecturer.getExpertise());
    }

    @Override
    public int getItemCount() {
        return getListLecturer().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView lbl_name, lbl_gender, lbl_expertise;

        CardViewViewHolder(View itemView) {
            super(itemView);
            lbl_name = itemView.findViewById(R.id.lbl_name_lect_adp);
            lbl_gender = itemView.findViewById(R.id.lbl_gender_lect_adp);
            lbl_expertise = itemView.findViewById(R.id.lbl_expertise_lect_adp);
        }
    }
}
