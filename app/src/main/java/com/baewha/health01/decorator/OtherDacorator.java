package com.baewha.health01.decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class OtherDacorator implements DayViewDecorator {

    private final Calendar cal = Calendar.getInstance();

    public OtherDacorator(){

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(cal);
        int year, month, stDay, enDay;

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        stDay = cal.getMinimum(Calendar.DAY_OF_MONTH); // 1
        enDay = cal.getMaximum(Calendar.DAY_OF_MONTH); // 31

        return (day.getMonth() == month && day.getDay() > enDay) ||
                (day.getMonth() == month && day.getDay() < stDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#d2d2d2")));
    }
}
