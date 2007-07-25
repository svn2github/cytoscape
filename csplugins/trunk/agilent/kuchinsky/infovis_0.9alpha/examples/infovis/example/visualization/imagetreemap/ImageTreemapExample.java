/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.example.visualization.imagetreemap;

import infovis.Tree;
import infovis.column.StringColumn;
import infovis.example.ExampleRunner;
import infovis.tree.DefaultTree;
import infovis.tree.io.DirectoryTreeReader;
import infovis.tree.visualization.TreemapVisualization;
import infovis.visualization.ItemRenderer;
import infovis.visualization.render.*;

/**
 * Class ImageTreemapExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class ImageTreemapExample {
    public static void main(String args[]) {
        ExampleRunner example =
            new ExampleRunner(args, "ImageTreemapVisualization");
        if (example.fileCount() != 1) {
            System.err.println(
                "Syntax: ImageTreemapVisualization <dir>");
            System.exit(1);
        }
        Tree t = new DefaultTree();
        t.addColumn(new StringColumn(DirectoryTreeReader.COLUMN_PATH));
        DirectoryTreeReader reader =
            new DirectoryTreeReader(example.getArg(0), t);
        if (reader.load()) {
            TreemapVisualization visualization =
                new TreemapVisualization(
                    t,
                    null);
            ItemRenderer ir = new DefaultFillingItemRenderer();
            if (! AbstractItemRenderer.insertAfterNamed(VisualAlpha.VISUAL, new VisualImage(), ir)) {
                System.out.println("Coulnd't find a VisualAlpha to replace");
                System.exit(1);
            }
            visualization.setItemRenderer(ir);
            visualization.setVisualColumn(
                    VisualImage.VISUAL_URL, 
                    t.getColumn(DirectoryTreeReader.COLUMN_PATH));
            example.createFrame(visualization);
        }
        else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }    
}
