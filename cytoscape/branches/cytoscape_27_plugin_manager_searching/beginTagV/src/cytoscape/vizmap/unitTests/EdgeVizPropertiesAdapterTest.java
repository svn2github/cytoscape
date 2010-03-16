// EdgeVizPropertiesAdapterTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.vizmap.EdgeViz;
import cytoscape.vizmap.EdgeVizPropertiesAdapter;

import cytoscape.GraphObjAttributes;
import java.awt.Color;
import java.util.*;
//------------------------------------------------------------------------------
public class EdgeVizPropertiesAdapterTest extends TestCase {


//------------------------------------------------------------------------------
public EdgeVizPropertiesAdapterTest (String name) 
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

  String key = "edge.color.controller";
  assertTrue (testProps.containsKey (key));
  String colorController = testProps.getProperty (key);
  assertTrue (colorController.equals ("interaction"));

  key = "edge.targetDecoration.controller";
  assertTrue (testProps.containsKey (key));
  String targetDecorationController = testProps.getProperty (key);
  assertTrue (targetDecorationController.equals ("interaction"));

  key = "edge.sourceDecoration.controller";
  assertTrue (testProps.containsKey (key));
  String sourceDecorationController = testProps.getProperty (key);
  assertTrue (targetDecorationController.equals ("interaction"));

  EdgeVizPropertiesAdapter propsReader = new EdgeVizPropertiesAdapter (testProps);
  EdgeViz viz = propsReader.createEdgeViz ();
  // System.out.println (viz);
  
  assertTrue (viz.getAttributeController(EdgeViz.COLOR).equals (colorController));
  assertTrue (viz.getAttributeController (
                EdgeViz.TARGET_DECORATION).equals (targetDecorationController));
  assertTrue (viz.getAttributeController (
                EdgeViz.SOURCE_DECORATION).equals (sourceDecorationController));

} // testAssignAttributeControllerssFromProperties
//-------------------------------------------------------------------------
/**
 * use an EdgeVizPropertiesAdapter to create an EdgeViz, and make sure that
 * color values known to be in the properties file can be found in the EdgeViz.
 */
public void testAssignEdgeColorsFromPropsFile () throws Exception
{ 
  System.out.println ("testAssignEdgePropertiesColorsPropsFile");

  Properties testProps = new Properties ();
  File testPropsFile = new File ("edgeVizTest.props");
  assertTrue (testPropsFile.canRead ());
  FileInputStream istream = new FileInputStream (testPropsFile);
  testProps.load (istream);

  String key = "edge.color.controller";
  assertTrue (testProps.containsKey (key));
  String value = testProps.getProperty (key);
  assertTrue (value.equals ("interaction"));

  key = "edge.color.interaction.type";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("discrete"));

  key = "edge.color.interaction.map.pp";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("0,0,255"));

  key = "edge.color.interaction.map.pd";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("255,255,0"));

  key = "edge.targetDecoration.controller";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("interaction"));

  EdgeVizPropertiesAdapter propsReader = new EdgeVizPropertiesAdapter (testProps);
  EdgeViz viz = propsReader.createEdgeViz ();
  
  assertTrue (viz.getAttributeController(EdgeViz.COLOR).equals ("interaction"));

    // ensure that interaction=pp get the right color
  HashMap bundle = new HashMap ();
  bundle.put ("interaction", "pp");
  Color ppEdgeColor = viz.getEdgeColor (bundle);
  assertTrue (ppEdgeColor.equals (new Color (0, 0, 255)));

    // an empty bundle is a cheap way to enusre that no relevant attributes 
    // have been set on an edge, and so we must get gack the default edge color

  bundle = new HashMap ();
  assertTrue (viz.getEdgeColor (bundle).equals (viz.getDefaultEdgeColor ()));

} // testAssignEdgeColorsFromPropsFile
//-------------------------------------------------------------------------
/**
 * use an EdgeVizPropertiesAdapter to create an EdgeViz, and make sure that
 * source and target decorations are retrieved.
 */
public void testAssignEdgeDecorationsFromPropsFile () throws Exception
{ 
  System.out.println ("testAssignEdgePropertiesDecorationsPropsFile");

  Properties testProps = new Properties ();
  File testPropsFile = new File ("edgeVizTest.props");
  assertTrue (testPropsFile.canRead ());
  FileInputStream istream = new FileInputStream (testPropsFile);
  testProps.load (istream);

  String key = "edge.targetDecoration.controller";
  assertTrue (testProps.containsKey (key));
  String value = testProps.getProperty (key);
  assertTrue (value.equals ("interaction"));

  key = "edge.color.interaction.type";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("discrete"));

  key = "edge.targetDecoration.interaction.map.pd";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("arrow"));

  key = "edge.sourceDecoration.controller";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("interaction"));

  EdgeVizPropertiesAdapter propsReader = new EdgeVizPropertiesAdapter (testProps);
  EdgeViz viz = propsReader.createEdgeViz ();
  
  assertTrue (viz.getAttributeController(EdgeViz.TARGET_DECORATION).equals ("interaction"));
  assertTrue (viz.getAttributeController(EdgeViz.SOURCE_DECORATION).equals ("interaction"));

    // an empty bundle (representing all the attributes of an edge) should only
    // get default values
  HashMap bundle = new HashMap ();
  assertTrue (viz.getSourceDecoration (bundle).equals (viz.getDefaultSourceDecoration ()));
  assertTrue (viz.getTargetDecoration (bundle).equals (viz.getDefaultTargetDecoration ()));

    // now create an attribute bundle for an edge, setting "interaction=pd".  this
    // should produce a targetDecoration of "arrow", and the default sourceDecoration.

  bundle = new HashMap ();
  bundle.put ("interaction", "pd");
  assertTrue (viz.getSourceDecoration (bundle).equals (viz.getDefaultSourceDecoration ()));
  //System.out.println ("targetDecoration for pd: " + viz.getTargetDecoration (bundle));
  assertTrue (viz.getTargetDecoration (bundle).equals ("arrow"));

    // set "interaction=pp", and make sure that we get the opposite values of
    // the immediately preceeding step:  a sourceDecoration of "arrow", and 
    // the default targetDecoration.

  bundle = new HashMap ();
  bundle.put ("interaction", "pp");
  assertTrue (viz.getTargetDecoration (bundle).equals (viz.getDefaultTargetDecoration ()));
  // System.out.println ("sourceDecoration for pp: " + viz.getSourceDecoration (bundle));
  assertTrue (viz.getSourceDecoration (bundle).equals ("arrow"));

} // testAssignEdgeDecorationsFromPropsFile
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (EdgeVizPropertiesAdapterTest.class));
}
//------------------------------------------------------------------------------
} // EdgeVizPropertiesAdapterTest
