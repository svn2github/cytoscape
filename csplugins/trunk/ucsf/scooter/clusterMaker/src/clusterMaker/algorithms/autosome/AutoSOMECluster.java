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
package clusterMaker.algorithms.autosome;

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
import clusterMaker.algorithms.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.EdgeAttributeHandler;
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

import clusterMaker.algorithms.autosome.launch.Settings;

import java.util.*;

import cytoscape.layout.CyLayoutAlgorithm;


public class AutoSOMECluster extends AbstractNetworkClusterer implements TunableListener, ActionListener {

	private String AutoSOME_Mode = "Normal";
	private String[] mode = new String[]{"Normal","Precision","Speed"};
	private Settings settings = new Settings();
	private String[] attributeArray = new String[1];
	private String dataAttribute;
	private boolean heatmap=false;
	private boolean finishedClustering = false;
	private boolean ignoreMissing = false;
	private boolean selectedNodes = false;
	private List<CyEdge> edges;
	private int MAXEDGES = 2000;

	private EdgeAttributeHandler edgeAttributeHandler = null;

	private TaskMonitor monitor = null;
	private CyLogger logger = null;
	private RunAutoSOME runAutoSOME = null;

	private Tunable logscaling, unitVar, medCent, sumSqr, ensembleRuns, fcnInput, fcnDM, mxedges;

	private List<NodeCluster> nodeCluster;

	private List<String>attrList;
	private List<String>attrOrderList;
	private List<String>nodeOrderList;
	
	public AutoSOMECluster(boolean heatmap) {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_AutoSOME_cluster";
		logger = CyLogger.getLogger(AutoSOMECluster.class);
		initializeProperties();
		this.heatmap = heatmap;
	}

