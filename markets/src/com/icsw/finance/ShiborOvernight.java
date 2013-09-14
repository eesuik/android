package com.icsw.finance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShiborOvernight {
	public long year;
	public long month;
	public long day;
	public float rate;
	public boolean status;
	
	public ShiborOvernight(String date, String rate) {
		if(isDate(date) && isFloat(rate)) {
			String[] strDate = date.split("-|/");
			year = Long.parseLong(strDate[0]);
			month = Long.parseLong(strDate[1]);
			day = Long.parseLong(strDate[2]);
			this.rate = Float.parseFloat(rate);
			status = true;
		} else {
			year = 0;
			month = 0;
			day = 0;
			this.rate = 0;
			status = false;
		}
	}
	
	public static boolean isDate(String str) {
		Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher isDate = pattern.matcher(str);
		if (!isDate.matches()) {
			return false;
		} else {
			return true;
		}
	}
	
	// [-+]?[0-9]*\.?[0-9]*
	public static boolean isFloat(String str) {
		Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
		Matcher isDate = pattern.matcher(str);
		if (!isDate.matches()) {
			return false;
		} else {
			return true;
		}
	}
	
	public String getDate() {
		String d = String.format("%d-%02d-%02d", year, month, day);
		return d;
	}

	public String getRate() {
		String r = String.format("%f", rate);
		return r;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String shibor = String.format("Shibor Overnight: %4d-%02d-%02d %f\n", year, month, day, rate);
		
		return shibor.toString();
	}
}
