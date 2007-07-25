/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.Tree;
import infovis.visualization.render.DefaultVisualLabel;

import java.awt.*;

/**
 * VisualLabel for treemaps.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class TreemapVisualLabel extends DefaultVisualLabel {
    protected transient Tree tree;

    /**
     * Constructor.
     *
     */
    public TreemapVisualLabel() {
    }
    
    /**
     * Constructor with default color.
     * @param def the default color.
     */
    public TreemapVisualLabel(Color def) {
        super(def);
    }
    
    /**
     * {@inheritDoc}
     */
    public void install(Graphics2D graphics) {
        super.install(graphics);
        tree = (Tree)getVisualization();
    }
    
    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape s) {
        if (tree.isLeaf(row)) {
            super.paint(graphics, row, s);
        }
        else {
//            String label = getLabelAt(row);
//            if (label == null) {
//                return;
//            }
//
//            Rectangle2D r = s.getBounds2D();
//            FontMetrics fm = graphics.getFontMetrics();
//            Graphics2D g = (Graphics2D)graphics.create(
//                    (int)r.getX(), (int)r.getY(),
//                    (int)r.getWidth(), (int)r.getHeight());
//            contrastColor(g, row);
//            g.drawString(
//                    label, 
//                    1,
//                    1 + fm.getAscent());
            float sj = justification;
            float svj = vjustification;
            try {
                justification = -3;
                vjustification = -1;
                super.paint(graphics, row, s);
            }
            finally {
                justification = sj;
                vjustification = svj;
            }
        }
    }
}
