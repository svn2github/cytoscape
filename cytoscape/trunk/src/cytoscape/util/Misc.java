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
import java.util.*;

import y.view.Arrow;
//------------------------------------------------------------------------------
public class Misc {
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
  else if(arrowtext.equalsIgnoreCase("white_delta"))
      return Arrow.WHITE_DELTA;
  else if(arrowtext.equalsIgnoreCase("whitedelta"))
      return Arrow.WHITE_DELTA;
  else if(arrowtext.equalsIgnoreCase("white_diamond"))
      return Arrow.WHITE_DIAMOND;
  else if(arrowtext.equalsIgnoreCase("whitediamond"))
      return Arrow.WHITE_DIAMOND;
  else if(arrowtext.equalsIgnoreCase("none"))
      return Arrow.NONE;
  else
      return Arrow.NONE;
} // parseArrowText

}
