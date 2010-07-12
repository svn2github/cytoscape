package csplugins.layout;

import cytoscape.*;

import cytoscape.view.*;

import giny.view.*;

import java.util.*;

/**
 * This class is intended to be a common interface for Layout Node classes that interact
 * with LayoutPartition class.
 * In particular, it aims at providing convenient handle
 * to information about position, the node itself, the node view.  Many of
 * the methods of this class are wrappers for CyNode or NodeView methods, and
 * these are often wrapped by LayoutPartition methods.
 */
public abstract class LayoutNode {

    // static (class) variables
    static final double EPSILON = 0.0000001D;


    // Instance variables
    protected boolean isLocked = false;
    protected double x;
    protected double y;
    protected double dispX;
    protected double dispY;
    protected ArrayList<LayoutNode> neighbors = null;
    protected int index = -1;

    // instance methods


    // !!!! This methods MUST be overriden by subclasses !!!!


    /**
     * 
     */
    public abstract void moveToLocation();

    /**
     * Accessor function to return the NodeView associated with
     * this LayoutNode.
     *
     * @return    NodeView that is associated with this LayoutNode
     */
    public abstract NodeView getNodeView();

    /**
     * Return the width of this node
     *
     * @return        width of this node
     */
    public abstract double getWidth();

    /**
     * Return the height of this node
     *
     * @return        height of this node
     */
    public abstract double getHeight();

    /**
     * Return the node's identifier.
     *
     * @return        String containing the node's identifier
     */
    public abstract String getIdentifier();

    /**
     * Return a string representation of the node
     *
     * @return        String containing the node's identifier and location
     */
    public abstract String toString();

    /**
     * Return a string with the "type" of the node ("normal", "label")
     *
     * @return        String containing the node's type
     */
    public abstract String getType();

    // End of abstract methods


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
	this.isLocked = true;
    }
	
    /**
     * Register this node as being "unlocked".  Locked nodes are exempt from being moved
     * during layout.  Usually, these are the unselected nodes when a selected-only
     * layout is being executed.  The "unlocked" state is the default.
     */
    public void unLock() {
	this.isLocked = false;
    }

    /**
     * Returns "true" if this node is locked, false otherwise.
     *
     * @return        true if locked, false if unlocked.
     */
    public boolean isLocked() {
	return this.isLocked;
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
    public double distance(LayoutNode otherNode) {
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

    /**
     * Convenience function to keep track of neighbors of this node.  This can be
     * used to improve the performance of algorithms that try to determine the edge
     * partners of nodes.
     *
     * @param    v    LayoutNode that is a neighbor of this LayoutNode
     */
    public void addNeighbor(LayoutNode v) {
	this.neighbors.add(v);
    }

    /**
     * Convenience function to return the list of neighbors (connected nodes) of this node.
     *
     * @return        List of all of the neighbors (nodes with shared edges) of this node.
     */
    public List<LayoutNode> getNeighbors() {
	return this.neighbors;
    }

    /**
     * Return the node's degree (i.e. number of nodes it's connected to).
     *
     * @return        Degree of this node
     */
    public double getDegree() {
	return (double)neighbors.size();
    }

    /**
     * Return a string representation of the node's displacement
     *
     * @return        String containing the node's X,Y displacement
     */
    public String printDisp() {
	String ret = new String("" + dispX + ", " + dispY);

	return ret;
    }

    /**
     * Return a string representation of the node's location
     *
     * @return        String containing the node's X,Y location
     */
    public String printLocation() {
	String ret = new String("" + x + ", " + y);

	return ret;
    }

    /**
     * Returns the index of this LayoutNode.  This is <em>not</em> the same as the
     * rootGraphIndex of the node.  Its primarily used by LayoutPartition to keep
     * track of the offset in the node array that holds this LayoutNode.
     *
     * @return        The index of this node
     * @see    LayoutPartition
     */
    public int getIndex() {
	return this.index;
    }


}