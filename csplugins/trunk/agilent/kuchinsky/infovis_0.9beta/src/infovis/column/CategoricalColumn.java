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
import infovis.column.format.CategoricalFormat;
import infovis.metadata.ValueCategory;

import java.util.Map;
import java.util.TreeMap;


/**
 * Specialization of an IntColumn storing categorical values.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.20 $
 * 
 * @infovis.factory ColumnFactory "categorical" DENSE
 * @infovis.factory ColumnFactory "cat" DENSE
 */
public class CategoricalColumn extends IntColumn {
    private static final long serialVersionUID = 8628407342150620750L;

    /**
     * Constructor for CategoricalColumn.
     * @param name the column name
     */
    public CategoricalColumn(String name) {
	this(name, 10, null);
    }

    /**
     * Constructor for CategoricalColumn.
     *
     * @param name the Column name.
     * @param reserve the initial reserved size.
     * @param map the initial category map.
     */
    public CategoricalColumn(String name, int reserve, Map map) {
	super(name, reserve);
	if (map == null) {
	    map = new TreeMap();
	}
	setFormat(new CategoricalFormat(name));
	getMetadata().addAttribute(
            ValueCategory.VALUE_CATEGORY_TYPE, 
            ValueCategory.VALUE_CATEGORY_TYPE_CATEGORICAL);
    }

    /**
     * Returns a column as a <code>IntColumn</code> from an
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param index index in the <code>DefaultTable</code>
     *
     * @return a <code>IntColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>IntColumn</code>.
     */
    public static IntColumn getColumn(Table t, int index) {
	Column c = t.getColumnAt(index);

	if (c instanceof CategoricalColumn) {
	    return (CategoricalColumn)c;
	} else {
	    return null;
	}
    }

    /**
     * Returns a column as a <code>IntColumn</code> from a table,
     * creating it if needed.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a column as a <code>IntColumn</code> from a table,
     */
    public static IntColumn findColumn(Table t, String name) {
	Column c = t.getColumn(name);
	if (c == null) {
	    c = new CategoricalColumn(name);
	    t.addColumn(c);
	}
	return (CategoricalColumn)c;
    }
}
