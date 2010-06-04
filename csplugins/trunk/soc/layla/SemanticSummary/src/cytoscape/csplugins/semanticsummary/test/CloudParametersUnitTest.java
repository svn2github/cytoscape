/*
 File: CloudParameters.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.csplugins.semanticsummary.CloudParameters;
import cytoscape.csplugins.semanticsummary.SemanticSummaryParameters;

public class CloudParametersUnitTest {
	
	//Variables
	String cloudName = "CloudName";
	SemanticSummaryParameters parentParams;
	CloudParameters cloudParams;
	

	@Before
	public void setUp() throws Exception {
		
		//Create parent parameters
		parentParams = new SemanticSummaryParameters();
		
		//Create a set of CyNodes
		List<CyNode> allNodes = new ArrayList<CyNode>();
		CyNode node1 = Cytoscape.getCyNode("Node one", true);
		CyNode node2 = Cytoscape.getCyNode("Node two", true);
		CyNode node3 = Cytoscape.getCyNode("ONE", true);
		CyNode node4 = Cytoscape.getCyNode("Double double", true);//only one count
		CyNode punctNode1 = Cytoscape.getCyNode("Node with punctuation.", true);
		CyNode punctNode2 = Cytoscape.getCyNode("Node with non-stripped punctuation", true);
		
		//Add nodes to list
		allNodes.add(node1);
		allNodes.add(node2);
		allNodes.add(node3);
		allNodes.add(node4);
		allNodes.add(punctNode1);
		allNodes.add(punctNode2);
		
		//Create CloudParameters object
		cloudParams = new CloudParameters();
		cloudParams.setCloudName(cloudName);
		cloudParams.setNetworkParams(parentParams);
		
		//Create set of selected Nodes
		Set<CyNode> selNodes = new HashSet<CyNode>();
		CyNode node5 = Cytoscape.getCyNode("Node one", true);
		CyNode node6 = Cytoscape.getCyNode("Node two", true);
		CyNode node7 = Cytoscape.getCyNode("ONE", true);
		CyNode node8 = Cytoscape.getCyNode("Double double", true);//only one count
		CyNode node9 = Cytoscape.getCyNode("Reactome_node to strip lots from.", true);
		CyNode punctNode3 = Cytoscape.getCyNode("Node with punctuation.", true);
		CyNode punctNode4 = Cytoscape.getCyNode("Node with non-stripped punctuation", true);
		
		//Add Nodes to lists
		allNodes.add(node5);
		allNodes.add(node6);
		allNodes.add(node7);
		allNodes.add(node8);
		allNodes.add(node9);
		allNodes.add(punctNode3);
		allNodes.add(punctNode4);
		
		selNodes.add(node5);
		selNodes.add(node6);
		selNodes.add(node7);
		selNodes.add(node8);
		selNodes.add(node9);
		selNodes.add(punctNode3);
		selNodes.add(punctNode4);
		
		//Set Lists in Params
		parentParams.setNetworkNodes(allNodes);
		parentParams.setNetworkNumNodes(13);
		cloudParams.setSelectedNodes(selNodes);
		cloudParams.setSelectedNumNodes(7);
		
		//Register Cloud
		parentParams.addCloud(cloudName, cloudParams);
		
	}

	@Test
	public void testInitializeNetworkCounts() {
		cloudParams.initializeNetworkCounts();
		
		HashMap<String,Integer> counts = cloudParams.getNetworkCounts();
		assertEquals((Integer)counts.get("node"),new Integer(9));
		//TODO - finish
	}

	@Test
	public void testUpdateSelectedCounts() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateRatios() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalculateFontSizes() {
		fail("Not yet implemented");
	}

	@Test
	public void testRetrieveInputVals() {
		fail("Not yet implemented");
	}

}
