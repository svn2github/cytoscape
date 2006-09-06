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
package bioLayout.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Random;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.data.*;
import cytoscape.task.TaskMonitor;
import giny.view.*;

import csplugins.layout.AbstractLayout;


/**
 * Superclass for the two bioLayout algorithms (KK and FR).
 *
 * @author <a href="mailto:scooter@cgl.ucsf.edu">Scooter Morris</a>
 * @version 0.9
 */

public abstract class bioLayoutAlgorithm extends AbstractLayout {
	/**
	 * Properties
	 */
	private static final String debugProp = "debug";
	private static final String randomizeProp = "randomize";
	private static final String minWeightProp = "min_weight";
	private static final String maxWeightProp = "max_weight";

  /**
   * A small value used to avoid division by zero
	 */
	protected double EPSILON = 0.0000001D;

  /**
   * Enables/disables debugging messages
   */
	private final static boolean DEBUG = false;
	protected static boolean debug = DEBUG; // so we can overload it with a property

  /** 
   * Default attribute to use for edge weighting
   */
	protected String eValueAttribute = "eValue";

	/**
	 * The task monitor that we report to
	 */
	protected TaskMonitor taskMonitor;

	/**
	 * This flag tells us if the user has
	 * has requested a cancel.
	 */
	protected boolean cancel = false;

	/**
	 * The Vertex (CyNode) and Edge (CyEdge) arrays.
	 */
	protected static ArrayList nodeList;
 	protected static ArrayList edgeList;

	/**
	 * Minimum and maximum weights.  This is used to
	 * provide a bounds on the weights.
	 */
	protected double minWeightCutoff = 0;
	protected double maxWeightCutoff = Double.MAX_VALUE;
	protected static double logWeightCeiling = 1074;  // Maximum log value (-Math.log(Double.MIN_VALU))

	/**
	 * The computed width and height of the resulting layout
	 */
	protected double width = 0;
	protected double height = 0;

	/**
	 * Whether or not to initialize by randomizing all points
	 */
	protected boolean randomize = true;

	/**
	 * Whether or not to layout all nodes or only selected nodes
	 */
	protected boolean selectedOnly = false;

	/**
	 * This hashmap provides a quick way to get an index into
	 * the Vertex array given a graph index.
	 */
	protected HashMap nodeToVertex;

	/**
	 * This is the constructor for the bioLayout algorithm.
	 * @param networkView the CyNetworkView of the network 
	 *                    are going to lay out.
	 */
	public bioLayoutAlgorithm (CyNetworkView networkView, String prefix) {
		super (networkView);
		Edge e = new Edge();
		e.reset(); // This allows us to reset the static variables
		Vertex v = new Vertex();
		v.reset(); // This allows us to reset the static variables
		initialize_properties(prefix);
	}

	/**
	 * Sets the attribute to use for the weights
	 *
	 * @param value the name of the attribute
	 */
	public void SetEvalueAttribute(String value) {
		this.eValueAttribute = value;
	}

	/**
	 * Sets the flag that determines if we're to layout all nodes
	 * or only selected nodes.
	 *
	 * @param value the name of the attribute
	 */
	public void SetSelectedOnly(boolean value) {
		this.selectedOnly = value;
	}

	public void SetSelectedOnly(String value) {
		Boolean val = new Boolean(value);
		selectedOnly = val.booleanValue();
	}

	/**
	 * Sets the debug flag
	 *
	 * @param flag boolean value that turns debugging on or off
	 */
	public void SetDebug(boolean flag) {
		debug = flag;
	}

	public void SetDebug(String value) {
		Boolean val = new Boolean(value);
		debug = val.booleanValue();
	}

	/**
	 * Sets the randomize flag
	 *
	 * @param flag boolean value that turns initial randomization on or off
	 */
	public void SetRandomize(boolean flag) {
		randomize = flag;
	}

	public void SetRandomize(String value) {
		Boolean val = new Boolean(value);
		randomize = val.booleanValue();
	}

	/**
	 * Sets the minimum weight cutoff
	 *
	 * @param value minimum weight cutoff
	 */
	public void SetMinWeight(double value) {
		this.minWeightCutoff = value;
	}

	public void SetMinWeight(String value) {
		Double val = new Double(value);
		this.minWeightCutoff = val.doubleValue();
	}

	/**
	 * Sets the maximum weight cutoff
	 *
	 * @param value maximum weight cutoff
	 */
	public void SetMaxWeight(double value) {
		this.maxWeightCutoff = value;
	}

	public void SetMaxWeight(String value) {
		Double val = new Double(value);
		this.maxWeightCutoff = val.doubleValue();
	}

	/**
	 * Set the task monitor we're going to use
	 */
	public void SetTaskMonitor(TaskMonitor t) {
		this.taskMonitor = t;
	}

