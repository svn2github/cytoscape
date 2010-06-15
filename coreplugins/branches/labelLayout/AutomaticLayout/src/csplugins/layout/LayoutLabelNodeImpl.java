package csplugins.layout;

import cytoscape.*;
//import cytoscape.visual.LabelPosition;
import cytoscape.data.CyAttributes;
import cytoscape.view.*;

import giny.view.*;

import java.util.*;


public class LayoutLabelNodeImpl extends LayoutNode {

    // -- static (class) variables --
    // static final double EPSILON = 0.0000001D;

    // -- Instance variables inherited from superclass --
    //  protected boolean isLocked = false;
    //  protected double x;
    //  protected double y;
    //  protected double dispX;
    //  protected double dispY;

    protected LayoutNodeImpl parent;

    /**
     * Empty constructor
     */
    public LayoutLabelNodeImpl() {
    }

    /**
     * The main constructor for a LayoutLabelNode.
     *
     * @param parent The parent LayoutNode for this LayoutLabelNode
     */
    public LayoutLabelNodeImpl(LayoutNodeImpl parent) {
	this.parent = parent;
	this.setX(parent.getX());
	this.setY(parent.getY());
    }
	
    public void moveToLocation() {

 // 	CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
// 	String labelPosition = (String) nodeAtts.getAttribute(parent.getNode().getIdentifier(), 
// 							      "node.labelPosition");
// 	LabelPosition lp = LabelPosition.parse(labelPosition);

	ObjectPosition labelPosition = parent.getNodeView().getLabelPosition();

// 	if (this.isLocked()) { // If node is locked, adjust X and Y to its current location

// 	    this.setX(lp.getOffsetX() + parent.getNodeView().getXPosition());
// 	    this.setY(lp.getOffsetY() + parent.getNodeView().getYPosition());

// 	} else { // If node is unlocked set labels offsets properly	

// 	    parent.moveToLocation(); // make sure parent is where it should be
	    	    
// 	    lp.setOffsetX(this.getX() - parent.getX());
// 	    lp.setOffsetY(this.getY() - parent.getY());
//	} 
    }


    /**
     * Accessor function to return the CyNode associated with
     * this LayoutNode.
     *
     * @return    CyNode that is associated with this LayoutNode
     */
    public CyNode getNode() {
	return parent.node;
    }

    /**
     * Accessor function to return the NodeView associated with
     * this LayoutNode.
     *
     * @return    NodeView that is associated with this LayoutNode
     */
    public NodeView getNodeView() {
	return parent.nodeView;
    }

    /**
     * Return the width of this node
     *
     * @return        width of this node
     */
    public double getWidth() {
	return parent.nodeView.getWidth();
    }

    /**
     * Return the height of this node
     *
     * @return        height of this node
     */
    public double getHeight() {
	return parent.nodeView.getHeight();
    }

    protected double getParentX() {
	return parent.getX();
    }

    protected double getParentY() {
	return parent.getY();
    }

    /**
     * Return a string representation of the node
     *
     * @return        String containing the node's identifier and location
     */
    public String toString() {
	return "Label of Node: " + getIdentifier() + " at " + printLocation();
    }

    /**
     * Return the node's identifier.
     *
     * @return        String containing the node's identifier
     */
    public String getIdentifier() {
	return "Label of node:" + parent.getIdentifier();
    }


}