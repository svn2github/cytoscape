package cytoscape.data;

import junit.framework.TestCase;
import org.cytoscape.GraphPerspective;
import org.cytoscape.view.GraphView;

import org.cytoscape.Edge;
import org.cytoscape.Node;

import cytoscape.Cytoscape;
import cytoscape.data.writers.XGMMLWriter;
import cytoscape.data.readers.XGMMLReader;
import cytoscape.data.Semantics;
import org.cytoscape.RootGraph;

import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Tests round-tripping edge directionality:
 * exporting to XGMML and then importing from it must preserve edge directionality
 */
public class XGMMLTest extends TestCase {
	public void testRoundtrip() throws Exception {
		GraphPerspective network = Cytoscape.createNetwork("directed test network"); 
		GraphView view = Cytoscape.createNetworkView(network);

		// create nodes
		Node a = Cytoscape.getCyNode("from", true);
		Node b = Cytoscape.getCyNode("to", true);

		String attr = Semantics.INTERACTION;

		// create two edges:
		Edge undirected = Cytoscape.getCyEdge(a, b, attr, "u", true, false);
		Edge directed = Cytoscape.getCyEdge(a, b, attr, "d", true, true);
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
