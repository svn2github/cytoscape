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

import cytoscape.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetworkFactory;
import cytoscape.data.ExpressionData;
import cytoscape.data.Semantics;

public class CyNetworkFactoryTest extends TestCase {

  public CyNetworkFactoryTest(String name) {super(name);}

  public void setUp() throws Exception {}

  public void tearDown() throws Exception {}

  public void testCreateEmptyNetwork() throws Exception {
    CyNetwork network = Cytoscape.createNetwork();
    assertTrue( network != null );
    assertTrue( network.getNodeCount() == 0 );
    assertTrue( network.getEdgeCount() == 0 );
    assertTrue( network.getExpressionData() == null );
  }
    
  public void testCreateNetworkFromInteractionsFile() throws Exception {
    Cytoscape.clearCytoscape();
    String filename = "testData/galFiltered.sif";
    CyNetwork network = Cytoscape.createNetwork( filename );
    verifyNetworkBasics(network);
    verifyInteractionAttribute(network);
  }
    
  public void testCreateNetworkFromInteractionsFile2() throws Exception {
    Cytoscape.clearCytoscape();
    String filename = "testData/galFiltered.sif";
    CyNetwork network = Cytoscape.createNetwork(filename, Cytoscape.FILE_SIF, false, null, null);
    verifyNetworkBasics(network);
    verifyInteractionAttribute(network);
  }
    
  public void testCreateNetworkFromGMLFile() throws Exception {
    Cytoscape.clearCytoscape();
    String filename = "testData/galFiltered.gml";
    CyNetwork network = Cytoscape.createNetwork(filename);
    verifyNetworkBasics(network);
  }
  
  public void testLoadAttributes() throws Exception {
    Cytoscape.clearCytoscape();
    String filename = "testData/galFiltered.gml";
    CyNetwork network = Cytoscape.createNetwork(filename);
    verifyNetworkBasics(network);
    String[] nodeAttrLocations = { "testData/galFiltered.nodeAttrs1",
                                   "testData/galFiltered.nodeAttrs2" };
    String[] edgeAttrLocations = { "testData/galFiltered.edgeAttrs1",
                                   "testData/galFiltered.edgeAttrs2" };
    Cytoscape.loadAttributes( nodeAttrLocations, edgeAttrLocations);
    verifyAttributes(network);
  }

  public void testLoadAttributes2() throws Exception {
    Cytoscape.clearCytoscape();
    String filename = "testData/galFiltered.gml";
    CyNetwork network = Cytoscape.createNetwork(filename);
    verifyNetworkBasics(network);
    String[] nodeAttrLocations = { "testData/galFiltered.nodeAttrs1",
                                   "testData/galFiltered.nodeAttrs2" };
    String[] edgeAttrLocations = { "testData/galFiltered.edgeAttrs1",
                                   "testData/galFiltered.edgeAttrs2" };
    Cytoscape.loadAttributes( nodeAttrLocations, 
                              edgeAttrLocations,
                              false, 
                              null, 
                              null);
    verifyAttributes(network);
  }
 
  public void testCreateNetworkFromProject() throws Exception {
    Cytoscape.clearCytoscape();
    CyProject project = new CyProject("testData/networkProject.pro");
    CyNetwork network = Cytoscape.createNetworkFromProject(project, null);
    verifyNetworkBasics(network);
    verifyAttributes(network);
	
    assertTrue( network.getExpressionData() != null );
    assertTrue( network.getExpressionData().getNumberOfGenes() == 274 );
  }
 
  /**
   * This method performs basic tests on the network, assuming it represents the
   * galFiltered network.
   */
  private void verifyNetworkBasics ( CyNetwork network ) {
    assertTrue( network != null );

    assertTrue( network.getNodeCount() == 331 );
    assertTrue( network.getEdgeCount() == 362 );
    
    for (Iterator i = network.nodesIterator(); i.hasNext(); ) {
      Node node = (Node)i.next();
	    assertTrue( Cytoscape.getNodeNetworkData().getCanonicalName(node) != null );
    }

    for (Iterator i = network.edgesList().iterator(); i.hasNext(); ) {
      Edge edge = (Edge)i.next();
	    assertTrue( Cytoscape.getEdgeNetworkData().getCanonicalName(edge) != null );
    }
  }

  /**
   * This method tests for the interaction attribute in networks read from
   * galFiltered.sif.
   */
  private void verifyInteractionAttribute(CyNetwork network) {
    assertTrue( network != null );
    
    assertTrue( Cytoscape.getEdgeNetworkData().hasAttribute(Semantics.INTERACTION) );
    for (Iterator i = network.edgesList().iterator(); i.hasNext(); ) {
      Edge edge = (Edge)i.next();
	    String canonicalName = Cytoscape.getEdgeNetworkData().getCanonicalName(edge);
	    String type = Cytoscape.getEdgeNetworkData().getStringValue(Semantics.INTERACTION, canonicalName);
	    assertTrue(type != null);
    }
    String oneEdge = "YBL026W (pp) YOR167C";
    String oneType = Cytoscape.getEdgeNetworkData().getStringValue(Semantics.INTERACTION, oneEdge);
    assertTrue( oneType != null);
    assertTrue( oneType.equals("pp") );
  }
 
  /**
   * This method tests for the data attributes read from the galFiltered
   * sample attribute files.
   */
  private void verifyAttributes(CyNetwork network) {
    assertTrue( network != null );
    
    assertTrue( Cytoscape.getNodeNetworkData() != null );
    assertTrue( Cytoscape.getNodeNetworkData().hasAttribute("TestNodeAttribute1") );
    assertTrue( Cytoscape.getNodeNetworkData().getValue("TestNodeAttribute1",
                                        "YBR043C").equals(new Integer(3)) );
    assertTrue( Cytoscape.getNodeNetworkData().hasAttribute("TestNodeAttribute2") );
    assertTrue( Cytoscape.getNodeNetworkData().getValue("TestNodeAttribute2",
                                        "YBR043C").equals(new Integer(6)) );
	

    assertTrue( Cytoscape.getEdgeNetworkData() != null );
    assertTrue( Cytoscape.getEdgeNetworkData().hasAttribute("TestEdgeAttributes1") );
    assertTrue( Cytoscape.getEdgeNetworkData().getValue("TestEdgeAttributes1",
                                        "YBL026W (pp) YOR167C").equals(new Integer(1)) );
    assertTrue( Cytoscape.getEdgeNetworkData().hasAttribute("TestEdgeAttributes2") );
    assertTrue( Cytoscape.getEdgeNetworkData().getValue("TestEdgeAttributes2",
                                        "YBL026W (pp) YOR127C").equals(new Integer(3)) );
  }
  //------------------------------------------------------------------------------
  public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyNetworkFactoryTest.class));
  }
  //-------------------------------------------------------------------------
}

