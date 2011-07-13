package org.cytoscape.komal;

import java.io.*;
import cytoscape.logger.CyLogger;

import org.antlr.stringtemplate.*;
import org.stringtemplate.v4.*;
import org.antlr.stringtemplate.language.*;

public class STNew extends ExportToCWPlugin {

    public static String result;
    public static String indexhtml;

    public void STMake() {

        CyLogger.getLogger().info("STNew.java called");

        String currentdir = System.getProperty("user.dir");
        File dir = new File(currentdir);
        CyLogger.getLogger().info("Current Working Directory : " + dir);

        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("org/cytoscape/komal/page3.st");
        StringBuffer sb = new StringBuffer();
        BufferedReader bf = null;

        try {
            bf = new BufferedReader(new InputStreamReader(is));

            while (bf.ready()) {
                sb.append(bf.readLine() + "\n");
            }
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        STGroupString group = new STGroupString("");
        group.delimiterStartChar = '$';
        group.delimiterStopChar = '$';

        ST st = new ST(group, sb.toString());

        st.add("titleofhtml", htmlname);
        st.add("title", titleofpage);
        st.add("network", urlofxgmml);
        st.add("description", descriptionofnetwork);
        st.add("logo", urloflogo);

        result = st.render();

        CyLogger.getLogger().info("Attributes added");

        BuildHtml o = new BuildHtml();
        o.openFile();
        o.addRecords(result);
        indexhtml = o.completeurl();

        o.closeFile();
        CyLogger.getLogger().info("HTML CLOSED");

        STNew t = new STNew();
        t.openthehtmlfile();

    }

    void openthehtmlfile() {
        String x[] = {indexhtml};
        OpenBrowser.main(x);
    }
}


