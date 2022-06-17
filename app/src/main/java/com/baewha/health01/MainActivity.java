package com.baewha.health01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btn_gowe,btn_gocal, tap1, tap2, tap3, tap4, tap5;
    String dbid, gweight, nweight, fweight, today;
    ProgressBar pb;
    Database db;
    SQLiteDatabase sqlDB;
    String name, medcycle, start;
    TextView tv_hello, tv_percent, tv_today, tv_txt;
    private Permission permission;
    private long backBtnTime=0;
    Double now;
    Boolean gweightCheck, dateCheck, cycleCheck;
    Calendar cal;
    Integer year, month, day;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        findId();
        tap();
        todayDate();

        db = new Database(this);
        //로그인시 사용한 아이디 값 가져오기
        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        selectData1(dbid);
        tv_today.setText(today);

        gweightCheck = checkGweight(dbid); // 입력된 목표 체중 있는지 확인 false면 없는 것
        dateCheck = checkDate(dbid, today); // 오늘 날짜로 입력된 체중이 있는지 확인 false면 없는 것
        cycleCheck = checkCycle(dbid);

        if(gweightCheck==true){
            selectData2(dbid); //목표체중과 최근 체중 검색
            selectData3(dbid); //최초체중 검색
            setPb();
            if(dateCheck==true){
                tv_hello.setText("\t\t안녕하세요\t"+name+"님\n\t\t오늘 입력하신 체중은\n\t\t\t\t"+nweight+"kg 입니다");
                btn_gowe.setVisibility(View.INVISIBLE);
                if(cycleCheck==true){
                    selectData4(dbid);
                }
            }else{
                tv_hello.setText("안녕하세요\t"+name+"님\n 아직 체중을 입력하지 않았어요");
           }
        }else{
            //입력하러가기
        }

    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime){
            super.onBackPressed();
        }else{
            backBtnTime = curTime;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("종료 안내");
            builder.setMessage("어플을 종료하시겠습니까?");
            builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.finishAffinity(MainActivity.this);
                    System.exit(0);
                }
            });
            builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
        }
    }

    // 권한 체크
    private void checkPermission() {
        // PermissionSupport.java 클래스 객체 생성
        permission = new Permission(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()){
            //권한 요청
            permission.requestPermission();
        }
    }

    // Request Permission에 대한 결과 값 받아와
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //여기서도 리턴이 false로 들어온다면 (사용자가 권한 허용 거부)
        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
            // 다시 permission 요청
            permission.requestPermission();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // 아이디 연결시키는 메소드
    public void findId(){
        tv_hello = findViewById(R.id.tv_hello);
        pb = findViewById(R.id.pb);
        tv_percent = findViewById(R.id.tv_percent);
        tv_today = findViewById(R.id.tv_today);
        tv_txt = findViewById(R.id.tv_txt);
        btn_gowe = findViewById(R.id.btn_gowe);
    }
    public void todayDate(){
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        if(month<10){
            today=year+"-0"+month+"-"+day;
        }else{
            today=year+"-"+month+"-"+day;
        }
    }
    //입력한 목표 체중이 있는지 없는지 확인 없으면 아직 체중관련한 데이터를 입력하지 않은 것
    public boolean checkGweight(String wId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select wGweight from weight where wId = ? ;",new String[] {wId});
        if(cursor.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }
    //오늘 날짜 데이터 있는지 없는지 확인
    public boolean checkDate(String wId, String wDate){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select wNweight from weight where wId = ? and wDate = ?;",new String[] {wId , wDate});
        if(cursor.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }
    //입력한 약 주기가 있는지 확인
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
    public void selectData1(String mId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor= sqlDB.rawQuery("select mName from member where mId = ?;", new String[] {mId});
        while(cursor.moveToNext()){
            name = cursor.getString(0);
        }
        cursor.close();
        sqlDB.close();
    }
    public void selectData2(String wId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor= sqlDB.rawQuery("select wGweight, wNweight from weight where wId = ? order by wNum DESC limit 1;", new String[] {wId});
        while(cursor.moveToNext()){
            gweight = cursor.getString(0);
            nweight = cursor.getString(1);
        }
        cursor.close();
        sqlDB.close();
    }
    public void selectData3(String wId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor= sqlDB.rawQuery("select wNweight from weight where wId = ? order by wNum ASC limit 1;", new String[] {wId});
        while(cursor.moveToNext()){
            fweight = cursor.getString(0);
        }
        cursor.close();
        sqlDB.close();
    }
    public void selectData4(String sId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor= sqlDB.rawQuery("select sMedcycle, sStart from sick where sId = ?;", new String[] {sId});
        while(cursor.moveToNext()){
            medcycle = cursor.getString(0);
            start = cursor.getString(1);
        }
        cursor.close();
        sqlDB.close();
    }
    public void setPb(){
        Double g = Double.valueOf(gweight); //목표체충
        Double f = Double.valueOf(fweight); //처음체중
        Double n = Double.valueOf(nweight); //오늘체중

        now = 100 / (f-g)*(f-n);
        int nn = (int) Math.round(now);
        pb.setProgress(nn);
        tv_percent.setText("현재 진행률은 "+String.valueOf(nn)+"% 입니다");
    }
    public void setTxt(){
        tv_txt.setText("업데이트가 필요한 ");

    }
    public void tap(){
        tap2 = findViewById(R.id.tap2);
        tap3 = findViewById(R.id.tap3);
        tap4 = findViewById(R.id.tap4);
        tap5 = findViewById(R.id.tap5);

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
        tap4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalActivity.class);
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

}