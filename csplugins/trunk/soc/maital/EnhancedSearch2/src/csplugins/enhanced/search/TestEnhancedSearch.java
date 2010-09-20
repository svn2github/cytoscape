
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

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import junit.framework.TestCase;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import org.apache.lucene.store.RAMDirectory;
import csplugins.enhanced.search.EnhancedSearchQuery;

public class TestEnhancedSearch extends TestCase {

	protected static final String LOCATION = "location";
	protected static final String NUCLEUS = "nucleus";
	protected static final String CYTOPLASM = "cytoplasm";
	protected static final String CELL_MEMBRANE = "cell membrane";
	protected static final String RANK = "rank";
	protected static final String SCORE = "score";
	protected static final String PMID = "pmid";

	protected CyNetwork cyNetwork;
	String query = null;
	int hitCount;

	protected void initNetwork() {
		cyNetwork = Cytoscape.createNetwork("network1");
		CyNode node0 = Cytoscape.getCyNode("rain", true);
		CyNode node1 = Cytoscape.getCyNode("rainbow", true);
		CyNode node2 = Cytoscape.getCyNode("rabbit", true);
		CyNode node3 = Cytoscape.getCyNode("yellow", true);

		cyNetwork.addNode(node0);
		cyNetwork.addNode(node1);
		cyNetwork.addNode(node2);
		cyNetwork.addNode(node3);
		
		CyEdge edge0 = Cytoscape.getCyEdge(node0, node1, Semantics.INTERACTION, "pp", true);
		CyEdge edge1 = Cytoscape.getCyEdge(node0, node2, Semantics.INTERACTION, "pp", true);
		CyEdge edge2 = Cytoscape.getCyEdge(node0, node3, Semantics.INTERACTION, "pp", true);
		cyNetwork.addEdge(edge0);
		cyNetwork.addEdge(edge1);
		cyNetwork.addEdge(edge2);

		//  Create Node String Attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		nodeAttributes.setAttribute(node0.getIdentifier(), LOCATION, CYTOPLASM);
		nodeAttributes.setAttribute(node1.getIdentifier(), LOCATION, CYTOPLASM);
		nodeAttributes.setAttribute(node2.getIdentifier(), LOCATION, CELL_MEMBRANE);
		nodeAttributes.setAttribute(node3.getIdentifier(), LOCATION, NUCLEUS);

		//  Create Sample Integer Attributes
		nodeAttributes.setAttribute(node0.getIdentifier(), RANK, 4);
		nodeAttributes.setAttribute(node1.getIdentifier(), RANK, 3);
		nodeAttributes.setAttribute(node2.getIdentifier(), RANK, 1);
		nodeAttributes.setAttribute(node3.getIdentifier(), RANK, 2);

		//  Create Sample Double Attributes
		nodeAttributes.setAttribute(node0.getIdentifier(), SCORE, 45.2);
		nodeAttributes.setAttribute(node1.getIdentifier(), SCORE, 3.211);
		nodeAttributes.setAttribute(node2.getIdentifier(), SCORE, 22.2);
		nodeAttributes.setAttribute(node3.getIdentifier(), SCORE, -2.1);

		//  Create Edge String Attributes
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		edgeAttributes.setAttribute(edge0.getIdentifier(), PMID, "12345");
		edgeAttributes.setAttribute(edge1.getIdentifier(), PMID, "12345");
		edgeAttributes.setAttribute(edge2.getIdentifier(), PMID, "12355");

		Cytoscape.setCurrentNetwork(cyNetwork.getIdentifier());
	}
	
	// Simple queries
	public void testSimpleQueries() throws Exception {

		initNetwork();
		
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		// Limit to a particular attribute
		query = "location:cytoplasm";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);

		// Case insensitivity
		query = "LOCAtion:CYTOplasm";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);

		// Phrase search
		query = "location:\"cell membrane\"";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);

		// This query returns both nodes and edges (cannonicalName is a mutual attribute)
		query = "canonicalName:rain";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 4, hitCount);

		 // Search is executed on nodes and edges
		query = "rain";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 4, hitCount);

		// Search in all attributes
		query = "cytoplasm";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);
	}

	// Queries on multiple attribute fields
	public void testComplexQueries() throws Exception {

		initNetwork();
		
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		// Default Boolean operator is OR
		query = "location:cytoplasm rank:2";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 3, hitCount);

		// Boolean logic: AND
		query = "location:cytoplasm AND rank:4";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);

		// Boolean logic: NOT
		query = "location:cytoplasm NOT rank:4";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);

		// Boolean logic: AND / OR
		query = "location:cytoplasm AND rank:4 OR rank:2";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);

		// Use parenthesis to control Boolean logic
		query = "location:cytoplasm AND (rank:4 OR rank:2)";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);
	}

	// Multiple values for same attribute
	public void testFieldGrouping() throws Exception {

		initNetwork();
		
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		// Both terms must appear in location attribute
		query = "location:(+cell +membrane)";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);
	}

	// Wildcards queries
	public void testWildcardsQueries() throws Exception {

		initNetwork();
		
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		query = "PMID:12?45";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);

		query = "PMID:123*";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 3, hitCount);

		query = "PMID:1*5";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 3, hitCount);
	}

	// Range queries
	public void testRangeQueries() throws Exception {

		initNetwork();
		
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		// Limit search to a Numeric attribute
		query = "rank:4";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);

		// numeric range
		query = "rank:[1 TO 4]";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 4, hitCount);

		// Double range
		query = "score:[1 TO 100]";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 3, hitCount);

		// Negative double range
		query = "score:[-100 TO -1]";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);

		// Complex query including double range query and Boolean logic
		query = "score:[10 TO 50] OR weight:[-100 TO -1]";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);
	}
	
	
	// Queries with no results
	public void testNoResultsQueries() throws Exception {
	
		initNetwork();
		
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);

		// No results
		query = "location:cytoplasm AND rank:2";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 0, hitCount);
	}
	
}
