package ru.tpu.courses.lab2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("chars", lab2ViewsContainer.getCharacteristicsList());
        super.onSaveInstanceState(outState);
    }

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab2Activity.class);
    }

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

        // Восстанавливаем состояние нашего View, добавляя заново все View
        if (savedInstanceState != null ) {
            lab2ViewsContainer.setCharacteristics(savedInstanceState.getParcelableArrayList("chars"));
            lab2ViewsContainer.revalidate();
        }
    }
}
