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
import infovis.io.AbstractReader;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.TreemapVisualization;

/**
 * Example of Treemap visualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class TreemapExample {
    public static void main(String[] args) {
        ExampleRunner example =
            new ExampleRunner(args, "TreemapExample");

        Tree t = new DefaultTree();
        AbstractReader reader =
            TreeReaderFactory.createTreeReader(example.getArg(0), t);
        if (reader != null && reader.load()) {
            TreemapVisualization visualization =
                new TreemapVisualization(t);

            example.createFrame(visualization);
        }
        else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }
}
