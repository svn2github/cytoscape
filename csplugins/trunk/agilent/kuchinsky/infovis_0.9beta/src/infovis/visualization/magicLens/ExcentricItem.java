/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.Table;
import infovis.Visualization;
import infovis.utils.CompositeShape;
import infovis.visualization.render.*;

import java.awt.*;
import java.awt.geom.*;

/**
 * Class ExcentricItem
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */

public class ExcentricItem implements LabeledComponent.LabeledItem {
    protected int index;
    protected Visualization visualization;
        
    public ExcentricItem(Visualization visualization, int index) {
        this.visualization = visualization;
        this.index = index;
    }
    
    public static Point2D getPreciseShapeCenterIn(Shape s, Rectangle2D focus, Point2D ptOut) {
        if (s instanceof Rectangle) {
            return getShapeCenterIn(s, focus, ptOut);
        }
        else if (s instanceof Point2D) {
            ptOut.setLocation((Point2D)s);
        }
        else {
            Area area = new Area(s);
            area.intersect(new Area(focus));
            Rectangle2D inter = area.getBounds2D();
            ptOut.setLocation(
                    inter.getCenterX(),
                    inter.getCenterY());
        }
        return ptOut;
    }

    public static Point2D getShapeCenterIn(Shape s, Rectangle2D focus, Point2D ptOut) {
        if (s instanceof CompositeShape) {
            CompositeShape cs = (CompositeShape) s;
            for (int i = 0; i < cs.getShapeCount(); i++) {
                Shape shape = cs.getShape(i);
                if (shape.intersects(focus)) {
                    return getShapeCenterIn(shape, focus, ptOut);
                }
            }
        }
        Rectangle2D.Double inter = new Rectangle2D.Double();
        Rectangle2D rect = s.getBounds2D();
        Rectangle2D.intersect(focus, rect, inter);
        ptOut.setLocation(
            inter.getCenterX(),
            inter.getCenterY());
        return ptOut;
    }
                
    public Component getComponent() {
        return visualization.getParent();
    }
        
    public Shape getShape() {
        return visualization.getShapeAt(index);
    }
        
    public Point2D getCenterIn(Rectangle2D focus, Point2D ptOut) {
        Shape s = visualization.getShapeAt(index);
        if (s == null) {
            return null;
        }
        return getShapeCenterIn(s, focus, ptOut);
    }


    public Color getColor() {
        VisualColor vc = VisualColor.get(visualization);
        if (vc == null) return Color.BLACK;
        return vc.getColorAt(index);
    }

    public String getLabel() {
        VisualLabel vl = VisualLabel.get(visualization);
        if (vl != null)
            return vl.getLabelAt(index);
        else
            return null;
    }
        
    public String toString() {
        return ""+index;
    }
        
    public Table getTable() {
        return visualization.getTable();
    }
        
    public int getIndex() {
        return index;
    }
        
    public boolean equals(Object obj) {
        if (obj instanceof ExcentricItem) {
            ExcentricItem item = (ExcentricItem) obj;
            return item.index == index
                && item.getTable() == getTable();
        }
        return false;
    }
        
    public int hashCode() {
        return getTable().hashCode() + index;
    }
}
    
