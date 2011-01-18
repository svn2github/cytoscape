/**
 * Copyright (c) 2010 The Regents of the University of California.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.Math;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.CyNode;

import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.NodeCluster;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.hierarchical.Matrix;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

import clusterMaker.algorithms.autosome.launch.*;
import clusterMaker.algorithms.autosome.cluststruct.*;
import clusterMaker.algorithms.edgeConverters.*;
import clusterMaker.ClusterMaker;

public class RunAutoSOME {


	private List<CyNode> nodes;
	private List<CyEdge> edges;
	private boolean canceled = false;
	private CyLogger logger;
	public final static String GROUP_ATTRIBUTE = "__AutoSOMEGroups";
	protected int clusterCount = 0;
	private boolean createMetaNodes = false;
	private DoubleMatrix2D matrix = null;
	private boolean debug = false;
        private String dataAttributes;
        String[] attributeArray = new String[1];
        private boolean ignoreMissing = false;
	private boolean selectedOnly = false;
        private Run autRun;
        private Settings settings;
	
	public RunAutoSOME(String dataAttribute, String[] attributeArray, Settings settings, CyLogger logger)
	{
	

                this.dataAttributes=dataAttribute;
                this.attributeArray=attributeArray;
		this.logger = logger;
                this.settings=settings;

		// logger.info("InflationParameter = "+inflationParameter);
		// logger.info("Iterations = "+num_iterations);
		// logger.info("Clustering Threshold = "+clusteringThresh);
	}

        public void setIgnoreMissing(boolean val) { ignoreMissing = val; }
	public void setSelectedOnly(boolean val) { selectedOnly = val; }


	public void halt () { canceled = true; autRun.kill();}

	public void setDebug(boolean debug) { this.debug = debug; }
	
	public List<NodeCluster> run(TaskMonitor monitor)
	{


		long startTime = System.currentTimeMillis();



		debugln("Initial matrix:");
		//printMatrix(matrix);

		// Normalize
		//normalize(matrix, clusteringThresh, false);

		debugln("Normalized matrix:");
		//printMatrix(matrix);

		// logger.info("Calculating clusters");

		if (dataAttributes == null || dataAttributes.length() == 0) {
			if (monitor != null) {
				logger.warning("Must have an attribute list to use for cluster weighting");
				monitor.setException(null, "Error: no attribute list selected");
			} else
				logger.error("Must have an attribute list to use for cluster weighting");
			return null;
		}

		// Get our attributes we're going to use for the cluster
		String attributeArray[] = getAttributeArray(dataAttributes);

                Settings s = new Settings();

                //get parameter settings
                s.ensemble_runs = settings.ensemble_runs;
                s.mst_pval = settings.mst_pval;
                s.threads = settings.threads;
                s.logNorm=settings.logNorm;
                s.unitVar=settings.unitVar;
                s.medCenter=settings.medCenter;
                s.medCenterCol=settings.medCenterCol;
                s.sumSqrRows=settings.sumSqrRows;
                s.sumSqrCol=settings.sumSqrCol;
                s.som_iters=settings.som_iters;
                s.de_resolution=settings.de_resolution;
                
                s.htmlOut=false;
                s.textOut=false;

                // Create the matrix
		Matrix matrix = new Matrix(attributeArray, false, ignoreMissing, selectedOnly);


                List<EdgeWeightConverter>converters = new ArrayList<EdgeWeightConverter>();
		converters.add(new NoneConverter());
		converters.add(new DistanceConverter());
		converters.add(new LogConverter());
		converters.add(new NegLogConverter());
		converters.add(new SCPSConverter());
		EdgeWeightConverter converter = converters.get(0);
                DistanceMatrix dm = new DistanceMatrix(dataAttributes, selectedOnly, converter);
                nodes = dm.getNodes();
                edges = dm.getEdges();

                s.input = new dataItem[matrix.nRows()];
                //matrix.printMatrix();

                HashMap key = new HashMap();
                for(int i = 0; i < nodes.size(); i++){
                    String id = nodes.get(i).getIdentifier();
                    if(!key.containsKey(id)) key.put(id,i);
                }

                for(int k = 0, itor=0; k < matrix.nRows(); k++){
                    float[] f = new float[matrix.nColumns()];
                    //System.out.println(matrix.getRowLabels()[k]+" "+nodes.get(k).getIdentifier());
                    for(int l = 0; l < f.length; l++) {
                        //System.out.println(matrix.getValue(k,l).floatValue());
                        if(matrix.getValue(k,l)!=null) f[l] = matrix.getValue(k,l).floatValue();
                        else {
                            f[l] = -99999999;
                            s.fillMissing=true;
                        }
                    }
                    s.input[itor++] = new dataItem(f, matrix.getRowLabel(k));
                }


                autRun = new Run();
                clusterRun cr = autRun.runAutoSOMEBasic(s, monitor);

                if(cr==null){
                    monitor.setStatus("Clustering failed!");
                    return null;
                }

                monitor.setStatus("Assigning nodes to clusters");
                clusterCount = cr.c.length;
                HashMap<NodeCluster,NodeCluster> cMap = new HashMap();
                ArrayList<String>attrList = new ArrayList();
                ArrayList<String>attrOrderList = new ArrayList();
                ArrayList<String>nodeOrderList = new ArrayList();

                for(int i = 0; i < matrix.nColumns(); i++) attrOrderList.add(matrix.getColLabel(i));

                for(int i = 0; i < clusterCount; i++){
                    if(cr.c[i].ids.isEmpty()) continue;
                    NodeCluster nc = new NodeCluster();
                    for(int j = 0; j < cr.c[i].ids.size(); j++){
                        int dataID = Integer.valueOf(cr.c[i].ids.get(j).toString());
                        int nodeDataID = Integer.valueOf(key.get(matrix.getRowLabels()[dataID]).toString());
                        nc.add(nodes.get(nodeDataID));
                        attrList.add(nodes.get(nodeDataID).getIdentifier()+"\t"+0+"\t"+0+"\t"+0);
                        nodeOrderList.add(nodes.get(nodeDataID).getIdentifier());
                    }
                    cMap.put(nc,nc);
                }

                CyAttributes netAttr = Cytoscape.getNetworkAttributes();
                String netID = Cytoscape.getCurrentNetwork().getIdentifier();
                netAttr.setListAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE, attrList);
                netAttr.setListAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE, attrOrderList);
                netAttr.setListAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE, nodeOrderList);

		if (canceled) {
			monitor.setStatus("canceled");
			return null;
		}
		

		//Update node attributes in network to include clusters. Create cygroups from clustered nodes
		logger.info("Created "+clusterCount+" clusters");
		// debugln("Created "+clusterCount+" clusters:");
		//
		if (clusterCount == 0) {
			logger.error("Created 0 clusters!!!!");
			return null;
		}

		

		logger.info("Total runtime = "+(System.currentTimeMillis()-startTime)+"ms");

		Set<NodeCluster>clusters = cMap.keySet();
		return new ArrayList(clusters);
	}	






	

	/**
	 * Debugging routine to print out information about a matrix
	 *
	 * @param matrix the matrix we're going to print out information about
	 */
	private void printMatrix(DoubleMatrix2D matrix) {
		for (int row = 0; row < matrix.rows(); row++) {
			debug(nodes.get(row).getIdentifier()+":\t");
			for (int col = 0; col < matrix.columns(); col++) {
				debug(""+matrix.get(row,col)+"\t");
			}
			debugln();
		}
		debugln("Matrix("+matrix.rows()+", "+matrix.columns()+")");
		if (matrix instanceof SparseDoubleMatrix2D)
			debugln(" matrix is sparse");
		else
			debugln(" matrix is dense");
		debugln(" cardinality is "+matrix.cardinality());
	}

	private void debugln(String message) {
		if (debug) System.out.println(message);
	}

	private void debugln() {
		if (debug) System.out.println();
	}

	private void debug(String message) {
		if (debug) System.out.print(message);
	}

	

	

	


        private String[] getAttributeArray(String dataAttributes) {
		String indices[] = dataAttributes.split(",");
		String selectedAttributes[] = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			selectedAttributes[i] = attributeArray[Integer.parseInt(indices[i])];
		}
		return selectedAttributes;
	}




}

