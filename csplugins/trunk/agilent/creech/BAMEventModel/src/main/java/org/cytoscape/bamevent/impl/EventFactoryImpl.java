package org.cytoscape.bamevent.impl;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.bamevent.DeliveryOptions;
import org.cytoscape.bamevent.DeliveryTiming;
import org.cytoscape.bamevent.EventAction;
import org.cytoscape.bamevent.EventConditions;
import org.cytoscape.bamevent.EventFactory;
import org.cytoscape.bamevent.EventFilter;
import org.cytoscape.bamevent.EventManager;
import org.cytoscape.bamevent.EventNotification;
import org.cytoscape.bamevent.Notifier;
import org.cytoscape.bamevent.DeliveryMethod;

/**
 * Implementation of the EventFactory interface.
 */
public class EventFactoryImpl implements EventFactory {
    private static final EventFactoryImpl singletonFactory = new EventFactoryImpl();
    private EventFactoryImpl () {
    }
    /**
     * Obtain the singleton instance of the factory.
     */
    public static EventFactoryImpl getInstance() {
	return singletonFactory;
    }

    /**
     * {@inheritDoc}
     */
    public EventManager getEventManager () {
	return EventManagerImpl.getEventManager();
    }

    /**
     * {@inheritDoc}
     */
    public EventAction createEventAction (String eventAction) {
	return new EventActionImpl (eventAction);
    }
    /**
     * {@inheritDoc}
     */
    public <T>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction) {
	return new EventNotificationImpl<T> (notifier, context, eventAction);
    }
    /**
     * {@inheritDoc}
     */
    public <T,S>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction, S supportingInfo) {
	List<S> info = new ArrayList<S>(1);
	info.add (supportingInfo);
	return new EventNotificationImpl<T> (notifier, context, eventAction, info);
    }

    /**
     * {@inheritDoc}
     */
    public <T,S1,S2>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction, S1 supportingArg1, S2 supportingArg2) {
	// generic type info is not currently used:
	List info = new ArrayList(2);
	info.add (supportingArg1);
	info.add (supportingArg2);
	return new EventNotificationImpl<T> (notifier, context, eventAction, info);
    }

    /**
     * {@inheritDoc}
     */
    public <T,S1,S2,S3>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction, S1 supportingArg1, S2 supportingArg2, S3 supportingArg3) {
	// generic type info is not currently used:
	List info = new ArrayList(3);
	info.add (supportingArg1);
	info.add (supportingArg2);
	info.add (supportingArg3);
	return new EventNotificationImpl<T> (notifier, context, eventAction, info);
    }

    /**
     * {@inheritDoc}
     */
    public <T>EventNotification<T> createEventNotification (Notifier notifier, T context, EventAction eventAction, List<?> supportingInfo) {
	return new EventNotificationImpl<T> (notifier, context, eventAction, supportingInfo);
    }

    /**
     * {@inheritDoc}
     */
    public EventConditions createEventConditions (EventAction action) {
	return new EventConditionsImpl.Builder (action).build();
    }
    /**
     * {@inheritDoc}
     */
    public EventConditions createEventConditions (EventAction action,
						  DeliveryTiming deliveryTiming,
						  boolean collapseNotifications,
						  DeliveryMethod deliveryMethod,
						  EventFilter filter) {
	return createEventConditions (action, createDeliveryOptions (deliveryTiming, collapseNotifications, deliveryMethod), filter);
    }
    /**
     * {@inheritDoc}
     */    
    public EventConditions createEventConditions (EventAction action,
						  DeliveryOptions deliveryOptions,
						  EventFilter filter) {
	return new EventConditionsImpl.Builder(action).deliveryOptions(deliveryOptions).eventFilter(filter).build();
    }
    /**
     * {@inheritDoc}
     */    
    public DeliveryOptions createDeliveryOptions () {
	return new DeliveryOptionsImpl.Builder().build();	
    }

    /**
     * {@inheritDoc}
     */    
    public DeliveryOptions createDeliveryOptions (DeliveryTiming deliveryTiming,
						  boolean collapseNotifications,
						  DeliveryMethod deliveryMethod) {
	return new DeliveryOptionsImpl.Builder().deliveryTiming(deliveryTiming).collapseNotifications (collapseNotifications).deliveryMethod (deliveryMethod).build();	
    }
}
