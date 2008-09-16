
package org.cytoscape.event;

/**
 * The basic interface that any class interested in a particular
 * type of event should implement.
 * <p>
 * Instead of the customary strategy of registering events with 
 * the objects that fire the events, listeners should register
 * themselves as services with the OSGi ServiceRegistry.  
 * The event producers will simply query the ServiceRegistry 
 * to search for Listeners for the type of events they fire.  
 * <p>
 * It would be fantastic if we could specify one listener
 * interface that differentiated itself by a parameterized
 * type, but that doesn't appear possible with Java. That
 * means users are either must register as different listeners
 * or handle different event types in the handleEvent method. 
 */
public interface CyEventListener {

}
