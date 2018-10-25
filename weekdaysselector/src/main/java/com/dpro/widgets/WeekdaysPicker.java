package com.dpro.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private final Context mContext;
    private final float dayViewPadding = 5;
    private final float layoutWeight = 1.0f;
    private int selectedDayBackgroundColor;
    private int unSelectedDayBackgroundColor;
    private final int height = 30;
    private int selectedTextColor;
    private int unSelectedTextColor;
    private final int width = 30;
    private final float fontSize = 14f;
    private int mHighlightColor = Color.RED;
    private TextDrawable.IBuilder selectedBuilder;
    private int mTextColor = Color.WHITE;
    private int mBackgroundColor = Color.LTGRAY;
    private TextDrawable.IBuilder unselectedBuilder;
    private TextDrawable.IBuilder unselectedWeekendBuilder;
    private boolean mEditable = false;
    private boolean sunday_first_day = true;
    private boolean weekend = true;
    private boolean fullSize = false;
    private boolean recurrence = false;
    private int unSelectedWeekendColor;
    private int unSelectedWeekendTextColor;
    private int mWeekendTextColor = Color.RED;
    private int mWeekendColor = Color.GRAY;
    private Set<Integer> selectedDays;
    private boolean mWeekendTextColorChanged = false;
    private OnWeekdaysChangeListener changeListener = null;
    private OnWeekRecurrenceChangeListener recurrenceListener = null;
    private LinearLayout row1;
    private LinearLayout row2;
    private Spinner spinner;
    private LinearLayout.LayoutParams layoutParams;
    private boolean weekendDarker = false;
    private boolean mSeceletOnlyOne = false;
    private boolean mCustomDays = false;
    private int mBorderColor = -1;
    private int mBorderThickness = 4;
    private int mBorderHighlightColor = -1;
    private int mBorderHighlightThickness = 4;
    private List<Integer> allCreatedDays;
    private LinkedHashMap<Integer, Boolean> lastCustomDaysMap;

    private TextDrawable.IShapeBuilder unselectedWeekendIShapeBuilder;
    private TextDrawable.IShapeBuilder unselectedIShapeBuilder;
    private TextDrawable.IShapeBuilder selectedIShapeBuilder;

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

    public static Spinner getRecurrenceSpinner(final WeekdaysPicker widget, Context mContext, View v) {
        final View view = v;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mContext.getResources().getStringArray(R.array.recurrence));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = new Spinner(mContext);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int index, long id) {
                if (widget.recurrenceListener != null) {
                    widget.recurrenceListener.onWeekChange(view, widget.getSelectedDays(), index);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setSelection(0);
            }
        });

        int p_8 = (int) mContext.getResources().getDisplayMetrics().density * 8;
        spinner.setPadding(p_8, p_8, p_8, p_8);
        return spinner;
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
            mWeekendColor = a.getColor(R.styleable.WeekdaysPicker_weekend_color, Color.GRAY);
            mTextColor = a.getColor(R.styleable.WeekdaysPicker_text_color, Color.WHITE);
            sunday_first_day = a.getBoolean(R.styleable.WeekdaysPicker_sunday_first_day, true);
            weekend = a.getBoolean(R.styleable.WeekdaysPicker_show_weekend, true);
            fullSize = a.getBoolean(R.styleable.WeekdaysPicker_full_size, false);
            recurrence = a.getBoolean(R.styleable.WeekdaysPicker_recurrence, false);
            weekendDarker = a.getBoolean(R.styleable.WeekdaysPicker_weekenddarker, false);
            mWeekendTextColor = a.getColor(R.styleable.WeekdaysPicker_weekend_text_color, -1);
            mBorderColor = a.getColor(R.styleable.WeekdaysPicker_border_color, -1);
            mBorderThickness = a.getColor(R.styleable.WeekdaysPicker_border_thickness, 4);
            mBorderHighlightColor = a.getColor(R.styleable.WeekdaysPicker_border_highlight_color, -1);
            mBorderHighlightThickness = a.getColor(R.styleable.WeekdaysPicker_border_highlight_thickness, 4);
            if (mWeekendTextColor == -1) {
                mWeekendTextColorChanged = false;
                mWeekendTextColor = mHighlightColor;
            } else {
                mWeekendTextColorChanged = true;
            }

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
        allCreatedDays = new ArrayList<>();

        initColors();
        initRecurrence();

        row1 = new LinearLayout(mContext);
        row2 = new LinearLayout(mContext);
        row1.setOrientation(HORIZONTAL);
        row2.setOrientation(HORIZONTAL);
        row1.setLayoutParams(layoutParams);
        row2.setLayoutParams(layoutParams);

        initDrawableBuilders();

        addView(row1);
        if (fullSize) {
            selectedBuilder = selectedIShapeBuilder.roundRect(10);
            unselectedBuilder = unselectedIShapeBuilder.roundRect(10);
            unselectedWeekendBuilder = unselectedWeekendIShapeBuilder.roundRect(10);
            addView(row2);
        } else {
            selectedBuilder = selectedIShapeBuilder.round();
            unselectedBuilder = unselectedIShapeBuilder.round();
            unselectedWeekendBuilder = unselectedWeekendIShapeBuilder.round();
        }

        createDayViews();
    }

    private void createDayViews() {
        allCreatedDays.clear();
        selectedDays.clear();
        row1.removeAllViewsInLayout();
        row2.removeAllViewsInLayout();
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

    private void createDayViews(LinkedHashMap<Integer, Boolean> daySet) {
        allCreatedDays.clear();
        selectedDays.clear();
        row1.removeAllViewsInLayout();
        row2.removeAllViewsInLayout();
        System.out.println("map 1 " + daySet.get(0));

        if (sunday_first_day && daySet.containsKey(SUNDAY)) {
            LinkedHashMap<Integer, Boolean> map = new LinkedHashMap<>();
            Iterator it = daySet.entrySet().iterator();
            int x = 0;
            map.put(SUNDAY, daySet.get(SUNDAY));
            while (it.hasNext()) {
                if (x == 0) {
                    x++;
                    continue;
                }
                Map.Entry entry = (Map.Entry) it.next();
                map.put((int) entry.getKey(), (boolean) entry.getValue());
                x++;
            }
            daySet = map;
        }

        Iterator it = daySet.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            System.out.println("ID " + entry.getKey());
            if (!weekend && ((int) entry.getKey() == SATURDAY || (int) entry.getKey() == SUNDAY)) {
                //Weekend
            } else {
                if (findViewWithTag(entry.getKey()) != null) {
                    setDaySelected((ImageView) findViewWithTag(entry.getKey()), (boolean) entry.getValue());
                } else {
                    createDayView((int) entry.getKey(), (boolean) entry.getValue());
                }
            }
        }
    }

    private int getDpFromPx(float value) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
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
        allCreatedDays.add(tag);
        if (fullSize) {
            if (mCustomDays) {
                if (allCreatedDays.indexOf(tag) > 3)
                    row2.addView(dayView);
                else
                    row1.addView(dayView);
            } else {
                if (tag == FRIDAY || tag == SATURDAY || (tag == SUNDAY && !sunday_first_day) || (tag == THURSDAY && sunday_first_day)) {
                    row2.addView(dayView);
                } else {
                    row1.addView(dayView);
                }
            }
        } else {
            row1.addView(dayView);
        }
        setDaySelected(dayView, selected);
    }

    private void setDaySelected(ImageView dayView, boolean b) {
        dayView.setSelected(b);
        String dayText = getDayText(dayView);
        int tag = (int) dayView.getTag();
        if (b) {
            dayView.setImageDrawable(getDrawableBorderSelected(dayText, selectedDayBackgroundColor, selectedBuilder));
            selectedDays.add(tag);
        } else {
            if (tag == SATURDAY || tag == SUNDAY) {
                dayView.setImageDrawable(getDrawableBorderUnselected(dayText, unSelectedWeekendColor, unselectedWeekendBuilder));
            } else {
                dayView.setImageDrawable(getDrawableBorderUnselected(dayText, unSelectedDayBackgroundColor, unselectedBuilder));
            }
            selectedDays.remove(tag);
        }
    }

    private void toggleSelection(View v) {
        ImageView day = (ImageView) v;
        boolean selected = !day.isSelected();
        if (mSeceletOnlyOne)
            unselectAllDays();
        setDaySelected(day, selected);

        if (changeListener != null) {
            changeListener.onChange(this, (int) day.getTag(), getSelectedDays());
        }
    }

    private boolean isDarkColor(int color) {
        if (android.R.color.transparent == color) return true;

        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};

        int brightness =
                (int) Math.sqrt(
                        rgb[0] * rgb[0] * .241 +
                                rgb[1] * rgb[1] * .691 +
                                rgb[2] * rgb[2] * .068);
        return brightness <= 139;
    }

    private void unselectAllDays() {
        List<Integer> list = getSelectedDays();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ImageView iv = row1.findViewWithTag(list.get(i));
                if (iv == null) {
                    iv = row2.findViewWithTag(list.get(i));
                    if (iv != null)
                        setDaySelected(iv, false);
                } else {
                    setDaySelected(iv, false);
                }
            }
        }
    }

    private void unselectDaysOne() {
        List<Integer> list = getSelectedDays();
        if (list.size() > 0) {
            for (int i = 1; i < list.size(); i++) {
                ImageView iv = row1.findViewWithTag(list.get(i));
                if (iv == null) {
                    iv = row2.findViewWithTag(list.get(i));
                    if (iv != null)
                        setDaySelected(iv, false);
                } else {
                    setDaySelected(iv, false);
                }
            }
        }
    }

    private Drawable getDrawableBorderSelected(String dayText, int color, TextDrawable.IBuilder builder) {
        if (mBorderHighlightColor != -1) {
            Drawable[] layers = new Drawable[2];
            ShapeDrawable sd1;

            if (fullSize) {
                sd1 = new ShapeDrawable(new RectShape());
                sd1.getPaint().setPathEffect(new CornerPathEffect(10));
            } else
                sd1 = new ShapeDrawable(new OvalShape());
            sd1.getPaint().setColor(mBorderHighlightColor);
            sd1.getPaint().setStyle(Paint.Style.STROKE);
            sd1.getPaint().setStrokeWidth(mBorderHighlightThickness);

            layers[1] = sd1;
            layers[0] = builder.build(dayText, color);

            return new LayerDrawable(layers);
        } else {
            return builder.build(dayText, color);
        }
    }

    private Drawable getDrawableBorderUnselected(String dayText, int color, TextDrawable.IBuilder builder) {
        if (mBorderColor != -1) {
            Drawable[] layers = new Drawable[2];
            ShapeDrawable sd1;

            if (fullSize) {
                sd1 = new ShapeDrawable(new RectShape());
                sd1.getPaint().setPathEffect(new CornerPathEffect(10));
            } else
                sd1 = new ShapeDrawable(new OvalShape());
            sd1.getPaint().setColor(mBorderColor);
            sd1.getPaint().setStyle(Paint.Style.STROKE);
            sd1.getPaint().setStrokeWidth(mBorderThickness);

            layers[1] = sd1;
            layers[0] = builder.build(dayText, color);

            return new LayerDrawable(layers);
        } else {
            return builder.build(dayText, color);
        }
    }

    private void initColors() {
        selectedDayBackgroundColor = mHighlightColor;
        unSelectedDayBackgroundColor = mBackgroundColor;//Color.LTGRAY;
        unSelectedWeekendColor = weekendDarker ? mWeekendColor : mBackgroundColor; //Color.GRAY || Color.LTGRAY
        selectedTextColor = mTextColor;//Color.WHITE;
        unSelectedTextColor = mHighlightColor;

        if (mWeekendTextColorChanged) {
            unSelectedWeekendTextColor = mWeekendTextColor;
        } else {
            unSelectedWeekendTextColor = unSelectedTextColor;
            //unSelectedWeekendTextColor = isDarkColor(unSelectedWeekendColor) ? Color.RED : Color.DKGRAY;
        }
    }

    private void initDrawableBuilders() {
        // declare the builder object once.
        selectedIShapeBuilder = TextDrawable.builder();
        selectedIShapeBuilder.beginConfig()
                .textColor(selectedTextColor)
                .fontSize(getDpFromPx(fontSize))
                .bold()
                .width(fullSize ? getScreenWidth() / 5 : getDpFromPx(width))
                .height(getDpFromPx(height))
                .endConfig();
        unselectedIShapeBuilder = TextDrawable.builder();
        unselectedIShapeBuilder.beginConfig()
                .textColor(unSelectedTextColor)
                .fontSize(getDpFromPx(fontSize))
                .bold()
                .width(fullSize ? getScreenWidth() / 5 : getDpFromPx(width))
                .height(getDpFromPx(height))
                .endConfig();

        unselectedWeekendIShapeBuilder = TextDrawable.builder();
        unselectedWeekendIShapeBuilder.beginConfig()
                .textColor(weekendDarker ? unSelectedWeekendTextColor : unSelectedTextColor)
                .fontSize(getDpFromPx(fontSize))
                .bold()
                .width(fullSize ? getScreenWidth() / 5 : getDpFromPx(width))
                .height(getDpFromPx(height))
                .endConfig();
    }

    private void initRecurrence() {
        try {
            removeViewInLayout(spinner);
        } catch (NullPointerException e) {
            // Not in Parent View
        }

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
    }

    public void redrawDays() {
        initColors();
        initRecurrence();
        initDrawableBuilders();
        if (fullSize) {
            if (row2.getParent() != null)
                ((ViewGroup) row2.getParent()).removeView(row2);
            addView(row2);

            selectedBuilder = selectedIShapeBuilder.roundRect(10);
            unselectedBuilder = unselectedIShapeBuilder.roundRect(10);
            unselectedWeekendBuilder = unselectedWeekendIShapeBuilder.roundRect(10);
        } else {
            if (row2.getParent() != null)
                ((ViewGroup) row2.getParent()).removeView(row2);
            selectedBuilder = selectedIShapeBuilder.round();
            unselectedBuilder = unselectedIShapeBuilder.round();
            unselectedWeekendBuilder = unselectedWeekendIShapeBuilder.round();
        }

        List<Integer> selectedDays = getSelectedDays();

        if (mCustomDays && lastCustomDaysMap != null && !lastCustomDaysMap.isEmpty()) {
            createDayViews(lastCustomDaysMap);
        } else {
            createDayViews();
        }

        setSelectedDays(selectedDays);
    }


    /**
     * Get selected days as {@link Integer}
     * Day value is {@link java.util.Calendar}
     *
     * @return {@link List} of selected days as {@link Integer}
     */
    public List<Integer> getSelectedDays() {
        List<Integer> list = new ArrayList<>(selectedDays);
        if (!mCustomDays)
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
        if (!mCustomDays) {
            for (int day = SUNDAY; day <= SATURDAY; day++) {
                if (weekend) {
                    setDaySelected((ImageView) findViewWithTag(day), list.contains(day));
                } else {
                    if (day != SATURDAY && day != SUNDAY) {
                        setDaySelected((ImageView) findViewWithTag(day), list.contains(day));
                    }
                }
            }
        } else {
            for (int i : allCreatedDays) {
                ImageView iv = findViewWithTag(i);
                if (iv != null) {
                    setDaySelected((ImageView) findViewWithTag(i), list.contains(i));
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

    /**
     * Set select only one
     *
     * @param b true, false
     */
    public void setSelectOnlyOne(boolean b) {
        mSeceletOnlyOne = b;

        if (b)
            unselectDaysOne();
    }

    /**
     * Set custom days
     *
     * @param m
     */
    public void setCustomDays(LinkedHashMap<Integer, Boolean> m) {
        if (m != null) {
            mCustomDays = true;
            lastCustomDaysMap = m;
            createDayViews(m);
        } else {
            mCustomDays = false;
            lastCustomDaysMap = null;
            createDayViews();
        }
    }

    /**
     * get if fullsize
     */
    public boolean getFullSize() {
        return fullSize;
    }

    /**
     * Set the days fullsize
     *
     * @param b true, false
     */
    public void setFullSize(boolean b) {
        fullSize = b;
    }

    /**
     * Set the highlightcolor
     *
     * @param color
     */
    public void setHighlightColor(String color) {
        mHighlightColor = Color.parseColor(color);
    }

    /**
     * get highlightcolor
     */
    public int getHighlightColor() {
        return mHighlightColor;
    }

    /**
     * Set the highlightcolor
     *
     * @param color
     */
    public void setHighlightColor(@ColorInt int color) {
        mHighlightColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Set the backgroundcolor
     *
     * @param color
     */
    public void setBackgroundColor(String color) {
        mBackgroundColor = Color.parseColor(color);
    }

    /**
     * get backgroundcolor
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Set the backgroundcolor
     *
     * @param color
     */
    public void setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Set the weekendcolor
     *
     * @param color
     */
    public void setWeekendColor(String color) {
        mWeekendColor = Color.parseColor(color);
    }

    /**
     * get weekendcolor
     */
    public int getWeekendColor() {
        return mWeekendColor;
    }

    /**
     * Set the weekendcolor
     *
     * @param color
     */
    public void setWeekendColor(@ColorInt int color) {
        mWeekendColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Set the textcolor
     *
     * @param color
     */
    public void setTextColor(String color) {
        mTextColor = Color.parseColor(color);
    }

    /**
     * get textcolor
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Set the textcolor
     *
     * @param color
     */
    public void setTextColor(@ColorInt int color) {
        mTextColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Set the weekendtextcolor
     *
     * @param color
     */
    public void setWeekendTextColor(String color) {
        mWeekendTextColorChanged = true;
        mWeekendTextColor = Color.parseColor(color);
    }

    /**
     * get weekendtextcolor
     */
    public int getWeekendTextColor() {
        return mWeekendTextColor;
    }

    /**
     * Set the weekendtextcolor
     *
     * @param color
     */
    public void setWeekendTextColor(@ColorInt int color) {
        mWeekendTextColorChanged = true;
        mWeekendTextColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Set the bordercolor
     *
     * @param color
     */
    public void setBorderColor(String color) {
        mBorderColor = Color.parseColor(color);
    }

    /**
     * get bordercolor
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * Set the bordercolor
     *
     * @param color
     */
    public void setBorderColor(@ColorInt int color) {
        mBorderColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Set the borderhighlightcolor
     *
     * @param color
     */
    public void setBorderHighlightColor(String color) {
        mBorderHighlightColor = Color.parseColor(color);
    }

    /**
     * get borderhighlightcolor
     */
    public int getBorderHighlightColor() {
        return mBorderHighlightColor;
    }

    /**
     * Set the borderhighlightcolor
     *
     * @param color
     */
    public void setBorderHighlightColor(@ColorInt int color) {
        mBorderHighlightColor = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * get borderthickness
     */
    public int getBorderThickness() {
        return mBorderThickness;
    }

    /**
     * Set the borderthickness
     *
     * @param thickness
     */
    public void setBorderThickness(int thickness) {
        mBorderThickness = thickness;
    }

    /**
     * get borderhighlightthickness
     */
    public int getBorderHighlightThickness() {
        return mBorderHighlightThickness;
    }

    /**
     * Set the borderhighlightthickness
     *
     * @param thickness
     */
    public void setBorderHighlightThickness(int thickness) {
        mBorderHighlightThickness = thickness;
    }

    /**
     * get if Sunday is first day
     */
    public boolean getSundayFirstDay() {
        return sunday_first_day;
    }

    /**
     * Set Sunday is first day of week
     *
     * @param b
     */
    public void setSundayFirstDay(boolean b) {
        sunday_first_day = b;
    }

    /**
     * get if weekend is showing
     */
    public boolean getShowWeekend() {
        return weekend;
    }

    /**
     * Set weekend is showing
     *
     * @param b
     */
    public void setShowWeekend(boolean b) {
        weekend = b;
    }

    /**
     * get if weekend has other colors enabled
     */
    public boolean getWeekendDarker() {
        return weekendDarker;
    }

    /**
     * Set weekend has other colors
     *
     * @param b
     */
    public void setWeekendDarker(boolean b) {
        weekendDarker = b;
    }

}