//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.data.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import y.view.Graph2D;

import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.data.CyNetworkEvent;
import cytoscape.data.CyNetworkListener;
import cytoscape.data.CyNetworkAdapter;
import cytoscape.data.ExpressionData;
//-----------------------------------------------------------------------------------------
public class CyNetworkTest extends TestCase {
    private CyNetworkEvent currentEvent = null;
    private CyNetworkListener listener;
//------------------------------------------------------------------------------
public CyNetworkTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {
    listener = new CyNetworkAdapter() {
        public void onCyNetworkEvent(CyNetworkEvent event) {
            currentEvent = event;
        }
    };
}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testBasic() throws Exception { 
    System.out.println ("testBasic");
    
    Graph2D graph = new Graph2D();
    GraphObjAttributes nodeAttributes = new GraphObjAttributes();
    GraphObjAttributes edgeAttributes = new GraphObjAttributes();
    ExpressionData startData = new ExpressionData("testData/gal1.10x20.mRNA");
    CyNetwork network = new CyNetwork(graph, nodeAttributes,
                                      edgeAttributes, startData);
    
    assertTrue( network.getGraph() == graph );
    assertTrue( network.getNodeAttributes() == nodeAttributes );
    assertTrue( network.getEdgeAttributes() == edgeAttributes );
    assertTrue( network.getExpressionData() == startData );
    
    Graph2D newGraph = new Graph2D();
    network.setGraph(newGraph);
    assertTrue( network.getGraph() == newGraph );
    GraphObjAttributes newNodeAttributes = new GraphObjAttributes();
    network.setNodeAttributes(newNodeAttributes);
    assertTrue( network.getNodeAttributes() == newNodeAttributes );
    GraphObjAttributes newEdgeAttributes = new GraphObjAttributes();
    network.setEdgeAttributes(newEdgeAttributes);
    assertTrue( network.getEdgeAttributes() == newEdgeAttributes );
    ExpressionData expData = new ExpressionData("testData/gal1.22x5.mRNA");
    network.setExpressionData(expData);
    assertTrue( network.getExpressionData() == expData );
    
    CyNetwork noExpNetwork = new CyNetwork(graph, nodeAttributes, edgeAttributes);
    assertTrue( noExpNetwork.getGraph() == graph );
    assertTrue( noExpNetwork.getNodeAttributes() == nodeAttributes );
    assertTrue( noExpNetwork.getEdgeAttributes() == edgeAttributes );
    assertTrue( noExpNetwork.getExpressionData() == null );
    
    CyNetwork nullNetwork = new CyNetwork(null, null, null, null);
    assertTrue(nullNetwork.getGraph() != null);
    assertTrue(nullNetwork.getNodeAttributes() != null);
    assertTrue(nullNetwork.getEdgeAttributes() != null);
    assertTrue(nullNetwork.getExpressionData() == null);
}
//-------------------------------------------------------------------------
public void testListeners() throws Exception {
    String callerID = "CyNetworkTest.testListeners";
    CyNetwork network = new CyNetwork(new Graph2D(), new GraphObjAttributes(),
                                      new GraphObjAttributes(), null);
    network.addCyNetworkListener(listener);
    
    network.beginActivity(callerID); //this should fire a begin event
    assertTrue(network.isStateClear() == false);
    assertTrue(currentEvent != null);
    assertTrue(currentEvent.getNetwork() == network);
    assertTrue(currentEvent.getType() == CyNetworkEvent.BEGIN);
    currentEvent = null;
    network.beginActivity(callerID); //this shouldn't fire an event
    assertTrue(network.isStateClear() == false);
    assertTrue(currentEvent == null);
    network.endActivity(callerID);  //this shouldn't fire an event
    assertTrue(network.isStateClear() == false);
    assertTrue(currentEvent == null);
    network.endActivity(callerID);  //this should fire a matching end event
    assertTrue(network.isStateClear() == true);
    assertTrue(currentEvent != null);
    assertTrue(currentEvent.getNetwork() == network);
    assertTrue(currentEvent.getType() == CyNetworkEvent.END);
    currentEvent = null;
    network.endActivity(callerID); //this unmatched call should do nothing
    assertTrue(network.isStateClear() == true);
    assertTrue(currentEvent == null);
    network.forceClear(callerID); //again should do nothing
    assertTrue(network.isStateClear() == true);
    assertTrue(currentEvent == null);
    
    network.beginActivity(callerID); //should fire a begin event
    assertTrue(network.isStateClear() == false);
    assertTrue(currentEvent != null);
    assertTrue(currentEvent.getNetwork() == network);
    assertTrue(currentEvent.getType() == CyNetworkEvent.BEGIN);
    currentEvent = null;
    network.forceClear(callerID); //should force an end event
    assertTrue(network.isStateClear() == true);
    assertTrue(currentEvent != null);
    assertTrue(currentEvent.getNetwork() == network);
    assertTrue(currentEvent.getType() == CyNetworkEvent.END);
    
    Set allListeners = network.getCyNetworkListeners();
    assertTrue(allListeners.size() == 1);
    assertTrue(allListeners.iterator().next() == listener);
    boolean wasThere = network.removeCyNetworkListener(listener);
    assertTrue(wasThere == true);
    assertTrue(network.getCyNetworkListeners().size() == 0);
    boolean stillThere = network.removeCyNetworkListener(listener);
    assertTrue(stillThere == false);
}
//-------------------------------------------------------------------------
public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyNetworkTest.class));
}
//-------------------------------------------------------------------------
}

