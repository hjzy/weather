package com.animee.forecast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库库操作，参照Android开发->ch04.pdf
 */
public class DBManager {
    public static SQLiteDatabase database;
    /* 初始化数据库信息*/
    //获取一个可读写的数据库操作对象
    public static void initDB(Context context){
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }


    /* 查找数据库当中城市列表*/
    public static List<String>queryAllCityName(){
        //关于cursor参见https://blog.csdn.net/android_zyf/article/details/53420267
        //另见Android开发->ch04.pdf->Cursor的使用方式
        Cursor cursor = database.query("info", null, null, null, null, null,null);
        List<String>cityList = new ArrayList<>();
        //遍历查询到的所有城市，并将城市存入List中
        while (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex("city"));
            cityList.add(city);
        }
        return cityList;
    }


    /* 根据城市名称，替换信息内容*/
    public static int updateInfoByCity(String city,String content){
        ContentValues values = new ContentValues();
        values.put("content",content);
        /*
        //Andriod开发->ch04.pdf->使用update()方法更新数据
        SQLiteDatabase 类提供了 update() 方法用于更新数据。
        update() 方法原型如下：
        public int update ( tring table , ContentValues values ,String whereClause , String [ ] whereArgs )
        参数及返回值含义：
        table，更新操作的表名；
        values，键值对数据，key为列名，value为值；
        whereClause，where 子句；
        whereArgs，where 子句的? 占位符对应的参数；
        返回值，返回更新的行数。
        * */
        return database.update("info",values,"city=?",new String[]{city});
    }


    /* 新增一条城市记录*/
    public static long addCityInfo(String city,String content){
        ContentValues values = new ContentValues();
        values.put("city",city);
        values.put("content",content);
        /*
        Andriod开发->ch04.pdf->使用insert()方法插入数据
        public long insert (String table , String nullColumnHack , ContentValues values ) ;
        参数及返回值含义：
        table，插入数据的表名。
        nullColumnHack，可为 NULL 的列名。8
        values，键值对数据，key 为列名，value 为列的值。
        返回值，返回新插入的数据记录 ID 或 -1。

        * */
        return database.insert("info",null,values);
    }


    /* 根据城市名，查询数据库当中的内容*/
    public static String queryInfoByCity(String city){
    /*
    * Andriod开发->ch04.pdf->使用query方法查询数据
    public Cursor query ( String table , String [ ] columns , String selection , String [ ] selectionArgs ,
    String groupBy , String having , String orderBy ) ;
    参数及返回值含义：
    groupBy，group by 子句；
    having，having 子句；
    orderBy，order by 子句；
    返回值，返回 Cursor 对象，指向查询结果集。
    * */
        Cursor cursor = database.query("info", null, "city=?", new String[]{city}, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("content"));
            return content;
        }
        return null;
    }


    /* 存储城市天气要求最多存储5个城市的信息，一旦超过5个城市就不能存储了，获取目前已经存储的数量*/
    public static int getCityCount(){
        Cursor cursor = database.query("info", null, null, null, null, null, null);
        int count = cursor.getCount();
        return count;
    }


    /* 查询数据库当中的全部信息*/
    public static List<Database>queryAllInfo(){
        Cursor cursor = database.query("info", null, null, null, null, null, null);
        List<Database>list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            Database bean = new Database(id, city, content);
            list.add(bean);
        }
        return list;
    }


    /* 根据城市名称，删除这个城市在数据库当中的数据*/
    public static int deleteInfoByCity(String city){
/*        public int delete ( String table , String whereClause , String [ ] whereArgs ) ;
        参数及返回值含义：
        table，删除操作的表名；
        whereClause，where 子句；
        whereArgs，where 子句的? 占位符对应的参数；
        返回值，返回删除的行数。*/

        return database.delete("info","city=?",new String[]{city});
    }


    /* 删除表当中所有的数据信息*/
    public static void deleteAllInfo(){
        String sql = "delete from info";
        database.execSQL(sql);
    }
}
