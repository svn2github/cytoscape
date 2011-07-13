package org.cytoscape.komal;


import java.io.*;
import java.lang.*;
import java.util.*;
import cytoscape.logger.CyLogger;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;

public class WebPage extends ExportToCWPlugin {

    public static String result;
    public static String indexhtml;

    public static void main(String[] args) {

        CyLogger.getLogger().info("WebPage.java called");

        String currentdir = System.getProperty("user.dir");
        File dir = new File(currentdir);
        CyLogger.getLogger().info("Current Working Directory : "+ dir);

        StringTemplateGroup group = new StringTemplateGroup("myGroup", "./", DefaultTemplateLexer.class);
        StringTemplate obj = group.getInstanceOf("/st/page3");

        CyLogger.getLogger().info("String Template Group made");

        obj.setAttribute("title", titleofpage);
        obj.setAttribute("network", urlofxgmml);
        obj.setAttribute("description", descriptionofnetwork);
        obj.setAttribute("logo", urloflogo);
        result = obj.toString();

        CyLogger.getLogger().info("Attributes added");

        htmlpage o = new htmlpage();
        o.openFile();
        CyLogger.getLogger().info("HTML "+HtmlPath+" CREATED");
        o.addRecords(result);
        CyLogger.getLogger().info("HTML "+HtmlPath+" FILLED");
        indexhtml = o.completeurl();
        
        o.closeFile();
        CyLogger.getLogger().info("HTML CLOSED");

        WebPage t = new WebPage();
        t.openthehtmlfile();

    }

    void openthehtmlfile() {
        String x[] = {indexhtml};
        OpenBrowser.main(x);
    }
}
