/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aauklend;

import java.util.ArrayList;

/**
 *
 * @author AlfAuklend
 */
public class QWBPNameInfo {
    String bpName;
    int count;
    ArrayList al;
    
    public QWBPNameInfo(){
        
    }
    public QWBPNameInfo(String name,String wfid){
        this.bpName = name;
        count = 1;
        al = new ArrayList();
        al.add(wfid);
    }  
    public void increaseCount(String wfid){
        if (!al.contains(wfid)){
            count++;
            al.add(wfid);
        }
    }
}
