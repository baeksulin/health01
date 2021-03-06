package com.baewha.health01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SickActivity extends AppCompatActivity {
    TextView tv_hospital, tv_sickname , tv_medcycle, tv_start, tv_check, tv_tell;
    Button btn_edit, tap1, tap2, tap4, tap5;
    String hospital, sickname, medcycle, start, check, dbid, tell;
    Database db;
    SQLiteDatabase sqlDB;
    boolean sicknameCheck, tellCheck;
    ImageView img_call;
    private Permission permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sick);
        setTitle("건강 지킴이 - 질병 정보");
        Intent i = getIntent();
        dbid = i.getStringExtra("아이디"); // 로그인시 사용한 아이디 데이터 받기
        db = new Database(this);
        findId(); //아이디 연결
        btn();
        sicknameCheck = checkSickname(dbid);
        tellCheck = checkTell(dbid);
        if(sicknameCheck==true){
            selectData(dbid);
        }
        if(tellCheck==false){
            img_call.setVisibility(View.INVISIBLE);
        }
    }
    //finish로 돌아오면 화면 새로고침
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_sick);

        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        db = new Database(this);
        findId();
        btn();
        sicknameCheck = checkSickname(dbid);
        tellCheck = checkTell(dbid);
        if(sicknameCheck==true){
            selectData(dbid);
        }
        if(tellCheck==false){
            img_call.setVisibility(View.INVISIBLE);
        }
    }

    public void findId(){
        tv_hospital = findViewById(R.id.tv_hospital);
        tv_tell = findViewById(R.id.tv_tell);
        tv_sickname = findViewById(R.id.tv_sickname);
        tv_medcycle = findViewById(R.id.tv_medcycle);
        tv_start = findViewById(R.id.tv_start);
        tv_check = findViewById(R.id.tv_check);
        btn_edit = findViewById(R.id.btn_edit);
        img_call = findViewById(R.id.img_call);
        tap1 = findViewById(R.id.tap1);
        tap2 = findViewById(R.id.tap2);
        tap4 = findViewById(R.id.tap4);
        tap5 = findViewById(R.id.tap5);
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
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = tell.replaceAll("-","");
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tel));

                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M){
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                        getApplicationContext().startActivity(intent);
                    }else{
                    }
                }else {
                    getApplicationContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }

            }
        });
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
    //입력한 전화번호가 있는지 확인
    public boolean checkTell(String sid){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select sTell from sick where sid = ? ;",new String[] {sid});
        if(cursor.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }
    public void selectData(String sId){ // weight 테이블에 있는 키, 목표 체중, 오늘 체중 데이터 검색
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select sHospital, sTell, sSickname, sMedcycle, sStart, sCheck from sick where sId = ? ;",new String[] {sId});
        while(cursor.moveToNext()){
            hospital = cursor.getString(0);
            tell = cursor.getString(1);
            sickname = cursor.getString(2);
            medcycle = cursor.getString(3);
            start = cursor.getString(4);
            check = cursor.getString(5);
        }
        cursor.close();
        sqlDB.close();

        tv_hospital.setText(hospital);
        tv_tell.setText(tell);
        tv_sickname.setText(sickname);
        tv_medcycle.setText(medcycle+"일");
        tv_start.setText(start);
        tv_check.setText("\t"+check);
    }
}