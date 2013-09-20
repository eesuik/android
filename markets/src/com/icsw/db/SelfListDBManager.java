package com.icsw.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.icsw.finance.Stock;


public class SelfListDBManager {
	private SelfListDBHelper helper;
    private SQLiteDatabase db;
    
    public SelfListDBManager(Context context) {  
        helper = new SelfListDBHelper(context);  
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);  
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里  
        db = helper.getWritableDatabase();
    }

    /** 
     * add stocks 
     * @param stocks 
     */
    public void add(List<Stock> stocks) {  
        db.beginTransaction();  //开始事务  
        try {
            for (Stock stock : stocks) {  
            	db.execSQL("INSERT INTO stock VALUES(?, ?, ?, ?, ?, ?, ?)",
        				   new Object[]{stock.code, stock.name, stock.price,
        							    stock.change, stock.rate, stock.date, stock.time}); 
            	db.setTransactionSuccessful();  //设置事务成功完成  
            }
        } finally {  
            db.endTransaction();    //结束事务  
        }  
    }
    
    /** 
     * update stock's information 
     * @param stock 
     */
    public void update(Stock stock) {
        ContentValues cv = new ContentValues();  
        cv.put("price", stock.price);
        cv.put("change", stock.change);
        cv.put("rate", stock.rate);
        cv.put("date", stock.date);
        cv.put("time", stock.time);
        db.update("stock", cv, "code = ?", new String[]{String.valueOf(stock.code)});  
    }

    /** 
     * delete a stock 
     * @param stock 
     */
    public void deleteStock(Stock stock) {
        db.delete("stock", "code = ?", new String[]{String.valueOf(stock.code)});  
    }
    
    /** 
     * query all stocks, return list 
     * @return List<Stock> 
     */  
    public List<Stock> query() {
        ArrayList<Stock> stocks = new ArrayList<Stock>();  
        Cursor c = db.rawQuery("SELECT * FROM stock", null);
        while (c.moveToNext()) {  
        	Stock stock = new Stock();  
        	stock.code = c.getInt(c.getColumnIndex("code"));  
        	stock.name = c.getString(c.getColumnIndex("name"));  
        	stock.price = c.getFloat(c.getColumnIndex("price"));  
        	stock.change = c.getFloat(c.getColumnIndex("change"));  
        	stock.rate = c.getFloat(c.getColumnIndex("rate"));  
        	stock.date = c.getString(c.getColumnIndex("date"));  
        	stock.time = c.getString(c.getColumnIndex("time"));
        	stock.setExist();;
        	stocks.add(stock);
        }  
        c.close();
        return stocks;
    }
    
    public int length() {    	
    	Cursor c = db.rawQuery("SELECT * FROM stock", null);
    	int len = c.getCount();
    	return len;
    }
    
    /** 
     * query all persons, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM stock", null);  
        return c;  
    }  
      
    /** 
     * close database 
     */
    public void closeDB() {  
    	if(db != null) {
    		db.close();  
    	}
    } 
}
