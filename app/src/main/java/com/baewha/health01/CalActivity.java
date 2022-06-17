package com.baewha.health01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.baewha.health01.decorator.EventDecorator;
import com.baewha.health01.decorator.MyselectionDecorator;
import com.baewha.health01.decorator.OtherDacorator;
import com.baewha.health01.decorator.SaturdayDecorator;
import com.baewha.health01.decorator.SundayDecorator;
import com.baewha.health01.decorator.TodayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class CalActivity extends AppCompatActivity {
    private MaterialCalendarView cal;
    Integer medcycle, year, month, day;
    String start, dbid, today;
    Calendar calendar;
    Database db;
    SQLiteDatabase sqlDB;
    boolean cycleCheck;
    TextView tv_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);

        calendar = Calendar.getInstance();
        todayDate();
        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        db = new Database(this);

        cycleCheck = checkCycle(dbid);
        if(cycleCheck == true){
            selectData(dbid);
        }else{
            medcycle=0;
        }
        cal = findViewById(R.id.cal);
        tv_txt = findViewById(R.id.tv_txt);
        //cal.setSelectedDate(CalendarDay.today());
        cal.addDecorator(new MyselectionDecorator(this));
        cal.addDecorator(new SundayDecorator());
        cal.addDecorator(new SaturdayDecorator());
        cal.addDecorator(new TodayDecorator(this));
        //cal.addDecorator(new OtherDacorator());
        cal.setHeaderTextAppearance(R.style.CalendarWidgetHeader);
        new Event().executeOnExecutor(Executors.newSingleThreadExecutor());
    }
    private class Event extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                Date date1 = dateFormat.parse(start); // 약 시작일
                Date date2 = dateFormat.parse(today); // 오늘

                long Sec = (date1.getTime() - date2.getTime()) / 1000; // 초
                long Days = Sec / (24*60*60); // 일자수 비교

                if(date1.before(date2)){ // date1이 date2보다 먼저인지? => true
                    calendar.add(Calendar.DAY_OF_MONTH, (int) Days); //Days 후 부터 시작
                }else{
                    calendar.add(Calendar.DAY_OF_MONTH, (int) -Days); //Days 전 부터 시작
                }
            } catch (ParseException ex) {
            }

            ArrayList<CalendarDay> dates = new ArrayList<>();
            for(int i = 0; i < 100; i++){
                CalendarDay days = CalendarDay.from(calendar);
                dates.add(days);
                calendar.add(Calendar.DATE, medcycle);
            }
            return dates;
        }
        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            cal.addDecorator(new EventDecorator(Color.RED, calendarDays));
            //tv_txt.setText(Arrays.toString(new List[]{calendarDays}));

        }
    }
    //입력한 질병주기가 있는지 확인
    public boolean checkCycle(String sid){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select sMedcycle from sick where sid = ? ;",new String[] {sid});
        if(cursor.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }
    public void selectData(String sId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select sMedcycle, sStart from sick where sId = ? ;",new String[] {sId});
        while(cursor.moveToNext()){
            medcycle = cursor.getInt(0);
            start = cursor.getString(1);
        }
        cursor.close();
        sqlDB.close();
    }
    public void todayDate(){
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        if(month<10){
            today=year+"-0"+month+"-"+day;
        }else{
            today=year+"-"+month+"-"+day;
        }
    }
}