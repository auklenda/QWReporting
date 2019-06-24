/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author DEY9APB5
 */
public class QWUconfig {
    static int MaxThreads = 0;
    static int [][] threadPools = new int [11][9];
    static String [][] ThreadPools = new String [17][10];
    String td = "<td>";
    String tde = "</td>";
    
    public QWUconfig() {
        
    }
   public void createConfig(String str) {
        ArrayList al = QWUreader.tokenize(str," ");
        MaxThreads = Integer.parseInt((String)al.get(5));
        for (int i = 0; i < 16; i++) {
            String qItem = (String)al.get(i+6);
            ArrayList qiAl = QWUreader.tokenize(qItem,";");
            
            for (int j = 0; j < 10; j++) {
                ThreadPools[i][j] = (String)qiAl.get(j);
            }
        }
        System.out.println(MaxThreads);
   } 
   public void addConfig(String cfg) {
       String node = cfg.substring(0,5);
       if (QWAGlobal.Nodeconfig == null) {
           QWAGlobal.Nodeconfig = new HashMap();
           QWAGlobal.Nodename = new ArrayList();
           QWAGlobal.NodeconfigExist = true;
       }
       if (!QWAGlobal.Nodeconfig.containsKey(node)) {
           QWAGlobal.Nodeconfig.put(node, cfg);
           QWAGlobal.Nodename.add(node);
       }    
       
   }
   /**
    *    ArrayList 0=NODEn - 1=DATE - 2=TIME - 3=CFGtag - 4=MaxThreads 5=Pool info
    * @param str
    * @param node
    * @return 
    */
   public String createConfigHtml(String str,String node){
     String cfg = str;
     StringBuilder sb = new StringBuilder();
     try {
       sb.append(QWUhtml.table1);
       sb.append("<tr>"); 
       sb.append(QWUhtml.thCls).append(node).append("</th>"); 
       for (int ii = 0; ii < 9; ii++) {
           sb.append(QWUhtml.thCls + "Q" + (ii+1) + "</th>");
       }  
       sb.append("</tr>");
       ArrayList al = QWUreader.tokenize(cfg," ");
       //MaxThreads = Integer.parseInt((String)al.get(5));
       String maxThreads = (String)al.get(4);
       for (int i = 0; i < 16; i++) {
            String qItem = (String)al.get(i+5);
            ArrayList qiAl = QWUreader.tokenize(qItem,";");
            sb.append("<tr>");
            for (int j = 0; j < 10; j++) {
                sb.append(td).append((String)qiAl.get(j)).append(tde);
            }
            sb.append("</tr>");
       }
       
     }catch(Exception exp) {
         exp.printStackTrace();
     } 
     sb.append("</table>");
     return(sb.toString());
   }
     
 }