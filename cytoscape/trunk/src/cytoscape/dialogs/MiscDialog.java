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

import javax.swing.*;

import y.view.Arrow;
import y.view.LineType;
import y.view.ShapeNodeRealizer;

import cytoscape.util.Misc;

//------------------------------------------------------------------------------
public class MiscDialog {

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

    public static ImageIcon[] getArrowIcons() {
	String p = getDialogImagePath();
	if(p==null) return new ImageIcon [0];

	ImageIcon [] arrowIcons = new ImageIcon [7];
	arrowIcons[0] = new ImageIcon(p+"arrow_delta.jpg", "DELTA");
	arrowIcons[1] = new ImageIcon(p+"arrow_diamond.jpg", "DIAMOND");
	arrowIcons[2] = new ImageIcon(p+"arrow_standard.jpg", "STANDARD");
	arrowIcons[3] = new ImageIcon(p+"arrow_short.jpg", "SHORT");
	arrowIcons[4] = new ImageIcon(p+"arrow_whitediamond.jpg", "WHITE_DIAMOND");
	arrowIcons[5] = new ImageIcon(p+"arrow_whitedelta.jpg", "WHITE_DELTA");
	arrowIcons[6] = new ImageIcon(p+"arrow_scalable.jpg", "SCALABLE");

	return arrowIcons;
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



}
