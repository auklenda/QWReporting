/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;
import javax.swing.SwingWorker;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author Alf
 */
public class QWSwingWorker extends SwingWorker<Integer, String> {
 JTextArea messageText = null;
 int minThread = 0;
 int minQueue = 0;
 int longRunners = 0;
    public QWSwingWorker(javax.swing.JTextArea messageText) {
       this.messageText = messageText; 
    }

    public void setMinThread(int minThread) {
        this.minThread = minThread;
    }

    public void setMinQueue(int minQueue) {
        this.minQueue = minQueue;
    }

    public void setLongRunners(int longRunners) {
        this.longRunners = longRunners;
    }

 static boolean hungBP = true;
 static boolean badItems=true;
 static boolean buffered = true;
 static boolean maxThreads= true;
 static boolean maxQueues=true;
 static boolean qFrequence=true;
 static boolean bpNames=true;
 static boolean longRun=true;
 static boolean heapGraph = true;
 static boolean threadGraph=true;
 static boolean queueGraph=true;
 static boolean maxpools=true;
 static boolean poolRequest=true;
 static boolean biggestQueue=true;
 static boolean bpTable=true;
 static boolean swingTable=true;
 static boolean swiftTable=true;
 static boolean systemTable=true; 
 static boolean environment=true;
 static boolean longRunner=true;
 static boolean selectedBPs=true;
 static boolean hungBPs=true;
 static boolean doConfig=true;
 
  @Override
  protected Integer doInBackground() throws Exception {
  
    QWExtractor eXtractor = new QWExtractor(null);
    publish("Prolog started");
    eXtractor.prolog();
    eXtractor.jdbcHdr();
    publish("Prolog Ended");
    publish("toalRecords started");
    eXtractor.totalRecords();
    publish("toalRecords Ended");
    if (QWAGlobal.EnvOnTop) {
       if (QWAGlobal.environment) {
           publish("displayEnvironment started");
           eXtractor.displayEnvironment();
           publish("displayEnvironment ended");
       }
       if (QWAGlobal.doConfig) {
           publish("buildConfig started");
           eXtractor.buildConfig();
           publish("buildConfig ended");
       }
    }
    if (QWAGlobal.maxpools) {
        publish("maxPool started");
        eXtractor.getMaxPool();
        publish("maxPool ended");
    }    
    if (QWAGlobal.badItems) {
        publish("getBadItems started");
        eXtractor.getBadItems();
        publish("getBadItems ended");
    }
    if (QWAGlobal.buffered) {
        publish("getBuffered Started"); 
        eXtractor.getBufferedItems();
        publish("getBuffered ended");
    }
    if (QWAGlobal.poolRequest) {    
        publish("getPoolRequests Started");
        eXtractor.createPoolRequests();
        publish("getPoolRequests ended");
        publish("analyzePoolConnections Started");
        eXtractor.analyzePoolConnections();
        publish("analyzePoolConnections ended");
    }
    
    if (QWAGlobal.queueGraph) {
        publish("getActiveGraphs started");
        eXtractor.getActiveGraph(0);
        publish("getActiveGraphs ended");
    }    
    if (QWAGlobal.maxThreads) {
        publish("getThreads Started");
        eXtractor.getThreads(minThread);
        publish("getThreads ended");
    }
    if (QWAGlobal.maxQueues) {
        publish("getDepth started");
        eXtractor.getDepth(minQueue);
        publish("getDepth ended");
    }    
    if (QWAGlobal.maxQueues) {     // Daily
        publish("Daily Graphs started");
        eXtractor.buildSingleFileGraphs();
        publish("Daily Graphs ended");
    }    
   
    if (QWAGlobal.queueGraph) {
        publish("getQueueGraphs started");
        eXtractor.getQueueGraph(0,-1); // Total depth
        eXtractor.getQueueGraph(0,1); //individual q-depth
        eXtractor.getQueueGraph(0,2); //individual q-depth
        eXtractor.getQueueGraph(0,3); //individual q-depth
        eXtractor.getQueueGraph(0,4); //individual q-depth
        eXtractor.getQueueGraph(0,5); //individual q-depth
        eXtractor.getQueueGraph(0,6); //individual q-depth
        eXtractor.getQueueGraph(0,7);
        publish("getQueueGraphs ended");
    }
     /*
    if (QWAGlobal.maxQueues) {
        publish("getDepth started");
        eXtractor.getDepth(minQueue);
        publish("getDepth ended");
    }
    */
    if (QWAGlobal.biggestQueue) {
        publish("biggestQueuers started");
        eXtractor.biggestQueuers();
        publish("biggestQueuers ended");
    }

    if (QWAGlobal.bpTable) {
        publish("buildBPperiodicUsageSN started");
        eXtractor.buildBPperiodicUsageSN();
        publish("buildBPperiodicUsageSN ended");
    }

    if (QWAGlobal.bpTable) {
        publish("buildBPtable started");
        eXtractor.buildBPtable(300);
        publish("buildBPtable ended");
    }
    if (QWAGlobal.swingTable) {
        publish("builSwingBPUsage started");
        eXtractor.buildSwingBPUsage();
        publish("buildSwingBUsage ended");
    }
    if (QWAGlobal.swiftTable) {
        publish("builSwiftBPUsage started");
        eXtractor.buildSwiftBPUsage();
        publish("buildSwiftBUsage ended");
    }
    if (QWAGlobal.systemTable) {
        publish("builSystemBPUsage started");
        eXtractor.buildSystemBPUsage();
        publish("buildSystemBUsage ended");
    }
    if (QWAGlobal.longRunner) {
        publish("getLongRunners started");
        eXtractor.getLongRunningBPs(longRunners);
        publish("getLongRunners ended");
    }
    if (QWAGlobal.selectedBPs) {
        publish("getSelectedBPs started");
       // eXtractor.getSelectedBPs("Schedule_PurgeService");
        publish("getSelectedBPs ended");
    }
    if (QWAGlobal.hungBPs) {
        publish("getHung started");
        eXtractor.getHungBPs(longRunners);
        publish("getHung ended");
    }
    if (!QWAGlobal.EnvOnTop) {
       if (QWAGlobal.environment) {
          publish("displayEnvironment started");
          eXtractor.displayEnvironment();
          publish("displayEnvironment ended");
       }
       if (QWAGlobal.doConfig) {
          publish("buildConfig started");
          eXtractor.buildConfig();
          publish("buildConfig ended");
       }
    }
    /*
    if (QWAGlobal.heapGraph) {
        publish("getHeapGrahp started");
        eXtractor.getHeapGraph();
        publish("getHeapGraphs ended");
    }    

        publish("getActiveGraphs started");
        eXtractor.getActiveGraph(0);
        publish("getActiveGraphs ended");
        publish("getQueueGraphs started");
        eXtractor.getQueueGraph(0);
        publish("getQueueGraphs ended");
*/
        eXtractor.epilog();
        publish("DONE");
  /*    
      // Start
    publish("Start");
    setProgress(1);
    
    // More work was done
    publish("More work was done");
    setProgress(10);

    // Complete
    publish("Complete");
    setProgress(100); */
    return 1;
  }
  
  @Override
  protected void process(List<String> chunks) {
 
    for (final String string : chunks) {
      messageText.append(string);
      messageText.append("\n");
    }    
  }
    
}
