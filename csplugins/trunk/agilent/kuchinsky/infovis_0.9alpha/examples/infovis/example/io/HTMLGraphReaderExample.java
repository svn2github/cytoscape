/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.example.io;
import infovis.Graph;
import infovis.example.*;
import infovis.graph.DefaultGraph;
import infovis.graph.io.HTMLGraphReader;
import infovis.graph.visualization.MatrixVisualization;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Class HTMLGraphReaderExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class HTMLGraphReaderExample {

    public static void main(String[] args) {
        ExampleRunner example = new ExampleRunner(args, "HTMLGraphReaderExample");
        if (example.fileCount() < 1) {
            System.err.println("syntax: <base url> [start-path] [real-url]");
            System.exit(1);
        }
        Graph          g = new DefaultGraph();
        HTMLGraphReader reader = new HTMLGraphReader(example.getArg(0), g);

        String startPath = (example.fileCount() == 1) ? "" : example.getArg(1);

        OutputStream out = System.out;

        reader.setLog(new PrintStream(out));
        reader.add(startPath);
        if (reader.load()) {
            MatrixVisualization visualization = new MatrixVisualization(g);

            visualization.setVertexLabelColumn(
                ExampleRunner.getStringColumn(g.getEdgeTable(), 0));
            example.createFrame(visualization);
        } else {
            System.err.println("cannot load " + example.getArg(0));
        }        
    }
}
