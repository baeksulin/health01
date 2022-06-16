package com.baewha.health01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SickActivity extends AppCompatActivity {
    TextView tv_hospital, tv_sickname , tv_medcycle, tv_start, tv_check;
    Button btn_edit;
    String hospital, sickname, medcycle, start, check, dbid;
    Database db;
    SQLiteDatabase sqlDB;
    boolean sicknameCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sick);

        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        db = new Database(this);

        findId();
        btn();
        sicknameCheck = checkSickname(dbid);
        if(sicknameCheck==true){
            selectData(dbid);
        }
    }
    public void findId(){
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_sickname = findViewById(R.id.tv_sickname);
        tv_medcycle = findViewById(R.id.tv_medcycle);
        tv_start = findViewById(R.id.tv_start);
        tv_check = findViewById(R.id.tv_check);
        btn_edit = findViewById(R.id.btn_edit);
    }
    public void btn(){
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SickeditActivity.class);
                intent.putExtra("아이디", dbid);
                startActivity(intent);
            }
        });
    }
    //입력한 병원과 질병이 있는지 확인
    public boolean checkSickname(String sid){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select sHospital, sSickname from sick where sid = ? ;",new String[] {sid});
        if(cursor.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }

    public void selectData(String sId){ // weight 테이블에 있는 키, 목표 체중, 오늘 체중 데이터 검색
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select sHospital, sSickname, sMedcycle, sStart, sCheck from sick where sId = ? ;",new String[] {sId});
        while(cursor.moveToNext()){
            hospital = cursor.getString(0);
            sickname = cursor.getString(1);
            medcycle = cursor.getString(2);
            start = cursor.getString(3);
            check = cursor.getString(4);
        }
        cursor.close();
        sqlDB.close();

        tv_hospital.setText(hospital);
        tv_sickname.setText(sickname);
        tv_medcycle.setText(medcycle);
        tv_start.setText(start);
        tv_check.setText(check);
    }
}