//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import java.awt.Color;
import java.awt.Font;

import y.base.Edge;
import y.view.LineType;
import y.view.Arrow;

import cytoscape.util.Misc;

import cytoscape.visual.calculators.*;
import cytoscape.visual.parsers.FontParser;
//----------------------------------------------------------------------------
/**
 * This class calculates the appearance of an Edge. It holds a default value
 * and a (possibly null) calculator for each visual attribute.
 */
public class EdgeAppearanceCalculator {

    Color defaultEdgeColor = Color.BLACK;
    LineType defaultEdgeLineType = LineType.LINE_1;
    Arrow defaultEdgeSourceArrow = Arrow.NONE;
    Arrow defaultEdgeTargetArrow = Arrow.NONE;
    String defaultEdgeLabel = "";
    String defaultEdgeToolTip = "";
    Font defaultEdgeFont = new Font(null, Font.PLAIN, 10);
    
    EdgeColorCalculator edgeColorCalculator;
    EdgeLineTypeCalculator edgeLineTypeCalculator;
    EdgeArrowCalculator edgeSourceArrowCalculator;
    EdgeArrowCalculator edgeTargetArrowCalculator;
    EdgeLabelCalculator edgeLabelCalculator;
    EdgeToolTipCalculator edgeToolTipCalculator;
    EdgeFontFaceCalculator edgeFontFaceCalculator;
    EdgeFontSizeCalculator edgeFontSizeCalculator;

    public EdgeAppearanceCalculator() {}
    /**
     * Creates a new EdgeAppearanceCalculator and immediately customizes it
     * by calling applyProperties with the supplied arguments.
     */
    public EdgeAppearanceCalculator(String name, Properties eacProps,
                                    String baseKey, CalculatorCatalog catalog) {
        applyProperties(name, eacProps, baseKey, catalog);
    }

    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Edge in the supplied
     * Network. A new EdgeApperance object will be created.
     */
    public EdgeAppearance calculateEdgeAppearance(Edge edge, Network network) {
        EdgeAppearance appr = new EdgeAppearance();
        calculateEdgeAppearance(appr, edge, network);
        return appr;
    }
    
    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Edge in the supplied
     * Network. The supplied EdgeAppearance object will be changed to hold
     * the new values.
     */
    public void calculateEdgeAppearance(EdgeAppearance appr, Edge edge, Network network) {
        appr.setColor( calculateEdgeColor(edge, network) );
        appr.setLineType( calculateEdgeLineType(edge, network) );
        appr.setSourceArrow( calculateEdgeSourceArrow(edge, network) );
        appr.setTargetArrow( calculateEdgeTargetArrow(edge, network) );
        appr.setLabel( calculateEdgeLabel(edge, network) );
        appr.setToolTip( calculateEdgeToolTip(edge, network) );
	appr.setFont( calculateEdgeFont(edge, network) );
    }
    
    
    public Color getDefaultEdgeColor() {return defaultEdgeColor;}
    public void setDefaultEdgeColor(Color c) {
        if (c != null) {defaultEdgeColor = c;}
    }
    public EdgeColorCalculator getEdgeColorCalculator() {return edgeColorCalculator;}
    public void setEdgeColorCalculator(EdgeColorCalculator c) {edgeColorCalculator = c;}
    public Color calculateEdgeColor(Edge edge, Network network) {
        if (edge == null || network == null || edgeColorCalculator == null) {
            return defaultEdgeColor;
        }
        Color c = edgeColorCalculator.calculateEdgeColor(edge, network);
        return (c == null) ? defaultEdgeColor : c;
    }
    
    public LineType getDefaultEdgeLineType() {return defaultEdgeLineType;}
    public void setDefaultEdgeLineType(LineType lt) {
        if (lt != null) {defaultEdgeLineType = lt;}
    }
    public EdgeLineTypeCalculator getEdgeLineTypeCalculator() {
        return edgeLineTypeCalculator;
    }
    public void setEdgeLineTypeCalculator(EdgeLineTypeCalculator c) {
        edgeLineTypeCalculator = c;
    }
    public LineType calculateEdgeLineType(Edge edge, Network network) {
        if (edge == null || network == null || edgeLineTypeCalculator == null) {
            return defaultEdgeLineType;
        }
        LineType lt = edgeLineTypeCalculator.calculateEdgeLineType(edge, network);
        return (lt == null) ? defaultEdgeLineType : lt;
    }
    
    public Arrow getDefaultEdgeSourceArrow() {return defaultEdgeSourceArrow;}
    public void setDefaultEdgeSourceArrow(Arrow a) {
        if (a != null) {defaultEdgeSourceArrow = a;}
    }
    public EdgeArrowCalculator getEdgeSourceArrowCalculator() {
        return edgeSourceArrowCalculator;
    }
    public void setEdgeSourceArrowCalculator(EdgeArrowCalculator c) {
        edgeSourceArrowCalculator = c;
    }
    public Arrow calculateEdgeSourceArrow(Edge edge, Network network) {
        if (edge == null || network == null || edgeSourceArrowCalculator == null) {
            return defaultEdgeSourceArrow;
        }
        Arrow a = edgeSourceArrowCalculator.calculateEdgeArrow(edge, network);
        return (a == null) ? defaultEdgeSourceArrow : a;
    }
    
