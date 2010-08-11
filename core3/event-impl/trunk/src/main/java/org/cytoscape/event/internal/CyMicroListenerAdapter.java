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

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyMicroListener;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;


public class CyMicroListenerAdapter {

	private final Map<Object,Map<Class<?>,Object>> proxys;
	private final Map<Object,Map<Class<?>,Set<Object>>> listeners;
	private final Map<Class<?>,ServiceTracker> trackers;
	private final BundleContext bc; 

	public CyMicroListenerAdapter(final BundleContext bc) {
		this.bc = bc;
		proxys = new HashMap<Object,Map<Class<?>,Object>>();
		listeners = new HashMap<Object,Map<Class<?>,Set<Object>>>();
		trackers = new HashMap<Class<?>,ServiceTracker>();
	}

	public <L extends CyMicroListener> L getMicroListener(Class<L> listenerClass, Object eventSource) {
		// make sure we're tracking this listener class
		if ( !trackers.containsKey( listenerClass ) ) {
			final ServiceTracker st = new ServiceTracker(bc,listenerClass.getName(),
			                                             new Customizer(listenerClass));
			st.open();
			trackers.put(listenerClass, st);
		}

		Map<Class<?>,Object> classMap = proxys.get(eventSource);
		if ( classMap == null )
			return listenerClass.cast( noOpProxy(listenerClass) );

		Object proxy = classMap.get(listenerClass);

		if ( proxy == null )
			return listenerClass.cast( noOpProxy(listenerClass) );

		return listenerClass.cast( proxy );
	}

	private Object noOpProxy( Class c ) {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), 
		                              new Class[] { c }, new ListenerHandler());
	}

	// This customizer takes newly registered services of the specified class
	// and binds the services to the set of listeners appropriate for the event 
	// source specified by the service. 
	private class Customizer implements ServiceTrackerCustomizer {

		private final Class clazz;

		Customizer(final Class clazz) {
			this.clazz = clazz;
		}

		public Object addingService(ServiceReference reference) {
			final Object service = bc.getService(reference);
			if ( service instanceof CyMicroListener ) {
				final Object source = ((CyMicroListener)service).getEventSource();

				// First add the listener service to the set of services
				// for this object and class.
				if ( !listeners.containsKey(source) )
					listeners.put(source, new HashMap<Class<?>,Set<Object>>());

				Map<Class<?>,Set<Object>> listenerMap = listeners.get(source);
				if ( !listenerMap.containsKey(clazz) )
					listenerMap.put(clazz,new HashSet<Object>());

				Set<Object> listenerServices = listenerMap.get(clazz);
				listenerServices.add(service);
			
				// Now create a Proxy object for this object and class.
				if ( !proxys.containsKey(source) )
					proxys.put( source, new HashMap<Class<?>,Object>() );

				Map<Class<?>,Object> sourceProxys = proxys.get(source);
				if ( !sourceProxys.containsKey( clazz ) )
					sourceProxys.put( clazz,  
					                  Proxy.newProxyInstance(this.getClass().getClassLoader(), 
									                         new Class[] { clazz }, 
															 new ListenerHandler(listenerServices)));
			}
			return service;
		}

		public void modifiedService(ServiceReference reference, Object service) { }

		public void removedService(ServiceReference reference, Object service) {
			if ( service instanceof CyMicroListener ) {
				final Object source = ((CyMicroListener)service).getEventSource();

				// clean up the listeners
				Map<Class<?>,Set<Object>> sourceListeners = listeners.get(source);
				if ( sourceListeners != null ) {
					Set<Object> classListeners = sourceListeners.get(clazz);
					if ( classListeners != null ) {
						classListeners.remove(service);
						if ( classListeners.size() == 0 )
							sourceListeners.remove( clazz );
					}
					
					// this gets rid of the reference to the source object, which should
					// help with garbage collection
					if ( sourceListeners.size() == 0 )
						listeners.remove(source);
				}

				// clean up the proxys
				Map<Class<?>,Object> sourceProxys = proxys.get(source);
				if ( sourceProxys != null ) {
					sourceProxys.remove(clazz);

					// this gets rid of the reference to the source object, which should
					// help with garbage collection
					if ( sourceProxys.size() == 0 )
						proxys.remove(source);
				}
			}
		}
	}


	// Simply iterates over the provided list of Listeners and
	// executes the specified method on each Listener.
	private class ListenerHandler implements InvocationHandler {
		private	final Set<Object> ol; 

		public ListenerHandler() {
			this.ol = null;
		}

		public ListenerHandler(Set<Object> ol) {
			this.ol = ol;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ( ol == null )
				return null;

			for ( Object o : ol )
				method.invoke(o,args);
			return null; // TODO ??
		}
	}
}
