package com.baewha.health01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText ed_id, ed_pw;
    Button btn_join, btn_login;
    Database db;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("건강 지킴이");
        findId(); //아이디 연결
        db = new Database(this);
        btn();

    }
    //아이디 연결시키는 메소드
    public void findId(){
        ed_id = findViewById(R.id.ed_id);
        ed_pw = findViewById(R.id.ed_pw);
        btn_join = findViewById(R.id.btn_join);
        btn_login = findViewById(R.id.btn_login);
    }
    public void btn(){
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ed_id.getText().toString();
                String pw = ed_pw.getText().toString();
                boolean ok = login(id, pw); // false면 일치하는 사용자가 없다는 뜻
                if(id.equals("") || pw.equals("")){
                    Toast.makeText(LoginActivity.this, "아이디 혹은 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    if(ok==true){
                        Toast.makeText(LoginActivity.this, "환영합니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("아이디", id);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "아이디 혹은 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
    //로그인
    public boolean login(String mId, String mPw){
        sqlDB = db.getReadableDatabase();
        Cursor cursor= sqlDB.rawQuery("select * from member where mId = ? and mPw = ?;", new String[] {mId, mPw});
        if(cursor.getCount()>0) {
            return true;
        }
        else {
            return false;
        }
    }
}