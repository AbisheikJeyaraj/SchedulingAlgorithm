package controller;

import java.io.*;
import java.util.*;


/**
 * Created by Abisheik on 7/14/2016.
 */
public class SchedulingController {

    Integer sourceStartTime = 0;
    Integer aluDelay = 0;
    Integer mulDelay = 0;
    Integer sourceSinkDelay = 0;
    Integer latency = 0;
    Integer aluResources = 0;
    Integer mulResources = 0;

    LinkedHashMap<Integer, Object> operationMap = new LinkedHashMap<Integer, Object>();

    HashMap<Integer, Boolean> aluResourcesMap = new HashMap<Integer, Boolean>();
    HashMap<Integer, Boolean> mulResourcesMap = new HashMap<Integer, Boolean>();

    Map<Integer, Boolean> scheduleCheckMap = new HashMap<Integer, Boolean>();

    /*
        1) In this method, based on algorithm selection, input files are to be read.
        2) After reading corresponding function is called to scheduling.
            a) scheduleAsapAlgorithm()
            b) scheduleAlapAlgorithm()
            c) scheduleListLAlgorithm()
        3) After all the operations scheduled, output is displayed from final block and writePropertyFile()
            function is called to write the output to text file.
    */
    public void readInputFileByAlgorithm ( String algorithm ) {
        String line = "";
        loadPropertyFile( algorithm );
        BufferedReader in = null;
        try {
            if ( algorithm.equalsIgnoreCase("asap") ) {
                System.out.println("############################################");
                System.out.println("         Scheduling ASAP algorithm          ");
                System.out.println("############################################");
                in = new BufferedReader(new FileReader("asapSchedulingInput.txt"));
                while((line = in.readLine()) != null) {
                    //System.out.println(line);
                    scheduleAsapAlgorithm(line);
                }
            }
            else if ( algorithm.equalsIgnoreCase("alap") ) {
                System.out.println("############################################");
                System.out.println("         Scheduling ALAP algorithm          ");
                System.out.println("############################################");
                in = new BufferedReader(new FileReader("alapSchedulingInput.txt"));
                while((line = in.readLine()) != null) {
                    //System.out.println(line);
                    scheduleAlapAlgorithm(line);
                }
            }
            else if ( algorithm.equalsIgnoreCase("list_l") ) {
                System.out.println("############################################");
                System.out.println("        Scheduling LIST_L algorithm         ");
                System.out.println("############################################");
                in = new BufferedReader(new FileReader("listSchedulingInput.txt"));
                while((line = in.readLine()) != null) {
                    //System.out.println(line);
                    loadListLSchedulingInput(line);
                }
            }
            in.close();
            if ( algorithm.equalsIgnoreCase("list_l") ) {
                scheduleListLAlgorithm();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            /* Display content using Iterator*/
            SchedulingOperation so = null;
            TreeMap<Integer, Object> sortedMap = new TreeMap<>(operationMap);
            Set set = sortedMap.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                so = new SchedulingOperation();
                Map.Entry mentry = (Map.Entry)iterator.next();
                //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
                so = (SchedulingOperation)mentry.getValue();
                System.out.println("Operation :: " + so.getOperationId() + " scheduled @ clock cycle :: " + so.getStartTime());
            }
            System.out.println("############################################");
            System.out.println("         Latency Estimated :: " + latency    );
            System.out.println("############################################");
           //writePropertyFile ( algorithm ) ;
        }
    }

    /*
        This method is to schedule ASAP algorithm
    */
    public void scheduleAsapAlgorithm ( String line ) {
        SchedulingOperation operation = new SchedulingOperation();
        SchedulingOperation ope = null;
        String[] values = line.split(",");
        String[] predecessorList = null;
        Integer maxStartTime = 0;
        Integer key = 0;
        if ( values!= null ) {
            operation.setOperationId( Integer.valueOf( values[0] ) );
            operation.setOperationName( values[1] );

            if ( values.length > 2 )  {
                operation.setHasPredecessor(true);
                predecessorList = values[2].split("#");
                operation.setPredecessorList( predecessorList );
            }

            if ( operation.getOperationName().equalsIgnoreCase("source") ) {
                operation.setStartTime( sourceStartTime );
                operation.setDelayTime( sourceSinkDelay );
            }
            else {
                for ( int i = 0; i < operation.getPredecessorList().length; i++ ) {
                    ope = new SchedulingOperation();
                    key = Integer.valueOf( operation.getPredecessorList()[i] );
                    ope = (SchedulingOperation)operationMap.get(key);
                    if ( ope!= null && ope.isScheduled() ) {
                        maxStartTime = Math.max( maxStartTime, ope.getStartTime() + ope.getDelayTime() );
                    }
                }
                operation.setStartTime( maxStartTime );
                if ( operation.getOperationName().equalsIgnoreCase("alu") ) {
                    operation.setDelayTime(aluDelay);
                }
                else if ( operation.getOperationName().equalsIgnoreCase("mul") )  {
                    operation.setDelayTime(mulDelay);
                }
                else if ( operation.getOperationName().equalsIgnoreCase("sink") )  {
                    operation.setDelayTime(sourceSinkDelay);
                    latency = operation.getStartTime() - sourceStartTime;
                }
            }
            operation.setScheduled(true);
            operationMap.put( operation.getOperationId(), operation );
        }
    }

