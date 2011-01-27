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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyMicroListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CyMicroListenerAdapter {

	private final Map<Object,Map<Class<?>,Object>> proxys;
	private final Map<Object,Map<Class<?>,SortedSet<Object>>> listeners;
	private final Map<Class<?>,Object> noOpProxies;
	private final Set<Object> silencedSources;

	private final static ServiceComparator serviceComparator = new ServiceComparator();
	private final static Logger logger = LoggerFactory.getLogger(CyMicroListenerAdapter.class);

	public CyMicroListenerAdapter() {
		proxys = new HashMap<Object,Map<Class<?>,Object>>();
		listeners = new HashMap<Object,Map<Class<?>,SortedSet<Object>>>();
		noOpProxies = new HashMap<Class<?>,Object>();
		silencedSources = new HashSet<Object>();
	}

	public <L extends CyMicroListener> L getMicroListener(Class<L> listenerClass, Object eventSource) {

		Map<Class<?>,Object> classMap = proxys.get(eventSource);
		if ( classMap == null || silencedSources.contains(eventSource) )
			return listenerClass.cast( noOpProxy(listenerClass) );

		Object proxy = classMap.get(listenerClass);

		if ( proxy == null )
			return listenerClass.cast( noOpProxy(listenerClass) );

		return listenerClass.cast( proxy );
	}

	private Object noOpProxy(final Class<?> c ) {
		Object noOp = noOpProxies.get( c );
		if ( noOp == null ) {
			noOp = Proxy.newProxyInstance(c.getClassLoader(), new Class[] { c }, 
			                              new ListenerHandler());
			noOpProxies.put(c,noOp);
		}
		return noOp;
	}

	public <L extends CyMicroListener> void addMicroListener(L service, Class<L> clazz, Object source) {
		if ( service == null ) {
			logger.warn("attempting to add null listener for microlistener class: " + clazz);
			return;
		}

		if ( source == null ) {
			logger.warn("attempting to add microlistener of type: " + clazz + " to null source");
			return;
		}

		// First add the listener service to the set of services
		// for this object and class.
		if ( !listeners.containsKey(source) )
			listeners.put(source, new HashMap<Class<?>,SortedSet<Object>>());

		Map<Class<?>,SortedSet<Object>> listenerMap = listeners.get(source);
		if ( !listenerMap.containsKey(clazz) )
			listenerMap.put(clazz,new TreeSet<Object>(serviceComparator));

		SortedSet<Object> listenerServices = listenerMap.get(clazz);
		listenerServices.add(service);
			
		// Now create a Proxy object for this object and class.
		if ( !proxys.containsKey(source) )
			proxys.put( source, new HashMap<Class<?>,Object>() );

		Map<Class<?>,Object> sourceProxys = proxys.get(source);
		if ( !sourceProxys.containsKey( clazz ) )
			sourceProxys.put( clazz,  
			                  Proxy.newProxyInstance(clazz.getClassLoader(), 
			                                         new Class[] { clazz }, 
			                                         new ListenerHandler(listenerServices)));
	}

	public <L extends CyMicroListener> void removeMicroListener(L service, Class<L> clazz, Object source) {
		// clean up the listeners
		Map<Class<?>,SortedSet<Object>> sourceListeners = listeners.get(source);
		if ( sourceListeners != null ) {
			SortedSet<Object> classListeners = sourceListeners.get(clazz);
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

	void silenceEventSource(Object eventSource) {
		silencedSources.add(eventSource);
	}
                                
	void unsilenceEventSource(Object eventSource) {
		silencedSources.remove(eventSource);
	}

	// Simply iterates over the provided list of Listeners and
	// executes the specified method on each Listener.
	private static class ListenerHandler implements InvocationHandler {
		private	final SortedSet<Object> ol; 

		public ListenerHandler() {
			this.ol = null;
		}

		public ListenerHandler(SortedSet<Object> ol) {
			this.ol = ol;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ( ol == null )
				return null;

			for (final Object o : ol )
				method.invoke(o,args);
			
			return null;
		}
	}
}
