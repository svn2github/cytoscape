package org.cytoscape.bamevent;

/**
 * Defines the time of delivery of notifications to a Listener.
 */
public enum DeliveryTiming {
    // uses constant-specific methods:
    /** 
     * The Listener should immediately be notified. An
     * Listener should only specify DELIVER_IMMEDIATELY
     * when absolutely necessary (e.g., for tasks like showing a
     * progress bar). There may be restrictions on DELIVER_IMMEDIATELY
     * Listeners since the objects surrounding the notification
     * may be in an inconsistent state. For example, if a notification
     * is immediately delivered about a CyNode being added to a CyNetwork
     * and the CyNetwork is in the process of being created, then
     * browsing the CyNetwork could lead to problems since the
     * CyNetwork is not completely created.
     */
    DELIVER_IMMEDIATELY { public String toString() { return "DELIVER_IMMEDIATELY"; }},
    /**
     * The Listener can receive notifications in a delayed fashion where
     * they may be delayed for safety and optimization reasons.
     * This is the normal way Listeners should receive notifications.
     */
    CAN_BE_DELAYED { public String toString() { return "CAN_BE_DELAYED";}};
}
