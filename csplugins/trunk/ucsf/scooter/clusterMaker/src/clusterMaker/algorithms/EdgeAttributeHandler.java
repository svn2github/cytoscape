/* vim: set ts=2: */
/**
 * Copyright (c) 2009 The Regents of the University of California.
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
package clusterMaker.algorithms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;


import clusterMaker.algorithms.edgeConverters.EdgeWeightConverter;
import clusterMaker.algorithms.edgeConverters.DistanceConverter;
import clusterMaker.algorithms.edgeConverters.LogConverter;
import clusterMaker.algorithms.edgeConverters.NegLogConverter;
import clusterMaker.algorithms.edgeConverters.NoneConverter;
import clusterMaker.algorithms.edgeConverters.SCPSConverter;

import clusterMaker.algorithms.ThresholdHeuristic;

import clusterMaker.ui.HistogramDialog;
import clusterMaker.ui.HistoChangeListener;

/**
 * This is a helper class that can be used by cluster algorithms to
 * handle a lot of the details involved with choosing the attribute(s)
 * to use for clustering, appropriate transformations for the attributes,
 * and the tunables handling.
 */

public class EdgeAttributeHandler
       implements TunableListener, ActionListener, HistoChangeListener {

	public static final String NONEATTRIBUTE = "--None--";

	private ClusterProperties clusterProperties;
	private DistanceMatrix matrix = null;
	private boolean adjustLoops = true;
	private boolean undirectedEdges = true;
	private boolean selectedOnly = false;
	private EdgeWeightConverter converter = null;
	private boolean supportAdjustments = false;
	private Double edgeCutOff = null;
	private String[] attributeArray = new String[1];
	private List<EdgeWeightConverter>converters = null;

	private String dataAttribute = null;

	private HistogramDialog histo = null;

	public EdgeAttributeHandler(ClusterProperties clusterProperties, boolean supportAdjustments) {
		this.clusterProperties = clusterProperties;
		this.supportAdjustments = supportAdjustments;
		// Create all of our edge weight converters
		converters = new ArrayList<EdgeWeightConverter>();
		converters.add(new NoneConverter());
		converters.add(new DistanceConverter());
		converters.add(new LogConverter());
		converters.add(new NegLogConverter());
		converters.add(new SCPSConverter());
		converter = converters.get(0); // Initialize to the None converter

		initializeTunables(clusterProperties);
	}

	public void initializeTunables(ClusterProperties clusterProperties) {

		clusterProperties.add(new Tunable("attributeListGroup",
		                                  "Source for array data",
		                                  Tunable.GROUP, new Integer(5)));

		// The attribute to use to get the weights
		attributeArray = getAllAttributes();
		Tunable attrTunable = new Tunable("attribute",
		                                  "Array sources",
		                                  Tunable.LIST, 0,
		                                  (Object)attributeArray, new Integer(0), 0);

		clusterProperties.add(attrTunable);

		// Whether or not to create a new network from the results
		Tunable selTune = new Tunable("selectedOnly","Cluster only selected nodes",
		                              Tunable.BOOLEAN, new Boolean(false));
		clusterProperties.add(selTune);

		// TODO: Change this to a LIST with:
		// 		--None--
		// 		1/value
		// 		-LOG(value)
		// 		LOG(value)
		// 		SCPS
		EdgeWeightConverter[] edgeWeightConverters = converters.toArray(new EdgeWeightConverter[1]);
		Tunable edgeWeighter = new Tunable("edgeWeighter","Edge weight conversion",
		                                   Tunable.LIST, 0, 
		                                   (Object)edgeWeightConverters, new Integer(0), 0);
		clusterProperties.add(edgeWeighter);
		edgeWeighter.addTunableValueListener(this);

		// We want to "listen" for changes to these
		attrTunable.addTunableValueListener(this);
		selTune.addTunableValueListener(this);

		clusterProperties.add(new Tunable("edgeCutoffGroup",
		                                  "Edge weight cutoff",
		                                  Tunable.GROUP, new Integer(2)));

		Tunable edgeCutOffTunable = new Tunable("edgeCutOff",
		                                        "",
		                                        Tunable.DOUBLE, new Double(0),
		                                        new Double(0), new Double(1), Tunable.USESLIDER);
		clusterProperties.add(edgeCutOffTunable);
		edgeCutOffTunable.addTunableValueListener(this);

		clusterProperties.add(new Tunable("edgeHistogram",
		                                  "Set Edge Cutoff Using Histogram",
		                                  Tunable.BUTTON, "Edge Histogram", this, null, Tunable.IMMUTABLE));

		if (supportAdjustments) {
			clusterProperties.add(new Tunable("options_panel1",
			                                  "Array data adjustments",
			                                  Tunable.GROUP, new Integer(2)));

			//Whether or not to assume the edges are undirected
			clusterProperties.add(new Tunable("undirectedEdges","Assume edges are undirected",
			                                  Tunable.BOOLEAN, new Boolean(true)));

			// Whether or not to adjust loops before clustering
			clusterProperties.add(new Tunable("adjustLoops","Adjust loops before clustering",
			                                  Tunable.BOOLEAN, new Boolean(true)));
		}

		updateAttributeList();
	}

	public void updateSettings(boolean force) {
		Tunable t = clusterProperties.get("edgeWeighter");
		if ((t != null) && (t.valueChanged() || force)) {
			int index = ((Integer) t.getValue()).intValue();
			if (index < 0) index = 0;
			converter = converters.get(index);
		}

		t = clusterProperties.get("edgeCutOff");
		if ((t != null) && (t.valueChanged() || force)) {
			edgeCutOff = (Double) t.getValue();
		}

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("undirectedEdges");
		if ((t != null) && (t.valueChanged() || force))
			undirectedEdges = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("adjustLoops");
		if ((t != null) && (t.valueChanged() || force))
			adjustLoops = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("attribute");
		if ((t != null) && (t.valueChanged() || force)) {
			if (attributeArray.length == 1) {
				dataAttribute = attributeArray[0];
			} else {
				int index = ((Integer) t.getValue()).intValue();
				if (index < 0) index = 0;
				dataAttribute = attributeArray[index];
			}
			if (dataAttribute != null) {
				Tunable et = clusterProperties.get("edgeHistogram");
				et.clearFlag(Tunable.IMMUTABLE);
			}
			// tunableChanged(t);
		}
	}

	public void	updateAttributeList() {
		Tunable attributeTunable = clusterProperties.get("attribute");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);
		if (dataAttribute == null && attributeArray.length > 0)
			dataAttribute = attributeArray[0];
		tunableChanged(attributeTunable);
	}

	public void histoValueChanged(double cutoffValue) {
		// System.out.println("New cutoff value: "+cutoffValue);
		Tunable edgeCutoff = clusterProperties.get("edgeCutOff");
		edgeCutoff.removeTunableValueListener(this);
		edgeCutoff.setValue(cutoffValue);
		edgeCutoff.addTunableValueListener(this);
	}

	public void tunableChanged(Tunable tunable) {
		// If the tunable that changed is the edge cutoff tunable, 
		// just update the histogram, if it's up
		if (tunable.getName().equals("edgeCutOff")) {
			edgeCutOff = (Double) tunable.getValue();
			if (histo != null) {
				// No backs!
				histo.removeHistoChangeListener(this);
				histo.setLineValue(edgeCutOff);
				histo.addHistoChangeListener(this);
			}
			return;
		}

		updateSettings(false);
		Tunable edgeCutOffTunable = clusterProperties.get("edgeCutOff");
		if (edgeCutOffTunable == null || dataAttribute == null) 
			return;

		Tunable t = clusterProperties.get("edgeHistogram");
		t.clearFlag(Tunable.IMMUTABLE);

		this.matrix = new DistanceMatrix(dataAttribute, selectedOnly, converter);
		double dataArray[] = matrix.getEdgeValues();
		double range = matrix.getMaxWeight() - matrix.getMinWeight();
		edgeCutOffTunable.setUpperBound(matrix.getMaxWeight());
		edgeCutOffTunable.setLowerBound(matrix.getMinWeight());
		edgeCutOffTunable.setValue(matrix.getMinWeight());
		edgeCutOff = (Double) edgeCutOffTunable.getValue();

		if (histo != null) {
			histo.updateData(dataArray);
			histo.pack();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (this.matrix == null)
			this.matrix = new DistanceMatrix(dataAttribute, selectedOnly, converter);

		ThresholdHeuristic thueristic = new ThresholdHeuristic(matrix);

		double dataArray[] = matrix.getEdgeValues();

		// TODO: There really needs to be a better way to calculate the number of bins
		int nbins = 100;
		if (dataArray.length < 100)
			nbins = 10;
		// else if (dataArray.length > 10000)
		// 	nbins = 1000;
		String title = "Histogram for "+dataAttribute+" edge attribute";
		histo = new HistogramDialog(title, dataArray, nbins,thueristic);
		histo.pack();
		histo.setVisible(true);
		histo.addHistoChangeListener(this);
	}

	public DistanceMatrix getMatrix() {
		if (this.matrix == null) {
			if (dataAttribute == null) return null;
			this.matrix = new DistanceMatrix(dataAttribute, selectedOnly, converter);
		}

		matrix.setUndirectedEdges(undirectedEdges);

		if (edgeCutOff != null)
			matrix.setEdgeCutOff(edgeCutOff.doubleValue());

		if (adjustLoops)
			matrix.adjustLoops();

		return this.matrix;
	}

	private void getAttributesList(List<String>attributeList, CyAttributes attributes) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
			    attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(names[i]);
			}
		}
	}

	private String[] getAllAttributes() {
		attributeArray = new String[1];
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		attributeList.add(NONEATTRIBUTE);
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes());
		String[] attrArray = attributeList.toArray(attributeArray);
		if (attrArray.length > 1) 
			Arrays.sort(attrArray);
		return attrArray;
	}
}
