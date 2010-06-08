package cytoscape.layout.label;


import cytoscape.*;

import cytoscape.view.*;

import java.util.*;

/**
 * This class is intended to be a common interface for Layout Node classes that interact
 * with LayoutPartition class.
 * In particular, it aims at providing convenient handle
 * to information about position, the node itself, the node view.  Many of
 * the methods of this class are wrappers for CyNode or NodeView methods, and
 * these are often wrapped by LayoutPartition methods.
 */
public abstract class LayoutAbstractNode {

    // static (class) variables
    static int lockedNodes = 0;
    static final double EPSILON = 0.0000001D;


    // Instance variables
    protected boolean isLocked = false;
    protected double x;
    protected double y;
    protected double dispX;
    protected double dispY;

    /**
     * This method MUST be overriden by subclasses
     */
    public abstract void moveToLocation();


    // static methods
    /**
     * Returns the number of locked nodes.  This is a static that is incremented whenever
     * lock() is called and decremented whenever unlock() is called.  It is useful for some
     * algorithms that only want to get the number of unlocked nodes for the purposes of their
     * layout loops.
     *
     * @return        the number of unlocked nodes.
     */
    public static int lockedNodeCount() {
	return lockedNodes;
    }


    // instance methods
    /**
     * Set the location of this LayoutNode.  Note that this only
     * sets the location -- it does not actually move the node to
     * that location.  Users should call moveToLocation to actually
     * accomplish the move.
     *
     * @param    x    Double representing the new X corrdinate of this node
     * @param    y    Double representing the new Y corrdinate of this node
     */
    public void setLocation(double x, double y) {
	this.x = x;
	this.y = y;
    }

    /**
     * Set the X location of this LayoutNode.  Note that this only
     * sets the location -- it does not actually move the node to
     * that location.  Users should call moveToLocation to actually
     * accomplish the move.
     *
     * @param    x    Double representing the new X corrdinate of this node
     */
    public void setX(double x) {
	this.x = x;
    }

    /**
     * Set the Y location of this LayoutNode.  Note that this only
     * sets the location -- it does not actually move the node to
     * that location.  Users should call moveToLocation to actually
     * accomplish the move.
     *
     * @param    y    Double representing the new Y corrdinate of this node
     */
    public void setY(double y) {
	this.y = y;
    }

    /**
     * Set the displacement of this LayoutNode.  The displacement is a
     * little different than the location in that it records an offset from
     * the current location.  This is useful for algorithms such as Kamada Kawai
     * and Fructerman Rheingold, which update positions iteratively.
     *
     * @param    x    Double representing the amount to offset in the x direction
     * @param    y    Double representing the amount to offset in the y direction
     */
    public void setDisp(double x, double y) {
	this.dispX = x;
	this.dispY = y;
    }

    /**
     * Register this node as being "locked".  Locked nodes are exempt from being moved
     * during layout.  Usually, these are the unselected nodes when a selected-only
     * layout is being executed.
     */
    public void lock() {
	isLocked = true;
	lockedNodes += 1;
    }
	
    /**
     * Register this node as being "unlocked".  Locked nodes are exempt from being moved
     * during layout.  Usually, these are the unselected nodes when a selected-only
     * layout is being executed.  The "unlocked" state is the default.
     */
    public void unLock() {
	isLocked = false;
	lockedNodes -= 1;
    }

    /**
     * Returns "true" if this node is locked, false otherwise.
     *
     * @return        true if locked, false if unlocked.
     */
    public boolean isLocked() {
	return isLocked;
    }

    /**
     * Increment the displacement recorded for this node by (x,y).
     *
     * @param    x    the additional amount to displace in the x direction
     * @param    y    the additional amount to displace in the y direction
     */
    public void incrementDisp(double x, double y) {
	this.dispX += x;
	this.dispY += y;
    }

    /**
     * Increment the location of this node by (x,y).  Note that location
     * values are merely recorded until moveToLocation is called.
     *
     * @param    x    the amount to move in the x direction
     * @param    y    the amount to move in the y direction
     */
    public void increment(double x, double y) {
	this.x += x;
	this.y += y;
    }

    /**
     * Decrement the displacement recorded for this node by (x,y).
     *
     * @param    x    the additional amount to displace in the -x direction
     * @param    y    the additional amount to displace in the -y direction
     */
    public void decrementDisp(double x, double y) {
	this.dispX -= x;
	this.dispY -= y;
    }

    /**
     * Decrement the location of this node by (x,y).  Note that location
     * values are merely recorded until moveToLocation is called.
     *
     * @param    x    the amount to move in the -x direction
     * @param    y    the amount to move in the -y direction
     */
    public void decrement(double x, double y) {
	this.x -= x;
	this.y -= y;
    }

    /**
     * Return the current X value for this LayoutNode.
     *
     * @return        current X value
     */
    public double getX() {
	return this.x;
    }

    /**
     * Return the current Y value for this LayoutNode.
     *
     * @return        current Y value
     */
    public double getY() {
	return this.y;
    }

    /**
     * Return the current X displacement value for this LayoutNode.
     *
     * @return        current X displacement value
     */
    public double getXDisp() {
	return this.dispX;
    }

    /**
     * Return the current Y displacement value for this LayoutNode.
     *
     * @return        current Y displacement value
     */
    public double getYDisp() {
	return this.dispY;
    }

    /**
     * Return the euclidean distance between this node and another node
     *
     * @param    otherNode    the node to measure the distance to
     * @return        the euclidean distance from this node to otherNode
     */
    public double distance(LayoutAbstractNode otherNode) {
	double deltaX = this.x - otherNode.getX();
	double deltaY = this.y - otherNode.getY();

	return Math.max(EPSILON, Math.sqrt((deltaX * deltaX) + (deltaY * deltaY)));
    }

    /**
     * Return the euclidean distance between this node and a location
     *
     * @param    uX    the X location to measure the distance to
     * @param    uY    the Y location to measure the distance to
     * @return        the euclidean distance from this node to (uX,uY)
     */
    public double distance(double uX, double uY) {
	double deltaX = this.x - uX;
	double deltaY = this.y - uY;

	return Math.max(EPSILON, Math.sqrt((deltaX * deltaX) + (deltaY * deltaY)));
    }

}