package com.assignment.consumer;

import java.util.concurrent.Callable;

import com.assignment.common.BalanceSheet;
import com.assignment.common.CocurrentBalanceSheetStorage;
import com.assignment.common.ConcurrentBalancedSheetsStatuses;
import com.assignment.common.Record;
import com.assignment.producer.Extractor;
import com.assignment.producer.Queue;
import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;

public class QueueConsumerThread implements Callable<Record>{

	
	private String idOfTheProcess;
	private Extractor extractor;
	
	public QueueConsumerThread(String extractionId, Extractor extractor) {
		super();
		this.extractor = extractor;
		idOfTheProcess=extractionId;
	}
	
	
	@Override
	public Record call() throws Exception {
		Record record=null;
		// when none of them is true ,means the source has pushed all the data to the queue and queue is empty which means that there is nothing more to process
		while(!(extractor.isRead() && Queue.getDataToProcess(idOfTheProcess).isEmpty()) && (ConcurrentBalancedSheetsStatuses.getLoadAndTransformationStatues().get(idOfTheProcess)==true))
		{
			System.out.println(Queue.getDataToProcess(idOfTheProcess).isEmpty());
			if(!Queue.getDataToProcess(idOfTheProcess).isEmpty()){
			try {
				record=Queue.getDataToProcess(idOfTheProcess).remove();
				if(CocurrentBalanceSheetStorage.getSheetsbeingProcessedBydId(idOfTheProcess)==null) {
					CocurrentBalanceSheetStorage.initBalanceSheet(idOfTheProcess);
				}
				CocurrentBalanceSheetStorage.getSheetsbeingProcessedBydId(idOfTheProcess).addToSheet(record);
				//process record
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		}
		// mark the sheet as read completely
		ConcurrentBalancedSheetsStatuses.getLoadAndTransformationStatues().put(idOfTheProcess, false);
		
		return null;
	}
	
}
