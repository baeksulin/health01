package com.baewha.health01;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DB_NAME = "commute.db";
    private static final int DB_VER = 1;

    public Database(Context context){
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String member = "CREATE TABLE IF NOT EXISTS member ("+
                "mId   TEXT NOT NULL PRIMARY KEY, "+
                "mPw   TEXT NOT NULL,"+
                "mName   TEXT NOT NULL,"+
                "mGenger   TEXT NOT NULL,"+
                "mAge   TEXT NOT NULL,"+
                "mEmail   TEXT )";

        String weight = "CREATE TABLE IF NOT EXISTS weight ("+
                "wNum   INTEGER NOT NULL PRIMARY KEY autoincrement, "+
                "wDate   TEXT NOT NULL, "+
                "wId   TEXT NOT NULL, "+
                "wHeight   TEXT, "+
                "wGweight   TEXT, "+
                "wNweight   TEXT, "+
                "FOREIGN KEY(wId) REFERENCES member(mId))";

        String sick = "CREATE TABLE IF NOT EXISTS sick ("+
                "sId   TEXT NOT NULL PRIMARY KEY, "+
                "sHospital   TEXT NOT NULL, "+
                "sTell TEXT, "+
                "sSickname   TEXT NOT NULL, "+
                "sMedcycle   TEXT NOT NULL, "+
                "sStart   TEXT NOT NULL, "+
                "sCheck   TEXT, "+
                "FOREIGN KEY(sId) REFERENCES member(mId))";

        String schedule = "CREATE TABLE IF NOT EXISTS schedule ("+
                "scNum   INTEGER NOT NULL PRIMARY KEY autoincrement, "+
                "scDate   TEXT NOT NULL, "+
                "scTime   TEXT NOT NULL, "+
                "scId   TEXT, "+
                "scTxt   TEXT, "+
                "FOREIGN KEY(scId) REFERENCES member(mId))";

        db.execSQL(member);
        db.execSQL(weight);
        db.execSQL(sick);
        db.execSQL(schedule);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
