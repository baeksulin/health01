package com.baewha.health01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button tap1, tap2, tap3, tap4, tap5;
    String dbid;

    Database db;
    SQLiteDatabase sqlDB;
    String name;
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();

        db = new Database(this);
        //로그인시 사용한 아이디 값 가져오기
        Intent i = getIntent();
        dbid = i.getStringExtra("아이디");
        selectData(dbid);

        tap();

    }
    // 아이디 연결시키는 메소드
    public void findId(){
        tv1 = findViewById(R.id.tv1);
    }
    public void selectData(String mId){
        sqlDB = db.getReadableDatabase();
        Cursor cursor= sqlDB.rawQuery("select mName from member where mId = ?;", new String[] {mId});
        while(cursor.moveToNext()){
            name = cursor.getString(0);
        }
        cursor.close();
        sqlDB.close();
        tv1.setText("안녕하세요"+name+"님");

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