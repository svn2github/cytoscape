// SelectedSubGraphFactoryTest.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.HashMap;

import cytoscape.SelectedSubGraphFactory;
import cytoscape.GraphObjAttributes;

import y.base.*;
import y.view.*;
//------------------------------------------------------------------------------
public class SelectedSubGraphFactoryTest extends TestCase {


//------------------------------------------------------------------------------
public SelectedSubGraphFactoryTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
/**
 *  create empty graph, node & edge attributes, select nothing, ask the
 *  factory for subgraph with attributes.  all should be zero in size.
 */
public void testCtor () throws Exception
{ 
  System.out.println ("testCtor");
  Graph2D graph = new Graph2D ();
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  SelectedSubGraphFactory factory = 
      new SelectedSubGraphFactory (graph, nodeAttributes, edgeAttributes);

  GraphObjAttributes newNodeAttributes = factory.getNodeAttributes ();
  GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes ();
  Graph2D subGraph = factory.getSubGraph ();
  Node [] nodes = subGraph.getNodeArray ();
  Edge [] edges = subGraph.getEdgeArray ();

  assert nodes.length == 0;
  assert edges.length == 0;
  assert newNodeAttributes.size () == 0;
  assert newEdgeAttributes.size () == 0;

} // testCtor
//-------------------------------------------------------------------------
/**
 *  this helper method creates an edge on a graph, assigns the fundamental
 * 'interaction'  attribute (i.e., pp, pd, cogPartner, homology...), and updates the
 *  edgeAttributes name map.
 */
private void  createEdgeAndAttribute (String nodeName1, String nodeName2, 
                                      String interactionValue, 
                                      GraphObjAttributes edgeAttributes,
                                      HashMap nameToNodeMap,
                                      Graph2D graph)
{
  Node source = (Node) nameToNodeMap.get (nodeName1);
  Node target = (Node) nameToNodeMap.get (nodeName2);
  assert source != null;
  assert target != null;
  Edge edge = graph.createEdge (source, target);

  String edgeName = nodeName1 + " (" + interactionValue + ") " + nodeName2;
  edgeAttributes.add ("interaction", edgeName, interactionValue);
  edgeAttributes.addNameMapping (edgeName, edge);

} // createEdgeAndAttribute
//-------------------------------------------------------------------------
/**
 *  this involved test emulates the standard operation of the SelectedSubGraphFactory
 *  class, albeit with a small graph.  the strategy:
 *  <ol>
 *     <li>
 *     <li>
 *     <li>
 *     <li>
 *     <li>
 *  </ol>
 */
public void testSubGraphOperation () throws Exception
{ 
  System.out.println ("testSubGraphOperation");

  String [] canonicalNames = {"g0", 
                              "g1",
                              "g2",
                              "g3",
                              "g4",
                              "g5",
                              "g6"};

  String [] commonNames =    {"argH",
                              "acc",
                              "mmdA",
                              "vng0623g",
                              "gatA",
                              "gatB2",
                              "gltS"};

  assert canonicalNames.length == commonNames.length;

  Double [] expression =    {new Double (0.39), 
                             new Double (-0.144),
                             null,
                             null,
                             new Double (-0.05),
                             null, 
                             null};

  assert canonicalNames.length == expression.length;

  HashMap nameToNodeMap = new HashMap ();
  Node [] allNodes = new Node [canonicalNames.length];

    //-------------------------------
    // create all the nodes
    //-------------------------------

  Graph2D graph = new Graph2D ();
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();

  for (int i=0; i < canonicalNames.length; i++) {
    Node node = graph.createNode (0.0, 0.0, 70.0, 30.0, canonicalNames [i]);
    nameToNodeMap.put (canonicalNames [i], node);
    nodeAttributes.addNameMapping (canonicalNames [i], node);
    allNodes [i] = node;
    }


    //-------------------------------
    // add the node attributes
    //-------------------------------

  for (int i=0; i < canonicalNames.length; i++) {
    if (commonNames [i] != null) {
      nodeAttributes.add ("commonName", canonicalNames [i], commonNames [i]);
      nodeAttributes.add ("expression", canonicalNames [i], expression [i]);
      } // if !null
    } // for i

    //----------------------------------------------------------------
    // create the edges, and simultaneously add the edge attributes
    //----------------------------------------------------------------

  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  
  String a, b, interactionValue;
  a = "g5";
  b = "g4";
  interactionValue = "phylogeneticPattern";
  createEdgeAndAttribute (a, b, interactionValue, edgeAttributes, nameToNodeMap, graph);

  a = "g5";
  b = "g6";
  interactionValue = "geneFusion";
  createEdgeAndAttribute (a, b, interactionValue, edgeAttributes, nameToNodeMap, graph);

  a = "g3";
  b = "g1";
  interactionValue = "geneFusion";
  createEdgeAndAttribute (a, b, interactionValue, edgeAttributes, nameToNodeMap, graph);

  a = "g3";
  b = "g1";
  interactionValue = "phylogeneticPattern";
  createEdgeAndAttribute (a, b, interactionValue, edgeAttributes, nameToNodeMap, graph);

  a = "g4";
  b = "g1";
  interactionValue = "geneFusion";
  createEdgeAndAttribute (a, b, interactionValue, edgeAttributes, nameToNodeMap, graph);

  a = "g1";
  b = "g0";
  interactionValue = "geneFusion";
  createEdgeAndAttribute (a, b, interactionValue, edgeAttributes, nameToNodeMap, graph);


    //----------------------------------------------------------------
    // select 3 nodes directly connected to each other
    //----------------------------------------------------------------

  NodeRealizer realizer;

  Node selectedNode;

  String [] selectedNodeNames        = {canonicalNames [1], canonicalNames [3], canonicalNames [4]};
  String [] expectedCommonNames      = {   commonNames [1],    commonNames [3],    commonNames [4]};
  Double [] expectedExpressionValues = {    expression [1],     expression [3],     expression [4]};

  for (int i=0; i < selectedNodeNames.length; i++) {
    selectedNode = (Node) nameToNodeMap.get (selectedNodeNames [i]);
    realizer = graph.getRealizer (selectedNode);
    realizer.setSelected (true);
    }

  // System.out.println ("--------- edgeAttributes of original" + edgeAttributes);
  SelectedSubGraphFactory factory = 
      new SelectedSubGraphFactory (graph, nodeAttributes, edgeAttributes);

  GraphObjAttributes newNodeAttributes = factory.getNodeAttributes ();
  GraphObjAttributes newEdgeAttributes = factory.getEdgeAttributes ();
  Graph2D subGraph = factory.getSubGraph ();
  Node [] nodes = subGraph.getNodeArray ();
  Edge [] edges = subGraph.getEdgeArray ();

  assert nodes.length == 3;
  assert edges.length == 3;
  assert newNodeAttributes.size () == 3;  // commonName, canonicalName, expression
  assert newEdgeAttributes.size () == 2;  // interaction, canonicalName

  for (int i=0; i < selectedNodeNames.length; i++) {
    assert expectedCommonNames [i] == 
                (String) nodeAttributes.getValue ("commonName", selectedNodeNames [i]);
    assert expectedExpressionValues[i] == 
      (Double) nodeAttributes.getValue ("expression", selectedNodeNames [i]);
    } // for i

  String []    selectedEdgeNames  = {"g3 (geneFusion) g1", 
                                     "g3 (phylogeneticPattern) g1", 
                                     "g4 (geneFusion) g1"};

  String [] edgeInteractionValues = {"geneFusion",
                                     "phylogeneticPattern",
                                     "geneFusion"};        

  for (int i=0; i < selectedEdgeNames.length; i++) {
    String interactionType = (String) edgeAttributes.getValue ("interaction", selectedEdgeNames [i]);
    assert interactionType.equals (edgeInteractionValues [i]);
    }
    
} // testSubGraphOperation
//-------------------------------------------------------------------------
/**
 *  create a simple 3 node graph, with two edges.  select two 
 *  neighboring nodes, and generate the subgraph.  only -one- edge
 *  should come back with the two nodes
 */
public void testForSurplusEdges () throws Exception
{ 
  System.out.println ("testNoSurplusEdges");

  Graph2D graph = new Graph2D ();

  Node nodeA = graph.createNode (0.0, 0.0, 70.0, 30.0, "A");
  Node nodeB = graph.createNode (0.0, 0.0, 70.0, 30.0, "B");
  Node nodeC = graph.createNode (0.0, 0.0, 70.0, 30.0, "C");

  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  nodeAttributes.addNameMapping ("A", nodeA);
  nodeAttributes.addNameMapping ("B", nodeB);
  nodeAttributes.addNameMapping ("C", nodeC);


  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  Edge edge = graph.createEdge (nodeA, nodeB);
  String edgeName = "A (test) B";
  edgeAttributes.add ("interaction", edgeName, "test");
  edgeAttributes.addNameMapping (edgeName, edge);

  NodeRealizer realizer = graph.getRealizer (nodeA);
  realizer.setSelected (true);
  realizer = graph.getRealizer (nodeB);
  realizer.setSelected (true);


  SelectedSubGraphFactory factory = 
      new SelectedSubGraphFactory (graph, nodeAttributes, edgeAttributes);

  Graph2D subGraph = factory.getSubGraph ();
  Node [] nodes = subGraph.getNodeArray ();
  Edge [] edges = subGraph.getEdgeArray ();

  assert nodes.length == 2;
  assert edges.length == 1;

} // testNoSurplusEdges
//-------------------------------------------------------------------------
/**
 * Creates a simple graph with no node labels, and makes sure that the
 * resulting graph has edges.
 */
public void testGraphWithoutLabels () throws Exception
{
  System.out.println ("testGraphWithoutLabels");

  Graph2D graph = new Graph2D ();
  
  //Create nodes with no labels
  Node nodeA = graph.createNode ();
  Node nodeB = graph.createNode ();
  Node nodeC = graph.createNode ();
  Node nodeD = graph.createNode ();
  
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  nodeAttributes.addNameMapping ("A", nodeA);
  nodeAttributes.addNameMapping ("B", nodeB);
  nodeAttributes.addNameMapping ("C", nodeC);
  nodeAttributes.addNameMapping ("D", nodeD);

  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  // (A,B)
  Edge edge = graph.createEdge (nodeA, nodeB);
  String edgeName = "A (test) B";
  edgeAttributes.add ("interaction", edgeName, "test");
  edgeAttributes.addNameMapping (edgeName, edge);
  // (A,C)
  edge = graph.createEdge (nodeA, nodeC);
  edgeName = "A (test) C";
  edgeAttributes.add ("interaction", edgeName, "test");
  edgeAttributes.addNameMapping (edgeName, edge);
  // (A,D)
  edge = graph.createEdge (nodeA, nodeD);
  edgeName = "A (test) D";
  edgeAttributes.add ("interaction", edgeName, "test");
  edgeAttributes.addNameMapping (edgeName, edge);
  // (B,D)
  edge = graph.createEdge (nodeB, nodeD);
  edgeName = "B (test) D";
  edgeAttributes.add ("interaction", edgeName, "test");
  edgeAttributes.addNameMapping (edgeName, edge);
  // (C,D)
  edge = graph.createEdge (nodeC, nodeD);
  edgeName = "C (test) D";
  edgeAttributes.add ("interaction", edgeName, "test");
  edgeAttributes.addNameMapping (edgeName, edge);

  // Select nodes A,B, and C
  NodeRealizer realizer = graph.getRealizer (nodeA);
  realizer.setSelected (true);
  realizer = graph.getRealizer (nodeB);
  realizer.setSelected (true);
  realizer = graph.getRealizer (nodeC);
  realizer.setSelected (true);
  

  SelectedSubGraphFactory factory = 
      new SelectedSubGraphFactory (graph, nodeAttributes, edgeAttributes);

  Graph2D subGraph = factory.getSubGraph ();
  Node [] nodes = subGraph.getNodeArray ();
  Edge [] edges = subGraph.getEdgeArray ();

  assert nodes.length == 3;
  assert edges.length == 2;

}//testGraphWithoutLabels
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (SelectedSubGraphFactoryTest.class));
}
//------------------------------------------------------------------------------
} // SelectedSubGraphFactoryTest


