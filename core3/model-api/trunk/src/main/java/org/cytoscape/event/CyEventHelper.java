
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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
	public <E extends CyEvent> void fireSynchronousEvent(final E event);

	/**
	 * Calls each listener found in the Service Registry identified by the listenerClass
	 * the supplied event in a new thread.
	 * <p>This method should <b>ONLY</b> ever be called with a thread safe event object!</p>
	 *
	 * @param <E> The type of event fired. 
	 * @param event The event to be fired. 
	 */
	public <E extends CyEvent> void fireAsynchronousEvent(final E event);

	/**
	 * Returns a single instance of CyMicroListener that will in turn execute any method
	 * executed on the returned object on all registered CyMicroListeners for the specified
	 * event source object. So, executing the following code:
	 * <br/>
	 * eventHelper.getMicroListener(SomeListener.class, this).someEvent(...);
	 * <br/>
	 * will execute the "someEvent(...)" method on every registered SomeListener service
	 * that is listening for events from "this" event source.
	 * <br/>
	 * In general, CyMicroListener should avoided in favor the CyEvent/CyListener combination
	 * as that code provides more flexibility for backwards compatibility.  CyMicroListener
	 * should <b>only</b> be used when high performance is absolutely necessary <b>AND</b>
	 * when CyEvent/CyListener has been demonstrated to be inadequate!
	 *
	 * @param <M> The type of micro listener requested. 
	 * @param source The source object that fires the event. 
	 * @return A single instance CyMicroListener of type M that will in turn execute any
	 * called methods on all registered CyMicroListeners.
	 */
	public <M extends CyMicroListener> M getMicroListener(Class<M> m, Object source);
}
