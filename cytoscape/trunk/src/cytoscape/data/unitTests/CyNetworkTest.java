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
import cytoscape.data.ExpressionData;
//-----------------------------------------------------------------------------------------
public class CyNetworkTest extends TestCase {
//------------------------------------------------------------------------------
public CyNetworkTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testBasic() throws Exception { 
    System.out.println ("testBasic");
    
    Graph2D graph = new Graph2D();
    GraphObjAttributes nodeAttributes = new GraphObjAttributes();
    GraphObjAttributes edgeAttributes = new GraphObjAttributes();
    CyNetwork network = new CyNetwork(graph, nodeAttributes,
                                      edgeAttributes, null);
    
    assertTrue( network.getGraph() == graph );
    assertTrue( network.getNodeAttributes() == nodeAttributes );
    assertTrue( network.getEdgeAttributes() == edgeAttributes );
    assertTrue( network.getExpressionData() == null );
    
    Graph2D newGraph = new Graph2D();
    network.setGraph(newGraph);
    assertTrue( network.getGraph() == newGraph );
    GraphObjAttributes newNodeAttributes = new GraphObjAttributes();
    network.setNodeAttributes(newNodeAttributes);
    assertTrue( network.getNodeAttributes() == newNodeAttributes );
    GraphObjAttributes newEdgeAttributes = new GraphObjAttributes();
    network.setEdgeAttributes(newEdgeAttributes);
    assertTrue( network.getEdgeAttributes() == newEdgeAttributes );
    ExpressionData expData = new ExpressionData("../../testData/gal1.22x5.mRNA");
    network.setExpressionData(expData);
    assertTrue( network.getExpressionData() == expData );
    
}
//-------------------------------------------------------------------------
public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyNetworkTest.class));
}
//-------------------------------------------------------------------------
}

