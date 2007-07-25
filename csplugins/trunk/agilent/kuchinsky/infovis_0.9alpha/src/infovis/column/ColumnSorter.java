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
 * Class ColumnSorter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class ColumnSorter extends ColumnLink {
    IntColumn sortedColumn;
    
    public ColumnSorter(Column col) {
        super(col, new IntColumn("#sorted_"+col.getName()));
        sortedColumn = (IntColumn)toColumn;
        updateColumn();
    }
    
    public void update() {
        sortedColumn.clear();
        // Mat changement : for (int i = 0; i < toColumn.getRowCount(); i++) {
        for (int i = 0; i < fromColumn.size(); i++) {
            sortedColumn.add(i);
        }
        // Mat changement : sortedColumn.sort(sortedColumn);
        sortedColumn.sort(fromColumn);
    }


    public IntColumn getSortedColumn() {
        return sortedColumn;
    }

}
