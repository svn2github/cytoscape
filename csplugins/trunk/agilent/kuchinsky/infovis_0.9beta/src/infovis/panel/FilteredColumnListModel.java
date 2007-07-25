/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Table;
import infovis.column.ColumnFilter;
import infovis.column.filter.ComposeOrFilter;
import infovis.column.filter.InternalFilter;
import infovis.table.FilteredTable;

import java.util.Comparator;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


/**
 * Filtered ListModel for Columns.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 */
public class FilteredColumnListModel extends ColumnListModel
    implements TableModelListener {

    /**
     * Constructor for FilteredColumnListModel.
     * @param table the table.
     * @param filter the ColumnFilter.
     */
    public FilteredColumnListModel(Table table, ColumnFilter filter) {
        super(filteredTable(
                table, 
                ComposeOrFilter.create(
                        filter,
                        InternalFilter.sharedInstance())));
    }
    
    public FilteredColumnListModel(Table table) {
        this(table, null);
    }

    public FilteredTable getFilterTable() {
        return (FilteredTable)getTable();
    }
    
    public static FilteredTable filteredTable(Table table, ColumnFilter filter) {
        if (table instanceof FilteredTable) {
            FilteredTable ftable = (FilteredTable) table;
            
            ftable.setFilter(filter);
            return ftable;
        }
        return new FilteredTable(table, filter);
    }
    
    /**
     * Returns the filter
     * @return the filter
     */
    public ColumnFilter getFilter() {
        return getFilterTable().getFilter();
    }

    /**
     * Sets the filter
     * @param filter The filter
     */
    public void setFilter(ColumnFilter filter) {
        getFilterTable().setFilter(filter);
    }
    
    public Comparator getComparator() {
        return getFilterTable().getComparator();
    }
    
    public void setComparator(Comparator comp) {
        getFilterTable().setComparator(comp);
    }
    
    /**
     * @see javax.swing.event.TableModelListener#tableChanged(TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) return;
        fireContentsChanged(this, -1, -1);
    }

}
