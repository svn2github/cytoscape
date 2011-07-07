package main.java.org.cytoscape.komal;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cytoscape.logger.CyLogger;


public class htmlpage extends ExportToCWPlugin{

    private Formatter x;

    String nameofwebpage = HtmlPath;

    public void openFile() {
        try {
            x = new Formatter(nameofwebpage);
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    public void addRecords(String a) {
        x.format("%s", a);
        CyLogger.getLogger().info("RECORDS ADDED");
    }

    public String completeurl() {
        CyLogger.getLogger().info("HTML NAME AFTER ADD RECORD"+nameofwebpage);
        return nameofwebpage;

    }

    public void closeFile() {
        
        x.close();
    }
}
