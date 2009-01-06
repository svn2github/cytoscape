package org.cytoscape.bamevent.impl;

import org.cytoscape.bamevent.DeliveryMethod;
import org.cytoscape.bamevent.DeliveryOptions;
import org.cytoscape.bamevent.DeliveryTiming;

/**
 * Implementation of the DeliveryOptions interface. Uses Builder
 * pattern for constructing objects. For example, if we wanted to
 * set the EventAction and classEvent state, we could perform:
 * <PRE>
 *    new DeliveryOptionsImpl.Builder ().deliveryTiming(DeliveryTiming.DELIVER_IMMEDIATELY).deliveryMethod(DeliveryMethod.ASYNCHRONOUS).build();
 * </PRE>
 */
public class DeliveryOptionsImpl implements DeliveryOptions {
    private final DeliveryTiming deliveryTiming;
    private final boolean isCollapsingNotifications;
    private final DeliveryMethod deliveryMethod;

    public static class Builder {
	// optional params:
	private DeliveryTiming deliveryTiming = DeliveryTiming.CAN_BE_DELAYED;
	private boolean isCollapsingNotifications = false;
	private DeliveryMethod deliveryMethod = DeliveryMethod.SYNCHRONOUS;

	public Builder deliveryTiming (DeliveryTiming deliveryTiming) {
	    this.deliveryTiming = deliveryTiming;
	    return this;
	}

	public Builder collapseNotifications (boolean collapseNotifications) {
	    this.isCollapsingNotifications = collapseNotifications;
	    return this;
	}
	public Builder deliveryMethod (DeliveryMethod deliveryMethod) {
	    this.deliveryMethod = deliveryMethod;
	    return this;
	}

	public DeliveryOptionsImpl build () {
	    return new DeliveryOptionsImpl (this);
	}
    }
    private DeliveryOptionsImpl (Builder builder) {
	deliveryTiming = builder.deliveryTiming;
	isCollapsingNotifications = builder.isCollapsingNotifications;
	deliveryMethod = builder.deliveryMethod;
    }
    /**
     * {@inheritDoc}
     */
    public DeliveryTiming getDeliveryTiming() {
	return deliveryTiming;
    }
    /**
     * {@inheritDoc}
     */
    public boolean isCollapsingNotifications () {
	return isCollapsingNotifications;
    }
    /**
     * {@inheritDoc}
     */
    public DeliveryMethod getDeliveryMethod () {
	return deliveryMethod;
    }

    @Override public String toString () {
	StringBuilder result = new StringBuilder();
	result.append ('[' + this.getClass().getSimpleName() + '.' + hashCode());
	result.append (" deliveryTiming: " + deliveryTiming);
	result.append (" isCollapsingNotifications: " + isCollapsingNotifications);
	result.append (" deliveryMethod: " + deliveryMethod);
	result.append (']');
	return result.toString();
    }
}
