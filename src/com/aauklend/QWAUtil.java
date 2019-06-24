/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import com.sterlingcommerce.aauklend.utilities.SearchForFiles;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author AAuklend
 */
public class QWAUtil {
  String folder = "C:\\Customers\\SocGen\\SWING\\QWLogs\\17072013\\";
  String filter = "node*";
  static PrintWriter outp;
  static boolean sysout = false;
  static ArrayList nodeList = null;
    public QWAUtil() {
    }
    public static void closeOut() {
        if (outp != null)
            outp.close();
    }
    public static boolean openOutFile(String fn) {
      try {  
        outp = null; //gc  
        outp = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fn)));
      } catch (Exception exp) {
            exp.printStackTrace();
            return false;
      }  
      return true;
    }  
    
    private boolean findNodeFiles() throws SQLException{
        boolean found = false;
        StringBuilder delScript = new StringBuilder();
        delScript.append("cd ").append(folder).append("\n");
        SearchForFiles sf = new SearchForFiles();
        //Vector fn = getFileNames("c:\\temp",true,"*");
        File [] fils = sf.scanFolder(folder,true,filter);
        for (int i = 0; i < fils.length; i++) {
            File file = fils[i];
            String fn = file.getName();
            QWAUtil.printIt(fn);
        } // end for loop
        return found;
    }   
    public static void printIt(String str){
        if (sysout)
            System.out.println(str);
        else
            outp.println(str);
    }
    // print control for writing same node name on ever line (cosmetic)
    public static ArrayList initNodeList(){
        nodeList = new ArrayList();
        return(nodeList);
    }
    public static boolean nodeInList(String node){
        if (!nodeList.contains((String)node)) {
            nodeList.add((String)node);
            return false;
        }
        return true;
    }
    public static void destroyNodeList(){
        nodeList = null;
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
   /*
     *   Create a matrix with all parms for this sample line
     *   1 - 9 queues : Minpool, Used, Calculated , Pool, MaxPoool, Depth
     *   Q1 [0] ... Q9 [8]
    */ 
    public static int[][] createQMatrix(String str) {
     ArrayList inpAL = QWUreader.tokenize(str);
     return(createQMatrix(inpAL));   
    }
    public static int[][]  createQMatrix(ArrayList inpAL) {
     int [][] qmon = new int[9][6]; 
     int alIndx = 6;
     //ArrayList inpAL = QWUtil.tokenize(str);
     String currentQDate = (String)inpAL.get(1);
     String QTime = (String)inpAL.get(2);
     for (int i = 0; i < 9; i++) {
         for (int j = 0; j < 6; j++) {
             try {
                qmon[i][j] = Integer.parseInt((String)inpAL.get(alIndx++));
             } catch (Exception exp) {
                 System.out.println("Faild in creating QMatrix:" + inpAL.toString());
             }
         }
         alIndx +=2;
     }
     return qmon;
    }       
   public static void main(String[] args) throws SQLException {
        QWAUtil qwu = new QWAUtil();
        qwu.findNodeFiles();
    }  

}
