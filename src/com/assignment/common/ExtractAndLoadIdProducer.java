package com.assignment.common;

import java.util.concurrent.atomic.AtomicInteger;

public class ExtractAndLoadIdProducer {

	private  static final AtomicInteger id= new AtomicInteger();

	public static int getId() {
		return id.incrementAndGet();
	}
	
}
