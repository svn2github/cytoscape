
package org.cytoscape.event;

/**
 * The basic interface that any class interested in a particular
 * type of event should implement.
 * <p> <b> Any class implementing this inteface must implement the
 * method:
 * <p> <code>public void handleEvent(ZZZ e);</code>
 * <p>where ZZZ extends CyEvent! </b> 
 * <p>
 * Unfortunately, we can't parameterize this
 * because Java doesn't reiify generic types, meaning a class 
 * could only implement ONE instance of this interface, 
 * something that doesn't work for us.  And so we leave it
 * to convention.
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
	
	// implement 
	// public void handleEvent(CyEvent e);
}
