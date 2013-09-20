package com.icsw.finance;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.icsw.db.SelfListDBManager;

public class StockList {
	private SelfListDBManager dbm;
	public List<Stock> stockList;
	public List<Hashtable<String, String>> stockHashMapList;
	
	public StockList(Context context) {
		dbm = new SelfListDBManager(context);
		stockList = dbm.query();
		
		stockHashMapList = new ArrayList<Hashtable<String, String>>();
		for(int i = 0; i < stockList.size(); i++) {
			Hashtable<String, String> table = new Hashtable<String, String>();
			table.put("id", String.format("%2d", i + 1));
			table.put("name", stockList.get(i).name);
			table.put("price", "");
			table.put("change", "");
			table.put("rate", "");
			stockHashMapList.add(table);
			//Log.i("StockList", stockList.get(i).toString());
		}
	}

    /** 
     * Update the stocks from sina server.
     * @param none
     */
	public void update() {
    	List<Stock> list = new ArrayList<Stock>();
    	int[] stockCode = new int[stockList.size()];
    	for(int i = 0; i < stockList.size(); i++) {
    		stockCode[i] = stockList.get(i).code;
    	}
    	StockHttpClient stockClient = new StockHttpClient();
    	list = stockClient.getStock(stockCode);
    	
    	for(int i = 0; i < stockList.size(); i++) {
    		for(int j = 0; j < list.size(); j++) {
    			if(stockList.get(i).code == list.get(j).code) {
    				stockList.get(i).copy(list.get(j));
    				list.remove(j);
    				Hashtable<String, String> table = new Hashtable<String, String>();
    				table.put("id", String.format("%d", i + 1));
					table.put("name", stockList.get(i).name);
					table.put("price", String.format("%.2f", stockList.get(i).price));
					table.put("change", String.format("%.2f", stockList.get(i).change));
					table.put("rate", String.format("%.2f%%", stockList.get(i).rate));
					stockHashMapList.set(i, table);
					break;
    			}
    		}
    	}
	}

    /** 
     * Add a stock to the list.
     * @param stock
     */
	public void add(Stock stock) {
		for(int i = 0; i < stockList.size(); i++) {
			if(stock.code == stockList.get(i).code) {
				// this stock has already in the list, skip it
				return;
			}
		}
		// add it to the stock list
		stockList.add(stock);
		
		// add it to the hash map list
		Hashtable<String, String> table = new Hashtable<String, String>();
		table.put("id", String.format("%2d", stockList.size() + 1));
		table.put("name", stock.name);
		table.put("price", "");
		table.put("change", "");
		table.put("rate", "");
		stockHashMapList.add(table);
		
		// add it to the database
		List<Stock> list = new ArrayList<Stock>();
		list.add(stock);
		dbm.add(list);
	}

    /** 
     * Delete a stock from the list.
     * @param stock
     */
	public void remove(int location) {
		if(location >= stockList.size()) {
			return;
		}
		Stock stock = stockList.get(location);
		stockHashMapList.remove(location);
		dbm.deleteStock(stock);
		stockList.remove(location);
	}
	
	public void updateDB() {
		for(Stock stock : stockList) {
			dbm.update(stock);
		}
	}
	
    /** 
     * Close the database
     * @param none
     */
	public void closeDB() {
		dbm.closeDB();
	}
}
