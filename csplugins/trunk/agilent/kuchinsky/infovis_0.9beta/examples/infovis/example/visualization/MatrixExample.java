/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;
import infovis.Graph;
import infovis.example.*;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.visualization.MatrixVisualization;
import infovis.io.AbstractReader;


/**
 * Example of graph visualization with an adjacency matrix.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class MatrixExample {

    public static void main(String[] args) {
        ExampleRunner example = new ExampleRunner(args, "MatrixExample");
//        if (example.fileCount() != 1) {
//            System.err.println("Syntax: MatrixExample <name>");
//            System.exit(1);
//        }
        Graph          g = new DefaultGraph();
        AbstractReader reader = GraphReaderFactory.createGraphReader(example.getArg(0), g);

        if (reader != null && reader.load()) {
            MatrixVisualization visualization = new MatrixVisualization(g);

            visualization.setVertexLabelColumn(
                ExampleRunner.getStringColumn(g.getVertexTable(), 0));
            example.createFrame(visualization);
        } else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }
}
