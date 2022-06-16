package com.baewha.health01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Calendar;

public class WeighteditActivity extends AppCompatActivity {
    Button btn_genger, btn_height, btn_gweight, btn_nweight, btn_cancel, btn_save;
    String height1, height2, gweight1, gweight2, nweight1, nweight2, today, date, height, gweight, nweight, dbid , genger;
    /*height1 = 키 소수점 앞 부분 값, height2 = 키 소수점 뒷 부분 값, gweight1 = 목표체중 소수점 앞 부분 값, gweight2 = 목표체중 소수점 뒷 부분 값,
    nweight1 = 현재체중 소수점 앞 부분 값, nweight2 = 현재체중 소수점 뒷 부분 값 */
    Database db;
    SQLiteDatabase sqlDB;
    Calendar cal;
    Integer year, month, day;
    boolean gweightCheck, dateCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weightedit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("회원님의 페이지??????");

        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        db = new Database(this);
        findId();
        btn();
        todayDate();
        gweightCheck = checkGweight(dbid); // 입력된 목표 체중 있는지 확인 false면 없는 것
        dateCheck = checkDate(dbid, today); // 오늘 날짜로 입력된 체중이 있는지 확인 false면 없는 것

        if(gweightCheck==true){ // true면 데이터 있음 -> 불러오기
            if(dateCheck==true){ // 오늘 날짜로 입력된 데이터가 있으면 불러오기 없으면 setText해서 오늘 날짜로 새 데이터 입력하게 하기
                selectData1(dbid);
            } else{
                selectData1(dbid);
                btn_nweight.setText("");
            }
        }
        selectData2(dbid); // 나이 불러오는 메소드

    }
    //뒤로가기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void findId(){
        btn_genger = findViewById(R.id.btn_genger);
        btn_height = findViewById(R.id.btn_height);
        btn_gweight = findViewById(R.id.btn_gweight);
        btn_nweight = findViewById(R.id.btn_nweight);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
    }
    public void btn(){
        btn_genger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeighteditActivity.this,"성별은 회원 정보 수정에서 수정할 수 있습니다", Toast.LENGTH_SHORT).show();
            }
        });
        btn_height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_height(v);
                //Toast.makeText(WeighteditActivity.this,height+"d", Toast.LENGTH_SHORT).show();

            }
        });
        btn_gweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_gweight(v);
            }
        });
        btn_nweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_nweight(v);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }
    public void todayDate(){
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        if(month<10){
            if(day<10){
                today=year+"-0"+month+"-0"+day;
            }else{
                today=year+"-0"+month+"-"+day;
            }
        }else{
            if(day<10){
                today=year+"-"+month+"-0"+day;
            }else{
                today=year+"-"+month+"-"+day;
            }
        }
    }
    public void save(){
        height = height1+"."+height2;
        gweight = gweight1+"."+gweight2;
        nweight = nweight1+"."+nweight2;

        if(height1 == null || gweight1 == null || nweight1 == null){
            if(height1 == null){
                Toast.makeText(WeighteditActivity.this,"키를 입력해 주세요", Toast.LENGTH_SHORT).show();
            }else if(gweight == null){
                Toast.makeText(WeighteditActivity.this,"목표 체중을 입력해 주세요", Toast.LENGTH_SHORT).show();
            }else if(nweight == null){
                Toast.makeText(WeighteditActivity.this,"현재 체중을 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(dateCheck==true){ //오늘 입력된 데이터 있음 -> 오늘 날짜 데이터 수정
                updateData(height, gweight, nweight);
                Toast.makeText(WeighteditActivity.this,"수정 완료", Toast.LENGTH_SHORT).show();
                finish();
            }else{ // 오늘 입력된 데이터 없음 -> 오늘 날짜로 데이터 저장
                insertData(today, dbid, height, gweight, nweight);
                Toast.makeText(WeighteditActivity.this,"저장 완료", Toast.LENGTH_SHORT).show();
                finish();
            }
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
    public void selectData1(String wId){
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

        btn_height.setText(height1+"."+height2+"cm");
        btn_gweight.setText(gweight+"kg");
        btn_nweight.setText(nweight+"kg");
    }
    public void selectData2(String mId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select mGenger from member where mId = ? ;",new String[] {mId});
        while(cursor.moveToNext()){
            genger = cursor.getString(0);
        }
        cursor.close();
        sqlDB.close();

        btn_genger.setText(genger);
    }
    public void insertData(String date, String id, String height, String gweight, String nweight){
        sqlDB = db.getWritableDatabase();
        sqlDB.execSQL("insert into weight(wDate, wId, wHeight, wGweight, wNweight) values "+"('"+date+"','"+id+"', '"+height+"','"+gweight+"','"+nweight+"');");
        sqlDB.close();
    }
    public void updateData(String height, String gweight, String nweight){
        sqlDB = db.getWritableDatabase();
        sqlDB.execSQL("update weight set wHeight = '"+height+"', wGweight = '"+gweight+"', wNweight = '"+nweight+"' where wId = '"+dbid+"' and wDate = '"+today+"';");
        sqlDB.close();
    }
    //키 설정 다이얼로그
    public void dialog_height(View v){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_height, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();
        // np1 = 키 소수점 앞 부분
        NumberPicker np1 = dialogView.findViewById(R.id.np1);
        np1.setMaxValue(200);
        np1.setMinValue(100);

        if(btn_height.getText().toString()==""){ // 키 소수점 앞 부분 현재 값 설정
            np1.setValue(150);
        }else{ // 선택한 값이 있으면 그 값으로 현재 값 보여주기

            int h1 = Integer.valueOf(height1);
            np1.setValue(h1);
        }
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // numberpicker 선택한 값 실시간으로 변경
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        // np2 = 키 소수점 뒷 부분
        NumberPicker np2 = dialogView.findViewById(R.id.np2);
        np2.setMaxValue(9);
        np2.setMinValue(0);

        if(btn_height.getText().toString()==""){ // 키 소수점 뒷 부분 현재 값 설정
            np2.setValue(0);
        }else{ // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            int h2 = Integer.valueOf(height2);
            np2.setValue(h2);
        }
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // numberpicker 선택한 값 실시간으로 변경
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        //확인 버튼 눌렀을 때
        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height1 = String.valueOf(np1.getValue()); // 키 소수점 앞 부분 값 가져오기
                height2 = String.valueOf(np2.getValue()); // 키 소수점 뒷 부분 값 가져오기
                btn_height.setText(height1+"."+height2+" cm");
                alertDialog.dismiss();
            }
        });
        //취소 버튼 눌렀을 때
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    //목표 체중 설정 다이얼로그
    public void dialog_gweight(View v){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_gweight, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();
        // np1 = 체중 소수점 앞 부분
        NumberPicker np1 = dialogView.findViewById(R.id.np1);
        np1.setMaxValue(200);
        np1.setMinValue(0);

        if(btn_gweight.getText().toString()==""){ // 체중 소수점 앞 부분 현재 값 설정
            np1.setValue(50);
        }else{ // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            int g1 = Integer.valueOf(gweight1);
            np1.setValue(g1);
        }
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // numberpicker 선택한 값 실시간으로 변경
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        // np2 = 체중 소수점 뒷 부분
        NumberPicker np2 = dialogView.findViewById(R.id.np2);
        np2.setMaxValue(9);
        np2.setMinValue(0);

        if(btn_gweight.getText().toString()==""){ // 체중 소수점 뒷 부분 현재 값 설정
            np2.setValue(0);
        }else{ // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            int g2 = Integer.valueOf(gweight2);
            np2.setValue(g2);
        }
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // numberpicker 선택한 값 실시간으로 변경
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        //확인 버튼 눌렀을 때
        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gweight1 = String.valueOf(np1.getValue()); // 체중 소수점 앞 부분 값 가져오기
                gweight2 = String.valueOf(np2.getValue()); // 체중 소수점 뒷 부분 값 가져오기
                btn_gweight.setText(gweight1+"."+gweight2+" kg");
                alertDialog.dismiss();
            }
        });
        //취소 버튼 눌렀을 때
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    //현재 체중 설정 다이얼로그
    public void dialog_nweight(View v){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nweight, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();
        // np1 = 체중 소수점 앞 부분
        NumberPicker np1 = dialogView.findViewById(R.id.np1);
        np1.setMaxValue(200);
        np1.setMinValue(0);

        if(btn_nweight.getText().toString()==""){ // 체중 소수점 앞 부분 현재 값 설정
            np1.setValue(50);
        }else{ // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            int n1 = Integer.valueOf(nweight1);
            np1.setValue(n1);
        }
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // numberpicker 선택한 값 실시간으로 변경
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        // np2 = 체중 소수점 뒷 부분
        NumberPicker np2 = dialogView.findViewById(R.id.np2);
        np2.setMaxValue(9);
        np2.setMinValue(0);

        if(btn_nweight.getText().toString()==""){ // 체중 소수점 뒷 부분 현재 값 설정
            np2.setValue(0);
        }else{ // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            int n2 = Integer.valueOf(nweight2);
            np2.setValue(n2);
        }
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // numberpicker 선택한 값 실시간으로 변경
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        //확인 버튼 눌렀을 때
        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nweight1 = String.valueOf(np1.getValue()); // 체중 소수점 앞 부분 값 가져오기
                nweight2 = String.valueOf(np2.getValue()); // 체중 소수점 뒷 부분 값 가져오기
                btn_nweight.setText(nweight1+"."+nweight2+" kg");
                alertDialog.dismiss();
            }
        });
        //취소 버튼 눌렀을 때
        Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}