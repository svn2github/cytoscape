/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.event;

import javax.swing.event.ChangeEvent;

/**
 * Event produced by columns containing a detail about their
 * change. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class ColumnChangeEvent extends ChangeEvent {
    /** Value returned by getDetail when the column size has changed. */
    public static final int CHANGE_SIZE = -3;
    /** Value returned by getDetail when the whole column has changed. */
    public static final int CHANGE_ALL = -2;
    /** Value returned by getDetail when no value has changed. */
    public static final int CHANGE_NONE = -1;
    private int detail;
   
    /**
     * Constructor.
     * @param column the column
     * @param detail the detail.
     */
    public ColumnChangeEvent(Object column, int detail) {
        super(column);
        this.detail = detail;
    }
    
    /**
     * Constructor.
     * @param column the column.
     */
    public ColumnChangeEvent(Object column) {
        this(column, CHANGE_ALL);
    }

    /**
     * Returns the detail of changes.
     * @return the detail of changes.
     */
    public int getDetail() {
        return detail;
    }
}
