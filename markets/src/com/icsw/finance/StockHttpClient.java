package com.icsw.finance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.icsw.finance.Stock.ParseSinaStockException;

public class StockHttpClient {
	final private String SINAJS_URL = "http://hq.sinajs.cn/list=";
	//private HttpPost httpPost;
	
	//public StockHttpClient() {
	//}
	public List<Stock> getStock(long[] stockCodes) {
		ArrayList<Stock> list = new ArrayList<Stock>();
		String url = SINAJS_URL + generateStockCodeRequest(stockCodes);

    	try {
    		HttpPost httpPost = new HttpPost(url);
    		DefaultHttpClient httpclient = new DefaultHttpClient();
    		HttpResponse response = httpclient.execute(httpPost); 
    		/*��״̬��Ϊ200 ok*/
    		if(response.getStatusLine().getStatusCode() == 200)  
    		{ 
    			/*����������*/
    			String strResult = EntityUtils.toString(response.getEntity()); 
    			String[] strSplited = strResult.split("\n|\r\n");
    			//System.out.print(strSplited[0]);
    			for(int i = 0; i < strSplited.length; i++) {
    				Stock stock = Stock.parseSinaStock(strSplited[i]);
    				if(stock != null) {
    					list.add(Stock.parseSinaStock(strSplited[i]));
    				}
    			}
    		}
    	}   catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseSinaStockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return list;
	}
	
	private String generateStockCodeRequest(long[] stockCodes){
		
		if(stockCodes == null || stockCodes.length == 0) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
	
		final int length = stockCodes.length;
		for(int i = 0; i < length; ++i) {
			if(stockCodes[i] >= 600000 || stockCodes[i] < 301)
				sb.append(String.format("sh%06d", stockCodes[i]));
			else
				sb.append(String.format("sz%06d", stockCodes[i]));
			if(i != length - 1)
				sb.append(',');
		}
		
		return sb.toString();
	}
}
