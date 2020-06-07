package com.dpro.weekdaysselector;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dpro.widgets.OnWeekRecurrenceChangeListener;
import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private final LinkedHashMap<Integer, Boolean> mp = new LinkedHashMap<>();
    private WeekdaysPicker widget;
    private List<Integer> selected_days;

    private TextView tv_selected_days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selected_days = Arrays.asList(SATURDAY, WEDNESDAY, THURSDAY, SUNDAY);

        mp.put(SUNDAY, true);
        mp.put(SATURDAY, true);
        mp.put(THURSDAY, false);
        mp.put(FRIDAY, true);
        mp.put(TUESDAY, true);
        mp.put(SATURDAY, false); //For duplicated values, the first one is counting, but the last one is updating the selected value

        widget = findViewById(R.id.weekdays);
        widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                tv_selected_days.setText("Selected days: " + Arrays.toString(selectedDays.toArray()));
            }
        });
        widget.setOnWeekRecurrenceChangeListener(new OnWeekRecurrenceChangeListener() {

            @Override
            public void onWeekChange(View view, List<Integer> selectedDays, int even_week) {
                // Do something else
                switch (even_week) {
                    case WeekdaysPicker.ALL:
                        // if all weeks selected
                        break;
                    case WeekdaysPicker.ODD:
                        // if odd weeks selected
                        break;
                    case WeekdaysPicker.EVEN:
                        // if even weeks selected
                        break;
                }
            }
        });

        LinearLayout weekrecurrenceview = findViewById(R.id.weekrecurrenceview);
        weekrecurrenceview.addView(WeekdaysPicker.getRecurrenceSpinner(widget, this, weekrecurrenceview));

        ((Switch) findViewById(R.id.sw_enable)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_highlight_color)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_background_color)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_weekend_color)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_text_color)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_sunday_first)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_show_weekend)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_recurrence)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_weekenddarker)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_weekend_text_color)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_border_color)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_border_thickness)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_border_highlight_color)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_border_highlight_thickness)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_fullsize)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_customdays)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_selectonlyone)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.sw_setselecteddays)).setOnCheckedChangeListener(this);


        tv_selected_days = findViewById(R.id.tv_selected_days);
        tv_selected_days.setText("Selected days: " + Arrays.toString(widget.getSelectedDays().toArray()));

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            //selectDay
            case R.id.sw_enable:
                widget.setEditable(isChecked);
                break;
            case R.id.sw_highlight_color:
                widget.setHighlightColor(isChecked ? Color.BLACK : Color.RED);
                break;
            case R.id.sw_background_color:
                widget.setBackgroundColor(isChecked ? Color.YELLOW : Color.LTGRAY);
                break;
            case R.id.sw_weekend_color:
                widget.setWeekendColor(isChecked ? Color.GREEN : Color.GRAY);
                break;
            case R.id.sw_text_color:
                widget.setTextColor(isChecked ? Color.BLUE : Color.WHITE);
                break;
            case R.id.sw_sunday_first:
                widget.setSundayFirstDay(isChecked);
                break;
            case R.id.sw_show_weekend:
                widget.setShowWeekend(isChecked);
                break;
            case R.id.sw_recurrence:
                widget.setRecurrence(isChecked);
                break;
            case R.id.sw_weekenddarker:
                widget.setWeekendDarker(isChecked);
                break;
            case R.id.sw_weekend_text_color:
                widget.setWeekendTextColor(isChecked ? Color.BLUE : Color.WHITE);
                break;
            case R.id.sw_border_color:
                widget.setBorderColor(isChecked ? Color.BLUE : -1);
                break;
            case R.id.sw_border_thickness:
                widget.setBorderThickness(isChecked ? 10 : 3);
                break;
            case R.id.sw_border_highlight_color:
                widget.setBorderHighlightColor(isChecked ? Color.MAGENTA : -1);
                break;
            case R.id.sw_border_highlight_thickness:
                widget.setBorderHighlightThickness(isChecked ? 10 : 3);
                break;
            case R.id.sw_fullsize:
                widget.setFullSize(isChecked);
                break;
            case R.id.sw_customdays:
                widget.setCustomDays(isChecked ? mp : null);
                break;
            case R.id.sw_selectonlyone:
                widget.setSelectOnlyOne(isChecked);
                break;
            case R.id.sw_setselecteddays:
                if (isChecked) {
                    widget.setSelectedDays(selected_days);
                    tv_selected_days.setText("Selected days: " + Arrays.toString(widget.getSelectedDays().toArray()));
                }
                break;
        }
        widget.redrawDays();
    }
}
