package org.cytoscape.search;

import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.internal.ArrayGraph;

import junit.framework.TestCase;

public abstract class AbstractEnhancedSearchIndexTest extends TestCase{
	EnhancedSearchIndex esi = null;
	private TestEnhancedSearchIndex(EnhancedSearchIndex si){
			esi = si;
	}
	public void testgetIndex(){
		RAMDirectory rd = esi.getIndex();
		assertTrue(rd instanceof RAMDirectory);
	}
}
