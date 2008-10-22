/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.coreplugin.psi_mi.test.cyto_mapper;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import junit.framework.TestCase;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.coreplugin.psi_mi.cyto_mapper.MapToCytoscape;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiOneToInteractions;
import org.cytoscape.coreplugin.psi_mi.data_mapper.MapPsiTwoFiveToInteractions;
import org.cytoscape.coreplugin.psi_mi.model.vocab.CommonVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.util.ContentReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Tests the MapToCytoscape Class.
 *
 * @author Ethan Cerami.
 */
public class TestMapToCytoscape extends TestCase {
	/**
	 * Tests the MapPsiInteractionsTo Graph mapper.
	 * This test assumes a new empty GraphPerspective.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testMapper1() throws Exception {
		//  First, get some interactions from sample data file.
		ArrayList interactions = new ArrayList();
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent("src/test/resources/testData/psi_sample1.xml");

		//  Map from PSI One to DataService Interaction Objects.
		MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions(xml, interactions);
		mapper1.doMapping();

		//  Now Map to Cytocape Network Objects.
		CyNetwork network = Cytoscape.createNetwork("network1");
		MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.MATRIX_VIEW);
		mapper2.doMapping();
		addToCyNetwork(mapper2, network);

		//  Verify Number of Nodes and Number of Edges
		int nodeCount = network.getNodeCount();
		int edgeCount = network.getEdgeCount();
		assertEquals(7, nodeCount);
		assertEquals(6, edgeCount);

		Iterator nodeIterator = network.nodesIterator();
		Iterator edgeIterator = network.edgesIterator();

		//  Verify one of the nodes in the graph
		//  First find correct index value
		CyNode node1 = null;

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();

			if (node.getIdentifier().equals("YDL065C")) {
				node1 = node;
			}
		}

		String nodeId1 = node1.getIdentifier();
		assertEquals("YDL065C", nodeId1);

		//  Verify edge in the graph
		//  First find correct index value
		CyEdge edge1 = null;

		while (edgeIterator.hasNext()) {
			CyEdge edge = (CyEdge) edgeIterator.next();

			if (edge.getIdentifier().equals("YCR038C (classical two hybrid:11283351) YDR532C")) {
				edge1 = edge;
			}
		}

		String edgeId1 = edge1.getIdentifier();
		assertEquals("YCR038C (classical two hybrid:11283351) YDR532C", edgeId1);

		//  Verify source / target nodes of edge
		CyNode sourceNode = (CyNode) edge1.getSource();
		CyNode targetNode = (CyNode) edge1.getTarget();
		assertEquals("YCR038C", sourceNode.getIdentifier());
		assertEquals("YDR532C", targetNode.getIdentifier());

		//  Verify that Attributes were mapped over too...
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String taxonomyId = nodeAttributes.getStringAttribute(sourceNode.getIdentifier(),
		                                                      InteractorVocab.ORGANISM_NCBI_TAXONOMY_ID);
		assertEquals("4932", taxonomyId);

		String fullName = (String) nodeAttributes.getStringAttribute(sourceNode.getIdentifier(),
		                                                             InteractorVocab.FULL_NAME);
		assertTrue(fullName.indexOf("GTP/GDP exchange factor") > -1);

		//  Verify that DB Names were mapped over correctly.
		//  There are multiple DB Names in an array of Strings.
		List dbNameList = nodeAttributes.getListAttribute(sourceNode.getIdentifier(),
		                                                  CommonVocab.XREF_DB_NAME);
		assertEquals(15, dbNameList.size());
		assertEquals("RefSeq GI", dbNameList.get(0));

		//  Verify that Interaction Xrefs were mapped over correctly.
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		dbNameList = edgeAttributes.getListAttribute(edge1.getIdentifier(), CommonVocab.XREF_DB_NAME);

		List dbIdList = edgeAttributes.getListAttribute(edge1.getIdentifier(),
		                                                CommonVocab.XREF_DB_ID);
		assertEquals(2, dbNameList.size());
		assertEquals(2, dbIdList.size());
		assertEquals("DIP", dbNameList.get(0));
		assertEquals("CPATH", dbNameList.get(1));
		assertEquals("61E", dbIdList.get(0));
		assertEquals("12345", dbIdList.get(1));
	}

	/**
	 * Tests the MapPsiInteractionsTo Graph mapper.
	 * This test assumes a pre-existing GraphPerspective with existing nodes/edges.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testMapper2() throws Exception {
		//  First, get some interactions from sample data file.
		ArrayList interactions = new ArrayList();
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent("src/test/resources/testData/psi_sample1.xml");

		//  Map from PSI to DataService Interaction Objects.
		MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions(xml, interactions);
		mapper1.doMapping();

		//  Create CyNetwork, and pre-populate it with some existing data.
		CyNetwork network = Cytoscape.createNetwork("network2");
		CyNode node1 = Cytoscape.getCyNode("YDL065C", true);
		CyNode node2 = Cytoscape.getCyNode("YCR038C", true);
		network.addNode(node1);
		network.addNode(node2);

		//  Create Edge between node1 and node2.
		CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "pp", true);
		edge.setIdentifier("YDL065C (classical two hybrid, pmid:  11283351) " + "YCR038C");

		//  Now map interactions to cyNetwork.
		MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.MATRIX_VIEW);
		mapper2.doMapping();
		addToCyNetwork(mapper2, network);

		//  Verify Number of Nodes;  it should still be 7.
		//  If the mapper failed to check for pre-existing nodes, it would be 9.
		int nodeCount = network.getNodeCount();
		assertEquals(7, nodeCount);

		//  Verify Number of Edges;  it should still be 6.
		//  If the mapper failed to check for pre-existing edges, it would be 7.
		int edgeCount = network.getEdgeCount();
		assertEquals(6, edgeCount);
	}

	/**
	 * Tests the MapPsiInteractionsTo Graph mapper.
	 * This time, we test that the MATRIX_VIEW works with # interactors > 2.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testMapper3() throws Exception {
		//  First, get some interactions from sample data file.
		ArrayList interactions = new ArrayList();
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent("src/test/resources/testData/psi_sample2.xml");

		//  Map from PSI to DataService Interaction Objects.
		MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions(xml, interactions);
		mapper1.doMapping();

		//  Create CyNetwork
		CyNetwork network = Cytoscape.createNetwork("network3");

		//  Now map interactions to cyNetwork.
		MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.MATRIX_VIEW);
		mapper2.doMapping();
		addToCyNetwork(mapper2, network);

		//  Verify Number of Nodes;  there should be 4.
		int nodeCount = network.getNodeCount();
		assertEquals(4, nodeCount);

		//  Verify Number of Edges; there should be 6
		int edgeCount = network.getEdgeCount();
		assertEquals(6, edgeCount);

		Iterator edgeIterator = network.edgesIterator();
		int counter = 0;

		while (edgeIterator.hasNext()) {
			CyEdge edge = (CyEdge) edgeIterator.next();
			String id = edge.getIdentifier();

			if (id.equals("A (classical two hybrid:11283351) C")) {
				counter++;
			} else if (id.equals("A (classical two hybrid:11283351) D")) {
				counter++;
			} else if (id.equals("B (classical two hybrid:11283351) C")) {
				counter++;
			} else if (id.equals("B (classical two hybrid:11283351) D")) {
				counter++;
			} else if (id.equals("C (classical two hybrid:11283351) D")) {
				counter++;
			} else if (id.equals("A (classical two hybrid:11283351) B")) {
				counter++;
			}
		}

		assertEquals(6, counter);
	}

	/**
	 * Tests the MapPsiInteractionsTo Graph mapper.
	 * This time, we test that the SPOKE_VIEW works with # interactors > 2.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testMapper4() throws Exception {
		//  First, get some interactions from sample data file.
		ArrayList interactions = new ArrayList();
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent("src/test/resources/testData/psi_sample2.xml");

		//  Map from PSI to DataService Interaction Objects.
		MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions(xml, interactions);
		mapper1.doMapping();

		//  Create CyNetwork
		CyNetwork network = Cytoscape.createNetwork("network3");

		//  Now map interactions to cyNetwork.
		MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.SPOKE_VIEW);
		mapper2.doMapping();
		addToCyNetwork(mapper2, network);

		//  Verify Number of Nodes;  there should be 4.
		int nodeCount = network.getNodeCount();
		assertEquals(4, nodeCount);

		//  Verify Number of Edges; there should be 3
		int edgeCount = network.getEdgeCount();
		assertEquals(3, edgeCount);

		Iterator edgeIterator = network.edgesIterator();
		int counter = 0;

		while (edgeIterator.hasNext()) {
			CyEdge edge = (CyEdge) edgeIterator.next();
			String id = edge.getIdentifier();

			if (id.equals("A (classical two hybrid:11283351) B")) {
				counter++;
			} else if (id.equals("A (classical two hybrid:11283351) C")) {
				counter++;
			} else if (id.equals("A (classical two hybrid:11283351) D")) {
				counter++;
			}
		}

		assertEquals(3, counter);
	}

	/**
	 * Test PSI-MI Level 2.5.
	 * @throws Exception All Errors.
	 */
	public void testMapper5() throws Exception {
		//  First, get some interactions from sample data file.
		ArrayList interactions = new ArrayList();
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent("src/test/resources/testData/psi_sample_2_5_2.xml");

		//  Map from PSI to DataService Interaction Objects.
		MapPsiTwoFiveToInteractions mapper1 = new MapPsiTwoFiveToInteractions(xml, interactions);
		mapper1.doMapping();

		//  Create CyNetwork
		CyNetwork network = Cytoscape.createNetwork("network");

		//  Now map interactions to cyNetwork.
		MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.SPOKE_VIEW);
		mapper2.doMapping();
		addToCyNetwork(mapper2, network);

