package cytoscape.layout.label;


import cytoscape.*;

import cytoscape.view.*;

import java.util.*;


public class LayoutLabelNode extends LayoutAbstractNode {

    // -- static (class) variables --
    
    // static int lockedNodes = 0;
    // static final double EPSILON = 0.0000001D;


    // -- Instance variables inherited from superclass --
    
    //  protected boolean isLocked = false;
    //  protected double x;
    //  protected double y;
    //  protected double dispX;
    //  protected double dispY;

    private LayoutNode parent;

    /**
     * Empty constructor
     */
    public LayoutLabelNode() {
    }

    /**
     * The main constructor for a LayoutLabelNode.
     *
     * @param parent The parent LayoutNode for this LayoutLabelNode
     */
    public LayoutLabelNode(LayoutNode parent) {
	this.parent = parent;
	this.setX(parent.getX());
	this.setY(parent.getY());
    }
	
    public void moveToLocation() {

    }


//     protected void setParentOffsetX(double x) {
//     }

//     protected void setParentOffsetY(double y) {
//     }

//     protected void setParentOffset(double x, double y) {
//     }

    protected double getParentX() {
	return parent.getX();
    }

    protected double getParentY() {
	return parent.getY();
    }

    protected LayoutNode getParent() {
	return this.parent;
    }

}