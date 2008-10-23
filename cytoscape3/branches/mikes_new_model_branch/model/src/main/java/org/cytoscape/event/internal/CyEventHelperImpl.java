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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Some static utility methods that help you fire events.
 */
public class CyEventHelperImpl implements CyEventHelper {
	private static final Executor EXEC = Executors.newCachedThreadPool();
	
	private BundleContext bc;

	public void setBc(BundleContext bc) {
		this.bc = bc;
	}
	
	public CyEventHelperImpl() {
	}
	
	/**
	 * Creates a new CyEventHelperImpl object.
	 *
	 * @param bc  DOCUMENT ME!
	 */
	public CyEventHelperImpl(BundleContext bc) {
		this.bc = bc;
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
	public <E extends CyEvent, L extends CyListener> void fireSynchronousEvent(final E event,
	                                                                           final Class<L> listenerClass) {
		List<L> listeners = getListeners(listenerClass);

		try {
			Method method = listenerClass.getMethod("handleEvent",
			                                        event.getClass().getInterfaces()[0]);

			for (L listener : listeners)
				method.invoke(listener, event);
		} catch (NoSuchMethodException e) {
			System.err.println("Listener doesn't implement \"handleEvent\" method: "
			                   + listenerClass.getName());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Listener can't invoke \"handleEvent\" method: "
			                   + listenerClass.getName());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Listener can't exectue \"handleEvent\" method: "
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
	public <E extends CyEvent, L extends CyListener> void fireAsynchronousEvent(final E event,
	                                                                            final Class<L> listenerClass) {
		final List<L> listeners = getListeners(listenerClass);

		try {
			final Method method = listenerClass.getMethod("handleEvent",
			                                              event.getClass().getInterfaces()[0]);

			for (final L listener : listeners)
				EXEC.execute(new Runner<E, L>(method, listener, event, listenerClass.getName()));
		} catch (NoSuchMethodException e) {
			// TODO should probably rethrow
			System.err.println("Listener doesn't implement \"handleEvent\" method: "
			                   + listenerClass.getName());
			e.printStackTrace();
		}
	}

	private static class Runner<E extends CyEvent, L extends CyListener> implements Runnable {
		private final Method method;
		private final L listener;
		private final E event;
		private final String name;

		public Runner(final Method method, final L listener, final E event, String name) {
			this.method = method;
			this.listener = listener;
			this.event = event;
			this.name = name;
		}

		public void run() {
			try {
				method.invoke(listener, event);
			} catch (IllegalAccessException e) {
				// TODO should rethrow as something
				System.err.println("Listener can't execute \"handleEvent\" method: " + name);
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO should rethrow as something
				System.err.println("Listener can't invoke \"handleEvent\" method: " + name);
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked") // needed because of ServiceReference cast.

	private <L extends CyListener> List<L> getListeners(Class<L> listenerClass) {
		List<L> ret = new LinkedList<L>();

		if (bc == null)
			return ret;

		try {
			ServiceReference[] sr = bc.getServiceReferences(listenerClass.getName(), null);

			if (sr != null)
				for (ServiceReference r : sr) {
					L listener = (L) bc.getService(r);

					if (listener != null)
						ret.add(listener);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
}
