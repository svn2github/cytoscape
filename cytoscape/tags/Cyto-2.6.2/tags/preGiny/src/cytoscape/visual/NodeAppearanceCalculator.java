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

import y.base.Node;
import y.view.LineType;
import y.view.Arrow;
import y.view.ShapeNodeRealizer;

import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.visual.calculators.*;
import cytoscape.visual.parsers.*;
//----------------------------------------------------------------------------
/**
 * This class calculates the appearance of a Node. It holds a default value
 * and a (possibly null) calculator for each visual attribute.
 */
public class NodeAppearanceCalculator implements Cloneable {
    
    Color defaultNodeFillColor = Color.WHITE;
    Color defaultNodeBorderColor = Color.BLACK;
    LineType defaultNodeLineType = LineType.LINE_1;
    byte defaultNodeShape = ShapeNodeRealizer.RECT;
    double defaultNodeWidth = 70.0;
    double defaultNodeHeight = 30.0;
    String defaultNodeLabel = "";
    String defaultNodeToolTip = "";
    Font defaultNodeFont = new Font(null, Font.PLAIN, 12);

    // true if node width/height locked
    private boolean nodeSizeLocked = true;
    
    public static final String nodeFillColorBypass = "node.fillColor";
    public static final String nodeBorderColorBypass = "node.borderColor";
    public static final String nodeLineTypeBypass = "node.lineType";
    public static final String nodeShapeBypass = "node.shape";
    public static final String nodeWidthBypass = "node.width";
    public static final String nodeHeightBypass = "node.height";
    public static final String nodeLabelBypass = "node.label";
    public static final String nodeToolTipBypass = "node.toolTip";
    public static final String nodeFontBypass = "node.font";

    NodeColorCalculator nodeFillColorCalculator;
    NodeColorCalculator nodeBorderColorCalculator;
    NodeLineTypeCalculator nodeLineTypeCalculator;
    NodeShapeCalculator nodeShapeCalculator;
    NodeSizeCalculator nodeWidthCalculator;
    NodeSizeCalculator nodeHeightCalculator;
    NodeLabelCalculator nodeLabelCalculator;
    NodeToolTipCalculator nodeToolTipCalculator;
    NodeFontFaceCalculator nodeFontFaceCalculator;
    NodeFontSizeCalculator nodeFontSizeCalculator;

    /**
     * Make shallow copy of this object
     */
    public Object clone() {
	Object copy = null;
	try {
	    copy = super.clone();
	}
	catch (CloneNotSupportedException e) {
	    System.err.println("Error cloning!");
	}
	return copy;
    }
	
    public NodeAppearanceCalculator() {}
    /**
     * Creates a new NodeAppearanceCalculator and immediately customizes it
     * by calling applyProperties with the supplied arguments.
     */
    public NodeAppearanceCalculator(String name, Properties nacProps,
                                    String baseKey, CalculatorCatalog catalog) {
        applyProperties(name, nacProps, baseKey, catalog);
    }
    
