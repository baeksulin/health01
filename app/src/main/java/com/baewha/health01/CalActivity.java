package com.baewha.health01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baewha.health01.decorator.EventDecorator;
import com.baewha.health01.decorator.MyselectionDecorator;
import com.baewha.health01.decorator.SaturdayDecorator;
import com.baewha.health01.decorator.SundayDecorator;
import com.baewha.health01.decorator.TodayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class CalActivity extends AppCompatActivity {
    Button tap1, tap2, tap3, tap5;
    private MaterialCalendarView cal;
    Integer medcycle, year, month, day;
    String start = "", dbid, today ="";
    Calendar calendar;
    Database db;
    SQLiteDatabase sqlDB;
    boolean cycleCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        setTitle("건강 지킴이 - 일정");
        findId(); //아이디 연결
        btn();
        calendar = Calendar.getInstance();
        todayDate();
        Intent i = getIntent();
        dbid = i.getStringExtra("아이디"); //로그인한 아이디 데이터 값 받아오기
        db = new Database(this);

        cycleCheck = checkCycle(dbid);
        if(cycleCheck == true){
            selectData(dbid);
        }else{
            medcycle=0;
        }
        //MaterialCalendarView에 Decorator 추가
        cal.addDecorator(new MyselectionDecorator(this));
        cal.addDecorator(new SundayDecorator());
        cal.addDecorator(new SaturdayDecorator());
        cal.addDecorator(new TodayDecorator(this));
        cal.setHeaderTextAppearance(R.style.CalendarWidgetHeader);
        new Event().executeOnExecutor(Executors.newSingleThreadExecutor());
    }
    //아이디 연결하기
    public void findId(){
        cal = findViewById(R.id.cal);
        tap1 = findViewById(R.id.tap1);
        tap2 = findViewById(R.id.tap2);
        tap3 = findViewById(R.id.tap3);
        tap5 = findViewById(R.id.tap5);
    }
    public void btn(){
        tap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("아이디", dbid);
                startActivity(intent);
            }
        });
        tap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeightActivity.class);
                intent.putExtra("아이디", dbid);
                startActivity(intent);
            }
        });
        tap3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SickActivity.class);
                intent.putExtra("아이디", dbid);
                startActivity(intent);
            }
        });
        tap5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemeditActivity.class);
                intent.putExtra("아이디", dbid);
                startActivity(intent);
            }
        });
    }
    //MaterialCalendarView를 사용하기 위한 Event 클래스
    private class Event extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                Date date1 = dateFormat.parse(start); // 약 시작일
                Date date2 = dateFormat.parse(today); // 오늘

                long Sec = (date1.getTime() - date2.getTime()) / 1000; // 초
                long Days = Sec / (24*60*60); // 약 시작일과 오늘 일자수 비교

                if(date1.before(date2)){ // date1이 date2보다 먼저인지? => true
                    calendar.add(Calendar.DAY_OF_MONTH, (int) Days); //Days 후 부터 시작
                }else{
                    calendar.add(Calendar.DAY_OF_MONTH, (int) -Days); //Days 전 부터 시작
                }
            } catch (ParseException ex) {
            }
            //여기까지 하면 질병 정보 화면에서 선택한 시작일로 캘린더를 시작할 수 있음
            ArrayList<CalendarDay> dates = new ArrayList<>();
            for(int i = 0; i < 100; i++){
                CalendarDay days = CalendarDay.from(calendar);
                dates.add(days);
                calendar.add(Calendar.DATE, medcycle);
            }
            return dates;
        }
        //약 주기마다 빨간 점 찍어주기
        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }
            cal.addDecorator(new EventDecorator(Color.RED, calendarDays));
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
    //데이터베이스에서 데이터 검색
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
    //오늘 날짜 검색
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