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
import infovis.column.NumberColumn;
import infovis.column.filter.NotNumberFilter;
import infovis.utils.TransformedShape;
import infovis.visualization.ItemRenderer;

import java.awt.*;
import java.awt.geom.*;

/**
 * Class VisualArrowHead
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VisualArrowHead extends AbstractVisualColumn {
    public static final String VISUAL = "arrowHead";
    public static final int SHAPE_ARROW = 0;
    public static final int SHAPE_DIAMOND = 1;
    protected NumberColumn arrowHeadColumn;
    private static int[] xtri = { 10, 0, 10};
    private static int[] ytri = { 10, 0, -10};
    private static int[] xdiamond = { 10, 5, 0, 5};
    private static int[] ydiamond = { 5, 10, 5, 0};
    protected static Shape[] defaultShapeRepertoire = {
            new Polygon(xtri, ytri, xtri.length),
            new Ellipse2D.Float(0, 0, 10, 10),
            new Polygon(xdiamond, ydiamond, xdiamond.length),
    };
    
    protected Shape[] shapeRepertoire;
    protected int defaultShape = -1;
    protected transient AffineTransform TMP_TRANS;
    protected transient TransformedShape TMP_SHAPE;
    
    public static VisualArrowHead get(Visualization vis) {
        return (VisualArrowHead)findNamed(VISUAL, vis);
    }

    public VisualArrowHead(
            ItemRenderer child,
            int defaultShape,
            Shape[] shapeRepertoire) {
        super(VISUAL);
        addRenderer(child);
        this.filter = NotNumberFilter.sharedInstance();
                this.defaultShape = defaultShape;
        if (shapeRepertoire == null) {
            shapeRepertoire = defaultShapeRepertoire;
        }
        this.shapeRepertoire = shapeRepertoire;
    }
    
    public VisualArrowHead(ItemRenderer child) {
        this(child, -1, defaultShapeRepertoire);
    }
    
    public VisualArrowHead(ItemRenderer c1, ItemRenderer c2) {
        this(c1);
        addRenderer(c2);
    }

    public Column getColumn() {
        return arrowHeadColumn;
    }
    
    public void setColumn(Column column) {
        if (arrowHeadColumn == column) return;
        super.setColumn(column);
        arrowHeadColumn = (NumberColumn)column;
        invalidate();
    }
    
    public int getDefaultShape() {
        return defaultShape;
    }
    
    public void setDefaultShape(int defaultShape) {
        this.defaultShape = defaultShape;
        invalidate();
    }
    public Shape[] getShapeRepertoireReference() {
        return shapeRepertoire;
    }
    public void setShapeRepertoire(Shape[] shapeRepertoire) {
        this.shapeRepertoire = shapeRepertoire;
        invalidate();
    }
    
    public static Shape[] getDefaultShapeRepertoire() {
        return defaultShapeRepertoire;
    }
    
    public static void setDefaultShapeRepertoire(Shape[] def) {
        defaultShapeRepertoire = def;
    }
    
    public Shape getShapeAt(int row) {
        int i;
        if (arrowHeadColumn == null
                || arrowHeadColumn.isValueUndefined(row)) {
            i = defaultShape;
        }
        else
         i = arrowHeadColumn.getIntAt(row);
        if (i < 0) {
            return null;
        }
        i = i % shapeRepertoire.length;
        return shapeRepertoire[i];
    }
    
    public static AffineTransform computeTransform(
            float x0, float y0, float x1, float y1,
            AffineTransform t) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float norm = (float)Math.sqrt(dx*dx+dy*dy);
        if (t == null) {
            t = new AffineTransform();
        }
        if (norm == 0) {
            t.setToIdentity();
            return t;
        }
        float cos = dx / norm;
        float sin = dy / norm;
        t.setTransform(cos, sin, -sin, cos, x0, y0);
        return t;
    }
    
    public void computeTransform(Shape s) {
        PathIterator pi = s.getPathIterator(null);
        float x0 = 0, y0 = 0, x1 = 0, y1 = 0;
        float[] coords = new float[6];
        while(! pi.isDone()) {
            int seg = pi.currentSegment(coords);
            switch(seg) {
            case PathIterator.SEG_MOVETO:
                x0 = x1 = coords[0];
                y0 = y1 = coords[1];
                break;
            case PathIterator.SEG_LINETO:
                x1 = x0;
                y1 = y0;
                x0 = coords[0];
                y0 = coords[1];
                break;
            case PathIterator.SEG_QUADTO:
                x1 = coords[0];
                y1 = coords[1];
                x0 = coords[2];
                y0 =  coords[3];
                break;
            case PathIterator.SEG_CUBICTO:
                x1 = coords[2];
                y1 = coords[3];
                x0 = coords[4];
                y0 =  coords[5];
                break;
            case PathIterator.SEG_CLOSE:
            }
            pi.next();
        }
        
        computeTransform(x0, y0, x1, y1, TMP_TRANS);
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        Shape s = getShapeAt(row);
        if (s == null) {
            return;
        }
        if (TMP_TRANS == null) {
            TMP_TRANS = new AffineTransform();
            TMP_SHAPE = new TransformedShape(s, TMP_TRANS);
        }
        computeTransform(shape);
        TMP_SHAPE.setShape(s);
        super.paint(graphics, row, TMP_SHAPE);
    }
}