	public String getShortName() {
		if (heatmap)
			return "AutoSOME (HeatMap)";
		else
			return "AutoSOME (Network)";
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
		clusterProperties.add(new Tunable("tunables_panel",
		                                  "AutoSOME Basic Tuning",
		                                  Tunable.GROUP, new Integer(4)));

                Tunable tun = new Tunable("AutoSOME_Mode","Running Mode",
		                                   Tunable.LIST, 0,
		                                   (Object)mode, new Integer(0), 0);
                tun.addTunableValueListener(this);
		clusterProperties.add(tun);

		// Clustering Threshold
                ensembleRuns = new Tunable("ensembleRuns",
		                                  "Number of Ensemble Runs",
		                                  Tunable.INTEGER, new Integer(50),
		                                  new Integer(1), (Object)null, 0);
		clusterProperties.add(ensembleRuns);

		// Number of iterations
		clusterProperties.add(new Tunable("pvalueThresh",
		                                  "P-Value Threshold",
		                                  Tunable.DOUBLE, new Double(0.05),
		                                  new Integer(0), new Integer(1), 0));

		// Number of iterations
		clusterProperties.add(new Tunable("numThreads",
		                                  "Number of Threads (No. CPUs)",
		                                  Tunable.INTEGER, new Integer(Runtime.getRuntime().availableProcessors()),
		                                  new Integer(1), (Object)null, 0));



                //normalization tunables
                clusterProperties.add(new Tunable("tunables_panel2",
		                                  "Data Normalization",
		                                  Tunable.GROUP, new Integer(5), new Boolean(true), null, Tunable.COLLAPSABLE));

                Tunable norm_mode = new Tunable("norm_mode",
		                                  "Normalization mode",
		                                  Tunable.LIST, 0,
		                                  new Object[]{"Custom","No normalization", "Expression data 1", "Expression data 2" }, (Object)null, 0);

                norm_mode.addTunableValueListener(this);
                clusterProperties.add(norm_mode);
                logscaling = new Tunable("logScaling",
		                                  "Log2 Scaling",
		                                  Tunable.LIST, 0,
		                                  new Object[]{"No", "Yes"}, (Object)null, 0);
                clusterProperties.add(logscaling);

                unitVar = new Tunable("unitVariance",
		                                  "Unit Variance",
		                                  Tunable.LIST, 0,
		                                  new Object[]{"No", "Yes"}, (Object)null, 0);
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


                //fuzzy clustering tunables
                clusterProperties.add(new Tunable("fuzzyclustering", "Fuzzy Cluster Network Settings",
		                                  Tunable.GROUP, new Integer(4)));/*,
		                                  new Boolean(true), null, Tunable.COLLAPSABLE));*/
                Tunable fcn = new Tunable("enableFCN", "Perform Fuzzy Clustering",
		                                  Tunable.BOOLEAN, new Boolean(false));
                fcn.addTunableValueListener(this);
               clusterProperties.add(fcn);

               fcnInput = new Tunable("FCNInput",
		                                  "Source Data",
		                                  Tunable.LIST, 0,
		                                  new Object[]{"Nodes (Genes)", "Attributes (Arrays)"}, (Object)null, 0);
               fcnInput.setImmutable(true);
               clusterProperties.add(fcnInput);

               fcnDM = new Tunable("FCNDM",
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
                clusterProperties.add(new Tunable("advancedAut", "Additional Settings",
		                                  Tunable.GROUP, new Integer(1),
		                                  new Boolean(true), null, Tunable.COLLAPSABLE));

                
                clusterProperties.add(new Tunable("FillMV",
		                                  "Missing value handling",
		                                  Tunable.LIST, 0,
		                                  new Object[]{"Row Mean", "Row Median", "Column Mean", "Column Median"}, (Object)null, 0));


                //data input tunables
                clusterProperties.add(new Tunable("tunables_panel3",
		                                  "Data Input",
		                                  Tunable.GROUP, new Integer(3)));
                attributeArray = getAllAttributes();
		clusterProperties.add(new Tunable("attributeList",
		                                  "Array Sources (Node Attributes)",
		                                  Tunable.LIST, "",
		                                  (Object)attributeArray, (Object)null, Tunable.MULTISELECT));

                clusterProperties.add(new Tunable("selectedNodes", "Only use selected nodes for clustering",
		                                  Tunable.BOOLEAN, new Boolean(false)));

                clusterProperties.add(new Tunable("ignoreMissing", "Ignore nodes/edges with no data",
		                                  Tunable.BOOLEAN, new Boolean(true)));

              
               //output tunables
               clusterProperties.add(new Tunable("tunables_panel4",
		                                  "Data Output",
		                                  Tunable.GROUP, new Integer(2)));

                Tunable data_output = new Tunable("cluster_output",
		                                  "Choose Visualization",
		                                  Tunable.LIST, 0,
		                                  new Object[]{"Network", "Heatmap"}, (Object)null, 0);
                data_output.addTunableValueListener(this);
                clusterProperties.add(data_output);

                

                Tunable nwbutton = new Tunable("showDisplay",
		                                  "",
		                                  Tunable.BUTTON, "Display",
		                                  this, null, Tunable.IMMUTABLE);
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

		Tunable t = clusterProperties.get("AutoSOME_Mode");
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
			settings.logNorm = ((t.getValue()).toString().equals("1")) ? true : false;

                t = clusterProperties.get("unitVariance");
		if ((t != null) && (t.valueChanged() || force))
			settings.unitVar = ((t.getValue()).toString().equals("1")) ? true : false;

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
                    String val = (t.getValue()).toString();
                    if(val.equals("false")){
                        ignoreMissing=false;
                    } else ignoreMissing=true;
                }

                  t = clusterProperties.get("selectedNodes");
		if ((t != null) && (t.valueChanged() || force)){
                    String val = (t.getValue()).toString();
                    if(val.equals("false")){
                        selectedNodes=false;
                    } else selectedNodes=true;
                }
                
                 t = clusterProperties.get("enableFCN");
		if ((t != null) && (t.valueChanged() || force)){
                    String val = (t.getValue()).toString();
                    if(val.equals("false")){
                        settings.distMatrix=false;
                    } else settings.distMatrix=true;
                }

                t = clusterProperties.get("FCNInput");
		if ((t != null) && (t.valueChanged() || force)){
                    String val = (t.getValue()).toString();
                    if(val.equals("0")){
                        settings.FCNrows=true;
                    } else  settings.FCNrows=false;
                }

                t = clusterProperties.get("FCNDM");
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


                t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttribute = (String) t.getValue();
		}

                t = clusterProperties.get("cluster_output");
		if ((t != null) && (t.valueChanged() || force))
			heatmap = ((t.getValue()).toString().equals("1")) ? true : false;

                if (dataAttribute != null) {
				Tunable et = clusterProperties.get("showDisplay");
				et.clearFlag(Tunable.IMMUTABLE);
                                //et = clusterProperties.get("maxEdges");
				//et.clearFlag(Tunable.IMMUTABLE);
			}

		//edgeAttributeHandler.updateSettings(force);
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

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

                List<List<CyNode>> nodeClusters;

