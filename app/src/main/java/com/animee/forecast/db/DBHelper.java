package com.animee.forecast.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 用于创建数据库，参见https://www.jb51.net/article/122111.htm
 */

public class DBHelper extends SQLiteOpenHelper{
//参见安卓实验开发ppt->ch04.pdf->创建SqliteOpenHelper子类
    public DBHelper(Context context){
        super(context,"forecast.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        创建表的操作
        String sql = "create table info(" +
                "_id integer primary key autoincrement," +
                "city varchar(20) unique not null," +
                "content text not null)";
        //执行该语句
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
