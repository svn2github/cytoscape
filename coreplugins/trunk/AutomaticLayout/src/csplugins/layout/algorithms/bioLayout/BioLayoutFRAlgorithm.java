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
import java.util.ListIterator;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.awt.Dimension;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.data.*;
import cytoscape.task.TaskMonitor;
import giny.view.*;

import csplugins.layout.algorithms.bioLayout.LayoutNode;
import csplugins.layout.algorithms.bioLayout.LayoutEdge;
import csplugins.layout.algorithms.bioLayout.Profile;

/**
 * Lays out the nodes in a graph using a modification of the Fruchterman-Rheingold
 * algorithm.
 * <p>
 * The basic layout algorithm follows from the paper:
 * <em>"Graph Drawing by Force-directed Placement"</em>
 * by Thomas M.J. Fruchterman and Edward M. Reingold.
 * <p>
 * The algorithm has been modified to take into account edge weights, which
 * allows for its use for laying out similarity networks, which are useful
 * for biological problems.
 *
 * @author <a href="mailto:scooter@cgl.ucsf.edu">Scooter Morris</a>
 * @version 0.9
 */


public class BioLayoutFRAlgorithm extends BioLayoutAlgorithm {
	/**
	 * Property key for getting various tuning values
	 */
	private static final String propPrefix = "bioLayout.fr.";

	/**
	 * Tuning values
	 */
	private static final String rMultProp = "repulsion_multiplier";
	private static final String aMultProp = "attraction_multiplier";
	private static final String iterationsProp = "iterations";
	private static final String temperatureProp = "temperature";
	private static final String spreadFactorProp = "spread_factor";
	private static final String updateIterationsProp = "update_iterations";
	private static final String conflictProp = "conflict_avoidance";
	private static final String maxDistanceProp = "max_distance_factor";

	/**
	 * Sets the number of iterations for each update
	 */
	private static int update_iterations = -1; // 0 means we only update at the end

	/**
	 * The multipliers and computed result for the
	 * attraction and repulsion values.
	 */
	private double attraction_multiplier = .15;
	private double attraction_constant;

	private double repulsion_multiplier = 0.04;
	private double repulsion_constant;

	/**
	 * conflict_avoidance is a constant force that
	 * gets applied when two vertices are very close
	 * to each other.
	 */
	private double conflict_avoidance = 20;

	/**
	 * maxDistanceFactor is the portion of the graph
	 * beyond which repulsive forces will not operate.
	 */
	private double maxDistanceFactor = 10;

	/**
	 * maxDistance is the actual calculated distance
	 * beyond which repulsive forces will not operate.
	 * This value takes into account maxDistanceFactor,
	 * but also the size of the nodes in comparison to
	 * the size of the graph.
	 */
	private double maxDistance;

	/**
	 * This limits the velocity to no more than 1/maxVelocity_divisor
	 * of the width or height per iteration
	 */
	private double maxVelocity_divisor = 25;
	private double maxVelocity;

	/**
	 * The spread factor -- used to give extra space to expand
	 */
	private double spread_factor = 2;

	/**
	 * The initial temperature factor.  This will get damped
	 * out through the iterations
	 */
	private double temperature = 100;

	/**
	 * The number of iterations to run.
	 */
	private int nIterations = 1000;

	/**
	 * This ArrayList is used to calculate the slope of the magnitude
	 * of the displacement.  When the slope is (approximately) 0, we're
	 * done.
	 */
	private ArrayList displacementArray;

	/**
	 * Profile data
	 */
	Profile initProfile;
	Profile iterProfile;
	Profile	repulseProfile;
	Profile attractProfile;
	Profile updateProfile;

	/**
	 * This is the constructor for the bioLayout algorithm.
	 * @param networkView the CyNetworkView of the network 
	 *                    are going to lay out.
	 */
	public BioLayoutFRAlgorithm (CyNetworkView networkView) {
		super (networkView, propPrefix);

		displacementArray = new ArrayList(100);
		initializeProperties();
	}

	/**
	 * Sets the number of iterations
	 *
	 * @param value the number of iterations
	 */
	public void setNumberOfIterations(int value) {
		this.nIterations = value;
	}

	public void setNumberOfIterations(String value) {
		Integer val = new Integer(value);
		nIterations = val.intValue();
	}

	/**
	 * Sets the initial temperature
	 *
	 * @param value the initial temperature value
	 */
	public void setTemperature(double value) {
		this.temperature = value;
	}

