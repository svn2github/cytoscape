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
package cytoscape;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import junit.framework.TestCase;


/**
 *
 */
public class CyNodeTest extends TestCase {
	CyNetwork cytoNetwork;
	String title;
	int nodeCount;
	int edgeCount;

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	
	public void testSetNestedNetwork_before() throws Exception {
		CyNode node = Cytoscape.getCyNode("a", true);	
		assertNull(node.getNestedNetwork());
				
		// Before the setNestedNetwork() is called from the CyNode
		// There should be no NodeAttribute "nested.network.name" and NetworkAttribute "parent.node.name.list"
		cytoscape.data.CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		String[] nodeAttriNames = nodeAttrs.getAttributeNames();

		boolean found_node_attribute = false;
		for (int i=0; i<nodeAttriNames.length; i++){
			if (nodeAttriNames[i].equals("nested.network.name")){
				found_node_attribute = true;
				break;
			}
		}
		
		cytoscape.data.CyAttributes networkAttrs = Cytoscape.getNetworkAttributes();
		String[] networkAttriNames = networkAttrs.getAttributeNames();
		boolean found_network_attribute = false;
		for (int i=0; i<networkAttriNames.length; i++){
			if (networkAttriNames[i].equals("parent.node.name.list")){
				found_network_attribute = true;
				break;
			}
		}

		assertFalse(found_node_attribute);
		assertFalse(found_network_attribute);	
	}


	public void testSetNestedNetwork_after() throws Exception {
		// Create a network with 2 nodes
		CyNetwork network = Cytoscape.createNetwork("Test title", false);
		CyNode node1_in_nestedNetwork = Cytoscape.getCyNode("node1", true);
		CyNode node2_in_nestedNetwork = Cytoscape.getCyNode("node2", true);
		network.addNode(node1_in_nestedNetwork);
		network.addNode(node2_in_nestedNetwork);
				
		// Create a CyNode
		CyNode node = Cytoscape.getCyNode("a", true);				

		// Set nestedNetwork for newly created node
		node.setNestedNetwork(network);

		// After the setNestedNetwork() is called from the CyNode
		// There should be NodeAttribute "nested_network_id" and NetworkAttribute "parent_nodes"
		cytoscape.data.CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		String[] nodeAttriNames = nodeAttrs.getAttributeNames();

		cytoscape.data.CyAttributes networkAttrs = Cytoscape.getNetworkAttributes();
		String[] networkAttriNames = networkAttrs.getAttributeNames();
		
		boolean found_node_attribute = false;
		boolean found_network_attribute = false;
		
		for (int i=0; i<nodeAttriNames.length; i++){
			if (nodeAttriNames[i].equals("nested_network_id")){			
				found_node_attribute = true;
				break;
			}
		}
		
		for (int i=0; i<networkAttriNames.length; i++){
			if (networkAttriNames[i].equals("parent_nodes")){
				found_network_attribute = true;
				break;
			}
		}
				
		assertTrue(found_node_attribute);
		assertTrue(found_network_attribute);		
	}
}
