package ru.tpu.courses.lab4.adapter;

import ru.tpu.courses.lab4.db.Student;

public class StudentItem extends ListItem {

    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public int getType() {
        return TYPE_STUDENT;
    }
}
