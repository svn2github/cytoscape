/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization.icicletree;
import infovis.Tree;
import infovis.example.*;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.IcicleTreeVisualization;


/**
 * Example of Node Link diagram visualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class IcicleTreeExample {
    /**
     * ImageTreemapExample program.
     *
     * @param args args.
     */
    public static void main(String[] args) {
        ExampleRunner example = new ExampleRunner(args, "IcicleTreeExample");
        if (example.fileCount() != 1) {
            System.err.println("Syntax: IcicleTreeExample <name>");
            System.exit(1);
        }
        Tree           t = new DefaultTree();
        if (TreeReaderFactory.readTree(example.getArg(0), t)) {
            IcicleTreeVisualization visualization = new IcicleTreeVisualization(t);

            visualization.setVisualColumn(IcicleTreeVisualization.VISUAL_LABEL,
                ExampleRunner.getStringColumn(t, 0));
            visualization.setInteractor(new EditableIcicleTree(visualization));
            example.createFrame(visualization);
        } else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }
}
