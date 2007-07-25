/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;

import infovis.Graph;
import infovis.column.IntColumn;
import infovis.example.ExampleRunner;
import infovis.graph.Algorithms;
import infovis.graph.DefaultGraph;
import infovis.graph.algorithm.KCoreDecomposition;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.visualization.MatrixVisualization;
import infovis.io.AbstractReader;
import infovis.utils.MinFunction;

/**
 * Class KCoreGraphExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class KCoreGraphExample {

    public static void main(String[] args) {
        ExampleRunner example = new ExampleRunner(args,
                "KCoreGraphExample");
        Graph g;

        if (args.length == 0) {
            g = Algorithms.getOneCompnentGraph();
            //g = Algorithms.getGridGraph(10, 10);
        }
        else {
            g = new DefaultGraph();
            AbstractReader reader = 
            GraphReaderFactory.createGraphReader(
                example.getArg(0),
                g);
            if (reader == null || ! reader.load()) {
                System.err.println("cannot load " + example.getArg(0));
                System.exit(1);
            }
        }
        if (g.isDirected()) {
            System.err.println("K Core Decomposition only works on undirected graphs");
            System.exit(1);
        }
        IntColumn vertexCoreness = KCoreDecomposition.computeCoreness(
                g, null);
        IntColumn edgeCoreness = KCoreDecomposition.computeEdgeCoreness(
                g, 
                vertexCoreness, 
                null,
                MinFunction.instance);
        g.getVertexTable().addColumn(vertexCoreness);
        g.getEdgeTable().addColumn(edgeCoreness);
//        NodeLinkGraphVisualization visualization =
//            new NodeLinkGraphVisualization(g);
        //visualization.setVisualColumn("color", coreness);
        //visualization.setLayout(FRLayout.instance);
        //visualization.setLayout(new CircularLayout());
        MatrixVisualization visualization =
                new MatrixVisualization(g);
        visualization.setVisualColumn("color", edgeCoreness);

        example.createFrame(visualization);
    }
}
