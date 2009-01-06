package org.cytoscape.bamevent;

    /**
     * The details of exactly how a Listener
     * wants to be notified and what it wants to be notified about.
     */

    public interface EventConditions {
	/**
	 * The EventAction associated with this EventConditions.
	 * One special EventAction is EventAction.ANY, which
	 * specifies that any EventAction will match this condition.
	 */
	EventAction getEventAction ();
	/**
	 * The details of exactly how a notification should be delivered.
	 */
	DeliveryOptions getDeliveryOptions();

	/**
	 * The EventFilter to be executed to test if a possible
	 * EventNotification should be given to a Listener.
	 */
	EventFilter getEventFilter();
	/**
	 * Return a human readable String representing the contents of this
	 * object.
	 */
	String toString();
    }