	public void setTemperature(String value) {
		Integer val = new Integer(value);
		temperature = val.intValue();
	}

	/**
	 * Sets the attraction multiplier used to calculate
	 * the attraction force
	 *
	 * @param value the attraction multiplier
	 */
	public void setAttractionMultiplier(double am) {
		attraction_multiplier = am;
	}

	public void setAttractionMultiplier(String value) {
		Double val = new Double(value);
		attraction_multiplier = val.doubleValue();
	}

	/**
	 * Sets the repulsion multiplier used to calculate
	 * the repulsion force
	 *
	 * @param value the repulsion multiplier
	 */
	public void setRepulsionMultiplier(double am) {
		repulsion_multiplier = am;
	}

	public void setRepulsionMultiplier(String value) {
		Double val = new Double(value);
		repulsion_multiplier = val.doubleValue();
	}

	/**
	 * Sets the spread factor used to provide space for
	 * the graph larger than the area of the nodes themselves.
	 * The graph space will be (width*spread_factor, height*spread_factor)
	 *
	 * @param value the spread factor
	 */
	public void setSpreadFactor(double value) {
		spread_factor = value;
	}

	public void setSpreadFactor(String value) {
		Double val = new Double(value);
		spread_factor = val.doubleValue();
	}

	/**
	 * Sets the number of iterations to execute between each
	 * screen update.  If the value is 0 or -1, no updates
	 * will be done until the algorithm completes.
	 *
	 * @param value the number of iterations between updates
	 */
	public void setUpdateIterations(String value) {
		Integer val = new Integer(value);
		update_iterations = val.intValue();
	}

	public void setUpdateIterations(int value) {
		update_iterations = value;
	}

	public void setTaskMonitor(TaskMonitor t) {
		this.taskMonitor = t;
	}

	/**
	 * Reads all of our properties from the cytoscape properties map and sets
	 * the values as appropriates.
	 */
	private void initializeProperties() {
		// Initialize our tunables from the properties
		Properties properties = CytoscapeInit.getProperties();
		String pValue = null;

		if ( (pValue = properties.getProperty(propPrefix+rMultProp)) != null ) {
			setRepulsionMultiplier(pValue);
		}
		if ( (pValue = properties.getProperty(propPrefix+aMultProp)) != null ) {
			setAttractionMultiplier(pValue);
		}
		if ( (pValue = properties.getProperty(propPrefix+iterationsProp)) != null ) {
			setNumberOfIterations(pValue);
		}
		if ( (pValue = properties.getProperty(propPrefix+temperatureProp)) != null ) {
			setTemperature(pValue);
		}
		if ( (pValue = properties.getProperty(propPrefix+spreadFactorProp)) != null ) {
			setSpreadFactor(pValue);
		}
		if ( (pValue = properties.getProperty(propPrefix+updateIterationsProp)) != null ) {
			setUpdateIterations(pValue);
		}
	}

	/**
	 * Perform a layout
	 */
	public void layout() {
		Iterator iter = null;
		Dimension initialLocation = null;
		// Initialize all of our values.  This will create
		// our internal objects and initialize them
		// local_initialize();

		// Calculate a bounded rectangle for our
		// layout.  This is roughly the area of all
		// nodes * 2
		calculateSize();

		// Initialize our temperature
		double temp;
		if (temperature == 0) {
			temp = this.width / 10; 
		} else {
			temp = this.temperature;
		}

		// Figure out our starting point
		if (selectedOnly)
			initialLocation = calculateAverageLocation();

		// Randomize our points, if any points lie
		// outside of our bounds
		if (randomize) randomizeLocations();

		// Calculate our force constant
		calculateForces();

		// Calculate our edge weights
		calculateEdgeWeights();

		taskMonitor.setStatus("Calculating new node positions");
		taskMonitor.setPercentCompleted(1);
		// Main algorithm
		int iteration = 0;
		for (iteration = 0; (iteration < nIterations) && !cancel; iteration++) {
			if ((temp = doOneIteration(iteration, temp)) == 0) break;
			
			if (debug || (update_iterations > 0 && (iteration % update_iterations == 0))) {
				if (iteration > 0) {
					// Actually move the pieces around
					iter = nodeList.iterator();
					while (iter.hasNext()) {
						LayoutNode v = (LayoutNode) iter.next();
						// if this is locked, the move just resets X and Y
						v.moveToLocation();
						// System.out.println("Node "+v.getIdentifier()+" moved to "+v.getX()+","+v.getY());
					}
					networkView.updateView();
				}
				if (debug) {
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {}
				}
			}
			taskMonitor.setStatus("Calculating new node positions - "+iteration);
			taskMonitor.setPercentCompleted((int)Math.rint(iteration*100/nIterations));
		}

		taskMonitor.setStatus("Updating display");

		double xDelta = 0.0;
		double yDelta = 0.0;
		if (selectedOnly) {
			Dimension finalLocation = calculateAverageLocation();
			xDelta = finalLocation.getWidth()-initialLocation.getWidth();
			yDelta = finalLocation.getHeight()-initialLocation.getHeight();
		}

		// Actually move the pieces around
		iter = nodeList.iterator();
		while (iter.hasNext()) {
			LayoutNode v = (LayoutNode) iter.next();
			if (selectedOnly && !v.isLocked()) {
				v.decrement(xDelta, yDelta);
			}
			v.moveToLocation();
		}
		// System.out.println("Layout complete after "+iteration+" iterations");
	}

