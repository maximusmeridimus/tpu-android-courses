package ru.tpu.courses.lab2;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Px;

import java.util.ArrayList;

/**
 * Простейший пример самописного View. В данном случае мы просто наследуемся от LinearLayout-а и
 * добавляем свою логику, но также есть возможность отнаследоваться от {@link android.view.ViewGroup},
 * если необходимо реализовать контейнер для View полностью с нуля, либо отнаследоваться от {@link android.view.View}.
 * <p/>
 * Здесь можно было бы добавить автоматическое сохранение и восстановление состояния, переопределив методы
 * {@link #onSaveInstanceState()} и {@link #onRestoreInstanceState(Parcelable)}.
 */
public class Lab2ViewsContainer extends LinearLayout {

    private int minViewsCount;
    private ArrayList<Characteristics> characteristics = new ArrayList<Characteristics>();
    private int currentMaximumId = 0;

    /**
     * Этот конструктор используется при создании View в коде.
     */
    public Lab2ViewsContainer(Context context) {
        this(context, null);
    }

    /**
     * Этот конструктор выдывается при создании View из XML.
     */
    public Lab2ViewsContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Конструктор, вызывается при инфлейте View, когда у View указан дополнительный стиль.
     * Почитать про стили можно здесь https://developer.android.com/guide/topics/ui/look-and-feel/themes
     *
     * @param attrs атрибуты, указанные в XML. Стандартные android атрибуты обрабатываются внутри родительского класса.
     *              Здесь необходимо только обработать наши атрибуты.
     */
    public Lab2ViewsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Свои атрибуты описываются в файле res/values/attrs.xml
        // Эта строчка объединяет возможные применённые к View стили
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Lab2ViewsContainer, defStyleAttr, 0);

        minViewsCount = a.getInt(R.styleable.Lab2ViewsContainer_lab2_minViewsCount, 0);
        if (minViewsCount < 0) {
            throw new IllegalArgumentException("minViewsCount can't be less than 0");
        }

        // Полученный TypedArray необходимо обязательно очистить.
        a.recycle();
    }

    public void incrementViews(CharSequence optionTextSequence, CharSequence scoreTextSequence) {
        if(TextUtils.isEmpty(optionTextSequence)|| TextUtils.isEmpty(scoreTextSequence)){
            return;
        }

        String optionText = optionTextSequence.toString();
        double score = Double.parseDouble(scoreTextSequence.toString());
        int id = characteristics.size();

        Characteristics newChar = new Characteristics(id, optionText, score);
        characteristics.add(newChar);

        boolean isMaximum = newChar.Score > characteristics.get(currentMaximumId).Score;

        if(isMaximum) {
            changeMaximum(currentMaximumId);
            currentMaximumId = newChar.Id;
        }

        createView(id, optionText, score, isMaximum || characteristics.size() == 1);
    }

    private void changeMaximum(int id) {
        findViewWithTag(id).setBackgroundColor(0xFF000000);
    }

    public void createView(int id, String optionText, double score, boolean isMaximum) {
        Log.i("t","create view");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        layout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

        TextView optionTextView = new TextView(getContext());
        optionTextView.setText(optionText);
        optionTextView.setLayoutParams(new LayoutParams(dpToPx(130), ViewGroup.LayoutParams.WRAP_CONTENT, 2));

        TextView scoreTextView = new TextView(getContext());
        scoreTextView.setText(String.format("%.1f", score));
        scoreTextView.setGravity(Gravity.END);
        scoreTextView.setLayoutParams(new LayoutParams(dpToPx(40), ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new LayoutParams(dpToPx(200), ViewGroup.LayoutParams.MATCH_PARENT, 4));
        relativeLayout.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));

        View strip = new View(getContext());
        strip.setLayoutParams(new LayoutParams(dpToPx(200 * score / 10), dpToPx(8)));
        strip.setBackgroundColor(isMaximum ? 0xFFFF0000 : 0xFF000000);
        strip.setTag(id);

        relativeLayout.addView(strip);

        layout.addView(optionTextView);
        layout.addView(relativeLayout);
        layout.addView(scoreTextView);

        addView(layout);
    }

    /**
     * Метод трансформирует указанные dp в пиксели, используя density экрана.
     */
    @Px
    public int dpToPx(double dp) {
        if (dp == 0) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return (int) Math.ceil(density * dp);
    }

    public void revalidate(){
        currentMaximumId = 0;
        for(Characteristics chars : characteristics) {
            if(chars.Score > characteristics.get(currentMaximumId).Score){
                currentMaximumId = chars.Id;
            }
        }

        if(this.getChildCount() > 0){
            this.removeAllViews();
        }

        for(Characteristics chars : characteristics) {
            createView(chars.Id, chars.Option, chars.Score, chars.Id == currentMaximumId);
        }
    }

    public ArrayList<Characteristics> getCharacteristicsList() {
        return characteristics;
    }

    public void setCharacteristics(ArrayList<Characteristics> chars) {
        this.characteristics = chars;
    }
}