

package org.cytoscape.event.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.util.List;
import java.util.LinkedList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventListener;

/**
 * Some static utility methods that help you fire events. 
 */
public class CyEventHelperImpl implements CyEventHelper {

	private static final Executor exec = Executors.newCachedThreadPool();
	private final BundleContext bc;

	public CyEventHelperImpl( BundleContext bc ) {
		this.bc = bc;
	}

	/**
	 * Calls each listener found in the Service Registry identified by
	 * the listenerClass and filter with the supplied event.
	 */
	public <E extends CyEvent, L extends CyEventListener<E>> void fireSynchronousEvent( final E event, final Class<L> listenerClass ) {
		List<L> listeners = getListeners(listenerClass);
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
	public <E extends CyEvent, L extends CyEventListener<E>> void fireAsynchronousEvent( final E event, final Class<L> listenerClass ) {
		final List<L> listeners = getListeners(listenerClass);
		for ( final L listener : listeners ) {
			Runnable task = new Runnable() {
				public void run() {
					listener.handleEvent(event);
				}
			};
			exec.execute(task);
		}
	}
	

	@SuppressWarnings("unchecked") // needed because of ServiceReference cast.
	private <L extends CyEventListener> List<L> getListeners(Class<L> listenerClass) {
		List<L> ret = new LinkedList<L>();

		if ( bc == null )
			return ret;

		try {
			ServiceReference[] sr = bc.getServiceReferences(listenerClass.getName(), null);
			if ( sr != null )
				for (ServiceReference r : sr ) {
					L listener = (L)bc.getService(r);
					if ( listener != null )
						ret.add(listener);
				}
		} catch (Exception e) { e.printStackTrace(); }

		return ret;
	}
}