    public Arrow getDefaultEdgeTargetArrow() {return defaultEdgeTargetArrow;}
    public void setDefaultEdgeTargetArrow(Arrow a) {
        if (a != null) {defaultEdgeTargetArrow = a;}
    }
    public EdgeArrowCalculator getEdgeTargetArrowCalculator() {
        return edgeTargetArrowCalculator;
    }
    public void setEdgeTargetArrowCalculator(EdgeArrowCalculator c) {
        edgeTargetArrowCalculator = c;
    }
    public Arrow calculateEdgeTargetArrow(Edge edge, Network network) {
        if (edge == null || network == null || edgeTargetArrowCalculator == null) {
            return defaultEdgeTargetArrow;
        }
        Arrow a = edgeTargetArrowCalculator.calculateEdgeArrow(edge, network);
        return (a == null) ? defaultEdgeTargetArrow : a;
    }
    
    public String getDefaultEdgeLabel() {return defaultEdgeLabel;}
    public void setDefaultEdgeLabel(String s) {
        if (s != null) {defaultEdgeLabel = s;}
    }
    public EdgeLabelCalculator getEdgeLabelCalculator() {return edgeLabelCalculator;}
    public void setEdgeLabelCalculator(EdgeLabelCalculator c) {edgeLabelCalculator = c;}
    public String calculateEdgeLabel(Edge edge, Network network) {
        if (edge == null || network == null || edgeLabelCalculator == null) {
            return defaultEdgeLabel;
        }
        String s = edgeLabelCalculator.calculateEdgeLabel(edge, network);
        return (s == null) ? defaultEdgeLabel : s;
    }
        
    public Font getDefaultEdgeFont() {return defaultEdgeFont;}
    public void setDefaultEdgeFont(Font f) {
	if (f != null) {defaultEdgeFont = f;}
    }
    public Font getDefaultEdgeFontFace() {return defaultEdgeFont;}
    public void setDefaultEdgeFontFace(Font f) {
	if (f != null) {
	    float fontSize = defaultEdgeFont.getSize2D();
	    defaultEdgeFont = f.deriveFont(fontSize);
	}
    }
    public EdgeFontFaceCalculator getEdgeFontFaceCalculator() {return edgeFontFaceCalculator;}
    public void setEdgeFontFaceCalculator(EdgeFontFaceCalculator c) {edgeFontFaceCalculator = c;}
    

    public float getDefaultEdgeFontSize() {return defaultEdgeFont.getSize2D();}
    public void setDefaultEdgeFontSize(float f) {
	if (f > 0.0) defaultEdgeFont = defaultEdgeFont.deriveFont(f);
    }
    public EdgeFontSizeCalculator getEdgeFontSizeCalculator() {return edgeFontSizeCalculator;}
    public void setEdgeFontSizeCalculator(EdgeFontSizeCalculator c) {edgeFontSizeCalculator = c;}
    
    public Font calculateEdgeFont(Edge edge, Network network) {
	if (edge == null || network == null ||
	    (edgeFontFaceCalculator == null && edgeFontSizeCalculator == null)) {
	    return defaultEdgeFont;
	}
	Font f;
	if (edgeFontFaceCalculator == null) { // edgeFontSizeCalculator != null
	    f = defaultEdgeFont.deriveFont(edgeFontSizeCalculator.calculateEdgeFontSize(edge, network));
	}
	else { // edgeFontSizeCalculator == null
	    float defaultSize = defaultEdgeFont.getSize2D();
	    f = edgeFontFaceCalculator.calculateEdgeFontFace(edge, network).deriveFont(defaultSize);
	}
	return f;
    }

    public String getDefaultEdgeToolTip() {return defaultEdgeToolTip;}
    public void setDefaultEdgeToolTip(String s) {
        if (s != null) {defaultEdgeToolTip = s;}
    }
    public EdgeToolTipCalculator getEdgeToolTipCalculator() {
        return edgeToolTipCalculator;
    }
    public void setEdgeToolTipCalculator(EdgeToolTipCalculator c) {
        edgeToolTipCalculator = c;
    }
    public String calculateEdgeToolTip(Edge edge, Network network) {
        if (edge == null || network == null || edgeToolTipCalculator == null) {
            return defaultEdgeToolTip;
        }
        String s = edgeToolTipCalculator.calculateEdgeToolTip(edge, network);
        return (s == null) ? defaultEdgeToolTip : s;
    }
    
