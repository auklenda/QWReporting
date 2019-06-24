/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Alf
 */
public class QWUjdbc extends QWUhtml{
   public static String [] colNames;
   public static int nmbrOfPools = 0;
   public static ArrayList inRecord =null;
   public static boolean singleHeader = true;
   private static boolean hdrCreated;
   public static NumericalHashMap nhm = new NumericalHashMap(); 
   public static NumericalHashMap nodeList = new NumericalHashMap(); 
   public static HashMap nodeToPool = new HashMap(); 
   static int bufferedPoolSize = 500;
    
   public QWUjdbc() {
   }
   public static void resetNodePtrs(){
       nodeToPool.clear();
   }
   public static void setHdrCreated(boolean hdrCreated) {
        QWUjdbc.hdrCreated = hdrCreated;
   }

   public static void newNumMap(){
       nhm = new NumericalHashMap(); 
   }
   public static void setSingleHeader(boolean singleHeader) {
        QWUjdbc.singleHeader = singleHeader;
   }
   public static ArrayList getInRecords(){
      return inRecord;    
   }
   public static void setInRecords(ArrayList inRecord) {
        QWUjdbc.inRecord = inRecord;
   }
   public static void collectMaxPoolEntries(){
       String inpr; 
       boolean found=false;
       inRecord = new ArrayList();
       while ((inpr = QWUreader.readLine(QWAGlobal.jdbc)) != null) {
              inRecord.add((String)inpr);
              found=true;
       }
       if (!found)
           inRecord=null;
          
   } 
   public static void collectEntries(){
       String inpr; 
       boolean found=false;
       inRecord = new ArrayList();
       while ((inpr = QWUreader.readLine(QWAGlobal.jdbc)) != null) {
              inRecord.add((String)inpr);
              found=true;
       }
       if (!found)
           inRecord=null;
          
   }
   
