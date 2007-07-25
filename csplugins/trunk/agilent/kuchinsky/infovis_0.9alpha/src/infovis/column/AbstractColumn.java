/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.utils.*;
import infovis.utils.IntIntSortedMap;
import infovis.utils.RowIterator;

import java.util.EventListener;

import javax.swing.event.*;

/**
 * Class AbstractColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public abstract class AbstractColumn implements Column {
    /**
     * The number of time this column has been modified.
     *
     * This field is used to trigger notifications.
     */
    protected transient int modCount = 0;

    /**
     * The number of time disableNotify() has been called minus the
     * number of time enableNotify() has been called.
     */
    protected transient int inhibitNotify = 0;

    /**
     * List of listeners registered to this column.
     */
    protected transient EventListenerList eventListenerList;
    protected transient ChangeEvent       changeEvent;

    /**
     * Returns the modCount.
     * @return int
     */
    public int getModCount() {
        return modCount;
    }

    /**
     * Disables notification until enableNotify has been called.
     *
     * <p>This method is useful if a large number of modifications is
     * going to happen on the column and notifying each time would be
     * too time consuming.  The notification will be deferred until
     * enableNotify is called.</p>
     *
     * <p>Calls to disableNotify can be nested</p>
     * @see #enableNotify()
     */
    public void disableNotify() {
        inhibitNotify++;
    }

    /**
     * Re enable notifications, triggering eventListeners if
     * modifications occur.
     *
     * @see #disableNotify()
     */
    public void enableNotify() {
        inhibitNotify--;
        if (inhibitNotify <= 0) {
            inhibitNotify = 0;
            fireColumnChanged();
        }
    }

    /**
     * Fire the notification.
     */
    public void fireColumnChanged() {
        if (inhibitNotify > 0 || modCount == 0)
            return;
        if (eventListenerList != null) {
            EventListener[] list = eventListenerList.getListeners(ChangeListener.class);
            for (int i = list.length - 1; i >= 0; i--) {
                ((ChangeListener)list[i]).stateChanged(changeEvent);
            }
        }
        modCount = 0;
    }
    
    protected EventListenerList getEventListenerList() {
        if (eventListenerList == null) {
            eventListenerList = new EventListenerList();
            changeEvent = new ChangeEvent(this);
        }
        return eventListenerList;
    }

    /**
     * Adds a listener to the list that's notified each time a change occurs.
     *
     * @param listener the listener
     */
    public void addChangeListener(ChangeListener listener) {
        getEventListenerList().add(ChangeListener.class, listener);
    }

    /**
     * Removes a listener from the list that's notified each time a change occurs.
     *
     * @param listener the listener
     */
    public void removeChangeListener(ChangeListener listener) {
        if (eventListenerList != null) {
            eventListenerList.remove(ChangeListener.class, listener);
        }
    }

    /**
     * Mark the column as modified.
     *
     * Call notifications if not disabled.
     *
     * @return true if notifications have been called.
     */
    protected boolean modified() {
        modCount++;
        if (eventListenerList != null && inhibitNotify == 0) {
            fireColumnChanged();
            return true;
        }
        return false;
    }

    protected void readonly(String msg) throws ReadOnlyColumnException {
        throw new ReadOnlyColumnException(msg);        
    }
    
    protected void readonly() throws ReadOnlyColumnException {
         readonly("Trying to change a read-only column");        
    }

    public String toString() {
        return getName();
    }

    public IntIntSortedMap computeValueMap(RowComparator comp) {
        IntIntSortedMap map = new IntIntSortedMap(comp == null ? this : comp);
        for (RowIterator iter = iterator(); iter.hasNext(); ) {
            int v = iter.nextRow();
            if (comp != null && comp.isValueUndefined(v)) {
                continue;
            }
            if (map.containsKey(v)) {
                map.put(v, map.get(v)+1);
            }
            else {
                map.put(v, 1);
            }
        }
        
        return map;
    }
    
    public IntIntSortedMap computeValueMap() {
        return computeValueMap(null);
    }
}
