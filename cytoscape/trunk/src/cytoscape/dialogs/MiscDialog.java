// MiscDialog.java:  miscellaneous static utilities
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.dialogs;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.awt.Color;
import java.awt.Polygon;
import java.util.*;
import java.net.URL;

import javax.swing.*;

import y.view.Arrow;
import y.view.LineType;
import y.view.ShapeNodeRealizer;

import cytoscape.util.Misc;

//------------------------------------------------------------------------------
public class MiscDialog {

    /**
     *  arrow dialog functions:
     *
     *   getStringToArrowHashMap(int nodeSize)
     *   getArrowToStringHashMap(int nodeSize)
     *   getArrowIcons()
     *
     */
    public static HashMap getStringToArrowHashMap(int nodeSize) {
	HashMap h = new HashMap();
	h.put("DELTA", (Object)Arrow.DELTA);
	h.put("DIAMOND", (Object)Arrow.DIAMOND);
	h.put("STANDARD", (Object)Arrow.STANDARD);
	h.put("SHORT", (Object)Arrow.SHORT);
	h.put("WHITE_DELTA", (Object)Arrow.WHITE_DELTA);
	h.put("WHITE_DIAMOND", (Object)Arrow.WHITE_DIAMOND);
	h.put("SCALABLE",
	      (Object)Misc.parseArrowText("scalableArrow" +
					  Integer.toString((int)(nodeSize/2))));
	return h;
    }

    public static HashMap getArrowToStringHashMap(int nodeSize) {
	HashMap h = new HashMap();
	h.put((Object)Arrow.DELTA, "DELTA");
	h.put((Object)Arrow.DIAMOND, "DIAMOND");
	h.put((Object)Arrow.STANDARD, "STANDARD");
	h.put((Object)Arrow.SHORT, "SHORT");
	h.put((Object)Arrow.WHITE_DELTA, "WHITE_DELTA");
	h.put((Object)Arrow.WHITE_DIAMOND, "WHITE_DIAMOND");
	h.put((Object)Misc.parseArrowText("scalableArrow" +
					  Integer.toString((int)(nodeSize/2))),
	      "SCALABLE");
	return h;
    }

    public ImageIcon[] getArrowIcons() {
	String p = getDialogImagePath();
	if(p==null) return new ImageIcon [0];

	ImageIcon [] arrowIcons = new ImageIcon [7];

	// THIS SHOULD WORK!
	System.out.println("Yo!: "+locateImage("arrow_delta.jpg"));
	arrowIcons[0] = new ImageIcon(p+"arrow_delta.jpg", "DELTA");
	arrowIcons[1] = new ImageIcon(p+"arrow_diamond.jpg", "DIAMOND");
	arrowIcons[2] = new ImageIcon(p+"arrow_standard.jpg", "STANDARD");
	arrowIcons[3] = new ImageIcon(p+"arrow_short.jpg", "SHORT");
	arrowIcons[4] = new ImageIcon(p+"arrow_whitediamond.jpg", "WHITE_DIAMOND");
	arrowIcons[5] = new ImageIcon(p+"arrow_whitedelta.jpg", "WHITE_DELTA");
	arrowIcons[6] = new ImageIcon(p+"arrow_scalable.jpg", "SCALABLE");

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
	h.put("RECTANGLE", (Object) new Byte (ShapeNodeRealizer.RECT));
	h.put("DIAMOND", (Object) new Byte (ShapeNodeRealizer.DIAMOND));
	h.put("ELLIPSE", (Object) new Byte (ShapeNodeRealizer.ELLIPSE));
	h.put("HEXAGON", (Object) new Byte (ShapeNodeRealizer.HEXAGON));
	h.put("TRAPEZOID", (Object) new Byte (ShapeNodeRealizer.TRAPEZOID));
	h.put("TRIANGLE", (Object) new Byte (ShapeNodeRealizer.TRIANGLE));
	return h;
    }

    public static HashMap getShapeByteToStringHashMap() {
	HashMap h = new HashMap();
	h.put((Object) new Byte (ShapeNodeRealizer.RECT), "RECTANGLE");
	h.put((Object) new Byte (ShapeNodeRealizer.DIAMOND), "DIAMOND");
	h.put((Object) new Byte (ShapeNodeRealizer.ELLIPSE), "ELLIPSE");
	h.put((Object) new Byte (ShapeNodeRealizer.HEXAGON), "HEXAGON");
	h.put((Object) new Byte (ShapeNodeRealizer.TRAPEZOID), "TRAPEZOID");
	h.put((Object) new Byte (ShapeNodeRealizer.TRIANGLE), "TRIANGLE");
	return h;
    }

    public ImageIcon[] getShapeIcons() {
	String p = getDialogImagePath();
	if(p==null) return new ImageIcon [0];

	ImageIcon [] shapeIcons = new ImageIcon [6];  // Array of icons for the list
	shapeIcons[0] = new ImageIcon(p+"rectangle.jpg", "RECTANGLE");
	shapeIcons[1] = new ImageIcon(p+"diamond.jpg", "DIAMOND");
	shapeIcons[2] = new ImageIcon(p+"ellipse.jpg", "ELLIPSE");
	shapeIcons[3] = new ImageIcon(p+"hexagon.jpg", "HEXAGON");
	shapeIcons[4] = new ImageIcon(p+"trapezoid.jpg", "TRAPEZOID");
	shapeIcons[5] = new ImageIcon(p+"triangle.jpg", "TRIANGLE");

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

    public ImageIcon[] getLineTypeIcons() {
	String p = getDialogImagePath();
	if(p==null) return new ImageIcon [0];

	ImageIcon [] lineTypeIcons = new ImageIcon [12];  // Array of icons for the list
	lineTypeIcons[0] = new ImageIcon(p+"line_1.jpg", "LINE_1");
	lineTypeIcons[1] = new ImageIcon(p+"line_2.jpg", "LINE_2");
	lineTypeIcons[2] = new ImageIcon(p+"line_3.jpg", "LINE_3");
	lineTypeIcons[3] = new ImageIcon(p+"line_4.jpg", "LINE_4");
	lineTypeIcons[4] = new ImageIcon(p+"line_5.jpg", "LINE_5");
	lineTypeIcons[5] = new ImageIcon(p+"line_6.jpg", "LINE_6");
	lineTypeIcons[6] = new ImageIcon(p+"line_7.jpg", "LINE_7");
	lineTypeIcons[7] = new ImageIcon(p+"dashed_1.jpg", "DASHED_1");
	lineTypeIcons[8] = new ImageIcon(p+"dashed_2.jpg", "DASHED_2");
	lineTypeIcons[9] = new ImageIcon(p+"dashed_3.jpg", "DASHED_3");
	lineTypeIcons[10] = new ImageIcon(p+"dashed_4.jpg", "DASHED_4");
	lineTypeIcons[11] = new ImageIcon(p+"dashed_5.jpg", "DASHED_5");
	return lineTypeIcons;
    }

    
    private static String getRootPath() {
	String path = System.getProperty ("CYTOSCAPE_HOME");
	return path;
    }

    private static String getDialogImagePath() {
	String path = getRootPath();
	if(path==null) return null;
	else {
	    if (path.endsWith("/")) path = path.substring(0,path.length()-1);
	    path = path + "/cytoscape/dialogs/images/";
	    return path;
	}
    }


    /**
     * Get the image from the .jar file
     */
    private URL locateImage(String imageFilename) {
	return this.getClass().getClassLoader().getResource(imageFilename);
    }
}
