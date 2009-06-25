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
package clusterMaker.algorithms.MCL;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.ui.ClusterViz;

// clusterMaker imports

public class MCLCluster extends AbstractClusterAlgorithm implements TunableListener {
	
	double inflation_parameter = 2.0;
	int rNumber = 8;
	double clusteringThresh = 1e-15;
	boolean takeNegLOG = false;
	boolean createNewNetwork = false;
	boolean createMetaNodes = false;
	boolean selectedOnly = false;
	boolean adjustLoops = true;
	boolean undirectedEdges = true;
	double maxResidual = 0.001;
	Double edgeCutOff = null;

	String[] attributeArray = new String[1];

	String dataAttribute = null;
	TaskMonitor monitor = null;
	CyLogger logger = null;
	RunMCL runMCL = null;

	public MCLCluster() {
		super();
		logger = CyLogger.getLogger(MCLCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "MCL";};
	public String getName() {return "MCL cluster";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);
		if (dataAttribute == null && attributeArray.length > 0)
			dataAttribute = attributeArray[0];
		tunableChanged(attributeTunable);

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return null;
	}

	protected void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */
		clusterProperties.add(new Tunable("tunables_panel",
		                                  "MCL Tuning",
		                                  Tunable.GROUP, new Integer(4)));

		// Inflation Parameter
		clusterProperties.add(new Tunable("inflation_parameter",
		                                  "Density Parameter",
		                                  Tunable.DOUBLE, new Double(2.5),
		                                  (Object)null, (Object)null, 0));
		// Clustering Threshold
		clusterProperties.add(new Tunable("clusteringThresh",
		                                  "Weak EdgeWeight Pruning Threshold",
		                                  Tunable.DOUBLE, new Double(1e-15),
		                                  (Object)null, (Object)null, 0));

		// Number of iterations
		clusterProperties.add(new Tunable("rNumber",
		                                  "Number of iterations",
		                                  Tunable.INTEGER, new Integer(8),
		                                  (Object)null, (Object)null, 0));

		// Number of iterations
		clusterProperties.add(new Tunable("maxResidual",
		                                  "The maximum residual value",
		                                  Tunable.DOUBLE, new Double(.001),
		                                  (Object)null, (Object)null, 0));

		clusterProperties.add(new Tunable("options_panel1",
		                                  "Data value options",
		                                  Tunable.GROUP, new Integer(3)));

