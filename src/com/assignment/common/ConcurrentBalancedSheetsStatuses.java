package com.assignment.common;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentBalancedSheetsStatuses {

	private static ConcurrentHashMap<String, Boolean> loadAndTransformationStatues=new ConcurrentHashMap<String, Boolean>();

	public static ConcurrentHashMap<String, Boolean> getLoadAndTransformationStatues() {
		return loadAndTransformationStatues;
	}


	

	public static void setLoadAndTransformationStatues(ConcurrentHashMap<String, Boolean> loadAndTransformationStatues) {
		ConcurrentBalancedSheetsStatuses.loadAndTransformationStatues = loadAndTransformationStatues;
	}
	
}
