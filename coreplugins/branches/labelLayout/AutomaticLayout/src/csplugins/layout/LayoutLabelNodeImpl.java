/**
* Copyright (C) Gerardo Huck, 2010
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published 
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*  
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*  
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/


/** 
 * This class represents the fake nodes used to layout labels.
 * 
 * It was done as part of Google Summer of Code 2010.
 * Mentor: Mike Smoot
 * @author <a href="mailto:gerardohuck .at. gmail .dot. com">Gerardo Huck</a>
 * @version 0.1
 */

package csplugins.layout;

import cytoscape.*;
import cytoscape.logger.CyLogger;
import cytoscape.data.CyAttributes;
import cytoscape.view.*;
import cytoscape.visual.parsers.*;
import cytoscape.visual.VisualPropertyType;

import ding.view.ObjectPositionImpl;

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
    protected ObjectPosition lp;
    protected CyAttributes nodeAtts = null;

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
	
	// Set labelNode's location to parent node's current label position
 	nodeAtts = Cytoscape.getNodeAttributes();
 	String labelPosition = (String) nodeAtts.getAttribute(parentNodeView.getNode().getIdentifier(), 
							      "node.labelPosition");	

 	if (labelPosition == null) {
	    lp = new ObjectPositionImpl();
	    // logger.info("Created new ObjectPosition!");
	} else {
	    ObjectPositionParser parser = 
		(ObjectPositionParser) VisualPropertyType.NODE_LABEL_POSITION.getValueParser();
	    lp = parser.parseStringValue(labelPosition);
	    // logger.info("ObjectPosition succesfully parsed!");
	}

	try {	
	    // 	logger.info("Parent node: " + parentNodeView.getNode().getIdentifier());
	    // 	logger.info("Offset = " + lp.getOffsetX() + ", " + lp.getOffsetY() );
	    
	    this.setX(lp.getOffsetX() + parentNodeView.getXPosition());
	    this.setY(lp.getOffsetY() + parentNodeView.getYPosition());	    
	    this.neighbors = new ArrayList<LayoutNode>();
	    this.index = index;

	    // logger.info("Created " + this.getIdentifier() + "placed in: " + this.getX() + ", " + this.getY() );
	}
	catch(Exception e) {
	    // Log error
	    logger.warning("Error detected while creating LayoutLabelNodeImp: " + e.toString() );

	    // Reset this attribute by eliminating it

	    if (nodeAtts.hasAttribute(parentNodeView.getNode().getIdentifier(), "node.labelPosition")) {
		nodeAtts.deleteAttribute(parentNodeView.getNode().getIdentifier(), "node.labelPosition");
		logger.warning("Deleted 'node.labelPosition' attribute");
	    }
 
	    lp = new ObjectPositionImpl();
	    logger.warning("Created new ObjectPosition!");	    

	    this.setX( parentNodeView.getXPosition() );
	    this.setY( parentNodeView.getYPosition() );	    
	    this.neighbors = new ArrayList<LayoutNode>();
	    this.index = index;
	}
    }

    /**
     * Moves a label node to the (X,Y) position that is already defined if it is unlocked.
     * Otherwise, updates the (X,Y) fields in order to reflect its real position.
     * Note that moving the parent node will affect the position of this label node.
     */
    public void moveToLocation() {

	lp = new ObjectPositionImpl();
	
	if (this.isLocked()) { // If node is locked, adjust X and Y to its current location

	    //	    logger.info(this.toString() + " was locked");

	    this.setX(lp.getOffsetX() + parentNodeView.getXPosition());
	    this.setY(lp.getOffsetY() + parentNodeView.getYPosition());

	} else { // If node is unlocked set labels offsets properly	
	    	    
	    //	    logger.info(this.toString() + " was unlocked");

	    lp.setOffsetX(this.getX() - parentNodeView.getXPosition());
	    lp.setOffsetY(this.getY() - parentNodeView.getYPosition());

	    nodeAtts.setAttribute(parentNodeView.getNode().getIdentifier(),
	    			  "node.labelPosition", lp.shortString());

// 	    logger.info("Label node was moved!, short string: " + lp.shortString());
// 	    logger.info("full label string: " + lp.toString() );
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
     * Return a string with the "type" of the node ("normal", "label")
     *
     * @return        String containing the node's type
     */
    public String getType() {
	return "label";
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