# assignment1

Please run the jar file with arguments alike 
-DsourceFile=/home/nest/Downloads/TechCrunchcontinentalUSA.csv  -DnumOfConsumerThreads=2  -DouputPath=/home/nest/Downloads/

ExtractTranformAndLoadProcess.java has the thread(main) which kicks off the process



1.Validating the source if is valid 

2. Extract the data ,Each extraction process(1 process per csv file) is identified by a id 
   String extractionId = ExtractAndLoadIdProducer.getId() + "-flatFile"; 
   
   Scanner class is used to read the file as if the files are big of size >2 GB we do not want to load the while content of 2 GB 
   in RAM and fill it up.
   
   Please refer to the discussion here:
   https://stackoverflow.com/questions/10336478/does-the-scanner-class-load-the-entire-file-into-memory-at-once
   
   
3. The load process is handled by multiple threads which put the data initialized in the BlockingQueue in the below code

   ConcurrentBalancedSheetsStatuses.getLoadAndTransformationStatues().putIfAbsent(extractionId, true);
		// Reading each line of file using Scanner class
		Queue.intializeANewQueue(extractionId);
    
4. Threads are spawned by Java concurrent API framework as below 
   // we spawn n Threads
		int count = 0;
		int numOfConsumersToReadFromQueue = Integer.parseInt(System.getProperty("numOfConsumerThreads"));
		ExecutorService executorService = Executors.newFixedThreadPool(numOfConsumersToReadFromQueue);

		// PHASE2 : Transform
		while (count < numOfConsumersToReadFromQueue) {
			executorService.submit(new QueueConsumerThread(extractionId, extractor));
			count++;
		}
 5.com.assignment.common.Record signifies each transaction defined in source 
  each record is unique combination of the parties (from -to) as equals method  & hashcode defines the rule of equality below 
 The object is also comparable which is also defined from the strings of the parties involved.
 
 The sorting defination defined in the Record class makes sure that the data is wrriten in the sorted manner to csv as is sorted 
 using TreeSet.
