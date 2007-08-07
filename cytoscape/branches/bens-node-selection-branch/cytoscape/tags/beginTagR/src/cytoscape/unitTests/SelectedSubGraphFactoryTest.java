// SelectedSubGraphFactoryTest.java
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
import y.view.Graph2D;
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

  assertTrue (nodes.length == 0);
  assertTrue (edges.length == 0);
  assertTrue (newNodeAttributes.size () == 0);
  assertTrue (newEdgeAttributes.size () == 0);

} // testAllArgs
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (SelectedSubGraphFactoryTest.class));
}
//------------------------------------------------------------------------------
} // SelectedSubGraphFactoryTest
