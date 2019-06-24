/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;



/**
 *
 * @author aauklnd
 * 
 */
public class QWUqueue {
    String td = "<td>";
    String tde = "</td>";
    String tr = "<tr>";
    String tre = "</tr>";
    String trbgr ="<tr class=line1>";
    String col1 = "<td class=col1>";
    String col2 = "<td class=col2>";
    String col3 = "<td class=col3>";
    String nl = "\n";
    String[] swiftbp = {"SwiftN","SWIFTN","Swift","SWIFTD"};
    String[] systembp = {"Schedule_Purge","Schedule_Index","Recover.bpml","Schedule_Backup","Schedule_Message",
                         "Schedule_Asso","AFTPurge","PS_PurgeFile","Schedule_BPR","Schedule_BPExp","Schedule_Docum",
                         "Schedule_BPL"};  
    String[] swingbp ={"SWG","swg","ETECE"};
    NumericalHashMap nhm = new NumericalHashMap();
    NumericalHashMap wfNameAndCount = new NumericalHashMap();
    Hashtable wfidAndName = new Hashtable();
    Hashtable wfidAndQ = new Hashtable();
    Properties wfNodeHolder = new Properties();
    Hashtable bpNames = new Hashtable();
    NumericalHashMap nodesTOque = new NumericalHashMap();
    ArrayList nodeList = null;
    
    
    public long getMaxRecords(String node , String type) {
        NumericalHashMap hm = (NumericalHashMap) QWAGlobal.recordCount.get(node);
        if (hm == null)
            return -1;
        return hm.getLong(type);
    }
    private int [][] buildQrecord(ArrayList al,int inx){
       int [][] q = new int[9][6];
       int alIndx = 6;
       for (int i = 0; i < 9; i++) {
           for (int j = 0; j < 6; j++) {
               try {
                  q[i][j] = Integer.parseInt((String)al.get(alIndx++));
               } catch (Exception exp) {
                   exp.printStackTrace();
               }
          }
          alIndx +=2;
       }     
       return q;
    }
    public ArrayList initNodeList(){
        nodeList = new ArrayList();
        return(nodeList);
    }
    private boolean nodeInList(String node){
        if (!nodeList.contains((String)node)) {
            nodeList.add((String)node);
            return false;
        }
        return true;
    }
    public void destroyNodeList(){
        nodeList = null;
    }
    private void recordQUEprNode(String node,int recs) {
       if (!nodesTOque.containsKey(node))
            nodesTOque.put(node, recs);
       else {
           long n = nodesTOque.getLong(node);
           nodesTOque.remove(node);
           nodesTOque.put(node, n+recs);
       }
    }
    
