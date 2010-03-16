// NodeVizMapperTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.vizmap.NodeVizMapper;

import java.awt.Color;
import java.util.*;
//------------------------------------------------------------------------------
public class NodeVizMapperTest extends TestCase {


//------------------------------------------------------------------------------
public NodeVizMapperTest (String name) 
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
/** make sure all defaults are returned
 */
public void testDefaultCtor () throws Exception
{ 
  System.out.println ("testDefaultCtor");
  NodeVizMapper viz = new NodeVizMapper ();

  assertTrue (viz.getAttributeController (NodeVizMapper.FILL_COLOR) == null);

  HashMap emptyBundle = new HashMap ();  // assorted attributes for one node

  assertTrue (viz.getNodeFillColor (emptyBundle) == viz.getDefaultNodeFillColor ());

} // testDefaultCtor
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (NodeVizMapperTest.class));
}
//------------------------------------------------------------------------------
} // NodeVizMapperTest
