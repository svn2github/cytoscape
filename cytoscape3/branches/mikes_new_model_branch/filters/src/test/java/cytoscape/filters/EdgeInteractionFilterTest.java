package cytoscape.filters;

import csplugins.quickfind.util.QuickFind;

import java.util.BitSet;

public class EdgeInteractionFilterTest extends FilterTest {

	private StringFilter locationFilter;
	
	public void setUp() {
		// Create a sample network and add sample node/edge attributes
		initNetwork();
		
		// node filter
		locationFilter = new StringFilter();
		locationFilter.setName("locationFilter");
		locationFilter.setControllingAttribute(LOCATION);
		locationFilter.setSearchStr("cy*");
		locationFilter.setIndexType(QuickFind.INDEX_NODES);		
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */	
	public void testNodeSource() {
		// Test selection source nodes only
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(locationFilter);
		
		EdgeInteractionFilter edgeInteractionFilter = new EdgeInteractionFilter();
		edgeInteractionFilter.setPassFilter(testCmpFilter);
		edgeInteractionFilter.setName("myEdgeInteractionFilter");
		edgeInteractionFilter.setNetwork(cyNetwork);
		edgeInteractionFilter.setSourceChecked(true);
		edgeInteractionFilter.setTargetChecked(false);
		
		edgeInteractionFilter.apply();
		
		BitSet expectedEdgeBitSet = new BitSet(4);
		expectedEdgeBitSet.set(0, true);
		expectedEdgeBitSet.set(1, true);
		expectedEdgeBitSet.set(2, true);
		expectedEdgeBitSet.set(3, true);
		
		assertEquals(expectedEdgeBitSet.toString(), edgeInteractionFilter.getEdgeBits().toString());
	}
	
	public void testNodeTarget() {
		// Test selection source nodes only
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(locationFilter);
		
		EdgeInteractionFilter edgeInteractionFilter = new EdgeInteractionFilter();
		edgeInteractionFilter.setPassFilter(testCmpFilter);
		edgeInteractionFilter.setName("myEdgeInteractionFilter");
		edgeInteractionFilter.setNetwork(cyNetwork);
		edgeInteractionFilter.setSourceChecked(false);
		edgeInteractionFilter.setTargetChecked(true);
		
		edgeInteractionFilter.apply();

		BitSet expectedEdgeBitSet = new BitSet(4);
		expectedEdgeBitSet.set(2, true);
		expectedEdgeBitSet.set(3, true);
		
		assertEquals(expectedEdgeBitSet.toString(), edgeInteractionFilter.getEdgeBits().toString());
	}

	public void testNodeSourceTarget() {
		// Test selection source nodes only
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(locationFilter);
		
		EdgeInteractionFilter edgeInteractionFilter = new EdgeInteractionFilter();
		edgeInteractionFilter.setPassFilter(testCmpFilter);
		edgeInteractionFilter.setName("myEdgeInteractionFilter");
		edgeInteractionFilter.setNetwork(cyNetwork);
		edgeInteractionFilter.setSourceChecked(true);
		edgeInteractionFilter.setTargetChecked(true);
		
		edgeInteractionFilter.apply();

		BitSet expectedEdgeBitSet = new BitSet(4);
		expectedEdgeBitSet.set(0, true);
		expectedEdgeBitSet.set(1, true);
		expectedEdgeBitSet.set(2, true);
		expectedEdgeBitSet.set(3, true);
		
		assertEquals(expectedEdgeBitSet.toString(), edgeInteractionFilter.getEdgeBits().toString());
	}

	
	
	public void testNegation() {
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(locationFilter);
		
		EdgeInteractionFilter edgeInteractionFilter = new EdgeInteractionFilter();
		edgeInteractionFilter.setPassFilter(testCmpFilter);
		edgeInteractionFilter.setName("myEdgeInteractionFilter");
		edgeInteractionFilter.setNetwork(cyNetwork);
		edgeInteractionFilter.setSourceChecked(false);
		edgeInteractionFilter.setTargetChecked(true);
		edgeInteractionFilter.setNegation(true);
		
		edgeInteractionFilter.apply();

		BitSet expectedEdgeBitSet = new BitSet(4);
		expectedEdgeBitSet.set(0, true);
		expectedEdgeBitSet.set(1, true);
				
		assertEquals(expectedEdgeBitSet.toString(), edgeInteractionFilter.getEdgeBits().toString());
	}

	
	
	// String representation of a filter 
	public void test_toString() { 
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(locationFilter);
		
		EdgeInteractionFilter edgeInteractionFilter = new EdgeInteractionFilter();
		edgeInteractionFilter.setPassFilter(testCmpFilter);
		edgeInteractionFilter.setName("myEdgeInteractionFilter");
		edgeInteractionFilter.setNetwork(cyNetwork);
		edgeInteractionFilter.setSourceChecked(false);
		edgeInteractionFilter.setTargetChecked(true);
		
		edgeInteractionFilter.apply();
		
		String edgeInteractionFilter_expectStr = "<InteractionFilter>" +
				"\nname=myEdgeInteractionFilter" +
				"\n<AdvancedSetting>" +
				"\nscope.global=false" +
				"\nscope.session=true" +
				"\nselection.node=false" +
				"\nselection.edge=true" +
				"\nedge.source=true" +
				"\nedge.target=true" +
				"\nRelation=AND" +
				"\n</AdvancedSetting>" +
				"\nNegation=false" +
				"\nnodeType=1" +
				"\npassFilter=testCmpFilter" +
				"\n</InteractionFilter>";
		
		assertEquals(edgeInteractionFilter.toString(), edgeInteractionFilter_expectStr);	
	}
}