                if(!settings.distMatrix){

                    nodeClusters =
                        createGroups(netAttributes, networkID, nodeAttributes, clusters);                   
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
                monitor.setStatus("Done.  AutoSOME results:\n"+clusters.size()+" clusters found.");
                
               

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
				attributeList.add(prefix+names[i]);
			}
		}
	}

	private String[] getAllAttributes() {
		attributeArray = new String[1];
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(),"edge.");
		String[] attrArray = attributeList.toArray(attributeArray);
		if (attrArray.length > 1)
			Arrays.sort(attrArray);
		return attrArray;
	}

        public void tunableChanged(Tunable tunable) {
		updateSettings(false);

		Tunable t = clusterProperties.get("norm_mode");
		t.clearFlag(Tunable.IMMUTABLE);
                switch(Integer.valueOf((t.getValue().toString()))){
                    case 1:
                        logscaling.setValue(0);
                        unitVar.setValue(0);
                        medCent.setValue(0);
                        sumSqr.setValue(0);
                        break;
                    case 2:
                        logscaling.setValue(1);
                        unitVar.setValue(1);
                        medCent.setValue(1);
                        sumSqr.setValue(0);
                        break;
                    case 3:
                        logscaling.setValue(1);
                        unitVar.setValue(1);
                        medCent.setValue(1);
                        sumSqr.setValue(3);
                        break;
                }

                Tunable t3 = clusterProperties.get("showDisplay");
		t3.clearFlag(Tunable.IMMUTABLE);


                Tunable t2 = clusterProperties.get("cluster_output");
		t2.clearFlag(Tunable.IMMUTABLE);
                heatmap = (Integer.valueOf((t2.getValue().toString()))==0) ? false : true;

                Tunable t4 = clusterProperties.get("AutoSOME_Mode");
                t4.clearFlag(Tunable.IMMUTABLE);
                int c = Integer.valueOf((t4.getValue().toString()));
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
                }

                Tunable t5 = clusterProperties.get("enableFCN");
                String s = t5.getValue().toString();
                if(s.equals("true")){
                    fcnInput.setImmutable(false);
                    fcnDM.setImmutable(false);
                    mxedges.setImmutable(false);
                }else{
                    fcnInput.setImmutable(true);
                    fcnDM.setImmutable(true);
                    mxedges.setImmutable(true);
                }

	}

        public void actionPerformed(ActionEvent e){
            if(finishedClustering){
                Tunable t = clusterProperties.get("maxEdges");
                MAXEDGES = Integer.valueOf((t.getValue().toString()));
                Tunable t2 = clusterProperties.get("cluster_output");
                heatmap = (Integer.valueOf((t2.getValue().toString()))==0) ? false : true;

                CyAttributes netAttr = Cytoscape.getNetworkAttributes();
                String netID = Cytoscape.getCurrentNetwork().getIdentifier();
                netAttr.setListAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE, attrList);
                netAttr.setListAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE, attrOrderList);
                netAttr.setListAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE, nodeOrderList);

                if(heatmap){
                    netAttr.setAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, "kmeans");
                    KnnView tv = new KnnView();
                    tv.startViz();
                    tv.setVisible(true);
                }else{
                    if(!settings.distMatrix){
                        NewNetworkView nnv = new NewNetworkView(true);
                        nnv.startViz();
                        nnv.setVisible(true);
                    }else{
                        edges=runAutoSOME.getEdges(MAXEDGES);
                        createClusteredNetwork();
                    }
                }
            }
        }

        private void createClusteredNetwork() {
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();

                //Cytoscape.
		// Create the new network
		CyNetwork net = Cytoscape.createNetwork(currentNetwork.getTitle()+"--clustered",currentNetwork,false);
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
				String nodeLabel = cn.getIdentifier().split("_")[0];
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
		Cytoscape.setCurrentNetwork(net.getIdentifier());
		Cytoscape.setCurrentNetworkView(view.getIdentifier());
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

		//set edge width
		ContinuousMapping contMapping = new ContinuousMapping(new Double(0),ObjectMapping.EDGE_MAPPING);

		contMapping.setControllingAttributeName("CONFIDENCE");
		Interpolator numTonum = new LinearNumberToNumberInterpolator();
		contMapping.setInterpolator(numTonum);
		BoundaryRangeValues bv0 = new BoundaryRangeValues(.5, .5, .5);
		BoundaryRangeValues bv1 = new BoundaryRangeValues(10, 10, 10);
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
		bv1 = new BoundaryRangeValues(150, 150, 150);
		// BoundaryRangeValues bv2 = new BoundaryRangeValues(.5, .5, .5);
		contMapping.addPoint(-.5, bv0);
		contMapping.addPoint(.5, bv1);
		Calculator opacityCalculator = new BasicCalculator("Edge Opacity Calculator", contMapping, VisualPropertyType.EDGE_OPACITY);
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
