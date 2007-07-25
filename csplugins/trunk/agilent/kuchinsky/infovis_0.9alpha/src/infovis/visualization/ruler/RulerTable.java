/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.ruler;

import java.awt.Shape;

import infovis.Table;
import infovis.column.ShapeColumn;
import infovis.column.StringColumn;
import infovis.table.DefaultTable;

/**
 * Class RulerTable
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class RulerTable extends DefaultTable {
    public static final String SHAPE_COLUMN = "shape";
    public static final String LABEL_COLUMN = "label";
    protected ShapeColumn shapeColumn;
    protected StringColumn labelColumn;

    public RulerTable() {
        shapeColumn = new ShapeColumn(SHAPE_COLUMN);
        addColumn(shapeColumn);
        labelColumn = new StringColumn(LABEL_COLUMN);
        addColumn(labelColumn);
    }
    
    public void addRuler(Shape shape, String label) {
        shapeColumn.add(shape);
        labelColumn.add(label);
    }
    
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

    public static Shape getShape(Table table, int row) {
        return getShapes(table).get(row);
    }
    
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
