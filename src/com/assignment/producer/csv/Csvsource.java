package com.assignment.producer.csv;

import java.util.HashMap;
import java.util.Map;

import com.assignment.producer.Source;

public class Csvsource implements Source{

	private Map<String,String> map;
	private String type;
	@Override
	public void setType(String type) {
		type="csv";
		
	}

	@Override
	public String getType(String type) {
		// TODO Auto-generated method stub
		return this.type;
	}

	public Map<String,String> getSource(){
		return this.map;
	}
	@Override
	public void setSource(Map<String, String> identifiers) {
		this.map=identifiers;
	}

	
	
}
