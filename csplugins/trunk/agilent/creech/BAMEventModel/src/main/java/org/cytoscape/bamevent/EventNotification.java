package org.cytoscape.bamevent;    

import java.util.List;

/**
 * All the information about a particular event.
 * @param <T> the type of the context object--the object the event action affects.
 * <H4>Questions</H4>
 * <OL>
 * <LI> How much supporting information do we need for events?
 *
 * <P>My cursory look at events, I could only find a few cases that
 * required two supporting pieces of information.  One example, is finding out
 * what attribute changed in a CyDataTable, along with it's old value.
 * 
 * <P>Are there any cases that require 3 or more? Should we design for an
 * arbitrary number or just two?
 *
 * <P><LI> What is the best form for the supporting information?</P>
 *
 * <P> The information can be heterogeneous. It would be nice to avoid
 *     forcing casts in the API, but how do we create an easy to use
 *     heterogeneous, typesafe list?  To complicate things, the
 *     EventNotification is used as a parameter in the Listener
 *     callback, EventFilter, and in factory methods, so the solution much not
 *     complicate these references as well.
 * </OL>
 */

public interface EventNotification<T> {
    /**
     * The object notifying about this event. This is the object
     * that fired the event and is the source of the event.
     */
    Notifier getNotifier ();
    /**
     * The object this notification is about, such as a Network.
     * If this notification isn't about a particular object, as in
     * stateless events, like "save your plugin state now", then
     * the context may be a predefined system class or value, such
     * as Cytoscape.class.
     */
    T getContext();
    /**
     * The action on the context object, such as being added or destroyed.
     * 
     */
    EventAction getEventAction ();
    /**
     * A List of Objects that provide supporting information to the event.
     * @return a non-null List, if there is supporting information, or
     * null if no supporting info.
     */
    
    List getSupportingInfo();
    

    /**
     * Return the next notification. These notifications will match
     * this notification in terms of the EventAction, Listener, Context,
     * and Notifier. The only difference will be the supporting information.
     * @return the next EventNotification in a batched set of
     * notifications. This can only return non-null when a set of
     * EventNotifications have been batched together when
     * DeliveryOptions.isCollapsingNotifications() is true and
     * DeliveryTiming is CAN_BE_DELAYED. Otherwise, will return null.
     */ 
    EventNotification<T> getNextCollapsedNotification ();


    /**
     * Return a human readable String representing the contents of this
     * object.
     */

    String toString();
}
