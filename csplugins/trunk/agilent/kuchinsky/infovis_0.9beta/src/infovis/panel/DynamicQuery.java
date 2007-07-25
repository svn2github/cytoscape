/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Column;
import infovis.column.FilterColumn;
import infovis.utils.RowFilter;

import javax.swing.JComponent;

/**
 * Interface for Dynamic Query objects.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public interface DynamicQuery extends RowFilter {
    /**
     * Returns the associated Column.
     * 
     * @return the associated Column.
     */
    public Column getColumn();
    
    public FilterColumn getFilterColumn();
    
    public void setFilterColumn(FilterColumn filter);

    /**
     * Returns true if the value at that row is filtered out.
     * 
     * @param row the row to check.
     * 
     * @return true if the value at that row is filtered out.
     */
    public boolean isFiltered(int row);
	
    /**
     * Apply the DynamicQuery to the specified <code>FilterColumn</code>
     * 
     */
    public void apply();
	
    /**
     * Returns the <code>Component</code> associated with this dynamic query.
     */
    public JComponent getComponent();
	
}
