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
import com.icsw.finance.StockList;

public class MainActivity extends Activity {
	private static ListView stockListView;
	private static StockList stocks;
	private static SimpleAdapter simpleAdapter;
	private final static Semaphore available = new Semaphore(1, true);
		
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
		stocks = new StockList(this);
		simpleAdapter = new MySimpleAdapter(MainActivity.this, stocks.stockHashMapList, R.layout.list_item,
				                          	new String[]{"id", "name", "price", "change", "rate"},
				                          	new int[]{R.id.item_id, R.id.item_name, R.id.item_price,
				                                      R.id.item_change, R.id.item_rate}
										 	); 
		Log.i("initListView", "adapter ok!");
		stockListView.setAdapter(simpleAdapter);
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
				
				stocks.update();
				
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
	
    	
    private class MySimpleAdapter extends SimpleAdapter {
           	
        public MySimpleAdapter(Context context, List<Hashtable<String, String>> items, int resource, String[] from, int[] to ) {
            super(context, items, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            
            int color;
            float rate = stocks.stockList.get(position).rate;
            if(rate > 0)
            	color = 0xffff0000; // Red
            else if(rate == 0)
            	color = 0xffffffff; // White
            else
            	color = 0xff00ff00; // Green
            ((TextView)v.findViewById(R.id.item_price)).setTextColor(color);
            ((TextView)v.findViewById(R.id.item_change)).setTextColor(color);
            ((TextView)v.findViewById(R.id.item_rate)).setTextColor(color);
        
            return v;
        }
    }
    
    public void onClickRefresh(View source) {
    	updateStockList();
    }
    
    private void addSomeStocksToDB() {
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
    }

    public void onClickAdd(View view) {
    	//stocks.add(new Stock(600000));
    	addSomeStocksToDB();
    	updateStockList();
    	Log.i("Main", "--------- add stocks ok!");
    }
    
    public void onClickDelete(View view) {
    	stocks.remove(0);
    	updateStockList();
    	Log.i("Main", "--------- delete stock ok!");
    }
    
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
    	//界面销毁之前保存数据  
    	super.onSaveInstanceState(outState);
    	
    	stocks.updateDB();
    }
    
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        
        //应用的最后一个Activity关闭时应释放DB  
        stocks.closeDB();  
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
