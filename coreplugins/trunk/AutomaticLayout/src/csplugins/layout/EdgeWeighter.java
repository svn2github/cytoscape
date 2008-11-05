/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
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

import java.lang.Math;
import java.util.List;

import csplugins.layout.LayoutEdge;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;

import cytoscape.layout.Tunable;
import cytoscape.layout.LayoutProperties;
import cytoscape.logger.CyLogger;

import cytoscape.data.CyAttributes;

enum WeightTypes {
	GUESS("Heuristic"),
	LOG("-Log(value)"),
	DISTANCE("1 - normalized value"),
	WEIGHT("normalized value");

	private String name;
	private WeightTypes(String str) { name=str; }
	public String toString() { return name; }
}

/**
 * The EdgeWeighter class.  This class is used as a container for information
 * about how to interpret weights in an weighted layout.
 */
public class EdgeWeighter {
	WeightTypes type = WeightTypes.GUESS;
	double minWeightCutoff = 0;
	double maxWeightCutoff = Double.MAX_VALUE;
	final static double EPSILON = .001;

	// Default normalization bounds
	final static double LOWER_BOUND = .1f;
	final static double UPPER_BOUND = .9f;

	double lowerBounds = LOWER_BOUND;
	double upperBounds = UPPER_BOUND;

	double maxWeight = -1000000;
	double minWeight = 1000000;
	double maxLogWeight = -1000000;
	double minLogWeight = 1000000;

	double logWeightCeiling = 1028;
	boolean logOverflow = false;

	private CyLogger logger = null;

	// These are just here for efficiency reasons
	double normalFactor = Double.MAX_VALUE;

	String weightAttribute = null;

	static WeightTypes[] weightChoices = {WeightTypes.GUESS,
	                                      WeightTypes.LOG,
	                                      WeightTypes.DISTANCE,
	                                      WeightTypes.WEIGHT};

	Tunable[] weightList = null;

	public EdgeWeighter() {
		logger = CyLogger.getLogger(EdgeWeighter.class);
	}
	
	public void getWeightTunables(LayoutProperties props, List initialAttributes) {
		props.add(new Tunable("edge_weight_group","Edge Weight Settings",
		                      Tunable.GROUP, new Integer(4)));
		props.add(new Tunable("edge_attribute",
			            	      "The edge attribute that contains the weights",
			            	      Tunable.EDGEATTRIBUTE, "weight",
                  	      (Object) initialAttributes, (Object) null,
                  	      Tunable.NUMERICATTRIBUTE));
		props.add(new Tunable("weight_type", "How to interpret weight values",
                  	      Tunable.LIST, new Integer(0),
                  	      (Object) weightChoices, (Object) null, 0));
		props.add(new Tunable("min_weight", "The minimum edge weight to consider",
                  	      Tunable.DOUBLE, new Double(0)));
		props.add(new Tunable("max_weight", "The maximum edge weight to consider",
                  	      Tunable.DOUBLE, new Double(Double.MAX_VALUE)));
	}