    /*
       This method is to schedule ALAP algorithm
    */
    public void scheduleAlapAlgorithm ( String line ) {
        SchedulingOperation operation = new SchedulingOperation();
        SchedulingOperation ope = null;
        String[] values = line.split(",");
        String[] successorList = null;
        Integer minStartTime = latency;
        Integer key = 0;
        if ( values!= null ) {
            operation.setOperationId( Integer.valueOf( values[0] ) );
            operation.setOperationName( values[1] );

            if ( values.length > 2 )  {
                operation.setHasPredecessor(true);
                successorList = values[2].split("#");
                operation.setSuccessorList(successorList);
            }

            if ( operation.getOperationName().equalsIgnoreCase("sink") ) {
                operation.setStartTime( latency + 1 );
                operation.setDelayTime( sourceSinkDelay );
            }
            else {
                if ( operation.getOperationName().equalsIgnoreCase("alu") ) {
                    operation.setDelayTime(aluDelay);
                }
                else if ( operation.getOperationName().equalsIgnoreCase("mul") )  {
                    operation.setDelayTime(mulDelay);
                }
                else if ( operation.getOperationName().equalsIgnoreCase("source") )  {
                    operation.setDelayTime(sourceSinkDelay);
                }
                for ( int i = 0; i < operation.getSuccessorList().length; i++ ) {
                    ope = new SchedulingOperation();
                    key = Integer.valueOf( operation.getSuccessorList()[i] );
                    ope = (SchedulingOperation)operationMap.get(key);
                    if ( ope!= null && ope.isScheduled() ) {
                        minStartTime = Math.min( minStartTime, ope.getStartTime() - operation.getDelayTime() );
                    }
                }
                operation.setStartTime( minStartTime );
            }
            operation.setScheduled(true);
            operationMap.put( operation.getOperationId(), operation );
        }
    }

    /*
       This method is to load the input files for List_L scheduling
    */
    public void loadListLSchedulingInput ( String line ) {
        SchedulingOperation operation = new SchedulingOperation();
        String[] values = line.split(",");
        String[] predecessorList = null;
        if ( values!= null ) {
            operation.setOperationId( Integer.valueOf( values[0] ) );
            operation.setOperationName( values[1] );
            if ( values.length > 2 )  {
                operation.setHasPredecessor(true);
                predecessorList = values[2].split("#");
                operation.setPredecessorList(predecessorList);
            }
            operationMap.put( operation.getOperationId(), operation );
            scheduleCheckMap.put( operation.getOperationId(), false);
        }
    }

