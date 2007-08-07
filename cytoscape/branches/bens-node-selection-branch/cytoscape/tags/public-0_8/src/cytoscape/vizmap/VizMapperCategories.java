//VizMapperCategories.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.vizmap;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.HashMap;

import java.awt.Color;

import y.view.ShapeNodeRealizer;
import y.view.LineType;
import y.view.Arrow;

import cytoscape.util.Misc;
//----------------------------------------------------------------------------
/**
 * This implementation of AttributeMapperCategories defines all the
 * visual properties (that is, range attributes) known to Cytoscape.
 * A set of static Integer constants identifies each attribute. In
 * addition to the methods inherited from AttributeMapperCategories,
 * this class defines wrappers around an AttributeMapper object that
 * convert the Objects obtained from that object into the specific types
 * associated with each vizual attribute via a cast operation.
 */
public class VizMapperCategories implements AttributeMapperCategories {

    public static final Integer NODE_FILL_COLOR = new Integer(0);
    public static final Integer NODE_BORDER_COLOR = new Integer(1);
    public static final Integer NODE_HEIGHT = new Integer(2);
    public static final Integer NODE_WIDTH = new Integer(3);
    public static final Integer NODE_SHAPE = new Integer(4);

    public static final Integer EDGE_COLOR = new Integer(10);
    public static final Integer EDGE_LINETYPE = new Integer(11);
    public static final Integer EDGE_SOURCE_DECORATION = new Integer(12);
    public static final Integer EDGE_TARGET_DECORATION = new Integer(13);

    public static final Integer BG_COLOR = new Integer(14);

    public VizMapperCategories() {}
    //------------------------------------------------------------------

    public Color getNodeFillColor(Map attrBundle, AttributeMapper mapper) {
	return (Color)mapper.getRangeValue(attrBundle,NODE_FILL_COLOR);
    }

    public Color getNodeBorderColor(Map attrBundle, AttributeMapper mapper) {
	return (Color)mapper.getRangeValue(attrBundle,NODE_BORDER_COLOR);
    }

    public int getNodeHeight(Map attrBundle, AttributeMapper mapper) {
	Integer i = (Integer)mapper.getRangeValue(attrBundle,NODE_HEIGHT);
	if (i == null) {
	    return 0;
	} else {
	    return i.intValue();
	}
    }

    public int getNodeWidth(Map attrBundle, AttributeMapper mapper) {
	Integer i = (Integer)mapper.getRangeValue(attrBundle,NODE_WIDTH);
	if (i == null) {
	    return 0;
	} else {
	    return i.intValue();
	}
    }

    public byte getNodeShape(Map attrBundle, AttributeMapper mapper) {
	Byte b = (Byte)mapper.getRangeValue(attrBundle,NODE_SHAPE);
	if (b == null) {
	    return 0;
	} else {
	    return b.byteValue();
	}
    }


    public Color getEdgeColor(Map attrBundle, AttributeMapper mapper) {
	return (Color)mapper.getRangeValue(attrBundle,EDGE_COLOR);
    }

    public Color getBGColor(AttributeMapper mapper) {
	return (Color)mapper.getDefaultValue(BG_COLOR);
    }

    public LineType getEdgeLineType(Map attrBundle, AttributeMapper mapper) {
	return (LineType)mapper.getRangeValue(attrBundle,EDGE_LINETYPE);
    }

    public Arrow getEdgeSourceDecoration(Map attrBundle,
					 AttributeMapper mapper) {
	return (Arrow)mapper.getRangeValue(attrBundle,EDGE_SOURCE_DECORATION);
    }

    public Arrow getEdgeTargetDecoration(Map attrBundle,
						AttributeMapper mapper) {
	return (Arrow)mapper.getRangeValue(attrBundle,EDGE_TARGET_DECORATION);
    }

    //------------------------------------------------------------------

