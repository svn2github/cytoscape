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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.csplugins.semanticsummary.CloudParameters;
import cytoscape.csplugins.semanticsummary.CloudWordInfo;
import cytoscape.csplugins.semanticsummary.SemanticSummaryParameters;

public class CloudParametersUnitTest extends TestCase {
	
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
		CyNode node1 = Cytoscape.getCyNode("Node onenode", true);
		CyNode node2 = Cytoscape.getCyNode("Node twonode", true);
		CyNode node3 = Cytoscape.getCyNode("ONENODE", true);
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
		CyNode node5 = Cytoscape.getCyNode("Node onenode", true);
		CyNode node6 = Cytoscape.getCyNode("Node twonode", true);
		CyNode node7 = Cytoscape.getCyNode("ONENODE", true);
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
		List<String> nodeNames = new ArrayList<String>();
		for(Iterator iter = allNodes.iterator(); iter.hasNext();)
			nodeNames.add(iter.next().toString());
		
		parentParams.setNetworkNodes(nodeNames);
		parentParams.setNetworkNumNodes(13);
		
		List<String> selNodeNames = new ArrayList<String>();
		for(Iterator iter = selNodes.iterator(); iter.hasNext();)
			selNodeNames.add(iter.next().toString());
		
		cloudParams.setSelectedNodes(selNodeNames);
		cloudParams.setSelectedNumNodes(7);
		
		//Register Cloud
		parentParams.addCloud(cloudName, cloudParams);
		
	}

	@Test
	public void testInitializeNetworkCounts() {
		
		//Test flags
		assertFalse(cloudParams.getCountInitialized());
		assertFalse(cloudParams.getSelInitialized());
		assertFalse(cloudParams.getRatiosInitialized());
		
		cloudParams.initializeNetworkCounts();
		
		//Test network counts
		HashMap<String,Integer> counts = cloudParams.getNetworkCounts();
		assertEquals((Integer)counts.get("node"),new Integer(9));
		assertEquals((Integer)counts.get("onenode"),new Integer(4));
		assertEquals((Integer)counts.get("twonode"),new Integer(2));
		assertEquals((Integer)counts.get("double"),new Integer(2));
		assertEquals((Integer)counts.get("punctuation"),new Integer(4));
		assertEquals((Integer)counts.get("non-stripped"),new Integer(2));
		assertEquals((Integer)counts.get("strip"),new Integer(1));
		assertEquals((Integer)counts.get("with"),null);
		assertEquals((Integer)counts.get("reactome"),null);
		
		//Test mappings
		HashMap<String, List<String>> mapping = cloudParams.getStringNodeMapping();
		
		//Single node in list
		List<String> nodeList = mapping.get("strip");
		Iterator<String> iter = nodeList.iterator();
		while (iter.hasNext())
		{
			String curNodeName = (String)iter.next();
			CyNode curNode = Cytoscape.getCyNode(curNodeName);
			assertEquals(curNode.toString(),"Reactome_node to strip lots from.");
		}
		
		//Multiple nodes in list
		int count = 0;
		nodeList = mapping.get("twonode");
		Iterator<String> iter2 = nodeList.iterator();
		while (iter2.hasNext())
		{
			String curNodeName = (String)iter2.next();
			CyNode curNode = Cytoscape.getCyNode(curNodeName);
			assertEquals(curNode.toString(),"Node twonode");
			count++;
		}
		assertEquals(count,2);//make sure we have 2 nodes in list
		
		//Test flags
		assertTrue(cloudParams.getCountInitialized());
		assertFalse(cloudParams.getSelInitialized());
		assertFalse(cloudParams.getRatiosInitialized());
	}

	@Test
	public void testUpdateSelectedCounts() {
		
		//Test flags
		assertFalse(cloudParams.getCountInitialized());
		assertFalse(cloudParams.getSelInitialized());
		assertFalse(cloudParams.getRatiosInitialized());
		
		cloudParams.updateSelectedCounts();
		
		//Test selected counts
		HashMap<String,Integer> counts = cloudParams.getSelectedCounts();
		assertEquals((Integer)counts.get("node"),new Integer(5));
		assertEquals((Integer)counts.get("onenode"),new Integer(2));
		assertEquals((Integer)counts.get("twonode"),new Integer(1));
		assertEquals((Integer)counts.get("double"),new Integer(1));
		assertEquals((Integer)counts.get("punctuation"),new Integer(2));
		assertEquals((Integer)counts.get("non-stripped"),new Integer(1));
		assertEquals((Integer)counts.get("strip"),new Integer(1));
		assertEquals((Integer)counts.get("with"),null);
		assertEquals((Integer)counts.get("reactome"),null);
		
		//Test flags
		assertTrue(cloudParams.getCountInitialized());
		assertTrue(cloudParams.getSelInitialized());
		assertFalse(cloudParams.getRatiosInitialized());
	}

	@Test
	public void testUpdateRatios() {
		
		//Test flags
		assertFalse(cloudParams.getCountInitialized());
		assertFalse(cloudParams.getSelInitialized());
		assertFalse(cloudParams.getRatiosInitialized());
		
		cloudParams.updateRatios();
		
		//Test Ratios (with default k = 1)
		HashMap<String,Double> ratios = cloudParams.getRatios();
		assertEquals((Double)ratios.get("node"),new Double((5.0*13)/(7*9)));
		assertEquals((Double)ratios.get("onenode"),new Double((2.0*13)/(7*4)));
		assertEquals((Double)ratios.get("twonode"),new Double((1.0*13)/(7*2)));
		assertEquals((Double)ratios.get("double"),new Double((1.0*13)/(7*2)));
		assertEquals((Double)ratios.get("punctuation"),new Double((2.0*13)/(7*4)));
		assertEquals((Double)ratios.get("non-stripped"),new Double((1.0*13)/(7*2)));
		assertEquals((Double)ratios.get("strip"),new Double((1.0*13)/(7*1)));
		assertEquals((Double)ratios.get("with"),null);
		assertEquals((Double)ratios.get("reactome"),null);
		
		assertEquals(cloudParams.getMaxRatio(),new Double((1.0*13)/(7*1)));
		assertEquals(cloudParams.getMinRatio(),new Double((13.0/14)));
		
		//Test flags
		assertTrue(cloudParams.getCountInitialized());
		assertTrue(cloudParams.getSelInitialized());
		assertTrue(cloudParams.getRatiosInitialized());
	}

	@Test
	public void testCalculateFontSizes() {
		
		//Test flags
		assertFalse(cloudParams.getCountInitialized());
		assertFalse(cloudParams.getSelInitialized());
		assertFalse(cloudParams.getRatiosInitialized());
		
		cloudParams.calculateFontSizes();
		ArrayList<CloudWordInfo> cloudWords = cloudParams.getCloudWordInfoList();
		
		//Check that first and last entries have max and min sizes
		assertEquals(cloudWords.get(0).getFontSize(),
				new Integer(parentParams.getMaxFont()));
		assertEquals(cloudWords.get(cloudWords.size() - 1).getFontSize(),
				new Integer(parentParams.getMinFont()));
		
		//Test flags
		assertTrue(cloudParams.getCountInitialized());
		assertTrue(cloudParams.getSelInitialized());
		assertTrue(cloudParams.getRatiosInitialized());
	}

}
