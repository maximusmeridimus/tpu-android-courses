package ru.tpu.courses.lab4.adapter;

import ru.tpu.courses.lab4.db.Group;

public class GroupItem extends ListItem {
    private Group group;

    public void setGroup(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public int getType() {
        return TYPE_GROUP;
    }
}
