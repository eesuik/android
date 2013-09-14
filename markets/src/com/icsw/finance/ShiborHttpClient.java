package com.icsw.finance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class ShiborHttpClient {
	private final static String SHIBOR_URL = "http://www.shibor.org/shibor/shiborDefDown.do";
	private HttpPost httpPost;
	
	public ShiborHttpClient() {
		httpPost = new HttpPost(SHIBOR_URL);
	}
	
	@SuppressWarnings("deprecation")
	public List<ShiborOvernight> getShiborOvernight(String month) {
		ArrayList<ShiborOvernight> list = new ArrayList<ShiborOvernight>();

        List <NameValuePair> params = new ArrayList <NameValuePair>(); 
        params.add(new BasicNameValuePair("fileName", "Historical_Shibor_Data_" + month + ".txt")); 
        params.add(new BasicNameValuePair("url", "")); 
        params.add(new BasicNameValuePair("shiborRadio", "txt"));
        params.add(new BasicNameValuePair("quoteRadio", "xls"));
        
    	try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        	DefaultHttpClient httpclient = new DefaultHttpClient();
        	HttpResponse response = httpclient.execute(httpPost); 
        	/*若状态码为200 ok*/
        	if(response.getStatusLine().getStatusCode() == 200)  
        	{ 
        		/*读返回数据*/
        		String[] strResult = EntityUtils.toString(response.getEntity()).split("\n|\r\n"); 
        		for(int i = 2; i < strResult.length; i++) {
        			String[] dayShibor = strResult[i].split(" +");
        			list.add(new ShiborOvernight(dayShibor[0], dayShibor[1]));
        		}
        		
        		//mTextView1.setText(strResult); 
        	}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return list;
	}
}
