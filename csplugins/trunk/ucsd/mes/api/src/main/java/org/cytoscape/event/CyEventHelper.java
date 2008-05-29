

package org.cytoscape.event;

import java.util.List;
import java.util.LinkedList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Some static utility methods that help you fire events. 
 */
public class CyEventHelper {

	private CyEventHelper() {};

	/**
	 * Calls each listener found in the Service Registry identified by
	 * the listenerClass and filter with the supplied event.
	 */
	public static <E extends CyEvent, L extends CyEventListener<E>> void fireSynchronousEvent( final E event, final ListenerProvider<L> provider ) {
		List<L> listeners = provider.getListeners();
		for ( L listener : listeners )
			listener.handleEvent(event);
	}

	/**
	 * Calls each listener found in the Service Registry identified by
	 * the listenerClass and filter with the supplied event in a new
	 * thread.
	 * <p>
	 * This method should <b>ONLY</b> ever be called with a thread safe event object!
	 */
	public static <E extends CyEvent, L extends CyEventListener<E>> void fireAsynchronousEvent( final E event, final ListenerProvider<L> provider ) {
		final List<L> listeners = provider.getListeners();
		for ( final L listener : listeners ) {
			Runnable task = new Runnable() {
				public void run() {
					listener.handleEvent(event);
				}
			};
			exec.execute(task);
		}
	}
	
	static final Executor exec = Executors.newCachedThreadPool();
}
