package com.assignment.producer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.assignment.common.BalanceSheet;
import com.assignment.common.CocurrentBalanceSheetStorage;
import com.assignment.common.ConcurrentBalancedSheetsStatuses;
import com.assignment.common.ExtractAndLoadIdProducer;
import com.assignment.common.Record;
import com.assignment.consumer.QueueConsumerThread;
import com.assignment.producer.csv.CsvSourceExtractor;
import com.assignment.producer.csv.Csvsource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;

public class Pool {
	public static void main(String[] args) throws InterruptedException {

		CsvSourceExtractor extractor = new CsvSourceExtractor();
		String extractionId = ExtractAndLoadIdProducer.getId() + "csv";
		extractor.setSourceDescritption(ExtractAndLoadIdProducer.getId() + "csv");
		extractor.defineSource();
		extractor.validateSourceAvailability();
		// String SourceFilePath=extractor.getSource().getSource().get("location");

		// for reading a file we will use scannner,ref:->
		// https://stackoverflow.com/questions/10336478/does-the-scanner-class-load-the-entire-file-into-memory-at-once
		// incase we have a file of size 2Gb, we will end up loading the file in the RAM
		// incase we selectively do not read the lines by JAVA NIO
		File data = new File("/home/nest/Downloads/TechCrunchcontinentalUSA.csv");

		// Creating Scanner instnace to read File in Java
		Scanner scanner = null;
		try {
			scanner = new Scanner(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// mark this process as active
		ConcurrentBalancedSheetsStatuses.getLoadAndTransformationStatues().putIfAbsent(extractionId, true);
		// Reading each line of file using Scanner class
		List<QueueConsumerThread> tasks= new ArrayList<QueueConsumerThread>();
		Queue.intializeANewQueue(extractionId);
		tasks.add(new QueueConsumerThread(extractionId,extractor));
		tasks.add(new QueueConsumerThread(extractionId,extractor));
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(new QueueConsumerThread(extractionId,extractor));
		//executorService.submit(new QueueConsumerThread(extractionId,extractor));
		//executorService.invokeAll(tasks);
		
		while (scanner != null && scanner.hasNextLine()) {
			String line = scanner.nextLine();
			System.out.println(line);
			// if the names are like Khanna,Puneet or Vivek,QE,ThoughtSpot , splitting my
			// comma wont work ,we will have to extract as below
			String[] splitted = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			Record record = new Record();
			// if there are no 3 inputs in the string & if the amount is not empty 0r is not
			// valid,do not add the record to process
			if (splitted.length == 3 && !splitted[2].isEmpty()) {
				record.setFrom(splitted[0]);
				record.setTo(splitted[1]);
				try {
					record.setAmount(Double.valueOf(splitted[2]));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

			if (record.validate()) {
				System.out.println("validate success");
				Queue.getDataToProcess(extractionId).add(record);
				System.out.println(Queue.getDataToProcess(extractionId).size());
			}
		}
		// this will actually tell the consumers
	/*	1. There is no nothing more to process
		2.Please process whatever you can and mark the sheet as process when you see that the queue is empty */
		extractor.markSourceAsRead();
		// all one of the thread to finish the task(and see the queue empty, if the queue is emoty and source decalres there is nothing to process all the threads should finish .
		while(ConcurrentBalancedSheetsStatuses.getLoadAndTransformationStatues().get(extractionId)!=false) {
			Thread.sleep(1000);
		}
		executorService.shutdownNow();
		// now get the balance sheet and store it in a list ( sort it ) .
		//We decided to sort later s each insert on insertion if we sort while we store will take logn time(best case) & extra space
		// which will make the over all process slow and will explore more memory
		// Also concurrent API's in Java are not meant to keep the data sorted by their comparison definiation, they are meant to have concurrent additions/removal without impacting performance
		
	//	Phase3:
		//	Load
			
		BalanceSheet balanceSheet=	CocurrentBalanceSheetStorage.getSheetsbeingProcessedBydId(extractionId);
		ConcurrentHashMap<Record, Double> sheet =balanceSheet.getConcrrentSheet();
		TreeSet<Record> sortedSheet= new TreeSet();
		for (Record record : sheet.keySet()) {
			sortedSheet.add(record);
		}
		
		System.out.println(sortedSheet);
		//phase 4 
		//dump to a file

	}
}
