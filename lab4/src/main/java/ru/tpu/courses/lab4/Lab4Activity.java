package ru.tpu.courses.lab4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import ru.tpu.courses.lab4.adapter.GroupItem;
import ru.tpu.courses.lab4.adapter.ListItem;
import ru.tpu.courses.lab4.adapter.StudentItem;
import ru.tpu.courses.lab4.adapter.StudentsAdapter;
import ru.tpu.courses.lab4.add.AddStudentActivity;
import ru.tpu.courses.lab4.db.Group;
import ru.tpu.courses.lab4.db.GroupDao;
import ru.tpu.courses.lab4.db.Lab4Database;
import ru.tpu.courses.lab4.db.Student;
import ru.tpu.courses.lab4.db.StudentDao;

/**
 * <b>Взаимодействие с файловой системой, SQLite</b>
 * <p>
 * В лабораторной работе вместо сохранения студентов в оперативную память будем сохранять их в
 * базу данных SQLite, которая интегрирована в ОС Android. Для более удобного взаимодействия с
 * ней будем использовать ORM библиотеку Room (подключение см. в build.gradle).
 * </p>
 * <p>
 * В {@link AddStudentActivity} введенные поля теперь сохраняются в
 * {@link android.content.SharedPreferences} - удобный способ для хранения небольших данных в
 * файловой системе, а также напрямую поработаем с {@link java.io.File} для работы с фото,
 * полученного с камеры.
 * </p>
 */
public class Lab4Activity extends AppCompatActivity {

    private static final int REQUEST_STUDENT_ADD = 1;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab4Activity.class);
    }

    private StudentDao studentDao;
    private GroupDao groupDao;

    private RecyclerView list;
    private FloatingActionButton fab;
    private FloatingActionButton fabGroup;

    private AlertDialog dialog;
    private StudentsAdapter studentsAdapter;
    private LinearLayoutManager layoutManager;

    private List<ListItem> consolidatedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        studentDao = Lab4Database.getInstance(this).studentDao();
        groupDao = Lab4Database.getInstance(this).groupDao();

        setTitle(getString(R.string.lab4_title, getClass().getSimpleName()));

        setContentView(R.layout.lab4_activity);
        list = findViewById(android.R.id.list);
        fab = findViewById(R.id.fab);
        fabGroup = findViewById(R.id.fab_group);

        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        int savedPosition = getSavedScrollPosition();
        if(savedPosition > 0) {
            layoutManager.scrollToPosition(savedPosition);
        }

        List<Group> groups = groupDao.getAll();

        HashMap<Integer, List<Student>> groupedHashMap = groupDataIntoHashMap(studentDao.getAll());

        for (Integer groupId : groupedHashMap.keySet()) {
            GroupItem groupItem = new GroupItem();
            Optional<Group> groupOptional = groups.stream().filter(x -> x.id == groupId).findFirst();

            if(groupOptional.isPresent()){
                Group group = groupOptional.get();

                groupItem.setGroup(group);
                consolidatedList.add(groupItem);

                for (Student student : groupedHashMap.get(groupId)) {
                    StudentItem studentItem = new StudentItem();
                    studentItem.setStudent(student);
                    consolidatedList.add(studentItem);
                }
            }
        }

        // Точно такой же список, как и в lab3, но с добавленным выводом фото

        studentsAdapter = new StudentsAdapter(this, consolidatedList);
        list.setAdapter(studentsAdapter);
        studentsAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(Lab4Activity.this, AddStudentActivity.class);
            intent.putExtra("student_details", ((StudentItem)consolidatedList.get(position)).getStudent());

            startActivity(intent);
        });

        fab.setOnClickListener(
                v -> startActivityForResult(
                        AddStudentActivity.newIntent(this),
                        REQUEST_STUDENT_ADD
                )
        );

        fabGroup.setOnClickListener(v -> createGroup());
    }

    private HashMap<Integer, List<Student>> groupDataIntoHashMap(List<Student> studentList) {

        HashMap<Integer, List<Student>> groupedHashMap = new HashMap<>();

        for (Student student : studentList) {
            Integer hashMapKey = student.groupId;

            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap.get(hashMapKey).add(student);
            } else {
                List<Student> list = new ArrayList<>();
                list.add(student);
                groupedHashMap.put(hashMapKey, list);
            }
        }

        return groupedHashMap;
    }

    public void createGroup(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.lab4_activity_add_group, null);
        final EditText groupTitle = dialogView.findViewById(R.id.new_group_edit_text);
        Button newGroupBtn = dialogView.findViewById(R.id.new_group_btn);

        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();

        newGroupBtn.setOnClickListener(v -> {
            Group group = new Group(groupTitle.getText().toString());
            if(TextUtils.isEmpty(group.groupName)) {
                Toast.makeText(this, R.string.lab4_error_empty_fields, Toast.LENGTH_LONG).show();
                return;
            }

            if (groupDao.count(group.groupName) != 0) {
                Toast.makeText(this, R.string.lab4_error_group_already_exists, Toast.LENGTH_LONG).show();
                return;
            }

            long groupId = groupDao.insert(group);
            group.id = (int)groupId;

            GroupItem groupItem = new GroupItem();
            groupItem.setGroup(group);
            consolidatedList.add(groupItem);
            studentsAdapter.setStudentConsolidatedList(consolidatedList);

            studentsAdapter.notifyItemInserted(studentsAdapter.getItemCount());

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STUDENT_ADD && resultCode == RESULT_OK) {
            Student student = AddStudentActivity.getResultStudent(data);

            int studentsInGroup = studentDao.studentCount(student.groupId);
            int groupItemPosition = studentsAdapter.getGroupItemPosition(student.groupId);

            studentDao.insert(student);

            StudentItem studentItem = new StudentItem();
            studentItem.setStudent(student);

            //если студент добавляется в последнюю группу, то просто записываем в конец
            if(consolidatedList.size() == groupItemPosition + studentsInGroup + 1) {
                consolidatedList.add(studentItem);
            } else {
                consolidatedList.add(groupItemPosition + studentsInGroup + 1, studentItem);
            }
            studentsAdapter.setStudentConsolidatedList(consolidatedList);
            studentsAdapter.notifyItemInserted(groupItemPosition + studentsInGroup + 1);
            list.scrollToPosition(groupItemPosition + studentsInGroup);
        }
    }

    private void saveScrollPosition() {
        int position = layoutManager.findFirstCompletelyVisibleItemPosition();
        SharedPreferences prefs = getSharedPreferences("scroll", MODE_PRIVATE);
        prefs.edit().putInt("position", position).apply();
    }

    private int getSavedScrollPosition() {
        SharedPreferences prefs = getSharedPreferences("scroll", MODE_PRIVATE);
        return prefs.getInt("position", 0);
    }

    @Override
    protected void onDestroy() {
        if(dialog != null) {
            dialog.dismiss();
        }
        saveScrollPosition();
        super.onDestroy();
    }
}
