// EdgeVizMapperPropertiesAdapterTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.vizmap.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.vizmap.EdgeVizMapper;
import cytoscape.vizmap.EdgeVizMapperPropertiesAdapter;

import cytoscape.GraphObjAttributes;
import java.awt.Color;
import java.util.*;
//------------------------------------------------------------------------------
public class EdgeVizMapperPropertiesAdapterTest extends TestCase {


//------------------------------------------------------------------------------
public EdgeVizMapperPropertiesAdapterTest (String name) 
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
public void testAssignAttributeControllers () throws Exception
{ 
  System.out.println ("testAssignAttributeControllers");

  String propsFileName = "discreteEdgeTest.props";
  File testPropsFile = new File (propsFileName);
  assertTrue (testPropsFile.canRead ());

  Properties testProps = new Properties ();
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

  EdgeVizMapperPropertiesAdapter propsReader = new EdgeVizMapperPropertiesAdapter (testProps);
  EdgeVizMapper viz = propsReader.createEdgeVizMapper ();
  //System.out.println (viz);
  
  assertTrue (viz.getAttributeController(EdgeVizMapper.COLOR).equals (colorController));
  assertTrue (viz.getAttributeController (
                EdgeVizMapper.TARGET_DECORATION).equals (targetDecorationController));
  assertTrue (viz.getAttributeController (
                EdgeVizMapper.SOURCE_DECORATION).equals (sourceDecorationController));

} // testAssignAttributeControllers
//-------------------------------------------------------------------------
/**
 * use an EdgeVizMapperPropertiesAdapter to create an EdgeVizMapper, and make sure that
 * color values for a discrete variable, known to be in the properties file,
 *  can be found in the EdgeVizMapper.
 */
public void testSetColorDiscrete () throws Exception
{ 
  System.out.println ("testSetColorDiscrete");

  String propsFileName = "discreteEdgeTest.props";
  File testPropsFile = new File (propsFileName);
  assertTrue (testPropsFile.canRead ());

  Properties testProps = new Properties ();
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

  EdgeVizMapperPropertiesAdapter propsReader = new EdgeVizMapperPropertiesAdapter (testProps);
  EdgeVizMapper viz = propsReader.createEdgeVizMapper ();
  
  assertTrue (viz.getAttributeController(EdgeVizMapper.COLOR).equals ("interaction"));

    // ensure that interaction=pp get the right color
  HashMap bundle = new HashMap ();
  bundle.put ("interaction", "pp");
  Color ppEdgeColor = viz.getEdgeColor (bundle);
  assertTrue (ppEdgeColor.equals (new Color (0, 0, 255)));

    // an empty bundle is a cheap way to enusre that no relevant attributes 
    // have been set on an edge, and so we must get gack the default edge color

  bundle = new HashMap ();
  assertTrue (viz.getEdgeColor (bundle).equals (viz.getDefaultEdgeColor ()));

} // testSetColorDiscrete
//-------------------------------------------------------------------------
/**
 * use an EdgeVizMapperPropertiesAdapter to create an EdgeVizMapper, and make sure that
 * source and target decorations are retrieved.
 */
public void testSetDecorationDiscrete () throws Exception
{ 
  System.out.println ("testSetDecorationDiscrete");

  String propsFileName = "discreteEdgeTest.props";
  File testPropsFile = new File (propsFileName);
  assertTrue (testPropsFile.canRead ());

  Properties testProps = new Properties ();
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

  EdgeVizMapperPropertiesAdapter propsReader = new EdgeVizMapperPropertiesAdapter (testProps);
  EdgeVizMapper viz = propsReader.createEdgeVizMapper ();
  
  assertTrue (viz.getAttributeController(EdgeVizMapper.TARGET_DECORATION).equals ("interaction"));
  assertTrue (viz.getAttributeController(EdgeVizMapper.SOURCE_DECORATION).equals ("interaction"));

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
  //System.out.println ("sourceDecoration for pp: " + viz.getSourceDecoration (bundle));
  assertTrue (viz.getSourceDecoration (bundle).equals ("arrow"));

} // testSetDecorationDiscrete
//-------------------------------------------------------------------------
/**
 * use an EdgeVizMapperPropertiesAdapter to create an EdgeVizMapper, and make sure that
 * color values for a discrete variable, known to be in the properties file,
 *  can be found in the EdgeVizMapper.
 */
public void testSetColorContinuous () throws Exception
{ 
  System.out.println ("testSetColorContinuous");

  String propsFileName = "continuousEdgeTest.props";
  File testPropsFile = new File (propsFileName);
  assertTrue (testPropsFile.canRead ());

  Properties testProps = new Properties ();
  FileInputStream istream = new FileInputStream (testPropsFile);
  testProps.load (istream);

  String key = "edge.color.controller";
  assertTrue (testProps.containsKey (key));
  String value = testProps.getProperty (key);
  assertTrue (value.equals ("homology"));

  key = "edge.color.homology.type";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("continuous"));

  key = "edge.color.homology.min.value";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("0.0"));

  key = "edge.color.homology.min.color";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("0,0,0"));

  key = "edge.color.homology.max.value";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("100.0"));

  key = "edge.color.homology.max.color";
  assertTrue (testProps.containsKey (key));
  value = testProps.getProperty (key);
  assertTrue (value.equals ("255,255,255"));

  EdgeVizMapperPropertiesAdapter propsReader = new EdgeVizMapperPropertiesAdapter (testProps);
  EdgeVizMapper viz = propsReader.createEdgeVizMapper ();
  assertTrue (viz.getAttributeController(EdgeVizMapper.COLOR).equals ("homology"));

    // ensure that interaction=pp get the right color
  HashMap bundle = new HashMap ();
  bundle.put ("homology", new Double (50.0));
  Color edgeColor = viz.getEdgeColor (bundle);
  //System.out.println ("edgeColor: " + edgeColor);
  assertTrue (edgeColor.equals (new Color (127, 127, 127)));

    // an empty bundle is a cheap way to enusre that no relevant attributes 
    // have been set on an edge, and so we must get gack the default edge color

  bundle = new HashMap ();
  assertTrue (viz.getEdgeColor (bundle).equals (viz.getDefaultEdgeColor ()));

} // testSetColorContinuous
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (EdgeVizMapperPropertiesAdapterTest.class));
}
//------------------------------------------------------------------------------
} // EdgeVizMapperPropertiesAdapterTest
