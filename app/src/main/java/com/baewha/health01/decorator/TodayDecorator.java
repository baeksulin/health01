package com.baewha.health01.decorator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.baewha.health01.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

public class TodayDecorator implements DayViewDecorator {
    private CalendarDay date;
    private final Drawable drawable;
    public TodayDecorator(Activity context){
        date = CalendarDay.today();
        drawable = context.getResources().getDrawable(R.drawable.select);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.3f));
        view.addSpan(new ForegroundColorSpan(Color.BLACK));

    }

    public void setDay(Date date){
        this.date = CalendarDay.from(date);
    }
}
