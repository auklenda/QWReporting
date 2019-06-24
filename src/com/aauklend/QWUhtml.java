/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

/**
 *
 * @author Alf
 */
public abstract class QWUhtml {
    static String tableHtml = "<table border='1' cellspacing='0' cellpadding='4'>";
    static String tablee ="</table>";
    static String css_bkr = "th.bkr { background-color: #FFFF66; }";
    static String css_bkr1 = "td.bkr1 {background-color: #66FFFF;}";
    static String css_bkr2 = "td.bkr2 {background-color: #FF0000;}";
    static String css_col1 = "td.col1 {background-color: #FFCC00;}";
    static String css_col2 = "td.col2 {background-color: #FFAA99;}";
    static String css_col3 = "td.col3 {background-color: #FFCC99;}";
    static String css_warn = "td.warn {background-color:pink;}";
    static String css_yr = "th.yr { background-color: #FFFF66;color:red }";
    static String css_yb = "th.yb { background-color: #FFFF66;color:blue }";
    static String css_yg = "th.yg { background-color: #FFFF66;color:green }";  
    static String css_line1 = "tr.line1 {background-color:#C6EFF7}";
    static String css_line2 = "td.line2 {background-color:#C6EFF7}";
    static String thCls = "<th class='bkr'>";
    static String yr = "<th class='yr'>";
    static String yb = "<th class='yb'>";
    static String yg = "<th class='yg'>";
    static String bgcol1 = "<td class='col1'>";
    static String sumCls = "<td class='bkr1'>";
    static String isRedbg = "<td class='bkr2'>";
    static String jdbcline1 = "<td class='line2'>";
    static String hdrShort = "<th class='bkr'>";
    static String hdrLong = "<th colspan='4' class='bkr'>";
    static String hdrLong1 = "<th colspan='1' class='bkr'>";
    static String explain1 = "<h4><br>T=Running threads <br>C= Calculated threads  <br>D= QueueDepth (BPs waiting for threads</h4>";
    static String nl = "\n";
    static String table1 = "<table border='1' cellspacing='0' cellpadding='4' style='font-size: small;'>";
    static String th = "<th>";
    static String the = "</th>";
    static String tr = "<tr>";
    static String tre = "</tr>";
    static String td ="<td>";
    static String tde ="</td>";
    static String comment ="<p><font color='blue'><b>Comments: </b></font></p>";
    static String recommendation ="<p><font color='blue'><b>Recommendation: </b></font></p>";
    static String [] cal = {"00","01","02","03","04","05","06","07","08","09",
                            "10","11","12","13","14","15","16","17","18","19",
                            "20","21","22","23"};
    public static StringBuilder getSB(){
         return(new StringBuilder());
    }
    public static StringBuilder createHDR() {
      StringBuilder sb = getSB();
      sb.append("<!DOCTYPE html PUBLIC ").append('"').append("-//W3C//DTD XHTML 1.0 Transitional//EN").append('"').append("\n");
      sb.append("xmlns=").append('"').append("http://www.w3.org/1999/xhtml").append('"').append("\n");
      sb.append("<HTML><HEAD>").append("<style>").append("\n");
      sb.append(css_bkr).append("\n");
      sb.append(css_bkr1).append("\n");
      sb.append(css_bkr2).append("\n");
      sb.append(css_col1).append("\n");
      sb.append(css_col2).append("\n");
      sb.append(css_col3).append("\n");
      sb.append(css_warn).append("\n");
      sb.append(css_line1).append("\n");
      sb.append(css_line2).append("\n");
      sb.append(css_yr);
      sb.append(css_yb);
      sb.append(css_yg);
      sb.append("h3 {color:blue;}").append(nl);
      sb.append(".legend { list-style: none; }").append(nl); 
      sb.append(".legend li { float: left; margin-right: 10px; } ").append(nl);
      sb.append(".legend span { border: 1px solid #ccc; float: left; width: 12px; height: 12px; margin: 2px; }").append(nl);
      sb.append(".legend .q1 { background-color: red; }").append(nl);
      sb.append(".legend .q2 { background-color: green; }").append(nl);
      sb.append(".legend .q3 { background-color: blue; }").append(nl);
      sb.append(".legend .q4 { background-color: yellow; }").append(nl);
      sb.append(".legend .q5 { background-color: cyan; }").append(nl);
      sb.append(".legend .q6 { background-color: maroon; }").append(nl);
      sb.append(".legend .q7 { background-color: gray; }").append(nl);
      sb.append(".legend .q8 { background-color: magenta; }").append(nl);
      sb.append(".legend .q9 { background-color: black; }").append(nl);
      sb.append("</style>").append("</HEAD>").append(nl);
      sb.append("<script src='pie_1.js' type='text/javascript'></script>").append(nl);
      //  beware -  javascript
      sb.append("<script type='text/javascript'>").append(nl);
      sb.append("function toggleMe(a,b,c){").append(nl);
      sb.append("var e=document.getElementById(a);").append(nl);
      sb.append("if(!e)return true;").append(nl);
      sb.append("if(e.style.display=='none'){").append(nl);
      sb.append("e.style.display='block'").append(nl);
      sb.append("document.getElementById(b).value='- ' + c;").append(nl);
      sb.append(" } else {").append(nl);
      sb.append("e.style.display='none'").append(nl);
      sb.append("document.getElementById(b).value='+ ' + c; }").append(nl);
      sb.append("return true; }").append(nl);
      sb.append("</script>").append("\n");
      sb.append(tableHtml);
      sb.append(tr).append(th).append("<img src=" + "'logo.png' alt='logo'>").append(the);
      sb.append(th).append("<h2 style='color:blue'>QWA Report</h2>").append(the).append(tre);
      sb.append(tr).append(td).append("Created:").append(tde).append(td).append(QWUreader.getDateAndTime()).append(tde).append(tre);
      sb.append(tr).append(td).append("PROJECT:").append(tde).append(td).append(QWExtractor.project).append(tde).append(tre);
      sb.append(tr).append(td).append("Log Period:").append(tde).append(td).append("Look at the file name list").append(tde).append(tre);
      sb.append(tablee);
      //sb.append("<img src=" + "'logo.png' alt='logo'><h2 style='color:blue'><br><hr>QWA Report - ").append(QWUreader.getDateAndTime()).append("</h2>"); 
      //sb.append("<hr><h3>PROJECT: ").append(QWExtractor.project).append("</h3>");
      
      return sb;          
    }    
    private static String explainQueue() {
       StringBuilder sb = getSB(); 
       sb.append("<ul>");
       sb.append("<li type=square><font color='red'><b>T:&nbsp</b></font>Running Threads").append(nl);
       sb.append("<li type=square><font color='blue'><b>F:&nbsp</b></font>Calculated Maximum Threads (Fairshare)").append(nl);
       sb.append("<li type=square><font color='green'><b>M:&nbsp</b></font>ConfiguredMaximum Threads for queue pool").append(nl);
       sb.append("<li type=square><b>D:&nbsp</b>QueueDepth (BPs waiting for threads").append(nl);
       sb.append("</ul>").append(nl);
       sb.append("<h4>When column <font color='red'><b>T:&nbsp</b></font> and <font color='black'><b>D:&nbsp</b></font> color is non-white the cell has a active count</h4>").append(nl);
       sb.append("<h4>When column <font color='red'><b>T:&nbsp</b></font> is red the queue is using less threads than allowed by <font color='blue'><b>F:&nbsp</b></font></h4>").append(nl);
       return sb.toString();
    }

