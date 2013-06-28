/* vim: set ts=2: */
/**
 * Copyright (c) 2013 The Regents of the University of California.
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
 * 
 * File last modified 06-25-13 by Aaron M. Newman, Ph.D.
 *
 */
package clusterMaker.algorithms.attributeClusterers.autosome;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.layout.CyLayouts;
import cytoscape.visual.NodeAppearance;

import ding.view.DGraphView;
import ding.view.DingCanvas;
import cytoscape.ding.DingNetworkView;

import java.awt.*;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.networkClusterers.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.edgeConverters.EdgeAttributeHandler;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;
import clusterMaker.ui.TreeView;
import clusterMaker.ui.KnnView;

import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import cytoscape.visual.mappings.LinearNumberInterpolator;
import cytoscape.visual.mappings.LinearNumberToColorInterpolator;
import cytoscape.visual.mappings.LinearNumberToNumberInterpolator;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.BoundaryRangeValues;

import clusterMaker.algorithms.attributeClusterers.autosome.launch.Settings;

import java.util.*;

import cytoscape.layout.CyLayoutAlgorithm;


public class AutoSOMECluster extends AbstractNetworkClusterer implements TunableListener, ActionListener {

	private String AutoSOME_Mode = "Normal";
	private String[] mode = new String[]{"Normal","Precision","Speed"};
	private Settings settings = new Settings();
	private String[] attributeArray = new String[1];
	private String dataAttribute;
	private boolean heatmap=false;
	private int cluster_output=0;
	private boolean finishedClustering = false;
	private boolean ignoreMissing = true;
	private boolean selectedNodes = false;
	private List<CyEdge> edges;
	private int MAXEDGES = 2000;

	private EdgeAttributeHandler edgeAttributeHandler = null;

	private TaskMonitor monitor = null;
	private CyLogger logger = null;
	private RunAutoSOME runAutoSOME = null;

	private Tunable logscaling, unitVar, medCent, sumSqr, ensembleRuns, fcnInput, fcnDM, fillMV, mxedges, data_output;
        private String last_output = "false";
        
	private List<NodeCluster> nodeCluster;

	private List<String>attrList;
	private List<String>attrOrderList;
	private List<String>nodeOrderList;
	
	public AutoSOMECluster(boolean heatmap) {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_AutoSOME_cluster";
		logger = CyLogger.getLogger(AutoSOMECluster.class);
		this.heatmap = heatmap;
		if (heatmap) cluster_output = 1;

		// Initialize our settings
		settings.ensemble_runs = 50;
		settings.mst_pval = 0.05;
		settings.threads = Runtime.getRuntime().availableProcessors();
		settings.logNorm = false;
		settings.unitVar = false;
		settings.distMatrix = false;

		initializeProperties();
	}

	public String getShortName() {
		return (heatmap) ? "autosome_heatmap" : "autosome_network";
	};

	public String getName() {return "AutoSOME "+((settings.distMatrix) ? "Fuzzy " : "")+"Clustering";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		//edgeAttributeHandler.updateAttributeList();
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return (heatmap) ? new KnnView() : new NewNetworkView(true);
	}

	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */
		//data input tunables
		clusterProperties.add(new Tunable("tunables_panel3",
						  "Data Input",
						  Tunable.GROUP, new Integer(3)));
		attributeArray = getAllAttributes();
		clusterProperties.add(new Tunable("attributeList",
						  "Array Sources (Node Attributes)",
						  Tunable.LIST, "",
						  (Object)attributeArray, (Object)null, Tunable.MULTISELECT));

		clusterProperties.add(new Tunable("selectedOnly", "Only use selected nodes (in network) for clustering",
						  Tunable.BOOLEAN, new Boolean(selectedNodes)));

		Tunable ign = new Tunable("ignoreMissing", "Ignore nodes/edges with no data",
														  Tunable.BOOLEAN, ignoreMissing);
		ign.addTunableValueListener(this);
		clusterProperties.add(ign);

