package org.cytoscape.bamevent;

    /**
     * Defines the method of delivering notifications to a Listener.
     */
    public enum DeliveryMethod {
	/**
	 * The Listener will receive EventNotifications in a
	 * synchronous manner with the Notifier
	 */
	SYNCHRONOUS { public String toString() { return "SYNCHRONOUS"; }},
	/**
	 * The Listener will receive EventNotifications in a
	 * an asynchronous manner with the Notifier. The Notifier will
	 * continue to run independent of the 
	 */
	ASYNCHRONOUS { public String toString() { return "ASYNCHRONOUS"; }},
	/**
	 * A form of synchronous update, where the Notifier is not
	 * running in the Swing event-dispatch thread and the
	 * EventReciever will be updating the Swing/AWT visual
	 * display. It ensures the receiverNotification() method is
	 * run on the Swing Event-Dispatch thread.
	 * 
	 */
	SWING_DELIVERY { public String toString() { return "SWING_DELIVERY"; }}
    }