    public static String getQhdr(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n" + tableHtml);
        return null;
    } 
    public static String createQueueHdr(String tabName, String id,String bton){
      StringBuilder sb = new StringBuilder();
      //sb.append(nl).append(explainQueue());
      sb.append(nl);
      sb.append("<input type='button' id='").append(bton).append("' onclick=\"return toggleMe(").append(id).append(",'").append(bton);
      sb.append("','").append(tabName).append("')\" value='+");
      sb.append(tabName).append("'>").append(nl);
      sb.append("<p id=").append(id).append(" style='display:none'>").append(nl);
      sb.append(table1);
      sb.append(nl);
      sb.append(nl).append(explainQueue());
      sb.append("<tr>");
      sb.append("<th>" + "&nbsp</th>").append("<th>").append("&nbsp</th>");
      for (int i = 0; i < 9; i++) {
          sb.append(hdrLong).append("Q" + (i+1)).append("</th>");
      }    
      sb.append("</tr><tr>");
      sb.append(thCls + "NODE</th>").append(thCls).append("DateTime</th>");
      for (int i = 0; i < 9; i++) {
          sb.append(yr).append("T").append("</th>");
          sb.append(yb).append("F").append("</th>");
          sb.append(yg).append("M").append("</th>");
          sb.append(thCls).append("D").append("</th>");
      }
      sb.append(thCls).append("TotThread").append("</th>");
      sb.append(thCls).append("TotDepth").append("</th>"); 
      sb.append("</tr>");
      return sb.toString();
    }
    /**
     * 
     * @param tabName
     * @param id
     * @param bton
     * @return 
     */
    public static String createPeriodicHTML(com.aauklend.QWUqueue.BPkeeper bpk, String start,boolean hdr){
      StringBuilder sb = new StringBuilder();
      int st = 0; // Integer.parseInt(start);            
      int first = 0; // get hours to midnight
      if (hdr){
          st = Integer.parseInt(start);
          first = 24 -(24-st);
          //sb.append(nl).append(explainQueue());
          sb.append(nl);
          sb.append(table1);
          sb.append(nl);
          sb.append("<tr>");
          sb.append("<th>").append("BPName").append("</th>");
          sb.append("<th>").append("Tot").append("</th>");
          for (int i = 0; i < (24-st); i++) {
              sb.append(hdrLong1).append(cal[st + i]+ ":00").append("</th>");
          }
          for (int i = 0; i < first; i++) {  // hours after midnight (24 total)
              sb.append(hdrLong1).append(cal[i]+ ":00").append("</th>");
          }
          sb.append("</tr><tr><th>").append(bpk.DateTime).append("</th>").append("<th>").append("Cnt/Act").append("</th>");
          for (int i=0; i < 24; i++){
              sb.append("<th>").append("Cnt/Act").append("</th>");
              //sb.append("<th>").append("Act").append("</th>");
          }
          sb.append("</tr>");
      }  // end hdr
      String bp = bpk.bpName;
      sb.append("<tr><td>").append(bpk.bpName).append("</td>");
      sb.append("<td>").append(bpk.totCount).append("/").append(bpk.totAct/bpk.totCount).append("</td>");
      for (int i = 0; i < (24-st); i++) {
          long count = bpk.periodicCount[st+i];
          long res = 0l;
          if (count > 0l)
              res = bpk.periodicActive[st+i]/count;
          //sb.append("<td>").append(bpk.periodicCount[st+i]).append("</td>");
          //sb.append("<td>").append(res).append("</td>");
          sb.append("<td>").append(count).append("/").append(res).append("</td>");
      }
      for (int i = 0; i < first; i++) {  // hours after midnight (24 total)
          long count = bpk.periodicCount[i];
          long res = 0l;
          if (count > 0l)
              res = bpk.periodicActive[i]/count;
          //sb.append("<td>").append(bpk.periodicCount[i]).append("</td>");
          //sb.append("<td>").append(res).append("</td>");
          sb.append("<td>").append(count).append("/").append(res).append("</td>");
      }
      sb.append("</tr>");
      return sb.toString();
    }     
  /*  
   public static String createDepthHdr(){
      StringBuilder sb = new StringBuilder();
      sb.append(nl);
      sb.append(nl).append(explain1); 
      sb.append(nl);
      sb.append(table1);
      sb.append("<tr>");
      sb.append("<th>" + "&nbsp</th>").append("<th>").append("&nbsp</th>");
      for (int i = 0; i < 9; i++) {
          sb.append(hdrLong).append("Q" + (i+1)).append("</th>");
      }    
      sb.append("</tr><tr>");
      sb.append(nl);
      sb.append(thCls + "NODE</th>").append(thCls).append("DateTime</th>");
      for (int i = 0; i < 9; i++) {
          sb.append(thCls).append("T").append("</th>");
          sb.append(thCls).append("C").append("</th>");
          sb.append(thCls).append("D").append("</th>");
      }
      sb.append(thCls).append("TotDepth").append("</th>"); 
      sb.append(thCls).append("TotThread").append("</th>");
      sb.append("</tr>");
      sb.append("\n");
      return sb.toString();
    } 
    */ 
   public static String createBigQHdr() {
      StringBuilder sb = new StringBuilder();
      sb.append(table1);
      sb.append("<tr>");
      sb.append(thCls + "NODE</th>").append(thCls).append("Tot Samples").append("</th>");
      for (int i = 0; i < 9; i++) {
          sb.append(thCls).append("Q" + (i+1)).append("</th>");
      }
      sb.append(thCls).append("Tot Qs").append("</th>");
      sb.append(thCls).append(" % ").append("</th>");
      sb.append("</tr>");
      return sb.toString();
   }
   public static String createWFHdr(boolean usage) {
      StringBuilder sb = new StringBuilder();
      sb.append(table1);
      sb.append("<tr>");
      sb.append("<th>" + "&nbsp</th>");
      for (int i = 0; i < QWAGlobal.nOfNodes; i++) {
          sb.append(hdrLong1).append(QWAGlobal.nodeNames[i]).append("</th>");
      }
      sb.append("</tr><tr>");
      sb.append(thCls).append("BP Name").append("</th>");
      for (int i = 0; i < QWAGlobal.nOfNodes; i++) {
          sb.append(thCls).append("Observed").append("</th>");
          sb.append(thCls).append("Queues").append("</th>");
      }    
      sb.append("</tr>");
      return sb.toString();
   }
   public static String createWFHdr1() {
      StringBuilder sb = new StringBuilder();
      sb.append(table1);
      sb.append("<tr>");
      sb.append("<th>" + "&nbsp</th>");
      for (int i = 0; i < QWAGlobal.nOfNodes; i++) {
          sb.append(hdrShort).append(QWAGlobal.nodeNames[i]).append("</th>");
      }
      sb.append("</tr><tr>");
      sb.append(thCls).append("BP Name").append("</th>");
      for (int i = 0; i < QWAGlobal.nOfNodes; i++) {
          sb.append(thCls).append("Observed").append("</th>");
      }    
      sb.append("</tr>");
      return sb.toString();
   }   
   public static String createLongRunnerHdr() {
       StringBuilder sb = new StringBuilder();
       sb.append(table1);
       sb.append("<tr>"); 
       sb.append(thCls + "NODE</th>").append(thCls).append("DateTime</th>");
       sb.append(thCls + "BP Name</th>").append(thCls).append("WorkflowID</th>");
       sb.append(thCls + "Queue</th>").append(thCls + "Active</th></tr>"); 
       return sb.toString();
   }
   public static String createConfigHdr(){
      StringBuilder sb = new StringBuilder();
       sb.append(table1);
       sb.append("<tr>"); 
       sb.append(thCls + "Parms.</th>");
       for (int i = 0; i < 9; i++) {
           sb.append(thCls + "Q" + (i+1) + "</th></tr>");
       }
       return sb.toString();
   }
   
}
