package com.baewha.health01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SickeditActivity extends AppCompatActivity {
    EditText ed_hospital, ed_sickname, ed_check;
    Button btn_tell, btn_medcycle, btn_start, btn_cancel, btn_save;
    String hospital, tell, sickname, medcycle, start, check, dbid;
    Database db;
    SQLiteDatabase sqlDB;
    Calendar cal;
    boolean sicknameCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sickedit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // EditText에 입력할 때 layout은 그대로고 키보드만 생기게 하기

        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        db = new Database(this);

        findId();
        btn();
        sicknameCheck = checkSickname(dbid);
        if(sicknameCheck==true){ // 입력된 병원과 질병이 있는지 확인 true면 있다는 뜻
            selectData(dbid);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Cursor cursor = getContentResolver().query(data.getData(), new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);

            while(cursor.moveToNext()){
                tell = cursor.getString(0);

            }
            cursor.close();
            btn_tell.setText(tell);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void findId(){
        ed_hospital = findViewById(R.id.ed_hospital);
        ed_sickname = findViewById(R.id.ed_sickname);
        ed_check = findViewById(R.id.ed_check);
        btn_tell = findViewById(R.id.btn_tell);
        btn_medcycle = findViewById(R.id.btn_medcycle);
        btn_start = findViewById(R.id.btn_start);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
    }
    public void btn(){
        btn_tell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
        btn_medcycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_medcycle(v);
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_start(v);
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
                finish();
            }
        });
    }
    public void save(){
        hospital = ed_hospital.getText().toString();
        sickname = ed_sickname.getText().toString();
        //medcycle은 변수에 값을 저장하기 때문에 따로 필요 없음
        start = btn_start.getText().toString();
        check = ed_check.getText().toString();

        if(hospital.equals("") || sickname.equals("") || medcycle == null || start.equals("")){
            if(hospital.equals("")){
                Toast.makeText(SickeditActivity.this,"병원을 입력해 주세요", Toast.LENGTH_SHORT).show();
            }else if(sickname.equals("")){
                Toast.makeText(SickeditActivity.this,"질병을 입력해 주세요", Toast.LENGTH_SHORT).show();
            }else if(medcycle == null){
                Toast.makeText(SickeditActivity.this,"약 주기를 설정해 주세요", Toast.LENGTH_SHORT).show();
            }else if(start.equals("")){
                Toast.makeText(SickeditActivity.this,"시작 날짜를 선택해 주세요", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(sicknameCheck==true){
                updateData(hospital, tell, sickname, medcycle, start, check);
                Toast.makeText(SickeditActivity.this,"수정 완료", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                insertData(dbid, hospital, tell, sickname, medcycle, start, check);
                Toast.makeText(SickeditActivity.this,"저장 완료", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
    public void selectData(String sId){
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

        ed_hospital.setText(hospital);
        btn_tell.setText(tell);
        ed_sickname.setText(sickname);
        btn_medcycle.setText(medcycle+"일");
        btn_start.setText(start);
        ed_check.setText(check);
    }
    public void insertData(String id, String hospital, String tell, String sickname, String medcycle, String start, String check){
        sqlDB = db.getWritableDatabase();
        sqlDB.execSQL("insert into sick(sId, sHospital, sTell, sSickname, sMedcycle, sStart, sCheck) values "+"('"+id+"', '"+hospital+"','"+tell+"','"+sickname+"', '"+medcycle+"','"+start+"','"+check+"');");
        sqlDB.close();
    }
    public void updateData(String hospital, String tell, String sickname, String medcycle, String start, String check){
        sqlDB = db.getWritableDatabase();
        sqlDB.execSQL("update sick set sHospital = '"+hospital+"', sTell = '"+tell+"', sSickname = '"+sickname+"', sMedcycle = '"+medcycle+"' , sStart = '"+start+"', sCheck = '"+check+"' where sId = '"+dbid+"';");
        sqlDB.close();
    }
    //약 주기 설정 다이얼로그
    public void dialog_medcycle(View v) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_medcycle, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();
        // np1 = 약 주기
        NumberPicker np1 = dialogView.findViewById(R.id.np1);
        np1.setMaxValue(100);
        np1.setMinValue(0);

        if (btn_medcycle.getText().toString() == "") { // 주기 현재 값 설정
            np1.setValue(2);
        } else { // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            int k2 = Integer.valueOf(medcycle);
            np1.setValue(k2);
        }
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // numberpicker 선택한 값 실시간으로 변경
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        //확인 버튼 눌렀을 때
        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medcycle = String.valueOf(np1.getValue()); // 선택한 주기 값 가져오기
                btn_medcycle.setText(medcycle + " 일");
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
    //약 시작 날짜 설정 다이얼로그
    public void dialog_start(View v){
        cal = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dp = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
                btn_start.setText(sdf.format(cal.getTime()));
            }
        };
        new DatePickerDialog(SickeditActivity.this, dp, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

}