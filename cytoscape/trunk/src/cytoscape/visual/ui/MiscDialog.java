
/*
  File: MiscDialog.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// MiscDialog.java:  miscellaneous static utilities
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
//package cytoscape.dialogs;
package cytoscape.visual.ui;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.awt.Color;
import java.awt.Polygon;
import java.util.*;
import java.net.URL;

import javax.swing.*;

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.ShapeNodeRealizer;

//import cytoscape.util.Misc;

//------------------------------------------------------------------------------
public class MiscDialog {

  /**
   *  arrow dialog functions:
   *
   *   getStringToArrowHashMap(int nodeSize)
   *   getArrowToStringHashMap(int nodeSize)
   *   getArrowIcons()
   *
   * Note: with the move to Giny, I'm dropping support for the scalable
   * arrow type; we can add this back later if desired. -AM 2003-10-28
   */
  public static HashMap getStringToArrowHashMap(int nodeSize) {
    HashMap h = new HashMap();

    h.put( "NONE", (Object)Arrow.NONE);

    h.put( "WHITE_DIAMOND", (Object)Arrow.WHITE_DIAMOND);
    h.put( "BLACK_DIAMOND", (Object)Arrow.BLACK_DIAMOND);
    h.put( "COLOR_DIAMOND", (Object)Arrow.COLOR_DIAMOND);

    h.put( "WHITE_DELTA", (Object)Arrow.WHITE_DELTA);
    h.put( "BLACK_DELTA", (Object)Arrow.BLACK_DELTA);
    h.put( "COLOR_DELTA", (Object)Arrow.COLOR_DELTA);

    h.put( "WHITE_T", (Object)Arrow.WHITE_T);
    h.put( "BLACK_T", (Object)Arrow.BLACK_T);
    h.put( "COLOR_T", (Object)Arrow.COLOR_T);
  
    h.put( "WHITE_CIRCLE", (Object)Arrow.WHITE_CIRCLE);
    h.put( "BLACK_CIRCLE", (Object)Arrow.BLACK_CIRCLE);
    h.put( "COLOR_CIRCLE", (Object)Arrow.COLOR_CIRCLE);

    h.put( "WHITE_ARROW", (Object)Arrow.WHITE_ARROW);
    h.put( "BLACK_ARROW", (Object)Arrow.BLACK_ARROW);
    h.put( "COLOR_ARROW", (Object)Arrow.COLOR_ARROW);

    return h;
  }

  public static HashMap getArrowToStringHashMap(int nodeSize) {
    HashMap h = new HashMap();
    h.put((Object)Arrow.NONE, "NONE");

    h.put((Object)Arrow.WHITE_DIAMOND, "WHITE_DIAMOND");
    h.put((Object)Arrow.BLACK_DIAMOND, "BLACK_DIAMOND");
    h.put((Object)Arrow.COLOR_DIAMOND, "COLOR_DIAMOND");
  
    h.put((Object)Arrow.WHITE_DELTA, "WHITE_DELTA");
    h.put((Object)Arrow.BLACK_DELTA, "BLACK_DELTA");
    h.put((Object)Arrow.COLOR_DELTA, "COLOR_DELTA");

    h.put((Object)Arrow.WHITE_CIRCLE, "WHITE_CIRCLE");
    h.put((Object)Arrow.BLACK_CIRCLE, "BLACK_CIRCLE");
    h.put((Object)Arrow.COLOR_CIRCLE, "COLOR_CIRCLE");

    h.put((Object)Arrow.WHITE_T, "WHITE_T");
    h.put((Object)Arrow.BLACK_T, "BLACK_T");
    h.put((Object)Arrow.COLOR_T, "COLOR_T");

    h.put((Object)Arrow.WHITE_ARROW, "WHITE_ARROW");
    h.put((Object)Arrow.BLACK_ARROW, "BLACK_ARROW");
    h.put((Object)Arrow.COLOR_ARROW, "COLOR_ARROW");

    return h;
  }

  public  ImageIcon[] getArrowIcons() {

    ImageIcon[] arrowIcons = new ImageIcon[16];
    
    arrowIcons[0] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/arrow_none.jpg") , "NONE" );
   
    arrowIcons[1] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/WHITE_DIAMOND.jpg") , "WHITE_DIAMOND" );
    arrowIcons[2] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/BLACK_DIAMOND.jpg") , "BLACK_DIAMOND" );
    arrowIcons[3] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/COLOR_DIAMOND.jpg") , "COLOR_DIAMOND" );

    arrowIcons[4] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/WHITE_DELTA.jpg") , "WHITE_DELTA" );
    arrowIcons[5] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/BLACK_DELTA.jpg") , "BLACK_DELTA" );
    arrowIcons[6] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/COLOR_DELTA.jpg") , "COLOR_DELTA" );
  
    arrowIcons[7] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/WHITE_CIRCLE.jpg") , "WHITE_CIRCLE" );
    arrowIcons[8] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/BLACK_CIRCLE.jpg") , "BLACK_CIRCLE" );
    arrowIcons[9] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/COLOR_CIRCLE.jpg") , "COLOR_CIRCLE" );
  
    arrowIcons[10] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/WHITE_ARROW.jpg") , "WHITE_ARROW" );
    arrowIcons[11] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/BLACK_ARROW.jpg") , "BLACK_ARROW" );
    arrowIcons[12] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/COLOR_ARROW.jpg") , "COLOR_ARROW" );
  
    arrowIcons[13] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/WHITE_T.jpg") , "WHITE_T" );
    arrowIcons[14] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/BLACK_T.jpg") , "BLACK_T" );
    arrowIcons[15] = new ImageIcon( getClass().getResource("/cytoscape/images/edgeEnds/COLOR_T.jpg") , "COLOR_T" );
  
    return arrowIcons;
  }


  /**
   *  shape dialog functions:
   *
   *   getStringToShapeByteHashMap()
   *   getShapeByteToStringHashMap()
   *   getShapeIcons()
   *
   */
  public static HashMap getStringToShapeByteHashMap() {
    HashMap h = new HashMap();
    h.put("ELLIPSE", (Object) new Byte (ShapeNodeRealizer.ELLIPSE));
    h.put("ROUND_RECT", (Object) new Byte (ShapeNodeRealizer.ROUND_RECT));
    h.put("RECTANGLE", (Object) new Byte (ShapeNodeRealizer.RECT));
    h.put("DIAMOND", (Object) new Byte (ShapeNodeRealizer.DIAMOND));
    h.put("HEXAGON", (Object) new Byte (ShapeNodeRealizer.HEXAGON));
    h.put("OCTAGON", (Object) new Byte (ShapeNodeRealizer.OCTAGON));
    h.put("PARALLELOGRAM", (Object) new Byte (ShapeNodeRealizer.PARALLELOGRAM));
    h.put("TRIANGLE", (Object) new Byte (ShapeNodeRealizer.TRIANGLE));
    return h;
  }

  public static HashMap getShapeByteToStringHashMap() {
    HashMap h = new HashMap();
    h.put((Object) new Byte (ShapeNodeRealizer.ELLIPSE), "ELLIPSE");
    h.put((Object) new Byte (ShapeNodeRealizer.ROUND_RECT), "ROUND_RECT");
    h.put((Object) new Byte (ShapeNodeRealizer.RECT), "RECTANGLE");
    h.put((Object) new Byte (ShapeNodeRealizer.DIAMOND), "DIAMOND");
    h.put((Object) new Byte (ShapeNodeRealizer.HEXAGON), "HEXAGON");
    h.put((Object) new Byte (ShapeNodeRealizer.OCTAGON), "OCTAGON");
    h.put((Object) new Byte (ShapeNodeRealizer.PARALLELOGRAM), "PARALLELOGRAM");
    h.put((Object) new Byte (ShapeNodeRealizer.TRIANGLE), "TRIANGLE");
    return h;
  }

  public static ImageIcon[] getShapeIcons() {
    ImageIcon [] shapeIcons = new ImageIcon [8];  // Array of icons for the list
    shapeIcons[0] = new ImageIcon(locateImage("ellipse.jpg"),
                                  "ELLIPSE");
    shapeIcons[1] = new ImageIcon(locateImage("round_rect.jpg"),
                                  "ROUND_RECT");
    shapeIcons[2] = new ImageIcon(locateImage("rect.jpg"),
                                  "RECTANGLE");
    shapeIcons[3] = new ImageIcon(locateImage("diamond.jpg"),
                                  "DIAMOND");
    shapeIcons[4] = new ImageIcon(locateImage("hexagon.jpg"),
                                  "HEXAGON");
    shapeIcons[5] = new ImageIcon(locateImage("octagon.jpg"),
                                  "OCTAGON");
    shapeIcons[6] = new ImageIcon(locateImage("parallelogram.jpg"),
                                  "PARALLELOGRAM");
    shapeIcons[7] = new ImageIcon(locateImage("triangle.jpg"),
                                  "TRIANGLE");
    return shapeIcons;
  }



  /**
   *  line type dialog functions:
   *
   *   getStringToLineTypeHashMap()
   *   getLineTypeToStringHashMap()
   *   getLineTypeIcons()
   *
   */
  public static HashMap getStringToLineTypeHashMap() {
    HashMap h = new HashMap();
    h.put("DASHED_1", LineType.DASHED_1);
    h.put("DASHED_2", LineType.DASHED_2);
    h.put("DASHED_3", LineType.DASHED_3);
    h.put("DASHED_4", LineType.DASHED_4);
    h.put("DASHED_5", LineType.DASHED_5);
    h.put("LINE_1", LineType.LINE_1);
    h.put("LINE_2", LineType.LINE_2);
    h.put("LINE_3", LineType.LINE_3);
    h.put("LINE_4", LineType.LINE_4);
    h.put("LINE_5", LineType.LINE_5);
    h.put("LINE_6", LineType.LINE_6);
    h.put("LINE_7", LineType.LINE_7);
    return h;
  }

  public static HashMap getLineTypeToStringHashMap() {
    HashMap h = new HashMap();
    h.put(LineType.DASHED_1, "DASHED_1");
    h.put(LineType.DASHED_2, "DASHED_2");
    h.put(LineType.DASHED_3, "DASHED_3");
    h.put(LineType.DASHED_4, "DASHED_4");
    h.put(LineType.DASHED_5, "DASHED_5");
    h.put(LineType.LINE_1, "LINE_1");
    h.put(LineType.LINE_2, "LINE_2");
    h.put(LineType.LINE_3, "LINE_3");
    h.put(LineType.LINE_4, "LINE_4");
    h.put(LineType.LINE_5, "LINE_5");
    h.put(LineType.LINE_6, "LINE_6");
    h.put(LineType.LINE_7, "LINE_7");
    return h;
  }

  public static ImageIcon[] getLineTypeIcons() {
    ImageIcon [] lineTypeIcons = new ImageIcon [12];  // Array of icons for the list
    lineTypeIcons[0] = new ImageIcon(locateImage("line_1.jpg"),
                                     "LINE_1");
    lineTypeIcons[1] = new ImageIcon(locateImage("line_2.jpg"),
                                     "LINE_2");
    lineTypeIcons[2] = new ImageIcon(locateImage("line_3.jpg"),
                                     "LINE_3");
    lineTypeIcons[3] = new ImageIcon(locateImage("line_4.jpg"),
                                     "LINE_4");
    lineTypeIcons[4] = new ImageIcon(locateImage("line_5.jpg"),
                                     "LINE_5");
    lineTypeIcons[5] = new ImageIcon(locateImage("line_6.jpg"),
                                     "LINE_6");
    lineTypeIcons[6] = new ImageIcon(locateImage("line_7.jpg"),
                                     "LINE_7");
    lineTypeIcons[7] = new ImageIcon(locateImage("dashed_1.jpg"),
                                     "DASHED_1");
    lineTypeIcons[8] = new ImageIcon(locateImage("dashed_2.jpg"),
                                     "DASHED_2");
    lineTypeIcons[9] = new ImageIcon(locateImage("dashed_3.jpg"),
                                     "DASHED_3");
    lineTypeIcons[10] = new ImageIcon(locateImage("dashed_4.jpg"),
                                      "DASHED_4");
    lineTypeIcons[11] = new ImageIcon(locateImage("dashed_5.jpg"),
                                      "DASHED_5");
    return lineTypeIcons;
  }


  /**
   * Get the image from the .jar file
   */
  private static URL locateImage(String imageFilename) {
    // have to construct a dialog to get class from,
    // because otherwise the getClass method is not
    // static, and thus this function can't be static.
    MiscDialog md = new MiscDialog();
    return md.getClass().getResource("images/"+imageFilename);
    //return this.getClass().getClassLoader().getResource(
    //    "cytoscape/dialogs/images/"+imageFilename);
  }

}
