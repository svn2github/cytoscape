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
import cytoscape.data.Semantics;
//-----------------------------------------------------------------------------------------
public class CyNetworkFactoryTest extends TestCase {
//------------------------------------------------------------------------------
public CyNetworkFactoryTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testBadFiles() throws Exception {
    //none of these should throw exceptions, but all should return null
    CyNetwork nullInt = CyNetworkFactory.createNetworkFromInteractionsFile(null);
    assertTrue( nullInt == null );
    System.err.println("CyNetworkFactory.testBadFiles:" +
        "Trying to load missing interactions file, expect FileNotFoundException");
    CyNetwork noInt = CyNetworkFactory.createNetworkFromInteractionsFile("notThere");
    assertTrue( nullInt == null );
    //note that almost any file can be parsed as an interactions file, thus
    //testing for parse errors isn't needed
    
    CyNetwork nullGML = CyNetworkFactory.createNetworkFromGMLFile(null);
    assertTrue( nullGML == null );
    System.err.println("CyNetworkFactory.testBadFiles:" +
        "Trying to load missing GML file, expect FileNotFoundException");
    CyNetwork noGML = CyNetworkFactory.createNetworkFromGMLFile("notThere");
    assertTrue( noGML == null );
    System.err.println("CyNetworkFactory.testBadFiles:" +
        "Trying to read interactions file as GML, expect ParseException");
    String sifFile = "testData/galFiltered.sif";
    CyNetwork sifGML = CyNetworkFactory.createNetworkFromGMLFile(sifFile);
    assertTrue( sifGML == null );
}
//------------------------------------------------------------------------------
public void testCreateNetworkFromInteractionsFile() throws Exception {
    String filename = "testData/galFiltered.sif";
    CyNetwork network = CyNetworkFactory.createNetworkFromInteractionsFile(filename);
    verifyNetworkBasics(network);
    verifyInteractionAttribute(network);
    assertTrue( network.getNeedsLayout() == true );
}
//------------------------------------------------------------------------------
public void testCreateNetworkFromInteractionsFile2() throws Exception {
    String filename = "testData/galFiltered.sif";
    CyNetwork network =
        CyNetworkFactory.createNetworkFromInteractionsFile(filename, false, null, null);
    verifyNetworkBasics(network);
    verifyInteractionAttribute(network);
    assertTrue( network.getNeedsLayout() == true );
}
//------------------------------------------------------------------------------
public void testCreateNetworkFromGMLFile() throws Exception {
    String filename = "testData/galFiltered.gml";
    CyNetwork network = CyNetworkFactory.createNetworkFromGMLFile(filename);
    verifyNetworkBasics(network);
    assertTrue( network.getNeedsLayout() == false );
}
//------------------------------------------------------------------------------
public void testLoadAttributes() throws Exception {
    String filename = "testData/galFiltered.gml";
    CyNetwork network = CyNetworkFactory.createNetworkFromGMLFile(filename);
    verifyNetworkBasics(network);
    String[] nodeAttrLocations = { "testData/galFiltered.nodeAttrs1",
                                   "testData/galFiltered.nodeAttrs2" };
    String[] edgeAttrLocations = { "testData/galFiltered.edgeAttrs1",
                                  "testData/galFiltered.edgeAttrs2" };
    CyNetworkFactory.loadAttributes(network, nodeAttrLocations, edgeAttrLocations);
    verifyAttributes(network);
}
//------------------------------------------------------------------------------
public void testLoadAttributes2() throws Exception {
    String filename = "testData/galFiltered.gml";
    CyNetwork network = CyNetworkFactory.createNetworkFromGMLFile(filename);
    verifyNetworkBasics(network);
    String[] nodeAttrLocations = { "testData/galFiltered.nodeAttrs1",
                                   "testData/galFiltered.nodeAttrs2" };
    String[] edgeAttrLocations = { "testData/galFiltered.edgeAttrs1",
                                  "testData/galFiltered.edgeAttrs2" };
    CyNetworkFactory.loadAttributes(network, nodeAttrLocations, edgeAttrLocations,
                                    false, null, null);
    verifyAttributes(network);
}
//------------------------------------------------------------------------------
public void testCreateNetworkFromProject() throws Exception {
    CyProject project = new CyProject("testData/networkProject.pro");
    CyNetwork network = CyNetworkFactory.createNetworkFromProject(project, null);
    verifyNetworkBasics(network);
    verifyAttributes(network);
    
    assertTrue( network.getExpressionData() != null );
    assertTrue( network.getExpressionData().getNumberOfGenes() == 274 );
}
//-------------------------------------------------------------------------
/**
 * This method performs basic tests on the network, assuming it represents the
 * galFiltered network.
 */
private void verifyNetworkBasics(CyNetwork network) {
    assertTrue( network != null );
    assertTrue( network.getGraph() != null );
    assertTrue( network.getGraph().nodeCount() == 331 );
    assertTrue( network.getGraph().edgeCount() == 362 );
    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
    Node[] allNodes = network.getGraph().getNodeArray();
    for (int i=0; i<allNodes.length; i++) {
        assertTrue( nodeAttributes.getCanonicalName(allNodes[i]) != null );
    }
    GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
    Edge[] allEdges = network.getGraph().getEdgeArray();
    for (int i=0; i<allEdges.length; i++) {
        assertTrue( edgeAttributes.getCanonicalName(allEdges[i]) != null );
    }
}
//------------------------------------------------------------------------------
/**
 * This method tests for the interaction attribute in networks read from
 * galFiltered.sif.
 */
private void verifyInteractionAttribute(CyNetwork network) {
    assertTrue( network != null );
    GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
    assertTrue( edgeAttributes != null );
    assertTrue( edgeAttributes.hasAttribute(Semantics.INTERACTION) );
    Edge[] allEdges = network.getGraph().getEdgeArray();
    assertTrue( allEdges != null );
    for (int i=0; i<allEdges.length; i++) {
        String canonicalName = edgeAttributes.getCanonicalName(allEdges[i]);
        String type = edgeAttributes.getStringValue(Semantics.INTERACTION, canonicalName);
        assertTrue(type != null);
    }
    String oneEdge = "YBL026W (pp) YOR167C";
    String oneType = edgeAttributes.getStringValue(Semantics.INTERACTION, oneEdge);
    assertTrue( oneType != null);
    assertTrue( oneType.equals("pp") );
}
//------------------------------------------------------------------------------
/**
 * This method tests for the data attributes read from the galFiltered
 * sample attribute files.
 */
private void verifyAttributes(CyNetwork network) {
    assertTrue( network != null );
    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
    assertTrue( nodeAttributes != null );
    assertTrue( nodeAttributes.hasAttribute("TestNodeAttribute1") );
    assertTrue( nodeAttributes.getValue("TestNodeAttribute1",
                                        "YBR043C").equals(new Integer(3)) );
    assertTrue( nodeAttributes.hasAttribute("TestNodeAttribute2") );
    assertTrue( nodeAttributes.getValue("TestNodeAttribute2",
                                        "YBR043C").equals(new Integer(6)) );
                                        
    GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
    assertTrue( edgeAttributes != null );
    assertTrue( edgeAttributes.hasAttribute("TestEdgeAttributes1") );
    assertTrue( edgeAttributes.getValue("TestEdgeAttributes1",
                                        "YBL026W (pp) YOR167C").equals(new Integer(1)) );
    assertTrue( edgeAttributes.hasAttribute("TestEdgeAttributes2") );
    assertTrue( edgeAttributes.getValue("TestEdgeAttributes2",
                                        "YBL026W (pp) YOR127C").equals(new Integer(3)) );
}
//------------------------------------------------------------------------------
public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyNetworkFactoryTest.class));
}
//-------------------------------------------------------------------------
}

