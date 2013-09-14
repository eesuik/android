package com.icsw.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
  
public class SelfListDBHelper extends SQLiteOpenHelper {  
  
    private static final String DATABASE_NAME = "self_list.db";  
    private static final int DATABASE_VERSION = 1;  
      
    public SelfListDBHelper(Context context) {  
        //CursorFactory����Ϊnull,ʹ��Ĭ��ֵ  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    //���ݿ��һ�α�����ʱonCreate�ᱻ����  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS stock" +  
                "(code INTEGER, name VARCHAR, price FLOAT, change FLOAT, rate FLOAT, date STRING, time STRING)"); 
        Log.i("DBHelper", "self_list.db created!");
    }  
  
    //���DATABASE_VERSIONֵ����Ϊ2,ϵͳ�����������ݿ�汾��ͬ,�������onUpgrade  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");  
    }  
}  

