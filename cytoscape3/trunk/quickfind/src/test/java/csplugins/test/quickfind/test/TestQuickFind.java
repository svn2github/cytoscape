
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

package csplugins.test.quickfind.test;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.quickfind.util.TaskMonitorBase;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import junit.framework.TestCase;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

import java.util.List;


/**
 * Unit Test for Quick Find.
 *
 * @author Ethan Cerami.
 */
public class TestQuickFind extends TestCase {
	private static final String LOCATION = "location";
	private static final String NUCLEUS = "nucleus";
	private static final String CYTOPLASM = "cytoplasm";
	private static final String RANK = "rank";
	private static final String SCORE = "score";
	private static final String PMID = "pmid";

	/**
	 * Runs basic tests to verify node indexing.
	 */
	public void testNodeIndexing() {
		//  Create Sample Network
		CyNetwork cyNetwork = Cytoscape.createNetwork("network1");
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

		//  Add node/edge attributes
		addNodeAttributes(node0, node1, node2, node3);
		addEdgeAttributes(edge0, edge1, edge2);

		//  Index this network by Node:UNIQUE_IDENTIFIER
		TaskMonitorBase monitor = new TaskMonitorBase();
		QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		quickFind.addNetwork(cyNetwork, monitor);

		//  Verify default values
		TextIndex textIndex = (TextIndex) quickFind.getIndex(cyNetwork);
		String attributeKey = textIndex.getControllingAttribute();
		assertEquals(QuickFind.UNIQUE_IDENTIFIER, attributeKey);

		//  Verify that nodes have been indexed
		Hit[] hits = textIndex.getHits("ra", Integer.MAX_VALUE);
		assertEquals(3, hits.length);
		assertEquals("rabbit", hits[0].getKeyword());
		assertEquals("rain", hits[1].getKeyword());
		assertEquals("rainbow", hits[2].getKeyword());

		//  Verify Embedded Nodes
		hits = textIndex.getHits("rain", Integer.MAX_VALUE);
		assertEquals(1, hits[0].getAssociatedObjects().length);
		assertEquals(node0, hits[0].getAssociatedObjects()[0]);

		//  Verify TaskMonitor data
		assertEquals("Indexing node attributes", monitor.getStatus());
		assertEquals(100, monitor.getPercentComplete());

		//  Now, try reindexing by Node:LOCATION
		textIndex = (TextIndex) quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES,
		                                                 LOCATION, monitor);

		//  Verify Index type
		assertEquals(QuickFind.INDEX_NODES, textIndex.getIndexType());

		//  Verify that nodes have been indexed
		hits = textIndex.getHits("nu", Integer.MAX_VALUE);
		assertEquals(1, hits.length);
		assertEquals(NUCLEUS, hits[0].getKeyword());

		//  Verify Embedded Nodes
		hits = textIndex.getHits(NUCLEUS, Integer.MAX_VALUE);
		assertEquals(2, hits[0].getAssociatedObjects().length);
		assertEquals(node3, hits[0].getAssociatedObjects()[0]);
		assertEquals(node2, hits[0].getAssociatedObjects()[1]);

		validateIndexAllAttributes(quickFind, cyNetwork, monitor);
		validateIntegerIndex(quickFind, cyNetwork, monitor);
		validateDoubleIndex(quickFind, cyNetwork, monitor);

