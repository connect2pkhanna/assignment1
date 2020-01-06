package com.assignment.producer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.assignment.common.Record;

public class Queue
{
	private static ConcurrentHashMap<String,BlockingQueue<Record>> mapOfQueues= new ConcurrentHashMap<String, BlockingQueue<Record>>();
	
	
	
     public static  BlockingQueue<Record> getDataToProcess(String id) {
    	return  mapOfQueues.get(id);
	}

	public static void intializeANewQueue(String id) {
		mapOfQueues.putIfAbsent(id, new ArrayBlockingQueue<Record>(1000, true));
	}


	
}
