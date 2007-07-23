/* -*-Java-*-
********************************************************************************
*
* File:         ListenerList.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/utils/ListenerList.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Tue Jun 14 13:11:57 2005
* Modified:     Tue Jun 14 13:31:01 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/
package cytoscape.hyperedge.impl.utils;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;


/**
 * EventListeners are used to extend the Java delegation event model
 * so that objects you create can listen to and generate events (this
 * is part of an Observer design pattern, as described in Design
 * Patterns by Erich Gamma et. al.).
 * <P>This class is thread-safe except for iterator() and use of
 * getListeners() (see these methods for details).
 *
 * <P>NOTE: This class is similar to
 * <CODE>java.swing.event.EventListenerLists</CODE>, but is not based
 * on class-listener association (it can be instance-listener
 * association) and is more clear on its thread safety.
 *
 * @author Michael L. Creech
 * @version 1.0 */
public class ListenerList
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final int INITIAL_SIZE = 3;

    //~ Instance fields ////////////////////////////////////////////////////////

    final private transient List<EventListener> _listeners = new ArrayList<EventListener>(INITIAL_SIZE);

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds a listener to storage. Duplicates are
     * ignored. true is returned if a listener was actually
     * added to storage.  */
    public boolean addListener (EventListener l)
    {
        if (l == null)
        {
            System.out.println ("Warning: attempted to add a null EventListener! Ignoring.");
            return false;
        }
        synchronized (_listeners)
        {
            if (_listeners.contains (l))
            {
                return false;
            }
            _listeners.add (l);
            return true;
        }
    }

    /**
     * Removes a listener from storage.
     * @param l the EventListener to remove.
     * @return false if <EM>l</EM> is null.
     *        false if <EM>l</EM> is not in this listener
     *        store. In this case, a warning is also issued using
     *        <CODE>Status.logln()</CODE>, true otherwise.
     */
    public boolean removeListener (EventListener l)
    {
        if (l == null)
        {
            return false;
        }
        synchronized (_listeners)
        {
            if (!_listeners.remove (l))
            {
                System.out.println ("Warning: attempted to remove Listener " +
                                    l + " which was never added.");
                return false;
            }
            return true;
        }
    }

    /**
     * Remove all listeners from the store.
     */
    public void clear ()
    {
        synchronized (_listeners)
        {
            _listeners.clear ();
        }
    }

    /**
     * Returns a "snapshot" list of all listeners.
     * This list represents an independent view of the
     * underlying listener storage mechanism (backing object).  This
     * makes this list safely modifiable without affecting the
     * backing object, however note that this list may not continue
     * to reflect the underlying listener storage--especially if other
     * threads call addListeners() or removeListeners() after the
     * snapshot is obtained.  Note that if you want to simply iterate
     * over all listeners to fire notification events, use iterator().
     *
     * <P>This method is thread-compatible.
     * To ensure this list reflects the underlying backing object,
     * you must obtain a lock on this ListenerList before
     * you call this method and retain this lock until you are finished using
     * this list.
     * @see ListenerList#iterator
     */
    public List getListeners ()
    {
        // Make a copy of the listener, in case listeners are added or
        // removed while events are being delivered (multi-threads):
        // Reading as well as writing must be synchronized because reading
        // isn't guaranteed to read the last value written by another thread
        // (See Effective Java by Joshua Bloch, Item 48).
        synchronized (_listeners)
        {
            if (_listeners == null)
            {
                return null;
            }

            List<EventListener> copy = new ArrayList<EventListener>(_listeners.size ());
            copy.addAll (_listeners);
            return (copy);
        }
    }

    /**
     * Does this ListenerList have any listeners?  */

    // Reading as well as writing must be synchronized because reading
    // isn't guaranteed to read the last value written by another thread
    // (See Effective Java by Joshua Bloch, Item 48).
    public boolean hasListeners ()
    {
        synchronized (_listeners)
        {
            return !(_listeners.isEmpty ());
        }
    }

    /**
     * Creates a new ListenerList when necessary.
     * @param ls a ListenerList that may be null.
     * @return creates and returns a new ListenerList if <EM>ls</EM> is
     * null. Otherwise, it returns <EM>ls</EM>
     */
    static public ListenerList setupListenerListWhenNecessary (ListenerList ls)
    {
        if (ls == null)
        {
            ls = new ListenerList();
        }
        return ls;
    }

    /**
     * Returns a <EM>fail-fast</EM> iterator over a list of all
     * listeners.  This is the preferred way to iterate over all
     * listeners to perform tasks such as firing notification events.
     * If the listener store is structurally modified at any time after
     * the iterator is created, in any way except through the iterator's
     * own remove method, the iterator will throw a
     * ConcurrentModificationException.  Thus, during concurrent
     * modification, the iterator fails quickly and cleanly, rather than
     * risking arbitrary, non-deterministic behavior at an undetermined
     * future time.
     * @throws ConcurrentModificationException if the listener store is
     * structurally modified at any time after the iterator is created,
     * in any way except through the iterator's own remove method
     * @see java.util.AbstractList
     */

    // simple wrapper for the List iterator.
    public Iterator iterator ()
    {
        // Reading as well as writing must be synchronized because reading
        // isn't guaranteed to read the last value written by another thread.
        synchronized (_listeners)
        {
            return _listeners.iterator ();
        }
    }
}
