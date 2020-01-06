package com.assignment.producer;

import java.util.Map;

public interface Source {

	void setType(String type);
	String getType(String type);
	// read from propertiee file or pass through command line which are generally strings thus Map is generalized to <String,String>
	void setSource(Map<String,String> identifiers);
	
}
