package ru.tpu.courses.lab3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import ru.tpu.courses.lab3.GroupItem;
import ru.tpu.courses.lab3.ListItem;
import ru.tpu.courses.lab3.Student;
import ru.tpu.courses.lab3.StudentItem;

/**
 * Задача Адаптера - управление View, которые содержатся в RecyclerView, с учётом его жизненного цикла.
 * Адаптер работает не с View, а с {@link RecyclerView.ViewHolder}. Этот класс содержит не только
 * View, которая будет показана на экране, но и дополнительную информацию, вроде позиции элемента
 * в списке.
 * <p>
 * Сначала мы переопределяем метод {@link #getItemCount()}. В нём необходимо вернуть количество
 * элементов в списке. В нашем случае это количество студентов помноженное на 2, т.к. на каждого
 * студента идёт 2 отдельных View. Одна - с номером студента, другая - с его ФИО.
 * <p>
 * Т.к. у нас идёт 2 разных типа View, то мы переопределяем метод {@link #getItemViewType(int)},
 * в котором должны вернуть номер типа View для переданной в методе позиции списка.
 * <p>
 * В методе {@link #onCreateViewHolder(ViewGroup, int)} мы создаём ViewHolder для
 * соответствующего типа View. Здесь мы производим инфлейт View из XML и ищем нужные нам View
 * в их иерархии.
 * <p>
 * В методе {@link #onBindViewHolder(RecyclerView.ViewHolder, int)} мы описываем заполнение
 * ViewHolder-а данными, соответствующими переданной нам позиции.
 * <p>
 * Когда мы только вызвали {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, согласно алгоритму
 * лэйаута описанному в LayoutManager, RecyclerView начинает вызывать методы адаптера,
 * чтобы расположить столько элементов, сколько помещается на экране.
 * Для каждого такого элемента вызывается сначала getItemViewType, с полученным itemViewType
 * вызывается метод onCreateViewHolder и созданный viewHolder передаётся в onBindViewHolder для заполнения
 * данными. После этого измеряются размеры элемента и добавляются в RecyclerView. Как только мы вышли
 * за пределы размеров RecyclerView, процесс останавливается. При скролле списка вниз, верхние
 * ViewHolder, которые стали не видны, открепляются от RecyclerView и добавляются в
 * {@link RecyclerView.RecycledViewPool}. Снизу же, когда появляется пустое пространство, в
 * RecyclerViewPool ищется ViewHolder для viewType следующего элемента. Если такой найден, то для него
 * вызывается onBindViewHolder и ViewHolder добавляется снизу.
 * <p>
 * Для того, чтобы сказать RecyclerView, что список был обновлён, используются методы
 * {@link #notifyDataSetChanged()}, {@link #notifyItemInserted(int)} и т.д. notifyDataSetChanged
 * обновляет весь список, а остальные методы notify... говорят об изменении конкретного элемента и
 * что с ним произошло, что позволяет делать анимированные изменения в списке. При этом RecyclerView
 * всё также будет работать с теми же закэшированными ViewHolder и не будет пересоздавать все View.
 */
public class StudentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_GROUP = 0;
    public static final int TYPE_STUDENT = 1;

    private List<Student> students = new ArrayList<>();
    private OnItemClickListener mListener;

    private List<ListItem> consolidatedList = new ArrayList<>();
    private Context mContext;

    public StudentsAdapter(Context context, List<ListItem> consolidatedList){
        this.consolidatedList = consolidatedList;
        this.mContext = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_GROUP:
                return new GroupHolder(parent);
            case TYPE_STUDENT:
                return new StudentHolder(parent, mListener);
        }
        throw new IllegalArgumentException("unknown viewType = " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case TYPE_GROUP:
                GroupItem groupItem = (GroupItem) consolidatedList.get(position);
                GroupHolder groupHolder = (GroupHolder) holder;
                groupHolder.groupTitle.setText(groupItem.getGroupTitle());
                break;

            case TYPE_STUDENT:
                StudentItem studentItem = (StudentItem) consolidatedList.get(position);
                StudentHolder studentHolder = (StudentHolder) holder;
                studentHolder.student.setText(studentItem.getStudent().toString());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return consolidatedList != null ? consolidatedList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    public void setStudents(List<Student> students){
        this.students = students;
    }

    public void setStudentConsolidatedList(List<ListItem> consolidatedList) {
        this.consolidatedList = consolidatedList;
    }

    public int getGroupItemPosition(String groupTitle){

        for (int i = 0; i < consolidatedList.size(); i++) {
            ListItem listItem = consolidatedList.get(i);
            if(listItem.getClass() == GroupItem.class && ((GroupItem) listItem).getGroupTitle().equals(groupTitle)) {
                return i;
            }
        }
        return 0;
    }
}
