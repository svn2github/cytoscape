
/*
  File: EdgeAppearance.java 
  
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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.Paint;
import cytoscape.visual.LineType;
import cytoscape.visual.Arrow;
import cytoscape.visual.parsers.ObjectToString;
import cytoscape.visual.parsers.ArrowParser;
import cytoscape.visual.parsers.FontParser;
import cytoscape.visual.parsers.LineTypeParser;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.ui.VizMapUI;

import giny.view.EdgeView;
import giny.view.Label;
import java.util.Properties;
//----------------------------------------------------------------------------
/**
 * Objects of this class hold data describing the appearance of an Edge.
 */
public class EdgeAppearance implements Appearance, Cloneable {
    
    Color color = Color.BLACK;
    LineType lineType = LineType.LINE_1;
    Arrow sourceArrow = Arrow.NONE;
    Arrow targetArrow = Arrow.NONE;
    String label = "";
    String toolTip = "";
    Font font = new Font(null, Font.PLAIN, 10);

    public EdgeAppearance() {}
    
    public Color getColor() {return color;}
    public void setColor(Color c) {if (c != null ) color = c;}
    
    public LineType getLineType() {return lineType;}
    public void setLineType(LineType lt) {if (lt != null ) lineType = lt;}
    
    public Arrow getSourceArrow() {return sourceArrow;}
    public void setSourceArrow(Arrow a) {if (a != null ) sourceArrow = a;}
    
    public Arrow getTargetArrow() {return targetArrow;}
    public void setTargetArrow(Arrow a) {if (a != null ) targetArrow = a;}
    
    public String getLabel() {return label;}
    public void setLabel(String s) {if (s != null ) label = s;}
    
    public String getToolTip() {return toolTip;}
    public void setToolTip(String s) {if (s != null ) toolTip = s;}

    public Font getFont() {return font;}
    public void setFont(Font f) {if (f != null ) font = f;}

    public float getFontSize() {return (float)font.getSize2D();}
    public void setFontSize(float f) { font = font.deriveFont(f); }


