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

import y.base.Node;
import y.base.Edge;

import cytoscape.CyProject;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetworkFactory;
import cytoscape.data.CyNetwork;
import cytoscape.data.ExpressionData;
//-----------------------------------------------------------------------------------------
public class CyNetworkFactoryTest extends TestCase {
//------------------------------------------------------------------------------
public CyNetworkFactoryTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testCreateNetworkFromProject() throws Exception {
    CyProject project = new CyProject("testData/networkProject.pro");
    CyNetwork network = CyNetworkFactory.createNetworkFromProject(project, null);
    
    assertTrue( network.getGraph() != null );
    assertTrue( network.getGraph().nodeCount() == 331 );
    assertTrue( network.getGraph().edgeCount() == 362 );
    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
    Node[] allNodes = network.getGraph().getNodeArray();
    for (int i=0; i<allNodes.length; i++) {
        assertTrue( nodeAttributes.getCanonicalName(allNodes[i]) != null );
    }
    assertTrue( nodeAttributes.hasAttribute("TestNodeAttribute1") );
    assertTrue( nodeAttributes.getValue("TestNodeAttribute1",
                                        "YBR043C").equals(new Integer(3)) );
    assertTrue( nodeAttributes.hasAttribute("TestNodeAttribute2") );
    assertTrue( nodeAttributes.getValue("TestNodeAttribute2",
                                        "YBR043C").equals(new Integer(6)) );
    GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
    Edge[] allEdges = network.getGraph().getEdgeArray();
    for (int i=0; i<allEdges.length; i++) {
        assertTrue( edgeAttributes.getCanonicalName(allEdges[i]) != null );
    }
    assertTrue( edgeAttributes.hasAttribute("TestEdgeAttributes1") );
    assertTrue( edgeAttributes.getValue("TestEdgeAttributes1",
                                        "YBL026W (pp) YOR167C").equals(new Integer(1)) );
    assertTrue( edgeAttributes.hasAttribute("TestEdgeAttributes2") );
    assertTrue( edgeAttributes.getValue("TestEdgeAttributes2",
                                        "YBL026W (pp) YOR127C").equals(new Integer(3)) );
    
    assertTrue( network.getExpressionData() != null );
    assertTrue( network.getExpressionData().getNumberOfGenes() == 274 );
}
//-------------------------------------------------------------------------
public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyNetworkFactoryTest.class));
}
//-------------------------------------------------------------------------
}

