package cytoscape.visual;

import giny.view.NodeView;

/**
 * This is a replacement for ShapeNodeRealizer.java
 *
 * @since Cytoscape 2.5
 * @version 0.7 
 * @author kono
 *
 */
public enum NodeShape {RECT(NodeView.RECTANGLE, "Rectangle"), 
    ROUND_RECT(NodeView.ROUNDED_RECTANGLE, "Round Rectangle"), 
    RECT_3D(NodeView.RECTANGLE, "3D Rectabgle"), 
    TRAPEZOID(NodeView.RECTANGLE, "Trapezoid"), 
    TRAPEZOID_2(NodeView.RECTANGLE, "Trapezoid 2"), 
    TRIANGLE(NodeView.TRIANGLE, "Traiangle"), 
    PARALLELOGRAM(NodeView.PARALELLOGRAM, "Parallelogram"), 
    DIAMOND(NodeView.DIAMOND, "Diamond"), 
    ELLIPSE(NodeView.ELLIPSE, "Ellipse"), 
    HEXAGON(NodeView.HEXAGON, "Hexagon"), 
    OCTAGON(NodeView.OCTAGON, "Octagon");

    private int ginyShape;
    private String name;

    private NodeShape(int ginyShape, String name) {
        this.ginyShape = ginyShape;
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static NodeShape parseNodeShapeText(String text) {
        String trimed = text.trim();

        for (NodeShape shape : values()) {
            if (getNodeShapeText(shape).equalsIgnoreCase(trimed))
                return shape;
        }

        // Unknown shape: return rectangle.
        return NodeShape.RECT;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String[] valuesAsString() {
        final int length = values().length;
        final String[] nameArray = new String[length];

        for (int i = 0; i < length; i++)
            nameArray[i] = values()[i].getShapeName();

        return nameArray;
    }

    /**
     * Get name of the shape.
     *
     * @return DOCUMENT ME!
     */
    public String getShapeName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean isValidShape(NodeShape type) {
        for (NodeShape curType : values()) {
            if (type == curType)
                return true;
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param shape
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getNodeShapeText(NodeShape shape) {
        String nstext = shape.name();
        nstext = nstext.replaceAll("_", "");

        return nstext.toLowerCase();
    }

    /**
     * Get GINY shape as integer.
     *
     * @return Giny shape as int.
     */
    public int getGinyShape() {
        return ginyShape;
    }
    
    /**
     * Convert from Giny shape to Cytoscape NodeShape enum.
     * 
     * @param ginyShape
     * @return
     */
    public static NodeShape getNodeShape(Byte ginyShape) {
    	for(NodeShape shape: values()) {
    		if(shape.ginyShape == ginyShape) {
    			return shape;
    		}
    	}
    	
    	// Unknown. Return rectangle as the def val.
    	return NodeShape.RECT;
    }
}
