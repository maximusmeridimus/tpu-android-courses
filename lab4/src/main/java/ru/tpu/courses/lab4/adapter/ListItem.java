package ru.tpu.courses.lab4.adapter;

public abstract class ListItem {
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_STUDENT = 1;

    abstract public int getType();
}