	/**
	 * Reads all of our properties from the cytoscape properties map and sets
	 * the values as appropriates.
	 */
	private void initialize_properties(String propPrefix) {
		// Initialize our tunables from the properties
		Properties properties = CytoscapeInit.getProperties();
		String pValue = null;

		// debug
		if ( (pValue = properties.getProperty(propPrefix+debugProp) ) != null ) {
			SetDebug(pValue);
		}
		// randomize
		if ( (pValue = properties.getProperty(propPrefix+randomizeProp) ) != null ) {
			SetRandomize(pValue);
		}
		// max_weight
		if ( (pValue = properties.getProperty(propPrefix+maxWeightProp) ) != null ) {
			SetMaxWeight(pValue);
		}
		// min_weight
		if ( (pValue = properties.getProperty(propPrefix+minWeightProp) ) != null ) {
			SetMinWeight(pValue);
		}
	}

	/**
	 * Main entry point for AbstractLayout classes
	 */
	public Object construct() {
		taskMonitor.setStatus("Initializing");
		initialize();  // Calls initialize_local
		layout(); 		 // Abstract -- must be overloaded
		networkView.fitContent();
		networkView.updateView();
		return null;
	}

	/**
	 * Main function that must be implemented by the child class.
	 */
	public abstract void layout();

	/**
	 * Call all of the initializtion code.  Called from
	 * AbstractLayout.initialize().
	 */
	protected void initialize_local() {
		this.nodeToVertex = new HashMap();

		// Get our vertices
		vertex_initialize();

		// Get our edges
		edge_initialize();
	}

	/**
	 * Initialize the edge array.
	 */
	protected void edge_initialize() {
		this.edgeList = new ArrayList(network.getEdgeCount());
		Iterator iter = network.edgesIterator();
		while (iter.hasNext()) {
			CyEdge edge = (CyEdge)iter.next();
			CyNode source = (CyNode)edge.getSource();
			CyNode target = (CyNode)edge.getTarget();
			if (source == target) {continue;}
			Vertex v1 = (Vertex)nodeToVertex.get(source);
			Vertex v2 = (Vertex)nodeToVertex.get(target);
			// Do we care about this edge?
			if (v1.isLocked() && v2.isLocked())
				continue; // no, ignore it
			Edge newEdge = new Edge(edge, v1, v2);
			newEdge.setWeight(eValueAttribute);
			edgeList.add(newEdge);
		}
	}

	/**
	 * Initialize the vertex array.
	 */
	protected void vertex_initialize() {
		int nodeIndex = 0;
		this.nodeList = new ArrayList(network.getNodeCount());
		Set selectedNodes = null;
		Iterator iter = networkView.getNodeViewsIterator();
		if (selectedOnly) {
			selectedNodes = ((CyNetwork)network).getSelectedNodes();
		}
		while (iter.hasNext()) {
			NodeView nv = (NodeView)iter.next();
			CyNode node = (CyNode)nv.getNode();
			Vertex v;
			if (selectedNodes != null && !selectedNodes.contains(node)) {
				v = new Vertex(nv, nodeIndex, false);
				v.lock();
			} else {
				v = new Vertex(nv, nodeIndex, true);
			}
			nodeList.add(v);
			nodeToVertex.put(node,v);
			nodeIndex++;
		}
	}

	/**
	 * Randomize the graph locations.
	 */
	protected void randomize_locations() {
		// Get a seeded pseudo random-number generator
		Date today = new Date();
		Random random = new Random(today.getTime());
		Iterator iter = nodeList.iterator();
		while (iter.hasNext()) { 
			((Vertex)iter.next()).setRandomLocation(random); 
		}
		return;
	}

	/**
	 * Calculate and set the edge weights.  Note that this will delete
	 * edges from the calculation (not the graph) when certain conditions
	 * are met.
	 */
	protected void calculate_edge_weights() {
		// Normalize the weights to between 0 and 1
		ListIterator iter = edgeList.listIterator();
		while (iter.hasNext()) { 
			Edge edge = (Edge)iter.next();
			// Drop any edges that are outside of our bounds
			if (edge.getWeight() <= minWeightCutoff || edge.getWeight() > maxWeightCutoff) {
				iter.remove();
			} else {
				edge.normalizeWeight();
				// Drop any edges where the normalized weight is small
				if (edge.getWeight() < .001)
					iter.remove();
			}
		}
	}

	protected void print_disp() {
		Iterator iter = nodeList.iterator();
		while (iter.hasNext()) {
			Vertex v = (Vertex)iter.next();
			debugln("Node "+v.getIdentifier()
								+ " displacement="+v.printDisp());
		}
	}

	/**
	 * The Vertex class
	 */
	public static class Vertex {
		private double x, y;
		private double dispX, dispY;
		private CyNode node;
		private NodeView nodeView;
		private int index;
		private static double totalWidth = 0;
		private static double totalHeight = 0;
		private static double minX = 100000;
 		private static double minY = 100000;
 		private static double maxX = -100000;
 		private static double maxY = -100000;
		private static int lockedNodes = 0;
		private boolean isLocked = false;
		static final double EPSILON = 0.0000001D;
		private ArrayList neighbors = null;