	/**
	 * This executes a single iteration of the FR algorithm.
	 *
	 * @param iteration The current interation.
	 * @param temp The current temperature factor.
	 * @return an updated temperature factor.
	 */
	public double doOneIteration(int iteration, double temp)
	{
		Iterator iter = nodeList.iterator();

		// Calculate repulsive forces
		while (iter.hasNext()) {
			LayoutNode v = (LayoutNode)iter.next();
			if (!v.isLocked()) {
				calculateRepulsion(v);
			}
		}

		// Dump the current displacements
		// print_disp();

		// Calculate attractive forces
		ListIterator lIter = edgeList.listIterator();
		while (lIter.hasNext()) {
			LayoutEdge e = (LayoutEdge)lIter.next();
			calculateAttraction(e);
		}

		// Dump the current displacements
		// print_disp();

		// Dampen & update
		double xAverage = 0;
		double yAverage = 0;
		double xDispTotal = 0;
		double yDispTotal = 0;
		iter = nodeList.iterator();
		while (iter.hasNext()) {
			LayoutNode v = (LayoutNode)iter.next();
			if (v.isLocked()) continue;
			calculatePosition(v, temp);
				
			xAverage += v.getX()/nodeList.size();
			yAverage += v.getY()/nodeList.size();
			xDispTotal += Math.abs(v.getXDisp());
			yDispTotal += Math.abs(v.getYDisp());
		}

		// Translate back to the middle (or to the starting point,
		// if we're dealing with a selected group
		if (!selectedOnly) {
			iter = nodeList.iterator();
			while (iter.hasNext()) {
				LayoutNode v = (LayoutNode)iter.next();
				v.decrement(xAverage-width/2,yAverage-height/2);
			}
		}

		// Test our total x and y displacement to see if we've
		// hit our completion criteria
		if (complete(xDispTotal, yDispTotal)) return 0;

		// cool
		return cool(temp, iteration);
	}

	/**
	 * calculate the slope of the total displacement over the last 10 iterations.  If its positive or 0
	 * we're done.
	 *
	 */
	private boolean complete(double xDisp, double yDisp) {
		Double disp = new Double(Math.sqrt(xDisp*xDisp+yDisp*yDisp));
		
		displacementArray.add(disp);
		
		Object dispArray[] = displacementArray.toArray();
		if (dispArray.length < 99) return false;
		double averageSlope = 0;
		double averageValue = ((Double)dispArray[0]).doubleValue()/dispArray.length;
		for (int i=1; i < dispArray.length; i++) {
			averageSlope += (((Double)dispArray[i]).doubleValue()-((Double)dispArray[i-1]).doubleValue())/dispArray.length;
			averageValue += ((Double)dispArray[i]).doubleValue()/dispArray.length;
		}
		// System.out.println("Total displacement = "+disp.doubleValue()+" Average slope = "+averageSlope);
		// 5% a reasonable criteria?
		// if (Math.abs(averageSlope) < Math.abs(averageValue)*.001) return true;
		if (Math.abs(averageSlope) < .001) return true;
			
		if (displacementArray.size() > 99)
			displacementArray.remove(0);

		return false;
	}

