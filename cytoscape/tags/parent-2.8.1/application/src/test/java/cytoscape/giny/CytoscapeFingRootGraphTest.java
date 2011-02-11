/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.giny;

import junit.framework.TestCase;

import giny.model.Node;
import giny.model.Edge;
import cytoscape.CyNode;
import cytoscape.CyEdge;

/**
 *
 */
public class CytoscapeFingRootGraphTest extends TestCase {

	CytoscapeFingRootGraph root;
	
	public void setUp() throws Exception {
		root = new CytoscapeFingRootGraph();
	}

	public void tearDown() throws Exception {
		root = null;
	}
	
	public void testCreateNode() throws Exception {
		int n1id = root.createNode();
		assertTrue( n1id < 0 );
		Node n1 = root.getNode( n1id );
		assertNotNull( n1 );
		assertEquals( n1id, n1.getRootGraphIndex() );
	}

	public void testCreateEdge() throws Exception {
		Node n1 = root.getNode( root.createNode() );
		Node n2 = root.getNode( root.createNode() );
		int e1id = root.createEdge( n1.getRootGraphIndex(), n2.getRootGraphIndex() );
		assertTrue( e1id < 0 );
		Edge e1 = root.getEdge( e1id );
		assertNotNull( e1 );
		assertEquals( e1id, e1.getRootGraphIndex() );
	}

	public void testGetSetNodeIdentifier() throws Exception {
		Node n1 = root.getNode( root.createNode() );

		root.setNodeIdentifier("homer",n1.getRootGraphIndex());

		CyNode nx = root.getNode("homer");

		assertNotNull( nx );
		assertEquals( n1.getRootGraphIndex(), nx.getRootGraphIndex() );
	}

	public void testGetSetNodeIdentifierBad() throws Exception {

		root.setNodeIdentifier("homer",-73);

		CyNode nx = root.getNode("homer");

		assertNull( nx );
	}

	public void testGetNodeIdentifierNull() throws Exception {

		CyNode nx = root.getNode(null);

		assertNull( nx );
	}


	public void testGetSetEdgeIdentifier() throws Exception {
		Node n1 = root.getNode( root.createNode() );
		Node n2 = root.getNode( root.createNode() );
		Edge e1 = root.getEdge( root.createEdge( n1.getRootGraphIndex(), n2.getRootGraphIndex() ) );

		root.setEdgeIdentifier("homer",e1.getRootGraphIndex());

		CyEdge ex = root.getEdge("homer");

		assertNotNull( ex );
		assertEquals( e1.getRootGraphIndex(), ex.getRootGraphIndex() );
	}

	public void testGetSetEdgeIdentifierBad() throws Exception {

		root.setEdgeIdentifier("homer",-73);

		CyEdge ex = root.getEdge("homer");

		assertNull( ex );
	}

	public void testGetEdgeIdentifierNull() throws Exception {

		CyEdge ex = root.getEdge(null);

		assertNull( ex );
	}
}
