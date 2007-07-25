/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;

/**
 * Maintains a column sorted according to the order of
 * another column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class ColumnSorter extends ColumnLink {
    IntColumn sortedColumn;
   
    /**
     * Creates a ColumnSorter.
     * 
     * @param col the column used to sort.
     */
    public ColumnSorter(Column col) {
        super(col, new IntColumn("#sorted_"+col.getName()));
        sortedColumn = (IntColumn)toColumn;
        updateColumn();
    }
    
    /**
     * {@inheritDoc}
     */
    public void update() {
        sortedColumn.clear();
        // Mat changement : for (int i = 0; i < toColumn.getRowCount(); i++) {
        for (int i = 0; i < fromColumn.size(); i++) {
            sortedColumn.add(i);
        }
        // Mat changement : sortedColumn.sort(sortedColumn);
        sortedColumn.sort(fromColumn);
    }

    /**
     * Returns the sorted column.
     * @return the sorted column.
     */
    public IntColumn getSortedColumn() {
        return sortedColumn;
    }

}
