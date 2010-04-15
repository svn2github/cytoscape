
package org.cytoscape.test.support;

import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.CyMicroListener;

import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;


public class StubEventHelper implements CyEventHelper {
	public <E extends CyEvent> void fireSynchronousEvent(final E event) {}
	public <E extends CyEvent> void fireAsynchronousEvent(final E event) {}
	public <M extends CyMicroListener> M getMicroListener(Class<M> c, Object o) {
		return c.cast( Proxy.newProxyInstance(this.getClass().getClassLoader(),
		               new Class[] { c }, new DummyListenerHandler()));
	}
	private class DummyListenerHandler implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}
	}
}


