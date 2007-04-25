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
package csplugins.layout.algorithms.bioLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

import cytoscape.*;
import cytoscape.view.*;
import giny.view.*;

/**
 * The LayoutNode class
 */
class LayoutNode {
	// static (class) variables
	private static double totalWidth = 0;
	private static double totalHeight = 0;
	private static double minX = 100000;
	private static double minY = 100000;
 	private static double maxX = -100000;
 	private static double maxY = -100000;
	private static int lockedNodes = 0;
	static final double EPSILON = 0.0000001D;

	// instance variables
	private double x, y;
	private double dispX, dispY;
	private CyNode node;
	private NodeView nodeView;
	private int index;
	private boolean isLocked = false;
	private ArrayList neighbors = null;

	public LayoutNode() { }

	public LayoutNode(NodeView nodeView, int index, boolean accumulate) { 
		this.nodeView = nodeView;
		this.node = (CyNode)nodeView.getNode();
		this.index = index;
		this.x = nodeView.getXPosition();
		this.y = nodeView.getYPosition();
		this.neighbors = new ArrayList();
		if (accumulate) {
			minX = Math.min(minX,x);
			minY = Math.min(minY,y);
			maxX = Math.max(maxX,x);
			maxY = Math.max(maxY,y);
			this.totalWidth += nodeView.getWidth();
			this.totalHeight += nodeView.getHeight();
		}
	}

	public void reset() {
		this.totalWidth = 0;
		this.totalHeight = 0;
		this.minX = 100000;
		this.minY = 100000;
		this.maxX = -100000;
		this.maxY = -100000;
		this.lockedNodes = 0;
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setDisp(double x, double y) {
		this.dispX = x;
		this.dispY = y;
	}

  public void addNeighbor(LayoutNode v) {
    this.neighbors.add(v);
  }

  public List getNeighbors() {
    return (List)this.neighbors;
  }

  public int getIndex() {
    return this.index;
  }

  public void lock() {
   this.isLocked = true;
	this.lockedNodes += 1;
  }

  public void unLock() {
    this.isLocked = false;
		this.lockedNodes -= 1;
  }

  public boolean isLocked() {
  	return isLocked;
  }

	public int lockedNodeCount() {
		return lockedNodes;
	}

	public void incrementDisp(double x, double y) {
		this.dispX += x;
		this.dispY += y;
	}

	public void increment(double x, double y) {
		this.x += x;
		this.y += y;
	}

	public void decrementDisp(double x, double y) {
		this.dispX -= x;
		this.dispY -= y;
	}

	public void decrement(double x, double y) {
		this.x -= x;
		this.y -= y;
	}

	public double getX () { return this.x; }

	public double getY () { return this.y; }

	public double getXDisp () { return this.dispX; }

	public double getYDisp () { return this.dispY; }

	public double distance (LayoutNode u) {
		double deltaX = this.x - u.getX();
		double deltaY = this.y - u.getY();
		return Math.max(EPSILON,Math.sqrt(deltaX*deltaX + deltaY*deltaY));
	}

	public double distance (double uX, double uY) {
		double deltaX = this.x - uX;
		double deltaY = this.y - uY;
		return Math.max(EPSILON,Math.sqrt(deltaX*deltaX + deltaY*deltaY));
	}

	public double getArea() {
		return totalWidth*totalHeight;
	}

	public double getTotalWidth() { return this.totalWidth; }

	public double getTotalHeight() { return this.totalHeight; }

	public double getMinX() { return this.minX; }

	public double getMinY() { return this.minY; }

	public double getMaxX() { return this.maxX; }

	public double getMaxY() { return this.maxY; }

	public double getWidth() { return this.nodeView.getWidth(); }

	public double getHeight() { return this.nodeView.getHeight(); }

	public void setRandomLocation(Random r) {
		this.x = r.nextDouble()*this.totalWidth;
		this.y = r.nextDouble()*this.totalHeight;
	}

	public void moveToLocation() {
		if (isLocked) {
			this.x = nodeView.getXPosition();
			this.y = nodeView.getYPosition();
		} else {
			nodeView.setXPosition(this.x);
			nodeView.setYPosition(this.y);
		}
	}

	public String getIdentifier() {
		return node.getIdentifier();
	}

	public String printDisp() {
		String ret = new String(""+dispX+", "+dispY);
		return ret;
	}

	public String printLocation() {
		String ret = new String(""+x+", "+y);
		return ret;
	}
}
