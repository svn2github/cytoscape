
/*
  File: VizUIUtilities.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
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

//--------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------
package cytoscape.visual.ui;
//--------------------------------------------------------------------------------
import java.awt.Color;
import java.awt.Font;

import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
//--------------------------------------------------------------------------------
/**
 * This class provides utility functions for the UI package. Most of these
 * methods involve converting a generic operation on a byte constant specifying
 * the visual attribute type to the corresponding operation specific to the
 * particular attribute.
 *
 * These methods are package-protected because the UI is designed to make sure
 * that the arguments passed to these methods are appropriate.
 */
public class VizUIUtilities {
    
    /**
     * Gets the current default value for the visual attribute
     * specified by the second argument in the visual style specified by the
     * first argument. Returns null if the first argument is null.
     */
    static Object getDefault(VisualStyle style, byte type) {
        if (style == null) {return null;}
        Object defaultObj = null;
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();
        switch (type) {
	case VizMapUI.NODE_COLOR:
	    defaultObj = nodeCalc.getDefaultNodeFillColor();
	    break;
	case VizMapUI.NODE_BORDER_COLOR:
	    defaultObj = nodeCalc.getDefaultNodeBorderColor();
	    break;
	case VizMapUI.NODE_LINETYPE:
	    defaultObj = nodeCalc.getDefaultNodeLineType();
	    break;
	case VizMapUI.NODE_SHAPE:
	    defaultObj = new Byte(nodeCalc.getDefaultNodeShape());
	    break;
	case VizMapUI.NODE_HEIGHT:
	    defaultObj = new Double(nodeCalc.getDefaultNodeHeight());
	    break;
	case VizMapUI.NODE_WIDTH:
	    defaultObj = new Double(nodeCalc.getDefaultNodeWidth());
	    break;
	case VizMapUI.NODE_SIZE:
	    defaultObj = new Double(nodeCalc.getDefaultNodeHeight());
	    break;
	case VizMapUI.NODE_LABEL:
	    defaultObj = nodeCalc.getDefaultNodeLabel();
	    break;
        case VizMapUI.NODE_LABEL_COLOR:
            defaultObj = nodeCalc.getDefaultNodeLabelColor();
            break;
	case VizMapUI.NODE_TOOLTIP:
	    defaultObj = nodeCalc.getDefaultNodeToolTip();
	    break;
	case VizMapUI.EDGE_COLOR:
	    defaultObj = edgeCalc.getDefaultEdgeColor();
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    defaultObj = edgeCalc.getDefaultEdgeLineType();
	    break;
	case VizMapUI.EDGE_SRCARROW:
	    defaultObj = edgeCalc.getDefaultEdgeSourceArrow();
	    break;
	case VizMapUI.EDGE_TGTARROW:
	    defaultObj = edgeCalc.getDefaultEdgeTargetArrow();
	    break;
	case VizMapUI.EDGE_LABEL:
	    defaultObj = edgeCalc.getDefaultEdgeLabel();
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    defaultObj = edgeCalc.getDefaultEdgeToolTip();
	    break;
	case VizMapUI.NODE_FONT_FACE:
	    defaultObj = nodeCalc.getDefaultNodeFont();
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    defaultObj = edgeCalc.getDefaultEdgeFont();
	    break;	  
	case VizMapUI.NODE_FONT_SIZE:
	    defaultObj = new Double(nodeCalc.getDefaultNodeFont().getSize2D());
	    break;
	case VizMapUI.EDGE_FONT_SIZE:
	    defaultObj = new Double(edgeCalc.getDefaultEdgeFont().getSize2D());
	    break;
	}
        return defaultObj;
    }
    
