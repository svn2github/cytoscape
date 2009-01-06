package org.cytoscape.bamevent;

    /**
     * Used to tailor when a Listener will receive events.

     * <P>For example, we
     * could have a Listener that is only invoked when a specific Network is destroyed.
     * Filters also allow you to build more abstract event
     * receivers. For example, we could build a ContainerFilter, that
     * might return true for any ADD, REMOVE, and CHANGE events.
     * <PRE>
     * class ContainerFilter implements EventFilter {
     *    public boolean includeEvent (EventNotification note) {
     *       EventAction action = note.getEventAction ();
     *       return action.isEventAction ("ADDED") ||
     *              action.isEventAction ("REMOVED") ||
     *              action.isEventAction ("CHANGED");
     *       }
     * }
     * </PRE>
     * 
     */
    public interface EventFilter {
	/**
	 * Return true iff the given EventNotification passes this filter.
	 * @param note The EventNotification in question.
	 */
	boolean includeEvent (EventNotification<?> note);
    }