    public Map getInitialDefaults() {
	Map returnVal = new HashMap();

	returnVal.put( NODE_FILL_COLOR, new Color(255,255,255) );
	returnVal.put( NODE_BORDER_COLOR, new Color(0,0,0) );
	returnVal.put( NODE_HEIGHT, new Integer(30) );
	returnVal.put( NODE_WIDTH, new Integer(70) );
	returnVal.put( NODE_SHAPE, new Byte(ShapeNodeRealizer.RECT) );
	returnVal.put( EDGE_COLOR, new Color(0,0,0) );
	returnVal.put( BG_COLOR, new Color(255,255,255) );
	returnVal.put( EDGE_LINETYPE, LineType.LINE_1 );
	returnVal.put( EDGE_SOURCE_DECORATION, Arrow.NONE );
	returnVal.put( EDGE_TARGET_DECORATION, Arrow.NONE );

	return returnVal;
    }

    public Map getPropertyNamesMap() {
	Map returnVal = new HashMap();

	returnVal.put( NODE_FILL_COLOR, "node.fillColor" );
	returnVal.put( NODE_BORDER_COLOR, "node.borderColor" );
	returnVal.put( NODE_HEIGHT, "node.height" );
	returnVal.put( NODE_WIDTH, "node.width" );
	returnVal.put( NODE_SHAPE, "node.shape" );
	returnVal.put( EDGE_COLOR, "edge.color" );
	returnVal.put( BG_COLOR, "background.color" );
	returnVal.put( EDGE_LINETYPE, "edge.linetype" );
	returnVal.put( EDGE_SOURCE_DECORATION, "edge.sourceDecoration" );
	returnVal.put( EDGE_TARGET_DECORATION, "edge.targetDecoration" );

	return returnVal;
    }

    //------------------------------------------------------------------

    public Object parseRangeAttributeValue(Integer vizAttribute,
					   String value) {
	Object returnVal = null;
	if ( vizAttribute.equals(NODE_FILL_COLOR) ||
	     vizAttribute.equals(NODE_BORDER_COLOR) ||
	     vizAttribute.equals(EDGE_COLOR) ||
	     vizAttribute.equals(BG_COLOR)) {
	    returnVal = Misc.parseRGBText(value);
	} else if ( vizAttribute.equals(NODE_HEIGHT) ||
		    vizAttribute.equals(NODE_WIDTH) ) {
	    returnVal = new Integer(value);
	} else if( vizAttribute.equals(NODE_SHAPE) ) {
	    returnVal = Misc.parseNodeShapeTextIntoByte(value);
	} else if ( vizAttribute.equals(EDGE_LINETYPE) ) {
	    returnVal = Misc.parseLineTypeText(value);
	} else if ( vizAttribute.equals(EDGE_SOURCE_DECORATION) ||
		    vizAttribute.equals(EDGE_TARGET_DECORATION) ) {
	    returnVal = Misc.parseArrowText(value);
	} else {//unknown attribute
	    System.err.println("Error parsing range attribute value:");
	    System.err.println("    unknown vizAttribute: "
			       + vizAttribute.toString() );
	}

	return returnVal;
    }

    //------------------------------------------------------------------

    public Interpolator getInterpolator(Integer vizAttribute) {
	Interpolator fInt = null;
	if ( vizAttribute.equals(NODE_FILL_COLOR) ||
	     vizAttribute.equals(NODE_BORDER_COLOR) ||
	     vizAttribute.equals(EDGE_COLOR) ||
	     vizAttribute.equals(BG_COLOR) ) {
	    fInt = new LinearNumberToColorInterpolator();
	} else if ( vizAttribute.equals(NODE_HEIGHT) ||
		    vizAttribute.equals(NODE_WIDTH) ) {
	    //fInt = new IntegerInterpolator();
	} else if( vizAttribute.equals(NODE_SHAPE) ) {
	    //fInt = new ShapeInterpolator();
	} else if ( vizAttribute.equals(EDGE_LINETYPE) ) {
	    //fInt = new LineTypeInterpolator();
	} else if ( vizAttribute.equals(EDGE_SOURCE_DECORATION) ||
		    vizAttribute.equals(EDGE_TARGET_DECORATION) ) {
	    //fInt = new ArrowInterpolator();
	} else {//unknown attribute
	    System.err.println("Error parsing range attribute value:");
	    System.err.println("    unknown vizAttribute: "
			       + vizAttribute.toString() );
	}

	return fInt;
    }
}
