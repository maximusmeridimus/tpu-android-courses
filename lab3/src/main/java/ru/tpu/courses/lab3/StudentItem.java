package ru.tpu.courses.lab3;

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
