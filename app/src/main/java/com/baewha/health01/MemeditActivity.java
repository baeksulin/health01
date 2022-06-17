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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MemeditActivity extends AppCompatActivity {
    TextView tv_id;
    EditText ed_nowpw, ed_newpw, ed_newpwto, ed_name, ed_email;
    Button btn_genger, btn_age, btn_cancel, btn_save, tap1, tap2, tap3, tap4;
    Database db;
    SQLiteDatabase sqlDB;
    String id, pw, nowpw,newpw, newpwto, name, genger, age, email, dbid; // pw는 데베에서 불러오는 비밀번호, nowpw는 EditText에 입력하는 현재 비빌번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memedit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // EditText에 입력할 때 layout은 그대로고 키보드만 생기게 하기
        setTitle("건강 지킴이 - 회원 정보 수정");
        findId(); // 아이디 찾는 메소드
        db = new Database(this);
        Intent i = getIntent();
        dbid = i.getStringExtra("아이디"); //로그인한 아이디 데이터 받기
        selectData(dbid);
        btn();
    }
    // 아이디 연결시키는 메소드
    public void findId(){
        tv_id = findViewById(R.id.tv_id);
        ed_nowpw = findViewById(R.id.ed_nowpw);
        ed_newpw = findViewById(R.id.ed_newpw);
        ed_newpwto = findViewById(R.id.ed_newpwto);
        ed_name = findViewById(R.id.ed_name);
        ed_email = findViewById(R.id.ed_email);
        btn_genger = findViewById(R.id.btn_genger);
        btn_age = findViewById(R.id.btn_age);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);
        tap1 = findViewById(R.id.tap1);
        tap2 = findViewById(R.id.tap2);
        tap3 = findViewById(R.id.tap3);
        tap4 = findViewById(R.id.tap4);
    }
    //버튼 메소드
    public void btn(){
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
        tap4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CalActivity.class);
                intent.putExtra("아이디", dbid);
                startActivity(intent);
            }
        });
    }
    public void selectData(String mid){
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select * from member where mid = ? ;",new String[] {mid});
        while(cursor.moveToNext()){
            id = cursor.getString(0);
            pw = cursor.getString(1);
            name = cursor.getString(2);
            genger = cursor.getString(3);
            age = cursor.getString(4);
            email = cursor.getString(5);
        }
        cursor.close();
        sqlDB.close();

        tv_id.setText(id);
        ed_name.setText(name);
        btn_genger.setText(genger);
        btn_age.setText(age);
        ed_email.setText(email);
    }
    public void save(){
        nowpw = ed_nowpw.getText().toString();
        newpw = ed_newpw.getText().toString();
        newpwto = ed_newpwto.getText().toString();
        name = ed_name.getText().toString();
        //성별과 나이는 다이얼로그에서 저장한 값 사용
        email = ed_email.getText().toString();

        if(nowpw.equals("") || newpw.equals("") || newpwto.equals("") || name.equals("")){
            if(nowpw.equals("")){
                Toast.makeText(MemeditActivity.this, "현재 사용중인 비밀번호를 입력 해주세요", Toast.LENGTH_SHORT).show();
            }else if(newpw.equals("")){
                Toast.makeText(MemeditActivity.this, "변경할 비밀번호를 입력 해주세요", Toast.LENGTH_SHORT).show();
            }else if(newpwto.equals("")){
                Toast.makeText(MemeditActivity.this, "변경할 비밀번호를 다시 입력 해주세요", Toast.LENGTH_SHORT).show();
            }else if(name.equals("")){
                Toast.makeText(MemeditActivity.this, "이름을 입력 해주세요", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(pw.equals(nowpw)){
                if(newpw.equals(newpwto)){
                    insertData(newpw, name, genger, age, email);
                    Toast.makeText(MemeditActivity.this, "수정이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(MemeditActivity.this, "변경할 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MemeditActivity.this, "현재 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void insertData(String newpw, String name, String genger, String age, String email){
        sqlDB = db.getWritableDatabase();
        sqlDB.execSQL("update member set mPw ='"+newpw+"', mName = '"+name+"', mGenger = '"+genger+"', mAge = '"+age+"', mEmail = '"+email+"' where mId = '"+dbid+"';");
        sqlDB.close();
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

        // 선택한 값이 있으면 그 값으로 현재 값 보여주기
        if(rb_man.getText().toString().equals(genger)){
            rb_man.setChecked(true);
        }else if(rb_woman.getText().toString().equals(genger)){
            rb_woman.setChecked(true);
        } else{
            rb_other.setChecked(true);
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

        // 선택한 값이 있으면 그 값으로 현재 값 보여주기
        int a = Integer.valueOf(age);
        np1.setValue(a);

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
}