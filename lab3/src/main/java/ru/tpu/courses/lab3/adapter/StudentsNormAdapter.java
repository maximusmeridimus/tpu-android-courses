package ru.tpu.courses.lab3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.tpu.courses.lab3.R;
import ru.tpu.courses.lab3.Student;

public class StudentsNormAdapter extends RecyclerView.Adapter<StudentsNormAdapter.StudentViewHolder>  {
    private List<Student> students;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public StudentsNormAdapter(List<Student> students) {
        this.students = students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewStudent;
        public TextView mTextViewGroup;

        public StudentViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTextViewStudent = itemView.findViewById(R.id.textview_student);
            mTextViewGroup = itemView.findViewById(R.id.textview_group);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lab3_student_item, parent, false);
        StudentViewHolder evh = new StudentViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(StudentViewHolder holder, int position) {
        Student currentItem = this.students.get(position);

        holder.mTextViewStudent.setText(String.format("%s %s %s", currentItem.firstName, currentItem.lastName, currentItem.secondName));
        holder.mTextViewGroup.setText(currentItem.groupName);
    }

    @Override
    public int getItemCount() {
        return this.students.size();
    }
}
