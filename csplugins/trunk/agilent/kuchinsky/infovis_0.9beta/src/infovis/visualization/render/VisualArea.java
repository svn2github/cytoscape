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
import infovis.visualization.Orientable;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualArea
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class VisualArea extends AbstractVisualColumn implements
        Orientable {
    public static final String VISUAL = "area";

    protected NumberColumn areaColumn;
    protected short orientation = ORIENTATION_NORTH;
    protected double defaultScale = 1;
    protected transient double scale;
    protected transient double origin;
    protected transient AffineTransform TMP_TRANS;
    protected transient TransformedShape TMP_SHAPE;


    public static VisualArea get(Visualization vis) {
        return (VisualArea) findNamed(VISUAL, vis);
    }

    public static VisualArea get(ItemRenderer ir) {
        return (VisualArea) findNamed(VISUAL, ir);
    }
    
    public VisualArea(String name) {
        super(name);
    }

    public VisualArea(ItemRenderer child) {
        super(VISUAL);
        addRenderer(child);
        this.filter = NotNumberFilter.sharedInstance();
    }

    public Column getColumn() {
        return areaColumn;
    }

    public void setColumn(Column column) {
        if (areaColumn == column)
            return;
        super.setColumn(column);
        areaColumn = (NumberColumn) column;
        invalidate();
    }

    public void setOrientation(short orientation) {
        if (this.orientation == orientation)
            return;
        this.orientation = orientation;
        invalidate();
    }

    public short getOrientation() {
        return orientation;
    }

    public void install(Graphics2D graphics) {
        super.install(graphics);
        if (areaColumn == null
                || areaColumn.getMaxIndex() == areaColumn.getMinIndex()) {
            scale = 0;
            origin = 0;
        } else {
            origin = areaColumn.getDoubleMin();
            scale = 1.0 / (areaColumn.getDoubleMax() - origin);
        }
    }

    public double getScaleAt(int row) {
        if (scale == 0 || areaColumn.isValueUndefined(row)) {
            return defaultScale;
        }
        return scale * (areaColumn.getDoubleAt(row) - origin);
    }

    public void paint(Graphics2D graphics, int row, Shape shape) {
        double s = getScaleAt(row);
        if (s == 1) {
            super.paint(graphics, row, shape);
        } else {
            Rectangle2D bounds = shape.getBounds2D();
            double tx, ty;
            double sx, sy;
            switch (orientation) {
            default:
            case ORIENTATION_CENTER:
                tx = bounds.getCenterX();
                ty = bounds.getCenterY();
                sx = sy = s;
                break;
            case ORIENTATION_EAST:
                tx = bounds.getMinX();
                ty = bounds.getCenterY();
                sx = s;
                sy = 1;
                break;
            case ORIENTATION_WEST:
                tx = bounds.getMaxX();
                ty = bounds.getCenterY();
                sx = s;
                sy = 1;
                break;
            case ORIENTATION_NORTH:
                tx = bounds.getCenterX();
                ty = bounds.getMaxY();
                sx = 1;
                sy = s;
                break;
            case ORIENTATION_SOUTH:
                tx = bounds.getCenterX();
                ty = bounds.getMinY();
                sx = 1;
                sy = s;
                break;
            }
            if (TMP_TRANS == null) {
                TMP_TRANS = new AffineTransform();
                TMP_SHAPE = new TransformedShape(shape, TMP_TRANS);
            }
            else {
                TMP_SHAPE.setShape(shape);
            }
            TMP_TRANS.setToIdentity();
            TMP_TRANS.translate(tx, ty);
            TMP_TRANS.scale(sx, sy);
            TMP_TRANS.translate(-tx, -ty);
//            if (shape instanceof Rectangle2D) {
//                Rectangle2D rect = (Rectangle2D) shape;
//                Rectangle2D.Float r = RectPool.allocateRect();
//                r.width = (float)(sx * rect.getWidth());
//                r.height = (float)(sy * rect.getHeight());
//                r.x = (float)((bounds.getWidth()-tx)*sx+tx);
//                r.y = (float)((bounds.getHeight()-ty)*sy+ty);
//                super.paint(graphics, row, r);
//                RectPool.freeRect(r);
//            }
//            else
                super.paint(graphics, row, TMP_SHAPE);
        }
    }

    public double getDefaultScale() {
        return defaultScale;
    }
    
    public void setDefaultScale(double s) {
        if (s < 0 || s > 1) return;
        if (s == defaultScale) return;
        defaultScale = s;
        invalidate();
    }
}