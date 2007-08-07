// $Revision$
// $Date$
// $Author$

package cytoscape.view.unitTests;

import junit.framework.*;
import java.io.*;
import java.util.*;

import giny.model.*;
import giny.view.*;

import cytoscape.*;
import cytoscape.view.CyNetworkView;
import cytoscape.unitTests.AllTests;

public class CytoscapeViewTests extends TestCase {

    CyNetwork network;
    CyNode node1;
    CyNode node2;
    CyEdge edge1;
    CyEdge edge2;
    CyNetworkView view;
    NodeView nodeView1;
    NodeView nodeView2;
    EdgeView edgeView1;
    EdgeView edgeView2;

public CytoscapeViewTests (String name) {
    super (name);
}

public void setUp () throws Exception {
  
    
    node1 = Cytoscape.getCyNode("node1", true);
    node2 = Cytoscape.getCyNode("node2", true);
    edge1 = Cytoscape.getCyEdge("node1", "node1 (pp) node2", "node2", "pp");
    edge2 = Cytoscape.getCyEdge("node2", "node2 (pp) node1", "node1", "pp");
    int[] nodeArray = { node1.getRootGraphIndex(), node2.getRootGraphIndex() };
    int[] edgeArray = { edge1.getRootGraphIndex(), edge2.getRootGraphIndex() };
    network = Cytoscape.createNetwork(nodeArray, edgeArray, null);
    view = Cytoscape.createNetworkView(network);
    nodeView1 = view.getNodeView(node1);
    nodeView2 = view.getNodeView(node2);
    edgeView1 = view.getEdgeView(edge1);
    edgeView2 = view.getEdgeView(edge2);
}

public void tearDown () throws Exception {
}

/**
 * Tests that the view is properly modified when the flagger is changed.
 */
public void testFilterToView() throws Exception {
    checkState(false, false, false, false);
    network.setFlagged(node1, true);
    checkState(true, false, false, false);
    network.setFlagged(edge2, true);
    checkState(true, false, false, true);
    network.flagAllNodes();
    checkState(true, true, false, true);
    network.flagAllEdges();
    checkState(true, true, true, true);
    network.setFlagged(node2, false);
    checkState(true, false, true, true);
    network.setFlagged(edge1, false);
    checkState(true, false, false, true);
    network.unFlagAllEdges();
    checkState(true, false, false, false);
    network.unFlagAllNodes();
    checkState(false, false, false, false);
}

/**
 * Tests that the flagger is properly modified when the view is changed.
 */
public void testViewToFilter() throws Exception {
    checkState(false, false, false, false);
    nodeView1.setSelected(true);
    checkState(true, false, false, false);
    edgeView2.setSelected(true);
    checkState(true, false, false, true);
    nodeView2.setSelected(true);
    checkState(true, true, false, true);
    edgeView1.setSelected(true);
    checkState(true, true, true, true);
    nodeView2.setSelected(false);
    checkState(true, false, true, true);
    edgeView1.setSelected(false);
    checkState(true, false, false, true);
    edgeView2.setSelected(false);
    checkState(true, false, false, false);
    nodeView1.setSelected(false);
    checkState(false, false, false, false);
}

/**
 * Checks that the current state of the filter and the view match the state
 * defined by the arguments.
 */
public void checkState ( boolean n1, boolean n2, boolean e1, boolean e2 ) {
  assertTrue( network.isFlagged(node1) == n1 );
    assertTrue( network.isFlagged(node2) == n2 );
    assertTrue( network.isFlagged(edge1) == e1 );
    assertTrue( network.isFlagged(edge2) == e2 );
    assertTrue( nodeView1.isSelected() == n1 );
    assertTrue( nodeView2.isSelected() == n2 );
    assertTrue( edgeView1.isSelected() == e1 );
    assertTrue( edgeView2.isSelected() == e2 );
}

public static void main ( String[] args )  {
  junit.textui.TestRunner.run (new TestSuite (CytoscapeViewTests.class));
  System.exit(0);
}
//------------------------------------------------------------------------------
}


