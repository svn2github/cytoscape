

package org.cytoscape.model.impl;

import org.cytoscape.model.GraphObject;

class GraphObjImpl implements GraphObject {

	private static int count = 0;

	private static synchronized int getNextSUID() {
		return count++;	
	}

	final private int suid;

	GraphObjImpl() {
		suid = getNextSUID();
	}

	public int getSUID() {
		return suid;
	}
}
