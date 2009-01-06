package org.cytoscape.bamevent;

    /**
     * Used for both specifying what types of events a Listener
     * are interested in as well as specifying the actual event action
     * of an EventNotification.
     * <H4>Questions</H4>
     * <OL>
     * <LI> Do we need to support hierarchy of EventActions?
     * <P> For example, we might have several actions dealing with Nodes in a Network, such as "NODE.DELETED" and "NODE.ADDED".
     *     Do we need the ability to register interest in any events dealing with NODE (either DELETED or ADDED)?
     *     My feeling no in that we can get the same behavior using EventAction.ANY and the use of EventFilters.
     * </OL>
     */
    public interface EventAction {
	/**
	 * Used to registerInterest in any EventAction that takes place
	 * for a given Notifier.
	 * @see EventManager#registerInterest
	 */
	EventAction ANY = ConcreteEventFactory.getInstance().createEventAction ("ANY");
	/**
	 * Return the action of this EventAction.
	 *
	 * <P><B>Q</B>: Should we allow the action string to be returned?
	 <P>I am torn between having a String returned and trying
	 * to keep the type's implementation within EventAction
	 * itself. If we can keep it contained, then we can potentially
	 * subclass EventActions.
	 */
	String getEventAction();
	/**
	 * Specifies if we match another EventAction.
	 * @return true iff this EventAction matches typeToMatch.
	 */
	boolean isEventAction (EventAction typeToMatch);
	/**
	 * Specifies if our action matches a given String.
	 * @return true iff this EventAction's action matches actionName.
	 */
	boolean isEventAction (String actionName);

	/**
	 * Return a human readable String representing the contents of this
	 * object.
	 */
	String toString ();
    }
