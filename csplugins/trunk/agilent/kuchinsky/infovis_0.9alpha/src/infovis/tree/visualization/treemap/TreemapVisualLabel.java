/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.Tree;
import infovis.visualization.ItemRenderer;
import infovis.visualization.render.VisualLabel;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Class TreemapLabel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class TreemapVisualLabel extends VisualLabel {
    protected transient Tree tree;

    public TreemapVisualLabel(ItemRenderer child) {
        super(child);
    }
    
    public void install(Graphics2D graphics) {
        super.install(graphics);
        tree = (Tree)getVisualization();
    }
    
    public void paint(Graphics2D graphics, int row, Shape s) {
        if (tree.isLeaf(row)) {
            super.paint(graphics, row, s);
        }
        else {
            String label = getLabelAt(row);
            if (label == null) {
                return;
            }

            Rectangle2D r = s.getBounds2D();
            FontMetrics fm = graphics.getFontMetrics();
            Graphics2D g = (Graphics2D)graphics.create(
                    (int)r.getX(), (int)r.getY(),
                    (int)r.getWidth(), (int)r.getHeight());
            contrastColor(g);
            g.drawString(
                    label, 
                    1,
                    1 + fm.getAscent());
        }
    }
}
