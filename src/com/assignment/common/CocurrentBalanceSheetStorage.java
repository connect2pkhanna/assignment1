package com.assignment.common;

import java.util.concurrent.ConcurrentHashMap;

public class CocurrentBalanceSheetStorage {

	private static ConcurrentHashMap<String, BalanceSheet> sheetsbeingProcessed=new ConcurrentHashMap<String, BalanceSheet>();

	
	public static BalanceSheet getSheetsbeingProcessedBydId(String id) {
		// as get  @throws NullPointerException if the specified key is null,Please see doc of the api
		if(!sheetsbeingProcessed.containsKey(id)) 
		{
			return null;
		}
		return sheetsbeingProcessed.get(id);
	}

	
	public static void initBalanceSheet(String id) {
		sheetsbeingProcessed.putIfAbsent(id, new BalanceSheet());
	}
	
	
}
