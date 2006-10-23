package cytoscape;


import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.data.ImportHandler;
import cytoscape.data.Semantics;
import giny.model.Edge;
import giny.model.Node;
import java.util.*;
import java.io.IOException;

public class CytoscapeTest extends TestCase {
    CyNetwork cytoNetwork;
    String title;
    int nodeCount;
    int edgeCount;

    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    public void testGetImportHandler() throws Exception {
        ImportHandler importHandler = Cytoscape.getImportHandler();
        assertEquals(importHandler.getClass(), ImportHandler.class);
    }

    public void testNullNetwork() throws Exception {
        cytoNetwork = Cytoscape.getNullNetwork();

        title = cytoNetwork.getTitle();
        assertEquals("0", title);

        nodeCount = cytoNetwork.getNodeCount();
        assertEquals(0, nodeCount);

        edgeCount = cytoNetwork.getEdgeCount();
        assertEquals(0, edgeCount);
    }

    //public void test
    //try getting network attributes

    //try creating a network
    public void testCreateNetwork() throws Exception {
        try {
        cytoNetwork = Cytoscape.createNetworkFromFile("testNetwork");

        /*
           * Network title is unpredictable!
           */
//		title = cytoNetwork.getTitle();
//		assertEquals("20", title);

        nodeCount = cytoNetwork.getNodeCount();
        assertEquals(0, nodeCount);

        edgeCount = cytoNetwork.getEdgeCount();
        assertEquals(0, edgeCount);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testBug839() throws IOException {

        Set<String> nodes = new HashSet<String>();
        nodes.add("n1");
        nodes.add("n2");
        nodes.add("n3");
        nodes.add("n4");
        nodes.add("n5");

        cytoNetwork = Cytoscape.createNetworkFromFile("testData/bug_839.sif");

        // check that all nodes exist
        Iterator it = cytoNetwork.nodesIterator();
        while ( it.hasNext() ) {
            Node n = (Node)it.next();
            assertTrue("checking node " + n.getIdentifier(),
                       nodes.contains(n.getIdentifier()));
        }

        Set<String> edges = new HashSet<String>();
        edges.add(CyEdge.createIdentifier("n1","activates","n2"));
        edges.add(CyEdge.createIdentifier("n1","activates","n4"));
        edges.add(CyEdge.createIdentifier("n1","activates","n5"));
        edges.add(CyEdge.createIdentifier("n2","activates","n1"));
        edges.add(CyEdge.createIdentifier("n2","activates","n5"));
        edges.add(CyEdge.createIdentifier("n3","inhibits","n3"));
        edges.add(CyEdge.createIdentifier("n3","inhibits","n4"));
        edges.add(CyEdge.createIdentifier("n3","inhibits","n5"));
        edges.add(CyEdge.createIdentifier("n4","activates","n1"));
        edges.add(CyEdge.createIdentifier("n4","activates","n2"));
        edges.add(CyEdge.createIdentifier("n4","activates","n4"));
        edges.add(CyEdge.createIdentifier("n5","activates","n1"));
        edges.add(CyEdge.createIdentifier("n5","activates","n4"));
        edges.add(CyEdge.createIdentifier("n5","activates","n5"));

        it = cytoNetwork.edgesIterator();
        while ( it.hasNext() ) {
            Edge e = (Edge)it.next();
            assertTrue("checking edge " + e.getIdentifier(), edges.contains(e.getIdentifier()));
        }
    }

    public void testgetCyEdgeWithStrings() throws IOException {

        cytoNetwork = Cytoscape.createNetworkFromFile("testData/directedGraph.sif");

        assertEquals(2,cytoNetwork.getNodeCount());
        assertEquals(4,cytoNetwork.getEdgeCount());

        String en1 = CyEdge.createIdentifier("a","pp","b");

        // edge should exist in network already
        CyEdge ce1 = Cytoscape.getCyEdge("a",en1,"b","pp");
        assertNotNull(ce1);

        CyEdge ce1_again = Cytoscape.getCyEdge("a",en1,"b","pp");
        assertTrue(ce1 == ce1_again);

        // edge should be created
        String en2 = CyEdge.createIdentifier("a","xx","b");
        CyEdge ce2 = Cytoscape.getCyEdge("a",en2,"b","pp");
        assertNotNull(ce2);

        // should create a different edge because of directedness
        String en3 = CyEdge.createIdentifier("b","pp","a");
        CyEdge ce3 = Cytoscape.getCyEdge("b",en3,"a","pp");
        assertTrue(ce1 != ce3);

    }

    public void testgetCyEdgeWithNodes() throws IOException {
        cytoNetwork = Cytoscape.createNetworkFromFile("testData/directedGraph.sif");
        Node a = Cytoscape.getCyNode("a");
        Node b = Cytoscape.getCyNode("b");
        Node c = Cytoscape.getCyNode("c",true);
        String attr = Semantics.INTERACTION;

        // test directed edges
        assertNotNull(Cytoscape.getCyEdge(a,b,attr,"pp",false,true));
        assertNotNull(Cytoscape.getCyEdge(b,a,attr,"pp",false,true));
        assertNotNull(Cytoscape.getCyEdge(a,a,attr,"pp",false,true));
        assertNotNull(Cytoscape.getCyEdge(a,a,attr,"pp",false,true));
        assertNotNull(Cytoscape.getCyEdge(a,b,attr,"pd",false,true));
        assertNull(Cytoscape.getCyEdge(b,a,attr,"pd",false,true));

        // test undirectedness
        assertNotNull(Cytoscape.getCyEdge(b,a,attr,"pd",false,false));

        // test non-existent edge
        assertNull(Cytoscape.getCyEdge(a,c,attr,"pp",false,true));

        // test bad attr_value
        assertNull(Cytoscape.getCyEdge(a,b,attr,"xx",false,true));

        // test create node
        assertNotNull(Cytoscape.getCyEdge(a,c,attr,"pd",true,true));

        // make sure we got the node we created
        assertNotNull(Cytoscape.getCyEdge(a,c,attr,"pd",false,true));
    }

}
