
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
import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import org.apache.lucene.search.Hits;
import org.apache.lucene.store.RAMDirectory;
import csplugins.enhanced.search.EnhancedSearchQuery;

public class TestEnhancedSearch extends TestCase {

	String query = null;
	Hits hits = null;

	CyNetwork cyNetwork;

	// Load sample network and attributes into memory
	public TestEnhancedSearch() {

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
		hits = queryHandler.ExecuteQuery(query); // 56
		assertEquals(query, 56, hits.length());

		query = "GO_Cellular_Component:\"plasma membrane\"";
		hits = queryHandler.ExecuteQuery(query); // 7
		assertEquals(query, 7, hits.length());

		query = "canonicalName:251155_at";
		hits = queryHandler.ExecuteQuery(query); // 12 - notice it returnes both nodes and edges.
		assertEquals(query, 12, hits.length());

		query = "265480_at";
		hits = queryHandler.ExecuteQuery(query); // 26 - notice: search is executed on nodes and edges.
		assertEquals(query, 26, hits.length());

		query = "response";
		hits = queryHandler.ExecuteQuery(query); // Search in all attributes
		assertEquals(query, 70, hits.length());
	}

	// Queries on multiple attribute fields
	public void testComplexQueries() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "GO_Biological_Process:\"water deprivation\" AND Gene_Title:aquaporin";
		hits = queryHandler.ExecuteQuery(query); // 3
		assertEquals(query, 3, hits.length());

		query = "Desiccation_Response:true NOT Chromosome:5";
		hits = queryHandler.ExecuteQuery(query); // 20
		assertEquals(query, 20, hits.length());

		query = "GO_Biological_Process:stress AND (GO_Molecular_Function:peroxidase OR GO_Molecular_Function:catalase)";
		hits = queryHandler.ExecuteQuery(query); // 4
		assertEquals(query, 4, hits.length());
	}

	// Multiple values for same attribute
	public void testFieldGrouping() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "Gene_Title:(+60S +\"ribosomal protein\")";
		hits = queryHandler.ExecuteQuery(query); // 9
		assertEquals(query, 9, hits.length());
	}

	// Wildcards queries
	public void testWildcardsQueries() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "Gene_Title:deHYdr*n";
		hits = queryHandler.ExecuteQuery(query); // 4
		assertEquals(query, 4, hits.length());
	}

	/*
	// Range queries
	public void testRangeQueries() throws Exception {

		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "Chromosome:5";
		hits = queryHandler.ExecuteQuery(query); // 39
		assertEquals(query, 39, hits.length());

		query = "Chromosome:[4 TO 5]";
		hits = queryHandler.ExecuteQuery(query); // 79
		assertEquals(query, 79, hits.length());

		query = "weight:[0.95 TO 1]";
		hits = queryHandler.ExecuteQuery(query);
		assertEquals(query, 00, hits.length());

		query = "weight:[-1 TO -0.95]";
		hits = queryHandler.ExecuteQuery(query);
		assertEquals(query, 00, hits.length());

		query = "weight:[0.95 TO 1] OR weight:[-1 TO -0.95]";
		hits = queryHandler.ExecuteQuery(query); // 62
		assertEquals(query, 62, hits.length());
	}
	
	
	// Queries with no results
	public void testNoResultsQueries() throws Exception {
	
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "interaction:neg AND weight:[0.9 TO 0.95]";
		hits = queryHandler.ExecuteQuery(query);
		assertEquals(query, 0, hits.length());
	}

*/
	
}
