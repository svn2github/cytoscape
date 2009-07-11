package org.cytoscape.search;

import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.internal.ArrayGraph;
import org.cytoscape.search.internal.EnhancedSearchIndexImpl;

public class EnhancedSearchIndexTest extends AbstractEnhancedSearchIndexTest{
		
	private CyNetwork net;
		
	public void setUp(){
		net = new ArrayGraph(new DummyCyEventHelper());
		esi = new EnhancedSearchIndexImpl(net);
	}
	
	public void tearDown(){
		esi = null;
	}
}
