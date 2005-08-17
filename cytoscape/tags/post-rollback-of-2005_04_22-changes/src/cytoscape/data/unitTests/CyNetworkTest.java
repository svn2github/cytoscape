//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.data.unitTests;

import junit.framework.*;
import java.io.*;
import java.util.*;

import giny.model.*;

import cytoscape.data.GraphObjAttributes;
import cytoscape.CyNetwork;
import cytoscape.CyNetworkEvent;
import cytoscape.CyNetworkListener;
import cytoscape.CyNetworkAdapter;
import cytoscape.data.ExpressionData;

public class CyNetworkTest extends TestCase {

  private CyNetworkEvent currentEvent = null;
  private CyNetworkListener listener;

  public CyNetworkTest(String name) {super(name);}

  public void setUp() throws Exception {
    listener = new CyNetworkAdapter() {
        public void onCyNetworkEvent(CyNetworkEvent event) {
          currentEvent = event;
        }
      };
  }

  public void tearDown() throws Exception {}

  public void testBasic() throws Exception {}// { 
//     System.out.println ("testBasic");
    
//     CyNetwork defaultNetwork = Cytoscape.createNetwork();
//     assertTrue( defaultNetwork.getExpressionData() == null );
    
//     ExpressionData startData = new ExpressionData("testData/gal1.10x20.mRNA");
//     CyNetwork network = new CyNetwork(rootGraph, nodeAttributes,
//                                       edgeAttributes, startData);
    
//     assertTrue( network.getRootGraph() == rootGraph );
//     assertTrue( network.getGraphPerspective() != null );
//     assertTrue( network.getGraphPerspective().getRootGraph() == rootGraph );
//     assertTrue( Cytoscape.getNodeNetworkData() == nodeAttributes );
//     assertTrue( network.getEdgeAttributes() == edgeAttributes );
//     assertTrue( network.getExpressionData() == startData );
//     assertTrue( network.getNeedsLayout() == false );
//     network.setNeedsLayout(true);
//     assertTrue( network.getNeedsLayout() == true );
//     network.setNeedsLayout(false);
//     assertTrue( network.getNeedsLayout() == false );
    
//     GraphPerspective newPerspective = GinyFactory.createGraphPerspective(rootGraph);
//     network.setGraphPerspective(newPerspective);
//     assertTrue( network.getGraphPerspective() == newPerspective );
//     GraphObjAttributes newNodeAttributes = new GraphObjAttributes();
//     network.setNodeAttributes(newNodeAttributes);
//     assertTrue( Cytoscape.getNodeNetworkData() == newNodeAttributes );
//     GraphObjAttributes newEdgeAttributes = new GraphObjAttributes();
//     network.setEdgeAttributes(newEdgeAttributes);
//     assertTrue( network.getEdgeAttributes() == newEdgeAttributes );
//     ExpressionData expData = new ExpressionData("testData/gal1.22x5.mRNA");
//     network.setExpressionData(expData);
//     assertTrue( network.getExpressionData() == expData );
    
//     CyNetwork gpNetwork = new CyNetwork(newPerspective, nodeAttributes,
//                                         edgeAttributes, startData);
//     assertTrue( gpNetwork.getRootGraph() == rootGraph );
//     assertTrue( gpNetwork.getGraphPerspective() == newPerspective );
//     assertTrue( gpNetwork.getNodeAttributes() == nodeAttributes );
//     assertTrue( gpNetwork.getEdgeAttributes() == edgeAttributes );
//     assertTrue( gpNetwork.getExpressionData() == startData );
//     assertTrue( gpNetwork.getNeedsLayout() == false );
    
//     CyNetwork gpNetwork2 = new CyNetwork(newPerspective, nodeAttributes,
//                                          edgeAttributes);
//     assertTrue( gpNetwork2.getRootGraph() == rootGraph );
//     assertTrue( gpNetwork2.getGraphPerspective() == newPerspective );
//     assertTrue( gpNetwork2.getNodeAttributes() == nodeAttributes );
//     assertTrue( gpNetwork2.getEdgeAttributes() == edgeAttributes );
//     assertTrue( gpNetwork2.getExpressionData() == null );
//     assertTrue( gpNetwork2.getNeedsLayout() == false );
    
//     CyNetwork noExpNetwork = new CyNetwork(rootGraph, nodeAttributes, edgeAttributes);
//     assertTrue( noExpNetwork.getRootGraph() == rootGraph );
//     assertTrue( noExpNetwork.getGraphPerspective() != null );
//     assertTrue( noExpNetwork.getGraphPerspective().getRootGraph() == rootGraph );
//     assertTrue( noExpNetwork.getNodeAttributes() == nodeAttributes );
//     assertTrue( noExpNetwork.getEdgeAttributes() == edgeAttributes );
//     assertTrue( noExpNetwork.getExpressionData() == null );
//     assertTrue( noExpNetwork.getNeedsLayout() == false );
//     //test setting new graph, removing old attributes
//     RootGraph newGraph = GinyFactory.createRootGraph();
//     CyNetwork newNetwork = new CyNetwork(newGraph, newNodeAttributes, newEdgeAttributes);
//     newNetwork.setNeedsLayout(true);
//     noExpNetwork.setNewGraphFrom(newNetwork, true);
//     assertTrue( noExpNetwork.getRootGraph() == newGraph );
//     assertTrue( noExpNetwork.getGraphPerspective() != null );
//     assertTrue( noExpNetwork.getGraphPerspective().getRootGraph() == newGraph );
//     assertTrue( noExpNetwork.getNodeAttributes() == newNodeAttributes );
//     assertTrue( noExpNetwork.getEdgeAttributes() == newEdgeAttributes );
//     assertTrue( noExpNetwork.getNeedsLayout() == true );
    
//     CyNetwork nullNetwork = new CyNetwork((RootGraph)null, null, null, null);
//     assertTrue(nullNetwork.getRootGraph() != null);
//     assertTrue(nullNetwork.getGraphPerspective() != null);
//     assertTrue(nullNetwork.getNodeAttributes() != null);
//     assertTrue(nullNetwork.getEdgeAttributes() != null);
//     assertTrue(nullNetwork.getExpressionData() == null);
//     //test setting new graph, preserving old attributes
//     //first install some attributes so we can do the test
//     GraphObjAttributes nullNodeAttributes = nullNetwork.getNodeAttributes();
//     GraphObjAttributes nullEdgeAttributes = nullNetwork.getEdgeAttributes();
//     Node n1 = rootGraph.getNode( rootGraph.createNode() );
//     nullNodeAttributes.addNameMapping("YGR074W", n1);
//     Node n2 = newGraph.getNode( newGraph.createNode() );
//     newNodeAttributes.addNameMapping("YBR043C", n2);
//     Node n3 = rootGraph.getNode( rootGraph.createNode() );
//     Node n4 = newGraph.getNode( newGraph.createNode() );
//     Edge e1 = rootGraph.getEdge( rootGraph.createEdge(n1, n3) );
//     nullEdgeAttributes.addNameMapping("YDR277C (pp) YDL124W", e1);
//     Edge e2 = newGraph.getEdge( newGraph.createEdge(n2, n4) );
//     newEdgeAttributes.addNameMapping("YBL026W (pp) YOR127C", e2);
//     nullNodeAttributes.readAttributesFromFile("testData/galFiltered.nodeAttrs1");
//     nullEdgeAttributes.readAttributesFromFile("testData/galFiltered.edgeAttrs1");
//     newNodeAttributes.readAttributesFromFile("testData/galFiltered.nodeAttrs2");
//     newEdgeAttributes.readAttributesFromFile("testData/galFiltered.edgeAttrs2");
//     nullNetwork.setNeedsLayout(true);
//     newNetwork.setNeedsLayout(false);
//     //now do the test
//     nullNetwork.setNewGraphFrom(newNetwork, false);
//     assertTrue( nullNetwork.getRootGraph() == newGraph );
//     assertTrue( nullNetwork.getNeedsLayout() == false );
//     assertTrue( nullNetwork.getNodeAttributes() == nullNodeAttributes );
//     assertTrue( nullNetwork.getEdgeAttributes() == nullEdgeAttributes );
//     assertTrue( nullNodeAttributes.hasAttribute("TestNodeAttribute1") );
//     assertTrue( nullNodeAttributes.hasAttribute("TestNodeAttribute2") );
//     assertTrue( nullEdgeAttributes.hasAttribute("TestEdgeAttributes1") );
//     assertTrue( nullEdgeAttributes.hasAttribute("TestEdgeAttributes2") );
//     assertTrue( nullNodeAttributes.getCanonicalName(n1).equals("YGR074W") );
//     assertTrue( nullNodeAttributes.getCanonicalName(n2).equals("YBR043C") );
//     assertTrue( nullEdgeAttributes.getCanonicalName(e1).equals("YDR277C (pp) YDL124W") );
//     assertTrue( nullEdgeAttributes.getCanonicalName(e2).equals("YBL026W (pp) YOR127C") );
//   }
  //-------------------------------------------------------------------------
  public void testListeners() throws Exception {}// {
//     String callerID = "CyNetworkTest.testListeners";
//     CyNetwork network = new CyNetwork();
//     network.addCyNetworkListener(listener);
    
//     network.beginActivity(callerID); //this should fire a begin event
//     assertTrue(network.isStateClear() == false);
//     assertTrue(currentEvent != null);
//     assertTrue(currentEvent.getNetwork() == network);
//     assertTrue(currentEvent.getType() == CyNetworkEvent.BEGIN);
//     currentEvent = null;
//     network.beginActivity(callerID); //this shouldn't fire an event
//     assertTrue(network.isStateClear() == false);
//     assertTrue(currentEvent == null);
//     network.endActivity(callerID);  //this shouldn't fire an event
//     assertTrue(network.isStateClear() == false);
//     assertTrue(currentEvent == null);
//     network.endActivity(callerID);  //this should fire a matching end event
//     assertTrue(network.isStateClear() == true);
//     assertTrue(currentEvent != null);
//     assertTrue(currentEvent.getNetwork() == network);
//     assertTrue(currentEvent.getType() == CyNetworkEvent.END);
//     currentEvent = null;
//     network.endActivity(callerID); //this unmatched call should do nothing
//     assertTrue(network.isStateClear() == true);
//     assertTrue(currentEvent == null);
//     network.forceClear(callerID); //again should do nothing
//     assertTrue(network.isStateClear() == true);
//     assertTrue(currentEvent == null);
    
//     network.beginActivity(callerID); //should fire a begin event
//     assertTrue(network.isStateClear() == false);
//     assertTrue(currentEvent != null);
//     assertTrue(currentEvent.getNetwork() == network);
//     assertTrue(currentEvent.getType() == CyNetworkEvent.BEGIN);
//     currentEvent = null;
//     network.forceClear(callerID); //should force an end event
//     assertTrue(network.isStateClear() == true);
//     assertTrue(currentEvent != null);
//     assertTrue(currentEvent.getNetwork() == network);
//     assertTrue(currentEvent.getType() == CyNetworkEvent.END);
    
//     Set allListeners = network.getCyNetworkListeners();
//     assertTrue(allListeners.size() == 1);
//     assertTrue(allListeners.iterator().next() == listener);
//     boolean wasThere = network.removeCyNetworkListener(listener);
//     assertTrue(wasThere == true);
//     assertTrue(network.getCyNetworkListeners().size() == 0);
//     boolean stillThere = network.removeCyNetworkListener(listener);
//     assertTrue(stillThere == false);
//   }
  //-------------------------------------------------------------------------
  public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyNetworkTest.class));
  }
  //-------------------------------------------------------------------------
}

