/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;
import infovis.Tree;
import infovis.example.ExampleRunner;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.NodeLinkTreeVisualization;
import infovis.tree.visualization.RainbowColumn;
import infovis.visualization.LinkVisualization;
import infovis.visualization.linkShapers.DendrogramLinkShaper;


/**
 * Example of Node Link diagram visualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class NodeLinkTreeExample {
    /**
     * Program that shows the use of node link diagrams for trees.
     * 
     * @param args arguments.
     */
    public static void main(String[] args) {
        ExampleRunner example = new ExampleRunner(args, "NodeLinkTreeExample");

        Tree t = new DefaultTree();
        if (TreeReaderFactory.readTree(example.getArg(0), t)) {
            NodeLinkTreeVisualization visualization = new NodeLinkTreeVisualization(t);
            visualization.setVisualColumn(NodeLinkTreeVisualization.VISUAL_LABEL,
                ExampleRunner.getStringColumn(t, 0));
            RainbowColumn rainbow = RainbowColumn.findColumn(t);
            visualization.setVisualColumn(
                    NodeLinkTreeVisualization.VISUAL_COLOR,
                    rainbow);
            LinkVisualization lv = visualization.getLinkVisualization();
            lv.setLinkShaper(new DendrogramLinkShaper());
            example.createFrame(visualization);
        } else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }
}
