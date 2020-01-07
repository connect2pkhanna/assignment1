package com.assignment.producer.csv;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.assignment.producer.Extractor;

public class CsvSourceExtractor extends Extractor{

	Object lock= new Object();
	private Csvsource source;
	private Map<String,String> identifiers=new HashMap();
	public Csvsource getSource(){
		return source;
	}

	public  CsvSourceExtractor(String filePath) {
		identifiers.put("file", filePath);
	}
	@Override
	public void instantiateSource() {
		synchronized (lock) {
		if(source==null) 
		{
			source=new Csvsource();
			source.setSource(identifiers);;
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
		String file=source.getSource().get("file");
		
		// check all fields in the map are valid .i.e file exists 
		return  ((file!=null) && (new File(file).exists()))?true:false;
	}
	
	

}
