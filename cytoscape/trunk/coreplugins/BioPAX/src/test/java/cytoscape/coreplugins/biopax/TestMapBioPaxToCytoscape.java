// $Id: TestMapBioPaxToCytoscape.java,v 1.15 2006/06/20 20:31:41 grossb Exp $
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
package cytoscape.coreplugins.biopax;

import java.io.FileInputStream;
import java.util.*;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.BioPaxVisualStyleUtil;

import giny.model.RootGraph;

import junit.framework.TestCase;

import org.biopax.paxtools.model.Model;

public class TestMapBioPaxToCytoscape extends TestCase {

	/**
	 * Tests the Mapper on a Valid BioPAX File.
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testMapper1() throws Exception {
		Model model = BioPaxUtil.read(new FileInputStream(getClass().getResource("/biopax_sample1.owl").getFile()));
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape();
		mapper.doMapping(model);
		
		CyNetwork cyNetwork = createNetwork("network1", mapper);
		
		verifyNodeList(cyNetwork);
		verifyControlConversion(cyNetwork);
		//verifyCoFactors(cyNetwork);
		
		Cytoscape.destroyNetwork(cyNetwork);
	}

	/**
	 * Tests that we can map BioPAX Complexes Correctly.
	 *
	 * TODO re-factor this test as IT DEPENDS ON ORDER OF THE ELEMENTS, which is different when run from Ant or Eclipse...
	 *
	 * @throws Exception All Exceptions.
	 */
	public void testComplexMapping() throws Exception {
		Model model = BioPaxUtil.read(new FileInputStream(getClass().getResource("/biopax_complex.owl").getFile()));
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
			String uri = Cytoscape.getNodeAttributes()
	                  .getStringAttribute(node.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_RDF_ID);
			if (uri.contains("CPATH-126")) {
				targetNodeIndex = node.getRootGraphIndex();
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should be two edges;  one for each member protein
		assertEquals(2, edgeIndices.length);
		
		Cytoscape.destroyNetwork(cyNetwork);
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
	 * 
	 * TODO re-factor this test as IT DEPENDS ON ORDER OF THE ELEMENTS, which is different when run from Ant or Eclipse...
	 */
	public void testPhysicalInteractions() throws Exception {
		Model model = BioPaxUtil.read(new FileInputStream(getClass().getResource("/DIP_ppi.owl").getFile()));
		MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape();
		mapper.doMapping(model);

		CyNetwork cyNetwork = createNetwork("network2", mapper);
		int nodeCount = cyNetwork.getNodeCount();
		assertEquals(3, nodeCount);

		//  First, find the Target Interaction:  physicalInteraction1.
		int targetNodeIndex = 0;
		RootGraph rootGraph = cyNetwork.getRootGraph();
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();
			String uri = Cytoscape.getNodeAttributes()
	                  .getStringAttribute(node.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_RDF_ID);
			if (uri.endsWith("physicalInteraction1")) {
				targetNodeIndex = node.getRootGraphIndex();
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should be two edges;  one for each participant
		assertEquals(2, edgeIndices.length);
	}

	/**
	 * Verifies the List of Newly Added Nodes.
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	private void verifyNodeList(CyNetwork cyNetwork) {
		int nodeCount = cyNetwork.getNodeCount();
		assertEquals(12, nodeCount);

		//  This HashMap contains all expected nodes.
		//  But node identifier is now a auto-generated md5hex digest; 
		//  so it's easier to test here by using biopax.rdfid values instead)
		Map<String, String> nodeMap = new HashMap<String, String>();
		nodeMap.put("physicalEntityParticipant44", "");
		nodeMap.put("physicalEntityParticipant31", "");
		nodeMap.put("physicalEntityParticipant9", "");
		nodeMap.put("physicalEntityParticipant17", "");
		nodeMap.put("physicalEntityParticipant22", "");
		nodeMap.put("physicalEntityParticipant26", "");
		nodeMap.put("physicalEntityParticipant38", "");
		nodeMap.put("physicalEntityParticipant99", "");
		
		//  These represent interaction nodes
		nodeMap.put("catalysis43", "");
		nodeMap.put("biochemicalReaction6", "");
		nodeMap.put("biochemicalReaction37", "");
		nodeMap.put("catalysis5", "");

		//  We don't know the order of nodes;  so use nodeMap for look up.
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();
			String id = node.getIdentifier();
			String uri = Cytoscape.getNodeAttributes()
	              .getStringAttribute(id, MapBioPaxToCytoscape.BIOPAX_RDF_ID);
			//  Test a specific node label
			if (uri.endsWith("physicalEntityParticipant99")) {
				String label = Cytoscape.getNodeAttributes()
				                  .getStringAttribute(id, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
				assertEquals("Mg2+", label);
			}

			for(String key : new HashSet<String>(nodeMap.keySet())) {
				if(uri.contains(key)) {
					nodeMap.put(key, "found!");
					break;
				}
			}
		}

		//  Verify that we found all expected node identifiers.
		StringBuilder sb = new StringBuilder();
		for (String key : nodeMap.keySet()) {
			if (nodeMap.get(key).isEmpty())
				sb.append(key).append(",");
		}
		if(sb.length() > 0)
			fail("Network does not contain: " + sb.toString());
	}

	/**
	 * Verifies that Control Interactions and Conversion Interactions
	 * were mapped over sucessfully.
	 * 
	 * TODO re-factor this test as IT DEPENDS ON ORDER OF THE ELEMENTS, which is different when run from Ant or Eclipse...
	 */
	private void verifyControlConversion(CyNetwork cyNetwork) {
		//  First, find the Target Interaction:  biochemicalReaction37.
		int targetNodeIndex = 0;
		RootGraph rootGraph = cyNetwork.getRootGraph();
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();
			String uri = Cytoscape.getNodeAttributes()
	                  .getStringAttribute(node.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_RDF_ID);
			if (uri.endsWith("biochemicalReaction37")) {
				targetNodeIndex = node.getRootGraphIndex();
				break;
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should only be three edges;  one for left;  one for right;
		//  and one for control
		assertEquals(3, edgeIndices.length);
	}

	/**
	 * Verifies that CoFactors were mapped over successfully
	 * 
	 * TODO re-factor this test as IT DEPENDS ON ORDER OF THE ELEMENTS, which is different when run from Ant or Eclipse...
	 */
	private void verifyCoFactors(CyNetwork cyNetwork) {
		//  First, find the Target Interaction:  protein45.
		int targetNodeIndex = 0;
		RootGraph rootGraph = cyNetwork.getRootGraph();
		Iterator nodeIterator = cyNetwork.nodesIterator();

		while (nodeIterator.hasNext()) {
			CyNode node = (CyNode) nodeIterator.next();
			String uri = Cytoscape.getNodeAttributes()
	                  .getStringAttribute(node.getIdentifier(), MapBioPaxToCytoscape.BIOPAX_RDF_ID);
			if (uri.equals("smallMolecule99-Mg2+")) {
				targetNodeIndex = node.getRootGraphIndex();
			}
		}

		//  Get All Edges Adjacent to this Node
		int[] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(targetNodeIndex, true, true, true);

		//  There should only be two edges;  one for controller, and
		//  one for cofactor.
		assertEquals(2, edgeIndices.length);
	}
}
