package org.cytoscape.komal;

import java.util.*;

import cytoscape.logger.CyLogger;

public class BuildHtml extends ExportToCWPlugin {

    private Formatter x;
    String nameofwebpage = HtmlPath;

    public void openFile() {
        try {
            x = new Formatter(nameofwebpage);
             CyLogger.getLogger().info("HTML " + HtmlPath + " CREATED");
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    public void addRecords(String a) {
        x.format("%s", a);
        CyLogger.getLogger().info("RECORDS ADDED TO "+HtmlPath );
    }

    public String completeurl() {
        CyLogger.getLogger().info("HTML NAME AFTER ADD RECORD: " + nameofwebpage);
        return nameofwebpage;

    }

    public void closeFile() {

        x.close();
    }
}