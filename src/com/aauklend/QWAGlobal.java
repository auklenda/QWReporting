/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aauklend;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ArrayList;
/**
 *
 * @author aauklend
 */
public class QWAGlobal {
 public static int  [][] histoGram = new int [9][10];
 public static int [][] threadConfig = new int [9][2];
 public static int [] maxDepth =  new int [9];
 public static int [] calcHigherThread = new int[9];
 public static String [] maxDepthTime = new String[9];
 public static int [] qs = new int[9];
 public static int [] allThreadsInUse = new int[9];
 public static int [] anyThreadsInUse = new int[9];
 public static int [] waitersForThreads = new int[9];
 public static int [] fairShareImposed = new int[9];
 static int que = 1;
 static int wfc = 2;
 static int mem = 3;
 static int hdr = 4;
 static int jdbc = 5;
 static int cfg = 6;
 static int env = 7;
 static int noop = 8;
 static int badItem = 1;
 static int bufferdItem = 2;
 static int maxPool = 3;
 static int connections = 4;
 static long [] cHelp;
 static ArrayList connectionHelp = null;
 static BufferedReader QWFile = null;
 static PrintWriter htmlFile = null;
 public static javax.swing.JTextField jTextField1;
 static String start = null;
 static String startDate = null;
 static String last = null;
 static String nodeName = null;
 static String procs = null;
 static String hdrHost = null;
 static String hdrPort = null;
 static String hdrRate = null;
 static String hdrNode = null;
 static String hdrTH = null;
 static String hdrMemory = null;
 static String hdrWFID = null;
 static String QWFileName = null;
 static String outFolder = "./";
 static HashMap bpList = null;
 static ArrayList queList = null;
 static int [][] queueDepth = null; 
 static long [] recTypes = new long[noop];
 static String [] nodeNames = new String[10];
 static int numberOfFiles = 0;
 static int nOfNodes = 0;
 static Hashtable recordCount = new Hashtable();
 //static Hashtable recordCount = null;
 static Hashtable cfgList = new Hashtable();
 static ArrayList nodeList = new ArrayList();
 static HashMap Nodeconfig = null;
 static ArrayList Nodename = null;
 static boolean NodeconfigExist = false;
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
 static boolean queueGraph=false;
 static boolean maxpools=true;
 static boolean poolRequest=true;
 static boolean biggestQueue=true;
 static boolean bpTable=true;
 static boolean swingTable=false;
 static boolean swiftTable=false;
 static boolean systemTable=true; 
 static boolean environment=true;
 static boolean longRunner=true;
 static boolean selectedBPs=true;
 static boolean hungBPs=true;
 static boolean doConfig=true; 
 static boolean EnvOnTop = true;
 static HashMap EnvList = null;
 static String inFile = null;
 static boolean fileMode = false;
 
 
}
