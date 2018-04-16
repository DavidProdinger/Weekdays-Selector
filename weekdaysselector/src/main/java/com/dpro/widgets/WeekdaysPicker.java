package com.dpro.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
    public static final int ALL = 0;
    public static final int ODD = 1;
    public static final int EVEN = 2;
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
    private boolean recurrence = false;
    private float dayViewPadding = 5;
    private float layoutWeight = 1.0f;
    private int height = 30;
    private int width = 30;
    private Set<Integer> selectedDays;
    private float fontSize = 14f;
    private OnWeekdaysChangeListener changeListener = null;
    private OnWeekRecurrenceChangeListener recurrenceListener = null;
    private LinearLayout row1;
    private LinearLayout row2;
    private Spinner spinner;
    private LinearLayout.LayoutParams layoutParams;

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

    /**
     * Adds an listener for selection change
     *
     * @param changeListener Listener
     */
    public void setOnWeekdaysChangeListener(OnWeekdaysChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    /**
     * Adds an listener for recurrence change
     *
     * @param recurrenceListener Listener
     */
    public void setOnWeekRecurrenceChangeListener(OnWeekRecurrenceChangeListener recurrenceListener) {
        this.recurrenceListener = recurrenceListener;
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
            recurrence = a.getBoolean(R.styleable.WeekdaysPicker_recurrence, false);

        } finally {
            a.recycle();
        }
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, layoutWeight);
        setLayoutParams(layoutParams);

        selectedDays = new HashSet<>();
        selectedDayBackgroundColor = mHighlightColor;
        unSelectedDayBackgroundColor = mBackgroundColor;//Color.LTGRAY;
        selectedTextColor = mTextColor;//Color.WHITE;
        unSelectedTextColor = mHighlightColor;

        if (recurrence) {
            final View view = this;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mContext.getResources().getStringArray(R.array.recurrence));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner = new Spinner(mContext);
            spinner.setAdapter(adapter);
            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View v, int index, long id) {
                    if (recurrenceListener != null) {
                        recurrenceListener.onWeekChange(view, getSelectedDays(), index);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    adapterView.setSelection(0);
                }
            });

            int p_8 = (int) getResources().getDisplayMetrics().density * 8;
            spinner.setPadding(p_8, p_8, p_8, p_8);
            addView(spinner);
        }
        row1 = new LinearLayout(mContext);
        row2 = new LinearLayout(mContext);
        row1.setOrientation(HORIZONTAL);
        row2.setOrientation(HORIZONTAL);
        row1.setLayoutParams(layoutParams);
        row2.setLayoutParams(layoutParams);

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

        addView(row1);
        if (fullSize) {
            selectedBuilder = selectedIShapeBuilder.roundRect(10);
            unselectedBuilder = unselectedIShapeBuilder.roundRect(10);
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
            row1.addView(dayView);
        }
        setDaySelected(dayView, selected);
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
        setDaySelected(day, !day.isSelected());

        if (changeListener != null) {
            changeListener.onChange(this, (int) day.getTag(), getSelectedDays());
        }
    }

    /**
     * Get selected days as {@link Integer}
     * Day value is {@link java.util.Calendar}
     *
     * @return {@link List} of selected days as {@link Integer}
     */
    public List<Integer> getSelectedDays() {
        List<Integer> list = new ArrayList<>(selectedDays);
        Collections.sort(list);
        return list;
    }

    /**
     * Set days
     * Day value is {@link java.util.Calendar}
     *
     * @param list {@link List} of which days should select
     */
    public void setSelectedDays(List<Integer> list) {
        for (int day = SUNDAY; day <= SATURDAY; day++) {
            if (weekend) {
                setDaySelected((ImageView) findViewWithTag(day), list.contains(day));
            } else {
                if (day != SATURDAY && day != SUNDAY) {
                    setDaySelected((ImageView) findViewWithTag(day), list.contains(day));
                }
            }
        }
    }

    /**
     * Select only one day
     * Day value is {@link java.util.Calendar}
     *
     * @param day Only one day to select
     */
    public void selectDay(int day) {
        setSelectedDays(Collections.singletonList(day));
    }

    /**
     * Get selected days as {@link String} in defalut {@link Locale}
     *
     * @return {@link List} of selected days as {@link String}
     */
    public List<String> getSelectedDaysText() {
        return getSelectedDaysText(Locale.getDefault());
    }

    /**
     * Get selected days as {@link String} in provided {@link Locale}
     *
     * @param locale Language of days-text
     * @return {@link List} of day-Strings
     */
    public List<String> getSelectedDaysText(Locale locale) {
        List<String> dayTextList = new ArrayList<>();
        for (int dayIndex : getSelectedDays()) {
            dayTextList.add(getDayString(dayIndex, locale));
        }
        return dayTextList;
    }

    /**
     * Check if all days are selected
     *
     * @return true if all days are selected
     */
    public boolean allDaysSelected() {
        return selectedDays.size() == 7;
    }

    /**
     * Check if no day is selected
     *
     * @return true if no day is selected
     */
    public boolean noDaySelected() {
        return selectedDays.size() == 0;
    }

    /**
     * Check if user can select days
     *
     * @return if widget is editable
     */
    @Override
    public boolean isInEditMode() {
        return mEditable;
    }

    /**
     * Enable/Disable userinteraction with the picker
     *
     * @param editable if user can interact
     */
    public void setEditable(boolean editable) {
        mEditable = editable;
    }

    /**
     * Check if recurrence is enabled
     *
     * @return if recurrence is enabled
     */
    public boolean getRecurrence() {
        return recurrence;
    }

    /**
     * Enable/Disable recurrence
     *
     * @param recurrence recurrence
     */
    public void setRecurrence(boolean recurrence) {
        this.recurrence = recurrence;
    }

    /**
     * Get week recurrence
     *
     * @return everey, odd or even week recurrence
     */
    public int getWeekRecurrence() {
        return spinner.getSelectedItemPosition();
    }

    /**
     * Set week recurrence
     *
     * @param weekRecurrence every(0), odd(1) or even(2)
     */
    public void setWeekRecurrence(int weekRecurrence) {
        spinner.setSelection(weekRecurrence);
    }
}