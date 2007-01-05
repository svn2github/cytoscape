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

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

import csplugins.layout.algorithms.bioLayout.LayoutNode;

/**
 * The LayoutEdge class
 */
class LayoutEdge {
	// static variables
	static double logWeightCeiling;
	static final double EPSILON = 0.0000001D;
	static CyAttributes edgeAttributes = null;

	// instance variables
	private LayoutNode v1;
	private LayoutNode v2;
	private double weight = 0.5;
	private double logWeight;
	private CyEdge edge;

	public LayoutEdge() { 
		if (edgeAttributes == null)
			this.edgeAttributes = Cytoscape.getEdgeAttributes();
	}

	public LayoutEdge(CyEdge edge) { 
		this.edge = edge;
		if (edgeAttributes == null)
			this.edgeAttributes = Cytoscape.getEdgeAttributes();
	}

	public LayoutEdge(CyEdge edge, LayoutNode v1, LayoutNode v2) {
		this.edge = edge;
		this.v1 = v1;
		this.v2 = v2;
		if (v1 != v2) {
			v1.addNeighbor(v2);
			v2.addNeighbor(v1);
		}
		if (edgeAttributes == null)
			this.edgeAttributes = Cytoscape.getEdgeAttributes();
	}

	public void addNodes(LayoutNode v1, LayoutNode v2) {
		this.v1 = v1;
		this.v2 = v2;
		if (v1 != v2) {
			v1.addNeighbor(v2);
			v2.addNeighbor(v1);
		}
	}

	public static void setLogWeightCeiling(double ceiling) {
		logWeightCeiling = ceiling;
	}

	/**
	 * Get and set the weights for this LayoutEdge.  Note that we calculate
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
		if (eValue == 0) {
			this.logWeight = 0;
		} else {
			this.logWeight = Math.min(-Math.log10(eValue),logWeightCeiling); 
		}

		// System.out.println("Setting weight to "+eValue+" logWeight to "+logWeight);
		this.weight = eValue; 
	}

	/**
	 * Normalize the weights to fall between 0 and 1.  This method
	 * also determines whether to use the log of the weight or
	 * the weight itself.
	 */
	public void normalizeWeight(double minWeight, double maxWeight, 
	                            boolean useLogWeights) {
		// Normalize the weights to fall between 0 and 1
		if (useLogWeights) {
			if (logWeight == 0) 
				weight = maxWeight+1;
			else
				weight = logWeight;
		}
		// System.out.println("Normalize weight ("+weight+") to between ("+minWeight+" and "+maxWeight+")");
		weight = (weight - minWeight) / (maxWeight-minWeight);
	}

	public double getWeight() { return this.weight; }
	public double getLogWeight() { return this.logWeight; }

	public LayoutNode getSource() { return this.v1; }

	public LayoutNode getTarget() { return this.v2; }

	public CyEdge getEdge() { return this.edge; }

	public String toString() {
		return "Edge "+edge.getIdentifier()+" connecting "+v1.getIdentifier()+" and "+v2.getIdentifier()+" with weight "+weight;
	}

}
