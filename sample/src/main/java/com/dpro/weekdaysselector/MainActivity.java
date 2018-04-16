package com.dpro.weekdaysselector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dpro.widgets.OnWeekRecurrenceChangeListener;
import com.dpro.widgets.OnWeekdaysChangeListener;
import com.dpro.widgets.WeekdaysPicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Integer> days = Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY, Calendar.SUNDAY);

        WeekdaysPicker widget = findViewById(R.id.weekdays);
        widget.setSelectedDays(days);
        widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                // Do Something
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
    }
}
