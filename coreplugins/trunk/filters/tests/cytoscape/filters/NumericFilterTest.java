package cytoscape.filters;


import java.util.BitSet;
import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.test.quickfind.test.TaskMonitorBase;
import csplugins.widgets.autocomplete.index.NumberIndex;
import java.util.List;

public class NumericFilterTest extends FilterTest {

	private NumericFilter<Integer> theIntegerFilter;
	private NumericFilter<Double> theDoubleFilter;
	private BitSet expectedBitSet1, expectedBitSet1_not, expectedBitSet2, expectedBitSet2_not;
	
	public void setUp() {
		// Crerate a sample network and add sample node/edge attributes
		initNetwork();
		
		//1. Index network by rank (Integer type)
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		quickFind.addNetwork(cyNetwork, new TaskMonitorBase());

		//NumberIndex integerIndex = (NumberIndex) quickFind.getIndex(cyNetwork);

		quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, RANK, new TaskMonitorBase());
		NumberIndex integetIndex = (NumberIndex) quickFind.getIndex(cyNetwork);
	
		quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, SCORE, new TaskMonitorBase());
		NumberIndex doubleIndex = (NumberIndex) quickFind.getIndex(cyNetwork);

		//Create two numericFilter objects -- Integer or Double
		theIntegerFilter = new NumericFilter<Integer>("integerFilter", RANK, 2,3);
		theIntegerFilter.setNumberIndex(integetIndex);
		theIntegerFilter.setNetwork(cyNetwork);

		theDoubleFilter = new NumericFilter<Double>("doubleFilter", SCORE, 3.0,25.0);
		theDoubleFilter.setNumberIndex(doubleIndex);
		theDoubleFilter.setNetwork(cyNetwork);

		//Expected values
		expectedBitSet1 = new BitSet(4);
		expectedBitSet1.set(0, true);
		expectedBitSet1.set(2, true);

		expectedBitSet2 = new BitSet(4);
		expectedBitSet2.set(1, true);
		expectedBitSet2.set(2, true);

		expectedBitSet1_not = (BitSet) expectedBitSet1.clone();
		expectedBitSet1_not.flip(0,4);

		expectedBitSet2_not = (BitSet) expectedBitSet2.clone();
		expectedBitSet2_not.flip(0,4);
		
	}
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testIntegerFilter_getBits() {
		// test NumericFilter<Integer>
		theIntegerFilter.setNegation(false);
		theIntegerFilter.apply();

		assertEquals(expectedBitSet1.toString(), theIntegerFilter.getBits().toString());
	}
	
	public void testIntegerFilter_getBits_not() {
		theIntegerFilter.setNegation(true);
		theIntegerFilter.apply();

		assertEquals(expectedBitSet1_not.toString(), theIntegerFilter.getBits().toString());
	}	
	
	public void testDoubleFilter_getBits() {
		//test NumericFilter<Double>
		theDoubleFilter.setNegation(false);	
		theDoubleFilter.apply();

		assertEquals(expectedBitSet2.toString(), theDoubleFilter.getBits().toString());
	}

	public void testDoubleFilter_getBits_not() {
		theDoubleFilter.setNegation(true);
		theDoubleFilter.apply();

		assertEquals(expectedBitSet2_not.toString(), theDoubleFilter.getBits().toString());
	}	
	
	// String representation of a filter 
	public void test_toString() { 
		String theFirstFilter_expectStr = "NumericFilter=integerFilter:rank:false:2:3:"+ QuickFind.INDEX_NODES;
		theIntegerFilter.setNegation(false);
		theDoubleFilter.apply();

		assertEquals(theFirstFilter_expectStr, theIntegerFilter.toString());	
		
		String theSecondFilter_expectStr = "NumericFilter=doubleFilter:score:false:3.0:25.0:"+QuickFind.INDEX_NODES;
		theDoubleFilter.setNegation(false);
		theDoubleFilter.apply();

		assertEquals(theSecondFilter_expectStr, theDoubleFilter.toString());			
	}
}
