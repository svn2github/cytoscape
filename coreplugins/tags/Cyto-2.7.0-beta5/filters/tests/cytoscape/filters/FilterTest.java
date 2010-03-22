package cytoscape.filters;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import junit.framework.TestCase;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

public class FilterTest extends TestCase {
	
	protected void initNetwork() {
		cyNetwork = Cytoscape.createNetwork("network1");
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

		//  Create Sample String Attributes
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		edgeAttributes.setAttribute(edge0.getIdentifier(), PMID, "12345");
		edgeAttributes.setAttribute(edge1.getIdentifier(), PMID, "12345");
		edgeAttributes.setAttribute(edge2.getIdentifier(), PMID, "12666");
	}
	
	protected static final String LOCATION = "location";
	protected static final String NUCLEUS = "nucleus";
	protected static final String CYTOPLASM = "cytoplasm";
	protected static final String RANK = "rank";
	protected static final String SCORE = "score";
	protected static final String PMID = "pmid";
	protected CyNetwork cyNetwork;
	
	public void testDumb() {
		//No test for FilterTest. It holds test data only!
		// Without this dumb test-case, a warning message will be printed out
	}

}