		// Whether or not to create a new network from the results
		clusterProperties.add(new Tunable("selectedOnly","Cluster only selected nodes",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		//Whether or not to assume the edges are undirected
		clusterProperties.add(new Tunable("undirectedEdges","Assume edges are undirected",
		                                  Tunable.BOOLEAN, new Boolean(true)));

		// Whether or not to adjust loops before clustering
		clusterProperties.add(new Tunable("adjustLoops","Adjust loops before clustering",
		                                  Tunable.BOOLEAN, new Boolean(true)));

		clusterProperties.add(new Tunable("attributeListGroup",
		                                  "Source for array data",
		                                  Tunable.GROUP, new Integer(4)));

		// The attribute to use to get the weights
		attributeArray = getAllAttributes();
		Tunable attrTunable = new Tunable("attributeList",
		                                  "Array sources",
		                                  Tunable.LIST, 0,
		                                  (Object)attributeArray, (Object)null, 0);

		clusterProperties.add(attrTunable);

		//Whether or not take -LOG of Edge-Weights
		Tunable tLog = new Tunable("takeNegLOG","Take the -LOG of Edge Weights in Network",
		                           Tunable.BOOLEAN, new Boolean(false));
		clusterProperties.add(tLog);

		// We want to "listen" for changes to these
		attrTunable.addTunableValueListener(this);
		tLog.addTunableValueListener(this);

		clusterProperties.add(new Tunable("edgeCutoffGroup",
		                                  "Edge weight cutoff",
		                                  Tunable.GROUP, new Integer(1)));

		clusterProperties.add(new Tunable("edgeCutOff",
		                                  "",
		                                  Tunable.DOUBLE, new Double(0), 
		                                  new Double(0), new Double(1), Tunable.USESLIDER));

		clusterProperties.add(new Tunable("options_panel2",
		                                  "Results options",
		                                  Tunable.GROUP, new Integer(2)));

		// Whether or not to create a new network from the results
		clusterProperties.add(new Tunable("createNewNetwork","Create a new network with independent clusters",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		// Whether or not to create a new network from the results
		clusterProperties.add(new Tunable("createMetaNodes","Create meta nodes for clusters",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

		Tunable t = clusterProperties.get("inflation_parameter");
		if ((t != null) && (t.valueChanged() || force))
			inflation_parameter = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("clusteringThresh");
		if ((t != null) && (t.valueChanged() || force))
			clusteringThresh = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("maxResidual");
		if ((t != null) && (t.valueChanged() || force))
			maxResidual = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("rNumber");
		if ((t != null) && (t.valueChanged() || force))
			rNumber = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("takeNegLOG");
		if ((t != null) && (t.valueChanged() || force))
			takeNegLOG = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("createMetaNodes");
		if ((t != null) && (t.valueChanged() || force))
			createMetaNodes = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("createNewNetwork");
		if ((t != null) && (t.valueChanged() || force))
			createNewNetwork = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("undirectedEdges");
		if ((t != null) && (t.valueChanged() || force))
			undirectedEdges = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("adjustLoops");
		if ((t != null) && (t.valueChanged() || force))
			adjustLoops = ((Boolean) t.getValue()).booleanValue();
		
		t = clusterProperties.get("edgeCutOff");
		if ((t != null) && (t.valueChanged() || force)) {
			edgeCutOff = (Double) t.getValue();
		}
		
		t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			if (attributeArray.length == 1) {
				dataAttribute = attributeArray[0];
			} else {
				dataAttribute = attributeArray[((Integer) t.getValue()).intValue()];
			}
			tunableChanged(t);
		}
	}

	public void tunableChanged(Tunable tunable) {
		updateSettings(false);
		Tunable edgeCutOffTunable = clusterProperties.get("edgeCutOff");
		if (edgeCutOffTunable == null || dataAttribute == null) 
			return;

		try {
			double[] span = getSpan(dataAttribute);

			double range = span[1]-span[0];
			edgeCutOffTunable.setUpperBound(span[1]);
			edgeCutOffTunable.setLowerBound(span[0]);
			edgeCutOffTunable.setValue(span[0]+(range/1000));
		} catch (ArithmeticException e) {
			logger.error(e.getMessage());
			if (takeNegLOG) {
				Tunable t = clusterProperties.get("takeNegLOG");
				t.removeTunableValueListener(this);
				t.setValue(false);
				t.addTunableValueListener(this);
			}
		}
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
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes());
		String[] attrArray = attributeList.toArray(attributeArray);
		if (attrArray.length > 1) 
			Arrays.sort(attrArray);
		return attrArray;
	}

	private double[] getSpan(String attr) throws ArithmeticException {
		CyNetwork net = Cytoscape.getCurrentNetwork();
		CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
		byte type = edgeAttrs.getType(attr);
		double lower = Double.MAX_VALUE;
		double upper = Double.MIN_VALUE;
		for (Object e: net.edgesList()) {
			CyEdge edge = (CyEdge)e;
			if (edgeAttrs.hasAttribute(edge.getIdentifier(), attr)) {
				double val;
				if (type == CyAttributes.TYPE_FLOATING)
					val = (edgeAttrs.getDoubleAttribute(edge.getIdentifier(), attr)).doubleValue();
				else
					val = (edgeAttrs.getIntegerAttribute(edge.getIdentifier(), attr)).doubleValue();

				if (takeNegLOG) {
					if (val < 0)
						throw new ArithmeticException("Can't take log of negative values");
					double nl = -Math.log10(val);
					lower = Math.min(lower,nl);
					upper = Math.max(upper,nl);
				} else {
					lower = Math.min(lower,val);
					upper = Math.max(upper,val);
				}
			}
		}
		double[] d = new double[2];
		d[0] = lower;
		d[1] = upper;
		return d;
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		// Sanity check all of our settings
		if (debug)
			logger.debug("Performing MCL clustering with attributes: "+dataAttribute);

		String clusterAttrName = Cytoscape.getCurrentNetwork().getIdentifier()+"_cluster";
		//Cluster the nodes
		runMCL = new RunMCL(clusterAttrName, dataAttribute, inflation_parameter, 
		                           rNumber, clusteringThresh, maxResidual, logger);
		if (createNewNetwork)
			runMCL.createNewNetwork();

		if (selectedOnly)
			runMCL.selectedOnly();

		if (createMetaNodes)
			runMCL.createMetaNodes();

		if (!undirectedEdges)
			runMCL.setDirectedEdges();

		if (adjustLoops)
			runMCL.setAdjustLoops();

		if (edgeCutOff != null)
			runMCL.setEdgeCutOff(edgeCutOff);

		runMCL.run(monitor);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
		runMCL.halt();
	}
}
