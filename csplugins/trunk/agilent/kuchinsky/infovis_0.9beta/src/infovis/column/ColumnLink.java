/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import java.io.Serializable;

import infovis.Column;
import infovis.metadata.DependencyMetadata;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Link together two columns, applying an abstract method each time the first
 * is modified to recompute the second.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public abstract class ColumnLink implements ChangeListener, Serializable {
    protected Column fromColumn;
    protected Column toColumn;
    static transient boolean   inNotification;

    /**
     * Creates a new ColumnLink object.
     *
     * @param from the column to track.
     * @param to the column to update.
     */
    public ColumnLink(Column from, Column to) {
        this.fromColumn = from;
        this.toColumn = to;
        DependencyMetadata.addDependentColumn(from, to);
        from.addChangeListener(this);
    }

    /**
     * This method is called each time the from Column is modified.
     * It should recompute the other column.
     */
    public abstract void update();

    /**
     * Removes the link.
     */
    public void dispose() {
        if (fromColumn != null) {
            DependencyMetadata.removeDependentColumn(fromColumn, toColumn);
            fromColumn.removeChangeListener(this);
            fromColumn = null;
        }
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        updateColumn();
    }

    /**
     * Make sure the destination column is synchronized with the dependant columns.
     *
     */
    public synchronized void updateColumn() {
        if (inNotification) {
            throw new RuntimeException("Cycle in Column links");
        }
        try {
            inNotification = true;
            toColumn.disableNotify();
            update();
        } finally {
            toColumn.enableNotify();
            inNotification = false;
        }
    }
    
    /**
     * Returns the FROM column.
     * @return the FROM column
     */
    public Column getFromColumn() {
        return fromColumn;
    }

    /**
     * Returns the TO column.
     * @return the TO column
     */
    public Column getToColumn() {
        return toColumn;
    }

}
