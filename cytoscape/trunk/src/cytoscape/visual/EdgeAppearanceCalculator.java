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

import cytoscape.visual.calculators.*;
import cytoscape.visual.parsers.*;
//----------------------------------------------------------------------------
/**
 * This class calculates the appearance of an Edge. It holds a default value
 * and a (possibly null) calculator for each visual attribute.
 */
public class EdgeAppearanceCalculator implements Cloneable {

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

    /**
     * Make shallow copy of this object
     */
    public Object clone() throws CloneNotSupportedException {
	Object copy = null;
	copy = super.clone();
	return copy;
    }

    public EdgeAppearanceCalculator() {}
    
    /**
     * Copy constructor. Returns a default object if the argument is null.
     */
    public EdgeAppearanceCalculator(EdgeAppearanceCalculator toCopy) {
        if (toCopy == null) {return;}
        
        setDefaultEdgeColor( toCopy.getDefaultEdgeColor() );
        setDefaultEdgeLineType( toCopy.getDefaultEdgeLineType() );
        setDefaultEdgeSourceArrow( toCopy.getDefaultEdgeSourceArrow() );
        setDefaultEdgeTargetArrow( toCopy.getDefaultEdgeTargetArrow() );
        setDefaultEdgeLabel( toCopy.getDefaultEdgeLabel() );
        setDefaultEdgeToolTip( toCopy.getDefaultEdgeToolTip() );
        setDefaultEdgeFont( toCopy.getDefaultEdgeFont() );
        
        setEdgeColorCalculator( toCopy.getEdgeColorCalculator() );
        setEdgeLineTypeCalculator( toCopy.getEdgeLineTypeCalculator() );
        setEdgeSourceArrowCalculator( toCopy.getEdgeSourceArrowCalculator() );
        setEdgeTargetArrowCalculator( toCopy.getEdgeTargetArrowCalculator() );
        setEdgeLabelCalculator( toCopy.getEdgeLabelCalculator() );
        setEdgeToolTipCalculator( toCopy.getEdgeToolTipCalculator() );
        setEdgeFontFaceCalculator( toCopy.getEdgeFontFaceCalculator() );
        setEdgeFontSizeCalculator( toCopy.getEdgeFontSizeCalculator() );
    }
    
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
	float defaultSize = defaultEdgeFont.getSize2D();
	if (edgeFontFaceCalculator == null) { // edgeFontSizeCalculator != null
	    float fontSize = edgeFontSizeCalculator.calculateEdgeFontSize(edge, network);
	    if (fontSize == -1)
		fontSize = defaultSize;
	    f = defaultEdgeFont.deriveFont(fontSize);
	    return (f == null) ? defaultEdgeFont : f;
	}
	else {
	    Font g = edgeFontFaceCalculator.calculateEdgeFontFace(edge, network);
	    if (g == null) {
		g = defaultEdgeFont;
	    }
	    if (edgeFontSizeCalculator == null) {
		f = g.deriveFont(defaultSize);
            } else {
		float fontSize = edgeFontSizeCalculator.calculateEdgeFontSize(edge, network);
		if (fontSize == -1)
		    fontSize = defaultSize;
                f = g.deriveFont(fontSize);
            }
	}
        return (f == null) ? defaultEdgeFont : f;
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
        String edgeLineTypeText = ObjectToString.getStringValue(defaultEdgeLineType);
        sb.append("defaultEdgeLineType = ").append(edgeLineTypeText).append(lineSep);
        String sourceArrowText = ObjectToString.getStringValue(defaultEdgeSourceArrow);
        sb.append("defaultEdgeSourceArrow = ").append(sourceArrowText).append(lineSep);
        String targetArrowText = ObjectToString.getStringValue(defaultEdgeTargetArrow);
        sb.append("defaultEdgeTargetArrow = ").append(targetArrowText).append(lineSep);
        sb.append("defaultEdgeLabel = ").append(defaultEdgeLabel).append(lineSep);
        sb.append("defaultEdgeToolTip = ").append(defaultEdgeToolTip).append(lineSep);
        sb.append("defaultEdgeFont = ").append(defaultEdgeFont).append(lineSep);
        sb.append("edgeColorCalculator = ").append(edgeColorCalculator).append(lineSep);
        sb.append("edgeLineTypeCalculator = ").append(edgeLineTypeCalculator).append(lineSep);
        sb.append("edgeSourceArrowCalculator = ").append(edgeSourceArrowCalculator).append(lineSep);
        sb.append("edgeTargetArrowCalculator = ").append(edgeTargetArrowCalculator).append(lineSep);
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
            Color c = (new ColorParser()).parseColor(value);
            if (c != null) {setDefaultEdgeColor(c);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeLineType");
        if (value != null) {
            LineType lt = (new LineTypeParser()).parseLineType(value);
            if (lt != null) {setDefaultEdgeLineType(lt);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeSourceArrow");
        if (value != null) {
            Arrow a = (new ArrowParser()).parseArrow(value);
            if (a != null) {setDefaultEdgeSourceArrow(a);}
        }
        value = eacProps.getProperty(baseKey + ".defaultEdgeTargetArrow");
        if (value != null) {
            Arrow a = (new ArrowParser()).parseArrow(value);
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
	    Font f = (new FontParser()).parseFont(value);
            if (f != null) {
		setDefaultEdgeFont(f);
	    }
        }
        
        //look for calculators
        value = eacProps.getProperty(baseKey + ".edgeColorCalculator");
        if (value != null && !value.equals("null")) {
            EdgeColorCalculator c = catalog.getEdgeColorCalculator(value);
            if (c != null) {setEdgeColorCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeLineTypeCalculator");
        if (value != null && !value.equals("null")) {
            EdgeLineTypeCalculator c = catalog.getEdgeLineTypeCalculator(value);
            if (c != null) {setEdgeLineTypeCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeSourceArrowCalculator");
        if (value != null && !value.equals("null")) {
            EdgeArrowCalculator c = catalog.getEdgeArrowCalculator(value);
            if (c != null) {setEdgeSourceArrowCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeTargetArrowCalculator");
        if (value != null && !value.equals("null")) {
            EdgeArrowCalculator c = catalog.getEdgeArrowCalculator(value);
            if (c != null) {setEdgeTargetArrowCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeLabelCalculator");
        if (value != null && !value.equals("null")) {
            EdgeLabelCalculator c = catalog.getEdgeLabelCalculator(value);
            if (c != null) {setEdgeLabelCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeToolTipCalculator");
        if (value != null && !value.equals("null")) {
            EdgeToolTipCalculator c = catalog.getEdgeToolTipCalculator(value);
            if (c != null) {setEdgeToolTipCalculator(c);}
        }
        value = eacProps.getProperty(baseKey + ".edgeFontFaceCalculator");
        if (value != null && !value.equals("null")) {
            EdgeFontFaceCalculator c = catalog.getEdgeFontFaceCalculator(value);
            if (c != null) {setEdgeFontFaceCalculator(c);}
        }
	value = eacProps.getProperty(baseKey + ".edgeFontSizeCalculator");
	if (value != null && !value.equals("null")) {
	    EdgeFontSizeCalculator c = catalog.getEdgeFontSizeCalculator(value);
	    if (c != null) {setEdgeFontSizeCalculator(c);}
	}
    }
    
    public Properties getProperties(String baseKey) {
        String key = null;
        String value = null;
        Properties newProps = new Properties();
        
        //save default values
        key = baseKey + ".defaultEdgeColor";
        value = ObjectToString.getStringValue( getDefaultEdgeColor() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeLineType";
        value = ObjectToString.getStringValue( getDefaultEdgeLineType() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeSourceArrow";
        value = ObjectToString.getStringValue( getDefaultEdgeSourceArrow() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeTargetArrow";
        value = ObjectToString.getStringValue( getDefaultEdgeTargetArrow() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeLabel";
        value = ObjectToString.getStringValue( getDefaultEdgeLabel() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeToolTip";
        value = ObjectToString.getStringValue( getDefaultEdgeToolTip() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultEdgeFont";
        value = ObjectToString.getStringValue( getDefaultEdgeFont() );
        newProps.setProperty(key, value);
        
        //save an entry for all calculators, including null values
        Calculator c = null;
        key = baseKey + ".edgeColorCalculator";
        c = getEdgeColorCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".edgeLineTypeCalculator";
        c = getEdgeLineTypeCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".edgeSourceArrowCalculator";
        c = getEdgeSourceArrowCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".edgeTargetArrowCalculator";
        c = getEdgeTargetArrowCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".edgeLabelCalculator";
        c = getEdgeLabelCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".edgeToolTipCalculator";
        c = getEdgeToolTipCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".edgeFontFaceCalculator";
        c = getEdgeFontFaceCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".edgeFontSizeCalculator";
        c = getEdgeFontSizeCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        
        return newProps;
    }
}