    /**
     * Returns a text description of the current default values and calculator
     * names.
     */
    public String getDescription() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("EdgeAppearanceCalculator:" + lineSep);
        sb.append("defaultEdgeColor = ").append(defaultEdgeColor).append(lineSep);
        String edgeLineTypeText = Misc.getLineTypeText(defaultEdgeLineType);
        sb.append("defaultEdgeLineType = ").append(edgeLineTypeText).append(lineSep);
        String sourceArrowText = Misc.getArrowText(defaultEdgeSourceArrow);
        sb.append("defaultEdgeSourceArrow = ").append(sourceArrowText).append(lineSep);
        String targetArrowText = Misc.getArrowText(defaultEdgeTargetArrow);
        sb.append("defaultEdgeTargetArrow = ").append(targetArrowText).append(lineSep);
        sb.append("defaultEdgeLabel = ").append(defaultEdgeLabel).append(lineSep);
        sb.append("defaultEdgeToolTip = ").append(defaultEdgeToolTip).append(lineSep);
        sb.append("defaultEdgeFont = ").append(defaultEdgeFont).append(lineSep);
        sb.append("edgeColorCalculator = ").append(edgeColorCalculator).append(lineSep);
        sb.append("edgeLineTypeCalculator = ").append(edgeLineTypeCalculator).append(lineSep);
        sb.append("edgeSourceArrowCalculator = ").append(edgeSourceArrowCalculator).append(lineSep);
        sb.append("edgeTargetCalculator = ").append(edgeTargetArrowCalculator).append(lineSep);
        sb.append("edgeLabelCalculator = ").append(edgeLabelCalculator).append(lineSep);
        sb.append("edgeToolTipCalculator = ").append(edgeToolTipCalculator).append(lineSep);
        sb.append("edgeFontFaceCalculator = ").append(edgeFontFaceCalculator).append(lineSep);
        sb.append("edgeFontSizeCalculator = ").append(edgeFontSizeCalculator).append(lineSep);
        return sb.toString();
    }
    
    /**
     * This method customizes this object by searching the supplied properties
     * object for keys identifying default values and calculators. Recognized
     * keys are of the form "nodeAppearanceCalculator." + name + ident, where
     * name is a supplied argument and ident is a String indicating a default
     * value or a calculator for a specific visual attribute. The specified
     * calculators are aquired by name from the supplied catalog.
     */
    public void applyProperties(String name, Properties eacProps, String baseKey,
                                CalculatorCatalog catalog) {
        String value = null;
        
        //look for default values
        value = eacProps.getProperty(baseKey + ".defaultEdgeColor");
        if (value != null) {
            Color c = Misc.parseRGBText(value);
            if (c != null) {setDefaultEdgeColor(c);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeLineType");
        if (value != null) {
            LineType lt = Misc.parseLineTypeText(value);
            if (lt != null) {setDefaultEdgeLineType(lt);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeSourceArrow");
        if (value != null) {
            Arrow a = Misc.parseArrowText(value);
            if (a != null) {setDefaultEdgeSourceArrow(a);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeTargetArrow");
        if (value != null) {
            Arrow a = Misc.parseArrowText(value);
            if (a != null) {setDefaultEdgeTargetArrow(a);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeLabel");
        if (value != null) {
            setDefaultEdgeLabel(value);
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeToolTip");
        if (value != null) {
            setDefaultEdgeToolTip(value);
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeFont");
        if (value != null) {
	    FontParser parser = new FontParser();
	    Font f = parser.parseFont(value);
            if (f != null) {
		setDefaultEdgeFont(f);
	    }
        }
        
        //look for calculators
        value = eacProps.getProperty(baseKey + ".edgeColorCalculator");
        if (value != null) {
            EdgeColorCalculator c = catalog.getEdgeColorCalculator(value);
            if (c != null) {setEdgeColorCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeLineTypeCalculator");
        if (value != null) {
            EdgeLineTypeCalculator c = catalog.getEdgeLineTypeCalculator(value);
            if (c != null) {setEdgeLineTypeCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeSourceArrowCalculator");
        if (value != null) {
            EdgeArrowCalculator c = catalog.getEdgeArrowCalculator(value);
            if (c != null) {setEdgeSourceArrowCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeTargetArrowCalculator");
        if (value != null) {
            EdgeArrowCalculator c = catalog.getEdgeArrowCalculator(value);
            if (c != null) {setEdgeTargetArrowCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeLabelCalculator");
        if (value != null) {
            EdgeLabelCalculator c = catalog.getEdgeLabelCalculator(value);
            if (c != null) {setEdgeLabelCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeToolTipCalculator");
        if (value != null) {
            EdgeToolTipCalculator c = catalog.getEdgeToolTipCalculator(value);
            if (c != null) {setEdgeToolTipCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeFontFaceCalculator");
        if (value != null) {
            EdgeFontFaceCalculator c = catalog.getEdgeFontFaceCalculator(value);
            if (c != null) {setEdgeFontFaceCalculator(c);}
        }
	value = eacProps.getProperty(baseKey + ".edgeFontSizeCalculator");
	if (value != null) {
	    EdgeFontSizeCalculator c = catalog.getEdgeFontSizeCalculator(value);
	    if (c != null) {setEdgeFontSizeCalculator(c);}
	}
    }
}