    private boolean bpType(int type,String name) {
     boolean ret = false;
     String[] strArray = null;
     switch (type) {
         case 0: strArray = swiftbp;
             break;
         case 1: strArray = systembp;
             break;
         case 2: strArray = swingbp;
             break;
         default:
             break;
    }
     int lng = strArray.length;
     for (int i = 0; i < lng; i++) {
          String string = strArray[i];
          //int ix = string.indexOf(name);
          int ix = name.indexOf(string);
          if (ix > -1) {
              ret = true;
              break;
          }
     }
     return ret;
    }
    /*
     *   Create a matrix with all parms for this sample line
     *   1 - 9 queues : 0=Minpool, 1=Used, 2=Calculated , 3=Pool, 4=MaxPoool, 5=Depth
     *   Q1 [0] ... Q9 [8]
     *   action = 1 thread table   ---- action=2  queue depth 
    */ 
    public ArrayList buildAEReprt(int action, int th, ArrayList list) {
     int [][] queue = new int[9][6] ; 
     if (list == null)
         return null;
     ArrayList alResult = new ArrayList();
     Iterator it = list.iterator();
     while (it.hasNext()) { 
       String str = (String)it.next();  
       ArrayList al = QWUreader.tokenize(str);
       String node = (String)al.get(0);
       String date =(String)al.get(1);
       String time =(String)al.get(2);
       if (!nhm.containsKey(node)) {
           nhm.put(node,0);
       }    
       int alIndx = 6;
       for (int i = 0; i < 9; i++) {
           for (int j = 0; j < 6; j++) {
               try {
                  queue[i][j] = Integer.parseInt((String)al.get(alIndx++));
               } catch (Exception exp) {
                   exp.printStackTrace();
               }
          }
          alIndx +=2;
       }     
       StringBuilder sb = new StringBuilder();
       int threads=0;
       int depths = 0;
       int threshold = 0;
       for (int i = 0; i < 9; i++) {
           threads += queue[i][1];
           depths +=  queue[i][5];  
       }
       if (action == 1)
           threshold = threads;
       else
           threshold = depths;
       // 
       if ( threshold >= th) {
          if (nodeInList(node)) node = "";
          sb.append(tr);
          sb.append(td).append(node).append(tde).append(td).append(date).append("  ").append(time).append(tde);
          for (int i = 0; i < 9; i++) {
              /*
               if (action==1 ) {
                   if (queue[i][1]>0) {
                       if ((queue[i][5] > 0) && (queue[i][1] < queue[i][2])) 
                           sb.append(QWUhtml.isRedbg);
                       else
                           sb.append(QWUhtml.sumCls);
                   } else
                       sb.append(td);
                   sb.append(queue[i][1]).append(tde);
               } else { 
                   if (queue[i][1]>0)
                       sb.append(QWUhtml.sumCls);
                   else
                       sb.append(td);
                   sb.append(queue[i][1]).append(tde);
               }   
              */
                   if (queue[i][1]>0) {
                       if ((queue[i][5] > 0) && (queue[i][1] < queue[i][2])) 
                           sb.append(QWUhtml.isRedbg);
                       else
                           sb.append(QWUhtml.sumCls);
                   } else
                       sb.append(td);
                   sb.append(queue[i][1]).append(tde);              
               sb.append(td).append(queue[i][2]).append(tde);  // Calculated
               sb.append(td).append(queue[i][4]).append(tde);  // MaxPo 
               if (action==1){
                  if (queue[i][1]>0 && queue[i][5]>0){
                      sb.append(QWUhtml.bgcol1);
                  } else {
                      sb.append(td);                
                  }
                  sb.append(queue[i][5]).append(tde);  
       
               } else {
                  if (queue[i][5]>0)
                      sb.append(QWUhtml.bgcol1);                 
                  else 
                      sb.append(td);
                  sb.append(queue[i][5]).append(tde);
               }   
             }
             float fl = (threads *100) / 65;
             String pct = String.format("%.0f%%", fl);
             if (action ==1)  
                sb.append(QWUhtml.sumCls).append(threads).append(" (" + pct + ")").append(tde).append(td).append(depths).append(tde);
             else 
                sb.append(td).append(threads).append(" (" + pct + ")").append(tde).append(QWUhtml.bgcol1).append(depths).append(tde);
                 //sb.append(QWUhtml.sumCls).append(depths).append(tde).append(td).append(threads).append(tde); 
             sb.append(tre);
             alResult.add(sb.toString());
          }
     } // end while  
     return alResult;
    }
    
