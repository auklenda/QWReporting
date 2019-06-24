/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.TimeSeriesCollection;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

import org.jfree.chart.axis.DateTickUnit;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartUtilities;

/**
 *
 * @author Alf
 */
public class QWMemgraph {
    String currentNode = null;
    TimeSeries inUse = null;
    TimeSeries inTotal = null;
 
    String start = null;
    String last = null;
    int xx = 1100;
    int yy = 650;
    int pageCtl = 0;
    JFreeChart chart;
    
    
    
    public QWMemgraph() {
    }
   private void saveChart(String graphFile) {
       try {
           ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);
        } catch (IOException e) {
            e.getStackTrace();
            System.err.println("Problem occurred creating chart in " + graphFile);
        } 
        
    }    
    public String buildHeapGraph(ArrayList list){
       buildHeapGraph(list,null); 
       return null;
    }   
    /**
     * Called from QWExtractor to build the heap graph
     * @param list
     * @param htm
     * @return 
     */
    public String buildHeapGraph(ArrayList list,StringBuilder htm){
       if (list  == null) {
           //return (createHeapChart(htm));
           createHeapChart(htm);
           return null;
       } 
       String str = (String)list.get(0);
       String tmp1 = str.substring(0,40);
       ArrayList al = QWUreader.tokenize(tmp1," ");       
       String node = (String)al.get(0);
       //String node = str.substring(0,5);
       
       if (currentNode == null) {
          currentNode = node; 
          start = (String)al.get(1) + " " +(String)al.get(2);
          //start = str.substring(6, 22);
          inUse = new TimeSeries("In Use", Second.class);
          inTotal = new TimeSeries("Total Comitted", Second.class);
          pageCtl = 0;
       } else {
          if (!node.equals(currentNode)) {
             createHeapChart(htm);
             currentNode = node; 
             start = (String)al.get(1) + " " +(String)al.get(2);
             //start = str.substring(6, 22);
             inUse = new TimeSeries("In Use", Second.class);
             inTotal = new TimeSeries("Total Comitted", Second.class);
          } 
       }
       createMemoryDataset(list);
       return null;
      
   }
    /**
     * This method does not call the createheapChart(...) method, it does it all within this method
     * @param list - MEM record strings 
     * @param htm - StringBuilder keeping the vertical cell (html) tags
     * @param seq - sequence number to seperate graph file names
     * @return 
     */
    public String buildSingleHeapGraph(ArrayList list,StringBuilder htm,int seq){
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
       currentNode = node; 
       sp = (String)al.get(1) + " " +(String)al.get(2);
          //start = str.substring(6, 22);
       inUse = new TimeSeries("In Use", Second.class);
       inTotal = new TimeSeries("Total Comitted", Second.class);
       createMemoryDataset(list);
      int w = 250;
      double h = Math.abs(w*0.54);
      TimeSeriesCollection dataset = new TimeSeriesCollection();
      dataset.addSeries(inTotal);
      dataset.addSeries(inUse);
      createMemChart(dataset,"Heap usage - " + currentNode,"Heap Comitted(GB)", st, sp );
      //String fn = QWExtractor.folder  +  "\\qw-" + currentNode + "_heap.jpg";
      String fn = "qw-" + currentNode + "_heap" + uniqueFN + seq + ".jpg";
      saveChart(QWExtractor.folder + "\\result\\" + fn);
      htm.append("<a href='").append(fn).append("'><img src='").append(fn);
      htm.append("' alt='heap graph' width='").append(w).append("' higth='").append(h).append("'></a>");       
     return null;
      
   }    
    //private String createHeapChart(){
    private String createHeapChart(StringBuilder htm){  
      int sideBYside = 3;
      //int w = 250*3;
      int w = 250;
      double h = Math.abs(w*0.54);
      TimeSeriesCollection dataset = new TimeSeriesCollection();
      dataset.addSeries(inTotal);
      dataset.addSeries(inUse);
      createMemChart(dataset,"Heap usage - " + currentNode,"Heap Comitted(GB)", start, last );
      //String fn = QWExtractor.folder  +  "\\qw-" + currentNode + "_heap.jpg";
      String fn = "qw-" + currentNode + "_heap.jpg";
      saveChart(QWExtractor.folder + "\\result\\" + fn);
      StringBuilder sb = new StringBuilder();
      sb.append("<a href='").append(fn).append("'><img src='").append(fn);
      sb.append("' alt='heap graph' width='").append(w).append("' higth='").append(h).append("'></a>");
      //String htm = "<a href='" + fn + "'>" + "<img src=" + "'" + fn +"'" + " alt='heap graph' width='250' higth='150'></a>";
      if (htm == null)
         QWAUtil.printIt(sb.toString());
      else 
         htm.append(sb);
      pageCtl++;
      if (pageCtl == sideBYside){
          QWAUtil.printIt("<br>");
          pageCtl=0;
      }
      return null;
    }    
   /**
   * 
   * @param header
   * @return 
   */    
  public void createMemoryDataset( ArrayList list) {
      String inpr = null;
      String prevTim = "";
      Iterator it = list.iterator();
      while (it.hasNext()) {
          inpr = (String)it.next();
          boolean dropThis = false;
          ArrayList al = QWUreader.tokenize(inpr);
          String node = (String)al.get(0);
          last = (String)al.get(1) + " " +(String)al.get(2);
          String dd = (String)al.get(1);
          //last = inpr.substring(6, 22);
          //yyyy-mm-dd
          int year = Integer.parseInt(dd.substring(0,4));
          int month = Integer.parseInt(dd.substring(5,7));
          int wday = Integer.parseInt(dd.substring(8,10));
          String tt = (String)al.get(2);  
          int hh = Integer.parseInt(tt.substring(0,2));
          int mm = Integer.parseInt(tt.substring(3,5));
          int ss = Integer.parseInt(tt.substring(6,8));
          last = (String)al.get(1) + " " +(String)al.get(2);
          String tim = (String)al.get(2);
/*
          last = inpr.substring(6, 22);
          String tim = inpr.substring(17, 22);
          int year = Integer.parseInt(inpr.substring(6, 10));
          int month = Integer.parseInt(inpr.substring(11, 13));
          int wday = Integer.parseInt(inpr.substring(14, 16));
          int hh = Integer.parseInt(inpr.substring(17, 19));
          int mm = Integer.parseInt(inpr.substring(20, 22));
          int ss = Integer.parseInt(inpr.substring(23, 25));
*/
          int ix = inpr.indexOf("TOT(GB)", 20);
          int ix1 = inpr.indexOf("TOT", ix + 8);   // Get end of value
          String m1 = inpr.substring(ix + 7, ix1).trim();
          if (m1.indexOf(".") == -1) {
              dropThis = true;   // ***** some problem with QWW
          }
          String m2 = removeSpace(m1);
          double commitedMem = Double.valueOf(m2);
          ix = inpr.indexOf("(%)", 75);
          ix1 = inpr.indexOf("%", ix + 6);   // Get end of value
          if (ix1 == -1) {
              ix1 = inpr.indexOf("P", ix + 6);
          }
          String freeMem = removeSpace(inpr.substring(ix+4,ix1));
          try {
              double occMem = (100.00 - (Double.valueOf(freeMem)));
              inUse.addOrUpdate(new Second(ss, mm, hh, wday, month, year), (commitedMem * (occMem / 100)));
              inTotal.addOrUpdate(new Second(ss, mm, hh, wday, month, year), (commitedMem));
          } catch (Exception exp) {
              exp.printStackTrace();
          }
          prevTim = tim;
      } // end while
       return ;
      }
    public JFreeChart createMemChart(TimeSeriesCollection dataset,
            String title, 
            String yaxis,
            String start,
            String last) {
        this.start = start;
        this.last = last;
        return(createMemChart(dataset,title,yaxis));
    }
    
    private JFreeChart createMemChart(TimeSeriesCollection dataset,String title, String yaxis) {    
        chart = ChartFactory.createXYAreaChart(
            title,
            "Time",
            yaxis,
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tool tips
            false  // URLs
        );
//        setFname(title);
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
/*        
        Rectangle rt = jPanel1.getBounds();
        xx = rt.width -0; 
        yy = rt.height -0;
*/
        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis domainAxis = new DateAxis("TIME");
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setTickLabelsVisible(true);
        plot.setDomainAxis(domainAxis);
        plot.setForegroundAlpha(0.5f);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.white);
       // plot.setBackgroundPaint(Color.white);
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.yellow);
        renderer.setSeriesPaint(1, Color.black);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setTickUnit(
            new DateTickUnit(
                DateTickUnit.MINUTE,30, new SimpleDateFormat("yyMMdd HHmm")
                //DateTickUnit.MINUTE,30, new SimpleDateFormat(" MMdd MM:DD")
            ),false,false
        );
        axis.setVerticalTickLabels(true);
        return chart;
    }
  
      private String removeSpace(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') sb.append(str.charAt(i)); }
        return sb.toString();
     }
    
}
