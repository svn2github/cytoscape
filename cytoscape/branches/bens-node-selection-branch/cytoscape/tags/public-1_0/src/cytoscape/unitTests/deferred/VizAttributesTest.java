// VizAttributesTest.java

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
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
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
import java.io.*;
import java.awt.Color;

import cytoscape.VizAttributes;
//------------------------------------------------------------------------------
public class VizAttributesTest extends TestCase {


//------------------------------------------------------------------------------
public VizAttributesTest (String name) 
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
  VizAttributes vizAtt = new VizAttributes ();

  Color defaultBackgroundColor = vizAtt.getBackgroundColor ();
  assert (defaultBackgroundColor != null);

  assert (vizAtt.getNumberOfNodeColorBins () == -99);

} // testCtor
//------------------------------------------------------------------------------
/**
 * in the absence of explictly set 'node color calculation factors' -- in which
 * we want the node's color to reflect the value of an attribute attached to it
 * (for which, see 'testNodeColorCalculationFromNodeAttribute' below) -- every
 * node should be displayed with the default node color.  test that here
 */
public void testNodeColorCalculationInAbsenceOfCalculationFactors () throws Exception
{ 
  System.out.println ("testNodeColorCalculationInAbsenceOfCalculationFactors ");
  VizAttributes vizAtt = new VizAttributes ();

  NodeProperties nodeProps = new NodeProperties ();
  Color result = vizAtt.getNodeColor (nodeProps, "GAL4");
  Color expected = vizAtt.getDefaultNodeFillColor ();
  assert (result.equals (new Color (expected.getRed (), expected.getGreen(), 
                         expected.getBlue ())));

} // testNodeColorCalculationInAbsenceOfCalculationFactors 
//------------------------------------------------------------------------------
/*
 * the color of a node (it's fill color) is a function of 
 *
 *  - the value of an attribute attached to the node 
 *  - the range of values that attribute takes
 *  - the base color of the node
 *  - the number of gradations allowed for the color
 *
 * for example, let's say that
 *  
 *   1. we deicde that the attribute 'mRNA expression' is to govern the
 *      intensity of the node's color
 *   2. the base color for the node is red, at half-intensity (128, 0, 0)
 *   3. the node's mRNA expression value is 1.8;
 *   4. the min mRNA expression value is 0.0
 *   5. the max is 10.0
 *   6. we want to show 5 levels of color
 *
**/
public void testNodeColorCalculationFromNodeAttribute () throws Exception
{ 
  System.out.println ("testNodeColorCalculationFromNodeAttribute");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;
  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue);

  NodeProperties nodeProps = new NodeProperties ();
  nodeProps.add ("mRNA expression", "GAL4", 5.0);
  nodeProps.add ("mRNA expression", "GAL80", 0.01);
  nodeProps.add ("foo", "GAL4", 321.23);

  int maxTests = 5;
  double expressionLevelDelta = (attributeMaxValue - attributeMinValue) / maxTests;
  float [] brightnessLevels = new float [maxTests+1];

  for (int i=0; i <= maxTests; i++) {
    double expressionLevel = i * expressionLevelDelta;
    nodeProps.add ("mRNA expression", "GAL4", expressionLevel);
    Color result = vizAtt.getNodeColor (nodeProps, "GAL4");
    int red = result.getRed ();
    int green = result.getGreen ();
    int blue = result.getBlue ();
    brightnessLevels [i] = Color.RGBtoHSB (red, green, blue, null) [2];
    //System.out.println ("GAL4 at "  + expressionLevel + ": " + result + " " 
    //                    +  brightnessLevels [i]);
    } // for i
 
  for (int i=1; i < brightnessLevels.length; i++) {
    // todo: weakened assert (less than -or- equal to) to accomodate
    //       the lower clamp on minimum values (brightness of 0.4
    // rethink this test after clamping high and low is in place
    //System.out.println ("i: " + i + "  " + brightnessLevels [i] + " > " +
    //                    brightnessLevels [i-1]);
    assert (brightnessLevels [i] >= brightnessLevels [i-1]);
    }

} // testNodeColorCalculationFromNodeAttribute
//------------------------------------------------------------------------------
/*
 * use 10 bins, and a value range also of 10.  
 * make sure that each successive brightness level is higher than the preceeding
 * make sure that a value of zero returns a brightness level of zero, and
 *           that a maximal value returns a brightness level of one
 *
 * results should look like this:
 *
 *   GAL4 at 0.0: java.awt.Color[r=0,g=0,b=0]       0.0
 *   GAL4 at 1.0: java.awt.Color[r=26,g=0,b=26]     0.101960786
 *   GAL4 at 2.0: java.awt.Color[r=51,g=0,b=51]     0.2
 *   GAL4 at 3.0: java.awt.Color[r=77,g=0,b=77]     0.3019608
 *   GAL4 at 4.0: java.awt.Color[r=102,g=0,b=102]   0.4
 *   GAL4 at 5.0: java.awt.Color[r=128,g=0,b=128]   0.5019608
 *   GAL4 at 6.0: java.awt.Color[r=153,g=0,b=153]   0.6
 *   GAL4 at 7.0: java.awt.Color[r=179,g=0,b=179]   0.7019608
 *   GAL4 at 8.0: java.awt.Color[r=204,g=0,b=204]   0.8
 *   GAL4 at 9.0: java.awt.Color[r=230,g=0,b=230]   0.9019608
 *   GAL4 at 10.0: java.awt.Color[r=255,g=0,b=255]  1.0
 *
**/
public void noTestUseFullRangeOfBrightness_0 () throws Exception
{ 
  System.out.println ("testUseFullRangeOfBrightness_0");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;
  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue);

  float [] brightnessLevels = new float [numberOfBins + 1];
  NodeProperties nodeProps = new NodeProperties ();
  for (int i=0; i < numberOfBins+1; i++) {
    double expressionLevel = i * 1.0;
    nodeProps.add ("mRNA expression", "GAL4", expressionLevel);
    Color result = vizAtt.getNodeColor (nodeProps, "GAL4");
    int red = result.getRed ();
    int green = result.getGreen ();
    int blue = result.getBlue ();
    float brightness = Color.RGBtoHSB (red, green, blue, null) [2];
    brightnessLevels [i] = brightness;
    //System.out.println ("GAL4 at "  + expressionLevel + ": " + result + " " 
    //                    +  brightness);
    } // for i

  Float bottom = new Float (brightnessLevels [0]);
  Float top    = new Float (brightnessLevels [brightnessLevels.length-1]);

  assert ((bottom.compareTo (new Float (0.0))) == 0);
  assert ((top.compareTo    (new Float (1.0))) == 0);

  for (int i=1; i < brightnessLevels.length; i++) {
    assert (brightnessLevels [i] > brightnessLevels [i-1]);
    }
  

} // testUseFullRangeOfBrightness_0
//------------------------------------------------------------------------------
/*
 * use 2 bins, and a value range of 10.  
 * make sure that each successive brightness level is higher than or
 *   equal to the preceeding brightness level
 * make sure that a value of zero returns a brightness level of zero, and
 *           that a maximal value returns a brightness level of one
 *
 * results should look like this:
 *
 *   GAL4 at 0.0: java.awt.Color[r=0,g=0,b=0]       0.0
 *   GAL4 at 1.0: java.awt.Color[r=0,g=0,b=0]       0.0
 *   GAL4 at 2.0: java.awt.Color[r=0,g=0,b=0]       0.0
 *   GAL4 at 3.0: java.awt.Color[r=0,g=0,b=0]       0.0
 *   GAL4 at 4.0: java.awt.Color[r=0,g=0,b=0]       0.0
 *   GAL4 at 5.0: java.awt.Color[r=128,g=0,b=128]   0.5019608
 *   GAL4 at 6.0: java.awt.Color[r=128,g=0,b=128]   0.5019608
 *   GAL4 at 7.0: java.awt.Color[r=128,g=0,b=128]   0.5019608
 *   GAL4 at 8.0: java.awt.Color[r=128,g=0,b=128]   0.5019608
 *   GAL4 at 9.0: java.awt.Color[r=128,g=0,b=128]   0.5019608
 *   GAL4 at 10.0: java.awt.Color[r=255,g=0,b=255]  1.0
 *   
**/
public void testUseFullRangeOfBrightness_1 () throws Exception
{ 
  System.out.println ("testUseFullRangeOfBrightness_1");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 2;
  int numberOfSamples = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;
  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue);

  float [] brightnessLevels = new float [numberOfSamples + 1];
  NodeProperties nodeProps = new NodeProperties ();
  for (int i=0; i < numberOfSamples+1; i++) {
    double expressionLevel = i * 1.0;
    nodeProps.add ("mRNA expression", "GAL4", expressionLevel);
    Color result = vizAtt.getNodeColor (nodeProps, "GAL4");
    int red = result.getRed ();
    int green = result.getGreen ();
    int blue = result.getBlue ();
    float brightness = Color.RGBtoHSB (red, green, blue, null) [2];
    brightnessLevels [i] = brightness;
    //System.out.println ("GAL4 at "  + expressionLevel + ": " + result + " " 
    //                    +  brightness);
    } // for i

  Float bottom = new Float (brightnessLevels [0]);
  Float top    = new Float (brightnessLevels [brightnessLevels.length-1]);

  assert ((bottom.compareTo (new Float (0.0))) == 0);
  assert ((top.compareTo    (new Float (1.0))) == 0);

  for (int i=1; i < brightnessLevels.length; i++) {
    assert (brightnessLevels [i] >= brightnessLevels [i-1]);
    }
  
} // testUseFullRangeOfBrightness_1
//------------------------------------------------------------------------------
public void testColorBinsCalculation_noClamps () throws Exception
{ 
  System.out.println ("testColorBinsCalculation_noClamps");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 10;
  int numberOfSamples = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;

  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue);

  double [] bins = vizAtt.calculateNodeColorBins ();

  assert (bins.length == 10);
  assert ((new Double (bins [0]).compareTo (new Double (0.0))) == 0);
  assert ((new Double (bins [1]).compareTo (new Double (1.0))) == 0);
  assert ((new Double (bins [2]).compareTo (new Double (2.0))) == 0);
  assert ((new Double (bins [3]).compareTo (new Double (3.0))) == 0);
  assert ((new Double (bins [4]).compareTo (new Double (4.0))) == 0);
  assert ((new Double (bins [5]).compareTo (new Double (5.0))) == 0);
  assert ((new Double (bins [6]).compareTo (new Double (6.0))) == 0);
  assert ((new Double (bins [7]).compareTo (new Double (7.0))) == 0);
  assert ((new Double (bins [8]).compareTo (new Double (8.0))) == 0);
  assert ((new Double (bins [9]).compareTo (new Double (9.0))) == 0);



} // testColorBinsCalculation_noClamps
//------------------------------------------------------------------------------
public void testColorBinsCalculation_lowClamp () throws Exception
{ 
  System.out.println ("testColorBinsCalculation_lowClamp");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 10;
  int numberOfSamples = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;

  double lowClamp = 2.5;
  double highClamp = attributeMaxValue;

  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue,
                                         lowClamp, highClamp);

  double [] bins = vizAtt.calculateNodeColorBins ();

  assert (bins.length == 11);

  assert ((new Double (bins [0]).compareTo (new Double (0.0))) == 0);
  assert ((new Double (bins [1]).compareTo (new Double (2.5))) == 0);
  assert ((new Double (bins [2]).compareTo (new Double (3.25))) == 0);
  assert ((new Double (bins [3]).compareTo (new Double (4.0))) == 0);
  assert ((new Double (bins [4]).compareTo (new Double (4.75))) == 0);
  assert ((new Double (bins [5]).compareTo (new Double (5.5))) == 0);
  assert ((new Double (bins [6]).compareTo (new Double (6.25))) == 0);
  assert ((new Double (bins [7]).compareTo (new Double (7.0))) == 0);
  assert ((new Double (bins [8]).compareTo (new Double (7.75))) == 0);
  assert ((new Double (bins [9]).compareTo (new Double (8.5))) == 0);
  assert ((new Double (bins [10]).compareTo (new Double (9.25))) == 0);



} // testColorBinsCalculation_lowClamp
//------------------------------------------------------------------------------
public void testColorBinsCalculation_highClamp () throws Exception
{ 
  System.out.println ("testColorBinsCalculation_highClamp");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 10;
  int numberOfSamples = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;

  double lowClamp = attributeMinValue;
  double highClamp = 7.5;

  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue,
                                         lowClamp, highClamp);

  double [] bins = vizAtt.calculateNodeColorBins ();

  assert (bins.length == 11);

  assert ((new Double (bins [0]).compareTo (new Double (0.0))) == 0);
  assert ((new Double (bins [1]).compareTo (new Double (0.75))) == 0);
  assert ((new Double (bins [2]).compareTo (new Double (1.5))) == 0);
  assert ((new Double (bins [3]).compareTo (new Double (2.25))) == 0);
  assert ((new Double (bins [4]).compareTo (new Double (3.0))) == 0);
  assert ((new Double (bins [5]).compareTo (new Double (3.75))) == 0);
  assert ((new Double (bins [6]).compareTo (new Double (4.5))) == 0);
  assert ((new Double (bins [7]).compareTo (new Double (5.25))) == 0);
  assert ((new Double (bins [8]).compareTo (new Double (6.0))) == 0);
  assert ((new Double (bins [9]).compareTo (new Double (6.75))) == 0);
  assert ((new Double (bins [10]).compareTo (new Double (7.5))) == 0);

} // testColorBinsCalculation_highClamp
//------------------------------------------------------------------------------
public void testColorBinsCalculation_bothClamps () throws Exception
{ 
  System.out.println ("testColorBinsCalculation_bothClamps");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 10;
  int numberOfSamples = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;

  double lowClamp = 2.5;
  double highClamp = 7.5;

  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue,
                                         lowClamp, highClamp);

  double [] bins = vizAtt.calculateNodeColorBins ();

  assert (bins.length == 12);

  assert ((new Double (bins [0]).compareTo (new Double (0.0))) == 0);
  assert ((new Double (bins [1]).compareTo (new Double (2.5))) == 0);
  assert ((new Double (bins [2]).compareTo (new Double (3.0))) == 0);
  assert ((new Double (bins [3]).compareTo (new Double (3.5))) == 0);
  assert ((new Double (bins [4]).compareTo (new Double (4.0))) == 0);
  assert ((new Double (bins [5]).compareTo (new Double (4.5))) == 0);
  assert ((new Double (bins [6]).compareTo (new Double (5.0))) == 0);
  assert ((new Double (bins [7]).compareTo (new Double (5.5))) == 0);
  assert ((new Double (bins [8]).compareTo (new Double (6.0))) == 0);
  assert ((new Double (bins [9]).compareTo (new Double (6.5))) == 0);
  assert ((new Double (bins [10]).compareTo (new Double (7.0))) == 0);
  assert ((new Double (bins [11]).compareTo (new Double (7.5))) == 0);


} // testColorBinsCalculation_bothClamps
//------------------------------------------------------------------------------
public void testSetClamp () throws Exception
{ 
  System.out.println ("testSetClamps");
  VizAttributes vizAtt = new VizAttributes ();

  String attributeName = "mRNA expression";
  Color baseColor = new Color (128, 0, 128);
  int numberOfBins = 10;
  int numberOfSamples = 10;
  double attributeMinValue = 0.0;
  double attributeMaxValue = 10.0;

  double lowClamp = 2.5;
  double highClamp = 7.5;

  vizAtt.setNodeColorCalculationFactors (attributeName, baseColor, numberOfBins,
                                         attributeMinValue, attributeMaxValue,
                                         lowClamp, highClamp);

  float [] brightnessLevels = new float [numberOfSamples + 1];
  NodeProperties nodeProps = new NodeProperties ();
  for (int i=0; i < numberOfSamples+1; i++) {
    double expressionLevel = i * 1.0;
    nodeProps.add ("mRNA expression", "GAL4", expressionLevel);
    Color result = vizAtt.getNodeColor (nodeProps, "GAL4");
    int red = result.getRed ();
    int green = result.getGreen ();
    int blue = result.getBlue ();
    float brightness = Color.RGBtoHSB (red, green, blue, null) [2];
    brightnessLevels [i] = brightness;
    //System.out.println ("GAL4 at "  + expressionLevel + ": " + result + " " 
    //                    +  brightness);
    } // for i

  Float bottom = new Float (brightnessLevels [0]);
  Float top    = new Float (brightnessLevels [brightnessLevels.length-1]);

  assert ((bottom.compareTo (new Float (0.0))) == 0);
  assert ((top.compareTo    (new Float (1.0))) == 0);

  for (int i=1; i < brightnessLevels.length; i++) {
    assert (brightnessLevels [i] >= brightnessLevels [i-1]);
    }
  
} // testSetClamps
//------------------------------------------------------------------------------
public void disabledTestColorIncrements () throws Exception
{
   Color c = new Color (10,0,80);
   float [] hsb =  Color.RGBtoHSB (c.getRed (), c.getGreen (), c.getBlue (), null);
   float hue = hsb [0];
   float saturation = hsb [1];
   float brightness = hsb [2];

   int steps = 10;
   float brightnessStep = (1.0f - brightness) / steps;

   for (int i=0; i < steps; i++) {
     brightness += brightnessStep;
     Color cNew = Color.getHSBColor (hue, saturation, brightness);
     System.err.println ("brightness: " + brightness + "  " + cNew);
     }

}
//------------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (VizAttributesTest.class));
}
//------------------------------------------------------------------------------
} // VizAttributesTest


