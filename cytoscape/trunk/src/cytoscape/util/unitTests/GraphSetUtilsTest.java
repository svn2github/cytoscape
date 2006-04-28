/*package cytoscape.util.unitTests;

import junit.framework.*;
import java.util.*;
import java.lang.Object;

import cytoscape.util.GraphSetUtils;
import cytoscape.unitTests.AllTests;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.giny.CytoscapeRootGraph;

import giny.model.RootGraph;


public class GraphSetUtilsTest extends TestCase {
protected List networklist;
protected int a,b,c,d,e,ab,bc,ac,bd,be,cd,ce;
	
public void setUp() throws Exception {
	networklist = new ArrayList();
	RootGraph root = Cytoscape.getRootGraph();
	a = root.createNode();
	b = root.createNode();
	c = root.createNode();
	d = root.createNode();
	e = root.createNode();
	
	int[] nodes1 = new int[] {a,b,c,d};
	int[] nodes2 = new int[] {b,c,d,e};
	
	ab = root.createEdge(a,b,true);
	bc = root.createEdge(b,c,true);
	ac = root.createEdge(a,c,true);
	bd = root.createEdge(b,d,true);
	be = root.createEdge(b,e,true);
	cd = root.createEdge(c,d,true);
	
	int[] edges1 = new int[] {ab,bc,ac,bd};
	int[] edges2 = new int[] {bd,bc,be};
	
	CyNetwork net1 = Cytoscape.createNetwork(nodes1,edges1,"graph1");
	CyNetwork net2 = Cytoscape.createNetwork(nodes2,edges2,"graph2");
	networklist.add(0, net1); 
	networklist.add(1, net2);
	
	//verify created nodes exist
	assertTrue (net1.containsNode(getNode(a)));
	assertTrue (net1.containsNode(getNode(b)));
	assertTrue (net1.containsNode(getNode(c)));
	assertTrue (net1.containsNode(getNode(d)));
	
	assertTrue (net2.containsNode(getNode(b)));
	assertTrue (net2.containsNode(getNode(c)));
	assertTrue (net2.containsNode(getNode(d)));
	assertTrue (net2.containsNode(getNode(e)));
	
	assertTrue (net1.containsEdge(getEdge(ab)));
	assertTrue (net1.containsEdge(getEdge(bc)));
	assertTrue (net1.containsEdge(getEdge(ac)));
	assertTrue (net1.containsEdge(getEdge(bd)));
	
	assertTrue (net2.containsEdge(getEdge(bd)));
	assertTrue (net2.containsEdge(getEdge(bc)));
	assertTrue (net2.containsEdge(getEdge(be)));
	
}
	
public void tearDown() throws Exception {
		
}

//-------------------------------------------------------------------------
public void testIntersection(){

	CyNetwork n = createIntersectionGraph(networklist, true, "intersect");
	
	assertTrue (n.containsEdge(getEdge(bc)));
	assertTrue (n.containsEdge(getEdge(bd)));
	assertFalse (n.containsEdge(getEdge(ab)));
	assertFalse (n.containsEdge(getEdge(be)));
	
	assertTrue (n.containsNode(getNode(b)));
	assertTrue (n.containsNode(getNode(c)));
	assertTrue (n.containsNode(getNode(d)));
	assertFalse (n.containsNode(getNode(a)));
	assertFalse (n.containsNode(getNode(e)));
	
}// testIntersection
//-------------------------------------------------------------------------

public void testDifference(){

	CyNetwork x = createDifferenceGraph(networklist, true, "difference");
	
	
	assertTrue (x.containsNode(getNode(a)));
	assertFalse (x.containsNode(getNode(b)));
	assertFalse (x.containsNode(getNode(c)));
	assertFalse (x.containsNode(getNode(d)));
	assertFalse (x.containsNode(getNode(e)));
	
	assertFalse (x.containsEdge(getEdge(ab)));
	assertFalse (x.containsEdge(getEdge(bc)));
	assertFalse (x.containsEdge(getEdge(ac)));
	assertFalse (x.containsEdge(getEdge(be)));
	assertFalse (x.containsEdge(getEdge(bd)));


}// testDifference
//-------------------------------------------------------------------------
public void testUnion(){

	CyNetwork y = createUnionGraph(networklist, true, "union");
	
	
	assertTrue (y.containsEdge(getEdge(ab)));
	assertTrue (y.containsEdge(getEdge(bc)));
	assertTrue (y.containsEdge(getEdge(ac)));
	assertTrue (y.containsEdge(getEdge(bd)));
	assertTrue (y.containsEdge(getEdge(ce)));
	
	assertTrue (y.containsNode(getNode(a)));
	assertTrue (y.containsNode(getNode(b)));
	assertTrue (y.containsNode(getNode(c)));
	assertTrue (y.containsNode(getNode(d)));
	assertTrue (y.containsNode(getNode(e)));
	
}// testUnion
//-------------------------------------------------------------------------
}*/
