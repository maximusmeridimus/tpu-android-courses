package ru.tpu.courses.lab3.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.courses.lab3.R;

public class StudentHolder extends RecyclerView.ViewHolder {

    public final TextView student;

    public StudentHolder(ViewGroup parent, final StudentsAdapter.OnItemClickListener listener) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.lab3_item_student, parent, false));
        student = itemView.findViewById(R.id.student);

        itemView.setOnClickListener(v -> {
            if(listener != null) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            }
        });

    }
}
