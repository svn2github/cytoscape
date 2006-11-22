package org.cytoscape.coreplugin.cpath.test.task;

import junit.framework.TestCase;
import org.cytoscape.coreplugin.cpath.task.ReadPsiFromCPath;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.ImportHandler;

import java.net.URL;

public class TestReadPsiFromCPath extends TestCase {

    public void testQueryByKeyword () throws Exception {
        ReadPsiFromCPath reader = new ReadPsiFromCPath();

        ImportHandler importHandler = new ImportHandler();
        URL url = new URL ("http://cbio.mskcc.org/cpath/webservice.do?version=1.0&cmd=get_by_keyword&q=DNA&format=psi_mi&startIndex=0&organism=&maxHits=10");
        GraphReader graphReader = importHandler.getReader(url);


        //graphReader = reader.getInteractionsByKeyword("p53", 10);
        if (graphReader == null) {
            System.out.println("Graph Reader is null");
        } else {
            System.out.println(graphReader.getClass().getName());
        }
    }
}
