// EdgeVizMapperTest.java
// todo (pshannon, 2002/02/28):  edgecolor for pp and pd works.  try it next
//                               for continuous values, stealing spectrum assignment
//                               code from VizAttributes.  then on to thickness,
//                               style, and decorations.
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

import cytoscape.GraphObjAttributes;
import java.awt.Color;
import java.util.*;
//------------------------------------------------------------------------------
public class EdgeVizMapperTest extends TestCase {


//------------------------------------------------------------------------------
public EdgeVizMapperTest (String name) 
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
 * make sure all defaults are returned
 */
public void testDefaultCtor () throws Exception
{ 
  System.out.println ("testDefaultCtor");
  EdgeVizMapper viz = new EdgeVizMapper ();

  assertTrue (viz.getAttributeController (EdgeVizMapper.COLOR) == null);
  assertTrue (viz.getAttributeController (EdgeVizMapper.LINE_STYLE) == null);
  assertTrue (viz.getAttributeController (EdgeVizMapper.THICKNESS) == null);
  assertTrue (viz.getAttributeController (EdgeVizMapper.SOURCE_DECORATION) == null);
  assertTrue (viz.getAttributeController (EdgeVizMapper.TARGET_DECORATION) == null);

  HashMap emptyBundle = new HashMap ();  // assorted attributes for one edge

  assertTrue (viz.getEdgeColor (emptyBundle) == Color.black);
  assertTrue (viz.getLineStyle (emptyBundle).equals ("solid"));
  assertTrue (viz.getLineThickness (emptyBundle) == 1);
  assertTrue (viz.getSourceDecoration (emptyBundle).equals ("none"));
  assertTrue (viz.getTargetDecoration (emptyBundle).equals ("none"));

} // testDefaultCtor
//-------------------------------------------------------------------------
/**
 * control edge color via a string scalar attribute of the edge
 */
public void testColorControlByStringScalar () throws Exception
{ 
  System.out.println ("testColorControlByStringScalar");
  EdgeVizMapper viz = new EdgeVizMapper ();
  String controllersName = "interaction";
  viz.setAttributeController (EdgeVizMapper.COLOR, controllersName, 
                              "string".getClass(), EdgeVizMapper.DISCRETE);

  assertTrue (viz.getAttributeController (EdgeVizMapper.COLOR).equals (controllersName));
  assertTrue (viz.getDataDomainType (EdgeVizMapper.COLOR).equals (EdgeVizMapper.DISCRETE));
  assertTrue (viz.getDataClassOfAttribute (EdgeVizMapper.COLOR).equals ("x".getClass()));
 
  viz.setDiscreteAttribute (EdgeVizMapper.COLOR, "pp", Color.blue);
  viz.setDiscreteAttribute (EdgeVizMapper.COLOR, "pd", Color.yellow);

  HashMap bundle = new HashMap ();  // assorted attributes for one edge
  bundle.put ("interaction", "pp");
  bundle.put ("homology", new Double (232.02));
  
  assertTrue (viz.getEdgeColor (bundle).equals (Color.blue));

  bundle = new HashMap ();
  bundle.put ("interaction",  "intentionalMistake");
  assertTrue (viz.getEdgeColor (bundle).equals (viz.getDefaultEdgeColor ()));

} // testColorControlByStringScalar
//-------------------------------------------------------------------------
/**
 * control edge color based on value of a continuously varying attribute value
 */
public void testColorControlByContinuousNumericalValue () throws Exception
{ 
  System.out.println ("testColorControlByContinuousNumericalValue");
  EdgeVizMapper viz = new EdgeVizMapper ();
  String controllersName = "homology";
  viz.setAttributeController (EdgeVizMapper.COLOR, controllersName, 
                              new Double(0.0).getClass(), EdgeVizMapper.CONTINUOUS);

  assertTrue (viz.getAttributeController (EdgeVizMapper.COLOR).equals (controllersName));
  assertTrue (viz.getDataDomainType (EdgeVizMapper.COLOR).equals (EdgeVizMapper.CONTINUOUS));
  assertTrue (viz.getDataClassOfAttribute (EdgeVizMapper.COLOR).equals (
                                                      new Double (0.0).getClass()));

  Double min = new Double (0.0);
  Double max = new Double (100.0);
  viz.setContinuousAttributeControls (EdgeVizMapper.COLOR, min, Color.black, max, Color.white);

  HashMap bundle = new HashMap ();  // assorted attributes for one edge
  bundle.put ("interaction", "pp");
  bundle.put ("homology", new Double (50.0));
  Color mappedColor = viz.getEdgeColor (bundle);
  int red = mappedColor.getRed ();
  int green = mappedColor.getGreen ();
  int blue = mappedColor.getBlue ();
  assertTrue (red == 127);
  assertTrue (blue == 127);
  assertTrue (green == 127);
  
  bundle = new HashMap ();
  bundle.put ("interaction",  "pp");
  assertTrue (viz.getEdgeColor (bundle).equals (viz.getDefaultEdgeColor ()));

} // testColorControlByContinuousNumericalValue
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (EdgeVizMapperTest.class));
}
//------------------------------------------------------------------------------
} // EdgeVizMapperTest
