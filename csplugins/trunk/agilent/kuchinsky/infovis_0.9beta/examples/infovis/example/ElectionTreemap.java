/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.example;

import infovis.Column;
import infovis.Visualization;
import infovis.io.AbstractReader;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.TreemapVisualization;
import infovis.tree.visualization.treemap.Strip;
import infovis.utils.Permutation;
import infovis.utils.RowComparator;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

import javax.imageio.ImageIO;

/**
 * Example of specialization of visualization for creating large treemaps of the French elections.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class ElectionTreemap {
    public static void syntax() {
        System.out.println(
            "syntax: ElectionTreemap [-o outimage] [-width int] [-height int] [-visual column] file");
    }
    public static void main(String args[]) {
        String inputFile = null;
        String outputFile = "out.png";
        int width = 1024;
        int height = 768;
        TreeMap visual = new TreeMap();
        
        visual.put("sort", "name");
        visual.put(Visualization.VISUAL_SIZE, "Nombre");
        visual.put(Visualization.VISUAL_LABEL, "name");
        visual.put(Visualization.VISUAL_COLOR, "Couleur");
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-width")) {
                Integer v = Integer.decode(args[i + 1]);
                if (v == null) {
                    syntax();
                    System.exit(1);
                }
                width = v.intValue();
                i++;
            }
            else if (arg.equals("-height")) {
                Integer v = Integer.decode(args[i + 1]);
                if (v == null) {
                    syntax();
                    System.exit(1);
                }
                height = v.intValue();
                i++;
            }
            else if (arg.equals("-o")) {
                outputFile = args[i + 1];
                i++;
            }
            else if (arg.startsWith("-")) {
                String key = arg.substring(1);
                String value = args[i + 1];
                i++;
                visual.put(key, value);
            }
            else {
                if (inputFile != null) {
                    syntax();
                    System.exit(1);
                }
                inputFile = arg;
            }
        }
        if (inputFile == null) {
            syntax();
            System.exit(1);
        }
        final DefaultTree tree = new DefaultTree();
        System.out.println("Reading " + inputFile);
        AbstractReader reader =
            TreeReaderFactory.createTreeReader(inputFile, tree);
        TreemapVisualization visualization =
            new TreemapVisualization(tree, Strip.instance);
        if (reader != null && reader.load()) {
            System.out.println("Done.");
            for (Iterator iter = visual.keySet().iterator();
                iter.hasNext();
                ) {
                String key = (String) iter.next();
                String value = (String) visual.get(key);
                visualization.setVisualColumn(
                    key,
                    tree.getColumn(value));
            }
            final Column order = visualization.getVisualColumn("sort");
            final Column size = visualization.getVisualColumn(Visualization.VISUAL_SIZE);
            Permutation perm = visualization.getPermutation();
            perm.sort(new RowComparator() {
                public int compare(int row1, int row2) {
                    if (tree.isLeaf(row1) && tree.isLeaf(row2)) {
                        return size.compare(row1, row2); 
                    }
                    else {
                        return order.compare(row1, row2);
                    }
                }

                public boolean isValueUndefined(int row) {
                    return order.isValueUndefined(row);
                }
            });
            visualization.setPermutation(perm);
            System.out.println("Rendering " + inputFile);
            Rectangle bounds = new Rectangle(0, 0, width, height);
            BufferedImage image =
                new BufferedImage(
                    width,
                    height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) image.getGraphics();
            visualization.paint(g2d, bounds);
            try {
                System.out.println("Done.\nSaving to " + outputFile);
                ImageIO.write(image, "png", new File(outputFile));
                System.out.println("Done.");
            }
            catch (IOException e) {
                System.err.println("cannot write " + outputFile);
            }
            image.flush();
            image = null;
        }
        else {
            System.out.println("Cannot load file " + inputFile);
            System.exit(1);
        }
    }
}
