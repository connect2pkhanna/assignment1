package com.assignment.producer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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

public class ExtractTranformAndLoadProcess {
	public static void main(String[] args) throws InterruptedException, IOException {

		String sourceFile = System.getProperty("sourceFile");
		CsvSourceExtractor extractor = new CsvSourceExtractor(sourceFile);

		// identify each extraction process as different
		String extractionId = ExtractAndLoadIdProducer.getId() + "-flatFile";
		extractor.setId(extractionId);
		extractor.instantiateSource();

		// do not process if the source is no valid/accesssble & return from the
		// thread's method's flow
		if (!extractor.validateSourceAvailability()) {
			return;
		}

		File data = new File(sourceFile);
		// Creating Scanner instnace to read File in Java
		Scanner scanner = null;
		scanner = new Scanner(data);

		// mark this process as active
		ConcurrentBalancedSheetsStatuses.getLoadAndTransformationStatues().putIfAbsent(extractionId, true);
		// Reading each line of file using Scanner class
		Queue.intializeANewQueue(extractionId);

		// we spawn n Threads
		int count = 0;
		int numOfConsumersToReadFromQueue = Integer.parseInt(System.getProperty("numOfConsumerThreads"));
		ExecutorService executorService = Executors.newFixedThreadPool(numOfConsumersToReadFromQueue);

		// PHASE2 : Transform
		while (count < numOfConsumersToReadFromQueue) {
			executorService.submit(new QueueConsumerThread(extractionId, extractor));
			count++;
		}

		/*
		 * read the file & put in the Queue
		 * for reading a file we will use scannner,ref:->
		 * https://stackoverflow.com/questions/10336478/does-the-scanner-class-load-the-
		 * entire-file-into-memory-at-once incase we have a file of size 2Gb, we will
		 * end up loading the file in the RAM incase we selectively do not read the
		 * lines by JAVA NIO
		 */

		// PHASE : 1 -> extract
		while (scanner != null && scanner.hasNextLine()) {
			String line = scanner.nextLine();
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

			// validate if any field is incorrect (absent,null ,if amount is not double )
			if (record.validate()) {
				// if the record is valid add to queue
				Queue.getDataToProcess(extractionId).add(record);
			}
		}

		// this will actually tell the consumers
		/*
		 * 1. There is no nothing more to process 2.Please process whatever you can and
		 * mark the sheet as process when you see that the queue is empty
		 */
		extractor.markSourceAsRead();
		
		
		
		// all one of the thread to finish the task(and see the queue empty, if the
		// queue is emoty and source decalres there is nothing to process all the
		// threads should finish .
		while (ConcurrentBalancedSheetsStatuses.getLoadAndTransformationStatues().get(extractionId) != false) {
			Thread.sleep(1000);
		}
		
		
		executorService.shutdownNow();

		/*
		 * now get the balance sheet and store it in a list ( sort it ) . We decided to
		 * sort later s each insert on insertion if we sort while we store will take
		 * logn time(best case) & extra space which will make the over all process slow
		 * and will explore more memory Also concurrent API's in Java are not meant to
		 * keep the data sorted by their comparison definiation, they are meant to have
		 * concurrent additions/removal without impacting performance
		 */

		// Phase3:
		// Load

		BalanceSheet balanceSheet = CocurrentBalanceSheetStorage.getSheetsbeingProcessedBydId(extractionId);
		ConcurrentHashMap<Record, Double> sheet = balanceSheet.getConcrrentSheet();
		TreeSet<Record> sortedSheet = new TreeSet();
		for (Record record : sheet.keySet()) {
			sortedSheet.add(record);
		}

		boolean successFullWrite=writeToaFile(sortedSheet);
		System.out.println("The file was written succesfully");

	}

	private static boolean writeToaFile(TreeSet<Record> sortedSheet) throws IOException {

		String dumpLocation=System.getProperty("ouputPath") + System.currentTimeMillis()+".csv";
		System.out.println("The output file will be "+dumpLocation);
		// generate a file with a new unique output everytime
		FileWriter csvWriter = new FileWriter(dumpLocation);

		for (Record record : sortedSheet) {
			List<String> data = new ArrayList<String>();
			data.add(record.getFrom());
			data.add(record.getTo());
			data.add(Double.toString(record.getAmount()));
			csvWriter.append(String.join(",", data));
			csvWriter.append("\n");
		}

		csvWriter.flush();
		csvWriter.close();
		return true;
	}
}
