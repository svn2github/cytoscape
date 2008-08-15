package cytoscape.filters;

import csplugins.quickfind.util.QuickFind;

import java.util.BitSet;

public class NodeInteractionFilterTest extends FilterTest {

	private StringFilter pmidFilter;
	
	public void setUp() {
		// Create a sample network and add sample node/edge attributes
		initNetwork();
		
		// edge filter
		pmidFilter = new StringFilter();
		pmidFilter.setName("edgeFilter");
		pmidFilter.setControllingAttribute(PMID);
		pmidFilter.setSearchStr("123*");
		pmidFilter.setIndexType(QuickFind.INDEX_EDGES);
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */	
	public void testNodeSource() {
		// Test selection source nodes only
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(pmidFilter);
		
		NodeInteractionFilter nodeInteractionFilter = new NodeInteractionFilter();
		nodeInteractionFilter.setPassFilter(testCmpFilter);
		nodeInteractionFilter.setName("myNodeInteractionFilter");
		nodeInteractionFilter.setNetwork(cyNetwork);
		nodeInteractionFilter.setSourceChecked(true);
		nodeInteractionFilter.setTargetChecked(false);
		
		nodeInteractionFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(5);
		expectedNodeBitSet.set(3, true);
		expectedNodeBitSet.set(4, true);
		
		assertEquals(expectedNodeBitSet.toString(), nodeInteractionFilter.getNodeBits().toString());
	}
	
	public void testNodeTarget() {
		// Test selection Target nodes only
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(pmidFilter);
		
		NodeInteractionFilter nodeInteractionFilter = new NodeInteractionFilter();
		nodeInteractionFilter.setPassFilter(testCmpFilter);
		nodeInteractionFilter.setName("myNodeInteractionFilter");
		nodeInteractionFilter.setNetwork(cyNetwork);
		nodeInteractionFilter.setSourceChecked(false);
		nodeInteractionFilter.setTargetChecked(true);
		
		nodeInteractionFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(5);
		expectedNodeBitSet.set(1, true);
		expectedNodeBitSet.set(2, true);
		expectedNodeBitSet.set(3, true);
		expectedNodeBitSet.set(4, true);
		
		assertEquals(expectedNodeBitSet.toString(), nodeInteractionFilter.getNodeBits().toString());
	}

	
	public void testNegation() {
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(pmidFilter);
		
		NodeInteractionFilter nodeInteractionFilter = new NodeInteractionFilter();
		nodeInteractionFilter.setPassFilter(testCmpFilter);
		nodeInteractionFilter.setName("myNodeInteractionFilter");
		nodeInteractionFilter.setNetwork(cyNetwork);
		nodeInteractionFilter.setSourceChecked(false);
		nodeInteractionFilter.setTargetChecked(true);
		nodeInteractionFilter.setNegation(true);
		
		nodeInteractionFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(5);
		expectedNodeBitSet.set(0, true);
		
		assertEquals(expectedNodeBitSet.toString(), nodeInteractionFilter.getNodeBits().toString());
	}

	
	public void testNodeSourceTarget() {
		// Test selection Target nodes only
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(pmidFilter);
		
		NodeInteractionFilter nodeInteractionFilter = new NodeInteractionFilter();
		nodeInteractionFilter.setPassFilter(testCmpFilter);
		nodeInteractionFilter.setName("myNodeInteractionFilter");
		nodeInteractionFilter.setNetwork(cyNetwork);
		nodeInteractionFilter.setSourceChecked(true);
		nodeInteractionFilter.setTargetChecked(true);
		
		nodeInteractionFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(1, true);
		expectedNodeBitSet.set(2, true);
		expectedNodeBitSet.set(3, true);
		expectedNodeBitSet.set(4, true);
		
		assertEquals(expectedNodeBitSet.toString(), nodeInteractionFilter.getNodeBits().toString());
	}
	
	// String representation of a filter 
	public void test_toString() { 
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter");
		testCmpFilter.addChild(pmidFilter);
		
		NodeInteractionFilter nodeInteractionFilter = new NodeInteractionFilter();
		nodeInteractionFilter.setPassFilter(testCmpFilter);
		nodeInteractionFilter.setName("myNodeInteractionFilter");
		nodeInteractionFilter.setNetwork(cyNetwork);
		nodeInteractionFilter.setSourceChecked(false);
		nodeInteractionFilter.setTargetChecked(true);
		
		nodeInteractionFilter.apply();

		String nodeInteractionFilter_expectStr = "<InteractionFilter>" +
				"\nname=myNodeInteractionFilter" +
				"\n<AdvancedSetting>" +
				"\nscope.global=false" +
				"\nscope.session=true" +
				"\nselection.node=true" +
				"\nselection.edge=false" +
				"\nedge.source=true" +
				"\nedge.target=true" +
				"\nRelation=AND" +
				"\n</AdvancedSetting>" +
				"\nNegation=false" +
				"\nnodeType=1" +
				"\npassFilter=testCmpFilter" +
				"\n</InteractionFilter>";
		
		assertEquals(nodeInteractionFilter.toString(), nodeInteractionFilter_expectStr);	
	}
}
