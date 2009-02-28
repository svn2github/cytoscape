package org.cytoscape.work.spring;

import org.cytoscape.work.AbstractTunableInterceptor;
import org.cytoscape.work.Handler;
import org.cytoscape.work.HandlerFactory;

import org.springframework.core.InfrastructureProxy; 

/**
 * This hack exists to handle Spring's proxy framework.  Since Spring returns
 * a proxy object rather than the original object when requesting an OSGi
 * service, we need this check to get at the original object where tunables
 * are actually defined.  This code can be safely omitted if this class isn't
 * being used with Spring.
 */
public abstract class SpringTunableInterceptor<T extends Handler> extends AbstractTunableInterceptor<T> {

	public SpringTunableInterceptor(HandlerFactory<T> hf) {
		super(hf);
	}

	public void loadTunables(Object obj) {
		if ( obj instanceof InfrastructureProxy )
			super.loadTunables( ((InfrastructureProxy)obj).getWrappedObject() );
		else
			super.loadTunables( obj );
	}

	protected Object[] convertSpringProxyObjs(Object... proxyObjs) {
		Object[] objs = new Object[proxyObjs.length];
		int i = 0;
		for ( Object o : proxyObjs )
		if ( o instanceof InfrastructureProxy )
			objs[i++] = ((InfrastructureProxy)o).getWrappedObject();
		else
			objs[i++] = o;

		return objs;
	}

	public abstract boolean createUI(Object... obj);
}
