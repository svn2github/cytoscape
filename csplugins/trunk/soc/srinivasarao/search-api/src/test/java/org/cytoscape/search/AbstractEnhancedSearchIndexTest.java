package org.cytoscape.search;

import junit.framework.TestCase;

import org.apache.lucene.store.RAMDirectory;

public abstract class AbstractEnhancedSearchIndexTest extends TestCase{
	protected EnhancedSearchIndex esi = null;
	
	public void testgetIndex(){
		RAMDirectory rd = esi.getIndex();
		assertTrue(rd instanceof RAMDirectory);
	}
}
