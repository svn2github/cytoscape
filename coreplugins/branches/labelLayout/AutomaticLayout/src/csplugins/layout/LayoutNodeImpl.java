/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package csplugins.layout;

import cytoscape.*;

import cytoscape.view.*;

import giny.view.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * The LayoutNode class.  This class is used as a container for information
 * about the nodes in a layout.  In particular, it provides a convenient handle
 * to information about position, the node itself, the node view.  Many of
 * the methods of this class are wrappers for CyNode or NodeView methods, and
 * these are often wrapped by LayoutPartition methods.
 */
public class LayoutNodeImpl extends LayoutNode {
    // static (class) variables
    // static final double EPSILON = 0.0000001D;

    // instance variables
    
    //     private double x;
    //     private double y;
    //     private double dispX;
    //     private double dispY;
    //     private boolean isLocked = false;
    
    protected CyNode node;
    protected NodeView nodeView;

    /**
     * Empty constructor
     */
    public LayoutNodeImpl() {
    }

    /**
     * The main constructor for a LayoutNodeImpl.
     *
     * @param nodeView The NodeView of this node
     * @param index The index (usually in a node array) of this node
     */
    public LayoutNodeImpl(NodeView nodeView, int index) {
	this.nodeView = nodeView;
	this.node = (CyNode) nodeView.getNode();
	this.index = index;
	this.x = nodeView.getXPosition();
	this.y = nodeView.getYPosition();
	this.neighbors = new ArrayList<LayoutNode>();
    }

    /**
     * Accessor function to return the CyNode associated with
     * this LayoutNode.
     *
     * @return    CyNode that is associated with this LayoutNode
     */
    public CyNode getNode() {
	return this.node;
    }

    /**
     * Accessor function to return the NodeView associated with
     * this LayoutNode.
     *
     * @return    NodeView that is associated with this LayoutNode
     */
    public NodeView getNodeView() {
	return this.nodeView;
    }

    /**
     * Return the width of this node
     *
     * @return        width of this node
     */
    public double getWidth() {
	return this.nodeView.getWidth();
    }

    /**
     * Return the height of this node
     *
     * @return        height of this node
     */
    public double getHeight() {
	return this.nodeView.getHeight();
    }

    /**
     * Move the node to its current x,y coordinate.
     */
    public void moveToLocation() {
	if (isLocked) {
	    this.x = nodeView.getXPosition();
	    this.y = nodeView.getYPosition();
	} else {
	    nodeView.setXPosition(this.x);
	    nodeView.setYPosition(this.y);
	}
    }

    /**
     * Return a string representation of the node
     *
     * @return        String containing the node's identifier and location
     */
    public String toString() {
	return "Node " + getIdentifier() + " at " + printLocation();
    }

    /**
     * Return the node's identifier.
     *
     * @return        String containing the node's identifier
     */
    public String getIdentifier() {
	return node.getIdentifier();
    }



}
