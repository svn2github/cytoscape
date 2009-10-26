// $Id: TestBioPaxToCytoscapeMapper.java,v 1.15 2006/06/20 20:31:41 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package cytoscape.coreplugins.biopax.mapper;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.style.BioPaxVisualStyleUtil;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;

import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.biopax.paxtools.model.Model;

import java.io.FileReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 * Tests the MapBioPaxToCytoscape Mapper Class.
 *
 * @author Ethan Cerami.
 */
public class TestBioPaxToCytoscapeMapper extends TestCase {
	/**
	 * Test boostrap.
	 *
	 * @return Test
	 */
	public static Test suite() {
		// Will dynamically add all methods as tests that begin with 'test'
		// and have no arguments:
		return new TestSuite(TestBioPaxToCytoscapeMapper.class);
	}

	/**
	 * Test main.
	 *
	 * @param args String[]
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	/**
	 * Tests the Mapper on a Valid BioPAX File.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testMapper1() throws Exception {
		Model model =BioPaxUtil.readFile("testData/biopax_sample1.owl");
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape();
		mapper.doMapping(model);

		CyNetwork cyNetwork = createNetwork("network1", mapper);
		verifyNodeList(cyNetwork);
		verifyControlConversion(cyNetwork);

		//verifyCoFactors(cyNetwork);
	}

	/**
	 * Tests that we can map BioPAX Complexes Correctly.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testComplexMapping() throws Exception {
		Model model =BioPaxUtil.readFile("testData/biopax_complex.owl");
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape();
		mapper.doMapping(model);

		CyNetwork cyNetwork = createNetwork("network1", mapper);
		int nodeCount = cyNetwork.getNodeCount();
		assertEquals(3, nodeCount);

		//  First, find the Target Complex:  CPATH-126.
		int targetNodeIndex = 0;
		RootGraph rootGraph = cyNetwork.getRootGraph();
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();

			if (node.getIdentifier().equals("CPATH-126")) {
				targetNodeIndex = node.getRootGraphIndex();
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should be two edges;  one for each member protein
		assertEquals(2, edgeIndices.length);

		//  Verify that edges point to member proteins
		for (int i = 0; i < edgeIndices.length; i++) {
			Edge edge = rootGraph.getEdge(edgeIndices[i]);
			Node target = edge.getTarget();

			if (i == 0) {
				assertEquals("CPATH-125(PM)-CPATH-126", target.getIdentifier());
			} else if (i == 1) {
				assertEquals("CPATH-124(PM)-CPATH-126", target.getIdentifier());
			}
		}
	}

	private CyNetwork createNetwork(String name, MapBioPaxToCytoscape mapper) {
		CyNetwork cyNetwork = Cytoscape.createNetwork(name,false);
		int[] nodeIndices = mapper.getNodeIndices();
		int[] edgeIndices = mapper.getEdgeIndices();

		for (int i = 0; i < nodeIndices.length; i++) {
			cyNetwork.addNode(nodeIndices[i]);
		}

		for (int i = 0; i < edgeIndices.length; i++) {
			cyNetwork.addEdge(edgeIndices[i]);
		}

		return cyNetwork;
	}

	/**
	 * Tests that we can map BioPAX Physical Interactions Correctly.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testPhysicalInteractions() throws Exception {
		Model model = BioPaxUtil.readFile("testData/DIP_ppi.owl");
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape();
		mapper.doMapping(model);

		CyNetwork cyNetwork = createNetwork("network1", mapper);
		int nodeCount = cyNetwork.getNodeCount();
		assertEquals(3, nodeCount);

		//  First, find the Target Interaction:  physicalInteraction1.
		int targetNodeIndex = 0;
		RootGraph rootGraph = cyNetwork.getRootGraph();
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();

			if (node.getIdentifier().equals("physicalInteraction1")) {
				targetNodeIndex = node.getRootGraphIndex();
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should be two edges;  one for each participant
		assertEquals(2, edgeIndices.length);

		//  Verify that edges point to member proteins
		for (int i = 0; i < edgeIndices.length; i++) {
			Edge edge = rootGraph.getEdge(edgeIndices[i]);
			Node target = edge.getTarget();

			if (i == 0) {
				assertEquals("protein2", target.getIdentifier());
			} else if (i == 1) {
				assertEquals("protein1", target.getIdentifier());
			}
		}
	}

	/**
	 * Verifies the List of Newly Added Nodes.
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	private void verifyNodeList(CyNetwork cyNetwork) {
		int nodeCount = cyNetwork.getNodeCount();
		assertEquals(12, nodeCount);

		//  This HashMap contains a list of expected node identifiers.
		HashMap nodeMap = new HashMap();
		nodeMap.put("protein45", new Integer(0));
		nodeMap.put("protein32", new Integer(0));
		nodeMap.put("smallMolecule10", new Integer(0));
		nodeMap.put("smallMolecule18(CY)", new Integer(0));
		nodeMap.put("smallMolecule23(CY)", new Integer(0));
		nodeMap.put("smallMolecule27", new Integer(0));
		nodeMap.put("smallMolecule39", new Integer(0));
		nodeMap.put("smallMolecule99", new Integer(0));

		//  These represent interaction nodes
		nodeMap.put("catalysis43", new Integer(0));
		nodeMap.put("biochemicalReaction6", new Integer(0));
		nodeMap.put("biochemicalReaction37", new Integer(0));
		nodeMap.put("catalysis5", new Integer(0));

		//  We don't know the order of nodes;  so use nodeMap for look up.
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();
			String id = node.getIdentifier();

			//  Test a specific node label
			if (id.equals("smallMolecule99")) {
				String label = Cytoscape.getNodeAttributes()
				                        .getStringAttribute(id,
				                                            BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
				assertEquals("Mg2+", label);
			}

			if (nodeMap.containsKey(id)) {
				nodeMap.put(id, new Integer(1));
			} else {
				fail("Network contains an Node that we were not expecting:  " + "\"" + id + "\"");
			}
		}

		//  Verify that we found all expected node identifiers.
		Set keySet = nodeMap.keySet();

		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Integer counter = (Integer) nodeMap.get(key);

			if (counter.intValue() != 1) {
				fail("Network does not contain expected node:  " + "\"" + key + "\"");
			}
		}
	}

	/**
	 * Verifies that Control Interactions and Conversion Interactions
	 * were mapped over sucessfully.
	 */
	private void verifyControlConversion(CyNetwork cyNetwork) {
		//  First, find the Target Interaction:  biochemicalReaction37.
		int targetNodeIndex = 0;
		RootGraph rootGraph = cyNetwork.getRootGraph();
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();

			if (node.getIdentifier().equals("biochemicalReaction37")) {
				targetNodeIndex = node.getRootGraphIndex();
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should only be three edges;  one for left;  one for right;
		//  and one for control
		assertEquals(3, edgeIndices.length);

		for (int i = 0; i < edgeIndices.length; i++) {
			Edge edge = rootGraph.getEdge(edgeIndices[i]);
			Node source = edge.getSource();
			Node target = edge.getTarget();

			//  System.out.println("Source:  " + source.getIdentifier());
			//  System.out.println("Target:  " + target.getIdentifier());
			if (i == 0) {
				//  Validate the Right Side of the Reaction
				//  biochemicalReaction37 --> (RIGHT) --> smallMolecule39
				assertEquals("biochemicalReaction37", source.getIdentifier());
				assertEquals("smallMolecule39", target.getIdentifier());
			} else if (i == 1) {
				//  Validate the Control the Reaction
				//  protein45 --> (CONTROLS) --> biochemicalReaction37
				assertEquals("catalysis43", source.getIdentifier());
				assertEquals("biochemicalReaction37", target.getIdentifier());

				// Validate the Edge Type
				//String edgeType = (String) Cytoscape.getEdgeAttributeValue(edge,
				//       MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE);
				String edgeType = Cytoscape.getEdgeAttributes()
				                           .getStringAttribute(edge.getIdentifier(),
				                                               MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE);
				assertEquals("ACTIVATION-NONALLOSTERIC", edgeType);
			} else if (i == 2) {
				//  Validate the Left Side of the Reaction
				//  smallMolecule27 --> (LEFT) --> biochemicalReaction37
				assertEquals("smallMolecule27", source.getIdentifier());
				assertEquals("biochemicalReaction37", target.getIdentifier());
			}
		}
	}

	/**
	 * Verifies that CoFactors were mapped over successfully
	 */
	private void verifyCoFactors(CyNetwork cyNetwork) {
		//  First, find the Target Interaction:  protein45.
		int targetNodeIndex = 0;
		RootGraph rootGraph = cyNetwork.getRootGraph();
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();

			if (node.getIdentifier().equals("smallMolecule99-Mg2+")) {
				targetNodeIndex = node.getRootGraphIndex();
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should only be two edges;  one for controller, and
		//  one for cofactor.
		assertEquals(2, edgeIndices.length);

		for (int i = 0; i < edgeIndices.length; i++) {
			Edge edge = rootGraph.getEdge(edgeIndices[i]);
			Node source = edge.getSource();
			Node target = edge.getTarget();

			// System.out.println("Source:  " + source.getIdentifier());
			// System.out.println("Target:  " + target.getIdentifier());
			if (i == 0) {
				//  Validate the Controller Edge
				assertEquals("protein45", source.getIdentifier());
				assertEquals("catalysis43", target.getIdentifier());
			} else if (i == 1) {
				//  Validate the CoFactor Edge
				assertEquals("smallMolecule99", source.getIdentifier());
				assertEquals("protein45", target.getIdentifier());

				// Validate the Edge Type
				String edgeType = Cytoscape.getEdgeAttributes()
				                           .getStringAttribute(edge.getIdentifier(),
				                                               MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE);
				assertEquals("COFACTOR", edgeType);
			}
		}
	}
}