		clusterProperties.add(new Tunable("tunables_panel",
						  "AutoSOME Basic Tuning",
						  Tunable.GROUP, new Integer(4)));

		Tunable tun = new Tunable("mode","Running Mode",
						   Tunable.LIST, 0,
						   (Object)mode, new Integer(0), 0);
		tun.addTunableValueListener(this);
		clusterProperties.add(tun);

		// Clustering Threshold
		ensembleRuns = new Tunable("ensembleRuns",
						  "Number of Ensemble Runs",
						  Tunable.INTEGER, new Integer(settings.ensemble_runs),
						  new Integer(1), (Object)null, 0);
		clusterProperties.add(ensembleRuns);

		// Number of iterations
		clusterProperties.add(new Tunable("pvalueThresh",
						  "P-Value Threshold",
						  Tunable.DOUBLE, new Double(settings.mst_pval),
						  new Double(0), new Double(1), 0));

		// Number of iterations
		clusterProperties.add(new Tunable("numThreads",
						  "Number of Threads (No. CPUs)",
						  Tunable.INTEGER, new Integer(settings.threads),
						  new Integer(1), (Object)null, 0));
	      

		//normalization tunables
		clusterProperties.add(new Tunable("tunables_panel2",
						  "Data Normalization",
						  Tunable.GROUP, new Integer(6), new Boolean(true), null, Tunable.COLLAPSABLE));

		Tunable norm_mode = new Tunable("norm_mode",
						  "Normalization mode",
						  Tunable.LIST, 0,
						  new Object[]{"Custom","No normalization", "Expression data 1", "Expression data 2" }, (Object)null, 0);

		norm_mode.addTunableValueListener(this);
		clusterProperties.add(norm_mode);

		logscaling = new Tunable("logScaling",
						  "Log2 Scaling",
						  Tunable.BOOLEAN, new Boolean(settings.logNorm));
		clusterProperties.add(logscaling);

		unitVar = new Tunable("unitVariance",
						  "Unit Variance",
						  Tunable.BOOLEAN, new Boolean(settings.unitVar));
		clusterProperties.add(unitVar);

		medCent = new Tunable("medCenter",
						  "Median Centering",
						  Tunable.LIST, 0,
						  new Object[]{"None", "Genes", "Arrays", "Both"}, (Object)null, 0);
		clusterProperties.add(medCent);

		sumSqr = new Tunable("sumSquares",
						  "Sum of Squares=1",
						  Tunable.LIST, 0,
						  new Object[]{"None", "Genes", "Arrays", "Both"}, (Object)null, 0);
		clusterProperties.add(sumSqr);

		fillMV = new Tunable("fillMV", "Missing value handling",
						  Tunable.LIST, 0,
						  new Object[]{"Row Mean", "Row Median", "Column Mean", "Column Median"}, (Object)null, 0);
		//if (ignoreMissing) fillMV.setImmutable(true);
		clusterProperties.add(fillMV);


		//fuzzy clustering tunables
		clusterProperties.add(new Tunable("fuzzyclustering", "Fuzzy Cluster Network Settings",
						  Tunable.GROUP, new Integer(4),
						  new Boolean(true), null, Tunable.COLLAPSABLE));

		Tunable fcn = new Tunable("enableFCN", "Perform Fuzzy Clustering",
						  Tunable.BOOLEAN, new Boolean(settings.distMatrix));
		fcn.addTunableValueListener(this);
		clusterProperties.add(fcn);

	       fcnInput = new Tunable("FCNInput",
						  "Source Data",
						  Tunable.LIST, new Integer(1),
						  new Object[]{"Nodes (Genes)", "Attributes (Arrays)"}, (Object)null, 0);
	       fcnInput.setImmutable(true);
	       clusterProperties.add(fcnInput);

