package com.icsw.finance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.icsw.finance.Stock.ParseSinaStockException;

public class StockHttpClient {
	final private String SINAJS_URL = "http://hq.sinajs.cn/list=";
	//private HttpPost httpPost;
	
	//public StockHttpClient() {
	//}
	public List<Stock> getStock(int[] stockCode) {
		ArrayList<Stock> list = new ArrayList<Stock>();
		String url = SINAJS_URL + generateStockCodeRequest(stockCode);

    	try {
    		HttpPost httpPost = new HttpPost(url);
    		DefaultHttpClient httpclient = new DefaultHttpClient();
    		HttpResponse response = httpclient.execute(httpPost); 
    		/*若状态码为200 ok*/
    		if(response.getStatusLine().getStatusCode() == 200)  
    		{ 
    			/*读返回数据*/
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
	
	private String generateStockCodeRequest(int[] stockCode){
		
		if(stockCode == null || stockCode.length == 0) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
	
		final int length = stockCode.length;
		for(int i = 0; i < length; ++i) {
			if(stockCode[i] >= 600000 || stockCode[i] < 301)
				sb.append(String.format("sh%06d", stockCode[i]));
			else
				sb.append(String.format("sz%06d", stockCode[i]));
			if(i != length - 1)
				sb.append(',');
		}
		
		return sb.toString();
	}
}
