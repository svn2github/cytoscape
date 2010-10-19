package org.cytoscape.work.spring;


import org.cytoscape.work.AbstractTunableInterceptor;
import org.cytoscape.work.TunableHandler;
import org.cytoscape.work.TunableHandlerFactory;

import org.springframework.core.InfrastructureProxy;


/**
 * This hack exists to handle Spring's proxy framework.  Since Spring returns
 * a proxy object rather than the original object when requesting an OSGi
 * service, we need this check to get at the original object where tunables
 * are actually defined.  This code can be safely omitted if this class isn't
 * being used with Spring.
 */
public abstract class SpringTunableInterceptor<T extends TunableHandler> extends AbstractTunableInterceptor<T> {
	public SpringTunableInterceptor(TunableHandlerFactory<T> hf) {
		super(hf);
	}

	final public void loadTunables(final Object obj) {
		if (obj instanceof InfrastructureProxy)
			super.loadTunables(((InfrastructureProxy)obj).getWrappedObject());
		else
			super.loadTunables( obj );
	}

	final protected Object convertSpringProxyObj(final Object o) {
		if (o instanceof InfrastructureProxy)
			return ((InfrastructureProxy)o).getWrappedObject();
		else
			return o;
	}

	final protected Object[] convertSpringProxyObjs(final Object... proxyObjs) {
		final Object[] objs = new Object[proxyObjs.length];
		int i = 0;
		for (final Object o : proxyObjs)
			objs[i++] = convertSpringProxyObj(o);

		return objs;
	}
}