	       fcnDM = new Tunable("FCNmetric",
						  "Distance Metric",
						  Tunable.LIST, 0,
						  new Object[]{"Uncentered Correlation", "Pearson's Correlation", "Euclidean"}, (Object)null, 0);
	       fcnDM.setImmutable(true);
	       clusterProperties.add(fcnDM);

		mxedges = new Tunable("maxEdges",
						  "Maximum number of edges to display in fuzzy network",
						  Tunable.INTEGER, new Integer(2000),
						  new Integer(1), (Object)null, 0);

	       mxedges.setImmutable(true);
	       mxedges.addTunableValueListener(this);
	       clusterProperties.add(mxedges);

		//advanced settings
		// clusterProperties.add(new Tunable("advancedAut", "Additional Settings",
		// 				  Tunable.GROUP, new Integer(1),
		// 				  new Boolean(false), null, Tunable.COLLAPSABLE));

		
	  //output tunables
	  clusterProperties.add(new Tunable("tunables_panel4",
	                                    "Data Output",
	                                    Tunable.GROUP, new Integer(2)));
                                      
		data_output = new Tunable("cluster_output",
						  "Choose Visualization",
						  Tunable.LIST, 0,
						  new Object[]{"Network", "Heatmap"}, (Object)null, 0);
		data_output.addTunableValueListener(this);
		clusterProperties.add(data_output);

		

		Tunable nwbutton = new Tunable("showDisplay",
						  "",
						  Tunable.BUTTON, "Display",
						  this, null, Tunable.IMMUTABLE);
		nwbutton.setImmutable(true);
		clusterProperties.add(nwbutton);

 

		// Use the standard edge attribute handling stuff....
		//edgeAttributeHandler = new EdgeAttributeHandler(clusterProperties, false);

		//super.advancedProperties();

