package exportasxgmmltrial;

import java.io.*;
import java.lang.*;
import java.util.*;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.*;

public class WebPage extends ExportToCytoscapeWeb {

    public static String result;
    public static String indexhtml;

    public static void main(String[] args) {

        StringTemplateGroup group = new StringTemplateGroup("myGroup", "C:/Users/Vampire/Documents/NetBeansProjects/ExportAsXGMMLTrial/src/exportasxgmmltrial", DefaultTemplateLexer.class);
        StringTemplate obj = group.getInstanceOf("page2");

        obj.setAttribute("title", titleofpage);
        obj.setAttribute("network", urlofxgmml);
        obj.setAttribute("description", descriptionofnetwork);
        obj.setAttribute("logo", urloflogo);
        result = obj.toString();

        htmlpage o = new htmlpage();
        o.openFile();
        o.addRecords(result);
        indexhtml = o.completeurl();
        o.closeFile();

        WebPage t = new WebPage();
        t.openthehtmlfile();

    }

    void openthehtmlfile() {
        String x[] = {indexhtml};
        OpenBrowser.main(x);
    }
}
