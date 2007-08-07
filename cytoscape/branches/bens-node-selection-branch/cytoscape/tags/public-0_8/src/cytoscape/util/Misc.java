// Misc.java:  miscellaneous static utilities
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.util;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.awt.Color;
import java.awt.Polygon;
import java.util.*;

import y.view.Arrow;
import y.view.LineType;
import y.view.ShapeNodeRealizer;
//------------------------------------------------------------------------------
public class Misc {

    static Polygon p;

    public static void init() {
	p = new Polygon();
	p.addPoint(0,0);
	p.addPoint(-40,20);
	p.addPoint(-30,0);
	p.addPoint(-40,-20);
	Arrow.addCustomArrow("BigDelta",p,new Color(255,128,0));
    }

    private static Arrow scalableArrow(String sizeText) {
	p = new Polygon();
	int size = Integer.parseInt(sizeText);
	p.addPoint(0,0);
	p.addPoint(-size,size/2);
	p.addPoint(-(size*3)/4,0);
	p.addPoint(-size,-size/2);
	System.out.println("scalable arrow " + sizeText);
	return Arrow.addCustomArrow("scalableArrow" + sizeText,p,new Color(255,255,255));
    }
//------------------------------------------------------------------------------
public static Color parseRGBText (String text)
{
  StringTokenizer strtok = new StringTokenizer (text, ",");
  if (strtok.countTokens () != 3) {
    System.err.println ("illegal RGB string in EdgeViz.parseRGBText: " + text);
    return Color.black;
    }

  String red = strtok.nextToken().trim();
  String green = strtok.nextToken().trim();
  String blue = strtok.nextToken().trim();
  
  try {
    int r = Integer.parseInt (red);
    int g = Integer.parseInt (green);
    int b = Integer.parseInt (blue);
    return new Color (r,g,b);
    }
  catch (NumberFormatException e) {
    return Color.black;
    }  

} // parseRGBText
//--------------------------------------------------------------------------------------


public static Arrow parseArrowText (String text)
{
  StringTokenizer strtok = new StringTokenizer (text, ",");
  if (strtok.countTokens () != 1) {
      System.err.println ("illegal Arrow string in EdgeViz.parseArrowText: " + text);
      return Arrow.NONE;
  }

  String arrowtext = strtok.nextToken().trim();
  
  if(arrowtext.equalsIgnoreCase("delta"))
      return Arrow.DELTA;
  else if(arrowtext.equalsIgnoreCase("standard"))
      return Arrow.STANDARD;
  else if(arrowtext.equalsIgnoreCase("arrow"))
      return Arrow.STANDARD;
  else if(arrowtext.equalsIgnoreCase("diamond"))
      return Arrow.DIAMOND;
  else if(arrowtext.equalsIgnoreCase("short"))
      return Arrow.SHORT;
  else if(arrowtext.equalsIgnoreCase("white_delta"))
      return Arrow.WHITE_DELTA;
  else if(arrowtext.equalsIgnoreCase("whitedelta"))
      return Arrow.WHITE_DELTA;
  else if(arrowtext.equalsIgnoreCase("white_diamond"))
      return Arrow.WHITE_DIAMOND;
  else if(arrowtext.equalsIgnoreCase("whitediamond"))
      return Arrow.WHITE_DIAMOND;
  else if(arrowtext.equalsIgnoreCase("bigdelta"))
      return Arrow.getCustomArrow("BigDelta");
  else if(arrowtext.equalsIgnoreCase("none"))
      return Arrow.NONE;
  else if(arrowtext.startsWith("scalableArrow")) {
      return scalableArrow(arrowtext.replaceFirst("scalableArrow",""));
  }
  else
      return Arrow.NONE;
} // parseArrowText


public static LineType parseLineTypeText (String text)
{
  StringTokenizer strtok = new StringTokenizer (text, ",");
  if (strtok.countTokens () != 1) {
      System.err.println ("illegal LineType string in EdgeViz.parseLineTypeText: " + text);
      return LineType.LINE_1;
  }

  String lttext = strtok.nextToken().trim();
  lttext = lttext.replaceAll("_",""); // ditch all underscores
  
  if(lttext.equalsIgnoreCase("dashed1"))
      return LineType.DASHED_1;
  else if(lttext.equalsIgnoreCase("dashed2"))
      return LineType.DASHED_2;
  else if(lttext.equalsIgnoreCase("dashed3"))
      return LineType.DASHED_3;
  else if(lttext.equalsIgnoreCase("dashed4"))
      return LineType.DASHED_4;
  else if(lttext.equalsIgnoreCase("dashed5"))
      return LineType.DASHED_5;
  else if(lttext.equalsIgnoreCase("line1"))
      return LineType.LINE_1;
  else if(lttext.equalsIgnoreCase("line2"))
      return LineType.LINE_2;
  else if(lttext.equalsIgnoreCase("line3"))
      return LineType.LINE_3;
  else if(lttext.equalsIgnoreCase("line4"))
      return LineType.LINE_4;
  else if(lttext.equalsIgnoreCase("line5"))
      return LineType.LINE_5;
  else if(lttext.equalsIgnoreCase("line6"))
      return LineType.LINE_6;
  else if(lttext.equalsIgnoreCase("line7"))
      return LineType.LINE_7;
  else
      return LineType.LINE_1;
} // parseLineTypeText


public static Byte parseNodeShapeTextIntoByte (String text) {
    return new Byte(parseNodeShapeText(text));
}

public static byte parseNodeShapeText (String text)
{
  StringTokenizer strtok = new StringTokenizer (text, ",");
  if (strtok.countTokens () != 1) {
      System.err.println ("illegal NodeShape string in EdgeViz.parseNodeShapeText: " + text);
      return ShapeNodeRealizer.RECT;
  }

  String nstext = strtok.nextToken().trim();
  nstext = nstext.replaceAll("_",""); // ditch all underscores
  
  if(nstext.equalsIgnoreCase("rect"))
      return ShapeNodeRealizer.RECT;
  else if(nstext.equalsIgnoreCase("roundrect"))
      return ShapeNodeRealizer.ROUND_RECT;
  else if(nstext.equalsIgnoreCase("rect3d"))
      return ShapeNodeRealizer.RECT_3D;
  else if(nstext.equalsIgnoreCase("trapezoid"))
      return ShapeNodeRealizer.TRAPEZOID;
  else if(nstext.equalsIgnoreCase("trapezoid2"))
      return ShapeNodeRealizer.TRAPEZOID_2;
  else if(nstext.equalsIgnoreCase("triangle"))
      return ShapeNodeRealizer.TRIANGLE;
  else if(nstext.equalsIgnoreCase("parallelogram"))
      return ShapeNodeRealizer.PARALLELOGRAM;
  else if(nstext.equalsIgnoreCase("diamond"))
      return ShapeNodeRealizer.DIAMOND;
  else if(nstext.equalsIgnoreCase("ellipse") || nstext.equalsIgnoreCase("circle"))
      return ShapeNodeRealizer.ELLIPSE;
  else if(nstext.equalsIgnoreCase("hexagon"))
      return ShapeNodeRealizer.HEXAGON;
  else if(nstext.equalsIgnoreCase("octagon"))
      return ShapeNodeRealizer.OCTAGON;
  else
      return ShapeNodeRealizer.RECT;
} // parseNodeShapeText

}
