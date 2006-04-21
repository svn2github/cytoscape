/*package cytoscape.util.unitTests;

import junit.framework.*;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import cytoscape.util.GraphSetUtils;
import cytoscape.unitTests.AllTests;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.giny.CytoscapeRootGraph;

import nct.graph.Graph;
import nct.graph.Edge;

public class GraphSetUtilsTest extends TestCase {
protected List networklist;
	
public void setUp() throws Exception {
	networklist = new ArrayList();
	RootGraph root = Cytoscape.getRootGraph();
	int a = root.createNode();
	int b = root.createNode();
	int c = root.createNode();
	int d = root.createNode();
	int e = root.createNode();
	
	int[] nodes1 = new int[] {a,b,c,d};
	int[] nodes2 = new int[] {b,c,d,e};
	
	int ab = root.createEdge(a,b,true);
	int bc = root.createEdge(b,c,true);
	int ac = root.createEdge(a,c,true);
	int bd = root.createEdge(b,d,true);
	int be = root.createEdge(b,e,true);
	int cd = root.createEdge(c,d,true);
	
	int[] edges1 = new int[] {ab,bc,ac,bd};
	int[] edges2 = new int[] {bd,bc,be};
	
	CyNetwork net1 = Cytoscape.createNetwork(nodes1,edges1,"graph1");
	CyNetwork net2 = Cytoscape.createNetwork(nodes2,edges2,"graph2");
}
	
public void tearDown() throws Exception {
		
}

public void testIntersection(){
	
}
}*/