    public void buildQueFrequency(ArrayList list) {
       int [][] queue = new int[9][6]; 
       if (list == null)
           return;
        ArrayList alResult = new ArrayList();
        String s = (String)list.get(0);
        String n = s.substring(0,5);
        recordQUEprNode(n,list.size());
        Iterator it = list.iterator();
        int nodeInx = 0;
        int cnt = 0;
      try {  
        while (it.hasNext()) { 
            String str = (String)it.next();  
            ArrayList al = QWUreader.tokenize(str);
            String node = (String)al.get(0);
            try {
                if (QWUjdbc.nodeList!= null && QWUjdbc.nodeList.size() > 0 )
                    nodeInx = QWUjdbc.nodeList.getInt(node);
           } catch( Exception exp){
               exp.printStackTrace();
           }
            queue = buildQrecord(al,nodeInx); 
            for (int i = 0; i < 9; i++) {
                if (queue[i][5] > 0){
                    QWAGlobal.queueDepth[nodeInx][i] += 1;
                }    
            }
            System.out.println("");
        }
           } catch( Exception exp){
               exp.printStackTrace();
           }
    }
    public void buildQhtml(){
        StringBuilder sb=null;
        int nodeLng = QWUjdbc.nodeList.size();
        for (int i = 0; i < nodeLng; i++) {
            sb = new StringBuilder();
            //double totQue = nodesTOque.getLong(QWAGlobal.nodeNames[i]);
            double totQue = getMaxRecords(QWAGlobal.nodeNames[i],"que");
            sb.append(tr);
            sb.append(td).append(QWAGlobal.nodeNames[i]).append(tde);
            sb.append(td).append(totQue).append(tde);
            double total = 0;
            DecimalFormat df = new DecimalFormat("##.#%");
            for (int j = 0; j < 9; j++) {
                total += QWAGlobal.queueDepth[i][j];
                double q = (float)(float)(QWAGlobal.queueDepth[i][j]*100/ totQue);
                String sq = String.format("%.1f", q);
                sb.append(td).append(QWAGlobal.queueDepth[i][j]).append("/").append(sq).append("%").append(tde);
           }
           sb.append(td).append(total).append(tde);
           double prct = (float) ((total*100) / totQue);
           String sprct = String.format("%.1f", prct);  //df.format(prct);
           sb.append(td).append(sprct).append(tde);
           sb.append(tre); 
           QWAUtil.printIt(sb.toString());
        }
    }
    public void buildQPieChart(){
        StringBuilder sb=null;
        QWAUtil.printIt("<br><canvas id='can' width='200' height='200'></canvas>\n");
        int nodeLng = QWUjdbc.nodeList.size();
        for (int i = 0; i < nodeLng; i++) {
            sb = new StringBuilder();
            //double totQue = nodesTOque.getLong(QWAGlobal.nodeNames[i]);
            sb.append("<script type='text/javascript'>").append("\n");
            sb.append(" CreatePieChart(");
            for (int j = 0; j < 9; j++) {
                if (j < (9-1))
                   sb.append(QWAGlobal.queueDepth[i][j]).append(",");
                else 
                   sb.append(QWAGlobal.queueDepth[i][j]);
           }
           sb.append(")").append("</script>");
           QWAUtil.printIt(sb.toString());
        }
    }    
    /**
     * Entries : wfc records from a single file
     * iist1 wf_id,name
     * list2 name,q
     * list3 name,count (numeric)
     * @param list 
     */
    public void buildBPusage(ArrayList list,int nbrOfiles) {
        if (list == null) return;
        NumericalHashMap wfNameAndCount = null;
        Hashtable wfidAndName = null;
        Hashtable wfidAndQ = null;
        NumericalHashMap swiftNameAndCount = null;
        NumericalHashMap systemNameAndCount = null;
        String node = null;
        ArrayList al = QWUreader.tokenize((String)list.get(0));
        node = (String)al.get(0);
        if (!wfNodeHolder.containsKey(node)) {
            wfNameAndCount = new NumericalHashMap();
            wfidAndName = new Hashtable();
            wfidAndQ = new Hashtable();
            //swiftNameAndCount = new NumericalHashMap();
            //systemNameAndCount = new NumericalHashMap();
                //wfNodeHolder.put(node + "sw",swiftNameAndCount);
                //wfNodeHolder.put(node + "sy",systemNameAndCount);
        } else {
                wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(node);
                wfidAndName = (Hashtable)wfNodeHolder.get(node + "wn");
                wfidAndQ = (Hashtable)wfNodeHolder.get(node + "wq");
        }
      
        Iterator it = list.iterator();
        while (it.hasNext()) { 
            String str = (String)it.next();  
            al = QWUreader.tokenize(str);
/*
            node = (String)al.get(0);
            if (!wfNodeHolder.containsKey(node)) {
                wfNameAndCount = new NumericalHashMap();
                wfidAndName = new Hashtable();
                wfidAndQ = new Hashtable();
                //swiftNameAndCount = new NumericalHashMap();
                //systemNameAndCount = new NumericalHashMap();
                wfNodeHolder.put(node,wfNameAndCount);
                wfNodeHolder.put(node + "wn",wfidAndName);
                wfNodeHolder.put(node + "wq",wfidAndQ);
                //wfNodeHolder.put(node + "sw",swiftNameAndCount);
                //wfNodeHolder.put(node + "sy",systemNameAndCount);
            } else {
                wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(node);
                wfidAndName = (Hashtable)wfNodeHolder.get(node + "wn");
                wfidAndQ = (Hashtable)wfNodeHolder.get(node + "wq");
            }
            */ 
            String q = (String)al.get(5);
            String wfid = (String)al.get(9);
            String bpName = (String)al.get(21);
            if  ((bpName.equals("BP") || bpName.equals("wfTransporter")))  //  drop bad record
                continue;
            if (!wfidAndName.containsKey(wfid)){
                wfidAndName.put(wfid,bpName);
                if (!bpNames.contains(bpName))
                    bpNames.put(bpName,"");
                if (!wfNameAndCount.containsKey(bpName)){
                    wfNameAndCount.put(bpName,1);
                    wfidAndQ.put(bpName,q);
                } else {
                    int n = wfNameAndCount.getInt(bpName);
                    wfNameAndCount.remove(bpName);
                    wfNameAndCount.put(bpName, n+1);
                    String oldq = (String) wfidAndQ.get(bpName);
                    int i = oldq.indexOf(q);
                    if (i == -1) {
                        q = oldq + ":" + q;
                        wfidAndQ.remove(bpName);
                        wfidAndQ.put(bpName,q);
                    }    
                }
            }
            /*
            if (bpType(0,bpName)) {
                if (!swiftNameAndCount.containsKey(bpName)){
                    swiftNameAndCount.put(bpName,1);
                } else {
                    int n = swiftNameAndCount.getInt(bpName);
                    swiftNameAndCount.remove(bpName);
                    swiftNameAndCount.put(bpName, n+1);  
                }    
            } else {
                if (bpType(1,bpName)) {
                    System.out.println(bpName);
                   if (!systemNameAndCount.containsKey(bpName)){
                       systemNameAndCount.put(bpName,1);
                   } else {
                       int n = systemNameAndCount.getInt(bpName);
                       systemNameAndCount.remove(bpName);
                       systemNameAndCount.put(bpName, n+1);                
                   }
                }   
            }
            */ 
        }
        wfNodeHolder.put(node,wfNameAndCount);
        wfNodeHolder.put(node + "wn",wfidAndName);
        wfNodeHolder.put(node + "wq",wfidAndQ);
        //wfNodeHolder.put(node + "sw",swiftNameAndCount);
        //wfNodeHolder.put(node + "sy",systemNameAndCount);
    }
    /**
     * Build BP usage for SINGLE NODE only
     * @param list
     * @param nbrOfiles 
     */
    public void buildBPusageSN(ArrayList list,int nbrOfiles) {
        if (list == null) return;
        NumericalHashMap wfNameAndCount = null;
        Hashtable wfidAndName = null;
        Hashtable wfidAndQ = null;
        NumericalHashMap swiftNameAndCount = null;
        NumericalHashMap systemNameAndCount = null;
        String node = null;
        ArrayList al = QWUreader.tokenize((String)list.get(0));
        node = (String)al.get(0);
        if (!wfNodeHolder.containsKey(node)) {
            wfNameAndCount = new NumericalHashMap();
            wfidAndName = new Hashtable();
            wfidAndQ = new Hashtable();
            //swiftNameAndCount = new NumericalHashMap();
            //systemNameAndCount = new NumericalHashMap();
                //wfNodeHolder.put(node + "sw",swiftNameAndCount);
                //wfNodeHolder.put(node + "sy",systemNameAndCount);
        } else {
                wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(node);
                wfidAndName = (Hashtable)wfNodeHolder.get(node + "wn");
                wfidAndQ = (Hashtable)wfNodeHolder.get(node + "wq");
        }
      
        Iterator it = list.iterator();
        while (it.hasNext()) { 
            String str = (String)it.next();  
            al = QWUreader.tokenize(str);
/*
            node = (String)al.get(0);
            if (!wfNodeHolder.containsKey(node)) {
                wfNameAndCount = new NumericalHashMap();
                wfidAndName = new Hashtable();
                wfidAndQ = new Hashtable();
                //swiftNameAndCount = new NumericalHashMap();
                //systemNameAndCount = new NumericalHashMap();
                wfNodeHolder.put(node,wfNameAndCount);
                wfNodeHolder.put(node + "wn",wfidAndName);
                wfNodeHolder.put(node + "wq",wfidAndQ);
                //wfNodeHolder.put(node + "sw",swiftNameAndCount);
                //wfNodeHolder.put(node + "sy",systemNameAndCount);
            } else {
                wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(node);
                wfidAndName = (Hashtable)wfNodeHolder.get(node + "wn");
                wfidAndQ = (Hashtable)wfNodeHolder.get(node + "wq");
            }
            */ 
            String q = (String)al.get(5);
            String wfid = (String)al.get(9);
            String bpName = (String)al.get(21);
            if  ((bpName.equals("BP") || bpName.equals("wfTransporter")))  //  drop bad record
                continue;
            if (!wfidAndName.containsKey(wfid)){
                wfidAndName.put(wfid,bpName);
                if (!bpNames.contains(bpName))
                    bpNames.put(bpName,"");
                if (!wfNameAndCount.containsKey(bpName)){
                    wfNameAndCount.put(bpName,1);
                    wfidAndQ.put(bpName,q);
                } else {
                    int n = wfNameAndCount.getInt(bpName);
                    wfNameAndCount.remove(bpName);
                    wfNameAndCount.put(bpName, n+1);
                    String oldq = (String) wfidAndQ.get(bpName);
                    int i = oldq.indexOf(q);
                    if (i == -1) {
                        q = oldq + ":" + q;
                        wfidAndQ.remove(bpName);
                        wfidAndQ.put(bpName,q);
                    }    
                }
            }
            /*
            if (bpType(0,bpName)) {
                if (!swiftNameAndCount.containsKey(bpName)){
                    swiftNameAndCount.put(bpName,1);
                } else {
                    int n = swiftNameAndCount.getInt(bpName);
                    swiftNameAndCount.remove(bpName);
                    swiftNameAndCount.put(bpName, n+1);  
                }    
            } else {
                if (bpType(1,bpName)) {
                    System.out.println(bpName);
                   if (!systemNameAndCount.containsKey(bpName)){
                       systemNameAndCount.put(bpName,1);
                   } else {
                       int n = systemNameAndCount.getInt(bpName);
                       systemNameAndCount.remove(bpName);
                       systemNameAndCount.put(bpName, n+1);                
                   }
                }   
            }
            */ 
        }
        wfNodeHolder.put(node,wfNameAndCount);
        wfNodeHolder.put(node + "wn",wfidAndName);
        wfNodeHolder.put(node + "wq",wfidAndQ);
        //wfNodeHolder.put(node + "sw",swiftNameAndCount);
        //wfNodeHolder.put(node + "sy",systemNameAndCount);
    }
    
