package com.icsw.markets;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.icsw.db.SelfListDBManager;
import com.icsw.finance.Stock;
import com.icsw.finance.StockHttpClient;

public class MainActivity extends Activity {
	private static int LIST_LENGTH = 0;
	private static ListView stockListView;
	private static SimpleAdapter simpleAdapter;
	private static List<Hashtable<String, String>> stockHashMapList;
	private final static Semaphore available = new Semaphore(1, true);
	
	private static SelfListDBManager selfListDB;
	
	//private static View loginLoading;
	//private static AnimationDrawable loadingAnimation;
	
    private static Dialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stockListView = (ListView)findViewById(R.id.list_stocks);
				
		loadingDialog = new AlertDialog.Builder(this).create();
		loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		
		initListView();
		
		updateStockList();

	}

	private void initListView() {
		selfListDB = new SelfListDBManager(this);
		if(selfListDB.length() == 0) {
			addSomeStocksToDB();
		}
		
		LIST_LENGTH = selfListDB.stockList.size();
		if(LIST_LENGTH > 20) LIST_LENGTH = 20;
		
		stockHashMapList = new ArrayList<Hashtable<String, String>>();

		Hashtable<String, String> table1 = new Hashtable<String, String>();
		table1.put("id", "");
		table1.put("name", "股票名称");
		table1.put("price", "最新价");
		table1.put("change", "涨跌额");
		table1.put("rate", "涨跌幅");
		stockHashMapList.add(table1);
		for (int i = 0; i < LIST_LENGTH; i++) {
			Hashtable<String, String> table = new Hashtable<String, String>();
			table.put("id", "");
			table.put("name", selfListDB.stockList.get(i).name);
			table.put("price", "");
			table.put("change", "");
			table.put("rate", "");
			stockHashMapList.add(table);
		} 
		simpleAdapter = new MySimpleAdapter(MainActivity.this, stockHashMapList, R.layout.list_item,
				                          	new String[]{"id", "name", "price", "change", "rate"},
				                          	new int[]{R.id.item_id, R.id.item_name, R.id.item_price,
				                                      R.id.item_change, R.id.item_rate}
										 	); 
		//simpleAdapter.getv
		Log.i("initListView", "adapter ok!");
		stockListView.setAdapter(simpleAdapter);
		//simpleAdapter.
	}

	private static void updateStockList() {
		new Thread(){
			@Override
			public void run(){
				//你要执行的方法
				try {
					available.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				handlerLoadBegin.sendEmptyMessage(0);
				
				getStocks();
				//执行完毕后给handler发送一个空消息
				
				handlerLoadOver.sendEmptyMessage(0);
				
				available.release();
			}
		}.start();
	}
	
	//定义Handler对象
	private static Handler handlerLoadOver =new Handler(){
		@Override
		//当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			//处理UI
			//loadingAnimation.stop();
			//loginLoading.setVisibility(View.GONE);
			
			loadingDialog.dismiss();
			
			simpleAdapter.notifyDataSetChanged();
		}
	};

	//定义Handler对象
	private static Handler handlerLoadBegin =new Handler(){
		@Override
		//当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			//处理UI

	    	loadingDialog.show();
	        // 注意此处要放在show之后 否则会报异常
	    	loadingDialog.setContentView(R.layout.loading_process_dialog_anim);
		}
	};
	
    public static void getStocks() {
    	List<Stock> list = new ArrayList<Stock>(LIST_LENGTH);
    	long[] stockCode = new long[LIST_LENGTH];
    	
    	for(int i = 0; i < LIST_LENGTH; i++) {
    		stockCode[i] = selfListDB.stockList.get(i).code;
    	}
    	
    	StockHttpClient stockClient = new StockHttpClient();
    	list = stockClient.getStock(stockCode);
    	
    	for(int i = 0; i < LIST_LENGTH; i++) {
    		for(int j = 0; j < list.size(); j++) {
    			if(selfListDB.stockList.get(i).code == list.get(j).code) {
    				selfListDB.stockList.get(i).copy(list.get(j));
    				list.remove(j);
    				Hashtable<String, String> table = new Hashtable<String, String>();
    				table.put("id", String.format("%d", i + 1));
					table.put("name", selfListDB.stockList.get(i).name);
					table.put("price", String.format("%.2f", selfListDB.stockList.get(i).price));
					table.put("change", String.format("%.2f", selfListDB.stockList.get(i).change));
					table.put("rate", String.format("%.2f%%", selfListDB.stockList.get(i).rate));
					stockHashMapList.set(i + 1, table);
					break;
    			}
    		}
    	}
    }
    	
    private class MySimpleAdapter extends SimpleAdapter {
           	
        public MySimpleAdapter(Context context, List<Hashtable<String, String>> items, int resource, String[] from, int[] to ) {
            super(context, items, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            
            if(position == 0) {
            	((TextView)v.findViewById(R.id.item_name)).setTextColor(0xffffffff);
            	((TextView)v.findViewById(R.id.item_price)).setTextColor(0xffffffff);
            	((TextView)v.findViewById(R.id.item_change)).setTextColor(0xffffffff);
            	((TextView)v.findViewById(R.id.item_rate)).setTextColor(0xffffffff);
            }
            else {
            	int color;
            	float rate = selfListDB.stockList.get(position - 1).rate;
            	if(rate > 0)
            		color = 0xffff0000; // Red
            	else if(rate == 0)
            		color = 0xffffffff; // White
            	else
            		color = 0xff00ff00; // Green
            	((TextView)v.findViewById(R.id.item_price)).setTextColor(color);
            	((TextView)v.findViewById(R.id.item_change)).setTextColor(color);
            	((TextView)v.findViewById(R.id.item_rate)).setTextColor(color);
            }
            
            return v;
        }
    }
    
    public void onClickRefresh(View source) {
    	//if(loginLoading.getVisibility() == View.GONE){
    	//	loginLoading.setVisibility(View.VISIBLE);
    	//	loadingAnimation.start();
    	//}
    	
    	updateStockList();
    }
    
    private void addSomeStocksToDB() {
    	List<Stock> stocks = new ArrayList<Stock>();

    	stocks.add(new Stock(1));         // 上证指数
    	stocks.add(new Stock(399001));    // 深成指
    	stocks.add(new Stock(600585));    // 
    	stocks.add(new Stock(600123));
    	stocks.add(new Stock(600971));
		
    	stocks.add(new Stock(600108));
    	stocks.add(new Stock(2041));
    	stocks.add(new Stock(600354));
    	stocks.add(new Stock(600030));    // 中信证券
    	stocks.add(new Stock(600267));

    	stocks.add(new Stock(600547));    // 
    	stocks.add(new Stock(600531));    // 豫光金铅
    	stocks.add(new Stock(2414));      // 高德红外
    	stocks.add(new Stock(600850));    // 华东电脑
    	
    	selfListDB.add(stocks);
    }

    public void onClickAdd(View view) {
    	List<Stock> stocks = new ArrayList<Stock>();
    	stocks.add(new Stock(600181));
    	stocks.add(new Stock(600281));
    	selfListDB.add(stocks);
    	selfListDB.updateDB();
    	Log.i("Main", "--------- add stocks ok!");
    }
    
    public void onClickDelete(View view) {
    	selfListDB.deleteStock(new Stock(600181));
    	Log.i("Main", "--------- delete stock ok!");
    }
    
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
    	//界面销毁之前保存数据  
    	super.onSaveInstanceState(outState);
    	
    	selfListDB.updateDB();
    }
    
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        
        //应用的最后一个Activity关闭时应释放DB  
        selfListDB.closeDB();  
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
