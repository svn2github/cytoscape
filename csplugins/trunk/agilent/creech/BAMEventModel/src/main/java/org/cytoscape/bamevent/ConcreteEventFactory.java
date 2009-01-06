package org.cytoscape.bamevent;

import org.cytoscape.bamevent.impl.EventFactoryImpl;

/**
 * Obtain a concrete implementation of an EventFactory.
 * The sole purpose of this class is to have users avoid
 * referring to implementation classes.
 */

public class ConcreteEventFactory {
    static public EventFactory getInstance() { return EventFactoryImpl.getInstance(); }
}
