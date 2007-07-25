/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.Column;
import infovis.column.FilterColumn;
import infovis.panel.DynamicQuery;
import infovis.utils.TableIterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Base abstract class for Dynamic Queries.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public abstract class AbstractDynamicQuery implements DynamicQuery, ChangeListener {
    protected FilterColumn filter;
    protected Column column;
    
    /**
     * Constructor.
     * @param column the column
     */
    public AbstractDynamicQuery(Column column) {
        setColumn(column);
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == column) {
            update();
        }
    }

    /**
     * Method called when the column is modified.
     *
     */
    public abstract  void update();

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    public FilterColumn getFilterColumn() {
        return filter;
    }

    /**
     * {@inheritDoc}
     */
    public void setFilterColumn(FilterColumn filter) {
        if (this.filter != null) {
            this.filter.removeDynamicQuery(this);
        }
        this.filter = filter;
        if (this.filter != null) {
            this.filter.addDynamicQuery(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void apply() {
        if (filter != null) {
            filter.applyDynamicQuery(
                    this, 
                    new TableIterator(0, Math.max(filter.size(), column.size())));
        }
    }

    protected void fireStateChanged() {
        apply();
    }    
}
