package com.icsw.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
  
public class SelfListDBHelper extends SQLiteOpenHelper {  
  
    private static final String DATABASE_NAME = "self_list.db";  
    private static final int DATABASE_VERSION = 1;  
      
    public SelfListDBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //数据库第一次被创建时onCreate会被调用  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS stock" +  
                "(code INTEGER, name VARCHAR, price FLOAT, change FLOAT, rate FLOAT, date STRING, time STRING)"); 
        Log.i("DBHelper", "self_list.db created!");
    }  
  
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");  
    }  
}  