    /*
       This method is to schedule LIST_L algorithm
    */
    public void scheduleListLAlgorithm () {
        SchedulingOperation operation = null;
        SchedulingOperation ope = null;
        String[] predecessorList = null;
        Integer maxStartTime = 0;
        Integer key = 0;
        boolean availability = false;
        Integer resourceKey = 0;
        boolean allPredecessorScheduled = false;

        boolean allOpeScheduled = false;
        Integer predicatedStartTime = 0;

        Set operationSet = null;
        Iterator operationIterator = null;

        Set aluResourceSet = null;
        Iterator aluResourceIterator = null;

        Set mulResourceSet = null;
        Iterator mulResourceIterator = null;

        Integer aluPredicatedStartTime = 1;
        Integer mulPredicatedStartTime = 1;

        List<Integer> aluClockCycleList = new ArrayList<Integer>();
        List<Integer> mulClockCycleList = new ArrayList<Integer>();

        while( !allOpeScheduled ) {

            predicatedStartTime = predicatedStartTime + 1;
            operationSet = operationMap.entrySet();
            operationIterator = operationSet.iterator();

            while (operationIterator.hasNext()) {

                maxStartTime = 0;
                //availability = "";
                resourceKey = 0;

                aluResourceSet = aluResourcesMap.entrySet();
                aluResourceIterator = aluResourceSet.iterator();

                mulResourceSet = mulResourcesMap.entrySet();
                mulResourceIterator = mulResourceSet.iterator();

                operation = new SchedulingOperation();
                Map.Entry mentry = (Map.Entry) operationIterator.next();
                operation = (SchedulingOperation) mentry.getValue();
                if (operation != null && !operation.isScheduled()) {

                    if (operation.getOperationName().equalsIgnoreCase("source")) {
                        operation.setDelayTime(sourceSinkDelay);
                        operation.setStartTime(sourceStartTime);
                        operation.setScheduled(true);
                    } else if (operation.getOperationName().equalsIgnoreCase("alu")) {
                        operation.setDelayTime(aluDelay);
                        for (int i = 0; i < operation.getPredecessorList().length; i++) {
                            ope = new SchedulingOperation();
                            key = Integer.valueOf(operation.getPredecessorList()[i]);
                            ope = (SchedulingOperation) operationMap.get(key);
                            if (ope != null && ope.isScheduled()) {
                                //maxStartTime =  Math.max( Math.max(maxStartTime, ope.getStartTime() + ope.getDelayTime()), aluPredicatedStartTime );
                                maxStartTime = Math.max(maxStartTime, ope.getStartTime() + ope.getDelayTime());
                                allPredecessorScheduled = true;
                            }
                            else {
                                allPredecessorScheduled = false;
                                break;
                            }
                        }
                        if (maxStartTime > 0 && allPredecessorScheduled) {
                            while (aluResourceIterator.hasNext()) {
                                Map.Entry aluEntry = (Map.Entry) aluResourceIterator.next();
                                resourceKey = (Integer) aluEntry.getKey();
                                availability = (Boolean) aluEntry.getValue();
                                if (availability) {
                                    while ( Collections.frequency(aluClockCycleList, maxStartTime) == aluResources ) {
                                        maxStartTime = maxStartTime + aluDelay;
                                        continue;
                                    }
                                    operation.setScheduledBy(resourceKey);
                                    aluResourcesMap.put(resourceKey, false);
                                    operation.setStartTime(maxStartTime);
                                    operation.setScheduled(true);
                                    aluClockCycleList.add(operation.getStartTime());
                                    break;
                                }
                            }
                        }
                    } else if (operation.getOperationName().equalsIgnoreCase("mul")) {
                        operation.setDelayTime(mulDelay);
                        for (int i = 0; i < operation.getPredecessorList().length; i++) {
                            ope = new SchedulingOperation();
                            key = Integer.valueOf(operation.getPredecessorList()[i]);
                            ope = (SchedulingOperation) operationMap.get(key);
                            if (ope != null && ope.isScheduled()) {
                                //maxStartTime =  Math.max( Math.max(maxStartTime, ope.getStartTime() + ope.getDelayTime()), mulPredicatedStartTime );
                                maxStartTime = Math.max(maxStartTime, ope.getStartTime() + ope.getDelayTime());
                                allPredecessorScheduled = true;
                            }
                            else {
                                allPredecessorScheduled = false;
                                break;
                            }
                        }
                        if (maxStartTime > 0 && allPredecessorScheduled) {
                            while (mulResourceIterator.hasNext()) {
                                Map.Entry mulEntry = (Map.Entry) mulResourceIterator.next();
                                resourceKey = (Integer) mulEntry.getKey();
                                availability = (Boolean) mulEntry.getValue();
                                //System.out.println(" mul maxStartTime : " + maxStartTime + Collections.frequency(mulClockCycleList, maxStartTime));
                                if (availability) {
                                    while ( Collections.frequency(mulClockCycleList, maxStartTime) == mulResources ) {
                                        maxStartTime = maxStartTime + mulDelay;
                                        continue;
                                    }
                                    operation.setScheduledBy(resourceKey);
                                    mulResourcesMap.put(resourceKey,false);
                                    operation.setStartTime(maxStartTime);
                                    operation.setScheduled(true);
                                    mulClockCycleList.add(operation.getStartTime());
                                    break;
                                }
                            }
                        }
                    } else if (operation.getOperationName().equalsIgnoreCase("sink")) {
                        operation.setDelayTime(sourceSinkDelay);
                        for ( int i = 0; i < operation.getPredecessorList().length; i++ ) {
                            ope = new SchedulingOperation();
                            key = Integer.valueOf( operation.getPredecessorList()[i] );
                            ope = (SchedulingOperation)operationMap.get(key);
                            if ( ope!= null && ope.isScheduled() ) {
                                maxStartTime = Math.max( maxStartTime, ope.getStartTime() + ope.getDelayTime() );
                                allPredecessorScheduled = true;
                            }
                            else {
                                allPredecessorScheduled = false;
                                break;
                            }
                        }
                        if (maxStartTime > 0 && allPredecessorScheduled) {
                            operation.setStartTime(maxStartTime);
                            operation.setScheduled(true);
                            latency = operation.getStartTime() - sourceStartTime;
                        }
                    }
                    operationMap.put(operation.getOperationId(), operation);
                    scheduleCheckMap.put(operation.getOperationId(), operation.isScheduled());

                }
                //System.out.println("Operation => " + operation.getOperationId() + ", is scheduled => " + operation.getStartTime());
            }

            for (Map.Entry<Integer, Boolean> entry : scheduleCheckMap.entrySet()) {
                //System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
                if ( entry.getValue() ) {
                    allOpeScheduled = true;
                }
                else {
                    allOpeScheduled = false;
                    for ( int i = 1; i <= aluResourcesMap.size(); i++ ) {
                        aluResourcesMap.put(i,true);
                    }
                    for ( int i = 1; i <= mulResourcesMap.size(); i++ ) {
                        mulResourcesMap.put(i,true);
                    }
                    break;
                }
            }
        }
    }

