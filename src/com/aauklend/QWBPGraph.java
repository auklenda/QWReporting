package com.aauklend;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Font;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
// XYDataset
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
import org.jfree.chart.axis.PeriodAxis;

import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import javax.swing.JFileChooser;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
/**
 *
 * @author DEY9APB5
 */
public class QWBPGraph  {
    String start = null;
    String last = null;
    String fnamePart = null;
    String graphFile = null;
    String title = null;
    JFreeChart chart;
    int xx = 1100;
    int yy = 650;    
    /**
     * Creates new form QWABPChart
     */
    public QWBPGraph() {

    }
    

    private void saveChart(String graphFile) {
       try {
           ChartUtilities.saveChartAsJPEG(new File(graphFile), chart, xx, yy);        
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        } 
        
    }
    private void setFname(String title) {
        int i = title.trim().indexOf(" ");
        if (i != -1)
           fnamePart =  title.trim().substring(0,i);
        else fnamePart = "chart";
    }
    public String getDateAndTime() {
     return(getDateAndTime("yyyyMMdd_HHmmss"));
    } 
    public String getDateAndTime(String frm) {
     Date now = new Date(System.currentTimeMillis());
     String formatStr = frm;
     SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
     return (formatter.format(now));   
    }
    /**
     * 
     * @param list
     * @param htm
     * @param seq
     * @return 
     */
    public String buildBPGraph(ArrayList list,StringBuilder htm,int seq,String st,String sp,String node){
      CategoryDataset dataset =  createDataset(list);
      int w = 250;
      double h = Math.abs(w*0.54);
      createBPChart(dataset,"BP usage - " + node,"25 highest observations", st, sp );
      //String fn = QWExtractor.folder  +  "\\qw-" + currentNode + "_heap.jpg";
      String fn = "qw-" + node + "_BPs_"  + seq + ".jpg";
      saveChart(QWExtractor.folder + "\\result\\" + fn);
      htm.append("<a href='").append(fn).append("'><img src='").append(fn);
      htm.append("' alt=BP graph' width='").append(w).append("' higth='").append(h).append("'></a>");       
     return null;
      
   }    
    /**
     * 
     * @param dataset
     * @param title
     * @param yaxis
     * @return
     */
    private JFreeChart createBPChart(CategoryDataset dataset,String title, String yaxis,String start,String stop) {    
          chart = ChartFactory.createBarChart(
            title,
            "BP",
            yaxis,
            dataset,
            PlotOrientation.HORIZONTAL,
    //                PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tool tips
            false  // URLs
        );
        setFname(title);
        String shdr = "   From: " + start + "    To: " + last;
        TextTitle subtitle = new TextTitle(shdr);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setPosition(RectangleEdge.TOP);
        subtitle.setPadding(new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05,
                0.05, 0.05));
        subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        chart.addSubtitle(subtitle);
        int w = 250;
        double h = Math.abs(w*0.54);        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
/*
        ValueAxis domainAxis = new DateAxis("COUNT");
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
*/
        plot.setDomainGridlinesVisible(true);
        plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8f);
        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        StandardCategoryToolTipGenerator tt
                = new StandardCategoryToolTipGenerator("{1}: {2} projects",
                new DecimalFormat("0"));
        renderer.setBaseToolTipGenerator(tt);
        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        renderer.setSeriesPaint(0, gp0);
        ChartPanel cpanel = new ChartPanel(chart);
        cpanel.setPreferredSize(new java.awt.Dimension(xx,yy));
        return chart;
    }
    /**
     * 
     * @param ArrayList (al)
     */
    public CategoryDataset createDataset(ArrayList al) {
        int display = 25;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      if (al.size() < 25)
          display = al.size();
      for (int i = 0; i < display; i++) {
           //QWAnaGUI.BPcompare o = (QWAnaGUI.BPcompare)al.get(i);
           QWBPNameInfo o = (QWBPNameInfo)al.get(i);
           dataset.addValue(o.count,"Encountered",o.bpName);
      }
      return dataset;
    }   
    private String getStart(){
        String st = this.start;
        int inx = st.indexOf(" ");
        String st1 = st.substring(0, inx);
        ArrayList al = QWAUtil.tokenize(st1,"-");
        StringBuilder sb = new StringBuilder(); 
        sb.append((String)al.get(0));
        sb.append((String)al.get(1));
        sb.append((String)al.get(2));
        sb.append("_");
        al = QWAUtil.tokenize(st.substring(inx+1),":");
        sb.append((String)al.get(0));
        sb.append((String)al.get(1));
        sb.append((String)al.get(2));
        return sb.toString();
    }               

 
 }