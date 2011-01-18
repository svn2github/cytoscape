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

import clusterMaker.algorithms.autosome.launch.Settings;




// clusterMaker imports

public class AutoSOMECluster extends AbstractNetworkClusterer implements TunableListener, ActionListener {
	

        private String AutoSOME_Mode = "Normal";
        private String[] mode = new String[]{"Normal","Precision","Speed"};
        private Settings settings = new Settings();
        private String[] attributeArray = new String[1];
        private String dataAttribute;
        private boolean heatmap=false;
        private boolean finishedClustering = false;

	private EdgeAttributeHandler edgeAttributeHandler = null;

	private TaskMonitor monitor = null;
	private CyLogger logger = null;
	private RunAutoSOME runAutoSOME = null;

        private Tunable logscaling, unitVar, medCent, sumSqr;


	public AutoSOMECluster() {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_AutoSOME_cluster";
		logger = CyLogger.getLogger(AutoSOMECluster.class);
		initializeProperties();
	}

	public String getShortName() {return "AutoSOME";};
	public String getName() {return "AutoSOME Cluster";};

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
		//return new NewNetworkView(true);
            return (heatmap) ? new TreeView() : new NewNetworkView(true);
	}

	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */
		clusterProperties.add(new Tunable("tunables_panel",
		                                  "AutoSOME Basic Tuning",
		                                  Tunable.GROUP, new Integer(4)));

		
		clusterProperties.add(new Tunable("AutoSOME_Mode","Running Mode",
		                                   Tunable.LIST, 0,
		                                   (Object)mode, new Integer(0), 0));

		// Clustering Threshold
		clusterProperties.add(new Tunable("ensembleRuns",
		                                  "Number of Ensemble Runs",
		                                  Tunable.INTEGER, new Integer(50),
		                                  (Object)null, (Object)null, 0));

		// Number of iterations
		clusterProperties.add(new Tunable("pvalueThresh",
		                                  "P-value Threshold",
		                                  Tunable.DOUBLE, new Double(0.05),
		                                  (Object)null, (Object)null, 0));

		// Number of iterations
		clusterProperties.add(new Tunable("numThreads",
		                                  "Number of Threads",
		                                  Tunable.INTEGER, new Integer(Runtime.getRuntime().availableProcessors()-1),
		                                  (Object)null, (Object)null, 0));
                clusterProperties.add(new Tunable("tunables_panel2",
		                                  "Data Normalization",
		                                  Tunable.GROUP, new Integer(5)));

                Tunable norm_mode = new Tunable("norm_mode",
		                                  "Normalization mode",
		                                  Tunable.LIST, 0,
		                                  new Object[]{"No normalization", "Expression data 1", "Expression data 2" }, (Object)null, 0);
                
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

                clusterProperties.add(new Tunable("tunables_panel3",
		                                  "Data Input",
		                                  Tunable.GROUP, new Integer(1)));
                attributeArray = getAllAttributes();
		clusterProperties.add(new Tunable("attributeList",
		                                  "Array Sources",
		                                  Tunable.LIST, "",
		                                  (Object)attributeArray, (Object)null, Tunable.MULTISELECT));

              /*  clusterProperties.add(new Tunable("tunables_panel4",
		                                  "AutoSOME Fuzzy Cluster Networks",
		                                  Tunable.GROUP, new Integer(3),new Boolean(true), null, Tunable.COLLAPSABLE));*/

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
			}

		//edgeAttributeHandler.updateSettings(force);
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		/*DistanceMatrix matrix = edgeAttributeHandler.getMatrix();
		if (matrix == null) {
			logger.error("Can't get distance matrix: no attribute value?");
			return;
		}*/

		//Cluster the nodes
		runAutoSOME = new RunAutoSOME(dataAttribute, attributeArray,settings, logger);

		runAutoSOME.setDebug(debug);

		// results = runMCL.run(monitor);
		List<NodeCluster> clusters = runAutoSOME.run(monitor);
                if(clusters.size()>0) finishedClustering=true;

                if(clusters==null) {
                    monitor.setStatus("Clustering failed!");
                    return;
                }

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters);

		ClusterResults results = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  AutoSOME results:\n"+results);

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
                    case 0:
                        logscaling.setValue(0);
                        unitVar.setValue(0);
                        medCent.setValue(0);
                        sumSqr.setValue(0);
                        break;
                    case 1:
                        logscaling.setValue(1);
                        unitVar.setValue(1);
                        medCent.setValue(1);
                        sumSqr.setValue(0);
                        break;
                    case 2:
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

	}

        public void actionPerformed(ActionEvent e){
            if(finishedClustering){
		Tunable t2 = clusterProperties.get("cluster_output");
                heatmap = (Integer.valueOf((t2.getValue().toString()))==0) ? false : true;
                if(heatmap){
                    TreeView tv = new TreeView();
                    tv.startViz();
                    tv.setVisible(true);
                }else{
                    NewNetworkView nnv = new NewNetworkView(true);
                    nnv.startViz();
                    nnv.setVisible(true);
                }
            }
        }

}
