package cytoscape.filters;

import cytoscape.filters.StringFilter;
import java.util.BitSet;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.test.quickfind.test.TaskMonitorBase;
import csplugins.widgets.autocomplete.index.TextIndex;

public class StringFilterTest extends FilterTest {

	private BitSet expectedBitSet1, expectedBitSet1_not;
	private BitSet expectedBitSet2, expectedBitSet2_not;
	private TextIndex index_by_UniqueIdentification, index_by_location;
	private StringFilter theFirstFilter, theSecondFilter;

	public void setUp() {
		// Crerate a sample network and add sample node/edge attributes
		initNetwork();
				
		// Set expected values
		expectedBitSet1 = new BitSet(4);
		expectedBitSet1.set(2, true);
		expectedBitSet1.set(3, true);

		expectedBitSet2 = new BitSet(4);
		expectedBitSet2.set(0, true);
		expectedBitSet2.set(1, true);
		
		expectedBitSet1_not = (BitSet) expectedBitSet1.clone();
		expectedBitSet1_not.flip(0,4);

		expectedBitSet2_not = (BitSet) expectedBitSet2.clone();
		expectedBitSet2_not.flip(0,4);
		
		//Create two indices (1) by default "Unique Identifier" and (2) "location"
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
		index_by_UniqueIdentification = (TextIndex) quickFind.getIndex(cyNetwork);

		quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, LOCATION, new TaskMonitorBase());
		index_by_location = (TextIndex) quickFind.getIndex(cyNetwork);

		// Created two StringFilter objects
		theFirstFilter = new StringFilter();
		theFirstFilter.setName("firstStringFilter");
		//theFirstFilter.setControllingAttribute(QuickFind.UNIQUE_IDENTIFIER);
		theFirstFilter.setControllingAttribute("canonicalName");

		theFirstFilter.setSearchStr("rai*");
		theFirstFilter.setIndex(index_by_UniqueIdentification);
		theFirstFilter.setNetwork(cyNetwork);

		theSecondFilter = new StringFilter();
		theSecondFilter.setName("secondStringFilter");
		theSecondFilter.setControllingAttribute(LOCATION);
		theSecondFilter.setSearchStr("nu*");
		theSecondFilter.setIndex(index_by_location);
		theSecondFilter.setNetwork(cyNetwork);
	}
		
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */

	public void testGetBits() { // Indexing Node
		
		// Test case 1: indexed by "Unique Identifier"
		theFirstFilter.setNegation(false);
		theFirstFilter.apply();
		assertEquals(expectedBitSet1.toString(), theFirstFilter.getNodeBits().toString());

		//Test case 2: indexed by "location"
		theSecondFilter.setNegation(false);
		theSecondFilter.apply();
		
		assertEquals(expectedBitSet2.toString(), theSecondFilter.getNodeBits().toString());
	}
	
	public void testGetBits_not() { 
		//Test case 1: indexed by "Unique Identifier"
		theFirstFilter.setNegation(true);
		theFirstFilter.apply();

		assertEquals(expectedBitSet1_not.toString(), theFirstFilter.getNodeBits().toString());

		//Test case 2: indexed by "location"
		theSecondFilter.setNegation(true);
		theSecondFilter.apply();

		assertEquals(expectedBitSet2_not.toString(), theSecondFilter.getNodeBits().toString());
	}

	// String representation of a filter 
	public void test_toString() { 
		String theFirstFilter_expectStr = "StringFilter=canonicalName:false:rai*:"+QuickFind.INDEX_NODES;
		
		assertEquals(theFirstFilter_expectStr, theFirstFilter.toString());	
		
		String theSecondFilter_expectStr = "StringFilter=location:false:nu*:"+QuickFind.INDEX_NODES;
		assertEquals(theSecondFilter_expectStr, theSecondFilter.toString());			
	}
	
	//	 May also need to test Indexing Edge?
}
