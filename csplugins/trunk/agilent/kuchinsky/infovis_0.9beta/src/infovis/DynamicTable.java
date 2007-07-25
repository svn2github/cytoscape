/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis;

/**
 * A Dynamic Table is a Table that can have elements removed.
 * 
 * <p>When a row is removed from a table, its index is considered
 * free and can be reused when creating a new row. 
 * The <code>isValid(int row)</code> can be used to test whether a row
 * is free.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public interface DynamicTable extends Table {
    /**
     * Creates a new row and returns it.
     * 
     * @return the new created row.
     */
    int addRow();
    
    /**
     * Removes a specified row.
     * 
     * @param row the row to remove.
     */
    void removeRow(int row);
}