		//  Now, try indexing edge attributes
		textIndex = (TextIndex) quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_EDGES, PMID,
		                                                 monitor);

		//  Verify Index type
		assertEquals(QuickFind.INDEX_EDGES, textIndex.getIndexType());

		//  Verify that edges have been indexed
		hits = textIndex.getHits("12", Integer.MAX_VALUE);
		assertEquals(2, hits.length);
		assertEquals("12345", hits[0].getKeyword());
		assertEquals("12666", hits[1].getKeyword());

		//  Verify Embedded Edges
		assertEquals(2, hits[0].getAssociatedObjects().length);

		CyEdge edge = (CyEdge) hits[1].getAssociatedObjects()[0];
		assertEquals("rain (pp) yellow", edge.getIdentifier());
	}

	private void addNodeAttributes(CyNode node0, CyNode node1, CyNode node2, CyNode node3) {
		//  Create Sample String Attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		nodeAttributes.setAttribute(node0.getIdentifier(), LOCATION, CYTOPLASM);
		nodeAttributes.setAttribute(node1.getIdentifier(), LOCATION, CYTOPLASM);
		nodeAttributes.setAttribute(node2.getIdentifier(), LOCATION, NUCLEUS);
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
		nodeAttributes.setAttribute(node3.getIdentifier(), SCORE, 2.1);
	}

	private void addEdgeAttributes(CyEdge edge0, CyEdge edge1, CyEdge edge2) {
		//  Create Sample String Attributes
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		edgeAttributes.setAttribute(edge0.getIdentifier(), PMID, "12345");
		edgeAttributes.setAttribute(edge1.getIdentifier(), PMID, "12345");
		edgeAttributes.setAttribute(edge2.getIdentifier(), PMID, "12666");
	}

	/**
	 * Validate that we can index all attributes.
	 */
	private void validateIndexAllAttributes(QuickFind quickFind, CyNetwork cyNetwork,
	                                        TaskMonitorBase monitor) {
		TextIndex textIndex;
		Hit[] hits;
		//  Try indexing on a non-existent attribute key.  This should
		//  do nothing silently, and should not throw any exceptions.
		textIndex = (TextIndex) quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES, "TYPE",
		                                                 monitor);

		//  Try indexing all attributes
		textIndex = (TextIndex) quickFind.reindexNetwork(cyNetwork, QuickFind.INDEX_NODES,
		                                                 QuickFind.INDEX_ALL_ATTRIBUTES, monitor);

		//  First, try unique identifiers
		hits = textIndex.getHits("ra", Integer.MAX_VALUE);
		assertEquals(3, hits.length);
		assertEquals("rabbit", hits[0].getKeyword());
		assertEquals("rain", hits[1].getKeyword());
		assertEquals("rainbow", hits[2].getKeyword());

		//  Then, try cellular location.
		hits = textIndex.getHits("nu", Integer.MAX_VALUE);
		assertEquals(1, hits.length);
		assertEquals(NUCLEUS, hits[0].getKeyword());
	}

	private void validateIntegerIndex(QuickFind quickFind, CyNetwork cyNetwork,
	                                  TaskMonitorBase monitor) {
		NumberIndex numberIndex = (NumberIndex) quickFind.reindexNetwork(cyNetwork,
		                                                                 QuickFind.INDEX_NODES,
		                                                                 RANK, monitor);
		assertEquals(1, numberIndex.getMinimumValue());
		assertEquals(4, numberIndex.getMaximumValue());

		List list = numberIndex.getRange(1, 2);
		assertEquals(2, list.size());

		CyNode node0 = (CyNode) list.get(0);
		assertEquals("rabbit", node0.getIdentifier());

		CyNode node1 = (CyNode) list.get(1);
		assertEquals("yellow", node1.getIdentifier());
	}

	private void validateDoubleIndex(QuickFind quickFind, CyNetwork cyNetwork,
	                                 TaskMonitorBase monitor) {
		NumberIndex numberIndex = (NumberIndex) quickFind.reindexNetwork(cyNetwork,
		                                                                 QuickFind.INDEX_NODES,
		                                                                 SCORE, monitor);
		assertEquals(2.1, numberIndex.getMinimumValue());
		assertEquals(45.2, numberIndex.getMaximumValue());

		List list = numberIndex.getRange(0.0, 5.0);
		assertEquals(2, list.size());

		CyNode node0 = (CyNode) list.get(0);
		assertEquals("yellow", node0.getIdentifier());

		CyNode node1 = (CyNode) list.get(1);
		assertEquals("rainbow", node1.getIdentifier());

		//  Validate that upper bound is inclusive
		list = numberIndex.getRange(0.0, 45.2);
		assertEquals(4, list.size());
	}
}
