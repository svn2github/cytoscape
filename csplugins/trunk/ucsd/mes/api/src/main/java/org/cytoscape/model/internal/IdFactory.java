

package org.cytoscape.model.network.impl;


class IdFactory {

	private IdFactory() {}

	private static long count = 0;

	static synchronized long getNextSUID() {
		return count++;	
	}
}
