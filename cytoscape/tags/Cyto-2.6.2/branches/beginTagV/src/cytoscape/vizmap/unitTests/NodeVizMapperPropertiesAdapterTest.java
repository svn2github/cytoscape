// NodeVizMapperPropertiesAdapterTest.java
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
import cytoscape.vizmap.NodeVizMapperPropertiesAdapter;

import cytoscape.GraphObjAttributes;
import java.awt.Color;
import java.util.*;
//------------------------------------------------------------------------------
public class NodeVizMapperPropertiesAdapterTest extends TestCase {


//------------------------------------------------------------------------------
public NodeVizMapperPropertiesAdapterTest (String name) 
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
public void testAssignAttributeControllersProperties () throws Exception
{ 
  System.out.println ("testAssignColorAttributesFromProperties");

  Properties testProps = new Properties ();
  File testPropsFile = new File ("test.props");
  assertTrue (testPropsFile.canRead ());
  FileInputStream istream = new FileInputStream (testPropsFile);
  testProps.load (istream);

  String key = "node.fillcolor.controller";
  assertTrue (testProps.containsKey (key));
  String colorController = testProps.getProperty (key);
  assertTrue (colorController.equals ("expression"));

  NodeVizMapperPropertiesAdapter propsReader = new NodeVizMapperPropertiesAdapter (testProps);
  NodeVizMapper viz = propsReader.createNodeVizMapper ();
  // System.out.println (viz);
  
  assertTrue (viz.getAttributeController(NodeVizMapper.FILL_COLOR).equals (colorController));

} // testAssignAttributeControllerssFromProperties
//-------------------------------------------------------------------------
/**
 * use an NodeVizMapperPropertiesAdapter to create an NodeVizMapper, and make sure that
 * color values known to be in the properties file can be found in the NodeVizMapper.
 */
public void testAssignNodeFillColorsFromPropsFile () throws Exception
{ 
  System.out.println ("testAssignNodePropertiesColorsPropsFile");

  String propsFileName = "continuousNodeTest.props";
  File testPropsFile = new File (propsFileName);
  assertTrue (testPropsFile.canRead ());


  Properties testProps = new Properties ();
  FileInputStream istream = new FileInputStream (testPropsFile);
  testProps.load (istream);

  String key = "node.fillcolor.controller";
  assertTrue (testProps.containsKey (key));
  String value = testProps.getProperty (key);
  assertTrue (value.equals ("expression"));

  key = "node.fillcolor.expression.type";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("continuous"));

  key = "node.fillcolor.expression.min.value";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("0.0"));

  key = "node.fillcolor.expression.min.color";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("0,255,0"));

  key = "node.fillcolor.expression.max.value";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("100.0"));

  key = "node.fillcolor.expression.max.color";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("255,0,0"));

  NodeVizMapperPropertiesAdapter propsReader = new NodeVizMapperPropertiesAdapter (testProps);
  NodeVizMapper viz = propsReader.createNodeVizMapper ();
  
  assertTrue (viz.getAttributeController (NodeVizMapper.FILL_COLOR).equals ("expression"));

    // ensure that values of 0, 25, 50, 75, and 100 get reasonable color values
  HashMap bundle = new HashMap ();

  bundle.put ("expression", new Double (0.0));
  assertTrue (viz.getNodeFillColor (bundle).equals (new Color (0,255,0)));

  bundle.put ("expression", new Double (25.0));
  assertTrue (viz.getNodeFillColor (bundle).equals (new Color (63,192,0)));

  bundle.put ("expression", new Double (50.0));
  assertTrue (viz.getNodeFillColor (bundle).equals (new Color (127,128,0)));

  bundle.put ("expression", new Double (75.0));
  assertTrue (viz.getNodeFillColor (bundle).equals (new Color (191,64,0)));

  bundle.put ("expression", new Double (100.0));
  assertTrue (viz.getNodeFillColor (bundle).equals (new Color (255,0,0)));

    // an empty bundle is a cheap way to enusre that no relevant attributes 
    // have been set on an node, and so we must get gack the default node color

  bundle = new HashMap ();
  assertTrue (viz.getNodeFillColor (bundle).equals (viz.getDefaultNodeFillColor ()));


} // testAssignNodeFillColorsFromPropsFile
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (NodeVizMapperPropertiesAdapterTest.class));
}
//------------------------------------------------------------------------------
} // NodeVizMapperPropertiesAdapterTest
