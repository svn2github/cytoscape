

package org.cytoscape.event;

import java.util.List;
import java.util.LinkedList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * An implementation of ListenerProvider that is backed by the OSGi Service Registry. 
 * This should probably be in a separate module. Alternatively, this could be an OSGi
 * service itself or could be injected by Guice or Spring.
 */
public class OSGiListenerProvider<L extends CyEventListener> implements ListenerProvider<L> {

	private final Class<L> lc;
	private final BundleContext bc;
	private final String f;
	
	public OSGiListenerProvider(Class<L> lc, BundleContext bc, String f) {
		this.lc = lc;
		this.bc = bc;
		this.f = f;
	}

	public List<L> getListeners() {
		return getListenerService(lc,bc,f);
	}
		

	@SuppressWarnings("unchecked") // needed because of ServiceReference cast.
	private static <L extends CyEventListener> List<L> getListenerService(Class<L> listenerClass, BundleContext bc, String filter) {
		List<L> ret = new LinkedList<L>();

		if ( bc == null )
			return ret;

		try {
			ServiceReference[] sr = bc.getServiceReferences(listenerClass.getName(), filter);
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
