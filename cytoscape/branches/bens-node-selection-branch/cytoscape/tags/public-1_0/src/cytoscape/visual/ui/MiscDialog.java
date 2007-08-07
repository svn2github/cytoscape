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

import y.view.Arrow;
import y.view.LineType;
import y.view.ShapeNodeRealizer;

import cytoscape.util.Misc;
import cytoscape.vizmap.*;

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
	h.put("NONE", (Object)Arrow.NONE);
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
	h.put((Object)Arrow.NONE, "NONE");
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

    public static ImageIcon[] getArrowIcons() {
	ImageIcon [] arrowIcons = new ImageIcon [8];

	arrowIcons[0] = new ImageIcon(locateImage("arrow_none.jpg"),
				      "NONE");
	arrowIcons[1] = new ImageIcon(locateImage("arrow_delta.jpg"),
				      "DELTA");
	arrowIcons[2] = new ImageIcon(locateImage("arrow_diamond.jpg"),
				      "DIAMOND");
	arrowIcons[3] = new ImageIcon(locateImage("arrow_standard.jpg"),
				      "STANDARD");
	arrowIcons[4] = new ImageIcon(locateImage("arrow_short.jpg"),
				      "SHORT");
	arrowIcons[5] = new ImageIcon(locateImage("arrow_whitediamond.jpg"),
				      "WHITE_DIAMOND");
	arrowIcons[6] = new ImageIcon(locateImage("arrow_whitedelta.jpg"),
				      "WHITE_DELTA");
	arrowIcons[7] = new ImageIcon(locateImage("arrow_scalable.jpg"),
				      "SCALABLE");
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
	h.put("RECT_3D", (Object) new Byte (ShapeNodeRealizer.RECT_3D));
	h.put("RECTANGLE", (Object) new Byte (ShapeNodeRealizer.RECT));
	h.put("DIAMOND", (Object) new Byte (ShapeNodeRealizer.DIAMOND));
	h.put("HEXAGON", (Object) new Byte (ShapeNodeRealizer.HEXAGON));
	h.put("OCTAGON", (Object) new Byte (ShapeNodeRealizer.OCTAGON));
	h.put("TRAPEZOID", (Object) new Byte (ShapeNodeRealizer.TRAPEZOID));
	h.put("TRAPEZOID_2", (Object) new Byte (ShapeNodeRealizer.TRAPEZOID_2));
	h.put("PARALLELOGRAM", (Object) new Byte (ShapeNodeRealizer.PARALLELOGRAM));
	h.put("TRIANGLE", (Object) new Byte (ShapeNodeRealizer.TRIANGLE));
	return h;
    }

    public static HashMap getShapeByteToStringHashMap() {
	HashMap h = new HashMap();
	h.put((Object) new Byte (ShapeNodeRealizer.ELLIPSE), "ELLIPSE");
	h.put((Object) new Byte (ShapeNodeRealizer.ROUND_RECT), "ROUND_RECT");
	h.put((Object) new Byte (ShapeNodeRealizer.RECT_3D), "RECT_3D");
	h.put((Object) new Byte (ShapeNodeRealizer.RECT), "RECTANGLE");
	h.put((Object) new Byte (ShapeNodeRealizer.DIAMOND), "DIAMOND");
	h.put((Object) new Byte (ShapeNodeRealizer.HEXAGON), "HEXAGON");
	h.put((Object) new Byte (ShapeNodeRealizer.OCTAGON), "OCTAGON");
	h.put((Object) new Byte (ShapeNodeRealizer.TRAPEZOID), "TRAPEZOID");
	h.put((Object) new Byte (ShapeNodeRealizer.TRAPEZOID_2), "TRAPEZOID_2");
	h.put((Object) new Byte (ShapeNodeRealizer.PARALLELOGRAM), "PARALLELOGRAM");
	h.put((Object) new Byte (ShapeNodeRealizer.TRIANGLE), "TRIANGLE");
	return h;
    }

    public static ImageIcon[] getShapeIcons() {
	ImageIcon [] shapeIcons = new ImageIcon [11];  // Array of icons for the list
	shapeIcons[0] = new ImageIcon(locateImage("ellipse.jpg"),
				      "ELLIPSE");
	shapeIcons[1] = new ImageIcon(locateImage("round_rect.jpg"),
				      "ROUND_RECT");
	shapeIcons[2] = new ImageIcon(locateImage("rect_3d.jpg"),
				      "RECT_3D");
	shapeIcons[3] = new ImageIcon(locateImage("rect.jpg"),
				      "RECTANGLE");
	shapeIcons[4] = new ImageIcon(locateImage("diamond.jpg"),
				      "DIAMOND");
	shapeIcons[5] = new ImageIcon(locateImage("hexagon.jpg"),
				      "HEXAGON");
	shapeIcons[6] = new ImageIcon(locateImage("octagon.jpg"),
				      "OCTAGON");
	shapeIcons[7] = new ImageIcon(locateImage("trapezoid.jpg"),
				      "TRAPEZOID");
	shapeIcons[8] = new ImageIcon(locateImage("trapezoid_2.jpg"),
				      "TRAPEZOID_2");
	shapeIcons[9] = new ImageIcon(locateImage("parallelogram.jpg"),
				      "PARALLELOGRAM");
	shapeIcons[10] = new ImageIcon(locateImage("triangle.jpg"),
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

    /**
     *  The main dialog and its tabs use this to sort through
     *  the vectors of backup mappers.
     */
    public static Integer findCorrectMapperIndex(Vector v, Class c) {
	Integer correctIndex = null;
	for (int tempIndex=0;tempIndex<v.size();tempIndex++) {
	    MapperAndAttribute maa = (MapperAndAttribute)v.get(tempIndex);
	    if(maa!=null) {
		ValueMapper vm = (ValueMapper)maa.mapper;
		if(vm!=null)
		    if(vm.getClass().equals(c))
			correctIndex = new Integer(tempIndex);
	    }
	}
	return correctIndex;
    }

}
