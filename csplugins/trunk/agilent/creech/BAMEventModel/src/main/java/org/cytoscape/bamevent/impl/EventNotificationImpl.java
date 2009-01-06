package org.cytoscape.bamevent.impl;

import java.util.List;

import org.cytoscape.bamevent.EventAction;
import org.cytoscape.bamevent.EventNotification;
import org.cytoscape.bamevent.Notifier;

/**
 * Implementation of the EventNotification interface.
 * @param <T> the type of the context object.
 */
public class EventNotificationImpl<T> implements EventNotification<T> {
    private Notifier notifier;
    private T context;
    private EventAction action;
    private List<?> supportingInfo;
    private EventNotification<T> nextCollapsedNotification;

    public EventNotificationImpl (Notifier notifier, T context, EventAction action) {
	    this.notifier = notifier;
	    this.context = context;
	    this.action = action;
	}

    public EventNotificationImpl (Notifier notifier,
				  T context,
				  EventAction action,
				  List<?> supportingInfo) {
	    this.notifier = notifier;
	    this.context = context;
	    this.action = action;
	    this.supportingInfo = supportingInfo;
	}

    /**
     * {@inheritDoc}
     */
    public Notifier getNotifier () { return notifier; }
    /**
     * {@inheritDoc}
     */
    public EventAction getEventAction () { return action; }
    /**
     * {@inheritDoc}
     */
    public T getContext () {
	return context;
    }
    /**
     * {@inheritDoc}
     */	
    public List<?> getSupportingInfo () {
	return supportingInfo;
    }
    /**
     * {@inheritDoc}
     */
    public EventNotification<T> getNextCollapsedNotification () {
	return nextCollapsedNotification;
    }

    /**
     * To only be set by the EventManager.
     */
    void setNextCollapsedNotification (EventNotification<T> nextCollapsedNotification) {
	this.nextCollapsedNotification = nextCollapsedNotification;
    }

    /**
     * {@inheritDoc}
     */
    @Override public String toString () {
	StringBuilder result = new StringBuilder();
	result.append ('[' + this.getClass().getSimpleName() + '.' + hashCode());
	result.append (" action: " + action);
	result.append (" notifier: " + notifier);
	result.append (" context: " + context);
	result.append (" supportingInfo: " + supportingInfo);	    
	result.append (" nextCollapsedNotification: " +
		       nextCollapsedNotification.getClass().getSimpleName() +
		       '.' + hashCode());
	result.append (']');
	return result.toString();
    }
}
