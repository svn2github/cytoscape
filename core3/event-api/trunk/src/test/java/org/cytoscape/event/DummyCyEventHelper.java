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


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


import java.util.ArrayList;
import java.util.List;


public class DummyCyEventHelper implements CyEventHelper {
	private Object lastSynchronousEvent;
	private Object lastAsynchronousEvent;
	private Object lastMicroListener;
	private List<String> calledMicroListenerMethods;

	public DummyCyEventHelper() {
		calledMicroListenerMethods = new ArrayList<String>();
	}
	
	public <E extends CyEvent<?>> void fireSynchronousEvent(final E event) {
		lastSynchronousEvent = event;
	}

	public <E extends CyEvent<?>> void fireAsynchronousEvent(final E event) {
		lastAsynchronousEvent = event;
	}

	public <M extends CyMicroListener> M getMicroListener(Class<M> c, Object o) {
		lastMicroListener = Proxy.newProxyInstance(this.getClass().getClassLoader(), 
		                    new Class[] { c }, new DummyListenerHandler());
		return c.cast( lastMicroListener ); 
	}

	public <M extends CyMicroListener> void addMicroListener(M m, Class<M> c, Object o) {
	}

	public <M extends CyMicroListener> void removeMicroListener(M m, Class<M> c, Object o) {
	}

	private class DummyListenerHandler implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			calledMicroListenerMethods.add(method.getName());
			return null;
		}
	}

	public List<String> getCalledMicroListeners() {
		return calledMicroListenerMethods;
	}

	public Object getLastSynchronousEvent() {
		return lastSynchronousEvent;
	}

	public Object getLastAsynchronousEvent() {
		return lastAsynchronousEvent;
	}
}
