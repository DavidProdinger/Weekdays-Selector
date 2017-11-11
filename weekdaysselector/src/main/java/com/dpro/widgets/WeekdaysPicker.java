package com.dpro.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

/**
 * Project: Weekdays-Selector
 * <p>
 * Created by David Prodinger <dprodinger@aon.at> on 24.10.2017.
 */

public class WeekdaysPicker extends LinearLayout {
    TextDrawable.IBuilder selectedBuilder;
    TextDrawable.IBuilder unselectedBuilder;
    private int selectedDayBackgroundColor;
    private int unSelectedDayBackgroundColor;
    private int selectedTextColor;
    private int unSelectedTextColor;
    private Context mContext;
    private int mHighlightColor = Color.RED;
    private int mTextColor = Color.WHITE;
    private int mBackgroundColor = Color.LTGRAY;
    private boolean mEditable = false;
    private boolean sunday_first_day = true;
    private boolean weekend = true;
    private boolean fullSize = false;
    private float dayViewPadding = 5;
    private float layoutWeight = 1.0f;
    private int height = 30;
    private int width = 30;
    private Set<Integer> selectedDays;
    private float fontSize = 14f;
    private OnWeekdaysChangeListener changeListener = null;
    private LinearLayout row1;
    private LinearLayout row2;

    public WeekdaysPicker(Context context) {
        super(context);
        mContext = context;
        mHighlightColor = Color.RED;
        initView();
    }

