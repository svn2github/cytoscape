/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import infovis.Column;
import infovis.column.FilterColumn;
import infovis.panel.DynamicQuery;

/**
 * Class AbstractDynamicQuery
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public abstract class AbstractDynamicQuery implements DynamicQuery, ChangeListener {
    protected FilterColumn filter;
    protected Column column;
    
    public AbstractDynamicQuery(Column column) {
        setColumn(column);
    }


    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == column) {
            update();
        }
    }

    public abstract  void update();

    public abstract boolean isFiltered(int row);

    /**
     * Returns the column.
     * @return Column
     */
    public Column getColumn() {
        return column;
    }

    /**
     * Sets the column.
     * @param column The column to set
     */
    public void setColumn(Column column) {
        if (column == this.column)
            return;
        if (this.column != null)
            this.column.removeChangeListener(this);
        this.column = column;
        if (this.column != null)
            this.column.addChangeListener(this);
        update();
    }

    public FilterColumn getFilterColumn() {
        return filter;
    }

    public void setFilterColumn(FilterColumn filter) {
        if (this.filter != null) {
            this.filter.removeDynamicQuery(this);
        }
        this.filter = filter;
        if (this.filter != null) {
            this.filter.addDynamicQuery(this);
        }
    }

    public void apply() {
        if (filter != null)
            filter.applyDynamicQuery(this, column.iterator());
    }

    protected void fireStateChanged() {
        apply();
    }    
}
