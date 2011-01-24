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
package org.cytoscape.event.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.cytoscape.event.CyEvent;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Some static utility methods that help you fire events.
 */
public class CyListenerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(CyListenerAdapter.class);
	private static final Executor EXEC = Executors.newCachedThreadPool();
	private static final ServiceComparator serviceComparator = new ServiceComparator(); 

	private final Map<Class<?>,ServiceTracker> serviceTrackers; 
	private final BundleContext bc;

	/**
	 * Creates a new CyListenerAdapter object.
	 *
	 * @param bc  DOCUMENT ME!
	 */
	public CyListenerAdapter(BundleContext bc) {
		this.bc = bc;
		serviceTrackers = new HashMap<Class<?>,ServiceTracker>();
	}

	/**
	 * Calls each listener found in the Service Registry identified by the listenerClass
	 * and filter with the supplied event.
	 *
	 * @param <E> The type of event. 
	 * @param event  The event object. 
	 */
	public <E extends CyEvent<?>> void fireSynchronousEvent(final E event) {
		final Class<?> listenerClass = event.getListenerClass();
		
		final Object[] listeners = getListeners(listenerClass);
		if ( listeners == null ) {
			return;
		} 

		try {
			final Method method = listenerClass.getMethod("handleEvent", event.getClass());

			for (final Object listener : listeners) {
				//System.out.println("event: " + event.getClass().getName() + "  listener: " + listener.getClass().getName());
				method.invoke(listenerClass.cast(listener), event);
			}
		} catch (NoSuchMethodException e) {
			logger.error("Listener doesn't implement \"handleEvent\" method: "
			                   + listenerClass.getName(), e);
		} catch (InvocationTargetException e) {
			logger.error("Listener threw exception as part of \"handleEvent\" invocation: "
			                   + listenerClass.getName(), e);
		} catch (IllegalAccessException e) {
			logger.error("Listener can't execute \"handleEvent\" method: "
			                   + listenerClass.getName(), e);
		}
	}


	/**
	 * Calls each listener found in the Service Registry identified by the listenerClass
	 * and filter with the supplied event in a new thread.<p>This method should <b>ONLY</b>
	 * ever be called with a thread safe event object!</p>
	 *
	 * @param <E> The type of event. 
	 * @param event  The event object. 
	 */
	public <E extends CyEvent> void fireAsynchronousEvent(final E event) {
		final Class listenerClass = event.getListenerClass(); 

		final Object[] listeners = getListeners(listenerClass);
		if ( listeners == null ) {
			return;
		} 

		try {
			final Method method = listenerClass.getMethod("handleEvent", event.getClass());

			for (final Object listener : listeners) {
				EXEC.execute(new Runner(method, listener, event, listenerClass));
			}
		} catch (NoSuchMethodException e) {
			// TODO should probably rethrow
			logger.error("Listener doesn't implement \"handleEvent\" method: "
			                   + listenerClass.getName(), e);
		}
	}

	private Object[] getListeners(Class<?> listenerClass) {
		if ( !serviceTrackers.containsKey( listenerClass ) ) {
			//logger.debug("added new service tracker for " + listenerClass);
			final ServiceTracker st = new ServiceTracker(bc, listenerClass.getName(), null);
			st.open();
			serviceTrackers.put( listenerClass, st );
		}

		Object[] services = serviceTrackers.get(listenerClass).getServices();

		if ( services == null )
			return null;

		Arrays.sort(services, serviceComparator);
		return services; 
	}

	private static class Runner implements Runnable {
		private final Method method;
		private final Object listener;
		private final Object event;
		private final Class clazz;

		public Runner(final Method method, final Object listener, final Object event, Class clazz) {
			this.method = method;
			this.listener = listener;
			this.event = event;
			this.clazz = clazz;
		}

		public void run() {
			try {
				method.invoke(clazz.cast(listener), event);
			} catch (IllegalAccessException e) {
				// TODO should rethrow as something
				logger.error("Listener can't execute \"handleEvent\" method: " + clazz.getName(), e);
			} catch (InvocationTargetException e) {
				// TODO should rethrow as something
				logger.error("Listener threw exception as part of \"handleEvent\" invocation: " + listener.toString(), e);
			}
		}
	}
}
