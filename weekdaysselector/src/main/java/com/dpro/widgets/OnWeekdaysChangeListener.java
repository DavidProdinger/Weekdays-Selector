package com.dpro.widgets;

import android.view.View;

import java.util.List;

/**
 * Project: Weekdays-Selector
 * <p>
 * Created by David Prodinger <dprodinger@aon.at> on 24.10.2017.
 */

public interface OnWeekdaysChangeListener {
    void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays);
}
