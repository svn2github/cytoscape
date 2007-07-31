
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

package csplugins.enhanced.search;

import java.io.File;
import org.apache.lucene.store.RAMDirectory;
import csplugins.enhanced.search.EnhancedSearchQuery;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import org.apache.lucene.search.Hits;
import junit.framework.TestCase;

public class TestEnhancedSearch extends TestCase {

	RAMDirectory idx;

	EnhancedSearchQuery queryHandler;

	CyNetwork cyNetwork;

	String query = null;

	Hits hits = null;

	// Constructor
	public TestEnhancedSearch() {
		init();
	}

	// Load sample network and attributes into memory
	private void init() {

		cyNetwork = Cytoscape.createNetworkFromFile("testData/network.sif");
		final CyNetwork currNetwork = Cytoscape.getCurrentNetwork(); // for
		// debug

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

	// Index sample network
	public void testEnhancedSearchIndex() throws Exception {
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex();
		idx = indexHandler.getIndex();
	}

	// Create query handler
	public void testEnhancedSearchQuery() throws Exception {
		queryHandler = new EnhancedSearchQuery();
	}

	// Simple queries
	public void testSimpleQueries() throws Exception {

		query = "Gene_Title:putative";
		queryHandler.ExecuteQuery(idx, query); // 56
		hits = queryHandler.getHits();
		assertEquals(query, 56, hits.length());

		query = "GO_Cellular_Component:\"plasma membrane\"";
		queryHandler.ExecuteQuery(idx, query); // 7
		hits = queryHandler.getHits();
		assertEquals(query, 7, hits.length());

		query = "canonicalName:251155_at";
		queryHandler.ExecuteQuery(idx, query); // 1
		hits = queryHandler.getHits();
		assertEquals(query, 1, hits.length());

		query = "265480_at";
		queryHandler.ExecuteQuery(idx, query); // 1
		hits = queryHandler.getHits();
		assertEquals(query, 1, hits.length());

		query = "response";
		queryHandler.ExecuteQuery(idx, query); // Search in all attributes
		hits = queryHandler.getHits();
		assertEquals(query, 00, hits.length());
	}

	// Queries on multiple attribute fields
	public void testComplexQueries() throws Exception {

		query = "GO_Biological_Process:\"water deprivation\" AND Gene_Title:aquaporin";
		queryHandler.ExecuteQuery(idx, query); // 3
		hits = queryHandler.getHits();
		assertEquals(query, 3, hits.length());

		query = "Desiccation_Response:true NOT Chromosome:5";
		queryHandler.ExecuteQuery(idx, query); // 20
		hits = queryHandler.getHits();
		assertEquals(query, 20, hits.length());

		query = "GO_Biological_Process:stress AND (GO_Molecular_Function:peroxidase OR GO_Molecular_Function:catalase)";
		queryHandler.ExecuteQuery(idx, query); // 4
		hits = queryHandler.getHits();
		assertEquals(query, 4, hits.length());
	}

	// Multiple values for same attribute
	public void testFieldGrouping() throws Exception {

		query = "Gene_Title:(+60S +\"ribosomal protein\")";
		queryHandler.ExecuteQuery(idx, query); // 9
		hits = queryHandler.getHits();
		assertEquals(query, 9, hits.length());
	}

	// Wildcards queries
	public void testWildcardsQueries() throws Exception {

		query = "Gene_Title:deHYdr*n";
		queryHandler.ExecuteQuery(idx, query); // 4
		hits = queryHandler.getHits();
		assertEquals(query, 4, hits.length());
	}

	// Range queries
	public void testRangeQueries() throws Exception {

		query = "Chromosome:5";
		queryHandler.ExecuteQuery(idx, query); // 39
		hits = queryHandler.getHits();
		assertEquals(query, 39, hits.length());

		query = "Chromosome:[4 TO 5]";
		queryHandler.ExecuteQuery(idx, query); // 79
		hits = queryHandler.getHits();
		assertEquals(query, 79, hits.length());

		query = "weight:[0.95 TO 1]";
		queryHandler.ExecuteQuery(idx, query);
		hits = queryHandler.getHits();
		assertEquals(query, 00, hits.length());

		query = "weight:[-1 TO -0.95]";
		queryHandler.ExecuteQuery(idx, query);
		hits = queryHandler.getHits();
		assertEquals(query, 00, hits.length());

		query = "weight:[0.95 TO 1] OR weight:[-1 TO -0.95]";
		queryHandler.ExecuteQuery(idx, query); // 62
		hits = queryHandler.getHits();
		assertEquals(query, 62, hits.length());
	}

	// Queries with no results
	public void testNoResultsQueries() throws Exception {

		query = "interaction:neg AND weight:[0.9 TO 0.95]";
		queryHandler.ExecuteQuery(idx, query);
		hits = queryHandler.getHits();
		assertEquals(query, 0, hits.length());
	}

}
