/*
 File: XGMMLTest.java

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
package cytoscape.data;

import cytoscape.Cytoscape;
import cytoscape.data.readers.XGMMLReader;
import cytoscape.data.writers.XGMMLWriter;
import junit.framework.TestCase;
import org.cytoscape.CyEdge;
import org.cytoscape.CyNetwork;
import org.cytoscape.CyNode;
import org.cytoscape.RootGraph;
import org.cytoscape.view.GraphView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Tests round-tripping edge directionality:
 * exporting to XGMML and then importing from it must preserve edge directionality
 */
public class XGMMLTest extends TestCase {
	public void testRoundtrip() throws Exception {
		CyNetwork network = Cytoscape.createNetwork("directed test network");
		GraphView view = Cytoscape.createNetworkView(network);

		// create nodes
		CyNode a = Cytoscape.getCyNode("from", true);
		CyNode b = Cytoscape.getCyNode("to", true);

		String attr = Semantics.INTERACTION;

		// create two edges:
		CyEdge undirected = Cytoscape.getCyEdge(a, b, attr, "u", true, false);
		CyEdge directed = Cytoscape.getCyEdge(a, b, attr, "d", true, true);
		assertNotNull(undirected);
		assertNotNull(directed);

		network.addNode(a);
		network.addNode(b);
		network.addEdge(undirected);
		network.addEdge(directed);

		// write network in xgmml format to string
		StringWriter writer = new StringWriter();

		final XGMMLWriter xgmmlWriter = new XGMMLWriter(network, view);

		xgmmlWriter.write(writer);
		writer.close();

		String output = writer.toString();
		assertTrue("must have some output", output.length() > 0);

		// TODO: could check here whether result is valid xml

		// remove graph items so that we can check loading; assert that items are really removed
		RootGraph rootGraph = Cytoscape.getRootGraph();
		System.out.println("edges:"+Cytoscape.getRootGraph().getEdgeCount());

		rootGraph.removeEdge(directed);
		rootGraph.removeEdge(undirected);
		assertNull("item not deleted", Cytoscape.getCyEdge(a, b, attr, "u", false, false));
		assertNull("item not deleted", Cytoscape.getCyEdge(a, b, attr, "d", false, true));

		rootGraph.removeNode(a);
		rootGraph.removeNode(b);
		assertNull("item not deleted", Cytoscape.getCyNode("from", false));
		assertNull("item not deleted", Cytoscape.getCyNode("to", false));

		// load from the string created above
		InputStream input = new ByteArrayInputStream(output.getBytes());
		XGMMLReader xgmmlReader = new XGMMLReader(input);
		xgmmlReader.read();

		// check network contents:
		a = Cytoscape.getCyNode("from", false);
		assertNotNull("item not reloaded", a);
		b = Cytoscape.getCyNode("to", false);
		assertNotNull("item not reloaded", b);

		undirected = Cytoscape.getCyEdge(a, b, attr, "u", false, false);
		assertNotNull("item not reloaded: undirected", undirected);
		directed = Cytoscape.getCyEdge(a, b, attr, "d", false, true);
		assertNotNull("item not reloaded: directed", directed);

		// check directionality:
		assertTrue("directionality doesn't match (directed)", directed.isDirected());
		assertFalse("directionality doesn't match (undirected)", undirected.isDirected());
	}
}
