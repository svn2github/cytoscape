
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package org.cytoscape.enhanced_search.internal;

import java.io.File;
import junit.framework.TestCase;
import org.cytoscape.model.CyNetwork;
import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.enhanced_search.internal.EnhancedSearchQuery;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.test.support.NetworkTestSupport;

public class TestEnhancedSearch extends TestCase {

	String query = null;
	int hitCount;

	CyNetwork cyNetwork;

	
	IndexAndSearchTask indexAndSearchTask;
	TaskMonitor taskMonitor;
	CyNetwork network;
	NetworkTestSupport support;
	EnhancedSearch enhancedSearch;

	public TestEnhancedSearch() {
		support = new NetworkTestSupport();
	}
		
	@Before
	public void setUp() {
		network = support.getNetwork(); 

		// normal setup
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();
//		indexAndSearchTask = new IndexAndSearchTask(network, enhancedSearch);
//		sampleTask.sleepTime = 10; // to speed things along!
//		taskMonitor = new DummyTaskMonitor();
	}

	@Test
    public void testLowerBound() {
//		checkSelectedNodes(0); 
	}
	
/*	
	// Load sample network and attributes into memory
	public TestEnhancedSearchOld() {

		cyNetwork = Cytoscape.createNetworkFromFile("testData/network.sif");

		String[] noa = new String[7];
		noa[0] = new File("testData/GOMolecularFunction.NA").getAbsolutePath();
		noa[1] = new File("testData/GOCellularComponent.NA").getAbsolutePath();
		noa[2] = new File("testData/GOBiologicalProcess.NA").getAbsolutePath();
		noa[3] = new File("testData/GeneTitle.NA").getAbsolutePath();
		noa[4] = new File("testData/DesiccationResponse.NA").getAbsolutePath();
		noa[5] = new File("testData/Chromosome.NA").getAbsolutePath();
		noa[6] = new File("testData/AGI.NA").getAbsolutePath();

		String[] eda = new String[2];
		eda[0] = new File("testData/weight.EA").getAbsolutePath();
		eda[1] = new File("testData/interaction.EA").getAbsolutePath();

		Cytoscape.loadAttributes(noa, eda);
	}

	// Simple queries
	public void testSimpleQueries() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "Gene_Title:putative";
		queryHandler.executeQuery(query); // 56
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 56, hitCount);

		query = "gENE_TitlE:putATIVE";
		queryHandler.executeQuery(query); // 56 - tests case insensitivity
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 56, hitCount);

		query = "GO_Cellular_Component:\"plasma membrane\"";
		queryHandler.executeQuery(query); // 7
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 7, hitCount);

		query = "canonicalName:251155_at";
		queryHandler.executeQuery(query); // 12 - notice it returnes both nodes and edges.
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 12, hitCount);

		query = "265480_at";
		queryHandler.executeQuery(query); // 26 - notice: search is executed on nodes and edges.
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 26, hitCount);

		query = "response";
		queryHandler.executeQuery(query); // Search in all attributes
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 70, hitCount);
	}

	// Queries on multiple attribute fields
	public void testComplexQueries() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "GO_Biological_Process:\"water deprivation\" AND Gene_Title:aquaporin";
		queryHandler.executeQuery(query); // 3
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 3, hitCount);

		query = "Desiccation_Response:true NOT Chromosome:5";
		queryHandler.executeQuery(query); // 20
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 20, hitCount);

		query = "GO_Biological_Process:stress AND (GO_Molecular_Function:peroxidase OR GO_Molecular_Function:catalase)";
		queryHandler.executeQuery(query); // 4
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 4, hitCount);
	}

	// Multiple values for same attribute
	public void testFieldGrouping() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "Gene_Title:(+60S +\"ribosomal protein\")";
		queryHandler.executeQuery(query); // 9
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 9, hitCount);
	}

	// Wildcards queries
	public void testWildcardsQueries() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "Gene_Title:deHYdr*n";
		queryHandler.executeQuery(query); // 4
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 4, hitCount);
	}

	// Range queries
	public void testRangeQueries() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "Chromosome:5";
		queryHandler.executeQuery(query); // 39
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 39, hitCount);

		query = "Chromosome:[4 TO 5]";
		queryHandler.executeQuery(query); // 79
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 79, hitCount);

		query = "weight:[0.95 TO 1]";
		queryHandler.executeQuery(query); // 369
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 369, hitCount);

		query = "weight:[-1 TO -0.95]";
		queryHandler.executeQuery(query); // 30
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 30, hitCount);

		query = "weight:[0.95 TO 1] OR weight:[-1 TO -0.95]";
		queryHandler.executeQuery(query); // 399
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 399, hitCount);
	}
	
	
	// Queries with no results
	public void testNoResultsQueries() throws Exception {
	
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "interaction:neg AND weight:[0.9 TO 0.95]";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 0, hitCount);
	}
*/	
}