    /**
     * Copy constructor. Returns a default object if the argument is null.
     */
    public NodeAppearanceCalculator(NodeAppearanceCalculator toCopy) {
        if (toCopy == null) {return;}
        
        setDefaultNodeFillColor( toCopy.getDefaultNodeFillColor() );
        setDefaultNodeBorderColor( toCopy.getDefaultNodeBorderColor() );
        setDefaultNodeLineType( toCopy.getDefaultNodeLineType() );
        setDefaultNodeShape( toCopy.getDefaultNodeShape() );
        setDefaultNodeWidth( toCopy.getDefaultNodeWidth() );
        setDefaultNodeHeight( toCopy.getDefaultNodeHeight() );
        setDefaultNodeLabel( toCopy.getDefaultNodeLabel() );
        setDefaultNodeToolTip( toCopy.getDefaultNodeToolTip() );
        setDefaultNodeFont( toCopy.getDefaultNodeFont() );
        
        setNodeSizeLocked( toCopy.getNodeSizeLocked() );
        
        setNodeFillColorCalculator( toCopy.getNodeFillColorCalculator() );
        setNodeBorderColorCalculator( toCopy.getNodeBorderColorCalculator() );
        setNodeLineTypeCalculator( toCopy.getNodeLineTypeCalculator() );
        setNodeShapeCalculator( toCopy.getNodeShapeCalculator() );
        setNodeWidthCalculator( toCopy.getNodeWidthCalculator() );
        setNodeHeightCalculator( toCopy.getNodeHeightCalculator() );
        setNodeLabelCalculator( toCopy.getNodeLabelCalculator() );
        setNodeToolTipCalculator( toCopy.getNodeToolTipCalculator() );
        setNodeFontFaceCalculator( toCopy.getNodeFontFaceCalculator() );
        setNodeFontSizeCalculator( toCopy.getNodeFontSizeCalculator() );
    }
    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Node in the supplied
     * CyNetwork. A new NodeApperance object will be created.
     */
    public NodeAppearance calculateNodeAppearance(Node node, CyNetwork network) {
        NodeAppearance appr = new NodeAppearance();
        calculateNodeAppearance(appr, node, network);
        return appr;
    }
    
    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Node in the supplied
     * CyNetwork. The supplied NodeAppearance object will be changed to hold
     * the new values.
     */
    public void calculateNodeAppearance(NodeAppearance appr, Node node, CyNetwork network) {
        appr.setFillColor( calculateNodeFillColor(node, network) );
        appr.setBorderColor( calculateNodeBorderColor(node, network) );
	appr.setBorderLineType( calculateNodeLineType(node, network) );
        appr.setShape( calculateNodeShape(node, network) );
	// if node size is locked, only use the height calculator
	if (this.nodeSizeLocked) {
	    double size = calculateNodeHeight(node, network);
	    appr.setWidth(size);
	    appr.setHeight(size);
	}
	else {
	    appr.setWidth( calculateNodeWidth(node, network) );
	    appr.setHeight( calculateNodeHeight(node, network) );
	}
        appr.setLabel( calculateNodeLabel(node, network) );
        appr.setToolTip( calculateNodeToolTip(node, network) );
	appr.setFont( calculateNodeFont(node, network) );
        //set other node appearance attributes
    }

    
    public Color getDefaultNodeFillColor() {return defaultNodeFillColor;}
    public void setDefaultNodeFillColor(Color c) {
        if (c != null) {defaultNodeFillColor = c;}
    }
    public NodeColorCalculator getNodeFillColorCalculator() {return nodeFillColorCalculator;}
    public void setNodeFillColorCalculator(NodeColorCalculator c) {nodeFillColorCalculator = c;}
    public Color calculateNodeFillColor(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeFillColor;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeFillColorBypass, name);
        if (attrValue instanceof Color) {return (Color)attrValue;}
        if (attrValue instanceof String) {
            Color c = (new ColorParser()).parseColor((String)attrValue);
            if (c != null) {return c;}
        }
        //try to get a value from the calculator
        if (nodeFillColorCalculator == null) {return defaultNodeFillColor;}
        Color c = nodeFillColorCalculator.calculateNodeColor(node, network);
        return (c == null) ? defaultNodeFillColor : c;
    }
        
    public Color getDefaultNodeBorderColor() {return defaultNodeBorderColor;}
    public void setDefaultNodeBorderColor(Color c) {
        if (c != null) {defaultNodeBorderColor = c;}
    }
    public NodeColorCalculator getNodeBorderColorCalculator() {return nodeBorderColorCalculator;}
    public void setNodeBorderColorCalculator(NodeColorCalculator c) {nodeBorderColorCalculator = c;}
    public Color calculateNodeBorderColor(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeBorderColor;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeBorderColorBypass, name);
        if (attrValue instanceof Color) {return (Color)attrValue;}
        if (attrValue instanceof String) {
            Color c = (new ColorParser()).parseColor((String)attrValue);
            if (c != null) {return c;}
        }
        //try to get a value from the calculator
        if (nodeBorderColorCalculator == null) {return defaultNodeBorderColor;}
        Color c = nodeBorderColorCalculator.calculateNodeColor(node, network);
        return (c == null) ? defaultNodeBorderColor : c;
    }
       
    public LineType getDefaultNodeLineType() {return defaultNodeLineType;}
    public void setDefaultNodeLineType(LineType lt) {
        if (lt != null) {defaultNodeLineType = lt;}
    }
    public NodeLineTypeCalculator getNodeLineTypeCalculator() {return nodeLineTypeCalculator;}
    public void setNodeLineTypeCalculator(NodeLineTypeCalculator c) {nodeLineTypeCalculator = c;}
    public LineType calculateNodeLineType(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeLineType;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeLineTypeBypass, name);
        if (attrValue instanceof LineType) {return (LineType)attrValue;}
        if (attrValue instanceof String) {
            LineType lt = (new LineTypeParser()).parseLineType((String)attrValue);
            if (lt != null) {return lt;}
        }
        //try to get a value from the calculator
        if (nodeLineTypeCalculator == null) {return defaultNodeLineType;}
        LineType lt = nodeLineTypeCalculator.calculateNodeLineType(node, network);
        return (lt == null) ? defaultNodeLineType : lt;
    }
       
    public byte getDefaultNodeShape() {return defaultNodeShape;}
    public void setDefaultNodeShape(byte s) {
        if (isValidShape(s)) {defaultNodeShape = s;}
    }
    public NodeShapeCalculator getNodeShapeCalculator() {return nodeShapeCalculator;}
    public void setNodeShapeCalculator(NodeShapeCalculator c) {nodeShapeCalculator = c;}
    public byte calculateNodeShape(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeShape;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeShapeBypass, name);
        if (attrValue instanceof Byte) {
            byte s = ((Byte)attrValue).byteValue();
            if (isValidShape(s)) {return s;}
        }
        if (attrValue instanceof String) {
            Byte b = (new NodeShapeParser()).parseNodeShape((String)attrValue);
            byte s = b.byteValue();
            if (isValidShape(s)) {return s;}
        }
        //try to get a value from the calculator
        if (nodeShapeCalculator == null) {return defaultNodeShape;}
        byte s = nodeShapeCalculator.calculateNodeShape(node, network);
        return (isValidShape(s)) ? s : defaultNodeShape;
    }
    
    /**
     * Since node shapes are specified by byte identifiers in yFiles, this
     * method checks to make sure that a supplied byte really matches one
     * of the known identifiers.
     * An alternative would be to create a new Shape class that takes a byte
     * in its constructor and performs the following check.
     */
    public boolean isValidShape(byte shape) {
        if(shape == ShapeNodeRealizer.RECT){return true;}
        if(shape == ShapeNodeRealizer.ROUND_RECT){return true;}
        if(shape == ShapeNodeRealizer.RECT_3D){return true;}
        if(shape == ShapeNodeRealizer.TRAPEZOID){return true;}
        if(shape == ShapeNodeRealizer.TRAPEZOID_2){return true;}
        if(shape == ShapeNodeRealizer.TRIANGLE){return true;}
        if(shape == ShapeNodeRealizer.PARALLELOGRAM){return true;}
        if(shape == ShapeNodeRealizer.DIAMOND){return true;}
        if(shape == ShapeNodeRealizer.ELLIPSE){return true;}
        if(shape == ShapeNodeRealizer.HEXAGON){return true;}
        if(shape == ShapeNodeRealizer.OCTAGON){return true;}
        
        return false;
    }
       
    public double getDefaultNodeWidth() {return defaultNodeWidth;}
    public void setDefaultNodeWidth(double d) {
        if (d > 0) {defaultNodeWidth = d;}
    }
    public NodeSizeCalculator getNodeWidthCalculator() {return nodeWidthCalculator;}
    public void setNodeWidthCalculator(NodeSizeCalculator c) {nodeWidthCalculator = c;}
    public double calculateNodeWidth(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeWidth;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeWidthBypass, name);
        if (attrValue instanceof Double) {return ((Double)attrValue).doubleValue();}
        if (attrValue instanceof String) {
            Double dObj = (new DoubleParser()).parseDouble((String)attrValue);
            if (dObj != null) {return dObj.doubleValue();}
        }
        //try to get a value from the calculator
        if (nodeWidthCalculator == null) {return defaultNodeWidth;}
        double d = nodeWidthCalculator.calculateNodeSize(node, network);
        return (d <= 0.0) ? defaultNodeWidth : d;
    }
       
    public double getDefaultNodeHeight() {return defaultNodeHeight;}
    public void setDefaultNodeHeight(double d) {
        if (d > 0.0) {defaultNodeHeight = d;}
    }
    public NodeSizeCalculator getNodeHeightCalculator() {return nodeHeightCalculator;}
    public void setNodeHeightCalculator(NodeSizeCalculator c) {nodeHeightCalculator = c;}
    public double calculateNodeHeight(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeHeight;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeHeightBypass, name);
        if (attrValue instanceof Double) {return ((Double)attrValue).doubleValue();}
        if (attrValue instanceof String) {
            Double dObj = (new DoubleParser()).parseDouble((String)attrValue);
            if (dObj != null) {return dObj.doubleValue();}
        }
        //try to get a value from the calculator
        if (nodeHeightCalculator == null) {return defaultNodeHeight;}
        double d = nodeHeightCalculator.calculateNodeSize(node, network);
        return (d <= 0.0) ? defaultNodeHeight : d;
    }

    public boolean getNodeSizeLocked() {
	return this.nodeSizeLocked;
    }
    public void setNodeSizeLocked(boolean b) {
	this.nodeSizeLocked = b;
    }
       
    public String getDefaultNodeLabel() {return defaultNodeLabel;}
    public void setDefaultNodeLabel(String s) {
        if (s != null) {defaultNodeLabel = s;}
    }
    public NodeLabelCalculator getNodeLabelCalculator() {return nodeLabelCalculator;}
    public void setNodeLabelCalculator(NodeLabelCalculator c) {nodeLabelCalculator = c;}
    public String calculateNodeLabel(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeLabel;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeLabelBypass, name);
        if (attrValue instanceof String) {return (String)attrValue;}
        //try to get a value from the calculator
        if (nodeLabelCalculator == null) {return defaultNodeLabel;}
        String s = nodeLabelCalculator.calculateNodeLabel(node, network);
        return (s == null) ? defaultNodeLabel : s;
    }
       
    public String getDefaultNodeToolTip() {return defaultNodeToolTip;}
    public void setDefaultNodeToolTip(String s) {
        if (s != null) {defaultNodeToolTip = s;}
    }
    public NodeToolTipCalculator getNodeToolTipCalculator() {return nodeToolTipCalculator;}
    public void setNodeToolTipCalculator(NodeToolTipCalculator c) {nodeToolTipCalculator = c;}
    public String calculateNodeToolTip(Node node, CyNetwork network) {
        if (node == null || network == null) {return defaultNodeToolTip;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeToolTipBypass, name);
        if (attrValue instanceof String) {return (String)attrValue;}
        //try to get a value from the calculator
        if (nodeToolTipCalculator == null) {return defaultNodeToolTip;}
        String s = nodeToolTipCalculator.calculateNodeToolTip(node, network);
        return (s == null) ? defaultNodeToolTip : s;
    }
    
    public Font getDefaultNodeFont() {return defaultNodeFont;}
    public void setDefaultNodeFont(Font f) {
	if (f != null) {defaultNodeFont = f;}
    }
    
    public Font getDefaultNodeFontFace() {return defaultNodeFont;}
    public void setDefaultNodeFontFace(Font f) {
	if (f != null) {
	    float fontSize = defaultNodeFont.getSize2D();
	    defaultNodeFont = f.deriveFont(fontSize);
	}
    }
    public NodeFontFaceCalculator getNodeFontFaceCalculator() {return nodeFontFaceCalculator;}
    public void setNodeFontFaceCalculator(NodeFontFaceCalculator c) {nodeFontFaceCalculator = c;}
    

    public float getDefaultNodeFontSize() {return defaultNodeFont.getSize2D();}
    public void setDefaultNodeFontSize(float f) {
	if (f > 0.0) defaultNodeFont = defaultNodeFont.deriveFont(f);
    }
    public NodeFontSizeCalculator getNodeFontSizeCalculator() {return nodeFontSizeCalculator;}
    public void setNodeFontSizeCalculator(NodeFontSizeCalculator c) {nodeFontSizeCalculator = c;}
    
    public Font calculateNodeFont(Node node, CyNetwork network) {
	if (node == null || network == null) {return defaultNodeFont;}
        //look for a suitable value in a specific data attribute
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        String name = nodeAttributes.getCanonicalName(node);
        Object attrValue = nodeAttributes.getValue(nodeFontBypass, name);
        if (attrValue instanceof Font) {return (Font)attrValue;}
        if (attrValue instanceof String) {
            Font f = (new FontParser()).parseFont((String)attrValue);
            if (f != null) {return f;}
        }
        //try to get a value from the calculators
        if (nodeFontFaceCalculator == null && nodeFontSizeCalculator == null) {
	    return defaultNodeFont;
	}
	Font f;
	float defaultSize = defaultNodeFont.getSize2D();
	if (nodeFontFaceCalculator == null) { // nodeFontSizeCalculator != null
	    float fontSize = nodeFontSizeCalculator.calculateNodeFontSize(node, network);
	    if (fontSize == -1)
		fontSize = defaultSize;
	    f = defaultNodeFont.deriveFont(fontSize);
	    return (f == null) ? defaultNodeFont : f;
	}
	else {
	    Font g = nodeFontFaceCalculator.calculateNodeFontFace(node, network);
	    if (g == null) {
		g = defaultNodeFont;
	    }
	    if (nodeFontSizeCalculator == null) {
		f = g.deriveFont(defaultSize);
            } else {
		float fontSize = nodeFontSizeCalculator.calculateNodeFontSize(node, network);
		if (fontSize == -1)
		    fontSize = defaultSize;
                f = g.deriveFont(fontSize);
            }
	}
        return (f == null) ? defaultNodeFont : f;
    }
    
    /**
     * Returns a text description of the current default values and calculator
     * names.
     */
    public String getDescription() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("NodeAppearanceCalculator:" + lineSep);
        sb.append("defaultNodeFillColor = ").append(defaultNodeFillColor).append(lineSep);
        sb.append("defaultNodeBorderColor = ").append(defaultNodeBorderColor).append(lineSep);
        String nodeLineTypeText = ObjectToString.getStringValue(defaultNodeLineType);
        sb.append("defaultNodeLineType = ").append(nodeLineTypeText).append(lineSep);
        Byte nodeShapeByte = new Byte(defaultNodeShape);
        String nodeShapeText = ObjectToString.getStringValue(nodeShapeByte);
        sb.append("defaultNodeShape = ").append(nodeShapeText).append(lineSep);
        sb.append("defaultNodeWidth = ").append(defaultNodeWidth).append(lineSep);
        sb.append("defaultNodeHeight = ").append(defaultNodeHeight).append(lineSep);
        sb.append("defaultNodeLabel = ").append(defaultNodeLabel).append(lineSep);
        sb.append("defaultNodeToolTip = ").append(defaultNodeToolTip).append(lineSep);
        sb.append("defaultNodeFont = ").append(defaultNodeFont).append(lineSep);
        sb.append("nodeSizeLocked = ").append(nodeSizeLocked).append(lineSep);
        sb.append("nodeFillColorCalculator = ").append(nodeFillColorCalculator).append(lineSep);
        sb.append("nodeBorderColorCalculator = ").append(nodeBorderColorCalculator).append(lineSep);
        sb.append("nodeLineTypeCalculator = ").append(nodeLineTypeCalculator).append(lineSep);
        sb.append("nodeShapeCalculator = ").append(nodeShapeCalculator).append(lineSep);
        sb.append("nodeWidthCalculator = ").append(nodeWidthCalculator).append(lineSep);
        sb.append("nodeHeightCalculator = ").append(nodeHeightCalculator).append(lineSep);
        sb.append("nodeLabelCalculator = ").append(nodeLabelCalculator).append(lineSep);
        sb.append("nodeToolTipCalculator = ").append(nodeToolTipCalculator).append(lineSep);
        sb.append("nodeFontFaceCalculator = ").append(nodeFontFaceCalculator).append(lineSep);
        sb.append("nodeFontSizeCalculator = ").append(nodeFontSizeCalculator).append(lineSep);
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
    public void applyProperties(String name, Properties nacProps, String baseKey,
                                CalculatorCatalog catalog) {
        String value = null;
        
        //look for default values
        value = nacProps.getProperty(baseKey + ".defaultNodeFillColor");
        if (value != null) {
            Color c = (new ColorParser()).parseColor(value);
            if (c != null) {setDefaultNodeFillColor(c);}
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeBorderColor");
        if (value != null) {
            Color c = (new ColorParser()).parseColor(value);
            if (c != null) {setDefaultNodeBorderColor(c);}
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeLineType");
        if (value != null) {
            LineType lt = (new LineTypeParser()).parseLineType(value);
            if (lt != null) {setDefaultNodeLineType(lt);}
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeShape");
        if (value != null) {
            Byte bObj = (new NodeShapeParser()).parseNodeShape(value);
            if (bObj != null) {
                byte b = bObj.byteValue();
                if (isValidShape(b)) {setDefaultNodeShape(b);}
            }
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeWidth");
        if (value != null) {
            Double dObj = (new DoubleParser()).parseDouble(value);
            if (dObj != null) {
                double d = dObj.doubleValue();
                if (d > 0) {setDefaultNodeWidth(d);}
            }
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeHeight");
        if (value != null) {
            Double dObj = (new DoubleParser()).parseDouble(value);
            if (dObj != null) {
                double d = dObj.doubleValue();
                if (d > 0) {setDefaultNodeHeight(d);}
            }
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeLabel");
        if (value != null) {
            setDefaultNodeLabel(value);
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeToolTip");
        if (value != null) {
            setDefaultNodeToolTip(value);
        }
        value = nacProps.getProperty(baseKey + ".defaultNodeFont");
        if (value != null) {
	    Font f = (new FontParser()).parseFont(value);
            if (f != null) {
		setDefaultNodeFont(f);
	    }
        }
        
        //see if node size is locked
        value = nacProps.getProperty(baseKey + ".nodeSizeLocked");
        if (value != null) {
            boolean b = Boolean.valueOf(value).booleanValue();
            setNodeSizeLocked(b);
        }
        
        //look for calculators; skip if the name is "null" (means no calculator)
        value = nacProps.getProperty(baseKey + ".nodeFillColorCalculator");
        if (value != null && !value.equals("null")) {
            NodeColorCalculator c = catalog.getNodeColorCalculator(value);
            if (c != null) {setNodeFillColorCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeBorderColorCalculator");
        if (value != null && !value.equals("null")) {
            NodeColorCalculator c = catalog.getNodeColorCalculator(value);
            if (c != null) {setNodeBorderColorCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeLineTypeCalculator");
        if (value != null && !value.equals("null")) {
            NodeLineTypeCalculator c = catalog.getNodeLineTypeCalculator(value);
            if (c != null) {setNodeLineTypeCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeShapeCalculator");
        if (value != null && !value.equals("null")) {
            NodeShapeCalculator c = catalog.getNodeShapeCalculator(value);
            if (c != null) {setNodeShapeCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeWidthCalculator");
        if (value != null && !value.equals("null")) {
            NodeSizeCalculator c = catalog.getNodeSizeCalculator(value);
            if (c != null) {setNodeWidthCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeHeightCalculator");
        if (value != null && !value.equals("null")) {
            NodeSizeCalculator c = catalog.getNodeSizeCalculator(value);
            if (c != null) {setNodeHeightCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeLabelCalculator");
        if (value != null && !value.equals("null")) {
            NodeLabelCalculator c = catalog.getNodeLabelCalculator(value);
            if (c != null) {setNodeLabelCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeToolTipCalculator");
        if (value != null && !value.equals("null")) {
            NodeToolTipCalculator c = catalog.getNodeToolTipCalculator(value);
            if (c != null) {setNodeToolTipCalculator(c);}
        }
        value = nacProps.getProperty(baseKey + ".nodeFontFaceCalculator");
        if (value != null && !value.equals("null")) {
            NodeFontFaceCalculator c = catalog.getNodeFontFaceCalculator(value);
            if (c != null) {setNodeFontFaceCalculator(c);}
        }
	value = nacProps.getProperty(baseKey + ".nodeFontSizeCalculator");
	if (value != null && !value.equals("null")) {
	    NodeFontSizeCalculator c = catalog.getNodeFontSizeCalculator(value);
	    if (c != null) {setNodeFontSizeCalculator(c);}
	}
    }
    
    public Properties getProperties(String baseKey) {
        String key = null;
        String value = null;
        Properties newProps = new Properties();
        
        //save default values
        key = baseKey + ".defaultNodeFillColor";
        value = ObjectToString.getStringValue( getDefaultNodeFillColor() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeBorderColor";
        value = ObjectToString.getStringValue( getDefaultNodeBorderColor() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeLineType";
        value = ObjectToString.getStringValue( getDefaultNodeLineType() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeShape";
        Byte nodeShapeByte = new Byte( getDefaultNodeShape() );
        value = ObjectToString.getStringValue(nodeShapeByte);
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeWidth";
        Double nodeWidthDouble = new Double( getDefaultNodeWidth() );
        value = ObjectToString.getStringValue(nodeWidthDouble);
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeHeight";
        Double nodeHeightDouble = new Double( getDefaultNodeHeight() );
        value = ObjectToString.getStringValue(nodeHeightDouble);
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeLabel";
        value = ObjectToString.getStringValue( getDefaultNodeLabel() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeToolTip";
        value = ObjectToString.getStringValue( getDefaultNodeToolTip() );
        newProps.setProperty(key, value);
        key = baseKey + ".defaultNodeFont";
        value = ObjectToString.getStringValue( getDefaultNodeFont() );
        newProps.setProperty(key, value);
        
        //save node size locked flag
        key = baseKey + ".nodeSizeLocked";
        value = Boolean.toString( getNodeSizeLocked() );
        newProps.setProperty(key, value);
        
        //save all calculator fields, including nulls
        Calculator c = null;
        key = baseKey + ".nodeFillColorCalculator";
        c = getNodeFillColorCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeBorderColorCalculator";
        c = getNodeBorderColorCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeLineTypeCalculator";
        c = getNodeLineTypeCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeShapeCalculator";
        c = getNodeShapeCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeWidthCalculator";
        c = getNodeWidthCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeHeightCalculator";
        c = getNodeHeightCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeLabelCalculator";
        c = getNodeLabelCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeToolTipCalculator";
        c = getNodeToolTipCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeFontFaceCalculator";
        c = getNodeFontFaceCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        key = baseKey + ".nodeFontSizeCalculator";
        c = getNodeFontSizeCalculator();
        value = (c == null) ? "null" : c.toString();
        newProps.setProperty(key, value);
        
        return newProps;
    }
}

