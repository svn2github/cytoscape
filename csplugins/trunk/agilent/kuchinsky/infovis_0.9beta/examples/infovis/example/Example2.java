/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example;
import infovis.io.AbstractReader;
import infovis.panel.*;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.TreemapVisualization;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.apache.log4j.BasicConfigurator;

/**
 * Example of Treemap visualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class Example2 {
    public static void main(String[] args) {
        BasicConfigurator.configure(); // Configure log4j
        String fileName =
            (args.length == 0) ? "data/election.tm3" : args[0];
        DefaultTree t = new DefaultTree();
        AbstractReader reader =
            TreeReaderFactory.createTreeReader(fileName, t);
        if (reader == null || !reader.load()) {
            System.err.println("cannot load " + fileName);
        }

        TreemapVisualization visualization =
            new TreemapVisualization(t, null);
        ControlPanel control =
            ControlPanelFactory.createControlPanel(
                visualization);
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new VisualizationPanel(control.getVisualization()),
                control);
        split.setResizeWeight(1.0);
        JFrame frame = new JFrame(fileName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(split);
        frame.setVisible(true);
        frame.pack();
    }
}
