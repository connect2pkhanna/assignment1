package com.assignment.producer.csv;

import java.util.HashMap;
import java.util.Map;

import com.assignment.producer.Extractor;

public class CsvSourceExtractor extends Extractor{

	Object lock= new Object();
	private Csvsource source;
	
	public Csvsource getSource(){
		return source;
	}

	@Override
	public void defineSource() {
		synchronized (lock) {
			
		
		if(source==null) 
		{
			Map<String,String> map= new HashMap();
			// read from file
			//source.setSource(map);
		}
		}
	}

	@Override
	public boolean validateSourceAvailability() {
		// check all fields in the map are valid 
		if(source==null || source.getSource()==null)
		{
			return false;
		}
		// check all fields in the map are valid .i.e file exists and is readble or check if the size if not greater than a threshHold
		// like as 30 GB to void DOS attack
		return true;
	}
	
	

}
