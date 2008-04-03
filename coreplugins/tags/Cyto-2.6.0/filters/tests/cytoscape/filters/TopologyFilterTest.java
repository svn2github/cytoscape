package cytoscape.filters;

import cytoscape.filters.Relation;
import cytoscape.filters.StringFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.filters.CompositeFilter;
import cytoscape.filters.TopologyFilter;

import java.io.File;
import java.util.BitSet;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.test.quickfind.test.TaskMonitorBase;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;

public class TopologyFilterTest extends FilterTest {

	//private CompositeFilter compositeFilter1, compositeFilter2;
	public void setUp() {
		// Create a sample network and add sample node/edge attributes
		initNetwork();
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */	
	public void testPassFilterNone() {
		TopologyFilter topoFilter = new TopologyFilter("testTopoFilter1");
		topoFilter.setNetwork(cyNetwork);
		topoFilter.setMinNeighbors(1);
		topoFilter.setDistance(1);
		topoFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(0, true);
		expectedNodeBitSet.set(1, true);
		expectedNodeBitSet.set(2, true);
		expectedNodeBitSet.set(3, true);
		
		assertEquals(expectedNodeBitSet.toString(), topoFilter.getNodeBits().toString());
		topoFilter.setMinNeighbors(3);		
		topoFilter.apply();
		
		expectedNodeBitSet.set(0, false);
		expectedNodeBitSet.set(1, false);
		expectedNodeBitSet.set(2, false);
		expectedNodeBitSet.set(3, true);
		assertEquals(expectedNodeBitSet.toString(), topoFilter.getNodeBits().toString());		
	}
	
	public void testPassFilter() {
		// Define a compositeFilter
		CompositeFilter compositeFilter1 = new CompositeFilter("firstCompositeFilter");
		NumericFilter scoreFilter = new NumericFilter();
		scoreFilter.setControllingAttribute(SCORE);
		scoreFilter.setLowBound(20.0);
		scoreFilter.setHighBound(30.0);
		scoreFilter.setIndexType(QuickFind.INDEX_NODES);
		compositeFilter1.addChild(scoreFilter);
				
		TopologyFilter topoFilter = new TopologyFilter("testTopoFilter2");
		topoFilter.setNetwork(cyNetwork);
		topoFilter.setPassFilter(compositeFilter1);
		topoFilter.setMinNeighbors(1);
		topoFilter.setDistance(1);
		topoFilter.apply();

		BitSet expectedNodeBitSet = new BitSet(4);
		expectedNodeBitSet.set(3, true);

		assertEquals(expectedNodeBitSet.toString(), topoFilter.getNodeBits().toString());		
	}	
	
	// String representation of a filter 
	public void test_toString() { 

		CompositeFilter compositeFilter1 = new CompositeFilter("firstCompositeFilter");
		NumericFilter scoreFilter = new NumericFilter();
		scoreFilter.setControllingAttribute(SCORE);
		scoreFilter.setLowBound(20.0);
		scoreFilter.setHighBound(30.0);
		scoreFilter.setIndexType(QuickFind.INDEX_NODES);
		compositeFilter1.addChild(scoreFilter);
				
		TopologyFilter topoFilter = new TopologyFilter("testTopoFilter2");
		topoFilter.setNetwork(cyNetwork);
		topoFilter.setPassFilter(compositeFilter1);
		topoFilter.setMinNeighbors(1);
		topoFilter.setDistance(1);
		topoFilter.apply();

		String topoFilter_expectStr = "<TopologyFilter>" +
				"\nname=testTopoFilter2" +
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
				"\nminNeighbors=1" +
				"\nwithinDistance=1" +
				"\npassFilter=firstCompositeFilter" +
				"\n</TopologyFiler>";
		
		assertEquals(topoFilter.toString(), topoFilter_expectStr);	
	}	
}
