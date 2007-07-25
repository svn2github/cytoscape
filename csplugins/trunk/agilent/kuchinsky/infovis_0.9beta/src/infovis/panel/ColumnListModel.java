/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.panel;

import infovis.Table;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * ListModel for columns inside a table.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class ColumnListModel extends AbstractListModel implements
        ComboBoxModel, TableModelListener {
    Table   table;
    Object  selectedObject;
    boolean nullAdded;

    /**
     * Constructor for ColumnListModel.
     */
    public ColumnListModel(Table table) {
        this.table = table;
        table.addTableModelListener(this);
    }

    /**
     * @see javax.swing.event.TableModelListener#tableChanged(TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        fireContentsChanged(this, -1, -1);
    }

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        return table.getColumnAt(index);
    }

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return table.getColumnCount() + (nullAdded ? 1 : 0);
    }

    // implements javax.swing.ComboBoxModel
    /**
     * Sets the value of the selected item. The selected item may be null.
     * <p>
     * 
     * @param anObject
     *            The combo box value or null for no selection.
     */
    public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals(anObject))
                || selectedObject == null && anObject != null) {
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
        }
    }

    // implements javax.swing.ComboBoxModel
    public Object getSelectedItem() {
        return selectedObject;
    }

    /**
     * Returns the table.
     * 
     * @return Table
     */
    public Table getTable() {
        return table;
    }

    /**
     * Returns the nullAdded.
     * 
     * @return boolean
     */
    public boolean isNullAdded() {
        return nullAdded;
    }

    /**
     * Sets the nullAdded.
     * 
     * @param nullAdded
     *            The nullAdded to set
     */
    public void setNullAdded(boolean nullAdded) {
        if (this.nullAdded != nullAdded) {
            this.nullAdded = nullAdded;
            fireContentsChanged(this, -1, -1);
        }
    }

}
