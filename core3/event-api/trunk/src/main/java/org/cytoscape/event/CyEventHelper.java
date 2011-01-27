/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.event;


/**
 * The basic event handling interface for Cytoscape.  All Cytoscape events
 * should be fired using these methods.  All listeners should be registered
 * as CyListener or CyMicroListener services.
 */
public interface CyEventHelper {
	/**
	 * Calls each listener found in the Service Registry identified by the listenerClass
	 * interface by the supplied CyEvent.
	 *
	 * @param <E> The type of event fired. 
	 * @param event The event to be fired. 
	 */
	public <E extends CyEvent<?>> void fireSynchronousEvent(final E event);

	/**
	 * Calls each listener found in the Service Registry identified by the listenerClass
	 * the supplied event in a new thread.
	 * <p>This method should <b>ONLY</b> ever be called with a thread safe event object!</p>
	 *
	 * @param <E> The type of event fired. 
	 * @param event The event to be fired. 
	 */
	public <E extends CyEvent<?>> void fireAsynchronousEvent(final E event);

	/**
	 * Returns a single instance of CyMicroListener that will in turn execute any method
	 * executed on the returned object on all registered CyMicroListeners for the specified
	 * event source object. So, executing the following code:
	 * <code>
	 * eventHelper.getMicroListener(SomeListener.class, this).someEvent(...);
	 * </code>
	 * will execute the "someEvent(...)" method on every registered SomeListener 
	 * that is listening for events from "this" event source.
	 * <br/>
	 * In general, CyMicroListener should avoided in favor the CyEvent/CyListener combination
	 * as that code provides more flexibility for backwards compatibility.  CyMicroListener
	 * should <b>only</b> be used when high performance is absolutely necessary <b>and</b>
	 * when CyEvent/CyListener has been demonstrated to be inadequate!
	 *
	 * @param <M> the type of micro listener requested.
	 * @param m the class object for type M
	 * @param source The source object that fires the event. 
	 * @return A single instance CyMicroListener of type M that will in turn execute any
	 * called methods on all registered CyMicroListeners.
	 */
	public <M extends CyMicroListener> M getMicroListener(Class<M> m, Object source);


	/**
	 * Registers an object as a CyMicroListener to the event source object.
	 *
	 * @param <M> The type of micro listener being registered.
	 * @param listener The object implementing the specified micro listener interface. 
	 * @param clazz The specific CyMicroListener class that the listener is being registered for.
	 * This is necessary because the listener object may implement several CyMicroListener 
	 * interfaces.
	 * @param source The event source that the listener object should listen to.
	 */
	public <M extends CyMicroListener> void addMicroListener(M listener, Class<M> clazz, Object source);

	/**
	 * Unregisters an object as a CyMicroListener for all event sources. 
	 *
	 * @param <M> The type of micro listener being unregistered.
	 * @param listener The object implementing the specified micro listener interface. 
	 * @param clazz The specific CyMicroListener class that the listener is being unregistered for.
	 * @param source The event source that the listener object should be removed from. 
	 */
	public <M extends CyMicroListener> void removeMicroListener(M listener, Class<M> clazz, Object source);

	/**
	 * This method will prevent any events fired from the specified source 
	 * object from being propagated to listeners.  This applies to both
	 * normal Listeners and MicroListeners.
	 * @param eventSource The object that should have its events blocked 
	 * from being sent.
	 */
	public void silenceEventSource(Object eventSource);

	/**
	 * This method will allow events fired from the specified source 
	 * object to be propagated to listeners.  This applies to both
	 * normal Listeners and MicroListeners.  This method only needs
	 * to be called if the silenceEventSource(eventSource) method has
	 * been called.  Otherwise, all events are by default propagated
	 * normally.
	 * @param eventSource The object that should have its events sent
	 * to listeners.
	 */
	public void unsilenceEventSource(Object eventSource);

}
