/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;

import infovis.Graph;
import infovis.column.NumberColumn;
import infovis.example.ExampleRunner;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.graph.visualization.layout.GraphVizLayout;
import infovis.utils.RowIterator;
import infovis.visualization.render.*;

import java.awt.Color;

public class SocialNetworkExample extends ExampleRunner {

    public SocialNetworkExample(String args[], String name) {
        super(args, name);
        DefaultGraph graph = new DefaultGraph();
        GraphReaderFactory.readGraph(getArg(0), graph);
        filterGraph(graph);
        NodeLinkGraphVisualization visualization = new NodeLinkGraphVisualization(
                graph);
        GraphVizLayout layout = new GraphVizLayout("neato");
        DefaultVisualLabel vl = (DefaultVisualLabel)VisualLabel.get(visualization);
        // vl.setJustification(0);
        vl.setOrientation(DefaultVisualLabel.ORIENTATION_WEST);
        vl.setClipped(false);
        vl.setDefaultColor(Color.BLACK);
        VisualSize.get(visualization).setDefaultSize(10);
        visualization.getClientProperty().addAttribute("dot::overlap", "false");
        visualization.getClientProperty().addAttribute("dot::model", "subset");
        visualization.setLayout(layout);
        visualization.setVisualColumn("color", graph.getVertexTable()
                .getColumn("type"));
        // AbstractWriter writer =
        // GraphWriterFactory.createGraphWriter("nicole.dot", visualization);
        // if (writer != null) {
        // DOTGraphWriter dot = (DOTGraphWriter) writer;
        // dot.setLabels(vl.getColumn());
        // dot.setColors(VisualColor.get(visualization));
        // writer.write();
        // }
        createFrame(visualization);
    }

    public static String[] defaultArgs = { "data/graph/nicole.xml" };

    public static void main(String[] args) {
        if (args.length == 0) {
            args = defaultArgs;
        }
        new SocialNetworkExample(args, "Social Network Example");

    }

    public static boolean isBipartite(Graph graph, int v) {
        NumberColumn type = (NumberColumn) graph.getVertexTable().getColumn(
                "type");
        int t = type.getIntAt(v);
        for (RowIterator eiter = graph.edgeIterator(v); eiter.hasNext();) {
            int v2 = graph.getOtherVertex(eiter.nextRow(), v);
            if (type.getIntAt(v2) == t)
                return false;
        }
        return true;
    }

    public static void filterGraph(DefaultGraph graph) {
        boolean modified = true;
        while (modified) {
            modified = false;
            for (RowIterator iter = graph.vertexIterator(); iter.hasNext();) {
                int v = iter.nextRow();
                assert (isBipartite(graph, v));
                if (graph.getDegree(v) < 2) {
                    iter.remove();
                    modified = true;
                }
            }
        }
    }

}
