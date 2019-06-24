/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sterlingcommerce.aauklend.utilities;
import java.util.*;
import java.io.*;
/**
 *
 * @author DEY9APB5
 */
public class SearchForFiles {
    boolean useSubFolders = false;
    boolean debug = false;
    boolean filenameOnly = false;
    String filters = null;
    String svcName=null;
    int vectorSize=1000;
    String directory = null;
    String cN = null;
    long classCount = 0l;
    long folderCount = 0l;   
    public File[] scanFolder(String folder) {
        return scanFolder(folder,true,null);
    }
    public File[] scanFolder(String folder,boolean useSubFolders,String filters) {
        File wkdir = new File(folder);
        String[] fileNames = wkdir.list();
        if ( !wkdir.exists() ) {
            return null;
        }
       File[] files = null;
        if ( !useSubFolders && filters.indexOf("*") == -1 ) {
            files = new File[1];
            files[0] = new File(folder, filters);
        }
        else {
            WildCardFilter filter = new WildCardFilter(filters, false, debug, svcName);
            if ( useSubFolders ) {
                Vector fileVect = new Vector(vectorSize); // used to combine all File objects
                traverseDir(fileVect, filter, wkdir);
                int vectSize = fileVect.size();
                if ( vectSize > 0 ) {
                    files = new File[vectSize];
                    for ( int i = 0; i < vectSize; i++ ) {
                        files[i] = (File)fileVect.elementAt(i);
                    }
                }
            }
            else { // short and sweet when useSubFolders=false
                files = wkdir.listFiles(filter);
            }
        }
        return files;
    }
    public Vector scanFolderV(String folder) {
        return scanFolderV(folder,true,filters);
    }
    public Vector scanFolderV(String folder,boolean useSubFolders,String filters) {    
        File wkdir = new File(folder);
        if ( !wkdir.exists() ) {
            return null;
        }
        WildCardFilter filter = new WildCardFilter(filters, false, debug, svcName);
        Vector fileVect = new Vector(vectorSize); // used to combine all File objects
        File[] files = null;
        if ( useSubFolders ) {
            traverseDir(fileVect, filter, wkdir);
            int vectSize = fileVect.size();
        } else { // short and sweet when useSubFolders=false
             files = wkdir.listFiles(filter);
             for ( int i = 0; i < files.length; i++ ) {
                 if (filter.accept(files[i]) ) {
                    if (filenameOnly)
                        fileVect.add(files[i].getName());
                    else fileVect.add(files[i]);
                }
             }
        }
        return fileVect;
    }
    
    public Vector findDirectories(String startFolder) {
        Object [] o = null;
        
        File wkdir = new File(startFolder);
        if ( !wkdir.exists() ) {
            return null;
        }
        Vector fileVect = new Vector(vectorSize); // used to combine all File objects
        File[] files = null;
        files = wkdir.listFiles();
        for ( int i = 0; i < files.length; i++ ) {
             if (files[i].isDirectory()) {
                    fileVect.add(files[i]);
             }
        }
        return fileVect;        
    }

   public void traverseDir(Vector fileVect, WildCardFilter filter, File wkdir) {
        File[] fileList = wkdir.listFiles(); // returns everything
        if ( fileList != null ) {
            for ( int i = 0; i < fileList.length; i++ ) {
                if ( !fileList[i].isDirectory() && filter.accept(fileList[i]) ) {
                    fileVect.add(fileList[i]);
                }
                else {
                    traverseDir(fileVect, filter, fileList[i]);
                }
            }
        }
    }
}
