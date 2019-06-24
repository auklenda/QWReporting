/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import com.sterlingcommerce.aauklend.utilities.SearchForFiles;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.JTextArea;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * @author AAuklend
 */
public class QWExtractor extends QWUhtml{
  
  static String folder = "D:\\Customers\\SocGen\\SWING\\QWLOGS\\06082013";
  //static String folder = "D:\\Customers\\SocGen\\SWING\\QWLOGS\\17072013";
  static String outFolder = "\\result";
  static String QWreport = "\\QWreport.html";
  static String project = "SWING";
  static String table = "<table border='1' cellspacing='0' cellpadding='4' style='font-size: small;'>";
  String td = "<td>";
  String tde = "</td>";
  String tr = "<tr>";
  String tre = "</tr>";  
  String nl = "\n";
  //static String folder = "D:\\Customers\\SocGen\\ETECE\\QWWLogs\\11-07-2013";  
  String filter = "*node*";
  String ajdbcReport ="<h3>JDBC: The follwing report BadItem and Buffered issues</h3> ";
  QWUqueue qwuQueue = null;  
  File [] files = null;
  QWUjdbc jdbc = null;
  ArrayList fileNames = new ArrayList();
  JTextArea jta = null;
  ArrayList nodeMap = new ArrayList();
  File [] fileList = null;
  static Properties poolMaxValues = new Properties();
  static HashMap poolRequests = new HashMap();
  private boolean addComment = false;
  
   public QWExtractor(JTextArea jtarea) {
       jta = jtarea;
       
   }
   public File [] findNodeFiles(){ 
       return fileList;
   }
   public File [] createNodeFiles(){
  
    try {
        SearchForFiles sf = new SearchForFiles();
        //Vector fn = getFileNames("c:\\temp",true,"*");
        fileList = sf.scanFolder(folder,false,filter);
    } catch(Exception exc) {
        Logger.getLogger(QWExtractor.class.getName()).log(Level.SEVERE, null, exc);
    }   
    return fileList;
  } // end for loop
/**
 * Just list the files included in this QWreport. 
 * @param f
 * @return 
 */
 public String filesToProcess(File [] f) throws Exception  {
      long [] rectypes = new long[QWAGlobal.noop];
      QWUconfig ucfg = new QWUconfig();
      if (QWAGlobal.EnvList == null)
          QWAGlobal.EnvList = new HashMap();
      QWAGlobal.numberOfFiles = f.length;
      StringBuilder sb = new StringBuilder();
      sb.append("<h3>Files to be procesesed in current analyze</h3>");
      sb.append("<ul>");
      sb.append("<li type=square><b>Caution:</b>File size with pink background does not contain qww data<br>");
      sb.append("<li type=square><b>Reason:</b>LWqww not connected to Node</p>");
      sb.append("</ul>");
      sb.append(nl);
      sb.append("<input type='button' id='bt1' onclick=\"return toggleMe('special1','bt1','List Files')\" value='+File List'>").append(nl);
      sb.append("<p id='special1' style='display:none'>").append(nl);
      sb.append(table);
      sb.append(nl);
      sb.append("<tr>");
      sb.append(QWUhtml.thCls).append("File Name").append("</th>").append(QWUhtml.thCls).append("File Length (bytes)").append("</th>");
      sb.append(QWUhtml.thCls).append("QUE").append("</th>").append(QWUhtml.thCls).append("WFC").append("</th>");
      sb.append(QWUhtml.thCls).append("MEM").append("</th>").append(QWUhtml.thCls).append("HDR").append("</th>");
      sb.append(QWUhtml.thCls).append("JDBC").append("</th>").append(QWUhtml.thCls).append("CFG").append("</th>");
      sb.append(QWUhtml.thCls).append("ENV").append("</th>");
      sb.append("</tr>");
      for (int i = 0; i < f.length; i++) {   // Read and count all records types
            String fn = f[i].getAbsolutePath();
            if (f[i].length() ==0)
                continue;
           
            QWUreader.setNextFile(fn);
            String str;
            String node = null;
            for (int j = 0; j < rectypes.length; j++) {
                 rectypes[j]=0l;
            }
            while ((str = QWUreader.readLine()) != null) {
                  if (node == null){
                     int inx = str.indexOf(" ",1);
                     node = str.substring(0,inx);
                     if (!QWUjdbc.nhm.containsKey(node)){
                        QWUjdbc.nhm.put(node,0);
                        QWAGlobal.nodeList.add(node);
                        if (!QWUjdbc.nodeList.containsKey(node)) {
                           int l = QWUjdbc.nodeList.size();
                           QWUjdbc.nodeList.put(node,l);
                           QWAGlobal.nodeNames[l]=node;
                        }    
                     }
                  }
                  int rectype = QWUreader.getRecordType(str);
                  if (rectype == QWAGlobal.cfg) {
                      ucfg.addConfig(str);
                  }
                  if (rectype == 0) {
                      System.out.println("");
                  }
                  rectypes[rectype-1]++;

                  if (rectype == QWAGlobal.cfg){
                      if (!QWAGlobal.nodeList.contains(node)){
                          QWAGlobal.nodeList.add(node);
                          QWAGlobal.cfgList.put(node, str);
                      }
                  }
 //                 QWAGlobal.recTypes[rect-1]++;
                  if (rectype == QWAGlobal.env){
                      QWAGlobal.EnvList.put(node,str);
                  } 
                  if (rectype == QWAGlobal.jdbc){
                      QWUreader.insertIfNotIn(node, str, poolMaxValues);
                      QWUreader.insertRegardless(node, str, poolRequests);
                  }
            }
            addRecords(node,rectypes);
            sb.append(QWUhtml.nl);
            sb.append("<tr>");
            sb.append("<td>").append(f[i].getName()).append("</td>");
            //sb.append("<td>").append(f[i].getAbsolutePath()).append("</td>");
            if (f[i].length() > 200)
                sb.append("<td>").append(f[i].length()).append("</td>");
            else
                sb.append("<td class='warn'>").append(f[i].length()).append("</td>");
            sb.append(td).append(rectypes[0]).append(tde);
            sb.append(td).append(rectypes[1]).append(tde);
            sb.append(td).append(rectypes[2]).append(tde);
            sb.append(td).append(rectypes[3]).append(tde);
            sb.append(td).append(rectypes[4]).append(tde);
            sb.append(td).append(rectypes[5]).append(tde);
            sb.append(td).append(rectypes[6]).append(tde);
            sb.append("</tr>");
        }
      sb.append("</table></p>");
      return sb.toString();
    }
  public void totalRecords() {
  //    long [] rectypes = new long[QWAGlobal.noop];
      StringBuilder sb = new StringBuilder();
      sb.append("<h3>Total number of records pr. NODE</h3>");
      sb.append("<ul>");
      sb.append("<li type=square><b>QUE  - various counters");
      sb.append("<li type=square><b>WFC  - number of BP records (same BP could appear more than ones)");
      sb.append("<li type=square><b>MEM  - number of heap sampling records");
      sb.append("<li type=square><b>HDR  - number of header records (normally 1 pr file)");
      sb.append("<li type=square><b>JDBC - number of JDBC sampling records");
      sb.append("<li type=square><b>CFG  - number of config records");
      sb.append("<li type=square><b>ENV  - number of environment records");
      sb.append("</ul>");
      sb.append(QWUhtml.nl);
      sb.append(table);
      sb.append(QWUhtml.nl);
      sb.append("<tr>");
      sb.append(QWUhtml.thCls).append("NODE ").append("</th>");
      sb.append(QWUhtml.thCls).append("QUE").append("</th>").append(QWUhtml.thCls).append("WFC").append("</th>");
      sb.append(QWUhtml.thCls).append("MEM").append("</th>").append(QWUhtml.thCls).append("HDR").append("</th>");
      sb.append(QWUhtml.thCls).append("JDBC").append("</th>").append(QWUhtml.thCls).append("CFG").append("</th>");
      sb.append(QWUhtml.thCls).append("ENV").append("</th>");
      sb.append("</tr>");
      Hashtable ht = QWAGlobal.recordCount;
      Enumeration counts = ht.keys();
      while(counts.hasMoreElements()) {
         String str = (String) counts.nextElement();
         NumericalHashMap nhm = (NumericalHashMap)ht.get(str);
         sb.append(tr).append(td).append(str).append(tde);
         sb.append(td).append(nhm.get("que")).append(tde);
         sb.append(td).append(nhm.get("wfc")).append(tde);
         sb.append(td).append(nhm.get("mem")).append(tde);
         sb.append(td).append(nhm.get("hdr")).append(tde);
         sb.append(td).append(nhm.get("jdb")).append(tde);
         sb.append(td).append(nhm.get("cfg")).append(tde);
         sb.append(td).append(nhm.get("env")).append(tde);
         sb.append(tre);
      }   
      sb.append("</table>");
      QWAUtil.printIt(sb.toString());
   }     

