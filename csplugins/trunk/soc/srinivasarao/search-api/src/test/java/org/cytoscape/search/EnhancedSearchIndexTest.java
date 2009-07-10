package org.cytoscape.search;

import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.internal.ArrayGraph;

import junit.framework.TestCase;

public class EnhancedSearchIndexTest extends TestCase{
	CyNetwork net = null;
	EnhancedSearchIndex esi = null;
	private TestEnhancedSearchIndex(EnhancedSearchIndex si){
			esi = si;
			net = new ArrayGraph(new DummyCyEventHelper());
			CyNode n1 = net.addNode();
			CyNode n2 = net.addNode();
	}
	public void testgetIndex(){
		RAMDirectory rd = esi.getIndex();
		assertTrue(rd instanceof RAMDirectory);
	}
}
