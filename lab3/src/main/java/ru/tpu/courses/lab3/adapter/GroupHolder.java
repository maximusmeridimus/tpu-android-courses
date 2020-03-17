package ru.tpu.courses.lab3.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.courses.lab3.R;

public class GroupHolder extends RecyclerView.ViewHolder {

    public final TextView groupTitle;

    public GroupHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.lab3_item_group, parent, false));
        groupTitle = itemView.findViewById(R.id.group);
    }
}
