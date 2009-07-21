
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

package org.cytoscape.search;

import junit.framework.TestCase;

import org.apache.lucene.store.RAMDirectory;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.internal.ArrayGraph;
import org.cytoscape.search.internal.EnhancedSearchIndexImpl;
import org.cytoscape.search.internal.EnhancedSearchQueryImpl;

public class TestEnhancedSearch extends TestCase {

	String query = null;
	int hitCount;
	private CyNetwork net;
	private EnhancedSearchQuery queryHandler;
	private RAMDirectory rd;
	private EnhancedSearchIndex esi;
	protected CyEventHelper helper;
	// Load sample network and attributes into memory
	public void setUp(){
		
		net = new ArrayGraph(new DummyCyEventHelper());
		CyNode n1 = net.addNode();
		CyNode n2 = net.addNode();
		CyNode n3 = net.addNode();
		CyNode n4 = net.addNode();
		CyNode n5 = net.addNode();
		CyNode n6 = net.addNode();
		
		CyDataTable nodetable = (CyDataTable)net.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		nodetable.createColumn("Official HUGO Symbol", String.class, true);
		nodetable.createColumn("canonicalName", Integer.class, true);
		
		CyRow r1 = nodetable.getRow(n1.getSUID());
		r1.set("Official HUGO Symbol","ING5");
		r1.set("canonicalName",84289);
		
		CyRow r2 = nodetable.getRow(n2.getSUID());
		r2.set("Official HUGO Symbol","CCNG1");
		r2.set("canonicalName",900);
		
		CyRow r3 = nodetable.getRow(n3.getSUID());
		r3.set("Official HUGO Symbol","SCOTIN");
		r3.set("canonicalName", 51246);
		
		CyRow r4 = nodetable.getRow(n4.getSUID());
		r4.set("Official HUGO Symbol","KLF4");
		r4.set("canonicalName", 9314);
		
		CyRow r5 = nodetable.getRow(n5.getSUID());
		r5.set("Official HUGO Symbol","TP53");
		r5.set("canonicalName", 7157);
		
		CyRow r6 = nodetable.getRow(n6.getSUID());
		r6.set("Official HUGO Symbol","TPGB1");
		r6.set("canonicalName", 3146);
		
		CyEdge e1 = net.addEdge(n5, n3, true);
		CyEdge e2 = net.addEdge(n5, n4, true);
		CyEdge e3 = net.addEdge(n2, n5, true);
		CyEdge e4 = net.addEdge(n6, n5, true);
		
		CyDataTable edgetable = (CyDataTable)net.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		edgetable.createColumn("EdgeName", String.class, true);
		edgetable.createColumn("interaction", String.class, true);
		
		CyRow re1 = edgetable.getRow(e1.getSUID());
		re1.set("EdgeName", "7157 (non_core) 51246");
		re1.set("interaction","non_core");
		
		CyRow re2 = edgetable.getRow(e2.getSUID());
		re2.set("EdgeName", "7157 (non_core) 9314");
		re2.set("interaction","non_core");
		
		CyRow re3 = edgetable.getRow(e3.getSUID());
		re3.set("EdgeName", "900 (non_core) 7157");
		re3.set("interaction","non_core");

		CyRow re4 = edgetable.getRow(e4.getSUID());
		re4.set("EdgeName", "3146 (non_core) 7157");
		re4.set("interaction","non_core");
		
		esi = new EnhancedSearchIndexImpl(net);
		rd = esi.getIndex();
		queryHandler = new EnhancedSearchQueryImpl(rd, net);
		
		
	}
	public static void main(String[] args) throws Exception{
		TestEnhancedSearch te = new TestEnhancedSearch();
		te.testSimpleQuery();
	}
	
	public void testSimpleQuery() throws Exception{
		query="900";
		//query="node.canonicalname:900 OR edge.interaction:non_core";
		queryHandler.executeQuery(query); // 1
		hitCount = queryHandler.getHitCount();
		System.out.println(hitCount);
		//System.out.println(queryHandler.getNodeHits().size());
		//System.out.println(NumberTools.longToString(51246));
		assertEquals(query, 2, hitCount);
		
	}
	/*
	// Simple queries
	public void testSimpleQueries() throws Exception {

		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

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
	*/
	// Queries on multiple attribute fields
	public void testComplexQueries() throws Exception {

		//query = "GO_Biological_Process:\"water deprivation\" AND Gene_Title:aquaporin";
		query = "Official_HUGO_Symbol:ING5 OR canonicalName:51246";
		queryHandler.executeQuery(query); // 2
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);

		//query = "Desiccation_Response:true NOT Chromosome:5";
		//query = "NOT node.Official_HUGO_Symbol:KLF4";
		query = "canonicalName:9314 NOT Official_HUGO_Symbol:SCOTIN";
		queryHandler.executeQuery(query); // 1
		hitCount = queryHandler.getHitCount();
		System.out.println(hitCount);
		assertEquals(query, 1, hitCount);

		//query = "GO_Biological_Process:stress AND (GO_Molecular_Function:peroxidase OR GO_Molecular_Function:catalase)";
		query = "interaction:non_core AND (EdgeName:3146 OR EdgeName:900)";
		queryHandler.executeQuery(query); // 2
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);
	}
	/*
	// Multiple values for same attribute
	public void testFieldGrouping() throws Exception {

		EnhancedSearchIndexImpl indexHandler = new EnhancedSearchIndexImpl(cyNetwork);
		RAMDirectory idx = indexHandler.getIndex();
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,cyNetwork);

		query = "Gene_Title:(+60S +\"ribosomal protein\")";
		queryHandler.executeQuery(query); // 9
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 9, hitCount);
	}
*/
	// Wildcards queries
	public void testWildcardsQueries() throws Exception {

		//query = "Gene_Title:deHYdr*n";
		query = "TP*";
		queryHandler.executeQuery(query); // 2
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 2, hitCount);
	}

	// Range queries
	public void testRangeQueries() throws Exception {

		query = "canonicalName:51246";
		queryHandler.executeQuery(query); // 1
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 1, hitCount);

		query = "canonicalName:[900 TO 52000]";
		queryHandler.executeQuery(query); //5
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 5, hitCount);

	}
	
	
	// Queries with no results
	public void testNoResultsQueries() throws Exception {
	
		query = "canonicalName:9314 AND interaction:non_core";
		queryHandler.executeQuery(query);
		hitCount = queryHandler.getHitCount();
		assertEquals(query, 0, hitCount);
	}
	
}
