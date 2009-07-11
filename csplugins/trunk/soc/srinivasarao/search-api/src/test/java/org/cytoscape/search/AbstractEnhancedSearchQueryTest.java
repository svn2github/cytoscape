package org.cytoscape.search;

import junit.framework.TestCase;

public abstract class AbstractEnhancedSearchQueryTest extends TestCase{

	abstract void testgetHitCount();
	abstract void testgetHits();
	abstract void testgetNodeHits();
	abstract void testgetEdgeHits();
}
