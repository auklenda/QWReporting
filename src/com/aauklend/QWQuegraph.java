/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import static com.aauklend.QWUreader.tokenize;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

/**
 *
 * @author Alf
 */
public class QWQuegraph {
    String currentNode = null;
    TimeSeries inUse = null;
    TimeSeries inTotal = null;
    int lowerThreadLimit = 0;
    String start = null;
    String last = null;
    String title = null;
    int xx = 1100;
    int yy = 650;
    int pageCtl = 0;
    JFreeChart chart; 
    TimeSeries s1 = null;
    String [] hdr = {"Minimum Threads","Active Threads",null,null,null,"Waiting BPs"};
    String [] hdr1 = {"_minimum","_active",null,null,null,"_waiting"};
  
  public QWQuegraph(int th){
      lowerThreadLimit = th;
  }
   private void saveChart(String graphFile) {
       try {
           ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);
        } catch (IOException e) {
            e.getStackTrace();
            System.err.println("Problem occurred creating chart in " + graphFile);
        } 
        
    }    
  private int [][] buildQrecord(ArrayList al){
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
  /**
   * 
   * @param list 
   * list - All QUE strings
   * sel  - The index into "depth" position
   * q    - the queue or -1 (all queues) 
   */
    public void buildQueGraph(ArrayList list,int sel){
        buildQueGraph(list,sel,-1);  // Select all 9 detph queues.
    }
    public void buildQueGraph(ArrayList list,int sel , int q){
        buildQueGraph(list,sel,-1,null); 
    }
    public void buildQueGraph(ArrayList list,int sel , int q,StringBuilder htm){
       if (list  == null) {
           createActiveChart(sel,q,htm);
           return;
       } 
       String str = (String)list.get(0);
       String tmp1 = str.substring(0,40);
       ArrayList al = QWUreader.tokenize(tmp1," ");       
       String node = (String)al.get(0);
       //String node = str.substring(0,5);
       if (currentNode == null) {
          currentNode = node; 
          
          //start = str.substring(6, 22);
          start = (String)al.get(1) + " " +(String)al.get(2);
          s1 = new TimeSeries(hdr[sel], Second.class);
       } else {
          if (!node.equals(currentNode)) {
             createActiveChart(sel,q,htm);
             currentNode = node; 
             //start = str.substring(6, 22);
             start = (String)al.get(1) + " " +(String)al.get(2);
             s1 = new TimeSeries(hdr[sel], Second.class);
          } 
       }
       createActiveThreadDataset(list,sel,q);
   }
    /**
     * 
     * @param list
     * @param htm 
     */
   public void buildSingleGraph(ArrayList list,int sel, StringBuilder htm,int seq,String g_type,boolean individual){
       String str = (String)list.get(0);
       String tmp1 = str.substring(0,40);
       ArrayList al = QWUreader.tokenize(tmp1," ");       
       String node = (String)al.get(0);
       String st = (String)al.get(1) + " " +(String)al.get(2);
       String uniqueFN = (String)al.get(1);
       currentNode = node; 
       int end = list.size();
       str = (String)list.get(end-1);
       tmp1 = str.substring(0,40);
       al = QWUreader.tokenize(tmp1," ");  
       String sp = (String)al.get(1) + " " +(String)al.get(2);
       TimeSeriesCollection dataset=null;
       //s1 = new TimeSeries(hdr[sel], Second.class);
       if (individual)
          dataset = createThreadAndQueDataset(list,sel);
       else
          dataset = createActiveThreadDataset(list,sel,-1);
       //TimeSeriesCollection dataset = new TimeSeriesCollection();
       //dataset.addSeries(s1);
       //String qName = "ALL";
       String qName = g_type;
       createThreadChart(dataset,hdr[sel] + " - " + currentNode + "-" + qName,hdr[sel], st, sp );
       String fn = "qw-" + currentNode + "-" + qName + hdr1[sel] + "_" + uniqueFN + seq +".jpg";
       saveChart(QWExtractor.folder + "\\result\\" + fn);
      //String htm = "<img src=" + "'" + fn +"'" + " alt='active graph'>";
       String html = "<a href='" + fn + "'>" + "<img src=" + "'" + fn +"'" + " alt='" + hdr1[sel] + " graph' width='250' higth='150'></a>";
       htm.append(html);       
   }    
    
    private void createActiveChart(int sel,int q,StringBuilder htm){
      TimeSeriesCollection dataset = new TimeSeriesCollection();
      dataset.addSeries(s1);
      String qName = "ALL";
      if (q != -1) {
          qName = String.valueOf(q+1);
      }
      createThreadChart(dataset,hdr[sel] + " - " + currentNode + "-" + qName,hdr[sel], start, last );
      String fn = "qw-" + currentNode + "-" + qName + hdr1[sel] + ".jpg";
      saveChart(QWExtractor.folder + "\\result\\" + fn);
      //String htm = "<img src=" + "'" + fn +"'" + " alt='active graph'>";
      String html = "<a href='" + fn + "'>" + "<img src=" + "'" + fn +"'" + " alt='" + hdr1[sel] + " graph' width='250' higth='150'></a>";
      if (htm == null)
          QWAUtil.printIt(html);
      else
          htm.append(html);
      pageCtl++;
      if (pageCtl ==3){
          QWAUtil.printIt("<br>");
          pageCtl=0;
      }

    }
    
    public TimeSeriesCollection createActiveThreadDataset(ArrayList list, int sel,int q) {
      int [][] queue = null;
      String inpr = null;
      TimeSeries ts = new TimeSeries(hdr[sel], Second.class); 
      Iterator it = list.iterator();
      while (it.hasNext()) {
          inpr = (String)it.next();
          ArrayList al = QWUreader.tokenize(inpr);
          String node = (String)al.get(0);
          last = (String)al.get(1) + " " +(String)al.get(2);
          String dd = (String)al.get(1);
          int year = Integer.parseInt(dd.substring(0,4));
          int month = Integer.parseInt(dd.substring(5,7));
          int wday = Integer.parseInt(dd.substring(8,10));
          String tt = (String)al.get(2);  
          int hh = Integer.parseInt(tt.substring(0,2));
          int mm = Integer.parseInt(tt.substring(3,5));
          int ss = Integer.parseInt(tt.substring(6,8));
          queue = buildQrecord(al);   
          int inUse = 0;
          if (q == -1) {
             for (int i = 0; i < 9; i++) {
                  inUse += queue[i][sel];
             }
          } else
             inUse =  queue[q][sel];
          try { 
               if (inUse > 0 && inUse >= lowerThreadLimit) {
                   ts.add(new Second(ss,mm,hh,wday,month,year),inUse);
               }
          } catch (org.jfree.data.general.SeriesException exp) {    
               ts.update(new Second(ss,mm,hh,wday,month,year),inUse);
          }
      }    
      TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
      return dataset;        
    }
   /**
    * Create Timeseries for active queues
    * @return
    */
    //public XYDataset createThreadAndQueDataset(ArrayList list,int sel) {
    public TimeSeriesCollection createThreadAndQueDataset(ArrayList list,int sel) {    
      String inpr = null;
      TimeSeries ts[] = {null,null,null,null,null,null,null,null,null};
      //TimeSeries s1 = new TimeSeries("Active Queue Threads", Second.class);
      Iterator it = list.iterator();
      while (it.hasNext()) {  
            inpr = (String)it.next();
            ArrayList al = QWUreader.tokenize(inpr);
            String node = (String)al.get(0);
            last = (String)al.get(1) + " " +(String)al.get(2);
            String dd = (String)al.get(1);
            int year = Integer.parseInt(dd.substring(0,4));
            int month = Integer.parseInt(dd.substring(5,7));
            int wday = Integer.parseInt(dd.substring(8,10));
            String tt = (String)al.get(2);  
            int hh = Integer.parseInt(tt.substring(0,2));
            int mm = Integer.parseInt(tt.substring(3,5));
            int ss = Integer.parseInt(tt.substring(6,8));
            int [][] queue = buildQrecord(al);              
            String tim = inpr.substring(17, 22);
            int inUse = 0;
            for (int i = 0; i < 9; i++) {
                inUse = queue[i][sel];
                // HACK HACK HACK
                if (inUse > -1) {
                    //if (inUse > 300) inUse = 500;
                    try {
                        if (ts[i] == null) {
                            ts[i] = new TimeSeries("Q " + (i + 1) + " ", Second.class);
                        }
                        ts[i].add(new Second(ss, mm, hh, wday, month, year), inUse);
                    } catch (org.jfree.data.general.SeriesException exp) {
                        ts[i].update(new Second(ss, mm, hh, wday, month, year), inUse);
                        System.out.println(inpr);
                    }
                }
            }
        }
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i = 0; i < 9; i++) {
            if (ts[i] != null) {
                dataset.addSeries(ts[i]);
            }
        }
        return dataset;        
    }
    
    /**
     * 
     * @param dataset
     * @param title
     * @param yaxis
     * @param start
     * @param last
     * @return
     */
   public JFreeChart createThreadChart(TimeSeriesCollection dataset,String title,String yaxis,String start,String last) {
        this.title = title; 
        chart = ChartFactory.createTimeSeriesChart(
            title,               // chart title
            "Time",               // domain axis label
            yaxis,                  // range axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        ); 
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(Color.white);
 
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYItemRenderer r = plot.getRenderer();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setTickUnit(
            new DateTickUnit(
                DateTickUnit.MINUTE,30, new SimpleDateFormat("HH:MM")
            ),false,false
        );
        // Set Integer as vertical
        axis.setVerticalTickLabels(true);
        NumberAxis axis1 = (NumberAxis) plot.getRangeAxis();
        TickUnitSource units = NumberAxis.createIntegerTickUnits();
        axis1.setStandardTickUnits(units);
        return chart;
    }
     
}
