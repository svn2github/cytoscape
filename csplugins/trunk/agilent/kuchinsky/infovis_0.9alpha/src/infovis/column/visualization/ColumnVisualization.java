/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.visualization;

import infovis.Column;
import infovis.Table;
import infovis.column.*;
import infovis.utils.RowIterator;
import infovis.visualization.DefaultVisualization;
import infovis.visualization.Orientation;
import infovis.visualization.render.VisualArea;
import infovis.visualization.ruler.DiscreteRulersBuilder;
import infovis.visualization.ruler.RulerTable;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 * Class ColumnVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class ColumnVisualization extends DefaultVisualization {
    protected Column column;

    protected DoubleColumn rulersSize;
    protected IntColumn rulersColor;
    protected IntColumn rulersRow;
    
    public ColumnVisualization(Table table, Column column) {
        super(table);
        //To be done explicitely
        //createRulers();

        if (table.indexOf(column) == -1) {
            table.addColumn(column);
        }
        this.column = column;
        setVisualColumn("label", column);
        if (column instanceof NumberColumn) {
            setVisualColumn(VisualArea.VISUAL, column);
        }
        setVisualColumn(VISUAL_COLOR, column);
    }

    public Column getColumn() {
        return column;
    }

    public boolean setColumn(Column column) {
        if (this.column == column)
            return false;
        table.removeColumn(this.column);
        this.column = column;
        table.addColumn(column);
        invalidate();
        return true;
    }
    
    public void createRulers() {
        if (rulersSize != null || rulers != null) return;
        rulersSize = new DoubleColumn("size");
        rulersColor = new IntColumn("color");
        rulersRow = new IntColumn("row");
        this.rulers = new RulerTable();
        this.rulers.add(rulersSize);
        this.rulers.add(rulersColor);
        this.rulers.add(rulersRow);
    }

    public void computeShapes(Rectangle2D bounds) {
        int nrows = table.getRowCount();
        clearRulers();
        if (nrows == 0) return;
        double w;
        double h;
        double dw = 0;
        double dh = 0;
        if (Orientation.isHorizontal(getOrientation())) {
            w = bounds.getWidth() / nrows;
            h = bounds.getHeight();
            dw = w;
        }
        else {
            w = bounds.getWidth();
            h = bounds.getHeight() / nrows;
            dh = h;
        }
        int i = 0;
        for (RowIterator iter = iterator(); iter.hasNext(); i++) {
            int row = iter.nextRow();
            
            Rectangle2D.Float s = findRectAt(row);
            s.setRect(bounds.getX() + dw * i, bounds.getY() + dh * i, w, h);
            setShapeAt(row, s);
        }
        computeRulers(bounds);
    }
    
    protected void computeRulers(Rectangle2D bounds) {
        if (rulers == null) return;
        int nrows = table.getRowCount();
        Column labels = getVisualColumn(VISUAL_LABEL);
        double w;
        if (Orientation.isHorizontal(getOrientation())) {
            w = bounds.getWidth() / nrows;
        }
        else {
            w = bounds.getHeight() / nrows;
        }
        int i = 0;
        for (RowIterator iter = iterator(); iter.hasNext(); i++) {
            int row = iter.nextRow();
            String label = (labels != null) ? labels.getValueAt(row) : Integer.toString(row);
            Rectangle2D.Float s = getRectAt(row);
            int ruler = rulers.getRowCount();
            if (Orientation.isHorizontal(getOrientation())) {
                DiscreteRulersBuilder.createVerticalRuler(bounds, label, s.getCenterX(), rulers);
            }
            else {
                DiscreteRulersBuilder.createHorizontalRuler(bounds, label, s.getCenterY(), rulers);
            }
            rulersSize.setExtend(ruler, w);
            rulersColor.setExtend(ruler, computeRulerColor(i));
            rulersRow.setExtend(ruler, row);
        }
    }

    protected int computeRulerColor(int row) {
        if ((row&1) == 0) {
            return Color.BLACK.getRGB();
        }
        else {
            return Color.WHITE.getRGB();
        }
    }
}
