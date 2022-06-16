package com.baewha.health01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baewha.health01.decorator.SaturdayDecorator;
import com.baewha.health01.decorator.SundayDecorator;
import com.baewha.health01.decorator.TodayDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class CalActivity extends AppCompatActivity {
    private MaterialCalendarView cal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);

        cal = findViewById(R.id.cal);
        cal.addDecorator(new SundayDecorator());
        cal.addDecorator(new SaturdayDecorator());
        cal.addDecorator(new TodayDecorator(this));
    }
}