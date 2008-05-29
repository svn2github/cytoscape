
package org.cytoscape.network.events.impl;

import org.cytoscape.network.CyNetwork;

/**
 * A basic network event that can extended to support nodes and edges.
 */
public class NetEvent<T> {
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
