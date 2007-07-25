/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.util.EventListener;

import javax.swing.event.*;

public class ChangeManager {

    /**
     * The number of time this column has been modified.
     * 
     * This field is used to trigger notifications.
     */
    private transient int                 modCount        = 0;

    /**
     * The number of time disableNotify() has been called minus the number of
     * time enableNotify() has been called.
     */
    private transient int                 inhibitNotify   = 0;

    public ChangeManager() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * List of listeners registered to this column.
     */
    protected transient EventListenerList eventListenerList;

    /**
     * Returns the modCount.
     * 
     * @return int
     */
    public int getModCount() {
        return modCount;
    }


    /**
     * Disables notification until enableNotify has been called.
     * 
     * <p>
     * This method is useful if a large number of modifications is going to
     * happen on the column and notifying each time would be too time consuming.
     * The notification will be deferred until enableNotify is called.
     * </p>
     * 
     * <p>
     * Calls to disableNotify can be nested
     * </p>
     * 
     * @see #enableNotify()
     */
    public void disableNotify() {
        inhibitNotify++;
    }

    /**
     * Re enable notifications, triggering eventListeners if modifications
     * occur.
     * 
     * @see #disableNotify()
     */
    public void enableNotify() {
        inhibitNotify--;
        if (inhibitNotify <= 0) {
            inhibitNotify = 0;
            fireChanged();
        }
    }

    protected ChangeEvent createChangeEvent() {
        return new ChangeEvent(this);
    }

    /**
     * Fire the notification.
     */
    protected void fireChanged() {
        if (inhibitNotify > 0 || modCount == 0) {
            return;
        }
        try {
            if (eventListenerList != null) {
                EventListener[] list = eventListenerList
                        .getListeners(ChangeListener.class);
                ChangeEvent changeEvent = createChangeEvent();
                for (int i = 0; i < list.length; i++) {
                    ((ChangeListener) list[i]).stateChanged(changeEvent);
                }
            }
        } finally {
            modCount = 0;
        }
    }

    protected EventListenerList getEventListenerList() {
        if (eventListenerList == null) {
            eventListenerList = new EventListenerList();
        }
        return eventListenerList;
    }

    /**
     * Adds a listener to the list that's notified each time a change occurs.
     * 
     * @param listener
     *            the listener
     */
    public void addChangeListener(ChangeListener listener) {
        getEventListenerList().add(ChangeListener.class, listener);
    }

    /**
     * Removes a listener from the list that's notified each time a change
     * occurs.
     * 
     * @param listener
     *            the listener
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
        if (eventListenerList != null) {
            if (inhibitNotify == 0) {
                fireChanged();
                return true;
            }
        }
        return false;
    }
}