   /**
    * 
    * @param action 1= badItems 2= buffered 3= maxPool 4=connections
    * @return 
    */
   public static ArrayList getJDBCitems(int action){
       return (getJDBCitems(inRecord,action));
   }
   public static ArrayList getJDBCitems(ArrayList list,int action){
     if (list == null)
         return null;
     ArrayList al = QWUreader.tokenize((String)list.get(0));
     int nmbrOfPools = Integer.parseInt((String)al.get(4));
     String node = (String)al.get(0);
     String date =(String)al.get(1);
     String time =(String)al.get(2);
     //if (!nhm.containsKey(node)){
     if (!nodeToPool.containsKey(node)){    
         //nhm.put(node,0);
         NumericalHashMap poolBadCount = new NumericalHashMap();
         int ofs = 5;
         for (int i = 0; i < nmbrOfPools ; i++) {
             String pName = (String)al.get(ofs);
             poolBadCount.put(pName,0);
             ofs += 8;
         }
         nodeToPool.put(node,poolBadCount);
         if (!nodeList.containsKey(node)) {
             int l = nodeList.size();
             nodeList.put(node,l);
             QWAGlobal.nodeNames[l]=node;
         }    
     }
     //System.out.println("NODE " + node);
     //int nmbrOfPools = Integer.parseInt((String)al.get(4));
     int offset = 5;
     int [] badItem = new int[nmbrOfPools+1];
     ArrayList alResult = new ArrayList();
     StringBuilder sb = new StringBuilder();
     if (!hdrCreated) {
         String thCls = "<th class='bkr'>";
         if (action == QWAGlobal.badItem) {
             sb.append("<h3>JDBC: BadItemCount</h3>");
             sb.append("<ul><li>").append("A result from failed -testOnReserve-</il></ul>");
         } else if (action == QWAGlobal.bufferdItem) {
             sb.append("<h3>JDBC: Buffered count.</h3>");
             sb.append("<ul><li>").append("Pools with Buffered count do not have sufficient connections (MaxSize)");
             sb.append("</il></ul>");
         }  else {
             sb.append("<h3>JDBC: Connections from in JDBC Pool</h3>");
             sb.append("<ul><li>").append("This table will indicate if any of the the JDBC pool are leaking");
             sb.append("</il></ul>");
             QWAGlobal.cHelp = new long[nmbrOfPools];
             for (int i = 0; i < nmbrOfPools-1; i++) {
                 QWAGlobal.cHelp[i]=0l;
             }
         }
         //sb.append(QWUhtml.comment + "\n" + QWUhtml.recommendation);
         sb.append("</p>");
         sb.append("\n").append(QWExtractor.table); 
         sb.append("\n<tr>").append(thCls).append("NODE</th>").append(thCls).append("DateTime</th>");
         for (int i=0 ; i < nmbrOfPools ; ++i){
             String tmp = (String)al.get(offset);
             sb.append(thCls).append(tmp.substring(1)).append("</th>");
             offset += 8;
         }
         sb.append("</tr>");
         if (action == QWAGlobal.connections){
           // sb = new StringBuilder();
             sb.append("\n<tr>").append(thCls).append("</th>").append(thCls).append("</th>");
             String jdbcStr = (String)QWExtractor.poolMaxValues.get(node);
             al.clear();
             al = QWUreader.tokenize(jdbcStr);
                    //sb.append(td).append(node).append(tde);
             offset = 7;
             for (int i=0 ; i < nmbrOfPools ; ++i){
                 int tmp = Integer.parseInt((String)al.get(offset));
                 tmp = tmp - bufferedPoolSize;
                 sb.append(thCls).append(tmp).append(" (Max)").append(the);
                 offset += 8;
             }
             sb.append(tre);             
         }    
         alResult.add(sb.toString());
         hdrCreated=true;
     }    
     Iterator it = list.iterator();
     while (it.hasNext()) {
         sb = new StringBuilder();
         String record = (String)it.next();
         al = QWUreader.tokenize(record);
         node = (String)al.get(0);
         date =(String)al.get(1);
         time =(String)al.get(2);
         sb.append("<tr><td>");
//         if (QWAUtil.nodeInList(node))  //  we dont want to print NODE name on every line
//            sb.append(" ");
//         else           
         sb.append(node);
         sb.append("</td><td>").append(date).append("  ").append(time).append("</td>");
         boolean found=false;
         int poolName = 5;
         int field = 7;
         if (action == QWAGlobal.badItem)
             field = 5;
         else if (action == QWAGlobal.connections)
             field = 1;
         offset=5;
         NumericalHashMap poolBadCount = (NumericalHashMap)nodeToPool.get(node);
         for (int i=0 ; i < nmbrOfPools ; ++i){
              long tmp = Long.parseLong((String)al.get(offset+field));
              String pName = (String) al.get(offset);
              offset+= 8;
              long value = poolBadCount.getInt(pName);
              if (action == QWAGlobal.badItem || action == QWAGlobal.bufferdItem){
                 if (tmp > value) { 
                     sb.append("<td bgcolor='PINK'>").append(tmp);
                     found = true;
                     poolBadCount.put(pName,tmp);
                 } else {
                     sb.append("<td>").append(tmp);
                 }
                 sb.append("</td>");
             } else if (action == QWAGlobal.connections){
                 sb.append("<td>");
                 if (tmp != QWAGlobal.cHelp[i]) {
                     QWAGlobal.cHelp[i] = tmp;
                     sb.append(tmp);
                     found=true;
                 }
                 sb.append("</td>");
             }
         }
         if (found) {
            sb.append("</tr>"); 
            alResult.add(sb.toString()); 
         }
     }
     return alResult;
   } 
   public void retrieveJDBCentries(String actionStr,int action) {
     String inpr = null;
     int poolField = action;
     if (action == 7) {
         poolField = 2;
     }
     if (inRecord == null){
         inRecord = new ArrayList();
     // Get all JDBC records
        while ((inpr = QWUreader.readLine(QWAGlobal.jdbc)) != null) {
              inRecord.add((String)inpr);
        }
     }   
     int rows = inRecord.size();
     ArrayList al = QWUreader.tokenize((String)inRecord.get(0));
     int nmbrOfPools = Integer.parseInt((String)al.get(4));
     int offset = 5;
     colNames = new String[nmbrOfPools+1];
     colNames[0] = "Time";
     // Build PoolName header
     for (int i=0 ; i < nmbrOfPools ; ++i){
         String tmp = (String)al.get(offset);
         colNames[i+1] = tmp.substring(1);
         offset += 8;
     }
     if (action == 5) {
     }
     Object [][] rowData = new Object[rows][nmbrOfPools+1];
     int rowIndex = 0;
     //int [] previous = new int[nCols];
     long [] previous = new long[nmbrOfPools];
     Iterator it = inRecord.iterator();
     while (it.hasNext()) {
         String record = (String)it.next();
         al = QWUreader.tokenize(record);
         rowData[rowIndex][0] = al.get(2);
         offset = 6 + poolField;
         if (action == 7 ) {      // ACTIVE items
            for (int i=0 ; i < nmbrOfPools ; ++i){
                 if (rowIndex == 0) {
                    //rowData[rowIndex][i+1] = (String)al.get(offset);
                    rowData[rowIndex][i+1] = "0";
                    previous[i]  = Long.parseLong((String)al.get(offset));
                 } else  {
                    long tmp =  Long.parseLong((String)al.get(offset));
                    long tmp1 = previous[i];
                    rowData[rowIndex][i+1]  = String.valueOf(tmp - tmp1);
                    long data =  tmp - tmp1;
//                    createTestJDBCDataset(ts[i],record,data);
                    previous[i] = tmp;
                 }
                 offset += 8;
           }
         } else if (action == 1) {   // deduct Buffered part because it creates a connection
                    for (int i=0 ; i < nmbrOfPools ; ++i){
                        int buffered = Integer.parseInt((String)al.get(offset));
                        if (buffered >= 500)
                            buffered = buffered -500;
                        rowData[rowIndex][i+1]  = String.valueOf(buffered);
                        offset += 8;
                    }
         } else {
            for (int i=0 ; i < nmbrOfPools ; ++i){
                 rowData[rowIndex][i+1]  = (String)al.get(offset);
                 offset += 8;
            }
         }
         rowIndex += 1;
     }
     return;
   }    
   public static void createPoolRequests(HashMap maxpool){
      int offset = 5;
      StringBuilder sb = new StringBuilder(); 
      sb.append("<h3>JDBC: Number of Requests pr. Pool</h3>");
      sb.append("<ul><li>").append("Showing the pool usage with regard to connection requests");
      sb.append("</il></ul>");
      //sb.append(QWUhtml.comment + "\n" + QWUhtml.recommendation);
      sb.append("</p>");
      sb.append("\n").append(QWExtractor.table); 
      sb.append("\n<tr>").append(thCls).append("NODE</th>");
      Set keys = maxpool.keySet();
      Iterator it = keys.iterator();
      String kNode = (String)it.next();
      ArrayList al = QWUreader.tokenize((String)maxpool.get(kNode));      
      //ArrayList al = QWUreader.tokenize((String)maxpool.get("NODE1"));
      int nmbrOfPools = Integer.parseInt((String)al.get(4));
      for (int i=0 ; i < nmbrOfPools ; ++i){
             String tmp = (String)al.get(offset);
             sb.append(thCls).append(tmp.substring(1)).append("</th>");
             offset += 8;
      }
      sb.append(tre).append(tr);
      Set mapSet = (Set) maxpool.entrySet();
      Iterator mapIterator = mapSet.iterator();
      while (mapIterator.hasNext()) 
      {
        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
        String node = (String) mapEntry.getKey();
        String value = (String) mapEntry.getValue();
        //System.out.println(value);
        al.clear();
        al = QWUreader.tokenize(value);
        sb.append(td).append(node).append(tde);
          offset = 8;
          for (int i=0 ; i < nmbrOfPools ; ++i){
             String tmp = (String)al.get(offset);
             sb.append(td).append(tmp).append(tde);
             offset += 8;
          }
          sb.append(tre);
      }
      sb.append("</table>");
      QWAUtil.printIt(sb.toString());
   }    
   public static void createMaxPool(Properties maxpool){
      int offset = 5;
      StringBuilder sb = new StringBuilder(); 
      sb.append("<h3>JDBC: Max Pool Size</h3>");
      sb.append("<ul><li>").append("Configured pool sizes - (The bufferedPoolSize is assumed to be 500)");
      sb.append("</il></ul>");
      //sb.append(QWUhtml.comment + "\n" + QWUhtml.recommendation);
      sb.append("</p>");
      sb.append("\n").append(QWExtractor.table); 
      sb.append("\n<tr>").append(thCls).append("NODE</th>");
      Enumeration keys = maxpool.keys();
      
      String kNode = (String)keys.nextElement();
      ArrayList al = QWUreader.tokenize((String)maxpool.get(kNode));
      //ArrayList al = QWUreader.tokenize((String)maxpool.get("NODE1"));
      int nmbrOfPools = Integer.parseInt((String)al.get(4));
      for (int i=0 ; i < nmbrOfPools ; ++i){
             String tmp = (String)al.get(offset);
             sb.append(thCls).append(tmp.substring(1)).append("</th>");
             offset += 8;
      }
      sb.append(tre).append(tr);
      for(Entry<Object, Object> x : maxpool.entrySet()) {
          String node = (String)x.getKey();
          al.clear();
          al = QWUreader.tokenize((String)x.getValue());
          sb.append(td).append(node).append(tde);
          offset = 7;
          for (int i=0 ; i < nmbrOfPools ; ++i){
             int tmp = Integer.parseInt((String)al.get(offset));
             tmp = tmp - bufferedPoolSize;
             sb.append(td).append(tmp).append(tde);
             offset += 8;
          }
          sb.append(tre);
      }
      sb.append("</table>");
      QWAUtil.printIt(sb.toString());
   } 
   public static void analyzePoolConnections(Properties maxpool){
       
   }
}      
      
       
 