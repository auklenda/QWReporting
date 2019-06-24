/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

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
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.UnitType;

/**
 *
 * @author AlfAuklend
 */
public class QWEjdbcGraph {
    /**
     * Called from QWExtractor to display JDBC Connection Pool usage
     * buildJdbcGraph( will write a graph to the result directory
     * @param inRecord - list of JDBC records 
     * @param seq - file sequence number for uniqueness
    * @return
     */
    public void buildJdbcGraph(ArrayList inRecord,StringBuilder htm,int seq) {
     String actionStr = "Connection Requests ";  
     String inpr = null;
     int poolField = 2;
     int rows = inRecord.size();
     ArrayList al = QWAUtil.tokenize((String)inRecord.get(0));   
     String node = (String)al.get(0);
     String start = (String)al.get(1)+ " " + (String)al.get(2);
     String last = null;
     int nCols = Integer.parseInt((String)al.get(4));
     int offset = 5;
     String []colNames = new String[nCols+1];
     colNames[0] = "Time";
     // Build PoolName header
     for (int i=0 ; i < nCols ; ++i){
         String tmp = (String)al.get(offset);
         colNames[i+1] = tmp.substring(1);
         offset += 8;
     }
     // Time Graph for each pool
     TimeSeries []ts =  new TimeSeries[nCols];
     for (int i = 0; i < nCols; i++) {
          ts [i] = new TimeSeries(colNames[i+1] + "", Second.class);
     }
     int rowIndex = 0;
     int [] previous = new int[nCols];
     Iterator it = inRecord.iterator();
     while (it.hasNext()) {
         String record = (String)it.next();
         al = QWAUtil.tokenize(record);
         last = (String)al.get(1)+ " " + (String)al.get(2);
        //  rowData[rowIndex][0] = al.get(2);
         offset = 6 + poolField;
         for (int i=0 ; i < nCols ; ++i){
             if (rowIndex == 0) {
                 previous[i]  = Integer.parseInt((String)al.get(offset));
             } else  {
                 int tmp =  Integer.parseInt((String)al.get(offset));
                 int tmp1 = previous[i];
                 int data =  tmp - tmp1;
                 if (data > 10000)
                     System.out.println("noooo");
                 createTestJDBCDataset(ts[i],record,data);
                 previous[i] = tmp;
                 //rowIndex++;
             }
             offset += 8;
           }
           rowIndex+=1;          
     }
     TimeSeriesCollection dataset = new TimeSeriesCollection();
     for (int i = 0; i < nCols; i++) {
         if (ts[i] != null)
             dataset.addSeries(ts[i]);
     }
     int w = 250;
     int h = 150;
     String yaxis_header ="Connect Requests"; //get_yaxis_header(action);
     String gtitle = "JDBC " + actionStr + " - " + node;
     JFreeChart chart = createChart(dataset,gtitle,yaxis_header, start, last,actionStr );  
     String graphFile = "qw-" + node + "_JDBC_Con_Req_"  + seq + ".jpg";
     saveChart(QWExtractor.folder + "\\result\\" + graphFile,chart);
     htm.append("<a href='").append(graphFile).append("'><img src='").append(graphFile);
     htm.append("' alt=BP graph' width='").append(w).append("' higth='").append(h).append("'></a>");       
  }  
   /**
    * Create Timeseries for active queues
    * @return
    */
    public static void createTestJDBCDataset(TimeSeries ts,String inpr,int inUse) {
      String tim = inpr.substring(17,22);
      int year = Integer.parseInt(inpr.substring(6,10));
      int month = Integer.parseInt(inpr.substring(11,13));
      int wday = Integer.parseInt(inpr.substring(14,16));
      int hh = Integer.parseInt(inpr.substring(17,19));
      int mm = Integer.parseInt(inpr.substring(20,22));
      int ss = Integer.parseInt(inpr.substring(23,25));
      try {
          ts.add(new Second(ss,mm,hh,wday,month,year),inUse);
      } catch (org.jfree.data.general.SeriesException exp) {
          ts.update(new Second(ss,mm,hh,wday,month,year),inUse);
          System.out.println(inpr);
      }
    }   
   /**
     *
     * @param dataset
     * @param title
     * @param yaxis
     * @return
     */
    //private JFreeChart createChart(TimeSeriesCollection dataset,String title,String yaxis,javax.swing.JPanel jpan) {
      private JFreeChart createChart(TimeSeriesCollection dataset,
                               String title,
                               String yaxis,
                               String start,
                               String last,
                               String tabName) {
          JFreeChart chart = ChartFactory.createTimeSeriesChart(
                                title,               // chart title
                                "Time",               // domain axis label
                                yaxis,                  // range axis label
                                dataset,            // data
                                true,               // create legend?
                                true,               // generate tooltips?
                                false               // generate URLs?
        );

        //setFname(title);
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        //plot.setBackgroundPaint(Color.lightGray);
        plot.setBackgroundPaint(Color.black);
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
        //ChartPanel cpanel = new ChartPanel(chart);
        return chart;

    }   
   private void saveChart(String graphFile,JFreeChart chart) {
    int xx = 1100;
    int yy = 650;       
       try {
           ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);
        } catch (Exception e) {
            e.getStackTrace();
            System.err.println("Problem occurred creating chart in " + graphFile);
        } 
        
    }       
}
