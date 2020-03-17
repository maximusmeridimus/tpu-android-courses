package ru.tpu.courses.android;

import android.app.Application;

import ru.tpu.courses.lab3.Group;
import ru.tpu.courses.lab3.GroupsCache;
import ru.tpu.courses.lab3.Student;
import ru.tpu.courses.lab3.StudentsCache;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // region lab3
        GroupsCache groupsCache = GroupsCache.getInstance();
        groupsCache.addGroup(new Group("10"));
        groupsCache.addGroup(new Group("16"));

        StudentsCache studentsCache = StudentsCache.getInstance();
        studentsCache.addStudent(new Student("Иван", "Иванович", "Иванов", "10"));
        studentsCache.addStudent(new Student("Петр", "Петрович", "Петров", "10"));

        studentsCache.addStudent(new Student("Сидор", "Сидорович", "Сидоров", "16"));
        studentsCache.addStudent(new Student("Сидр", "Сидрович", "Сидров", "16"));

        // endregion lab3
    }
}
