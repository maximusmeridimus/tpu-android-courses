package ru.tpu.courses.lab3;

import androidx.annotation.NonNull;

public class Group {
    public String Title;

    public Group(String groupTitle) {
        this.Title = groupTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return Title.equals(group.Title);
    }

    @NonNull
    @Override
    public String toString() {
        return this.Title;
    }
}
