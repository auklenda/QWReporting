/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
/**
 *
 * @author Alf
 */
public class QWUreader {
    static PrintWriter outp;
    BufferedReader inp;
    
    public static void resetInput() {
      try {
        if  (QWAGlobal.QWFile != null)
             QWAGlobal.QWFile.close();
             QWAGlobal.QWFile = new BufferedReader(new InputStreamReader(new FileInputStream(QWAGlobal.jTextField1.getText())));
      } catch (IOException exp) {
          exp.printStackTrace();
      }
    }
    public static void setNextFile(String fn) {
      try {
        if  (QWAGlobal.QWFile != null)
             QWAGlobal.QWFile.close();
        if (fn != null)    
             QWAGlobal.QWFile = new BufferedReader(new InputStreamReader(new FileInputStream(fn)));
      } catch (IOException exp) {
          exp.printStackTrace();
      }
    }    
    public static void openReportFile() {
        
    }
    public static BufferedReader openInFile(String fn) {
        try {
            return(new BufferedReader(new InputStreamReader(new FileInputStream(fn))));
      } catch (Exception exp) {
            return null;
      }  
    }
    private boolean openOutFile(String fn) {
      try {  
        outp = null; //gc  
        outp = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn)));
      } catch (Exception exp) {
            exp.printStackTrace();
            return false;
      }  
      return true;
    }
    public PrintWriter openFile(File fn) {
     try {  
        return(new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn))));
      } catch (Exception exp) {
            exp.printStackTrace();
            return null;
      }  
    }
    public PrintWriter openFile(String fn) {
     try {  
        return(new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn))));
      } catch (Exception exp) {
            exp.printStackTrace();
            return null;
      }  
    }
    public void openFile(String fn,String fn1){
      try {
        inp = new BufferedReader(new InputStreamReader(new FileInputStream(fn)));
        if (fn1 != null)
            outp = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn1)));
      } catch (Exception exp) {
            exp.printStackTrace();
      }  
    }    
    // tm HH:MIN:SEC
    public static int getTimeInSeconds(String tm) {
        ArrayList tok = tokenize(tm.trim(),":");
        int hh = (Integer.parseInt((String)tok.get(0)))*60*60;
        int min = hh + (Integer.parseInt((String)tok.get(1)))*60;
        return (min + Integer.parseInt((String)tok.get(2)));
    }
    public static String getTime(String line,int offs, String delimit) {
        return(line.substring(offs,line.indexOf(delimit)));
    }
    
    public static long getTimeInMS(String instring) {
       String rcv = getTime(instring,11,"]");
       long ms = Integer.parseInt(rcv.substring(1,3))*60*60*1000;
       ms += Integer.parseInt(rcv.substring(4,6))*60*1000;
       ms += Integer.parseInt(rcv.substring(7,9))*1000;
       long tms = Integer.parseInt(rcv.substring(10));
       if (rcv.substring(10).length() == 1)
          ms += tms*100;
       else if (rcv.substring(10).length() == 2 )
          ms += tms*10;
       else ms += tms;
     return ms;   
    }
    
    private static long getMS(String tm) {
       long ms = Integer.parseInt(tm.substring(1,3))*60*60*1000;
       ms += Integer.parseInt(tm.substring(4,6))*60*1000;
       ms += Integer.parseInt(tm.substring(7,9))*1000;
       return ms; 
    }
    
    public static String getTimeString(String line,int offs, String delimit) {
        return(line.substring(offs,line.indexOf(delimit)));
    }

    public static String getDateAndTime() {
        return (getDateAndTime("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getDateAndTime(String frm) {
        Date now = new Date(System.currentTimeMillis());
        String formatStr = frm;
        SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
        return (formatter.format(now));
    }
    public static ArrayList tokenize(String str) {
        return(tokenize(str," "));
    }
    public static ArrayList tokenize(String str,String token) {
        ArrayList al = new ArrayList();
	int i0 = 0;
        for ( StringTokenizer st = new StringTokenizer(str,token); st.hasMoreTokens();) {
            al.add(i0++,(String)st.nextToken()); 
        }
        return al;
    }
    public static int isitDigit(String strN) {
      try {
        return(Integer.parseInt(strN));
      } catch (Exception ex) {
          return -1;
      }
    }
    public static int isitQueue(String strN) {
       int tmp = -1;
        try {
           tmp = Integer.parseInt(strN);
           if (tmp < 1 || tmp > 9)
               return -1;
      } catch (Exception ex) {
          return -1;
      }
      return tmp;
    }

    public static boolean isDigit(String strN) {
      try {
        int num = Integer.parseInt(strN);
        return true;
      } catch (Exception ex) {
          return false;
      }
    }
    public static String readLine() {
       String str;
        try {
             str = QWAGlobal.QWFile.readLine();
             return str;
    //       return QWFile.readLine();
        } catch (IOException e) {
            System.out.println("QWUtil.readLine got a IOException");
        }
        return null;
    }
    public static String readLine(int recType) {
       String str;
        try {
             while((str = QWAGlobal.QWFile.readLine()) != null){
                if ((getRecordType(str)) == recType)
                     return str;
             }
        } catch (IOException e) {
            System.out.println("QWUtil.readLine got a IOException");
        }
        return null;
    }
    public static boolean insertIfNotIn(String key,String obj,Properties prop) {
        if (!prop.containsKey(key)) {
            prop.put(key, obj);
            return true;
        }
        return false;
    }
    public static boolean insertRegardless(String key,String obj,Properties prop) {
          prop.put(key, obj);
          return true;
    }
    public static boolean insertRegardless(String key,String obj,HashMap map) {
          map.put(key, obj);
          return true;
    }    
    private static boolean validateMem(String rec) {
        int i = rec.indexOf("(GB)", 20);
        String r = rec.substring(i+4,rec.indexOf("TOT", i+4));
        if (r.indexOf(".") == -1)
            return false;
        return true;
    }
    public static int getRecordType(String rec) {
       int recType = 0;
       //String tmp = rec.substring(26,29);    //  Record Type
       String tmp1 = rec.substring(0,40);
       ArrayList al = tokenize(tmp1," ");
       String tmp = (String)al.get(3);
       if (tmp.equalsIgnoreCase("QUE"))
           recType = QWAGlobal.que;
       else if (tmp.equalsIgnoreCase("WFC"))
           recType =  QWAGlobal.wfc;
       else if (tmp.equalsIgnoreCase("MEM")) {
           if (!validateMem(rec))
               recType =  QWAGlobal.noop;         // MEM
           else recType =  QWAGlobal.mem;         // MEM
       } else if (tmp.equalsIgnoreCase("HDR"))
           recType =  QWAGlobal.hdr;
       else if (tmp.equalsIgnoreCase("JDBC"))
           recType =  QWAGlobal.jdbc;
       else if (tmp.equalsIgnoreCase("CFG"))
           recType =  QWAGlobal.cfg;    
       else if (tmp.equalsIgnoreCase("ENV"))
           recType =  QWAGlobal.env;       
       
       return recType;
    }   
   public static ArrayList collectEntries(int recType){
       String inpr; 
       boolean found=false;
       ArrayList inRecord = new ArrayList();
       while ((inpr = QWUreader.readLine(recType)) != null) {
              inRecord.add((String)inpr);
              found=true;
       }
       if (!found)
           inRecord=null;
       return inRecord;
   } 
  public static ArrayList collectMultEntries(int recType, ArrayList inRecord){
       String inpr; 
       boolean found=false;
       while ((inpr = QWUreader.readLine(recType)) != null) {
              inRecord.add((String)inpr);
              found=true;
       }
       if (!found)
           inRecord=null;
       return inRecord;
   }     
}
