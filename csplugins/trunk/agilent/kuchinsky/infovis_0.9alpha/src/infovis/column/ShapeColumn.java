/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.Table;
import infovis.utils.*;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Columns of shapes and RectPool management.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class ShapeColumn extends BasicObjectColumn {
    protected Rectangle2D.Float bbox;

    public ShapeColumn(String name) {
        super(name);
    }

    public ShapeColumn(String name, int reserve) {
        super(name, reserve);
    }

    public void clear() {
        try {
            disableNotify();
            for (RowIterator iter = iterator(); iter.hasNext();) {
                freeRect(iter.nextRow());
            }
            super.clear();
        } finally {
            enableNotify();
        }
    }

    protected void updateMinMax() {
        if (min_max_updated) {
            return;
        }

        minIndex = -1;
        maxIndex = -1;
        bbox = RectPool.allocateRect();

        for (int i = 0; i < size(); i++) {
            if (!isValueUndefined(i)) {
                Rectangle2D b = get(i).getBounds2D();

                if (minIndex == -1) {
                    bbox.setRect(b);
                    minIndex = i;
                }
                else if (b.getX() < bbox.x) {
                    minIndex = i;
                }

                if (maxIndex == -1 || b.getMaxX() > bbox.getMaxX()) {
                    maxIndex = i;
                }
                bbox.add(b);
            }
        }
        min_max_updated = true;
    }

    public Shape get(int index) {
        return (Shape) getObjectAt(index);
    }

    public void set(int index, Shape s) {
        setObjectAt(index, s);
    }

    public void add(Shape s) {
        super.add(s);
    }

    public Rectangle2D.Float getRect(int index) {
        return (Rectangle2D.Float) getObjectAt(index);
    }

    public Rectangle2D.Float findRect(int index) {
        Rectangle2D.Float r = getRect(index);
        if (r == null) {
            r = RectPool.allocateRect();
            setExtend(index, r);
        }
        return r;
    }

    public void freeRect(int index) {
        Shape s = get(index);
        if (s instanceof Rectangle2D.Float) {
            RectPool.freeRect(s);
        }
        else if (s instanceof CompositeShape) {
            CompositeShape cs = (CompositeShape) s;
            for (int i = 0; i < cs.getShapeCount(); i++) {
                RectPool.freeRect(cs.getShape(i));
            }
        }

        set(index, null);
    }

    public static ShapeColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof ShapeColumn) {
            return (ShapeColumn) c;
        }
        else {
            return null;
        }
    }
    
    public static ShapeColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof ShapeColumn) {
            return (ShapeColumn) c;
        }
        else {
            return null;
        }
    }
    
    public static ShapeColumn findColumn(Table t, String name) {
        ShapeColumn c = getColumn(t, name);
        if (c == null) {
            c = new ShapeColumn(name);
            t.addColumn(c);
        }
        return c;
    }


    public Object definedValue() {
        return RectPool.allocateRect();
    }

    public Rectangle2D.Float getBounds() {
        updateMinMax();
        return bbox;
    }
}