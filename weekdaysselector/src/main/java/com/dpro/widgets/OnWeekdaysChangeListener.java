package com.dpro.widgets;

import android.view.View;

import java.util.List;

/**
 * Project: Weekdays-Selector
 * <p>
 * Created by David Prodinger <dprodinger@aon.at> on 24.10.2017.
 */

public interface OnWeekdaysChangeListener {
    /**
     * @param view             View of Weekdayspicker
     * @param clickedDayOfWeek Last clicked day
     * @param selectedDays     Integer-list of days from {@link java.util.Calendar}
     */
    void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays);
}