	/**
	 * calculate the repulsive forces and offsets for
	 * each vertex.
	 *
	 * @param v1 LayoutNode we're calculating repulsive forces for
	 */
	private void calculateRepulsion(LayoutNode v1) {
		// Initialize
		v1.setDisp(0,0);
		Iterator iter = nodeList.iterator();
		while (iter.hasNext()) {
			LayoutNode v2 = (LayoutNode)iter.next();
			if (v1 == v2) continue;

			double xDelta = v1.getX() - v2.getX();
			// If they are on top of each other, offset
			if (xDelta == 0.0) xDelta = v1.getWidth()*Math.random()*2;
			double yDelta = v1.getY() - v2.getY();
			if (yDelta == 0.0) yDelta = v1.getHeight()*Math.random()*2;
			double deltaDistance = v1.distance(v2);
			double force = forceR(repulsion_constant,deltaDistance);

			// If its too close, increase the force by a constant
			if (xDelta < ((v1.getWidth() + v2.getWidth())/2) ||
			    yDelta < ((v1.getHeight() + v2.getHeight())/2)) {
				force += conflict_avoidance;
			}
			if (Double.isNaN(force)) {
				force = EPSILON;
			}
			/* debugln("Repulsive force between "+v1.getIdentifier()
							 +" and "+v2.getIdentifier()+" is "+force); */
			v1.incrementDisp( (xDelta/deltaDistance) * force,
										    (yDelta/deltaDistance) * force);
		}
	}

	/**
	 * calculate the attractive forces and offsets for
	 * each vertex based on their connecting edges and the
	 * corresponding edge weights.
	 *
	 * @param e Edge we're calculating attractive forces for
	 */
	private void calculateAttraction(LayoutEdge e) {
		LayoutNode v1 = e.getSource();
		LayoutNode v2 = e.getTarget();
		double xDelta = v1.getX() - v2.getX();
		double yDelta = v1.getY() - v2.getY();
		double deltaDistance = v1.distance(v2);

		double force = forceA(attraction_constant,deltaDistance,e.getWeight());
		if (Double.isNaN(force)) {
			force = EPSILON;
		}
		/*
		debugln("Attractive force between "+v1.getIdentifier()
						 +" and "+v2.getIdentifier()+" is "+force);
		debugln("   distance = "+deltaDistance);
		debugln("   constant = "+attraction_constant);
		debugln("   weight = "+e.getWeight());
		*/

		// Adjust the displacement.  In the case of doing selectedOnly,
		// we increase the force to enhance the discrimination power.
		// Also note that we only update the displacement of the movable
		// node since the other node won't move anyways.
		if (v2.isLocked() && v1.isLocked()) {
			return; // shouldn't happen
		} else if (v2.isLocked()) {
			v1.decrementDisp( (xDelta/deltaDistance) * force * 5,
									  		(yDelta/deltaDistance) * force * 5);
		} else if (v1.isLocked()) {
			v2.incrementDisp( (xDelta/deltaDistance) * force * 5,
									  		(yDelta/deltaDistance) * force * 5);
		} else {
			v1.decrementDisp( (xDelta/deltaDistance) * force,
									  		(yDelta/deltaDistance) * force);
			v2.incrementDisp( (xDelta/deltaDistance) * force,
									  		(yDelta/deltaDistance) * force);
		}
	}

	/**
	 * Calculate and update the position to move a vertex.
	 * This routine also handles limiting the velocity and
	 * doing the bounds checking to keep the vertices within
	 * the graphics area.
	 *
	 * @param v LayoutNode we're moving
	 * @param temp double representing the current temperature
	 */
	private void calculatePosition(LayoutNode v, double temp) {
		double deltaDistance = v.distance(v.getXDisp(),v.getYDisp());
		double newXDisp = v.getXDisp() / deltaDistance
											* Math.min(deltaDistance, temp);
		if (Double.isNaN(newXDisp)) {newXDisp = 0;}
		double newYDisp = v.getYDisp() / deltaDistance
											* Math.min(deltaDistance, temp);
		if (Double.isNaN(newYDisp)) {newYDisp = 0;}

		// Governor: this prevents a hysterisis where the values
		// occillate wildly
		// newXDisp = govern(newXDisp);
		// newYDisp = govern(newYDisp);

		// v.setDisp(newXDisp, newYDisp);

		/*
		debugln("calculatePosition: Node "+v.getIdentifier()
						+ " will move by "+newXDisp+", "+newYDisp);

		debug("Node "+v.getIdentifier()+" will move from "+v.printLocation());
		*/
		v.increment(newXDisp, newYDisp);
		// debugln(" to "+v.printLocation());

/*
		boolean bound = false;
		if (bound) {
			// Bound it
			double borderWidth = this.width / 50.0;
			double newXPosition = v.getX();
			if (newXPosition < borderWidth) {
				newXPosition = borderWidth + Math.random() * borderWidth * 2.0;
			} else if (newXPosition > (this.width - borderWidth)) {
				newXPosition = this.width - borderWidth - Math.random() * borderWidth * 2.0;
			}
			double borderHeight = this.width / 50.0;
			double newYPosition = v.getY();
			if (newYPosition < borderHeight) {
				newYPosition = borderHeight + Math.random() * borderHeight * 2.0;
			} else if (newYPosition > (this.height - borderHeight)) {
				newYPosition = this.height - borderHeight - Math.random() * borderHeight * 2.0;
			}
			v.setLocation(newXPosition, newYPosition);
		}

		debugln("Node "+v.getIdentifier()
							+ " moved to "+v.printLocation()+" after bounding");
*/
	}

