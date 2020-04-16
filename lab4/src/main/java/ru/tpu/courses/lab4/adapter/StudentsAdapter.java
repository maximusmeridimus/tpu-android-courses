package ru.tpu.courses.lab4.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.tpu.courses.lab4.db.Student;

public class StudentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_GROUP = 0;
    public static final int TYPE_STUDENT = 1;

    private List<Student> students = new ArrayList<>();
    private OnItemClickListener mListener;

    private List<ListItem> consolidatedList = new ArrayList<>();
    private Context mContext;

    public StudentsAdapter(Context context, List<ListItem> consolidatedList){
        this.consolidatedList = consolidatedList;
        this.mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_GROUP:
                return new GroupHolder(parent);
            case TYPE_STUDENT:
                return new StudentHolder(parent);
        }
        throw new IllegalArgumentException("unknown viewType = " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_GROUP:
                GroupItem groupItem = (GroupItem) consolidatedList.get(position);
                GroupHolder groupHolder = (GroupHolder) holder;
                groupHolder.groupTitle.setText(groupItem.getGroup().groupName);
                break;

            case TYPE_STUDENT:
                StudentItem studentItem = (StudentItem) consolidatedList.get(position);
                StudentHolder studentHolder = (StudentHolder) holder;
                studentHolder.student.setText(studentItem.getStudent().toString());
                Student student = studentItem.getStudent();

                if (!TextUtils.isEmpty(student.photoPath)) {
                    studentHolder.photo.setVisibility(View.VISIBLE);
                    studentHolder.photo.setImageURI(Uri.parse(student.photoPath));
                } else {
                    studentHolder.photo.setVisibility(View.GONE);
                    studentHolder.photo.setImageURI(null);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return consolidatedList != null ? consolidatedList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void setStudentConsolidatedList(List<ListItem> consolidatedList) {
        this.consolidatedList = consolidatedList;
    }

    public int getGroupItemPosition(int groupId){

        for (int i = 0; i < consolidatedList.size(); i++) {
            ListItem listItem = consolidatedList.get(i);
            if(listItem.getClass() == GroupItem.class && ((GroupItem) listItem).getGroup().id == groupId) {
                return i;
            }
        }
        return 0;
    }
}
