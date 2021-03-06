package com.baewha.health01.decorator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.baewha.health01.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class MyselectionDecorator implements DayViewDecorator {
    private final Drawable drawable;

    public MyselectionDecorator(Activity context){
        drawable = context.getResources().getDrawable(R.drawable.my_selector);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
        view.addSpan(new ForegroundColorSpan(Color.BLACK));


    }
}
