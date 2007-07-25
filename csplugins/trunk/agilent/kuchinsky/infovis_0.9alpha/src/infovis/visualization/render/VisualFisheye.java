/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.column.filter.FilterAll;
import infovis.visualization.ItemRenderer;
import infovis.visualization.magicLens.Fisheye;

import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * A <code>VisualFisheye</code> applies a fisheye deformation to
 * its children.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class VisualFisheye extends AbstractVisualColumn {
    public static final String VISUAL = "fisheye";
    protected Fisheye fisheye;

    public static VisualFisheye get(Visualization vis) {
        return (VisualFisheye)findNamed(VISUAL, vis);
    }
    
    public static Fisheye getFisheye(Visualization vis) {
        VisualFisheye fir = get(vis);
        if (fir == null) {
            return null;
        }
        return fir.getFisheye();
    }
    
    public static void setFisheye(Visualization vis, Fisheye fisheye) {
        VisualFisheye vf = VisualFisheye.get(vis);
        if (vf != null) {
            vf.setFisheye(fisheye);
        }
        for (int i = 0; vis.getVisualization(i) != null; i++) {
            setFisheye(vis.getVisualization(i), fisheye);
        }
    }
    
    public VisualFisheye(
            ItemRenderer c1, 
            ItemRenderer c2,
            ItemRenderer c3,
            ItemRenderer c4) {
        super(VISUAL);
        super.filter = FilterAll.getInstance();
        addRenderer(c1);
        addRenderer(c2);
        addRenderer(c3);
        addRenderer(c4);
    }
    
    public VisualFisheye(
            ItemRenderer c1) {
        this(c1, null, null, null);
    }

    public VisualFisheye(
            ItemRenderer c1, 
            ItemRenderer c2) {
        this(c1, c2, null, null);
    }

    public VisualFisheye(
            ItemRenderer c1, 
            ItemRenderer c2,
            ItemRenderer c3) {
        this(c1, c2, c3, null);
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (fisheye != null) {
            shape = fisheye.transform(shape);
        }
        super.paint(graphics, row, shape);
    }
    
    public Column getColumn() {
        return null;
    }
    
    
    public Fisheye getFisheye() {
        return fisheye;
    }
    
    public void setFisheye(Fisheye fisheye) {
        if (this.fisheye == fisheye) return;
        this.fisheye = fisheye;
        getVisualization().repaint();
    }
}