	public void updateSettings(LayoutProperties layoutProperties, boolean force) {
		boolean resetRequired = false;
  	Tunable t = layoutProperties.get("min_weight");
    if ((t != null) && (t.valueChanged() || force)) {
    	minWeightCutoff = ((Double) t.getValue()).doubleValue();
			resetRequired = true;
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

    t = layoutProperties.get("max_weight"); 
    if ((t != null) && (t.valueChanged() || force)) {
    	maxWeightCutoff = ((Double) t.getValue()).doubleValue();
			resetRequired = true;
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

    t = layoutProperties.get("edge_attribute");
    if ((t != null) && (t.valueChanged() || force)) {
    	weightAttribute = (t.getValue().toString());
			resetRequired = true;
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("weight_type");
		if ((t != null) && (t.valueChanged() || force)) {
			type = weightChoices[((Integer) t.getValue()).intValue()];
			resetRequired = true;
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		if (resetRequired) 
			reset();

	}

	public void reset() {
		maxWeight = -1000000;
		minWeight = 1000000;
		maxLogWeight = -1000000;
		minLogWeight = 1000000;
		normalFactor = Double.MAX_VALUE;
		logOverflow = false;
	}

	public void setWeightType(WeightTypes type) {
		this.type = type;
	}

	public void setNormalizedBounds(double lowerBound, double upperBound) {
		this.lowerBounds = lowerBound;
		this.upperBounds = upperBound;
	}

	public void setWeight(LayoutEdge layoutEdge) {
		CyEdge edge = layoutEdge.getEdge();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		double eValue = 0.5;

		// logger.debug("Setting weight for "+layoutEdge+" using "+weightAttribute);

		if ((weightAttribute != null)
		    && edgeAttributes.hasAttribute(edge.getIdentifier(), weightAttribute)) {
			if (edgeAttributes.getType(weightAttribute) == CyAttributes.TYPE_INTEGER) {
				Integer val = edgeAttributes.getIntegerAttribute(edge.getIdentifier(),
				                                                 weightAttribute);
				eValue = (double) val.intValue();
			} else {
				Double val = edgeAttributes.getDoubleAttribute(edge.getIdentifier(),
				                                               weightAttribute);
				eValue = val.doubleValue();
			}
		}
		layoutEdge.setWeight(eValue);
		minWeight = Math.min(minWeight,eValue);
		maxWeight = Math.max(maxWeight,eValue);
		if (type == WeightTypes.GUESS || type == WeightTypes.LOG) {
			double logWeight;
			if (eValue == 0) {
				logWeight = logWeightCeiling;
				logOverflow = true;
			} else {
				logWeight = Math.min(-Math.log10(eValue), logWeightCeiling);
			}
			minLogWeight = Math.min(minLogWeight,logWeight);
			maxLogWeight = Math.max(maxLogWeight,logWeight);
			layoutEdge.setLogWeight(logWeight);
		}
	}

	public boolean normalizeWeight(LayoutEdge edge) {
		// If all of our weights are the same we should
		// normalize everything to 0.5
		if (minWeight == maxWeight) {
			edge.setWeight(0.5);
			return true;
		}

		// We need to handle the special case of a weight of 0.0 when
		// we're doing logs.  When we set the value, we set it to
		// logWeightCeiling as a placeholder, but we really want to
		// just set it to a value somewhat larger than the maximum
		if (logOverflow) {
			maxLogWeight = maxLogWeight+5;
			logOverflow = false;
		}

		if ((edge.getWeight() <= minWeightCutoff) ||	
		    (edge.getWeight() > maxWeightCutoff))
			return false;

		double weight = 0;

		switch (this.type) {
		case GUESS:
			// logger.debug("Heuristic: ");
			if (Math.abs(maxLogWeight-minLogWeight) > 3) {
				weight = edge.getLogWeight();
				// logger.debug("Log weight = "+weight);
				if (weight == logWeightCeiling)
					weight = maxLogWeight;
				weight = logNormalize(weight);
				// logger.debug(" normalized weight = "+weight);
			} else {
				weight = normalize(edge.getWeight());
			}
			break;
	  case LOG:
			// logger.debug("Log: ");
			weight = edge.getLogWeight();
			// logger.debug("Log weight = "+weight);
			if (weight == logWeightCeiling)
				weight = maxLogWeight;
			weight = logNormalize(weight);
			// logger.debug(" normalized weight = "+weight);
			break;
	  case DISTANCE:
			// logger.debug("Distance");
			weight = edge.getWeight();
			weight = (lowerBounds + upperBounds) - normalize(weight);
			break;
	  case WEIGHT:
			// logger.debug("Weight");
			weight = normalize(edge.getWeight());
			break;
		}

		edge.setWeight(weight);

		// We're now normalized to the range 0-1, so we can safely
		// ignore really small weights since they should be very far away
		//if (weight < EPSILON)
		//	return false;

		return true;
	}

	private double logNormalize(double weight) {
		if (normalFactor == Double.MAX_VALUE) {
			normalFactor = (upperBounds-lowerBounds)/(maxLogWeight-minLogWeight);
		}
		return (weight-minLogWeight)*normalFactor+lowerBounds;
	}

	private double normalize(double weight) {
		if (normalFactor == Double.MAX_VALUE) {
			normalFactor = (upperBounds-lowerBounds)/(maxWeight-minWeight);
		}
		return (weight-minWeight)*normalFactor+lowerBounds;
	}

	public void setMaxWeightCutoff(double maxWeight) {
		maxWeightCutoff = maxWeight;
	}

	public void setMinWeightCutoff(double minWeight) {
		minWeightCutoff = minWeight;
	}
}
