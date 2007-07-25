/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.ruler;

import infovis.Table;
import infovis.column.ShapeColumn;
import infovis.column.StringColumn;
import infovis.table.DefaultTable;

import java.awt.Shape;

/**
 * Class for managing rulers associated with a visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class RulerTable extends DefaultTable {
    /** Name of column containing the shapes managed by this table. */
    public static final String SHAPE_COLUMN = "shape";
    /** Name of column containing the labels managed by this table. */
    public static final String LABEL_COLUMN = "label";
    protected ShapeColumn shapeColumn;
    protected StringColumn labelColumn;

    /** 
     * Constructor.
     *
     */
    public RulerTable() {
        shapeColumn = new ShapeColumn(SHAPE_COLUMN);
        addColumn(shapeColumn);
        labelColumn = new StringColumn(LABEL_COLUMN);
        addColumn(labelColumn);
    }
    
    /**
     * Adds a new shape and associated label to this table.
     * @param shape the shape
     * @param label the label
     */
    public void addRuler(Shape shape, String label) {
        shapeColumn.add(shape);
        labelColumn.add(label);
    }
    
    /**
     * Adds a new shape and associated label to a specigied table.
     * @param table the table
     * @param shape the shape
     * @param label the label
     */
    public static void addRuler(Table table, Shape shape, String label) {
        if (table instanceof RulerTable) {
            RulerTable rt = (RulerTable) table;
            rt.addRuler(shape, label);
        }
        else {
            ShapeColumn.findColumn(table, SHAPE_COLUMN).add(shape);
            table.getColumn(LABEL_COLUMN).addValueOrNull(label);
        }
    }
    
    /**
     * Sets the name of the axis for the horizontal or vertial axis.
     * @param table the table
     * @param label the label
     * @param vertical true if the label is for the vertical axis,
     * false otherwise.
     */
    public static void setAxisLabel(Table table, String label, boolean vertical) {
        String attributeName = vertical ? "VERTICAL_LABEL" : "HORIZONTAL_LABEL";
        table.getMetadata().addAttribute(attributeName, label);
    }
    
    /**
     * Returns the name of the horizontal or vertial axis.
     * @param table the table
     * @param vertical true if the vertical axis label should be returned,
     * false otherwise.
     * @return the name of the horizontal or vertial axis.
     */
    public static String getAxisLabel(Table table, boolean vertical) {
        String attributeName = vertical ? "VERTICAL_LABEL" : "HORIZONTAL_LABEL";
        return (String)table.getMetadata().getAttribute(attributeName);
    }

    /**
     * Returns a shape from the shape column stored in the table.
     * @param table the table
     * @param row the row
     * @return a shape from the shape column stored in the table.
     */
    public static Shape getShape(Table table, int row) {
        return getShapes(table).get(row);
    }
    
    /**
     * Returns the ShapeColumn associated with the table.
     * @param table the table
     * @return the ShapeColumn associated with the table.
     */
    public static ShapeColumn getShapes(Table table) {
        if (table instanceof RulerTable) {
            RulerTable rt = (RulerTable) table;
            return rt.shapeColumn;
        }
        else {
            return ShapeColumn.findColumn(table, SHAPE_COLUMN);
        }
        
    }
}