    public void applyAppearance(EdgeView edgeView, boolean optimizer) {

	if (optimizer == false) {
		edgeView.setUnselectedPaint(getColor());
		edgeView.setStroke(getLineType().getStroke());
		edgeView.setSourceEdgeEnd(getSourceArrow()
				.getGinyArrow());
		edgeView.setTargetEdgeEnd(getTargetArrow()
				.getGinyArrow());
		Label label = edgeView.getLabel();
		label.setText(getLabel());
		label.setFont(getFont());
	} else {
		Paint existingUnselectedPaint = edgeView.getUnselectedPaint();
		Paint newUnselectedPaint = getColor();
		if (!newUnselectedPaint.equals(existingUnselectedPaint)) {
			edgeView.setUnselectedPaint(newUnselectedPaint);
		}
		Stroke existingStroke = edgeView.getStroke();
		Stroke newStroke = getLineType().getStroke();
		if (!newStroke.equals(existingStroke)) {
			edgeView.setStroke(newStroke);
		}

		int existingSourceEdge = edgeView.getSourceEdgeEnd();
		int newSourceEdge = getSourceArrow().getGinyArrow();
		if (newSourceEdge != existingSourceEdge) {
			edgeView.setSourceEdgeEnd(newSourceEdge);
		}

		int existingTargetEdge = edgeView.getTargetEdgeEnd();
		int newTargetEdge = getTargetArrow().getGinyArrow();
		if (newTargetEdge != existingTargetEdge) {
			edgeView.setTargetEdgeEnd(newTargetEdge);
		}

		Label label = edgeView.getLabel();
		String existingText = label.getText();
		String newText = getLabel();
		if (!newText.equals(existingText)) {
			label.setText(newText);
		}
		Font existingFont = label.getFont();
		Font newFont = getFont();
		if (!newFont.equals(existingFont)) {
			label.setFont(newFont);
		}
	}
    }


    
    public void applyDefaultProperties(Properties eacProps, String baseKey ) {

        String value = null;
        
        //look for default values
        value = eacProps.getProperty(baseKey + ".defaultEdgeColor");
        if (value != null) {
            Color c = (new ColorParser()).parseColor(value);
            if (c != null) {setColor(c);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeLineType");
        if (value != null) {
            LineType lt = (new LineTypeParser()).parseLineType(value);
            if (lt != null) {setLineType(lt);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeSourceArrow");
        if (value != null) {
            Arrow a = (new ArrowParser()).parseArrow(value);
            if (a != null) {setSourceArrow(a);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeTargetArrow");
        if (value != null) {
            Arrow a = (new ArrowParser()).parseArrow(value);
            if (a != null) {setTargetArrow(a);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeLabel");
        if (value != null) {
            setLabel(value);
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeToolTip");
        if (value != null) {
            setToolTip(value);
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeFont");
        if (value != null) {
	    Font f = (new FontParser()).parseFont(value);
            if (f != null) {
		setFont(f);
	    }
        }
    }
    
    public Properties getDefaultProperties(String baseKey) {
        String key = null;
        String value = null;
        Properties newProps = new Properties();
        
        //save default values
        key = baseKey + ".defaultEdgeColor";
        value = ObjectToString.getStringValue( getColor() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeLineType";
        value = ObjectToString.getStringValue( getLineType() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeSourceArrow";
        value = ObjectToString.getStringValue( getSourceArrow() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeTargetArrow";
        value = ObjectToString.getStringValue( getTargetArrow() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeLabel";
        value = ObjectToString.getStringValue( getLabel() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeToolTip";
        value = ObjectToString.getStringValue( getToolTip() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeFont";
        value = ObjectToString.getStringValue( getFont() );
        newProps.setProperty(key, value);
        
        return newProps;
    }

    public String getDescription(String prefix) {
        if ( prefix == null )
		prefix = "";

        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append(prefix + "EdgeColor = ").append(color).append(lineSep);
        String edgeLineTypeText = ObjectToString.getStringValue(lineType);
        sb.append(prefix + "EdgeLineType = ").append(edgeLineTypeText).append(lineSep);
        String sourceArrowText = ObjectToString.getStringValue(sourceArrow);
        sb.append(prefix + "EdgeSourceArrow = ").append(sourceArrowText).append(lineSep);
        String targetArrowText = ObjectToString.getStringValue(targetArrow);
        sb.append(prefix + "EdgeTargetArrow = ").append(targetArrowText).append(lineSep);
        sb.append(prefix + "EdgeLabel = ").append(label).append(lineSep);
        sb.append(prefix + "EdgeToolTip = ").append(toolTip).append(lineSep);
        sb.append(prefix + "EdgeFont = ").append(font).append(lineSep);

	return sb.toString();
    }
    public String getDescription() {
    	return getDescription(null);
    }


    public Object get(byte type) {
	Object defaultObj = null;
        switch (type) {
	case VizMapUI.EDGE_COLOR:
	    defaultObj = getColor();
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    defaultObj = getLineType();
	    break;
	case VizMapUI.EDGE_SRCARROW:
	    defaultObj = getSourceArrow();
	    break;
	case VizMapUI.EDGE_TGTARROW:
	    defaultObj = getTargetArrow();
	    break;
	case VizMapUI.EDGE_LABEL:
	    defaultObj = getLabel();
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    defaultObj = getToolTip();
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    defaultObj = getFont();
	    break;	  
	case VizMapUI.EDGE_FONT_SIZE:
	    defaultObj = new Double(getFont().getSize2D());
	    break;
	}
        return defaultObj;
    }
    
    public void set(byte type, Object c) {
        switch(type) {
	case VizMapUI.EDGE_COLOR:
	    setColor((Color) c);
	    break;
	case VizMapUI.EDGE_LINETYPE:
	    setLineType((LineType) c);
	    break;
	case VizMapUI.EDGE_SRCARROW:
	    setSourceArrow((Arrow) c);
	    break;
	case VizMapUI.EDGE_TGTARROW:
	    setTargetArrow((Arrow) c);
	    break;
	case VizMapUI.EDGE_LABEL:
	    setLabel((String) c);
	    break;
	case VizMapUI.EDGE_TOOLTIP:
	    setToolTip((String) c);
	    break;
	case VizMapUI.EDGE_FONT_FACE:
	    setFont((Font) c);
	    break;
	case VizMapUI.EDGE_FONT_SIZE:
	    setFontSize(((Double) c).floatValue());
	    break;
	}
    }

    public void copy(EdgeAppearance ea) {
    	setColor( ea.getColor() );
    	setLineType( ea.getLineType() );
    	setSourceArrow( ea.getSourceArrow() );
    	setTargetArrow( ea.getTargetArrow() );
    	setLabel( ea.getLabel() );
    	setToolTip( ea.getToolTip() );
    	setFont( ea.getFont() );
    }

    public Object clone() {
    	EdgeAppearance ea = new EdgeAppearance();
	ea.setColor(color);
	ea.setLineType(lineType);
	ea.setSourceArrow(sourceArrow); 
	ea.setTargetArrow(targetArrow );
	ea.setLabel(label );
	ea.setToolTip(toolTip);
	ea.setFont(font);
	return ea;
    }
} 