    private void recordFileNames(String fn) {
        fileNames.add(fn);
    } 
    private void addRecords(String node, long [] rt) {
      NumericalHashMap rc= null; 
      if (!QWAGlobal.recordCount.containsKey((String)node)){
          rc = new NumericalHashMap();
          rc.put((String)"que", rt[0]);
          rc.put((String)"wfc", rt[1]);
          rc.put((String)"mem", rt[2]);
          rc.put((String)"hdr", rt[3]);
          rc.put((String)"jdb", rt[4]);
          rc.put((String)"cfg", rt[5]);
          rc.put((String)"env", rt[6]);
          QWAGlobal.recordCount.put((String)node, rc);
      } else {
          rc = (NumericalHashMap)QWAGlobal.recordCount.get(node);
          long cnt;
          cnt = rc.getLong("que") + rt[0];
          rc.put((String)"que", cnt);
          cnt = rc.getLong("wfc") + rt[1];
          rc.put((String)"wfc", cnt);          
          cnt = rc.getLong("mem") + rt[2];
          rc.put((String)"mem", cnt); 
          cnt = rc.getLong("hdr") + rt[3];
          rc.put((String)"hdr", cnt);          
          cnt = rc.getLong("jdb") + rt[4];
          rc.put((String)"jdb", cnt);
          cnt = rc.getLong("cfg") + rt[5];
          rc.put((String)"cfg", cnt);
          cnt = rc.getLong("env") + rt[6];
          rc.put((String)"env", cnt);
          QWAGlobal.recordCount.put((String)node, rc);
      }
    }
    public void getfilesToProcess(){
      files = createNodeFiles();
      if (files.length==0)
           return;
      String ftop = null;
        try {
            ftop = filesToProcess(files);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(QWExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
      QWAUtil.printIt(ftop);        
    }
  /**
    * List configured MaxPoolSize
    */ 
   public void getMaxPool() {
      if (poolMaxValues.isEmpty())
           return;
      QWUjdbc.createMaxPool(poolMaxValues);
 }      
    /**
     * List all BufferedCount
     */
    public void getBufferedItems() {
       if (files.length==0)
          files = findNodeFiles();
       if (files.length==0)
          return;
       QWUjdbc.newNumMap();
       QWUjdbc.setHdrCreated(false);
       QWUjdbc.resetNodePtrs();
       QWAUtil.initNodeList();
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           QWUreader.setNextFile(file.getAbsolutePath());
           recordFileNames(file.getAbsolutePath());
           //System.out.println("FILE " + file.getAbsolutePath());
           QWUjdbc.setInRecords(QWUreader.collectEntries(QWAGlobal.jdbc));
           ArrayList al = QWUjdbc.getJDBCitems(QWAGlobal.bufferdItem);
           if (al != null) {
              Iterator it = al.iterator();
              while (it.hasNext()){
                    QWAUtil.printIt((String)it.next()) ;
              }
          }    
      }
      QWAUtil.printIt("</table>");
      QWAUtil.destroyNodeList();
    }
  
   /**
    * display BadItemCount
    */ 
   public void getBadItems() {
       files = findNodeFiles();
       if (files.length==0)
           return;
       jdbc = new QWUjdbc();
       QWUjdbc.setHdrCreated(false);
       QWUjdbc.resetNodePtrs();
       QWAUtil.initNodeList();
       for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String fn = file.getName();
            QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
            QWUjdbc.setInRecords(QWUreader.collectEntries(QWAGlobal.jdbc));
            ArrayList al = QWUjdbc.getJDBCitems(QWAGlobal.badItem);
            if (al != null) {
                Iterator it = al.iterator();
                while (it.hasNext()){
                       QWAUtil.printIt((String)it.next()) ;
                     //     found=true;
                 }
            }    
       }
       QWAUtil.printIt("</table>");  
       QWAUtil.destroyNodeList();
//            Logger.getLogger(QWExtractor.class.getName()).log(Level.SEVERE, null, ex);
//        }
 }
    public void createPoolRequests() {
      if (poolRequests.isEmpty())
           return;
      QWUjdbc.createPoolRequests(poolRequests);       
    }
    /**
     * 
     */
    public void analyzePoolConnections(){
       files = findNodeFiles();
       if (files.length==0)
           return;
       jdbc = new QWUjdbc();
       QWUjdbc.setHdrCreated(false);
       QWUjdbc.resetNodePtrs();
       QWAUtil.initNodeList();
       for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String fn = file.getName();
            QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
            QWUjdbc.setInRecords(QWUreader.collectEntries(QWAGlobal.jdbc));
            ArrayList al = QWUjdbc.getJDBCitems(QWAGlobal.connections);
            if (al != null) {
                Iterator it = al.iterator();
                while (it.hasNext()){
                       QWAUtil.printIt((String)it.next()) ;
                     //     found=true;
                 }
            }    
       }
       QWAUtil.printIt("</table>");  
       QWAUtil.destroyNodeList();
//            Logger.getLogger(QWExtractor.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    public void getThreads(int th){
       if (qwuQueue == null)  
           qwuQueue = new QWUqueue();
       if (files.length==0)
          files = findNodeFiles();
       if (files.length==0)
       return;
       StringBuilder sb = new StringBuilder();
       sb.append("<h3>THREADS: Report when more than <font color='red'><b>").append(th);
       sb.append("</b></font> threads are active</h3>").append(nl);
       //sb.append(QWUhtml.comment).append(QWUhtml.recommendation).append(nl);
       sb.append(comment());
       QWAUtil.printIt(sb.toString());
       String hdr = QWUhtml.createQueueHdr("Thread Usage","'special2'","bt2");
//       sb.append(hdr).append(nl);
       QWAUtil.printIt(hdr);
       qwuQueue.initNodeList();
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.que);
           ArrayList al = qwuQueue.buildAEReprt(1,th,alQ);  
           if (al != null) {
              Iterator it = al.iterator();
              while (it.hasNext()){
                    QWAUtil.printIt((String)it.next()) ;
                     //     found=true;
              }
           }    
       }
       QWAUtil.printIt("</table></p>");  
       qwuQueue.destroyNodeList();
    }
    public void getDepth(int th){
       if (qwuQueue == null)
           qwuQueue = new QWUqueue();
       if (files.length==0)
          files = findNodeFiles();
       if (files.length==0)
       return;
       QWAUtil.printIt("<h3>QUEUEDEPTH: Report QueueDepth when more than <font color='red'><b>" + th + "</b></font> BPs on depth queue</h3>");
       //QWAUtil.printIt(QWUhtml.comment + QWUhtml.recommendation);
       QWAUtil.printIt(comment());
       
       //String hdr = QWUhtml.createDepthHdr();
       String hdr = QWUhtml.createQueueHdr("Queue Depth","'special3'","bt3");
       QWAUtil.printIt(hdr);
       qwuQueue.initNodeList();
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.que);
           
           ArrayList al = qwuQueue.buildAEReprt(5,th,alQ);  
           if (al != null) {
              Iterator it = al.iterator();
              while (it.hasNext()){
                    QWAUtil.printIt((String)it.next()) ;
                     //     found=true;
              }
           }    
       }
       QWAUtil.printIt("</table>"); 
       qwuQueue.destroyNodeList();
    }
    public void biggestQueuers(){
       if (qwuQueue == null)
           qwuQueue = new QWUqueue();
       if (files.length==0)
          files = findNodeFiles();
       if (files.length==0)
       return;
       StringBuilder sb = new StringBuilder();
       sb.append("<h3>QUEUES: Frequence of Depth pr. Queue / Node</h3>");
       sb.append("<p>The counters indicates how many times the sampling found BPs waiting for threads</p>");
       sb.append("<p>The format is frequence/% of total</p>");
       //sb.append(QWUhtml.comment + QWUhtml.recommendation);
       sb.append(comment());
       sb.append(QWUhtml.createBigQHdr());
       QWAUtil.printIt(sb.toString());
       /*
       int nmbrOfNodes = QWUjdbc.nhm.size();  
       if (nmbrOfNodes == 0)
           nmbrOfNodes = 1;
       */
       int nmbrOfNodes = QWAGlobal.nodeList.size();
       QWAGlobal.nOfNodes = nmbrOfNodes;
       QWAGlobal.queueDepth = new int[nmbrOfNodes][9];       
       //QWAGlobal.nodeNames = new String[nmbrOfNodes];
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.que);
           qwuQueue.buildQueFrequency(alQ);
          
       }
       qwuQueue.buildQhtml();
       QWAUtil.printIt("</table>");
       //build_pie();      
    }
    private void build_pie(){
      StringBuilder sb = new StringBuilder();
      sb.append("<br><ul class='legend'>" +
            "<li><span class='q1'></span>Q1</li>" +
            "<li><span class='q2'></span>Q2</li>" +
            "<li><span class='q3'></span>Q3</li>" +
            "<li><span class='q4'></span>Q4</li>" +
            "<li><span class='q5'></span>Q5</li>" +
            "<li><span class='q6'></span>Q6</li>" +
            "<li><span class='q7'></span>Q7</li>" +
            "<li><span class='q8'></span>Q8</li>" +
	    "<li><span class='q9'></span>Q9</li></ul>");
      QWAUtil.printIt(sb.toString());
      qwuQueue.buildQPieChart();
    }
    /**
     * Build a table with BP usage
     * @param th 
     */
    public void buildBPtable(int th){
      try {
        if (!checkSetup())
            return;
       StringBuilder sb = new StringBuilder(); 
       sb.append("<h3>WORKFLOWS: ALL BPs </h3>");
       sb.append("<h4>BP Name: name of the BP<br>"
                        +  "Observed: number of times observed during sampling<br>"
                        +  "Queue: processing queues (1-9). INLINE usage will will run on parent queue</h4>" );
       sb.append("<h4>Subject of sampling rate</h4>");
       
       //sb.append(QWUhtml.comment + QWUhtml.recommendation);
       sb.append(comment());
       sb.append(QWUhtml.createWFHdr(false));
       QWAUtil.printIt(sb.toString());
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.wfc);
           qwuQueue.buildBPusage(alQ,i+1);
       }
       qwuQueue.displayBPusage();
       QWAUtil.printIt("</table>");
      } catch(Exception exp) {
          exp.printStackTrace();
      }  
    }
   public void buildBPusage(int th){
      try {
        if (!checkSetup())
            return;
       StringBuilder sb = new StringBuilder(); 
       sb.append("<h3>WORKFLOWS: ALL BPs </h3>");
       sb.append("<h4>BP Name: name of the BP<br>"
                        +  "Observed: number of times observed during sampling<br>"
                        +  "Queue: processing queues (1-9). INLINE usage will will run on parent queue</h4>" );
       sb.append("<h4>Subject of sampling rate</h4>");
       
       //sb.append(QWUhtml.comment + QWUhtml.recommendation);
       sb.append(comment());
       sb.append(QWUhtml.createWFHdr(false));
       QWAUtil.printIt(sb.toString());
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.wfc);
           qwuQueue.buildBPusageSN(alQ,i+1);
       }
       qwuQueue.displayBPusage();
       QWAUtil.printIt("</table>");
      } catch(Exception exp) {
          exp.printStackTrace();
      }  
    }    
    public void buildSwiftBPUsage(){
        if (!checkSetup())
            return;
       StringBuilder sb =qwuQueue.buildSwiftBPusage(0);
       if (sb != null) {
           QWAUtil.printIt("<h3>WORKFLOWS - SWIFT MEFG</h3>");
           QWAUtil.printIt("<h4>BP Name: name of the BP<br>"
                        +  "Observed: number of times observed during sampling<br>");
           //QWAUtil.printIt(QWUhtml.comment + QWUhtml.recommendation);
           QWAUtil.printIt(comment());
           QWAUtil.printIt(QWUhtml.createWFHdr1());
       //StringBuilder sb =qwuQueue.buildSwiftBPusage(0);
           QWAUtil.printIt(sb.toString());
           QWAUtil.printIt("</table>");
       }    
    }
    public void buildSystemBPUsage(){
        if (!checkSetup())
            return;
       QWAUtil.printIt("<h3>WORKFLOWS - SYSTEM BP</h3>");
       QWAUtil.printIt("<h4>BP Name: name of the BP<br>"
                        +  "Observed: number of times observed during sampling<br>");
       //QWAUtil.printIt(QWUhtml.comment + QWUhtml.recommendation);
       QWAUtil.printIt(comment());
       QWAUtil.printIt(QWUhtml.createWFHdr1());
       StringBuilder sb = qwuQueue.buildSwiftBPusage(1);
       QWAUtil.printIt(sb.toString());
       QWAUtil.printIt("</table>");
    }    
   public void buildSwingBPUsage(){
        if (!checkSetup())
            return;
       QWAUtil.printIt("<h3>WORKFLOWS - " + project + " BP</h3>");
       QWAUtil.printIt("<h4>BP Name: name of the BP<br>"
                        +  "Observed: number of times observed during sampling<br>");
       //QWAUtil.printIt(QWUhtml.comment + QWUhtml.recommendation);
       QWAUtil.printIt(comment());
       QWAUtil.printIt(QWUhtml.createWFHdr1());
       StringBuilder sb = qwuQueue.buildSwiftBPusage(2);
       QWAUtil.printIt(sb.toString());
       QWAUtil.printIt("</table>");
    }  
    /**
     * Look at BPs over a 24 hour periode
     */
    public void buildBPperiodicUsageSN(){
      try {
        if (!checkSetup())
            return;
       StringBuilder sb = new StringBuilder(); 
       sb.append("<h3>WORKFLOWS: Distributed count and average run time (ms)</h3>");
       sb.append("<h4>Periode over 24 hours starting with the time of first sampling record</h4>");
       sb.append("<h4>BP Name: name of the BP<br>"
                        +  "Cnt: number of times observed during hour<br>"
                        +  "Act: Average time in millisecont this WFC was active this hour</h4>" );
       sb.append("<h4>Subject of sampling rate </h4>");
       
       //sb.append(QWUhtml.comment + QWUhtml.recommendation);
       //sb.append(comment());
       //sb.append(QWUhtml.createWFHdr(false));
       QWAUtil.printIt(sb.toString());
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.wfc);
           qwuQueue.buildBPperiodicUsageSN(alQ, i+1); 
       }
       //qwuQueue.displayBPusage();
       QWAUtil.printIt("</table>");
      } catch(Exception exp) {
          exp.printStackTrace();
      }  
        
    }
    public void getLongRunningBPs(int maxTime) {
        if (!checkSetup())
           return;
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>WORKFLOWS: Workflows active longer than <font color='red'><B>").append(maxTime).append("</B></font> seconds</h3>");
        sb.append("<h4>The DateTime value is when this workflowID were last encountered.</h4>");
        //sb.append(QWUhtml.comment + QWUhtml.recommendation);
        sb.append(comment());
       QWAUtil.printIt(sb.toString());
       listLongRunningBPs(maxTime);
    }
    public void getSelectedBPs(String bp) {
        if (!checkSetup())
           return;
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>WORKFLOWS: List Selected BP : <font color='red'><B>").append(bp).append("</B></font></h3>");
        sb.append("<h4>The DateTime value is when this workflowID were last encountered.</h4>");
        //sb.append(QWUhtml.comment + QWUhtml.recommendation);
        sb.append(comment());
       QWAUtil.printIt(sb.toString());
       listSingelBPs(bp);
    }
    
    //
    // List BPs which have been in the system longer than "maxTime" (default 24 hours)
    public void getHungBPs(int maxTime) {
        if (!checkSetup())
           return;
        int mTime = 60*60*24;
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>WORKFLOWS: Workflows that appear in hung state <font color='red'><B> 24</B></font> HOURS</h3>");
        sb.append("<h4>The DateTime value is when this workflowID were last encountered.</h4>");
        //sb.append(QWUhtml.comment + QWUhtml.recommendation);
        sb.append(comment());
        QWAUtil.printIt(sb.toString());
       listLongRunningBPs(mTime);
    }
    //
    // List BPs which has been running longer than "maxTime"
    //
    public void listLongRunningBPs(int maxTime){
        if (!checkSetup())
            return;
       QWAUtil.printIt(QWUhtml.createLongRunnerHdr());
       String prevNode ="nonode";
       String nodepart = null;
       String nodepart1 = null;
       ArrayList alQ = new ArrayList();
       boolean build = false;
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           nodepart = getNodeFromFileName(file);
           if ((i + 1 ) ==  files.length) 
              build = true;
           else {
              File file1 = files[i+1];
              nodepart1 = getNodeFromFileName(file1); 
              if (nodepart1.equalsIgnoreCase(nodepart))
                  build = false;
           }
           QWUreader.setNextFile(file.getAbsolutePath());
           prevNode = nodepart;
           QWUreader.collectMultEntries(QWAGlobal.wfc,alQ);
           //ArrayList alQ = QWUreader.collectEntries(QWAGlobal.wfc);
           if (build) {
              qwuQueue.buildLongRunners(alQ,maxTime);
              build = false;
              alQ.clear();
           }   
       }
       QWAUtil.printIt("</table>"); 
    }
    private void listSingelBPs(String bp) {
        if (!checkSetup())
            return;
       QWAUtil.printIt(QWUhtml.createLongRunnerHdr());
       String prevNode ="nonode";
       String nodepart = null;
       String nodepart1 = null;
       ArrayList alQ = new ArrayList();
       boolean build = false;
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           nodepart = getNodeFromFileName(file);
           if ((i + 1 ) ==  files.length) 
              build = true;
           else {
              File file1 = files[i+1];
              nodepart1 = getNodeFromFileName(file1); 
              if (nodepart1.equalsIgnoreCase(nodepart))
                  build = false;
           }
           QWUreader.setNextFile(file.getAbsolutePath());
           prevNode = nodepart;
           QWUreader.collectMultEntries(QWAGlobal.wfc,alQ);
           //ArrayList alQ = QWUreader.collectEntries(QWAGlobal.wfc);
           if (build) {
              qwuQueue.buildSingelBPList(alQ,bp);
              build = false;
              alQ.clear();
           }   
       }
       QWAUtil.printIt("</table>");        
    }
    // ---------------------------------------
    // Build Execution Pool configuration
    // ----------------------------------------
    public void buildConfig(){
       if (QWAGlobal.NodeconfigExist) {
           StringBuilder sb = new StringBuilder();
           sb.append("<h3>CONFIGURATION: A list of WFE thread pool configuration for each NODE</h3>");
           //sb.append(QWUhtml.comment + QWUhtml.recommendation);
           sb.append(comment());
           QWAUtil.printIt(sb.toString());
           sb = null; 
           QWUconfig ucfg = new QWUconfig();
           int ncnt = QWAGlobal.Nodeconfig.size();
           for (int i = 0; i < ncnt; i++) {
               String node = (String)QWAGlobal.Nodename.get(i);
               String cfg = (String)QWAGlobal.Nodeconfig.get(node);
               QWAUtil.printIt(ucfg.createConfigHtml(cfg,node));
           }
       }    
    }
    private String getNodeFromFileName(File f) {
      String fn = f.getName(); //need of a hack to handle multiple files from same node
      int ix = fn.indexOf("-");
      if (ix == -1)
          return null;
      return(fn.substring(0, ix));
    }
    public void displayEnvironment() throws Exception {
      HashMap hm = QWAGlobal.EnvList;
      if (hm == null || hm.size() == 0)
          return;
      String nodeKey;
      String strValue;
      StringBuilder sb = new StringBuilder();
      ArrayList alist = new ArrayList();
      sb.append("<h3>Environmental status</h3>");
      //sb.append(QWUhtml.comment + QWUhtml.recommendation);
      sb.append(comment());
      sb.append(table1);
      sb.append(tr);
      sb.append(thCls).append("NODE</th>").append(thCls).append("Arch").append("</th>");
      sb.append(thCls).append("Procs</th>").append(thCls).append("OSName").append("</th>");
      sb.append(thCls).append("OSVersion</th>").append(thCls).append("VMStart").append("</th>");
      sb.append(thCls).append("VM Uptime</th>").append(thCls).append("VM Version").append("</th>");
      sb.append(thCls).append("VM Vendor</th>").append(tre);
      int inx = 0;
      Set mapSet = (Set) hm.entrySet();
      Iterator mapIterator = mapSet.iterator();
      while (mapIterator.hasNext()) {
             Map.Entry mapEntry = (Map.Entry) mapIterator.next();
             nodeKey = (String) mapEntry.getKey();
             strValue = (String) mapEntry.getValue();
             int ix = strValue.indexOf("ENV");
             if (ix == -1)
                 continue;
             String str1 = strValue.substring(ix + 4);
             ix=1;
             ArrayList al = QWAUtil.tokenize(str1,";");             
             if (al.size() == 0) 
                 continue;
             sb.append(tr);
             sb.append(td).append(nodeKey).append(tde);
             sb.append(td).append((String) al.get(ix)).append(tde);
             sb.append(td).append((String) al.get(ix+2)).append(tde);
             sb.append(td).append((String) al.get(ix+4)).append(tde);
             sb.append(td).append((String) al.get(ix+6)).append(tde);
             sb.append(td).append((String) al.get(ix+8)).append(tde);
             sb.append(td).append((String) al.get(ix+10)).append(tde);
             sb.append(td).append((String) al.get(ix+12)).append(tde);
             sb.append(td).append((String) al.get(ix+14)).append(tde).append(tre);
             String ms = (String) al.get(ix+10);
             long uptime = Long.parseLong(ms); //System.currentTimeMillis();
             long days = TimeUnit.MILLISECONDS.toDays(uptime);
             uptime -= TimeUnit.DAYS.toMillis(days);
             long hours = TimeUnit.MILLISECONDS.toHours(uptime);
             uptime -= TimeUnit.HOURS.toMillis(hours);
             long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
             //uptime -= TimeUnit.MINUTES.toMillis(minutes);
             //long secondss = TimeUnit.MILLISECONDS.toSeconds(uptime);
             String s = String.format("%d days %d hrs %d min",days,hours,minutes);
             alist.add(nodeKey + " up for " + s );
             
//             System.out.println(nodeKey + "    " + strValue);
      }
      sb.append(tablee);
      QWAUtil.printIt(sb.toString());
      sb = new StringBuilder();
      sb.append("<ul>");
      for (int i = 0; i < alist.size(); i++) {
          sb.append("<li>").append(alist.get(i)).append("</li>");
      }
      sb.append("</ul>");
      QWAUtil.printIt(sb.toString());
  }
    
    //
    // Create a GRAPH of heap usage
    //
    public void getHeapGraph() {
     try {  
        if (!checkSetup())
            return;
       QWAUtil.printIt("<h3>HEAP: Heap Memory Graphs</h3>");
       //QWAUtil.printIt(QWUhtml.comment + QWUhtml.recommendation);
       QWAUtil.printIt(comment());
       ArrayList href = new ArrayList();
       QWMemgraph mem = new QWMemgraph();
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.mem);
           if (alQ != null)
              href.add((String)mem.buildHeapGraph(alQ));
       }       
       href.add((String)mem.buildHeapGraph(null)); 
     } catch (Exception exp) {
         exp.printStackTrace();
     }  
       //printHref(href);
    }
    public void getActiveGraph(int th) {
       if (!checkSetup())
            return;
       QWAUtil.printIt("<h3>ACTIVE THREADS : Active BP threads</h3>");
       //QWAUtil.printIt(QWUhtml.comment + QWUhtml.recommendation);
       QWAUtil.printIt(comment());
       QWQuegraph que = new QWQuegraph(th);
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
                 //System.out.println("FILE " + file.getAbsolutePath());
           ArrayList alQ = QWUreader.collectEntries(QWAGlobal.que);
           if (alQ != null)
               que.buildQueGraph(alQ,1);
       }       
       que.buildQueGraph(null,1);
    }
    public void getQueueGraph(int th) {
        getQueueGraph(th,-1);
    } 
   public void getQueueGraph(int th,int q) {
       if (!checkSetup())
            return;
       String qname = "All";
       if (q != -1)
           qname = String.valueOf(q+1);
       StringBuilder sb = new StringBuilder();
       sb.append("<h3>QUEUE WAITERS: BPs waiting for threads on Q= ").append(qname).append("</h3>");
       QWAUtil.printIt(sb.toString());
       //QWAUtil.printIt("<h3>QUEUE WAITERS: BPs waiting for threads </h3>");
       //QWAUtil.printIt(QWUhtml.comment + QWUhtml.recommendation);
       QWAUtil.printIt(comment());
       QWQuegraph que = new QWQuegraph(th);
       ArrayList alQ=null;
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
            alQ = QWUreader.collectEntries(QWAGlobal.que);
           if (alQ != null)
               que.buildQueGraph(alQ,5,q);
       }       
       que.buildQueGraph(null,5,q);
    }
   /**
    * This will line up most graphs created from a single file. (mem,active,depth0 
    * @param al 
    */
   public void buildSingleFileGraphs() {
       if (!checkSetup())
            return;
       StringBuilder sb = new StringBuilder();
       sb.append("<h3>Daily SnapShot Graphs </h3>");
       sb.append("<p>The graphs should show aprox. 24 hours of snapshots.<br>");
       sb.append("The graphs is placed in a table for easy compare from day to day<br>");
       sb.append("Click on a graph to expand</p>");
       QWAUtil.printIt(sb.toString());
       QWAUtil.printIt(comment());
       //QWQuegraph que = new QWQuegraph(th);
       //que.pageCtl=0;
       //StringBuilder htm = new StringBuilder();
       //htm.append(QWUhtml.table1);
       //
       //htm.append("<th>DATE</th><th>HEAP</th><th>BP</th><th>DEPTH</th><th>THREADS</th></tr>");
       String istr;
       for (int i = 0; i < files.length; i++) {
           File file = files[i];
           String fn = file.getName();
           QWUreader.setNextFile(file.getAbsolutePath());
           ArrayList alQ= new ArrayList();
           ArrayList mem_al = new ArrayList();
           ArrayList wfc_al = new ArrayList();
           ArrayList jdbcLst = new ArrayList();
           Hashtable bpNameList = new Hashtable();
           String st = null;
           String stdate=null;
           String sp = null;
           String node=null;
//           htm.append("<th>DATE</th><th>HEAP</th><th>BP frequnce</th><th>JDBC Connection reqs</th><th></th></tr>");
//           htm.append("<tr>");
           while ((istr = QWUreader.readLine()) != null) {
               int recType = QWUreader.getRecordType(istr);
               if (recType == QWAGlobal.que){
                   alQ.add((String)istr);
                   
               } else if (recType == QWAGlobal.mem) {
                   mem_al.add((String)istr); 
               } else if (recType == QWAGlobal.jdbc) {
                   jdbcLst.add((String)istr);                
               } else if (recType == QWAGlobal.wfc){
                   ArrayList al_record = QWAUtil.tokenize(istr);
                   if (st == null){
                       st = ((String)al_record.get(1) + "_" + (String)al_record.get(2)); 
                       stdate = (String)al_record.get(1);
                   }    
                   sp = st = ((String)al_record.get(1) + "_" + (String)al_record.get(2));
                   node = (String)al_record.get(0);
                   String wfid = (String)al_record.get(2+7);
                   String act = (String)al_record.get(2+15);
                   String bpn = (String)al_record.get(2+17);
                   if ((act.startsWith("-1")) || (act.startsWith("9"))) {   // adjust for corrupted input
                      bpn = (String)al_record.get(2+19);
                   } 
                   QWBPNameInfo bpObj = (QWBPNameInfo)bpNameList.get(bpn);
                   if (bpObj == null) {
                       bpObj = new QWBPNameInfo(bpn,wfid);
                       bpNameList.put(bpn,bpObj);
                   } else {
                       bpObj.increaseCount(wfid);
                   }
               }    
           }
           Iterator it = bpNameList.entrySet().iterator();
           ArrayList al_obj = new ArrayList();
           while (it.hasNext()) {
                  Map.Entry pair = (Map.Entry)it.next();
                  QWBPNameInfo bpObj = (QWBPNameInfo)pair.getValue();
                  al_obj.add(bpObj);
                  //System.out.println(pair.getKey() + " = " + pair.getValue());
           }
           Collections.sort(al_obj, new Comparator<QWBPNameInfo>() {
           @Override
             public int compare(QWBPNameInfo u1, QWBPNameInfo u2) {
                  return u2.count - u1.count;
             }
           }); 
           int sz = al_obj.size();
           ArrayList al_short = new ArrayList();
           for (int j = 0; j < 25; j++) {
               al_short.add(al_obj.get(j));
           }
           StringBuilder htm = new StringBuilder();
           htm.append(QWUhtml.table1);
           htm.append("<th>DATE</th><th>HEAP</th><th>Highest BP freq.</th><th>JDBC Connection Requests</th><th></th></tr>");
           htm.append("<tr>");           
           htm.append("<td>"+ stdate + "</td><td>");
           QWMemgraph heap = new QWMemgraph();
           heap.buildSingleHeapGraph(mem_al,htm,i);
           htm.append("</td><td>");
           QWBPGraph bps = new QWBPGraph();
           bps.createDataset(al_short);
           bps.buildBPGraph(al_short, htm, i,st,sp,node);
           htm.append("</td>,<td>");
           QWEjdbcGraph jdbcG = new QWEjdbcGraph();
           jdbcG.buildJdbcGraph(jdbcLst,htm,i);
           //htm.append("</td></tr>,<tr>");
           htm.append("</td></tr>");
           htm.append("</table>");
           htm.append(QWUhtml.table1);
           htm.append("<th>DATE</th><th>Total Waiting BPs</th><th>Waiting BP pr. Queue</th><th>Total Active WFE Threads</th><th>Active WFE Threads pr. Queue</th></tr>");
           htm.append("<tr>");   
           htm.append("<td>"+ stdate + "</td><td>");
           QWQuegraph que = new QWQuegraph(0);
           que.buildSingleGraph(alQ, 5, htm,i,"Aggregated",false);
           htm.append("</td><td>"); 
           que = new QWQuegraph(0);
           que.buildSingleGraph(alQ, 5, htm,i,"Individual",true);
           htm.append("</td><td>"); 
           que = new QWQuegraph(0);
           que.buildSingleGraph(alQ, 1, htm,i,"Agregated",false); 
           htm.append("</td><td>"); 
           que = new QWQuegraph(0);
           que.buildSingleGraph(alQ, 1, htm,i,"Individual",true);          
           htm.append("</td></tr>");
       //}  
       htm.append("</table>");
       QWAUtil.printIt(htm.toString());
       }
   }   
    public void printHref(ArrayList al){
      StringBuilder sb = new StringBuilder();
      sb.append(table);
      int inx = 0;
      int size = al.size();
      for (int i = 0; i < (al.size())/3; i++) {
           sb.append("<tr>").append(td).append(al.get(inx++)).append(tde);
           if ((inx+1) < size)
              sb.append(td).append(al.get(inx++)).append(tde);
           if ((inx+1) < size)
              sb.append(td).append(al.get(inx++)).append(tde);
           sb.append("</tr>");
      }     
      QWAUtil.printIt(sb.toString());  
      QWAUtil.printIt("</table>");
    }
    private String comment(){
        if (addComment) {
            return QWUhtml.comment + QWUhtml.recommendation; 
        }
        return "</p>";
    }
    private boolean checkSetup(){
      if (qwuQueue == null)
          qwuQueue = new QWUqueue();
      if (files.length==0)
          files = findNodeFiles();
       if (files.length==0)
           return false; 
       return true;
    }
    public void prolog() {
      QWAUtil.openOutFile(folder+outFolder+QWreport); 
      StringBuilder sb = QWUhtml.createHDR();
      QWAUtil.printIt(sb.toString());
      try {
          this.getfilesToProcess();
      } catch (Exception exp) {
          exp.printStackTrace();
      }    
    }
    public void epilog() {
        QWAUtil.printIt("</HTML>");
        QWAUtil.closeOut();
    }
    public void jdbcHdr(){
        //System.out.println(jdbcReport);
    }
    
    public static void main(String[] args) throws SQLException {
        QWExtractor qwu = new QWExtractor(null);
        qwu.prolog();
        qwu.jdbcHdr();
        qwu.getBadItems();
        qwu.getBufferedItems();
        qwu.getThreads(50);
        qwu.getDepth(150);
        qwu.biggestQueuers();
        qwu.buildBPtable(300);
        qwu.getLongRunningBPs(120*2);
        qwu.getHeapGraph();
        qwu.getActiveGraph(0);
        qwu.getQueueGraph(0);
        qwu.epilog();
    }    
}
