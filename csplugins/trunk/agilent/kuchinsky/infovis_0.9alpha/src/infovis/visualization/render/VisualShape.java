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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.*;

/**
 * Choose the shape of items depending on the value of a
 * integer/categorical column
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class VisualShape extends AbstractVisualColumn {
    public static final String VISUAL = "shape";
    protected NumberColumn shapeColumn;
    private static int[] xtri = { 0, 1, 2};
    private static int[] ytri = { 0, -2, 0};
    private static int[] xdiamond = { 0, 1, 2, 1};
    private static int[] ydiamond = { 0, 1, 0, -1};
    protected static Shape[] defaultShapeRepertoire = {
            new Rectangle2D.Float(0, 0, 1, 1),
            new Ellipse2D.Float(0, 0, 1, 1),
            new RoundRectangle2D.Float(0, 0, 100, 100, 10, 10),
            new Polygon(xtri, ytri, xtri.length),
            new Polygon(xdiamond, ydiamond, xdiamond.length),
    };
    
    protected Shape[] shapeRepertoire;
    protected int defaultShape = -1;
    protected transient AffineTransform TMP_TRANS;
    protected transient TransformedShape TMP_SHAPE;
    
    public static VisualShape get(Visualization vis) {
        return (VisualShape)findNamed(VISUAL, vis);
    }
    
    public VisualShape(
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
    
    public VisualShape(ItemRenderer child) {
        this(child, -1, defaultShapeRepertoire);
    }
    
    public VisualShape(ItemRenderer c1, ItemRenderer c2) {
        this(c1);
        addRenderer(c2);
    }
    
    public Column getColumn() {
        return shapeColumn;
    }
    
    public void setColumn(Column column) {
        if (shapeColumn == column) return;
        super.setColumn(column);
        shapeColumn = (NumberColumn)column;
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
        if (shapeColumn == null
                || shapeColumn.isValueUndefined(row)) {
            return null;
        }
        int i = shapeColumn.getIntAt(row);
        if (i < 0) {
            return null;
        }
        i = i % shapeRepertoire.length;
        return shapeRepertoire[i];
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (shapeColumn != null) {
            Shape s = getShapeAt(row);
            if (s != null) {
                Rectangle2D bounds = shape.getBounds2D();
                Rectangle2D sbounds = s.getBounds2D();
                if (TMP_TRANS == null) {
                    TMP_TRANS = new AffineTransform();
                    TMP_SHAPE = new TransformedShape(s, TMP_TRANS);
                }
                TMP_TRANS.setTransform(
                        bounds.getWidth()/sbounds.getWidth(),
                        0,
                        0,
                        bounds.getHeight()/sbounds.getHeight(),
                        bounds.getX()-sbounds.getX(), 
                        bounds.getY()-sbounds.getY());
                TMP_SHAPE.setShape(s);
//                AffineTransform saved = graphics.getTransform();
//                try {
//                    graphics.transform(TMP_TRANS);
//                    super.paint(graphics, row, s);
//                }
//                finally {
//                    graphics.setTransform(saved);
//                }
                super.paint(graphics, row, TMP_SHAPE);
                return;
            }
        }
        super.paint(graphics, row, shape);
    }
    
}
