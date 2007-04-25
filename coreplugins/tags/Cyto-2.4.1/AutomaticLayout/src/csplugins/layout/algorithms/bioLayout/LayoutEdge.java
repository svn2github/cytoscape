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
	static int edgeCount = 0;
	static double maxWeight = 0.0;
	static double minWeight = 1.0;
	static double maxLogWeight = 0.0;
	static double minLogWeight = 0.0;
	static final double EPSILON = 0.0000001D;
	static CyAttributes edgeAttributes = null;

	// instance variables
	private LayoutNode v1;
	private LayoutNode v2;
	private double weight;
	private double logWeight;
	private CyEdge edge;

	public LayoutEdge() { 
		if (edgeAttributes == null)
			this.edgeAttributes = Cytoscape.getEdgeAttributes();
	}

	public LayoutEdge(CyEdge edge) { 
		this.edge = edge;
		edgeCount++; 
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
		edgeCount++;
		if (edgeAttributes == null)
			this.edgeAttributes = Cytoscape.getEdgeAttributes();
	}

	public void reset() {
		this.edgeCount = 0;
		this.maxWeight = 0;
		this.minWeight = 1;
		this.maxLogWeight = 0.0;
		this.minLogWeight = 0.0;
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
			this.logWeight = Math.min(-Math.log10(Double.MIN_VALUE),logWeightCeiling); 
		} else {
			this.logWeight = Math.min(-Math.log10(eValue),logWeightCeiling); 
		}

		this.weight = eValue; 
		maxWeight = Math.max(maxWeight, weight);
		minWeight = Math.min(minWeight, weight);
		maxLogWeight = Math.max(maxLogWeight, logWeight);
		minLogWeight = Math.min(minLogWeight, logWeight);
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
	}

	public double getWeight() { return this.weight; }

	public LayoutNode getSource() { return this.v1; }

	public LayoutNode getTarget() { return this.v2; }

	public CyEdge getEdge() { return this.edge; }

}
