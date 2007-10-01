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
		// Crerate a sample network and add sample node/edge attributes
		initNetwork();
		
		// Index this network by all the related Node attributes 
		TaskMonitorBase monitor = new TaskMonitorBase();
		QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		quickFind.addNetwork(cyNetwork, monitor);

		//  Try indexing all attributes
		quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, LOCATION, new TaskMonitorBase());
		TextIndex index_by_location = (TextIndex) quickFind.getIndex(cyNetwork);
		
		quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, RANK, new TaskMonitorBase());
		NumberIndex integerIndex_rank = (NumberIndex) quickFind.getIndex(cyNetwork);
	
		quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, SCORE, new TaskMonitorBase());
		NumberIndex doubleIndex_score = (NumberIndex) quickFind.getIndex(cyNetwork);

		quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_EDGES, PMID, new TaskMonitorBase());
		TextIndex textIndex_pmid = (TextIndex) quickFind.getIndex(cyNetwork);

		// Create a list of atomic filters
		locationFilter = new StringFilter("stringFilter",LOCATION,"cy*");
		locationFilter.setTextIndex(index_by_location);
		locationFilter.setNetwork(cyNetwork);
		
		rankFilter = new NumericFilter<Integer>("rankFilter",RANK, 2, 3);
		rankFilter.setNumberIndex(integerIndex_rank);
		rankFilter.setNetwork(cyNetwork);
		
		scoreFilter = 	new NumericFilter<Double>("scoreFilter",SCORE, 1.0, 5.0);
		scoreFilter.setNumberIndex(doubleIndex_score);
		scoreFilter.setNetwork(cyNetwork);

		pmidFilter = new StringFilter("edgeFilter",PMID,"123");
		pmidFilter.setTextIndex(textIndex_pmid);
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
		
		// Created expected BitSet values 

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

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(2, true);
		
		//System.out.println("testAND1:testCmpFilter.getNodeBits().toString() ="+testCmpFilter.getNodeBits().toString());
		
		assertEquals(expectedNodeBitSet.toString(), testCmpFilter.getNodeBits().toString());
	}
	
	// (node filter) AND (edge filter) ==> return empty bitSet -- false for all bits
	public void testAND2() {
		
		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter2");
		testCmpFilter.addChild(locationFilter);
		testCmpFilter.addChild(pmidFilter);
		testCmpFilter.setRelation(Relation.AND);

		BitSet expectedBitSet = new BitSet(4);
				
		assertEquals(expectedBitSet.toString(), testCmpFilter.getNodeBits().toString());
	}
	
	
	public void testOR1() {

		CompositeFilter testCmpFilter = new CompositeFilter("testCmpFilter3");
		testCmpFilter.addChild(locationFilter);
		testCmpFilter.addChild(rankFilter);
		testCmpFilter.setRelation(Relation.OR);

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(0, true);
		expectedNodeBitSet.set(2, true);
		expectedNodeBitSet.set(3, true);

		BitSet expectedEdgeBitSet = new BitSet(4);
		expectedEdgeBitSet.set(2, true);
		
		//System.out.println("CompositeFilterTest: node_bitSet1 = " + testCmpFilter.getNodeBits().toString());
		//System.out.println("CompositeFilterTest: expectedEdgeBitSet = " + expectedEdgeBitSet.toString());

		
		assertEquals(expectedNodeBitSet.toString(), testCmpFilter.getNodeBits().toString());
		//assertEquals(expectedEdgeBitSet.toString(), testCmpFilter.getEdgeBits().toString());

	}
	
	
	public void testChildDeletion() {

		// Test child deletion
		//compositeFilte13.removeChild(stringFilter1);
		//compositeFilte13.apply();
		//BitSet expectedBitSet4 = new BitSet();
		
		//assertEquals(expectedBitSet4, compositeFilte13.getBits());

	}

	public void testChildAddition() {
		// Test child addition
		//compositeFilte13.addChild(stringFilter1);
		//System.out.println("theDoubleFilter.toString() ="+theDoubleFilter.toString());		


	}
	// String representation of a filter 
	public void test_toString() { 
		//String theFirstFilter_expectStr = "StringFilter:firstStringFilter:Unique Identifier:false:rai*";
		//assertEquals(theFirstFilter_expectStr, theFirstFilter.toString());	
		
		System.out.println("CompositeFilter1.toString() ="+compositeFilter1.toString());		

		//String theSecondFilter_expectStr = "StringFilter:secondStringFilter:location:false:nu*";
		//assertEquals(theSecondFilter_expectStr, theSecondFilter.toString());			
	}

}
