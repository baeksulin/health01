package com.baewha.health01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class JoinActivity extends AppCompatActivity {
    EditText ed_id, ed_pw, ed_pwto, ed_name, ed_email;
    Button btn_id, btn_genger, btn_age, btn_join, btn_cancel;
    Database db;
    SQLiteDatabase sqlDB;
    String genger, age; //genger = 라디오버튼에서 선택한 성별 , age = numberpicker 선택한 나이
    String id, pw, pwto, name, email; // EditText 아이디, 비밀번호, 재확인, 이름, 이메일에 입력한 텍스트 가져온 값들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        findId();

        db = new Database(this);

        btn();

    }
    // 아이디 연결시키는 메소드
    public void findId(){
        ed_id = findViewById(R.id.ed_id);
        ed_pw = findViewById(R.id.ed_pw);
        ed_pwto = findViewById(R.id.ed_pwto);
        ed_name = findViewById(R.id.ed_name);
        ed_email = findViewById(R.id.ed_email);
        btn_id = findViewById(R.id.btn_id);
        btn_genger = findViewById(R.id.btn_genger);
        btn_age = findViewById(R.id.btn_age);
        btn_join = findViewById(R.id.btn_join);
        btn_cancel = findViewById(R.id.btn_cancel);
    }
    public void btn(){
        btn_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = ed_id.getText().toString();
                boolean idcheck = checkId(id);
                if(id.equals("")){
                    Toast.makeText(JoinActivity.this, "아이디를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else{
                    if(idcheck == false) { // 아이디 중복 확인 false면 중복하는 아이디가 없음
                        btn_id.setText("완료");
                        Toast.makeText(JoinActivity.this, "사용가능한 아이디 입니다", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(JoinActivity.this, "중복된 아이디 입니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_genger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_genger(v);
            }
        });
        btn_age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_age(v);
            }
        });
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join();
            }
        });
    }
    //성별 설정 다이얼로그
    public void dialog_genger(View v) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_genger, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();

        RadioGroup rg = dialogView.findViewById(R.id.rg);
        RadioButton rb_man = dialogView.findViewById(R.id.rb_man);
        RadioButton rb_woman = dialogView.findViewById(R.id.rb_woman);
        RadioButton rb_other = dialogView.findViewById(R.id.rb_other);

        if (btn_genger.getText().toString() == "") { // 성별 현재 값 설정
            rb_man.setChecked(true);
        } else { // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            if(rb_man.getText().toString().equals(genger)){
                rb_man.setChecked(true);
            }else if(rb_woman.getText().toString().equals(genger)){
                rb_woman.setChecked(true);
            } else{
                rb_other.setChecked(true);
            }
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // 라디오버튼에서 선택한 값 실시간으로 변경
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_man){
                    genger = rb_man.getText().toString();
                } else if(checkedId == R.id.rb_woman){
                    genger = rb_woman.getText().toString();
                } else{
                    genger = rb_other.getText().toString();
                }
            }
        });

        //확인 버튼 눌렀을 때
        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_genger.setText(genger);
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
    //나이 설정 다이얼로그
    public void dialog_age(View v) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_age, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();
        // np1 = 나이
        NumberPicker np1 = dialogView.findViewById(R.id.np1);
        np1.setMaxValue(150);
        np1.setMinValue(1);

        if (btn_age.getText().toString() == "") { // 나이 현재 값 설정
            np1.setValue(20);
        } else { // 선택한 값이 있으면 그 값으로 현재 값 보여주기
            int a = Integer.valueOf(age);
            np1.setValue(a);
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
                age = String.valueOf(np1.getValue()); // 선택한 나이 값 가져오기
                btn_age.setText(age + " 세");
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
    //회원가입
    public void join(){
        id = ed_id.getText().toString();
        pw = ed_pw.getText().toString();
        pwto = ed_pwto.getText().toString();
        name = ed_name.getText().toString();
        //성별 getText한 값은 genger, 나이는 age 다이얼로그에서 저장한 값 사용
        email = ed_email.getText().toString();

        if (id.equals("") || pw.equals("") || pwto.equals("") || name.equals("") || genger == null || age == null){
            if(id.equals("")){
                Toast.makeText(JoinActivity.this,"아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            }else if (pw.equals("") || pwto.equals("")){
                Toast.makeText(JoinActivity.this,"비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (name.equals("")){
                Toast.makeText(JoinActivity.this,"이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (genger == null){
                Toast.makeText(JoinActivity.this,"성별을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else if (age == null){
                Toast.makeText(JoinActivity.this,"나이를 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        }else {
            if(btn_id.getText().toString()=="완료"){
                if(pw.equals(pwto)){
                    insetData(id, pw, name, genger, age, email);
                    Toast.makeText(JoinActivity.this, "회원가입 되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(JoinActivity.this, "아이디 중복 확인을 해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //아이디 중복 확인
    public boolean checkId(String mid){
        sqlDB = db.getReadableDatabase();
        Cursor cursor= sqlDB.rawQuery("select * from member where mid = ?;", new String[] {mid});
        if(cursor.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }
    //데이터베이스에 데이터 저장
    public void insetData(String id, String pw, String name, String genger, String age, String email){
        sqlDB = db.getWritableDatabase();
        sqlDB.execSQL("insert into member(mId, mPw, mName, mGenger, mAge, mEmail) values "+"('"+id+"','"+pw+"','"+name+"','"+genger+"','"+age+"','"+email+"');");
        sqlDB.close();
    }
}