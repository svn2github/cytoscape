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

import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyListener;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Some static utility methods that help you fire events.
 */
public class CyListenerAdapter {
	private static final Executor EXEC = Executors.newCachedThreadPool();
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
	 * @param <E>  DOCUMENT ME!
	 * @param <L>  DOCUMENT ME!
	 * @param event  DOCUMENT ME!
	 * @param listenerClass  DOCUMENT ME!
	 */
	public <E extends CyEvent> void fireSynchronousEvent(final E event) {
		final Class listenerClass = event.getListenerClass(); 
		System.out.println("ATTEMPTING TO FIRE SYNC: " + event.toString() + " for " + listenerClass.getName());
		final Object[] listeners = getListeners(listenerClass);
		if ( listeners == null ) {
			System.out.println("Sync listeners is null");
			return;
		} 

		try {
			final Method method = listenerClass.getMethod("handleEvent", event.getClass());

			for (final Object listener : listeners) {
				System.out.println("SYNC firing event: " + event.getClass().toString() + "  for listener: " + listener.toString());
				method.invoke(listenerClass.cast(listener), event);
			}
		} catch (NoSuchMethodException e) {
			System.err.println("Listener doesn't implement \"handleEvent\" method: "
			                   + listenerClass.getName());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Listener threw exception as part of \"handleEvent\" invocation: "
			                   + listenerClass.getName());
			e.printStackTrace();
			System.err.println("caused by:");
			e.getCause().printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Listener can't execute \"handleEvent\" method: "
			                   + listenerClass.getName());
			e.printStackTrace();
		}
	}


	/**
	 * Calls each listener found in the Service Registry identified by the listenerClass
	 * and filter with the supplied event in a new thread.<p>This method should <b>ONLY</b>
	 * ever be called with a thread safe event object!</p>
	 *
	 * @param <E>  DOCUMENT ME!
	 * @param <L>  DOCUMENT ME!
	 * @param event  DOCUMENT ME!
	 * @param listenerClass  DOCUMENT ME!
	 */
	public <E extends CyEvent> void fireAsynchronousEvent(final E event) {
		final Class listenerClass = event.getListenerClass(); 
		System.out.println("ATTEMPTING TO FIRE async: " + event.toString() + " for " + listenerClass.getName());
		final Object[] listeners = getListeners(listenerClass);
		if ( listeners == null ) {
			System.out.println("async listeners is null");
			return;
		} 

		try {
			final Method method = listenerClass.getMethod("handleEvent", event.getClass());

			for (final Object listener : listeners) {
				System.out.println("async firing event: " + event.getClass().toString() + "  for listener: " + listener.getClass().getName());
				EXEC.execute(new Runner(method, listener, event, listenerClass));
			}
		} catch (NoSuchMethodException e) {
			// TODO should probably rethrow
			System.err.println("Listener doesn't implement \"handleEvent\" method: "
			                   + listenerClass.getName());
			e.printStackTrace();
		}
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
				System.err.println("Listener can't execute \"handleEvent\" method: " + clazz.getName());
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO should rethrow as something
				System.err.println("Listener threw exception as part of \"handleEvent\" invocation: " + listener.toString());
				e.printStackTrace();
				System.err.println("caused by:");
				e.getCause().printStackTrace();
			}
		}
	}


	private Object[] getListeners(Class listenerClass) {
		if ( !serviceTrackers.containsKey( listenerClass ) ) {
			System.out.println("added new service tracker for " + listenerClass);
			final ServiceTracker st = new ServiceTracker(bc, listenerClass.getName(), null);
			st.open();
			serviceTrackers.put( listenerClass, st );
		}

		return serviceTrackers.get(listenerClass).getServices();
	}
}