    public WeekdaysPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    public WeekdaysPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeekdaysPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView(attrs);
    }

    public void setOnWeekdaysChangeListener(OnWeekdaysChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    private void initView(AttributeSet attrs) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.WeekdaysPicker,
                0, 0);
        try {
            mEditable = a.getBoolean(R.styleable.WeekdaysPicker_enabled, true);
            mHighlightColor = a.getColor(R.styleable.WeekdaysPicker_highlight_color, Color.RED);
            mBackgroundColor = a.getColor(R.styleable.WeekdaysPicker_background_color, Color.LTGRAY);
            mTextColor = a.getColor(R.styleable.WeekdaysPicker_text_color, Color.WHITE);
            sunday_first_day = a.getBoolean(R.styleable.WeekdaysPicker_sunday_first_day, true);
            weekend = a.getBoolean(R.styleable.WeekdaysPicker_show_weekend, true);
            fullSize = a.getBoolean(R.styleable.WeekdaysPicker_full_size, false);

        } finally {
            a.recycle();
        }
        initView();
    }

    private void initView() {
        if (fullSize) {
            setOrientation(LinearLayout.VERTICAL);
        } else {
            setOrientation(LinearLayout.HORIZONTAL);
        }
        setGravity(Gravity.CENTER_VERTICAL);
        selectedDays = new HashSet<>();
        selectedDayBackgroundColor = mHighlightColor;
        unSelectedDayBackgroundColor = mBackgroundColor;//Color.LTGRAY;
        selectedTextColor = mTextColor;//Color.WHITE;
        unSelectedTextColor = mHighlightColor;

        row1 = new LinearLayout(mContext);
        row2 = new LinearLayout(mContext);

        // declare the builder object once.
        TextDrawable.IShapeBuilder selectedIShapeBuilder = TextDrawable.builder()
                .beginConfig()
                .textColor(selectedTextColor)
                .fontSize(getDpFromPx(fontSize))
                .bold()
                .width(fullSize ? getScreenWidth() / 5 : getDpFromPx(width))
                .height(getDpFromPx(height))
                .endConfig();
        TextDrawable.IShapeBuilder unselectedIShapeBuilder = TextDrawable.builder()
                .beginConfig()
                .textColor(unSelectedTextColor)
                .fontSize(getDpFromPx(fontSize))
                .bold()
                .width(fullSize ? getScreenWidth() / 5 : getDpFromPx(width))
                .height(getDpFromPx(height))
                .endConfig();

        if (fullSize) {
            selectedBuilder = selectedIShapeBuilder.roundRect(10);
            unselectedBuilder = unselectedIShapeBuilder.roundRect(10);
            addView(row1);
            addView(row2);
        } else {
            selectedBuilder = selectedIShapeBuilder.round();
            unselectedBuilder = unselectedIShapeBuilder.round();
        }

        // create DayViews
        if (sunday_first_day && weekend) {
            createDayView(SUNDAY, false);
        }
        createDayView(MONDAY, true);
        createDayView(TUESDAY, true);
        createDayView(WEDNESDAY, true);
        createDayView(THURSDAY, true);
        createDayView(FRIDAY, true);
        if (weekend) {
            createDayView(SATURDAY, false);
            if (!sunday_first_day) {
                createDayView(SUNDAY, false);
            }
        }
    }

    private String getDayText(View v) {
        int day = (int) v.getTag();
        return getDayLetter(day);
    }

    private String getDayLetter(int dayOfWeek) {
        String weekday = new DateFormatSymbols().getShortWeekdays()[dayOfWeek];
        return fullSize ? weekday : weekday.charAt(0) + "";
    }

    private String getDayString(int dayOfWeek, Locale locale) {
        return new DateFormatSymbols(locale).getWeekdays()[dayOfWeek];
    }

    private void createDayView(int tag, boolean selected) {
        ImageView dayView = new ImageView(mContext);
        dayView.setTag(tag);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT, layoutWeight);
        dayView.setLayoutParams(layoutParams);
        int padding = getDpFromPx(dayViewPadding);
        dayView.setPadding(padding, padding, padding, padding);
        dayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditable) {
                    toggleSelection(v);
                }
            }
        });
        if (fullSize) {
            if (tag == FRIDAY || tag == SATURDAY || tag == SUNDAY) {
                row2.addView(dayView);
            } else {
                row1.addView(dayView);
            }
        } else {
            addView(dayView);
        }
        setDaySelected(dayView, selected);
        selectedDays.add(tag);
    }

    private int getDpFromPx(float value) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private void setDaySelected(ImageView dayView, boolean b) {
        dayView.setSelected(b);
        String dayText = getDayText(dayView);
        int tag = (int) dayView.getTag();
        if (b) {
            dayView.setImageDrawable(selectedBuilder.build(dayText, selectedDayBackgroundColor));
            selectedDays.add(tag);
        } else {
            dayView.setImageDrawable(unselectedBuilder.build(dayText, unSelectedDayBackgroundColor));
            selectedDays.remove(tag);
        }
    }

    private void toggleSelection(View v) {
        ImageView day = (ImageView) v;
        if (day.isSelected()) {
            setDaySelected(day, false);
        } else {
            setDaySelected(day, true);
        }

        if (changeListener != null) {
            changeListener.onChange(this, (int) day.getTag(), getSelectedDays());
        }
    }

    public List<Integer> getSelectedDays() {
        List<Integer> list = new ArrayList<>(selectedDays);
        Collections.sort(list);
        return list;
    }

    public void setSelectedDays(List<Integer> list) {
        for (int day = 1; day <= 7; day++) {
            if (!weekend && !(day == SATURDAY || day == SUNDAY)) {
                setDaySelected((ImageView) findViewWithTag(day), list.contains(day));
            }
        }
    }

    public void selectDay(int day) {
        setSelectedDays(Collections.singletonList(day));
    }

    public List<String> getSelectedDaysText() {
        return getSelectedDaysText(Locale.getDefault());
    }

    public List<String> getSelectedDaysText(Locale locale) {
        List<String> dayTextList = new ArrayList<>();
        for (int dayIndex : getSelectedDays()) {
            dayTextList.add(getDayString(dayIndex, locale));
        }
        return dayTextList;
    }

    public boolean allDaysSelected() {
        return selectedDays.size() == 7;
    }

    public boolean noDaySelected() {
        return selectedDays.size() == 0;
    }

    public boolean isInEditMode() {
        return mEditable;
    }
}