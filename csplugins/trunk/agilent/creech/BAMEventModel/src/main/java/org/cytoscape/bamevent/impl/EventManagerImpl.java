package org.cytoscape.bamevent.impl;

import java.util.List;

import org.cytoscape.bamevent.EventConditions;
import org.cytoscape.bamevent.EventManager;
import org.cytoscape.bamevent.EventNotification;
import org.cytoscape.bamevent.Listener;
import org.cytoscape.bamevent.Notifier;

/**
 * Singleton implementation of an EventManager interface.
 * Currently just a stub implementation.
 */
public class EventManagerImpl implements EventManager {
    private static final EventManagerImpl singletonManager = new EventManagerImpl();
    static EventManager getEventManager() { return singletonManager; }
    /**
     * {@inheritDoc}
     */
    public void registerInterest (Notifier notifier, Listener notified, EventConditions conditions) { }

    /**
     * {@inheritDoc}
     */
    public void registerInterest (Class<? extends Notifier> notifierClass, Listener notified, EventConditions conditions) { }

    /**
     * {@inheritDoc}
     */
    public boolean unregisterInterest (Notifier notifier, Listener notified, EventConditions conditions) {
	return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean unregisterInterest (Class<? extends Notifier> notifierClass, Listener notified, EventConditions conditions) {
	return false;
    }

    /**
     * {@inheritDoc}
     */
    public void startAccumulating () { }

    /**
     * {@inheritDoc}
     */
    public void dispatch (Notifier notifier, EventNotification<?> notification) { }

    /**
     * {@inheritDoc}
     */
    public boolean dispatchAccumulated () { return false; }

    /**
     * {@inheritDoc}
     */
    public boolean hasListeners (Notifier notifier, Listener notified, EventConditions conditions) { return false; }
    /**
     * {@inheritDoc}
     */
    public List<Listener> getListeners (Notifier notifier, Listener notified, EventConditions conditions) { return null; }
}
