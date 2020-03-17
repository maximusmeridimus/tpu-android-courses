package ru.tpu.courses.lab3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import ru.tpu.courses.lab3.adapter.StudentsAdapter;
import ru.tpu.courses.lab3.adapter.StudentsNormAdapter;

/**
 * <b>RecyclerView, взаимодействие между экранами. Memory Cache.</b>
 * <p>
 * View, добавленные в {@link android.widget.ScrollView} отрисовываются все разом, при этом выводится
 * пользователю только та часть, до которой доскроллил пользователь. Соответственно, это замедляет
 * работу приложения и в случае с особо большим количеством View может привести к
 * {@link OutOfMemoryError}, краша приложение, т.к. система не может уместить все View в памяти.
 * </p>
 * <p>
 * {@link RecyclerView} - компонент для работы со списками, содержащими большое количество данных,
 * который призван исправить эту проблему. Это точно такой же {@link android.view.ViewGroup}, как и
 * ScrollView, но он содержит только те View, которые сейчас видимы пользователю. Работать с ним
 * намного сложнее, чем с ScrollView, поэтому если известно, что контент на экране статичен и не
 * содержит много элементов, то для простоты лучше воспользоваться ScrollView.
 * </p>
 * <p>
 * Для работы RecyclerView необходимо подключить отдельную библиотеку (см. build.gradle)
 * </p>
 */
public class Lab3Activity extends AppCompatActivity {

    private static final int REQUEST_STUDENT_ADD = 1;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab3Activity.class);
    }

    private final StudentsCache studentsCache = StudentsCache.getInstance();
    private final GroupsCache groupsCache = GroupsCache.getInstance();

    private List<ListItem> consolidatedList = new ArrayList<>();

    private RecyclerView list;
    private FloatingActionButton fab;
    private FloatingActionButton fabGroup;

    private StudentsAdapter studentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.lab3_title, getClass().getSimpleName()));

        setContentView(R.layout.lab3_activity);
        list = findViewById(android.R.id.list);
        fab = findViewById(R.id.fab);
        fabGroup = findViewById(R.id.fab_group);

        /*
        Здесь идёт инициализация RecyclerView. Первое, что необходимо для его работы, это установить
        реализацию LayoutManager-а. Он содержит логику размещения View внутри RecyclerView. Так,
        LinearLayoutManager, который используется ниже, располагает View последовательно, друг за
        другом, по аналогии с LinearLayout-ом. Из альтернатив можно например использовать
        GridLayoutManager, который располагает View в виде таблицы. Необходимость написания своего
        LayoutManager-а возникает очень редко и при этом является весьма сложным процессом, поэтому
        рассматриваться в лабораторной работе не будет.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        /*
        Следующий ключевой компонент - это RecyclerView.Adapter. В нём описывается вся информация,
        необходимая для заполнения RecyclerView. В примере мы выводим пронумерованный список
        студентов, подробнее о работе адаптера в документации к классу StudentsAdapter.
         */

        HashMap<String, List<Student>> groupedHashMap = groupDataIntoHashMap(studentsCache.getStudents());

        for (String group : groupedHashMap.keySet()) {
            GroupItem groupItem = new GroupItem();
            groupItem.setGroupTitle(group);
            consolidatedList.add(groupItem);

            int studentNumber = 1;

            for (Student student : groupedHashMap.get(group)) {
                StudentItem studentItem = new StudentItem();
                student.currentPosition = studentNumber++;
                studentItem.setStudent(student);
                consolidatedList.add(studentItem);
            }
        }

        studentsAdapter = new StudentsAdapter(this, consolidatedList);
        list.setAdapter(studentsAdapter);
        studentsAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(Lab3Activity.this, AddStudentActivity.class);
            intent.putExtra("student_details", ((StudentItem)consolidatedList.get(position)).getStudent());

            startActivity(intent);
        });


