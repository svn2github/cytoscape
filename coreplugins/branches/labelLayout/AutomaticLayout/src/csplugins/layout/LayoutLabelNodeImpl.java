package csplugins.layout;

import cytoscape.*;

import cytoscape.logger.CyLogger;

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

    protected CyLogger logger = null;

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
    public LayoutLabelNodeImpl(LayoutNodeImpl parent, int index) {
	this.parent = parent;
	this.setX(parent.getX());
	this.setY(parent.getY());
	this.neighbors = new ArrayList<LayoutNode>();
	this.index = index;
	logger = CyLogger.getLogger(LayoutLabelNodeImpl.class);
    }
	
    public void moveToLocation() {
	
	// make sure parent is where it should be
	parent.moveToLocation(); 

	logger.info("moved parent node of label node #" + index);

	ObjectPosition labelPosition;
	try {
	    labelPosition = parent.getNodeView().getLabelPosition();
	} catch(Exception e) {
	    logger.info("error while getting ObjectPosition:" + e.getMessage() );
	    return;
	}

	logger.info("Got ObjectPosition from parent");

 	if (this.isLocked()) { // If node is locked, adjust X and Y to its current location

	    logger.info("Label node was locked");

	    this.setX(labelPosition.getOffsetX() + parent.getX());
	    this.setY(labelPosition.getOffsetY() + parent.getY());

	} else { // If node is unlocked set labels offsets properly	
	    	    
	    logger.info("Label node was unlocked");

	    labelPosition.setOffsetX(this.getX() - parent.getX());
	    labelPosition.setOffsetY(this.getY() - parent.getY());
	
	    logger.info("Label node was moved!");
	} 
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