package com.baewha.health01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class WeightActivity extends AppCompatActivity {
    TextView tv_genger, tv_height, tv_gweight, tv_nweight;
    Button btn_edit;
    String height1, height2, gweight1, gweight2, nweight1, nweight2, today, date, height, gweight, nweight, dbid , genger;
    Database db;
    SQLiteDatabase sqlDB;
    Calendar cal;
    Integer year, month, day;
    boolean gweightCheck, dateCheck;
    ListView list_pweight;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        db = new Database(this);
        findId();
        btn();
        todayDate();
        gweightCheck = checkGweight(dbid); // 입력된 목표 체중 있는지 확인 false면 없는 것
        dateCheck = checkDate(dbid, today); // 오늘 날짜로 입력된 체중이 있는지 확인 false면 없는 것

        if(gweightCheck==true){ // true면 데이터 있음 -> 불러오기
            if(dateCheck==true){ // 오늘 날짜로 입력된 데이터가 있으면 불러오기 없으면 오늘 날짜로 체중 입력
                selectData1(dbid);
            } else{
                selectData1(dbid);
                tv_nweight.setText("");
            }
        }
        selectData2(dbid); // 나이 불러오는 메소드
        selectData3(dbid);

    }
    public void findId(){
        btn_edit = findViewById(R.id.btn_edit);
        tv_genger = findViewById(R.id.tv_genger);
        tv_height = findViewById(R.id.tv_height);
        tv_gweight = findViewById(R.id.tv_gweight);
        tv_nweight = findViewById(R.id.tv_nweight);
        list_pweight = findViewById(R.id.list_pweight);
    }
    public void btn(){
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WeighteditActivity.class);
                intent.putExtra("아이디", dbid);
                startActivity(intent);
            }
        });
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
    public void selectData1(String wId){ // weight 테이블에 있는 키, 목표 체중, 오늘 체중 데이터 검색
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select wDate, wHeight, wGweight, wNweight from weight where wId = ? ;",new String[] {wId});
        while(cursor.moveToNext()){
            date = cursor.getString(0);
            height = cursor.getString(1);
            gweight = cursor.getString(2);
            nweight = cursor.getString(3);
        }
        cursor.close();
        sqlDB.close();

        height1 = height.substring(0, height.length()-2);
        height2 =height.substring(height.length()-1, height.length());
        gweight1 = gweight.substring(0, gweight.length()-2);
        gweight2 =gweight.substring(gweight.length()-1, gweight.length());
        nweight1 = nweight.substring(0, nweight.length()-2);
        nweight2 =nweight.substring(nweight.length()-1, nweight.length());

        tv_height.setText(height1+"."+height2+"cm");
        tv_gweight.setText(gweight+"kg");
        tv_nweight.setText(nweight+"kg");
    }
    public void selectData2(String mId){ // member 테이블에 있는 성별 데이터 검색
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select mGenger from member where mId = ? ;",new String[] {mId});
        while(cursor.moveToNext()){
            genger = cursor.getString(0);
        }
        cursor.close();
        sqlDB.close();

        tv_genger.setText(genger);
    }
    public void selectData3(String wId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select wDate, wNweight from weight where wId = ?;", new String[] {wId});

        PweightAdapter adapter = new PweightAdapter();

        while (cursor.moveToNext()){
            adapter.addItem(cursor.getString(0), cursor.getString(1));
        }
        cursor.close();
        sqlDB.close();

        list_pweight.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}