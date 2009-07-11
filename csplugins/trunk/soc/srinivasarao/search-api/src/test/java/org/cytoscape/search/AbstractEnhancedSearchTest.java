package org.cytoscape.search;

import junit.framework.TestCase;

import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.internal.ArrayGraph;

public abstract class AbstractEnhancedSearchTest extends TestCase {
	protected EnhancedSearch es;
	protected CyNetwork net1 = null;
	
	protected void defaultSetup(){
		net1 = new ArrayGraph(new DummyCyEventHelper());
		CyNode n1 = net1.addNode();
		CyNode n2 = net1.addNode();
	}
	public void testaddNetwork(){
		if(net1==null)
			defaultSetup();
		es.addNetwork(net1);
		RAMDirectory rd = es.getNetworkIndex(net1);
		assertTrue(rd instanceof RAMDirectory);
	}
	public void testremoveNetwork(){
		testaddNetwork();
		es.removeNetworkIndex(net1);
		RAMDirectory rd = es.getNetworkIndex(net1);
		assertNull(rd);
	}
	public void testgetNetworkIndex(){
		if(net1==null)
			defaultSetup();
		es.addNetwork(net1);
		RAMDirectory rd = es.getNetworkIndex(net1);
		assertTrue(rd instanceof RAMDirectory);
	}
	public void testgetNetworkIndexStatus(){
		if(net1==null)
			defaultSetup();
		es.addNetwork(net1);
		String str = es.getNetworkIndexStatus(net1);
		assertEquals(str,EnhancedSearch.INDEX_SET);
	}
}
