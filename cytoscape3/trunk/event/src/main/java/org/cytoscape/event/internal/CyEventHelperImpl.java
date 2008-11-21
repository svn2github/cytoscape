

package org.cytoscape.event.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.util.List;
import java.util.LinkedList;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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
	public <E extends CyEvent, L extends CyEventListener> void fireSynchronousEvent( final E event, final Class<L> listenerClass ) {
		List<L> listeners = getListeners(listenerClass);
		try {

			Method method = listenerClass.getMethod("handleEvent", event.getClass().getInterfaces()[0]);
			for ( L listener : listeners )
				method.invoke(listener,event);

		} catch (NoSuchMethodException e) {
			System.err.println("Listener doesn't implement \"handleEvent\" method: "+listenerClass.getName());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Listener can't invoke \"handleEvent\" method: "+listenerClass.getName());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Listener can't exectue \"handleEvent\" method: "+listenerClass.getName());
			e.printStackTrace();
		}
	}

	/**
	 * Calls each listener found in the Service Registry identified by
	 * the listenerClass and filter with the supplied event in a new
	 * thread.
	 * <p>
	 * This method should <b>ONLY</b> ever be called with a thread safe event object!
	 */
	public <E extends CyEvent, L extends CyEventListener> void fireAsynchronousEvent( final E event, final Class<L> listenerClass ) {
		final List<L> listeners = getListeners(listenerClass);
		try {
		final Method method = listenerClass.getMethod("handleEvent", event.getClass().getInterfaces()[0]);
		for ( final L listener : listeners ) {
			Runnable task = new Runnable() {
				public void run() {
					try {
						method.invoke(listener,event);
					} catch (IllegalAccessException e) {
						System.err.println("Listener can't exectue \"handleEvent\" method: "+listenerClass.getName());
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						System.err.println("Listener can't invoke \"handleEvent\" method: "+listenerClass.getName());
						e.printStackTrace();
					}
				}
			};
			exec.execute(task);
		}
		} catch (NoSuchMethodException e) {
			System.err.println("Listener doesn't implement \"handleEvent\" method: "+listenerClass.getName());
			e.printStackTrace();
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