    public void displayBPusage(){
      int div = 0;
      Set set = bpNames.entrySet();
      Iterator it = set.iterator();
    try {  
      while (it.hasNext()) {
          Map.Entry entry = (Map.Entry) it.next();
          String bp = (String)entry.getKey();
            //System.out.println(entry.getKey() + " : " + entry.getValue());
          StringBuilder sb = new StringBuilder();
          if ((div % 2) > 0)
              //sb.append(trbgr);
              sb.append(tr);
          else 
              sb.append(tr);
          div++;
          sb.append(td).append(bp).append(tde);
          for (int i = 0; i < QWAGlobal.nOfNodes; i++) {
              String node = QWAGlobal.nodeNames[i];
              ArrayList ll = getBPparms(node,bp,0);
              sb.append(td).append(ll.get(0)).append(tde).append(td).append(ll.get(1)).append(tde);
          }
          sb.append(tre);
          QWAUtil.printIt(sb.toString());
      }
    } catch(Exception exp) {
        exp.printStackTrace();
    }  
    }
    private ArrayList getBPparms(String node,String bp,int tableType) {
      ArrayList ll = new ArrayList();
      NumericalHashMap wfNameAndCount = null;
      Hashtable wfidAndName = null;;
      Hashtable wfidAndQ = null;
      NumericalHashMap swiftNameAndCount = null;
      NumericalHashMap systemNameAndCount = null;
      try {
          switch(tableType){
              case 0 : 
               wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(node);
               wfidAndName = (Hashtable)wfNodeHolder.get(node + "wn");
               wfidAndQ = (Hashtable)wfNodeHolder.get(node + "wq");
               if (wfNameAndCount.containsKey(bp)){
                   ll.add(wfNameAndCount.getInt(bp));
                   ll.add((String)wfidAndQ.get(bp));
               } else {
                   ll.add("-");
                   ll.add("-");   
               }
               break;   
              case 1:
                swiftNameAndCount = (NumericalHashMap)wfNodeHolder.get(node + "sw");
                break;  
              case 2:
                break;  
          }    
      } catch  (Exception ex){
          ex.printStackTrace();
      }     
      return ll;
    }   
    public void sortBPusage() {
      try {  
        //int nodes = QWAGlobal.nodeList.size();
        // Single Node 
          int nodes = 1; 
        //  Create ArrayList for sorting BP counts
        for (int i = 0; i < nodes; i++) {
            String node = (String)QWAGlobal.nodeList.get(i);
            NumericalHashMap wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(node);
            Set nameAcount = wfNameAndCount.entrySet();
            Iterator it = nameAcount.iterator();
            ArrayList<Integer> sortIt = new ArrayList<Integer>();
            HashMap hm = new HashMap();
            while(it.hasNext()) {
                 Map.Entry entry = (Map.Entry) it.next();
                 String bp = (String)entry.getKey();
                 int count = wfNameAndCount.getInt(bp);
                 hm.put(count, bp);
                 sortIt.add(count);
                 //System.out.println(bp + "  " + count);
            }
            Collections.sort(sortIt);
            // now try to map BP to count
            myContainer mc = new myContainer(wfNameAndCount);
            for (int j = sortIt.size()-1; j > 0; j--) {
                int val = (Integer)sortIt.get(j).intValue();
                String bp = mc.getBP(val);
                System.out.println(bp + "   " + val);
            }
        }
      } catch(Exception exp) {
          exp.printStackTrace();
      }  
    }
    /**
     * Will build both Swift BP table and System BP table
     * @param bpType ; 0 - swift ; 1 - system
     */
    public StringBuilder buildSwiftBPusage(int bpType) {
      StringBuilder sbAll = new StringBuilder();
        try {  
        int nodes = QWAGlobal.nodeList.size();
        //int nodes = QWUjdbc.nodeList.size();
        int[] cnt = new int[nodes];
        int numberOfBPs = 0;
        ArrayList allBPs = new ArrayList();
        
        //  Make sure all BPs are listed
        for (int i = 0; i < nodes; i++) {
            String node = (String)QWAGlobal.nodeList.get(i);
            NumericalHashMap wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(node);
            Set nameAcount = wfNameAndCount.entrySet();
            Iterator it = nameAcount.iterator();
            while(it.hasNext()) {
                 Map.Entry entry = (Map.Entry) it.next();
                 String bp = (String)entry.getKey();
                 if (!allBPs.contains((String)bp)) {
                     allBPs.add((String)bp);
                 }
            }    
        }
        String bp = null;
        for (int i = 0; i < allBPs.size(); i++) {
            bp = (String)allBPs.get(i);
            if (!bpType(bpType,bp))
                continue;
            for (int j = 0; j < nodes; j++) {
                String nod = (String)QWAGlobal.nodeList.get(j);
                NumericalHashMap wfNameAndCount = (NumericalHashMap)wfNodeHolder.get(nod);
                cnt[j]= 0;
                if (wfNameAndCount.containsKey(bp))
                    cnt[j] = wfNameAndCount.getInt(bp);
            }
            StringBuilder sb = new StringBuilder();
            sb = new StringBuilder();
            sb.append(tr);
            sb.append(td).append(bp).append(tde);
            for (int ii = 0; ii < QWAGlobal.nOfNodes; ii++) {
                if (cnt[ii] > 0)
                    sb.append(td).append(cnt[ii]).append(tde);
                else sb.append("<td bgcolor='PINK'>").append(cnt[ii]).append(tde);
            }
            sb.append(tre);
            sbAll.append(sb);
            //QWAUtil.printIt(sb.toString());
        } 
      } catch(Exception exp) {
          exp.printStackTrace();
      }   
      return sbAll;
    }  
    /**
     * Periodic view over 24 hours (or less)
     * @param recList
     * @param nbrOfiles 
     */
    public void buildBPperiodicUsageSN(ArrayList recList,int nbrOfiles) {
        Map bpNames = new HashMap();
        Map nameAndSize = new NumericalHashMap();
        BPkeeper bpk = null;
        String startHr= null;   // indicate start of diagram and the next 24 hours
        ArrayList al1 = null;
        Iterator it = recList.iterator();
        while (it.hasNext()) { 
            String str = (String)it.next();  
            al1 = QWUreader.tokenize(str);
            String nd = (String)al1.get(0);
            String d = (String)al1.get(1);
            String t = (String)al1.get(2);
            String q = (String)al1.get(5);
            String bp = (String)al1.get(21);
            String w = (String)al1.get(9);
            String ms = (String)al1.get(19);
            try {
                long millis = Long.parseLong(ms);  
            } catch(NumberFormatException ex){
                System.out.println("");
                continue;
            }    
            if (startHr == null){
                al1 = QWUreader.tokenize(t,":");
                startHr = (String)al1.get(0);
                
            }
            if (!bpNames.containsKey(bp)) {
                bpk = new BPkeeper();
                bpNames.put(bp,bpk);
                
            }
            bpk = (BPkeeper) bpNames.get(bp);
            if (bpk == null)
                continue;
            long sz = bpk.addBP(bp,w,ms,d,t);
            nameAndSize.put(bp,sz);
        }
        // sort list
        ArrayList<Integer> al = new ArrayList<Integer>();
        Set nameClass = bpNames.entrySet();
        Iterator it1 = nameClass.iterator();
        while(it1.hasNext()) {
              Map.Entry entry = (Map.Entry) it1.next();
              bpk = (BPkeeper)entry.getValue();
              al.add(bpk.totCount);
        }      
        Collections.sort(al);
        // now try to map BP to count
        //it1 = nameClass.iterator();
        Set nameSize =nameAndSize.entrySet();
        it1 = nameSize.iterator();
        String currBP = null;
        boolean initHTML = true;
        int maxsz = al.size() -25;
        for (int j = al.size()-1; j > maxsz; j--) {
             long val = (Integer)al.get(j).intValue();
             while(it1.hasNext()) {
                   Map.Entry entry = (Map.Entry) it1.next();
                   long count = (Long)entry.getValue();  // Sorted value
                   if (val == count) {
                       currBP = (String)entry.getKey(); // 
                       bpk =(BPkeeper) bpNames.get(currBP);
                       //bpNames.remove(currBP);
                       String str = QWUhtml.createPeriodicHTML(bpk,startHr,initHTML);
                       initHTML=false;
                       QWAUtil.printIt(str);
                       it1 = nameSize.iterator();
                       break;
                   }
             }      
             //System.out.println(bp + "   " + val);
        }
    }
    private void createHtmlForBP(ArrayList wfid,Properties longRunners) {
        int div=0;
        StringBuilder sb = null;
        int len =wfid.size();
        for (int i = 0; i < len; i++) {
            String wf = (String)wfid.get(i);
            ArrayList al1 = (ArrayList)longRunners.get(wf);
            String n1 = (String)al1.get(0);
            String d = (String)al1.get(1);
            String t = (String)al1.get(2);
            String q = (String)al1.get(5);
            String bp = (String)al1.get(21);
            String w = (String)al1.get(9);
            String ms = (String)al1.get(19);
            long millis = Long.parseLong(ms);
          try{  
            String s = String.format("%d hrs %d min, %d sec", 
                                    TimeUnit.MILLISECONDS.toHours(millis),
                                    TimeUnit.MILLISECONDS.toMinutes(millis) - 
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                    TimeUnit.MILLISECONDS.toSeconds(millis) - 
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            sb = new StringBuilder();
            if ((div % 2) > 0)
               sb.append(tr); //sb.append(trbgr);
            else
               sb.append(tr);
            div++;
            sb.append(td).append(n1).append(tde).append(td).append(d + " " + t).append(" (ended)").append(tde);
            sb.append(td).append(bp).append(tde).append(td).append(w).append(tde).append(td).append(q).append(tde);
            sb.append(td).append(s).append(tde);
            sb.append(tre);
            QWAUtil.printIt(sb.toString());
          } catch (Exception exp) {
              exp.printStackTrace();
          }  
        
      }
    }    
    /**
     * 
     * @param list
     * @param maxTime
     * info: wfid=9, time=19, BP=21
     * @return 
     */
    public void buildLongRunners(ArrayList list,long maxTime) {
        if (list == null)
            return ;
        long th_time = maxTime*1000; // make ms
        Properties longRunners = new Properties();
        ArrayList wfid = new ArrayList();
  //      String node = null;
        StringBuilder sb = null;
        long runtime = 0l;
        Iterator it = list.iterator();
        while (it.hasNext()) { 
            String str = (String)it.next();  
            ArrayList al = QWUreader.tokenize(str);
            String wf_id = (String)al.get(9);
            try {
                runtime = Long.parseLong((String)al.get(19));
            } catch (NumberFormatException ex){
                continue;
            }    
            if (th_time < runtime) {
                if (longRunners.containsKey(wf_id)) 
                    longRunners.remove(wf_id);
                else
                    wfid.add(wf_id);    
                longRunners.put(wf_id,al);
            }
        }
        createHtmlForBP(wfid,longRunners);
/*        
        int div=0;
        int len =wfid.size();
        for (int i = 0; i < len; i++) {
            String wf = (String)wfid.get(i);
            ArrayList al1 = (ArrayList)longRunners.get(wf);
            String n1 = (String)al1.get(0);
            String d = (String)al1.get(1);
            String t = (String)al1.get(2);
            String q = (String)al1.get(5);
            String bp = (String)al1.get(21);
            String w = (String)al1.get(9);
            String ms = (String)al1.get(19);
            long millis = Long.parseLong(ms);
          try{  
            String s = String.format("%d hrs %d min, %d sec", 
                                    TimeUnit.MILLISECONDS.toHours(millis),
                                    TimeUnit.MILLISECONDS.toMinutes(millis) - 
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                    TimeUnit.MILLISECONDS.toSeconds(millis) - 
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            sb = new StringBuilder();
            if ((div % 2) > 0)
               sb.append(trbgr);
            else
               sb.append(tr);
            div++;
            sb.append(td).append(n1).append(tde).append(td).append(d + " " + t).append(tde);
            sb.append(td).append(bp).append(tde).append(td).append(w).append(tde).append(td).append(q).append(tde);
            sb.append(td).append(s).append(tde);
            sb.append(tre);
            QWAUtil.printIt(sb.toString());
          } catch (Exception exp) {
              exp.printStackTrace();
          }  
        }
*/               
        return;
    }
    public void buildSingelBPList(ArrayList list,String bp) {
        if (list == null)
            return ;
        Properties longRunners = new Properties();
        ArrayList wfid = new ArrayList();
  //      String node = null;
        StringBuilder sb = null;
        long runtime = 0l;
        Iterator it = list.iterator();
        while (it.hasNext()) { 
            String str = (String)it.next();  
            ArrayList al = QWUreader.tokenize(str);
            String wf_id = (String)al.get(9);
            String thisbp = (String)al.get(21);
            //System.out.println(thisbp);
            try {
                runtime = Long.parseLong((String)al.get(19));
            } catch (NumberFormatException ex){
                continue;
            }    
            if ((bp.compareToIgnoreCase(thisbp) == 0)) {
                if (longRunners.containsKey(wf_id)) 
                    longRunners.remove(wf_id);
                else
                    wfid.add(wf_id);    
                longRunners.put(wf_id,al);
            }
        }
        createHtmlForBP(wfid,longRunners);
    }    
    class myContainer {
     String[] bpNames = null;
     int[] observations = null;
     int lng = 0;
     public myContainer(NumericalHashMap nhm) {
            lng = nhm.size(); 
            Set nameAcount = nhm.entrySet();
            Iterator it = nameAcount.iterator();
            bpNames = new String[lng];
            observations = new int[lng];
            int i = 0;
            while(it.hasNext()) {
                 Map.Entry entry = (Map.Entry) it.next();
                 bpNames[i] = (String)entry.getKey();
                 observations[i] = nhm.getInt(bpNames[i]);            
                 i++;
            }
    }
   // little inner helper class   
    public String getBP(int counter) {
        for (int i = 0; i < lng; i++) {
            if (observations[i] == counter) {
                observations[i] = -1;
                return(bpNames[i]); 
            }
        }
        return null;
    } 
   }
   class BPkeeper {
     String bpName = null;
     String DateTime = null;
     int totCount = 0;
     long totAct = 0l;
     long [] periodicCount = new long[24]; //24 hours
     long [] periodicActive = new long[24];;
     Map wf_collision = new HashMap();
     long act = 0l;
     // 
     long addBP(String bp,String wf,String ms,String d,String t){
        if (DateTime == null)
             DateTime = d + " " + t;   
        ArrayList al = QWUreader.tokenize(t,":");
        int inx = Integer.parseInt((String)al.get(0));
        // System.out.println("parm " + bp + " " + ms + " " + d + " " + t + " " + inx);
        if (inx < 0 || inx >23)
            return -1;
        if (bpName == null)
            bpName=bp;
        if (wf_collision.containsKey(wf)){
            act = Long.parseLong((String)wf_collision.get(wf));
            wf_collision.remove(wf);
            periodicActive[inx]-= act;
            periodicCount[inx] -=1l;  
            totAct -= act;
            totCount--;
        }
        long tmp = Long.parseLong(ms);
        periodicActive[inx]+=tmp;
        periodicCount[inx] +=1l;
        wf_collision.put(wf, ms);
        totAct += tmp;
        totCount++;
        return totCount;
     }
   } 
}
