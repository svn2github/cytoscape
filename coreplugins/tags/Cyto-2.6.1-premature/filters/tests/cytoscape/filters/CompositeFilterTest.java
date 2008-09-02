package cytoscape.filters;

import cytoscape.filters.Relation;
import cytoscape.filters.StringFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.filters.CompositeFilter;

import java.io.File;
import java.util.BitSet;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.test.quickfind.test.TaskMonitorBase;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;

public class CompositeFilterTest extends FilterTest {

	private StringFilter locationFilter, pmidFilter;
	private NumericFilter rankFilter, scoreFilter;
	
	private CompositeFilter compositeFilter1, compositeFilter2;
	public void setUp() {
		// Create a sample network and add sample node/edge attributes
		initNetwork();
		
		// Index this network by all the related Node attributes 
		TaskMonitorBase monitor = new TaskMonitorBase();
		QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		quickFind.addNetwork(cyNetwork, monitor);

		//  Try indexing all attributes
		//quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, LOCATION, new TaskMonitorBase());
		//TextIndex index_by_location = (TextIndex) quickFind.getIndex(cyNetwork);
		
		//quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, RANK, new TaskMonitorBase());
		//NumberIndex integerIndex_rank = (NumberIndex) quickFind.getIndex(cyNetwork);
	
		//quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, SCORE, new TaskMonitorBase());
		//NumberIndex doubleIndex_score = (NumberIndex) quickFind.getIndex(cyNetwork);

		//quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_EDGES, PMID, new TaskMonitorBase());
		//TextIndex textIndex_pmid = (TextIndex) quickFind.getIndex(cyNetwork);

		// Create a list of atomic filters
		locationFilter = new StringFilter();
		locationFilter.setName("locationFilter");
		locationFilter.setControllingAttribute(LOCATION);
		locationFilter.setSearchStr("cy*");
		locationFilter.setIndexType(QuickFind.INDEX_NODES);
		
		locationFilter.setNetwork(cyNetwork);
		
		rankFilter = new NumericFilter<Integer>();
		rankFilter.setName("rankFilter");
		rankFilter.setControllingAttribute(RANK);
		rankFilter.setLowBound(2);
		rankFilter.setHighBound(3);
		rankFilter.setIndexType(QuickFind.INDEX_NODES);
		rankFilter.setNetwork(cyNetwork);
		
		scoreFilter = 	new NumericFilter<Double>();
		scoreFilter.setName("scoreFilter");
		scoreFilter.setControllingAttribute(SCORE);
		scoreFilter.setLowBound(1.0);
		scoreFilter.setHighBound(5.0);
		scoreFilter.setIndexType(QuickFind.INDEX_NODES);
		scoreFilter.setNetwork(cyNetwork);

		pmidFilter = new StringFilter();
		pmidFilter.setName("edgeFilter");
		pmidFilter.setControllingAttribute(PMID);
		pmidFilter.setSearchStr("123");
		pmidFilter.setIndexType(QuickFind.INDEX_EDGES);
		pmidFilter.setNetwork(cyNetwork);
		
		// Create a composite filter 1		
		compositeFilter1 = new CompositeFilter("firstCompositeFilter");
		compositeFilter1.addChild(locationFilter);
		compositeFilter1.addChild(rankFilter);
		compositeFilter1.setRelation(Relation.AND);
		
		// Create a composite filter 2		
		compositeFilter2 = new CompositeFilter("secondCompositeFilter");
		compositeFilter2.addChild(locationFilter);
		compositeFilter2.addChild(pmidFilter);
		compositeFilter2.setRelation(Relation.AND);
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */	

	public void testAND1() {
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter1");
		testCmpFilter.addChild(locationFilter);
		testCmpFilter.addChild(rankFilter);
		testCmpFilter.setRelation(Relation.AND);
		testCmpFilter.setNetwork(cyNetwork);
		testCmpFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(2, true);
		
		assertEquals(expectedNodeBitSet.toString(), testCmpFilter.getNodeBits().toString());
	}
	
	public void testAND2() {
		// (node filter) AND (edge filter) ==> return empty bitSet -- false for all bits		
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter2");
		testCmpFilter.addChild(locationFilter); // node filter
		testCmpFilter.addChild(pmidFilter);     // edge filter
		testCmpFilter.setRelation(Relation.AND);
		testCmpFilter.setNetwork(cyNetwork);
		testCmpFilter.apply();

		BitSet expectedBitSet = new BitSet(4);

		assertEquals(expectedBitSet.toString(), testCmpFilter.getNodeBits().toString());
		assertEquals(expectedBitSet.toString(), testCmpFilter.getEdgeBits().toString());
	}

	public void testOR1() {
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter3");
		testCmpFilter.addChild(locationFilter);
		testCmpFilter.addChild(rankFilter);
		testCmpFilter.setRelation(Relation.OR);
		testCmpFilter.setNetwork(cyNetwork);

		testCmpFilter.apply();
		//System.out.println("testCmpFilter.toString() ="+ testCmpFilter.toString());

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(0, true);
		expectedNodeBitSet.set(2, true);
		expectedNodeBitSet.set(3, true);
		
		assertEquals(expectedNodeBitSet.toString(), testCmpFilter.getNodeBits().toString());
	}
	
	public void testChildDeletion() {
		// Test child deletion
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter4");
		testCmpFilter.addChild(locationFilter);
		testCmpFilter.addChild(rankFilter);
		testCmpFilter.setRelation(Relation.AND);
		testCmpFilter.setNetwork(cyNetwork);

		testCmpFilter.apply();

		// Before deletion
		BitSet expectedBitSet4 = new BitSet();
		expectedBitSet4.set(2, true);

		assertEquals(expectedBitSet4.toString(), testCmpFilter.getNodeBits().toString());
		
		testCmpFilter.removeChild(locationFilter);
		testCmpFilter.apply();
		
		// after deletion
		expectedBitSet4.set(0, true);
		expectedBitSet4.set(1, false);
		expectedBitSet4.set(2, true);
		expectedBitSet4.set(3, false);
		
		assertEquals(expectedBitSet4.toString(), testCmpFilter.getNodeBits().toString());
	}

	public void testChildAddition() {
		// Test child addition
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter5");
		testCmpFilter.addChild(locationFilter);
		testCmpFilter.setRelation(Relation.AND);
		testCmpFilter.setNetwork(cyNetwork);
		
		testCmpFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(0, false);
		expectedNodeBitSet.set(1, false);
		expectedNodeBitSet.set(2, true);
		expectedNodeBitSet.set(3, true);
		
		assertEquals(expectedNodeBitSet.toString(), testCmpFilter.getNodeBits().toString());

		// Add new child
		testCmpFilter.addChild(rankFilter);

		testCmpFilter.apply();
		
		expectedNodeBitSet.set(0, false);
		expectedNodeBitSet.set(1, false);
		expectedNodeBitSet.set(2, true);
		expectedNodeBitSet.set(3, false);

		assertEquals(expectedNodeBitSet.toString(), testCmpFilter.getNodeBits().toString());
	}

	// String representation of a filter 
	public void test_toString() { 
		String compositeFilter1_expectStr = "<Composite>" +
				"\nname=firstCompositeFilter" +
				"\n<AdvancedSetting>" +
				"\nscope.global=false" +
				"\nscope.session=true" +
				"\nselection.node=false" +
				"\nselection.edge=false" +
				"\nedge.source=true" +
				"\nedge.target=true" +
				"\nRelation=AND" +
				"\n</AdvancedSetting>" +
				"\nNegation=false" +
				"\nStringFilter=location:false:cy*:0" +
				"\nNumericFilter=rank:false:2:3:0" +
				"\n</Composite>";
		
		assertEquals(compositeFilter1.toString(), compositeFilter1_expectStr);	

		String compositeFilter2_expectStr = "<Composite>" +
				"\nname=secondCompositeFilter" +
				"\n<AdvancedSetting>" +
				"\nscope.global=false" +
				"\nscope.session=true" +
				"\nselection.node=false" +
				"\nselection.edge=false" +
				"\nedge.source=true" +
				"\nedge.target=true" +
				"\nRelation=AND" +
				"\n</AdvancedSetting>" +
				"\nNegation=false" +
				"\nStringFilter=location:false:cy*:0" +
				"\nStringFilter=pmid:false:123:1" +
				"\n</Composite>";
		assertEquals(compositeFilter2.toString(), compositeFilter2_expectStr);	
	}
}