    /*
       This method is to load the property file
    */
    public void loadPropertyFile( String algorithm ) {
        File file = null;
        FileInputStream fileInput = null;
        Properties properties = null;
        try {
            file = new File("scheduling.properties");
            fileInput = new FileInputStream(file);
            properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            sourceStartTime = Integer.valueOf( properties.getProperty("sourceStartTime") );
            aluDelay = Integer.valueOf( properties.getProperty("aluDelay") );
            mulDelay = Integer.valueOf( properties.getProperty("mulDelay") );
            sourceSinkDelay = Integer.valueOf( properties.getProperty("sourceSinkDelay") );

            aluResources = Integer.valueOf( properties.getProperty("aluResources") );
            mulResources = Integer.valueOf( properties.getProperty("mulResources") );

            if ( aluResources != null && aluResources  > 0 ) {
                for ( int i = 1; i <= aluResources; i++ ) {
                    aluResourcesMap.put(i,true);
                }
            }

            if ( mulResources != null && mulResources  > 0 ) {
                for ( int i = 1; i <= mulResources; i++ ) {
                    mulResourcesMap.put(i,true);
                }
            }
            if ( algorithm.equalsIgnoreCase("alap")){
                if ( !properties.getProperty("latency").isEmpty() && properties.getProperty("latency") != "" ) {
                    latency = Integer.valueOf( properties.getProperty("latency") );
                }
                else {
                    System.out.println("Please enter the latency input for ALAP scheduling in input file and run again.");
                    System.exit(0);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
       This method is to write the values to the output file
    */
    public void writePropertyFile( String algorithm ) {
        Properties prop = null;
        File file = null;
        FileOutputStream fileOutput = null;
        StringBuffer buffer = null;
        try {

            buffer = new StringBuffer();
            FileWriter writer = new FileWriter("schedulingOutputFile.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            buffer.append("############################################\n");
            if ( algorithm.equalsIgnoreCase("asap") ) {
                buffer.append("         Scheduling ASAP algorithm          \n");
            }
            else if ( algorithm.equalsIgnoreCase("alap") ) {
                buffer.append("         Scheduling ALAP algorithm          \n");
            }
            else if ( algorithm.equalsIgnoreCase("list_l") ) {
                buffer.append("         Scheduling LIST_L algorithm        \n");
            }
            buffer.append("############################################\n");

            SchedulingOperation so = null;
            TreeMap<Integer, Object> sortedMap = new TreeMap<>(operationMap);
            Set set = sortedMap.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
                so = new SchedulingOperation();
                Map.Entry mentry = (Map.Entry)iterator.next();
                so = (SchedulingOperation)mentry.getValue();
                buffer.append("Operation :: " + so.getOperationId() + " scheduled @ clock cycle :: " + so.getStartTime() + "\n" );
            }
            buffer.append("############################################\n");
            buffer.append("         Latency Estimated :: " + latency + "\n");
            buffer.append("############################################\n");

            bufferedWriter.write(buffer.toString());
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
