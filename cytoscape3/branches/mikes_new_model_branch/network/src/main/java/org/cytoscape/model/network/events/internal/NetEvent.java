
package org.cytoscape.model.events.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;

/**
 * A basic network event that can extended to support nodes and edges.
 */
public class NetEvent<T> implements CyEvent<CyNetwork> {
	private final T t;
	private final CyNetwork n;

	public NetEvent(T t, CyNetwork n) {
		this.t = t;
		this.n = n;
	}

	public NetEvent(CyNetwork n) {
		this(null,n);
	}

	protected T get() {
		return t;
	}

	public CyNetwork getSource() {
		return n;
	}
}