    /**
     * Sets the default value for the visual attribute specified
     * by the second argument in the visual style specified by the first
     * argument. The third argument is the new default value. Returns
     * null if the first or third argument is null.
     */
    static void setDefault(VisualStyle style, byte type, Object c) {
        if (style == null || c == null) {return;}
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();
        switch(type) {
	case VizMapUI.NODE_COLOR:
	    nodeCalc.setDefaultNodeFillColor((Color) c);
	    break;
	case VizMapUI.NODE_BORDER_COLOR:
	    nodeCalc.setDefaultNodeBorderColor((Color) c);
	    break;
	case VizMapUI.NODE_LINETYPE:
	    nodeCalc.setDefaultNodeLineType((LineType) c);
	    break;
	case VizMapUI.NODE_SHAPE:
	    nodeCalc.setDefaultNodeShape(((Byte) c).byteValue());
	    break;
	case VizMapUI.NODE_HEIGHT:
	    nodeCalc.setDefaultNodeHeight(((Double) c).doubleValue());
	    break;
	case VizMapUI.NODE_WIDTH:
	    nodeCalc.setDefaultNodeWidth(((Double) c).doubleValue());
	    break;
	case VizMapUI.NODE_SIZE:
	    nodeCalc.setDefaultNodeHeight(((Double) c).doubleValue());
	    nodeCalc.setDefaultNodeWidth(((Double) c).doubleValue());
	    break;
	case VizMapUI.NODE_LABEL:
	    nodeCalc.setDefaultNodeLabel((String) c);
	    break;
        case VizMapUI.NODE_LABEL_COLOR:
            nodeCalc.setDefaultNodeLabelColor((Color) c);
            break;
	case VizMapUI.NODE_TOOLTIP:
	    nodeCalc.setDefaultNodeToolTip((String) c);
	    break;
	case VizMapUI.EDGE_COLOR:
	    edgeCalc.setDefaultEdgeColor((Color) c);
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    edgeCalc.setDefaultEdgeLineType((LineType) c);
	    break;
	case VizMapUI.EDGE_SRCARROW:
	    edgeCalc.setDefaultEdgeSourceArrow((Arrow) c);
	    break;
	case VizMapUI.EDGE_TGTARROW:
	    edgeCalc.setDefaultEdgeTargetArrow((Arrow) c);
	    break;
	case VizMapUI.EDGE_LABEL:
	    edgeCalc.setDefaultEdgeLabel((String) c);
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    edgeCalc.setDefaultEdgeToolTip((String) c);
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    edgeCalc.setDefaultEdgeFontFace((Font) c);
	    break;
	case VizMapUI.EDGE_FONT_SIZE:
	    edgeCalc.setDefaultEdgeFontSize(((Double) c).floatValue());
	    break;
	case VizMapUI.NODE_FONT_FACE:
	    nodeCalc.setDefaultNodeFontFace((Font) c);
	    break;
	case VizMapUI.NODE_FONT_SIZE:
	    nodeCalc.setDefaultNodeFontSize(((Double) c).floatValue());
	    break;
	}
    }
    
    /**
     * Gets the current calculator for the visual attribute specified by
     * the second argument in the visual style specified by the first argument.
     * This may be null if no calculator is currently specified.
     * Returns null if the first argument is null.
     */
    static Calculator getCurrentCalculator(VisualStyle style, byte type) {
        if (style == null) {return null;}
        Calculator currentCalculator = null;
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();
        switch (type) {
	case VizMapUI.NODE_COLOR:
	    currentCalculator = nodeCalc.getNodeFillColorCalculator();
	    break;
	case VizMapUI.NODE_BORDER_COLOR:
	    currentCalculator = nodeCalc.getNodeBorderColorCalculator();
	    break;
	case VizMapUI.NODE_LINETYPE:
	    currentCalculator = nodeCalc.getNodeLineTypeCalculator();
	    break;
	case VizMapUI.NODE_SHAPE:
	    currentCalculator = nodeCalc.getNodeShapeCalculator();
	    break;
	case VizMapUI.NODE_HEIGHT:
	    currentCalculator = nodeCalc.getNodeHeightCalculator();
	    break;
	case VizMapUI.NODE_WIDTH:
	    currentCalculator = nodeCalc.getNodeWidthCalculator();
	    break;
	case VizMapUI.NODE_SIZE:
	    currentCalculator = nodeCalc.getNodeHeightCalculator();
	    break;
	case VizMapUI.NODE_LABEL:
	    currentCalculator = nodeCalc.getNodeLabelCalculator();
	    break;
        case VizMapUI.NODE_LABEL_COLOR:
            currentCalculator = nodeCalc.getNodeLabelColorCalculator();
            break;
	case VizMapUI.NODE_TOOLTIP:
	    currentCalculator = nodeCalc.getNodeToolTipCalculator();
	    break;
	case VizMapUI.EDGE_COLOR:
	    currentCalculator = edgeCalc.getEdgeColorCalculator();
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    currentCalculator = edgeCalc.getEdgeLineTypeCalculator();
	    break;
	case VizMapUI.EDGE_SRCARROW:
	    currentCalculator = edgeCalc.getEdgeSourceArrowCalculator();
	    break;
	case VizMapUI.EDGE_TGTARROW:
	    currentCalculator = edgeCalc.getEdgeTargetArrowCalculator();
	    break;
	case VizMapUI.EDGE_LABEL:
	    currentCalculator = edgeCalc.getEdgeLabelCalculator();
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    currentCalculator = edgeCalc.getEdgeToolTipCalculator();
	    break;
	case VizMapUI.NODE_FONT_FACE:
	    currentCalculator = nodeCalc.getNodeFontFaceCalculator();
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    currentCalculator = edgeCalc.getEdgeFontFaceCalculator();
	    break;	  
	case VizMapUI.NODE_FONT_SIZE:
	    currentCalculator = nodeCalc.getNodeFontSizeCalculator();
	    break;
	case VizMapUI.EDGE_FONT_SIZE:
	    currentCalculator = edgeCalc.getEdgeFontSizeCalculator();
	    break;
	}
        return currentCalculator;
    }
    
