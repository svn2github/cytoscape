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

import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;
import csplugins.layout.Profile;

import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

import java.awt.Dimension;

import java.util.ArrayList;


/**
 * Lays out the nodes in a graph using a modification of the Fruchterman-Rheingold
 * algorithm.
 * <p>
 * The basic layout algorithm follows from the paper:
 * <em>"Graph Drawing by Force-Directed Placement"</em>
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
	 * Sets the number of iterations for each update
	 */
	private static int update_iterations = -1; // 0 means we only update at the end

	/**
	 * The multipliers and computed result for the
	 * attraction and repulsion values.
	 */
	private double attraction_multiplier = .3;
	private double attraction_constant;
	private double repulsion_multiplier = 0.04;
	private double repulsion_constant;
	private double gravity_multiplier = 1;
	private double gravity_constant;

	/**
	 * conflict_avoidance is a constant force that
	 * gets applied when two vertices are very close
	 * to each other.
	 */
	private double conflict_avoidance = 20;

	/**
	 * max_distance_factor is the portion of the graph
	 * beyond which repulsive forces will not operate.
	 */
	private double max_distance_factor = 100;

	/**
	 * maxDistance is the actual calculated distance
	 * beyond which repulsive forces will not operate.
	 * This value takes into account max_distance_factor,
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
	private double temperature = 90;

	/**
	 * The number of iterations to run.
	 */
	private int nIterations = 200;

	/**
	 * This ArrayList is used to calculate the slope of the magnitude
	 * of the displacement.  When the slope is (approximately) 0, we're
	 * done.
	 */
	private ArrayList<Double> displacementArray;

	/**
	 * The partition we're laying out
	 */
	private LayoutPartition partition;

	/**
 	 * The width and height of the layout
 	 */
	private double width = 0;
	private double height = 0;

	/**
	 * Profile data -- not used, for now
	Profile initProfile;
	Profile iterProfile;
	Profile repulseProfile;
	Profile attractProfile;
	Profile updateProfile;
	 */

	/**
	 * This is the constructor for the bioLayout algorithm.
	 */
	public BioLayoutFRAlgorithm(boolean supportEdgeWeights) {
		super();
		logger = CyLogger.getLogger(BioLayoutFRAlgorithm.class);

		supportWeights = supportEdgeWeights;

		displacementArray = new ArrayList<Double>(100);
		this.initializeProperties();
	}

	/**
	 * Required methods (and overrides) for AbstractLayout
	 */

	/**
	 * Return the "name" of this algorithm.  This is meant
	 * to be used by programs for deciding which algorithm to
	 * use.  toString() should be used for the human-readable
	 * name.
	 *
	 * @return the algorithm name
	 */
	public String getName() {
		return "Fruchterman-Rheingold";
	}

	/**
	 * Return the "title" of this algorithm.  This is meant
	 * to be used for titles and labels that represent this
	 * algorithm.
	 *
	 * @return the human-readable algorithm name
	 */
	public String toString() {
		if (supportWeights)
			return "Edge-weighted Force-Directed (BioLayout)";
		else

			return "Force-Directed (BioLayout)";
	}

	/**
	 * Sets the number of iterations
	 *
	 * @param value the number of iterations
	 */
	public void setNumberOfIterations(int value) {
		this.nIterations = value;
	}

	/**
	 * Sets the number of iterations
	 *
	 * @param value the number of iterations
	 */
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

	/**
	 * Sets the initial temperature
	 *
	 * @param value the initial temperature value
	 */
	public void setTemperature(String value) {
		Double val = new Double(value);
		temperature = val.doubleValue();
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

	/**
	 * Sets the attraction multiplier used to calculate
	 * the attraction force
	 *
	 * @param value the attraction multiplier
	 */
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

	/**
	 * Sets the repulsion multiplier used to calculate
	 * the repulsion force
	 *
	 * @param value the repulsion multiplier
	 */
	public void setRepulsionMultiplier(String value) {
		Double val = new Double(value);
		repulsion_multiplier = val.doubleValue();
	}

	/**
	 * Sets the gravity multiplier used to calculate
	 * the gravity force
	 *
	 * @param value the gravity multiplier
	 */
	public void setGravityMultiplier(double am) {
		gravity_multiplier = am;
	}

	/**
	 * Sets the gravity multiplier used to calculate
	 * the gravity force
	 *
	 * @param value the gravity multiplier
	 */
	public void setGravityMultiplier(String value) {
		Double val = new Double(value);
		gravity_multiplier = val.doubleValue();
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
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

	/**
	 * Sets the number of iterations to execute between each
	 * screen update.  If the value is 0 or -1, no updates
	 * will be done until the algorithm completes.
	 *
	 * @param value the number of iterations between updates
	 */
	public void setUpdateIterations(int value) {
		update_iterations = value;
	}

	/**
	 * Sets an additional repulsive force to nodes
	 * when they overlap
	 *
	 * @param value the additional repulsive force
	 */
	public void setConflictAvoidanceForce(String value) {
		Double val = new Double(value);
		conflict_avoidance = val.doubleValue();
	}

	/**
	 * Sets an additional repulsive force to nodes
	 * when they overlap
	 *
	 * @param value the additional repulsive force
	 */
	public void setConflictAvoidanceForce(double value) {
		conflict_avoidance = value;
	}

	/**
	 * Sets the percentage of the graph beyond which we
	 * don't calculate repulsive forces.
	 *
	 * @param value the maximum distance factor
	 */
	public void setMaxDistanceFactor(String value) {
		Double val = new Double(value);
		max_distance_factor = val.doubleValue();
	}

	/**
	 * Sets the percentage of the graph beyond which we
	 * don't calculate repulsive forces.
	 *
	 * @param value the maximum distance factor
	 */
	public void setMaxDistanceFactor(double value) {
		max_distance_factor = value;
	}

	/**
	 * Reads all of our properties from the cytoscape properties map and sets
	 * the values as appropriates.
	 */
	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */
		layoutProperties.add(new Tunable("algorithm_settings",
		                                 "Algorithm settings",
		                                 Tunable.GROUP, new Integer(9)));
		layoutProperties.add(new Tunable("repulsion_multiplier",
		                                 "Multiplier to calculate the repulsion force",
		                                 Tunable.DOUBLE, new Double(0.04)));
		layoutProperties.add(new Tunable("attraction_multiplier",
		                                 "Divisor to calculate the attraction force",
		                                 Tunable.DOUBLE, new Double(0.03)));
		layoutProperties.add(new Tunable("gravity_multiplier",
		                                 "Multiplier to calculate the gravity force",
		                                 Tunable.DOUBLE, new Double(1)));
		layoutProperties.add(new Tunable("iterations", "Number of iterations", Tunable.INTEGER,
		                                 new Integer(500)));
		layoutProperties.add(new Tunable("temperature", "Initial temperature", Tunable.DOUBLE,
		                                 new Double(80)));
		layoutProperties.add(new Tunable("spread_factor", "Amount of extra room for layout",
		                                 Tunable.DOUBLE, new Double(2)));
		layoutProperties.add(new Tunable("update_iterations",
		                                 "Number of iterations before updating display",
		                                 Tunable.INTEGER, new Integer(0)));
		layoutProperties.add(new Tunable("conflict_avoidance",
		                                 "Constant force applied to avoid conflicts",
		                                 Tunable.DOUBLE, new Double(20.0)));
		layoutProperties.add(new Tunable("max_distance_factor",
		                                 "Percent of graph used for node repulsion calculations",
		                                 Tunable.DOUBLE, new Double(20.0)));
		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
		updateSettings(true);
	}

	/**
	 *  update our tunable settings
	 */
	public void updateSettings() {
		updateSettings(false);
	}

	/**
	 *  update our tunable settings
	 *
	 * @param force whether or not to force the update
	 */
	public void updateSettings(boolean force) {
		super.updateSettings(force);

		Tunable t = layoutProperties.get("repulsion_multiplier");
		if ((t != null) && (t.valueChanged() || force)) {
			setRepulsionMultiplier(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("attraction_multiplier");
		if ((t != null) && (t.valueChanged() || force)) {
			setAttractionMultiplier(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("gravity_multiplier");
		if ((t != null) && (t.valueChanged() || force)) {
			setGravityMultiplier(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("iterations");
		if ((t != null) && (t.valueChanged() || force)) {
			setNumberOfIterations(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("temperature");
		if ((t != null) && (t.valueChanged() || force)) {
			setTemperature(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("spread_factor");
		if ((t != null) && (t.valueChanged() || force)) {
			setSpreadFactor(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("update_iterations");
		if ((t != null) && (t.valueChanged() || force)) {
			setUpdateIterations(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("conflict_avoidance");
		if ((t != null) && (t.valueChanged() || force)) {
			setConflictAvoidanceForce(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("max_distance_factor");
		if ((t != null) && (t.valueChanged() || force)) {
			setMaxDistanceFactor(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}
	}

	/**
	 * Perform a layout
	 */
	public void layoutPartion(LayoutPartition partition) {
		this.partition = partition;

		Dimension initialLocation = null;

		/* Get all of our profiles */
		/*
		        initProfile = new Profile();
		        iterProfile = new Profile();
		        repulseProfile = new Profile();
		        attractProfile = new Profile();
		        updateProfile = new Profile();

		        initProfile.start();
		*/

		// Calculate a bounded rectangle for our
		// layout.  This is roughly the area of all
		// nodes * 2
		calculateSize();

		logger.info("Laying out " + partition.nodeCount()
		            + " nodes and " + partition.edgeCount() + " edges: ");

		// Initialize our temperature
		double temp;

		if (temperature == 0) {
			temp = Math.sqrt(this.width*this.height)/2;
		} else {
			temp = Math.sqrt(this.width*this.height) * this.temperature/100;
		}

		// Figure out our starting point
		if (selectedOnly) {
			initialLocation = partition.getAverageLocation();
		}

		// Randomize our points, if any points lie
		// outside of our bounds
		if (randomize)
			partition.randomizeLocations();

		// Calculate our force constant
		calculateForces();

		// Calculate our edge weights
		partition.calculateEdgeWeights();
		// initProfile.done("Initialization completed in ");
		taskMonitor.setStatus("Calculating new node positions");
		taskMonitor.setPercentCompleted(1);

		// Main algorithm
		// iterProfile.start();
		int iteration = 0;

		for (iteration = 0; (iteration < nIterations) && !canceled; iteration++) {
			if ((temp = doOneIteration(iteration, temp)) == 0)
				break;

			if (debug || ((update_iterations > 0) && ((iteration % update_iterations) == 0))) {
				if (iteration > 0) {
					// Actually move the pieces around
					for (LayoutNode v: partition.getNodeList()) {
						// if this is locked, the move just resets X and Y
						v.moveToLocation();
						// logger.debug("Node "+v.getIdentifier()+" moved to "+v.getX()+","+v.getY());
					}
					networkView.updateView();
				}

				if (debug) {
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}

			taskMonitor.setStatus("Calculating new node positions - " + iteration);
			taskMonitor.setPercentCompleted((int) Math.rint((iteration * 100) / nIterations));
		}

		// iterProfile.done("Iterations complete in ");
		// logger.debug("Attraction calculation portion of iterations took "+attractProfile.getTotalTime()+"ms");
		// logger.debug("Repulsion calculation portion of iterations took "+repulseProfile.getTotalTime()+"ms");
		// logger.debug("Update portion of iterations took "+updateProfile.getTotalTime()+"ms");
		taskMonitor.setStatus("Updating display");

		// Actually move the pieces around
		// Note that we reset our min/max values before we start this
		// so we can get an accurate min/max for paritioning
		partition.resetNodes();

		for (LayoutNode v: partition.getNodeList()) {
			partition.moveNodeToLocation(v);
		}

		// Not quite done, yet.  If we're only laying out selected nodes, we need
		// to migrate the selected nodes back to their starting position
		if (selectedOnly) {
			double xDelta = 0.0;
			double yDelta = 0.0;
			Dimension finalLocation = partition.getAverageLocation();
			xDelta = finalLocation.getWidth() - initialLocation.getWidth();
			yDelta = finalLocation.getHeight() - initialLocation.getHeight();

			for (LayoutNode v: partition.getNodeList()) {
				if (!v.isLocked()) {
					v.decrement(xDelta, yDelta);
					partition.moveNodeToLocation(v);
				}
			}
		}

		logger.info("Layout complete after " + iteration + " iterations");
	}

	/**
	 * This executes a single iteration of the FR algorithm.
	 *
	 * @param iteration The current interation.
	 * @param temp The current temperature factor.
	 * @return an updated temperature factor.
	 */
	public double doOneIteration(int iteration, double temp) {
		double xAverage = 0;
		double yAverage = 0;

		// repulseProfile.start();
		// Calculate repulsive forces
		for (LayoutNode v: partition.getNodeList()) {
			if (!v.isLocked()) {
				xAverage += v.getX()/partition.nodeCount();
				yAverage += v.getY()/partition.nodeCount();
			}
		}

		for (LayoutNode v: partition.getNodeList()) {
			if (!v.isLocked()) {
				calculateRepulsion(v);
				if (gravity_constant != 0)
					calculateGravity(v,xAverage,yAverage);
			}
		}

		// repulseProfile.checkpoint();

		// Dump the current displacements
		// print_disp();

		// attractProfile.start();
		// Calculate attractive forces

/// for e in E do begin
		for (LayoutEdge e: partition.getEdgeList()) {
			calculateAttraction(e);
		}
/// end

		// attractProfile.checkpoint();

		// Dump the current displacements
		// print_disp();

		// Dampen & update
		double xDispTotal = 0;
		double yDispTotal = 0;
		// updateProfile.start();

/// for v in V do begin
		for (LayoutNode v: partition.getNodeList()) {
			if (v.isLocked())
				continue;

			calculatePosition(v, temp);

			xDispTotal += Math.abs(v.getXDisp());
			yDispTotal += Math.abs(v.getYDisp());
		}
/// end

		// Translate back to the middle (or to the starting point,
		// if we're dealing with a selected group
		if (!selectedOnly) {
			for (LayoutNode v: partition.getNodeList()) {
				v.decrement(xAverage - (width / 2), yAverage - (height / 2));
			}
		}

		// updateProfile.checkpoint();

		// Test our total x and y displacement to see if we've
		// hit our completion criteria
		if (complete(xDispTotal, yDispTotal))
			return 0;

// t := cool(t)
		return cool(temp, iteration);
	}

	/**
	 * calculate the slope of the total displacement over the last 10 iterations.  If its positive or 0
	 * we're done.
	 *
	 */
	private boolean complete(double xDisp, double yDisp) {
		Double disp = new Double(Math.sqrt((xDisp * xDisp) + (yDisp * yDisp)));

		displacementArray.add(disp);

		Object[] dispArray = displacementArray.toArray();

		if (dispArray.length < 99)
			return false;

		double averageSlope = 0;
		double averageValue = ((Double) dispArray[0]).doubleValue() / dispArray.length;

		for (int i = 1; i < dispArray.length; i++) {
			averageSlope += ((((Double) dispArray[i]).doubleValue()
			                 - ((Double) dispArray[i - 1]).doubleValue()) / dispArray.length);
			averageValue += (((Double) dispArray[i]).doubleValue() / dispArray.length);
		}

		// logger.debug("Total displacement = "+disp.doubleValue()+" Average slope = "+averageSlope);
		// 5% a reasonable criteria?
		// if (Math.abs(averageSlope) < Math.abs(averageValue)*.001) return true;
		if (Math.abs(averageSlope) < .001)
			return true;

		if (displacementArray.size() > 99)
			displacementArray.remove(0);

		return false;
	}

	/**
	 * calculate the repulsive forces and offsets for
	 * each vertex.
	 *
	 * @param v LayoutNode we're calculating repulsive forces for
	 */
	private void calculateRepulsion(LayoutNode v) {
/// v.disp := 0;
		v.setDisp(0, 0);

		double width = v.getWidth();
		double height = v.getHeight();
		double radius = v.getWidth() / 2;

/// for u in V do
		for (LayoutNode u: partition.getNodeList()) {
			double dx = v.getX() - u.getX();
			double dy = v.getY() - u.getY();

/// if (u # v) then begin
			if (v == u)
				continue;

			// Get the 
			// double xSign = Math.signum(v.getX() - u.getX());
			// double ySign = Math.signum(v.getY() - u.getY());

/// delta := v.pos - u.pos
			// Get our euclidean distance
			double deltaDistance = v.distance(u);

			if (deltaDistance == 0.0)
				deltaDistance = EPSILON;

			double fr = forceR(repulsion_constant, deltaDistance);

			// If its too close, increase the force by a constant
			if (deltaDistance < (radius + (u.getWidth() / 2))) {
				// logger.debug("Applying conflict_avoidance force: "+conflict_avoidance);
				fr += conflict_avoidance;
			}

			if (Double.isNaN(fr)) {
				fr = 500;
			}

			/*
			            logger.debug("Repulsive force between "+v.getIdentifier()
			                             +" and "+u.getIdentifier()+" is "+force);
			            logger.debug("   distance = "+deltaDistance);
			            logger.debug("   incrementing "+v.getIdentifier()+" by ("+
			                                   force+", "+force+")");
			*/

			// Adjust the displacement.  In the case of doing selectedOnly,
			// we increase the force to enhance the discrimination power.
			// Also note that we only update the displacement of the movable
			// node since the other node won't move anyways.
/// v.disp := v.disp + (delta/abs(delta)) * fr(abs(delta))
			double xVector = (dx/deltaDistance)*fr;
			double yVector = (dy/deltaDistance)*fr;
			if (v.isLocked()) {
				return; // shouldn't happen
			} else if (u.isLocked()) {
				v.incrementDisp(xVector * 2, yVector * 2);
			} else {
				v.incrementDisp(xVector, yVector);
			}
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
		LayoutNode v = e.getSource();
		LayoutNode u = e.getTarget();
		double dx = v.getX() - u.getX();
		double dy = v.getY() - u.getY();

/// delta := e.v.pos - e.u.pos
		double deltaDistance = v.distance(u);

		double fa = forceA(attraction_constant, deltaDistance, e.getWeight());

		if (Double.isNaN(fa)) {
			fa = EPSILON;
		}

		// Adjust the displacement.  In the case of doing selectedOnly,
		// we increase the force to enhance the discrimination power.
		// Also note that we only update the displacement of the movable
		// node since the other node won't move anyways.

/// e.v.disp := e.v.disp - (delta/abs(delta)) * fa(abs(delta))
/// e.u.disp := e.u.disp + (delta/abs(delta)) * fa(abs(delta))
		double xVector = dx*fa;
		double yVector = dy*fa;
		if (u.isLocked() && v.isLocked()) {
			return; // shouldn't happen
		} else if (u.isLocked()) {
			v.decrementDisp(xVector * 2, yVector * 2);
		} else if (v.isLocked()) {
			u.incrementDisp(xVector * 2, yVector * 2);
		} else {
			v.decrementDisp(xVector, yVector);
			u.incrementDisp(xVector, yVector);
		}
	}

	/**
	 * Calculate the gravity (pull towards the center) force.
	 *
	 * @param v the node we're pulling
	 * @param xAverage the X portion of the location that's pulling us
	 * @param yAverage the Y portion of the location that's pulling us
	 */
	private void calculateGravity(LayoutNode v,double xAverage, double yAverage)
	{
		double dx = v.getX() - xAverage;
		double dy = v.getY() - yAverage;
		double distance = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
		//double theta = Math.atan(dy/dx);
		//double xSign = Math.signum(dx);
		//double ySign = Math.signum(dy);
		if(distance == 0) distance = EPSILON;
		double phi = (1 +v.getDegree())/3;
		double force = gravity_constant*distance*phi;
		double xVector = dx*force;
		double yVector = dy*force;
		if (v.isLocked()) {
			return; 
		}// shouldn't happen
		
		else {
			// logger.debug("Gravity adjustment = "+xVector+", "+yVector);
			v.decrementDisp( xVector, yVector);
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
/// v.pos := v.pos + (v.disp/|v.disp|) * min (v.disp, t);
	private void calculatePosition(LayoutNode v, double temp) {
		double deltaDistance = v.distance(v.getXDisp(), v.getYDisp());

		double newXDisp = v.getXDisp() / deltaDistance * Math.min(deltaDistance, temp);

		if (Double.isNaN(newXDisp)) {
			newXDisp = 0;
		}

		double newYDisp = v.getYDisp() / deltaDistance * Math.min(deltaDistance, temp);

		if (Double.isNaN(newYDisp)) {
			newYDisp = 0;
		}
		v.increment(newXDisp, newYDisp);

/// v.pos.x := min(W/2, max(-W/2, v.pos.x));
/// v.pos.y := min(L/2, max(-L/2, v.pos.y));
	}

	/**
	 * Cools the current temperature
	 *
	 * @param temp the current temperature
	 * @param iteration the iteration number
	 * @return the new temperature
	 */
	private double cool(double temp, int iteration) {
		temp *= (1.0 - ((double)iteration / (double)nIterations));

		return temp;
	}

	/**
	 * Calculate the width and height of the new graph.  If the graph already has been laid
	 * out, then the width and height should be resonable, so use those.  Otherwise, calculate
	 * a width and height based on the area covered by the existing graph.
	 */
	private void calculateSize() {
		// double spreadFactor = Math.max(spread_factor, edgeList.length/nodeList.length);
		// LayoutNode v0 = (LayoutNode)nodeList.get(0); // Get the first vertex to get to the class variables
		int nodeCount = partition.nodeCount();
		int unLockedNodes = nodeCount - partition.lockedNodeCount();
		double spreadFactor = spread_factor;
		double averageWidth = partition.getWidth() / partition.nodeCount();
		double averageHeight = partition.getHeight() / partition.nodeCount();
		double current_area = (partition.getMaxX() - partition.getMinX()) * (partition.getMaxY()
		                                                                    - partition.getMinY());
		double node_area = partition.getWidth() * partition.getHeight();

		if (selectedOnly || (current_area > node_area)) {
			this.width = (partition.getMaxX() - partition.getMinX()) * spreadFactor;
			this.height = (partition.getMaxY() - partition.getMinY()) * spreadFactor;
			// make it square
			this.width = Math.max(this.width, this.height);
			this.height = this.width;
		} else {
			this.width = Math.sqrt(node_area) * spreadFactor;
			this.height = Math.sqrt(node_area) * spreadFactor;

			// logger.debug("spreadFactor = "+spreadFactor);
		}

		this.maxVelocity = Math.max(Math.max(averageWidth * 2, averageHeight * 2),
		                            Math.max(width, height) / maxVelocity_divisor);
		this.maxDistance = Math.max(Math.max(averageWidth * 10, averageHeight * 10),
		                            Math.min(width, height) * max_distance_factor / 100);

		// logger.debug("Size: "+width+" x "+height);
		// logger.debug("maxDistance = "+maxDistance);
		// logger.debug("maxVelocity = "+maxVelocity);
		/*
		*/
	}

	/**
	 * Calculate the attraction and repulsion constants.
	 */
	private void calculateForces() {
		double force = Math.sqrt((this.height * this.width) / partition.nodeCount());
		attraction_constant = force * attraction_multiplier;
		repulsion_constant = force * repulsion_multiplier;
		gravity_constant = gravity_multiplier;

/*
		        logger.debug("attraction_constant = "+attraction_constant
		                        +", repulsion_constant = "+repulsion_constant
						                +", gravity_constant = "+gravity_constant);
*/
	}

	/**
	 * Calculate the repulsive force
	 *
	 * @param k the repulsion constant
	 * @param distance the distance between the vertices
	 * @return the repulsive force
	 */
/// fr(z) := begin return k*k/z end;
	private double forceR(double k, double distance) {
		// We want to bound the distance over which
		// the repulsive force acts
		// Should we do this??
		if (distance > maxDistance)
			return 0;

		return ((k * k) / distance);
	}

	/**
	 * Calculate the attractive force
	 *
	 * @param k the attraction constant
	 * @param distance the distance between the vertices
	 * @param weight the edge weight
	 * @return the attractive force
	 */
/// fa(z) := begin return z*z/k end;
	private double forceA(double k, double distance, double weight) {
		return ((distance / k) * weight);
	}
}
