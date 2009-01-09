//VizMapperCategories.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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
    public static final Integer NODE_BORDER_LINETYPE = new Integer(2);
    public static final Integer NODE_HEIGHT = new Integer(3);
    public static final Integer NODE_WIDTH = new Integer(4);
    public static final Integer NODE_SHAPE = new Integer(5);
  public static final Integer NODE_SELECTED_COLOR = new Integer(6);

    public static final Integer EDGE_COLOR = new Integer(10);
    public static final Integer EDGE_LINETYPE = new Integer(11);
    public static final Integer EDGE_SOURCE_DECORATION = new Integer(12);
    public static final Integer EDGE_TARGET_DECORATION = new Integer(13);

    public static final Integer BG_COLOR = new Integer(14);

    public VizMapperCategories() {}
    //------------------------------------------------------------------

    public Color getNodeFillColor(Map attrBundle, AttributeMapper mapper) {
	return (Color)getRangeValue(attrBundle, mapper, NODE_FILL_COLOR);
    }

    public Color getNodeBorderColor(Map attrBundle, AttributeMapper mapper) {
	return (Color)getRangeValue(attrBundle, mapper, NODE_BORDER_COLOR);
    }

  public Color getNodeSelectedColor(AttributeMapper mapper){
    return (Color)mapper.getDefaultValue(NODE_SELECTED_COLOR);
    //return (Color)getRangeValue(attrBundle, mapper, NODE_SELECTED_COLOR);
  }
    
    public LineType getNodeBorderLineType(Map attrBundle, AttributeMapper mapper) {
        return (LineType)getRangeValue(attrBundle, mapper, NODE_BORDER_LINETYPE);
    }

    public double getNodeHeight(Map attrBundle, AttributeMapper mapper) {
	Double d = (Double)getRangeValue(attrBundle, mapper, NODE_HEIGHT);
	if (d == null) {
	    return 0;
	} else {
	    return d.doubleValue();
	}
    }

    public double getNodeWidth(Map attrBundle, AttributeMapper mapper) {
	Double d = (Double)getRangeValue(attrBundle, mapper, NODE_WIDTH);
	if (d == null) {
	    return 0;
	} else {
	    return d.doubleValue();
	}
    }

    public byte getNodeShape(Map attrBundle, AttributeMapper mapper) {
	Byte b = (Byte)getRangeValue(attrBundle, mapper, NODE_SHAPE);
	if (b == null) {
	    return 0;
	} else {
	    return b.byteValue();
	}
    }


    public Color getEdgeColor(Map attrBundle, AttributeMapper mapper) {
	return (Color)getRangeValue(attrBundle, mapper, EDGE_COLOR);
    }

    public Color getBGColor(AttributeMapper mapper) {
	return (Color)mapper.getDefaultValue(BG_COLOR);
    }

    public LineType getEdgeLineType(Map attrBundle, AttributeMapper mapper) {
	return (LineType)getRangeValue(attrBundle, mapper, EDGE_LINETYPE);
    }

    public Arrow getEdgeSourceDecoration(Map attrBundle,AttributeMapper mapper) {
	return (Arrow)getRangeValue(attrBundle, mapper, EDGE_SOURCE_DECORATION);
    }

    public Arrow getEdgeTargetDecoration(Map attrBundle, AttributeMapper mapper) {
	return (Arrow)getRangeValue(attrBundle, mapper, EDGE_TARGET_DECORATION);
    }
    
    /**
     * This helper method first checks for a data attribute with the same name
     * as a visual attribute name. If found and it has a String value, that value
     * is parsed and returned as the visual attribute value. Otherwise, delegates to
     * the AttributeMapper argument.
     */
    public Object getRangeValue(Map attrBundle, AttributeMapper mapper,
                                Integer rangeAttribute) {
        String propName = (String)getPropertyNamesMap().get(rangeAttribute);
        if (propName == null || attrBundle == null) {
            return mapper.getRangeValue(attrBundle, rangeAttribute);
        }
        Object propVal = attrBundle.get(propName);
        if (propVal == null) {
            return mapper.getRangeValue(attrBundle, rangeAttribute);
        }
        if ( !(propVal instanceof String) ) {
            System.err.println("Unexpected data attribute value:");
            System.err.println("    " + propName + " = " + propVal);
            System.err.println("    Expected a String, class = " + propVal.getClass() );
            return mapper.getRangeValue(attrBundle, rangeAttribute);
        }
        Object returnVal = parseRangeAttributeValue(rangeAttribute, (String)propVal);
        if (returnVal == null) {
            return mapper.getRangeValue(attrBundle, rangeAttribute);
        } else {
            return returnVal;
        }
    }
    //------------------------------------------------------------------

    public Map getInitialDefaults() {
	Map returnVal = new HashMap();

	returnVal.put( NODE_FILL_COLOR, new Color(255,255,255) );
	// how about selected color?
  returnVal.put( NODE_BORDER_COLOR, new Color(0,0,0) );
        returnVal.put( NODE_BORDER_LINETYPE, LineType.LINE_1 );
	returnVal.put( NODE_HEIGHT, new Double(30) );
	returnVal.put( NODE_WIDTH, new Double(70) );
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
  returnVal.put( NODE_SELECTED_COLOR,"node.selectedColor");
        returnVal.put( NODE_BORDER_LINETYPE, "node.borderLinetype" );
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
	     vizAttribute.equals(BG_COLOR) ||
       vizAttribute.equals(NODE_SELECTED_COLOR)) {
	    returnVal = Misc.parseRGBText(value);
	} else if ( vizAttribute.equals(NODE_HEIGHT) ||
		    vizAttribute.equals(NODE_WIDTH) ) {
	    returnVal = new Double(value);
	} else if( vizAttribute.equals(NODE_SHAPE) ) {
	    returnVal = Misc.parseNodeShapeTextIntoByte(value);
	} else if ( vizAttribute.equals(NODE_BORDER_LINETYPE) ||
                    vizAttribute.equals(EDGE_LINETYPE) ) {
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
    public String rangeAttributeValueToString(Integer vizAttribute, Object rangeAttributeValue){
	
	boolean objectTypeError = false;
	boolean unknownAttribute = false;
	
	if ( vizAttribute.equals(NODE_FILL_COLOR) ||
	     vizAttribute.equals(NODE_BORDER_COLOR) ||
	     vizAttribute.equals(EDGE_COLOR) ||
	     vizAttribute.equals(BG_COLOR) ||
       vizAttribute.equals(NODE_SELECTED_COLOR)){
	    
	    if( rangeAttributeValue instanceof Color){
		return Misc.getRGBText((Color)rangeAttributeValue);
	    }else{
		objectTypeError = true;
	    }
	}else if ( vizAttribute.equals(NODE_HEIGHT) ||
		   vizAttribute.equals(NODE_WIDTH) ){
	    if(rangeAttributeValue instanceof Double){ 
		return rangeAttributeValue.toString();
	    }else{
		objectTypeError = true;
	    }
	}else if( vizAttribute.equals(NODE_SHAPE) ) {
	    if(rangeAttributeValue instanceof Byte){
		return Misc.getNodeShapeText(((Byte)(rangeAttributeValue)).byteValue());
	    }else{
		objectTypeError = true;
	    }
	}else if ( vizAttribute.equals(NODE_BORDER_LINETYPE) ||
		   vizAttribute.equals(EDGE_LINETYPE) ) {
	    if(rangeAttributeValue instanceof LineType){
		return Misc.getLineTypeText((LineType)rangeAttributeValue);
	    }else{
		objectTypeError = true;	
	    }
	}else if ( vizAttribute.equals(EDGE_SOURCE_DECORATION) ||
		   vizAttribute.equals(EDGE_TARGET_DECORATION) ) {
	    if(rangeAttributeValue instanceof Arrow){
		return Misc.getArrowText((Arrow)rangeAttributeValue);
	    }else{
		objectTypeError = true;		
	    }
	}else{
	    unknownAttribute = true;
	}
	
	if(objectTypeError){
	    System.err.println("Error converting attribute value to string:");
	    System.err.println("Incorrect attribute value object type: "
			       + rangeAttributeValue.getClass());
	}

	if(unknownAttribute){
	    //unknown attribute
	    System.err.println("Error converting attribute value to string:");
	    System.err.println("    unknown vizAttribute: "
			       + vizAttribute.toString() ); 
	}
	
	return "";
    }//rangeAttributeValueToString

    //------------------------------------------------------------------

    public Interpolator getInterpolator(Integer vizAttribute) {
	Interpolator fInt = null;
	if ( vizAttribute.equals(NODE_FILL_COLOR) ||
	     vizAttribute.equals(NODE_BORDER_COLOR) ||
	     vizAttribute.equals(EDGE_COLOR) ||
	     vizAttribute.equals(BG_COLOR) ||
       vizAttribute.equals(NODE_SELECTED_COLOR)) {
	    fInt = new LinearNumberToColorInterpolator();
	} else if ( vizAttribute.equals(NODE_HEIGHT) ||
		    vizAttribute.equals(NODE_WIDTH) ) {
	    //fInt = new IntegerInterpolator();
            fInt = new LinearNumberToNumberInterpolator();
	} else if( vizAttribute.equals(NODE_SHAPE) ) {
	    //fInt = new ShapeInterpolator();
            fInt = new FlatInterpolator();
	} else if ( vizAttribute.equals(NODE_BORDER_LINETYPE) ||
                    vizAttribute.equals(EDGE_LINETYPE) ) {
	    //fInt = new LineTypeInterpolator();
            fInt = new FlatInterpolator();
	} else if ( vizAttribute.equals(EDGE_SOURCE_DECORATION) ||
		    vizAttribute.equals(EDGE_TARGET_DECORATION) ) {
	    //fInt = new ArrowInterpolator();
            fInt = new FlatInterpolator();
	} else {//unknown attribute
	    System.err.println("Error parsing range attribute value:");
	    System.err.println("    unknown vizAttribute: "
			       + vizAttribute.toString() );
	}

	return fInt;
    }
}