    /**
     * Sets the current calculator for the visual attribute specified by
     * the second argument in the visual style specified by the first argument.
     * The third argument is the new calculator and may be null. This method
     * does nothing if the first argument specifying the visual style is null.
     */
    static void setCurrentCalculator(VisualStyle style, byte type, Calculator c) {
        if (style == null) {return;}
        NodeAppearanceCalculator nodeCalc = style.getNodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeCalc = style.getEdgeAppearanceCalculator();
        switch(type) {
	case VizMapUI.NODE_COLOR:
	    nodeCalc.setNodeFillColorCalculator((NodeColorCalculator) c);
	    break;
	case VizMapUI.NODE_BORDER_COLOR:
	    nodeCalc.setNodeBorderColorCalculator((NodeColorCalculator) c);
	    break;
	case VizMapUI.NODE_LINETYPE:
	    nodeCalc.setNodeLineTypeCalculator((NodeLineTypeCalculator) c);
	    break;
	case VizMapUI.NODE_SHAPE:
	    nodeCalc.setNodeShapeCalculator((NodeShapeCalculator) c);
	    break;
	case VizMapUI.NODE_LABEL:
	    nodeCalc.setNodeLabelCalculator((NodeLabelCalculator) c);
	    break;
        case VizMapUI.NODE_LABEL_COLOR:
            nodeCalc.setNodeLabelColorCalculator((NodeLabelColorCalculator) c);
            break;
	case VizMapUI.NODE_HEIGHT:
	    nodeCalc.setNodeHeightCalculator((NodeSizeCalculator) c);
	    break;
	case VizMapUI.NODE_WIDTH:
	    nodeCalc.setNodeWidthCalculator((NodeSizeCalculator) c);
	    break;
	case VizMapUI.NODE_SIZE:
	    nodeCalc.setNodeWidthCalculator((NodeSizeCalculator) c);
	    nodeCalc.setNodeHeightCalculator((NodeSizeCalculator) c);
	    break;
	case VizMapUI.NODE_TOOLTIP:
	    nodeCalc.setNodeToolTipCalculator((NodeToolTipCalculator) c);
	    break;
	case VizMapUI.EDGE_COLOR:
	    edgeCalc.setEdgeColorCalculator((EdgeColorCalculator) c);
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    edgeCalc.setEdgeLineTypeCalculator((EdgeLineTypeCalculator) c);
	    break;
	case VizMapUI.EDGE_SRCARROW:
	    edgeCalc.setEdgeSourceArrowCalculator((EdgeArrowCalculator) c);
	    break;
	case VizMapUI.EDGE_TGTARROW:
	    edgeCalc.setEdgeTargetArrowCalculator((EdgeArrowCalculator) c);
	    break;
	case VizMapUI.EDGE_LABEL:
	    edgeCalc.setEdgeLabelCalculator((EdgeLabelCalculator) c);
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    edgeCalc.setEdgeToolTipCalculator((EdgeToolTipCalculator) c);
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    edgeCalc.setEdgeFontFaceCalculator((EdgeFontFaceCalculator) c);
	    break;
	case VizMapUI.EDGE_FONT_SIZE:
	    edgeCalc.setEdgeFontSizeCalculator((EdgeFontSizeCalculator) c);
	    break;
	case VizMapUI.NODE_FONT_FACE:
	    nodeCalc.setNodeFontFaceCalculator((NodeFontFaceCalculator) c);
	    break;
	case VizMapUI.NODE_FONT_SIZE:
	    nodeCalc.setNodeFontSizeCalculator((NodeFontSizeCalculator) c);
	    break;
	}
    }
}

