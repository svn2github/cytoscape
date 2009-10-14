package org.cytoscape.bamevent;

/**
 * Various aspects of how EventNotifications should be delivered to an
 * Listener.
 */
public interface DeliveryOptions {
    /**
     * Returns whether the Listener wishes to receive the
     * events as soon as they occur versus possibly having them
     * accumulate before notification (for safety and optimization). A
     * Listener should only specify it wants immediate events
     * when absolutely necessary (e.g., for tasks like showing a
     * progress bar). There may be restrictions on immediate
     * Listeners since the objects surrounding the notification
     * may be in an inconsistent state. For example, if a notification
     * is immediately delivered about a CyNode being added to a CyNetwork
     * and the CyNetwork is in the process of being created, then
     * browsing the CyNetwork could lead to problems since the
     * CyNetwork is not completely created.
     */
    DeliveryTiming getDeliveryTiming ();

    /**
     * When true, specifies that when a notification is batched, if
     * duplicates of this notification occur in the batch, they can be
     * collapsed into one notification (with chaining to the other
     * notifications). This is useful for improving efficiency for
     * Listeners that perform the same operations, such as
     * refreshing a GUI display.  collapsing notifications only makes
     * sense when the DeliveryTiming is CAN_BE_DELAYED.
     */
    boolean isCollapsingNotifications ();

    /**
     * The manner in which a Listener should be notified.  For
     * example, if the Listener will make visual modifications
     * the receiveNotification() method must be run in the
     * event-dispatch thread (DeliveryMethod.SWING_DELIVERY).  Also,
     * if the Listener may be doing some large computation, the
     * receiveNotification() method may want to be run asynchronously
     * in a separate thread (DeliveryMethod.ASYNCHRONOUS).
     */
    DeliveryMethod getDeliveryMethod ();   
    /**
     * Return a human readable String representing the contents of this
     * object.
     */
    String toString();
}
