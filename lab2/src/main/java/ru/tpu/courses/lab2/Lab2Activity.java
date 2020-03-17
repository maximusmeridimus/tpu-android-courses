package ru.tpu.courses.lab2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * <b>Вёрстка UI. Сохранение состояния.</b>
 * <p/>
 * Андроид старается минимизировать объём занимаемой оперативной памяти, поэтому при любом удобном
 * случае выгружает приложение или Activity из памяти. Например, при повороте экрана (если включен автоповорот),
 * весь объект Activity будет пересоздан с 0. Сохранить введенные данные можно несколькими способами:
 * <ul>
 * <li>Сохранить значения в оперативной памяти. Тогда данные переживут пересоздание Activity,
 * но не переживут освобождение приложения из памяти. Этот пример будет рассмотрен в 3ей лабораторной</li>
 * <li>Сохранить значения в файловой системе. Тогда данные переживут освобождение приложения.
 * Взаимодействие с файловой системой может быть длительной операцией и привносит свои проблемы.
 * Рассмотрено оно будет в 4ой лабораторной</li>
 * <li>Используя встроенную в андроид систему сохранения состояния, которую мы используем в
 * этой лабораторной работе. Перед уничтожением Activity будет вызван метод {@link #onSaveInstanceState(Bundle)},
 * в котором можно записать все необходимые значения в переданный объект Bundle. Стоит учитывать,
 * что дополнительно андроид сохраняет в него состояние всех View на экране по их идентификаторам.
 * Так, многие реализации View автоматически сохраняют свои данные, если им указан идентификатор.
 * Например, {@link android.widget.ScrollView} должен сохранять, на сколько отскроллен его
 * контент и восстанавливать его</li>
 * </ul>
 * <p/>
 */
public class Lab2Activity extends AppCompatActivity {

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        lab2ViewsContainer.saveState(getSharedPreferences("shared preferences", MODE_PRIVATE));
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        lab2ViewsContainer.loadState(getSharedPreferences("shared preferences", MODE_PRIVATE));
        super.onRestoreInstanceState(savedInstanceState);
    }

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab2Activity.class);
    }

    private static final String STATE_VIEWS_COUNT = "views_count";

    private Lab2ViewsContainer lab2ViewsContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab2_activity);

        setTitle(getString(R.string.lab2_title, getClass().getSimpleName()));

        lab2ViewsContainer = findViewById(R.id.container);

        findViewById(R.id.btn_add_view).setOnClickListener(view ->
                lab2ViewsContainer.incrementViews(
                        ((EditText)findViewById(R.id.textOption)).getText(),
                        ((EditText)findViewById(R.id.textScore)).getText()));

        findViewById(R.id.btn_save).setOnClickListener(view ->
                lab2ViewsContainer.saveState(getSharedPreferences("shared preferences", MODE_PRIVATE)));

        findViewById(R.id.btn_load).setOnClickListener(view ->
                lab2ViewsContainer.loadState(getSharedPreferences("shared preferences", MODE_PRIVATE)));

        // Восстанавливаем состояние нашего View, добавляя заново все View
        if (savedInstanceState != null) {
            lab2ViewsContainer.setViewsCount(savedInstanceState.getInt(STATE_VIEWS_COUNT));
        }
    }

    @Px
    public int dpToPx(float dp) {
        if (dp == 0) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return (int) Math.ceil(density * dp);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_VIEWS_COUNT, lab2ViewsContainer.getViewsCount());
    }
}