	/**
	 * Cools the current temperature
	 *
	 * @param temp the current temperature
	 * @param iteration the iteration number
	 * @return the new temperature
	 */
	private double cool(double temp, int iteration) {
		temp *= (1.0-iteration/nIterations);
		return temp;
	}
	
	/**
	 * Calculate the width and height of the new graph.  If the graph already has been laid
	 * out, then the width and height should be resonable, so use those.  Otherwise, calculate
	 * a width and height based on the area covered by the existing graph.
	 */
	private void calculateSize() {
		// double spreadFactor = Math.max(spread_factor, edgeList.length/nodeList.length);
		LayoutNode v0 = (LayoutNode)nodeList.get(0); // Get the first vertex to get to the class variables
		int nodeCount = nodeList.size();
		int unLockedNodes = nodeCount-v0.lockedNodeCount();
		double spreadFactor = spread_factor;
		double averageWidth = v0.getTotalWidth()/nodeList.size();
		double averageHeight = v0.getTotalHeight()/nodeList.size();
		double current_area = (v0.getMaxX()-v0.getMinX()) * (v0.getMaxY()-v0.getMinY());
		double node_area = v0.getTotalWidth()*v0.getTotalHeight();
		if (selectedOnly || (current_area > node_area)) {
			this.width = (v0.getMaxX() - v0.getMinX())*spreadFactor;
			this.height = (v0.getMaxY() - v0.getMinY())*spreadFactor;
			// make it square
			this.width = Math.max(this.width,this.height);
			this.height = this.width;
		} else {
			this.width = Math.sqrt(node_area)*spreadFactor;
			this.height = Math.sqrt(node_area)*spreadFactor;
			// System.out.println("spreadFactor = "+spreadFactor);
		}

		this.maxVelocity = Math.max(Math.max(averageWidth*2,averageHeight*2),
																Math.max(width,height)/maxVelocity_divisor);
		this.maxDistance = Math.max(Math.max(averageWidth*10,averageHeight*10),
															 	Math.max(width,height)/maxDistanceFactor);
		// debugln("Size: "+width+" x "+height);
		// debugln("maxDistance = "+maxDistance);
		// debugln("maxVelocity = "+maxVelocity);
	}

	/**
	 * Calculate the attraction and repulsion constants.
	 */
	private void calculateForces() {
		double force = Math.sqrt(this.height*this.width/nodeList.size());
		attraction_constant = force*attraction_multiplier;
		repulsion_constant = force*repulsion_multiplier;
		/* debugln("attraction_constant = "+attraction_constant
						+", repulsion_constant = "+repulsion_constant); */
	}

	/**
	 * Calculate the repulsive force
	 *
	 * @param k the repulsion constant
	 * @param distance the distance between the vertices
	 * @return the repulsive force
	 */
	private double forceR (double k, double distance) {
		// We want to bound the distance over which
		// the repulsive force acts
		// Should we do this??
		if (distance > maxDistance)
			return 0;
		return ((k*k)/distance);
	}

	/**
	 * Calculate the attractive force
	 *
	 * @param k the attraction constant
	 * @param distance the distance between the vertices
	 * @param weight the edge weight
	 * @return the attractive force
	 */
	private double forceA (double k, double distance, double weight) {
		distance *= weight;
		return ((distance*distance)/k);
	}

	/**
	 * Limit the displacement a node can move in any single step
	 *
	 * @param disp the displacement
	 * @return the new (potentially limited) displacement
	 */
	private double govern (double disp) {
		if (Math.abs(disp) > maxVelocity) {
			if (disp > 0) {
				return maxVelocity;
			} else {
				return (-maxVelocity);
			}
		}
		return disp;
	}
}
