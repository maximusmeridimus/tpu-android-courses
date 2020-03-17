package ru.tpu.courses.lab3;

public class GroupItem extends ListItem {
    private String groupTitle;

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    @Override
    public int getType() {
        return TYPE_GROUP;
    }
}