		clusterProperties.initializeProperties();
		updateSettings(true);
	}


	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

		Tunable t = clusterProperties.get("mode");
		if ((t != null) && (t.valueChanged() || force)){
		    int index = ((Integer) t.getValue()).intValue();
		    if (index < 0) index = 0;
			AutoSOME_Mode = mode[index];
			if(AutoSOME_Mode.equals("Normal")){
			    settings.som_iters=500;
			    settings.de_resolution=32;
			}else if(AutoSOME_Mode.equals("Speed")){
			    settings.som_iters=250;
			    settings.de_resolution=16;
			}else{
			    settings.som_iters=1000;
			    settings.de_resolution=64;
			}
		}

		t = clusterProperties.get("ensembleRuns");
		if ((t != null) && (t.valueChanged() || force))
			settings.ensemble_runs = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("pvalueThresh");
		if ((t != null) && (t.valueChanged() || force))
			settings.mst_pval = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("numThreads");
		if ((t != null) && (t.valueChanged() || force))
			settings.threads = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("logScaling");
		if ((t != null) && (t.valueChanged() || force))
			settings.logNorm = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("unitVariance");
		if ((t != null) && (t.valueChanged() || force))
			settings.unitVar = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("medCenter");
		if ((t != null) && (t.valueChanged() || force)){
		    String val = (t.getValue()).toString();
		    if(val.equals("1")){
			settings.medCenter=true;
			 settings.medCenterCol=false;
		    }
		    if(val.equals("2")){
			settings.medCenterCol=true;
			settings.medCenter=false;
		    }
		    if(val.equals("3")){
			settings.medCenter=true;
			settings.medCenterCol=true;
		    }
		    if(val.equals("0")){
			settings.medCenter=false;
			settings.medCenterCol=false;
		    }
		}

		t = clusterProperties.get("sumSquares");
		if ((t != null) && (t.valueChanged() || force)){
		    String val = (t.getValue()).toString();
		    if(val.equals("1")){
			settings.sumSqrRows=true;
			settings.sumSqrCol=false;
		    }
		    if(val.equals("2")){
			settings.sumSqrCol=true;
			settings.sumSqrRows=false;
		    }
		    if(val.equals("3")){
			settings.sumSqrRows=true;
			settings.sumSqrCol=true;
		    }
		    if(val.equals("0")){
			settings.sumSqrRows=false;
			settings.sumSqrCol=false;
		    }
		}

		 t = clusterProperties.get("ignoreMissing");
		 if ((t != null) && (t.valueChanged() || force)){
		    ignoreMissing = ((Boolean) t.getValue()).booleanValue();
				settings.fillMissing = !ignoreMissing;
		 }

		  t = clusterProperties.get("selectedOnly");
		  if ((t != null) && (t.valueChanged() || force)){
		    selectedNodes = ((Boolean) t.getValue()).booleanValue();
		  }
		
		 t = clusterProperties.get("enableFCN");
		 if ((t != null) && (t.valueChanged() || force)){
		    settings.distMatrix = ((Boolean) t.getValue()).booleanValue();
		}

		t = clusterProperties.get("FCNInput");
		if ((t != null) && (t.valueChanged() || force)){
		    String val = (t.getValue()).toString();
		    if(val.equals("0")){
			settings.FCNrows=true;
		    } else  settings.FCNrows=false;
		}

		t = clusterProperties.get("FCNmetric");
		if ((t != null) && (t.valueChanged() || force)){
		    String val = (t.getValue()).toString();
		    if(val.equals("0")){
					settings.dmDist=3;
		    }
		    if(val.equals("1")){
					settings.dmDist=2;
		    }
		    if(val.equals("2")){
					settings.dmDist=1;
		    }
		}

		t = clusterProperties.get("fillMV");
		if ((t != null) && (t.valueChanged() || force)){
		    String val = (t.getValue()).toString();
		    if(val.equals("0")){
					settings.mvMedian=false;
					settings.mvCol=false;
		    } else if(val.equals("1")){
					settings.mvMedian=true;
					settings.mvCol=false;
		    } else if(val.equals("2")){
					settings.mvMedian=false;
					settings.mvCol=true;
		    } else if (val.equals("3")) {
					settings.mvMedian=true;
					settings.mvCol=true;
				}
		}


		t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttribute = (String) t.getValue();
		}

		t = clusterProperties.get("cluster_output");
		if ((t != null) && (t.valueChanged() || force))
			cluster_output = ((Integer) t.getValue()).intValue();

	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();
               
                //got back to parent to cluster again
                if(networkID.contains("--AutoSOME")){
                    String[] tokens = networkID.split("--AutoSOME");
                    networkID = tokens[0];
                    Cytoscape.setCurrentNetwork(networkID);
                    network = Cytoscape.getCurrentNetwork();
                } 

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		//Cluster the nodes
		runAutoSOME = new RunAutoSOME(dataAttribute, attributeArray,settings,logger);

		runAutoSOME.setIgnoreMissing(ignoreMissing);
		runAutoSOME.setSelectedOnly(selectedNodes);

		runAutoSOME.setDebug(debug);

		logger.info("Running AutoSOME"+((settings.distMatrix) ? " Fuzzy Clustering" : ""));

		nodeCluster = runAutoSOME.run(monitor);

		if(nodeCluster==null) {
			monitor.setStatus("Clustering failed!");
			return;
		}

		if(nodeCluster.size()>0) finishedClustering=true;

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		nodeAttributes.deleteAttribute(clusterAttributeName);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");
		
		if(settings.distMatrix) edges=runAutoSOME.getEdges(MAXEDGES);

		attrList = runAutoSOME.attrList;
		attrOrderList = runAutoSOME.attrOrderList;
		nodeOrderList = runAutoSOME.nodeOrderList;

		String netID = Cytoscape.getCurrentNetwork().getIdentifier();
		netAttributes.setListAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE, attrList);
		netAttributes.setListAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE, attrOrderList);
		netAttributes.setListAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE, nodeOrderList);
		netAttributes.setAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, getShortName());

		List<List<CyNode>> nodeClusters;

		if(!settings.distMatrix) {
			nodeClusters =
				createGroups(netAttributes, networkID, nodeAttributes, nodeCluster);		   
			ClusterResults results = new ClusterResults(network, nodeClusters);
			monitor.setStatus("Done.  AutoSOME results:\n"+results);
		} else {
			nodeClusters = new ArrayList<List<CyNode>>();
			/*
			for (NodeCluster cluster: clusters) {
				List<CyNode>nodeList = new ArrayList();

				for (CyNode node: cluster) {
					nodeList.add(node);
				}
				nodeClusters.add(nodeList);
			}
	   */
		}

		monitor.setStatus("Done.  AutoSOME results:\n"+nodeCluster.size()+" clusters found.");

		Tunable t = clusterProperties.get("showDisplay");
		t.setImmutable(false);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
		runAutoSOME.halt();
	}

	private void getAttributesList(List<String>attributeList, CyAttributes attributes,
				      String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
			    attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
                                if(names[i].contains("--AutoSOME")) continue;
				attributeList.add(prefix+names[i]);
			}
		}
	}

	private String[] getAllAttributes() {
		attributeArray = new String[1];
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		//getAttributesList(attributeList, Cytoscape.getEdgeAttributes(),"edge.");
		String[] attrArray = attributeList.toArray(attributeArray);
		if (attrArray.length > 1)
			Arrays.sort(attrArray);
		return attrArray;
	}

	public void tunableChanged(Tunable tunable) {
		updateSettings(false);

		Tunable t = clusterProperties.get("norm_mode");
                if(t.valueChanged()){
		switch(Integer.valueOf((t.getValue().toString()))){
		    case 1:
			logscaling.setValue(Boolean.FALSE);
			unitVar.setValue(Boolean.FALSE);
			medCent.setValue(0);
			sumSqr.setValue(0);
			break;
		    case 2:
			logscaling.setValue(Boolean.TRUE);
			unitVar.setValue(Boolean.TRUE);
			medCent.setValue(1);
			sumSqr.setValue(0);
			break;
		    case 3:
			logscaling.setValue(Boolean.TRUE);
			unitVar.setValue(Boolean.TRUE);
			medCent.setValue(1);
			sumSqr.setValue(3);
			break;
		}}
/*
		t = clusterProperties.get("mode");
                if(t.valueChanged()){
		int c = Integer.valueOf((t.getValue().toString()));
		switch (c){
		    case 0:
			ensembleRuns.setValue(50);
		    break;
		    case 1:
			ensembleRuns.setValue(100);
		    break;
		    case 2:
			ensembleRuns.setValue(20);
		    break;
		}}*/

		t = clusterProperties.get("enableFCN");
		String s = t.getValue().toString();
		if(s.equals("true")){
		    fcnInput.setImmutable(false);
		    fcnDM.setImmutable(false);
		    mxedges.setImmutable(false);
                    if(last_output.equals("false")) data_output.setValue("Network");
		}else{
		    fcnInput.setImmutable(true);
		    fcnDM.setImmutable(true);
		    mxedges.setImmutable(true);
                    if(last_output.equals("true")) data_output.setValue("Heatmap");
		}
                last_output = s;

		t = clusterProperties.get("ignoreMissing");
		ignoreMissing = ((Boolean) t.getValue()).booleanValue();
		/*if (ignoreMissing) 
			fillMV.setImmutable(true);
		else
			fillMV.setImmutable(false);
*/
	}

	public void actionPerformed(ActionEvent e){
		if(!finishedClustering) {
			return;
		}

		Tunable t = clusterProperties.get("maxEdges");
		MAXEDGES = Integer.valueOf((t.getValue().toString()));
		Tunable t2 = clusterProperties.get("cluster_output");
		cluster_output = ((Integer) t2.getValue()).intValue();

		if(cluster_output == 1){
			CyAttributes netAttr = Cytoscape.getNetworkAttributes();
			String netID = Cytoscape.getCurrentNetwork().getIdentifier();
			netAttr.setAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, "autosome_heatmap");
			KnnView tv = new KnnView();
			try {
				tv.startViz();
				tv.setVisible(true);
			} catch (CyCommandException cce) {
			// Shouldn't happen
			}
		}else{
			if(!settings.distMatrix){
				try {
					NewNetworkView nnv = new NewNetworkView(true);
					nnv.startViz();
					nnv.setVisible(true);
				} catch (CyCommandException cce) {
					// Shouldn't happen
				}
			}else{
				edges=runAutoSOME.getEdges(MAXEDGES);
				createClusteredNetwork();
			}
		}
	}

	private void createClusteredNetwork() {
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();

		//Cytoscape.
		// Create the new network
                
                //increment id so results can be saved.
                String title = currentNetwork.getTitle();
                if(title.contains("--AutoSOME")){
                    String[] tokens = title.split("--AutoSOME");
                    title = tokens[0];
                }
                Set<CyNetwork> nets = Cytoscape.getNetworkSet();
                //System.out.println(title);
                int maxitor = 0;
                String first = "";
                for(CyNetwork cnet : nets){
                    //System.out.println("###\t"+cnet.getTitle());
                    String tit = cnet.getTitle();
                    if(tit.contains("--AutoSOME")){
                    String[] tokens = tit.split("--AutoSOME");
                    first = tokens[0];
                    int itor = Integer.valueOf(tokens[1]);
                    if(itor>maxitor) maxitor = itor;
                    }
                }
                if(maxitor>0) {maxitor++; title = title.concat("--AutoSOME"+maxitor);}
                else title = title.concat("--AutoSOME1");
                
		CyNetwork net = Cytoscape.createNetwork(title,currentNetwork,false);
                net.setIdentifier(title);
                //System.out.println(net.getIdentifier()+"\t"+currentNetwork.getIdentifier());
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		Map<Integer, Integer> ids = new HashMap<Integer, Integer>();

		// Create the cluster Map
		HashMap<Integer, List<CyNode>> clusterMap = new HashMap<Integer, List<CyNode>>();

		for (NodeCluster node: nodeCluster) {
			for(CyNode cn : node){
				// For each node -- see if it's in a cluster.  If so, add it to our map
				//if (nodeAttributes.hasAttribute(node.getIdentifier(), clusterAttribute)) {
				Integer cluster = node.getClusterNumber();//nodeAttributes.getIntegerAttribute(cn.getIdentifier(), node.getClusterNumber());
				if (!clusterMap.containsKey(cluster)) {
					clusterMap.put(cluster, new ArrayList<CyNode>());
				}
				clusterMap.get(cluster).add(cn);
				if(!ids.containsKey(cluster)) ids.put(cluster,cluster);
				net.addNode(cn);
				nodeAttributes.setAttribute(cn.getIdentifier(), Cytoscape.getCurrentNetwork().getIdentifier()+"_FCN_Cluster", new Integer(cluster));
				String temp = cn.getIdentifier();
                                //System.out.println(temp+"\t"+cluster);
                                String nodeLabel = temp;
                                if(temp.contains("_")){
                                    String[] tokens = temp.split("_");
                                    StringBuilder sb = new StringBuilder();
                                    for(int j = 0; j < tokens.length-1; j++) sb.append(tokens[j]+"_");
                                    temp = sb.substring(0,sb.length()-1);
                                }
				nodeAttributes.setAttribute(cn.getIdentifier(), "SAMPLE_NAME", nodeLabel);
			//}
			}
		}

		// Get the list of edges
		for (CyEdge edge: edges) {
			//System.out.println(edge.getIdentifier());
			double weight = Double.valueOf(edge.getIdentifier());
			//edge.setIdentifier(edge.getSource().getIdentifier()+" "+edge.getTarget().getIdentifier());
			net.addEdge(edge);
			// Add the cluster attribute to the edge so we can style it later
			edgeAttributes.setAttribute(edge.getIdentifier(), "CONFIDENCE", weight);
		}

		// Create the network view
		CyNetworkView view = Cytoscape.createNetworkView(net);

		// If available, do a force-directed layout
		CyLayoutAlgorithm alg = CyLayouts.getLayout("force-directed");

		if (alg != null){
			//alg.setLayoutAttribute("CONFIDENCE");
			List<Tunable> h = alg.getSettings().getTunables();
			for(Tunable t: h) {
                            //System.out.println(t.getName());
				if(t.getName().equals("edge_attribute")){
					t.setValue("CONFIDENCE");
					t.valueChanged();
				}
				if(t.getName().equals("max_weight")){
					t.setValue(new Double(0.5));
					t.valueChanged();
				}
				if(t.getName().equals("min_weight")){
					t.setValue(new Double(-0.5));
					t.valueChanged();
				}
                                if(t.getName().equals("defaultSpringCoefficient")){
					t.setValue(new Double(5E-6));
					t.valueChanged();
				}
                                if(t.getName().equals("deterministic")){
					t.setValue(true);
					t.valueChanged();
				}
			// System.out.println(t.getName()+" "+t.getValue().toString());
			}

			alg.getSettings().updateTunablePanel();

			alg.getSettings().setProperty("edge_attribute", "CONFIDENCE");
			alg.getSettings().setProperty("min_weight", "-0.5");
			alg.getSettings().setProperty("max_weight", "0.5");

			alg.updateSettings();

			view.applyLayout(alg);
		}
			

		// Get the current visual mapper
		//VisualStyle vm = Cytoscape.getVisualMappingManager().getVisualStyle();

		VisualStyle vm = createNewStyle("test", "_FCN", ids);

		view.applyVizmapper(vm);


		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		String netID = Cytoscape.getCurrentNetwork().getIdentifier();
		netAttr.setListAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE, attrList);
		netAttr.setListAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE, attrOrderList);
		netAttr.setListAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE, nodeOrderList);
		//Cytoscape.setCurrentNetwork(net.getIdentifier());
		Cytoscape.setCurrentNetwork(net.getTitle());
		return;
	}

	private VisualStyle createNewStyle(String attribute, String suffix, Map<Integer,Integer> ids) {
		boolean newStyle = false;


		// Get our current vizmap
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calculatorCatalog = manager.getCalculatorCatalog();

		// Get the current style
		VisualStyle style = Cytoscape.getCurrentNetworkView().getVisualStyle();


		NodeAppearanceCalculator nodeAppCalc = style.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator edgeAppCalc = style.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator globalAppCalc = style.getGlobalAppearanceCalculator();

		// Create a new vizmap
		Set<String> styles = calculatorCatalog.getVisualStyleNames();
		if (styles.contains(style.getName()+suffix))
			style = calculatorCatalog.getVisualStyle(style.getName()+suffix);
		else {
			style = new VisualStyle(style, style.getName()+suffix);
			newStyle = true;
		}

		globalAppCalc.setDefaultBackgroundColor(Color.WHITE);



		PassThroughMapping pm = new PassThroughMapping(new String(), "SAMPLE_NAME");
		Calculator nlc = new BasicCalculator("Node Label",
						       pm, VisualPropertyType.NODE_LABEL);
		nodeAppCalc.setCalculator(nlc);


		// Discrete Mapping - set node color
		DiscreteMapping disMapping = new DiscreteMapping(java.awt.Color.BLACK,
									 ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(Cytoscape.getCurrentNetwork().getIdentifier()+"_FCN_Cluster");


		float increment = 1f / ((Number) ids.size()).floatValue();
		float hue = 0;
		Map<Integer,Color> valueMap = new HashMap<Integer,Color>();
		for (Integer key : ids.keySet()) {
			hue += increment;
			valueMap.put(key, new Color(Color.HSBtoRGB(hue, 1f, 1f)));
		}

		disMapping.putAll(valueMap);
		Calculator nlc2 = new BasicCalculator("Node Color",
						       disMapping, VisualPropertyType.NODE_FILL_COLOR);
		nodeAppCalc.setCalculator(nlc2);


		// Discrete Mapping - set node border color
		nodeAppCalc.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, Color.BLACK);
                //set node line width
                nodeAppCalc.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 2.5);

                
                
		//set edge width
		ContinuousMapping contMapping = new ContinuousMapping(new Double(0),ObjectMapping.EDGE_MAPPING);

		contMapping.setControllingAttributeName("CONFIDENCE");
		Interpolator numTonum = new LinearNumberToNumberInterpolator();
		contMapping.setInterpolator(numTonum);
		BoundaryRangeValues bv0 = new BoundaryRangeValues(.5, .5, .5);
		BoundaryRangeValues bv1 = new BoundaryRangeValues(20, 20, 20);
		// BoundaryRangeValues bv2 = new BoundaryRangeValues(.5, .5, .5);
		contMapping.addPoint(-.5, bv0);
		contMapping.addPoint(.5, bv1);
		Calculator widthCalculator = new BasicCalculator("Edge Width Calculator", contMapping, VisualPropertyType.EDGE_LINE_WIDTH);
		edgeAppCalc.setCalculator(widthCalculator);

		//set edge opacity
		contMapping = new ContinuousMapping(new Double(0),ObjectMapping.EDGE_MAPPING);

		contMapping.setControllingAttributeName("CONFIDENCE");
		numTonum = new LinearNumberToNumberInterpolator();
		contMapping.setInterpolator(numTonum);
		bv0 = new BoundaryRangeValues(.5, .5, .5);
		bv1 = new BoundaryRangeValues(200, 200, 200);
		// BoundaryRangeValues bv2 = new BoundaryRangeValues(.5, .5, .5);
		contMapping.addPoint(-.5, bv0);
		contMapping.addPoint(.5, bv1);
		Calculator opacityCalculator = new BasicCalculator("Edge Opacity Calculator", contMapping, VisualPropertyType.EDGE_OPACITY);
		edgeAppCalc.setCalculator(opacityCalculator);

                
                //set node color gradient
                VisualPropertyType type = VisualPropertyType.EDGE_COLOR;
                Object defaultObj = type.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
		contMapping = new ContinuousMapping(defaultObj,ObjectMapping.EDGE_MAPPING);

		contMapping.setControllingAttributeName("CONFIDENCE", Cytoscape.getCurrentNetwork(), false);
		numTonum = new LinearNumberToColorInterpolator();
		contMapping.setInterpolator(numTonum);
                Color red = Color.RED;
                Color blue = Color.BLUE;
		bv0 = new BoundaryRangeValues(red, red, red);
		bv1 = new BoundaryRangeValues(blue, blue, blue);
		// BoundaryRangeValues bv2 = new BoundaryRangeValues(.5, .5, .5);
		contMapping.addPoint(-.5, bv0);
		contMapping.addPoint(0, bv1);
		opacityCalculator = new BasicCalculator("Edge Color Calculator", contMapping, VisualPropertyType.EDGE_COLOR);
		edgeAppCalc.setCalculator(opacityCalculator);


		style.setEdgeAppearanceCalculator(edgeAppCalc);
		style.setNodeAppearanceCalculator(nodeAppCalc);
		style.setGlobalAppearanceCalculator(globalAppCalc);
		if (newStyle) {
			calculatorCatalog.addVisualStyle(style);
			manager.setVisualStyle(style);
		}
		return style;
	}

}