		//  Verify Number of Nodes;  there should be 3.
		int nodeCount = network.getNodeCount();
		assertEquals(3, nodeCount);

		//  Verify Number of Edges; there should be 35
		int edgeCount = network.getEdgeCount();
		assertEquals(35, edgeCount);

		Iterator edgeIterator = network.edgesIterator();
		int counter = 0;

		while (edgeIterator.hasNext()) {
			CyEdge edge = (CyEdge) edgeIterator.next();
			String id = edge.getIdentifier().trim();

			if (id.equals("kaib_synp7 (pull down:kaib-kaia-2:10064581) kaia_synp7")) {
				counter++;
			} else if (id.equals("kaib_synp7 (pull down:kaib-kaic-5:10064581) kaic_synp7")) {
				counter++;
			} else if (id.equals("kaic_synp7 (two hybrid:kaic-kaia-1:10064581) kaia_synp7")) {
				counter++;
			}
		}

		assertEquals(3, counter);
	}

	/**
	 * Tests BioGrid Data defined in Bug:  0001126
	 *
	 * @throws Exception All Errors
	 */
	public void testBioGridData() throws Exception {
		//  First, get some interactions from sample data file.
		ArrayList interactions = new ArrayList();
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent("src/test/resources/testData/bio_grid.xml");

		//  Map from PSI to DataService Interaction Objects.
		MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions(xml, interactions);
		mapper1.doMapping();

		//  Create CyNetwork
		CyNetwork network = Cytoscape.createNetwork("network");

		//  Now map interactions to cyNetwork.
		MapToCytoscape mapper2 = new MapToCytoscape(interactions, MapToCytoscape.SPOKE_VIEW);
		mapper2.doMapping();
		addToCyNetwork(mapper2, network);

		CyNode node = Cytoscape.getCyNode("HGNC:7733", false);
		assertEquals("HGNC:7733", node.getIdentifier());

		int[] edges = network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true);

		//  validate that we have 9 edges.
		assertEquals(9, edges.length);
	}

	/**
	 * Profile Loading of HPRD Data.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void profileHprd() throws Exception {
		ArrayList allInteractions = new ArrayList();

		//  First, get some interactions from sample data file.
		ContentReader reader = new ContentReader();
		String xml = reader.retrieveContent("src/test/resources/testData/hprd.xml");

		//  Map from PSI to DataService Interaction Objects.
		for (int i = 0; i < 25; i++) {
			ArrayList interactions = new ArrayList();
			MapPsiOneToInteractions mapper1 = new MapPsiOneToInteractions(xml, interactions);
			mapper1.doMapping();
			allInteractions.addAll(interactions);
		}

		//  Now Map to Cytocape Network Objects.
		System.out.println("Mapping to Cytoscape Network");
		System.out.println("Number of Interactions:  " + allInteractions.size());

		CyNetwork network = Cytoscape.createNetwork("network1");
		MapToCytoscape mapper2 = new MapToCytoscape(allInteractions, MapToCytoscape.MATRIX_VIEW);
		addToCyNetwork(mapper2, network);
		mapper2.doMapping();
		System.out.println("DONE");
	}

	private void addToCyNetwork(MapToCytoscape mapper, CyNetwork cyNetwork) {
		//  Add new nodes/edges to network
		int[] nodeIndices = mapper.getNodeIndices();
		int[] edgeIndices = mapper.getEdgeIndices();

		for (int i = 0; i < nodeIndices.length; i++) {
			cyNetwork.addNode(nodeIndices[i]);
		}

		for (int i = 0; i < edgeIndices.length; i++) {
			cyNetwork.addEdge(edgeIndices[i]);
		}
	}

	/**
	 * Main Method.  Used for JProfiler.
	 *
	 * @param args Command Line Arguments.
	 * @throws Exception All Exceptions.
	 */
	public static void main(String[] args) throws Exception {
		Date start = new Date();
		TestMapToCytoscape test = new TestMapToCytoscape();
		test.profileHprd();

		Date stop = new Date();
		long time = stop.getTime() - start.getTime();
		System.out.println("Time:  " + time);
	}
}
