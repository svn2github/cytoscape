package cytoscape.layout.label;


import cytoscape.*;
import cytoscape.visual.LabelPosition;
import cytoscape.data.CyAttributes;

import cytoscape.view.*;

import java.util.*;


public class LayoutLabelNode extends LayoutAbstractNode {

    // -- static (class) variables --
    // static final double EPSILON = 0.0000001D;

    // -- Instance variables inherited from superclass --
    //  protected boolean isLocked = false;
    //  protected double x;
    //  protected double y;
    //  protected double dispX;
    //  protected double dispY;

    protected LayoutNode parent;

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

	CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
	String labelPosition = (String) nodeAtts.getAttribute(parent.getNode().getIdentifier(), 
							      "node.labelPosition");
	LabelPosition lp = LabelPosition.parse(labelPosition);


	if (this.isLocked()) { // If node is locked, adjust X and Y to its current location

	    this.setX(lp.getOffsetX() + parent.getNodeView().getXPosition());
	    this.setY(lp.getOffsetY() + parent.getNodeView().getYPosition());

	} else { // If node is unlocked set labels offsets properly	

	    parent.moveToLocation(); // make sure parent is where it should be
	    	    
	    lp.setOffsetX(this.getX() - parent.getX());
	    lp.setOffsetY(this.getY() - parent.getY());
	}
    }


//     protected void setParentOffsetX(double x) {
//     }

//     protected void setParentOffsetY(double y) {
//     }

//     protected void setParentOffset(double x, double y) {
//     }

//     protected LayoutNode getParent() {
// 	return this.parent;
//     }

    protected double getParentX() {
	return parent.getX();
    }

    protected double getParentY() {
	return parent.getY();
    }


}