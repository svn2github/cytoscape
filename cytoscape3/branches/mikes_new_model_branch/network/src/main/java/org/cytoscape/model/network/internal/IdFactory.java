

package org.cytoscape.model.internal;


class IdFactory {

	private IdFactory() {}

	private static long count = 0;

	static synchronized long getNextSUID() {
		return count++;	
	}
}
