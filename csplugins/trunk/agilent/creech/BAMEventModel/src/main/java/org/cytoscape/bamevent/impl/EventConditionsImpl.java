package org.cytoscape.bamevent.impl;

import org.cytoscape.bamevent.ConcreteEventFactory;
import org.cytoscape.bamevent.DeliveryOptions;
import org.cytoscape.bamevent.EventAction;
import org.cytoscape.bamevent.EventConditions;
import org.cytoscape.bamevent.EventFilter;
import org.cytoscape.bamevent.DeliveryMethod;

    /**
     * Implementation of the EventConditions interface. Uses Builder
     * pattern for constructing objects. For example, if we wanted to
     * set the EventAction and classEvent state, we could perform:
     * <PRE>
     *    new EventConditionsImpl.Builder (eventAction).deliveryOptions(<options>).eventFilter(<filter>).build();
     * </PRE>
     */
    public class EventConditionsImpl implements EventConditions {
	private final EventAction action;
	private       DeliveryOptions deliveryOptions;
	private final EventFilter filter;
	public static class Builder {
	    // required params:
	    private EventAction action;
	    // optional params:
	    private DeliveryOptions deliveryOptions = null;
	    private EventFilter filter = null;
	    public Builder (EventAction action) {
		this.action = action;
	    }
	    public Builder deliveryOptions (DeliveryOptions deliveryOptions) {
		this.deliveryOptions = deliveryOptions;
		return this;
	    }
	    public Builder eventFilter (EventFilter filter) {
		this.filter = filter;
		return this;
	    }
	    public EventConditionsImpl build () {
		return new EventConditionsImpl (this);
	    }
	}
	private EventConditionsImpl (Builder builder) {
	    action = builder.action;
	    deliveryOptions = builder.deliveryOptions;
	    filter = builder.filter;
	    if (deliveryOptions == null) {
		deliveryOptions = ConcreteEventFactory.getInstance().createDeliveryOptions ();
	    }
	}

	/**
	 * {@inheritDoc}
	 */
	public EventAction getEventAction () {
	    return action;
	}
	/**
	 * {@inheritDoc}
	 */
	public DeliveryOptions getDeliveryOptions () {
	    return deliveryOptions;
	}
	/**
	 * {@inheritDoc}
	 */
	public EventFilter getEventFilter() {
	    return filter;
	}

	@Override public String toString () {
	    StringBuilder result = new StringBuilder();
	    result.append ('[' + this.getClass().getSimpleName() + '.' + hashCode());
	    result.append (" action: " + action);
	    result.append (" deliveryOptions: " + deliveryOptions);
	    result.append (" eventFilter: " + filter);	    
	    result.append (']');
	    return result.toString();
	}
    }
