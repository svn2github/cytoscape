
package org.cytoscape;

import org.cytoscape.impl.FRootGraph;

public class RootGraphFactory {
	public static RootGraph getRootGraph() {
		return new FRootGraph();
	}
}
			
