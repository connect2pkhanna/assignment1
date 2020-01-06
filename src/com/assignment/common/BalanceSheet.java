package com.assignment.common;

import java.util.concurrent.ConcurrentHashMap;

public class BalanceSheet {

	private static  ConcurrentHashMap<Record, Double> concurrentMap=new ConcurrentHashMap();
	private String id;
	
	public ConcurrentHashMap<Record, Double> getConcrrentSheet() {
		return concurrentMap;
	}
	public void addToSheet(Record record) {
		concurrentMap.put(record, concurrentMap.getOrDefault(record, (double)0.0)+record.getAmount().doubleValue());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
