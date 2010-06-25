package csplugins.layout;

import cytoscape.*;

import cytoscape.logger.CyLogger;

import cytoscape.data.CyAttributes;
import cytoscape.view.*;

import giny.view.*;

import java.util.*;


public class LayoutLabelNodeImpl extends LayoutNode {

    // -- Instance variables inherited from superclass --
    //  protected boolean isLocked = false;
    //  protected double x;
    //  protected double y;
    //  protected double dispX;
    //  protected double dispY;

    protected CyLogger logger = null;

    protected NodeView parentNodeView;
    protected ObjectPosition labelPosition;

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
    public LayoutLabelNodeImpl(NodeView parentNodeView, int index) {
	logger = CyLogger.getLogger(LayoutLabelNodeImpl.class);
	this.parentNodeView = parentNodeView;
	labelPosition = parentNodeView.getLabelPosition();

	logger.info("Offset = " + labelPosition.getOffsetX() + ", " + labelPosition.getOffsetY() );
	logger.info("Parent node: " + parentNodeView.getNode().getIdentifier());

	this.setX(labelPosition.getOffsetX() + parentNodeView.getXPosition());
	this.setY(labelPosition.getOffsetY() + parentNodeView.getYPosition());	    
	this.neighbors = new ArrayList<LayoutNode>();
	this.index = index;

	logger.info("Created " + this.getIdentifier() + "placed in: " + this.getX() + ", " + this.getY() );
	logger.info("Parent placed in: " + parentNodeView.getXPosition() + ", " + parentNodeView.getYPosition() );
    }

    /**
     * Moves a label node to the (X,Y) position that is already defined if it is unlocked.
     * Otherwise, updates the (X,Y) fields in order to reflect its real position.
     * Note that moving the parent node will affect the position of this label node.
     */
    public void moveToLocation() {
	
 	if (this.isLocked()) { // If node is locked, adjust X and Y to its current location

	    logger.info(this.toString() + " was locked");

	    this.setX(labelPosition.getOffsetX() + parentNodeView.getXPosition());
	    this.setY(labelPosition.getOffsetY() + parentNodeView.getYPosition());

	} else { // If node is unlocked set labels offsets properly	
	    	    
	    logger.info(this.toString() + " was unlocked");

	    labelPosition.setOffsetX(this.getX() - parentNodeView.getXPosition());
	    labelPosition.setOffsetY(this.getY() - parentNodeView.getYPosition());
	
	     logger.info("Label node was moved!");
	} 
    }

    /**
     * Accessor function to return the NodeView associated with
     * this LayoutNode.
     *
     * @return    NodeView that is associated with this LayoutNode
     */
    public NodeView getNodeView() {
	return parentNodeView;
    }

    /**
     * Return the width of this node
     *
     * @return        width of this node
     */
    public double getWidth() {
	return parentNodeView.getWidth();
    }

    /**
     * Return the height of this label node
     *
     * @return        height of this node
     */
    public double getHeight() {
	return parentNodeView.getHeight();
    }

    public double getParentX() {
	return parentNodeView.getXPosition();
    }

    public double getParentY() {
	return parentNodeView.getYPosition();
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
	return "Label of node:" + parentNodeView.getNode().getIdentifier();
    }


}