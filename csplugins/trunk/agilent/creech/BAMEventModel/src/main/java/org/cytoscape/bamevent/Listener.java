package org.cytoscape.bamevent;

    /**
     * An object that can receive notifications about events of interest.
     *
     * <H4>Assumptions</H4>
     * <OL>
     * <LI>A Notifier only generates events with one type of context object.
     * <P>For example, this means that all CyNetwork events would have to have
     *    the same context--a given CyNetwork. If this is not the case,
     *    we may need to change the specification for the Listener, below.
     * <P><LI>Use just one callback method versus a different
     * method for each event.
     * <P>This alleviates
     * the problem of the Notifier having to write different
     * specific methods for each type of event--a good thing. If a
     * Listener wishes to monitor many events, it should
     * define separate Listeners as nested anonymous or
     * private classes for each event type. This is cleaner
     * and doesn't require any parsing of the information in the
     * EventNotification to separate one event from another.</P></LI>
     * </OL>
     * @param <C> the type of the EventNotification context--the
     * object the event action affects. (see
     * EventNotification.getContext()).
     * @param <T> the actual type of the EventNotification. This
     * allows subclassing of the the EventNotification.
     */

    public interface Listener<C,T extends EventNotification<C>> {
	/**
	 * An event has occurred that you are interested in.
	 * @param note the EventNotification giving the details of the event.
	 */
	void receiveNotification (T note);
    }
