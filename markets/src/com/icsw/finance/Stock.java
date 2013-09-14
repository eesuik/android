package com.icsw.finance;

public class Stock {
	public final static int NEW_STOCK = 0;
	public final static int EXIST_IN_DB = 1;
	public final static int TO_BE_DELETED = 2;
	
	public int code;
	public String name;
	public float price;
	public float change;
	public float rate;
	public String date;
	public String time;
	public static int status;
	
	private Stock(int code, String name, float price,
				  float change, float rate, String date, String time) {
		this.code = code;
		this.name = name;
		this.price = price;
		this.change = change;
		this.rate = rate;
		this.date = date;
		this.time = time;
		setNew();
	}
	
	public Stock(int code) {
		this.code = code;
		this.name = "";
		this.price = 0;
		this.change = 0;
		this.rate = 0;
		this.date = "";
		this.time = "";
		setNew();
	}
	
	public Stock() {
		// TODO Auto-generated constructor stub
		this.code = 0;
		this.name = "";
		this.price = 0;
		this.change = 0;
		this.rate = 0;
		this.date = "";
		this.time = "";
		setNew();
	}

	public void copy(Stock stock) {
		this.code = stock.code;
		this.name = stock.name;
		this.price = stock.price;
		this.change = stock.change;
		this.rate = stock.rate;
		this.date = stock.date;
		this.time = stock.time;
	}
	
	public static Stock parseSinaStock(String source) throws ParseSinaStockException {
		int start = source.indexOf('\"');
		String targetString = source.substring(start+1, source.length()-2);

		if(targetString.length() == 0) {
			return null;
		}
		String[] stockInfo = targetString.split(",");
		if(stockInfo.length != 33) {
			throw new ParseSinaStockException();
		}

		final int stockCode = Integer.parseInt(source.substring(start - 7, start - 1));
		final String name = stockInfo[0];
		final float yestodayPrice = Float.parseFloat(stockInfo[2]);
		final float nowPrice = Float.parseFloat(stockInfo[3]);
		final String date = stockInfo[30];
		final String time = stockInfo[31];
		
		final float change = nowPrice - yestodayPrice;
		final float rate = change / yestodayPrice * 100;
		
		Stock stock = new Stock(stockCode, name, nowPrice, change, rate, date, time);
		return stock;
	}
	
	public void setNew() {
		status = NEW_STOCK;
	}

	public void setExist() {
		status = EXIST_IN_DB;
	}
	
	public void setDeleted() {
		status = TO_BE_DELETED;
	}
	
	public boolean isNew() {
		if(status == NEW_STOCK)
			return true;
		else
			return false;
	}

	public boolean isInDB() {
		if(status == EXIST_IN_DB)
			return true;
		else
			return false;
	}
	
	public boolean isDeleted() {
		if(status == TO_BE_DELETED)
			return true;
		else
			return false;
	}
	
	@SuppressWarnings("serial")
	public static class ParseSinaStockException extends Exception{
		public ParseSinaStockException(){
			super("Parse sina stock information error!");
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("  股票代码： " + code + "\n");
		sb.append("  股票名称： " + name + "\n");
		sb.append("  当前股价： " + price + "元\n");
		sb.append("    涨跌额： " + change + "元\n");
		sb.append("    涨跌幅： " + rate + "元\n");
		sb.append("      日期： " + date + "\n");
		sb.append("      时间： " + time + "\n");
		
		return sb.toString();
	}
}