		public Vertex() { }

		public Vertex(NodeView nodeView, int index, boolean accumulate) { 
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

    public void addNeighbor(Vertex v) {
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

		public double distance (Vertex u) {
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

	/**
	 * The Edge class
	 */
	public static class Edge {
		private Vertex v1;
		private Vertex v2;
		private double weight;
		private double logWeight;
		static int edgeCount = 0;
		static double maxWeight = 0.0;
		static double minWeight = 1.0;
		static double maxLogWeight = 0.0;
		static double minLogWeight = 0.0;
		private CyEdge edge;
		static final double EPSILON = 0.0000001D;
		static CyAttributes edgeAttributes = null;

		public Edge() { 
			edgeCount++; 
			if (edgeAttributes == null)
				this.edgeAttributes = Cytoscape.getEdgeAttributes();
		}

		public Edge(CyEdge edge) { 
			this.edge = edge;
			edgeCount++; 
			if (edgeAttributes == null)
				this.edgeAttributes = Cytoscape.getEdgeAttributes();
		}

		public Edge(CyEdge edge, Vertex v1, Vertex v2) {
			this.edge = edge;
			this.v1 = v1;
			this.v2 = v2;
			if (v1 != v2) {
				v1.addNeighbor(v2);
				v2.addNeighbor(v1);
			}
			edgeCount++;
			if (edgeAttributes == null)
				this.edgeAttributes = Cytoscape.getEdgeAttributes();
		}

		public void reset() {
			this.edgeCount = 0;
			this.maxWeight = 0;
			this.minWeight = 1;
		}

		/**
		 * Get and set the weights for this Edge.  Note that we calculate
		 * both the weight and the log of the weight in case we need to result to
		 * log values to get a reasonable spread.
		 *
		 * @param weightedAttribute the name of the attribute to use to get the weight
		 */
		public void setWeight(String weightedAttribute) { 
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			double eValue = 1;
			if ((weightedAttribute != null) && 
				  edgeAttributes.hasAttribute(edge.getIdentifier(),weightedAttribute)) {
				if (edgeAttributes.getType(weightedAttribute) == CyAttributes.TYPE_INTEGER) {
					Integer val = edgeAttributes.getIntegerAttribute(edge.getIdentifier(),
																															weightedAttribute);
 					eValue = (double)val.intValue();
				} else {
					Double val = edgeAttributes.getDoubleAttribute(edge.getIdentifier(),
																											weightedAttribute);
					eValue = val.doubleValue();
				}
			}
			debugln("eValue for "+edge.getIdentifier()+" is "+eValue);
			if (eValue == 0) {
				this.logWeight = Math.min(-Math.log10(Double.MIN_VALUE),logWeightCeiling); 
			} else {
				this.logWeight = Math.min(-Math.log10(eValue),logWeightCeiling); 
			}

			this.weight = eValue; 
			maxWeight = Math.max(maxWeight, weight);
			minWeight = Math.min(minWeight, weight);
			maxLogWeight = Math.max(maxLogWeight, logWeight);
			minLogWeight = Math.min(minLogWeight, logWeight);
			debugln("Weight for "+edge.getIdentifier()+" is "+weight);
		}

		/**
		 * Normalize the weights to fall between 0 and 1.  This method
		 * also determines whether to use the log of the weight or
		 * the weight itself.
		 */
		public void normalizeWeight() {
			// Normalize the weights to fall between 0 and 1

			if ((maxWeight-minWeight) == 0) {
				weight = .5; // all weights are the same -- go unweighted
			} else if (Math.abs(maxLogWeight - minLogWeight) > 3) {
				// Three orders of magnitude!  Use the log
				weight = (logWeight - minLogWeight) / (maxLogWeight-minLogWeight);
			} else {
				weight = (weight - minWeight) / (maxWeight-minWeight);
			}
			debugln("Final weight for "+edge.getIdentifier()+" is "+weight);
		}

		public double getWeight() { return this.weight; }

		public Vertex getSource() { return this.v1; }

		public Vertex getTarget() { return this.v2; }

		public CyEdge getEdge() { return this.edge; }
	}

	public static void debugln(String message) {
		if (debug) { System.err.println(message); }
	}

	public static void debug(String message) {
		if (debug) { System.err.print(message); }
	}

	public void SetCancel() {
		this.cancel = true;
	}

	/**
	 * This class is used to provide some simple profiling
	 */
	public static class Profile {
		long startTime;
		long totalTime;
		
		public Profile() {
			this.startTime = 0;
			this.totalTime = 0;
		}

		public void start() {
			this.startTime = System.currentTimeMillis();
		}

		public long checkpoint() {
			long runTime = System.currentTimeMillis()-this.startTime;
			this.totalTime += runTime;
			return runTime;
		}

		public void done(String message) {
			// Get our runtime
			checkpoint();

			System.out.println(message+this.totalTime+"ms");
			this.totalTime = 0;
		}

		public long getTotalTime() {
			return this.totalTime;
		}
	}

}