//        list.setAdapter(studentsAdapter = new StudentsAdapter(studentsCache.getStudents()));
//        studentsAdapter.setOnItemClickListener(new StudentsNormAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Intent intent = new Intent(Lab3Activity.this, AddStudentActivity.class);
//                intent.putExtra("student", studentsCache.getStudents().get(position));
//
//                startActivity(intent);
//            }
//        });

        /*
        При нажатии на кнопку мы переходим на Activity для добавления студента. Обратите внимание,
        что здесь используется метод startActivityForResult. Этот метод позволяет организовывать
        передачу данных обратно от запущенной Activity. В нашем случае, после закрытия AddStudentActivity,
        у нашей Activity будет вызван метод onActivityResult, в котором будут данные, которые мы
        указали перед закрытием AddStudentActivity.
         */
        fab.setOnClickListener(
                v -> startActivityForResult(
                        AddStudentActivity.newIntent(this),
                        REQUEST_STUDENT_ADD
                )
        );

        fabGroup.setOnClickListener(v -> createGroup());
    }

    private HashMap<String, List<Student>> groupDataIntoHashMap(List<Student> studentList) {

        HashMap<String, List<Student>> groupedHashMap = new HashMap<>();

        for (Student student : studentList) {

            String hashMapKey = student.groupName;

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(student);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<Student> list = new ArrayList<>();
                list.add(student);
                groupedHashMap.put(hashMapKey, list);
            }
        }

        return groupedHashMap;
    }

    public void createGroup(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.lab3_activity_add_group, null);
        final EditText groupTitle = dialogView.findViewById(R.id.new_group_edit_text);
        Button newGroupBtn = dialogView.findViewById(R.id.new_group_btn);

        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        newGroupBtn.setOnClickListener(v -> {
            Group group = new Group(groupTitle.getText().toString());
            if(TextUtils.isEmpty(group.Title)) {
                Toast.makeText(this, R.string.lab3_error_empty_fields, Toast.LENGTH_LONG).show();
                return;
            }

            if (groupsCache.contains(group)) {
                Toast.makeText(this, R.string.lab3_error_group_already_exists, Toast.LENGTH_LONG).show();
                return;
            }

            groupsCache.addGroup(group);

            GroupItem groupItem = new GroupItem();
            groupItem.setGroupTitle(group.Title);
            consolidatedList.add(groupItem);
            studentsAdapter.setStudentConsolidatedList(consolidatedList);

            studentsAdapter.notifyItemInserted(studentsAdapter.getItemCount());

            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Этот метод вызывается после того, как мы ушли с запущенной с помощью метода
     * {@link #startActivityForResult(Intent, int)} Activity.
     *
     * @param requestCode переданный в метод startActivityForResult requestCode, для случаев,
     *                    когда с нашей активитизапускается несколько различных активити. По этому
     *                    идентификатору мы их различаем.
     * @param resultCode  идентификатор, описывающий, с каким результатом запущенная активити была
     *                    завершена. Если пользователь просто закрыл Activity, то по умолчанию будет
     *                    {@link #RESULT_CANCELED}.
     * @param data        даные переданные нам от запущенной Activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_STUDENT_ADD && resultCode == RESULT_OK) {
            Student student = AddStudentActivity.getResultStudent(data);

            int count = 0;
            for (Student x : studentsCache.getStudents()) {
                if (x.groupName.equals(student.groupName)) {
                    count++;
                }
            }
            int studentsInGroup = count;
            int groupItemPosition = studentsAdapter.getGroupItemPosition(student.groupName);

            student.currentPosition = studentsInGroup + 1;

            studentsCache.addStudent(student);

            StudentItem studentItem = new StudentItem();
            studentItem.setStudent(student);

            if(consolidatedList.size() == groupItemPosition + studentsInGroup + 1) {
                consolidatedList.add(studentItem);
            } else {
                consolidatedList.add(groupItemPosition + studentsInGroup + 1, studentItem);
            }
            studentsAdapter.setStudentConsolidatedList(consolidatedList);

            Log.i("POSITION", "position:" + (groupItemPosition + studentsInGroup));
            studentsAdapter.notifyItemInserted(groupItemPosition + studentsInGroup + 1);
            //studentsAdapter.notifyItemRangeInserted(studentsAdapter.getItemCount() - 2, 1);
            list.scrollToPosition(groupItemPosition + studentsInGroup);
        }
    }
}
