package com.icsw.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class StockListDBManager {
	private final int BUFFER_SIZE = 1024;
	private SQLiteDatabase database;
	private Context context;


	public StockListDBManager(Context context) {
		this.context = context;
        File sdFile = Environment.getExternalStorageDirectory();
        File stockPath = new File(sdFile.getPath()+"/SimpleStockList/stocks.db");

        if (!stockPath.exists()) {
            try {
            	//´´½¨Ä¿Â¼www.2cto.com
            	File pmsPath = new File(sdFile.getPath()+"/SimpleStockList");
            	//Log.i("pmsPaht", "pmsPaht: "+pmsPaht.getPath());
            	pmsPath.mkdirs();
           
            	AssetManager am = this.context.getAssets();
            	InputStream is = am.open("stocks.mp3");

            	FileOutputStream fos = new FileOutputStream(stockPath);
 
            	byte[] buffer = new byte[BUFFER_SIZE];
            	int count = 0;
            	while ((count = is.read(buffer)) > 0) {
                   fos.write(buffer, 0, count);
            	}
            	fos.flush();

            	fos.close();
            	is.close();
            	//am.close();
            } catch (IOException e) { 
            	e.printStackTrace();
            	}
        }

        database = SQLiteDatabase.openOrCreateDatabase(stockPath, null);
	}


	public void close() {
		if (database != null) {
			this.database.close();
		}
	}
}
