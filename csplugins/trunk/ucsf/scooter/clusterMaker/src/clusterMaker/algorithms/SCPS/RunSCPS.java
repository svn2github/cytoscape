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
package clusterMaker.algorithms.SCPS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
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
import clusterMaker.algorithms.ClusterResults;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.colt.matrix.linalg.SingularValueDecomposition;



import cern.colt.matrix.linalg.Algebra;
import cern.colt.list.IntArrayList;
import cern.colt.list.DoubleArrayList;

import clusterMaker.algorithms.SCPS.KCluster;

public class RunSCPS {

       
       
        private List<CyNode> nodes;
        private List<CyEdge> edges;
        private boolean canceled = false;
        private CyLogger logger;
        public final static String GROUP_ATTRIBUTE = "__SCPSGroups";
        protected int clusterCount = 0;
        private boolean createMetaNodes = false;
        private DistanceMatrix distanceMatrix = null;
        private DoubleMatrix2D matrix = null;
        private boolean debug = false;
        
	private double epsilon;
	private int kvalue;
        private int rnumber;
	private DoubleMatrix2D LMat;
       

        private  HashMap<Integer, NodeCluster> clusterMap;

        private HashMap<Integer,Integer> new2oldMap;
        private HashMap<Integer,Integer> old2newMap;

        

        public RunSCPS(DistanceMatrix dMat, double epsilon, int kvalue, int rnumber, CyLogger logger )
        {
                this.distanceMatrix = dMat;
                this.epsilon = epsilon;
                this.kvalue = kvalue;
		this.rnumber = rnumber; 
                
                this.logger = logger;
		this.clusterMap = new HashMap<Integer,NodeCluster>();
		this.clusterCount = 0;
                nodes = distanceMatrix.getNodes();
                edges = distanceMatrix.getEdges();
                this.matrix = distanceMatrix.getDistanceMatrix();
		
		
		//maps indices of filtered nodes in new uMatrix to the the original in the complete, unfiltered matrix, and vice-versa
		this.old2newMap = new HashMap<Integer,Integer>();
		this.new2oldMap = new HashMap<Integer,Integer>();
	}


        public void halt () { canceled = true; }

        public List<NodeCluster> run(TaskMonitor monitor)
        {
	   
	    int k;

	    monitor.setStatus("Formatting Matrix Data");
	    DoubleMatrix2D sMat = getSMat(this.distanceMatrix);
	    DoubleMatrix2D LMat = getLMat(sMat);

	    monitor.setStatus("Calculating K value");

	    if(this.kvalue > -1)
		k = this.kvalue;
	    else
		k = getK(sMat);
		       
	    System.out.println("K is " + k);

            monitor.setStatus("Calculating Eigenvalues");
	    DoubleMatrix2D uMat = getUMat(LMat,k);
            monitor.setStatus("Running kmeans clustering");
	    doKMeansClustering(uMat);

	    //clusterMap calculated in getSMat and doKMeansClustering steps. Simply return the results
	    return new ArrayList(this.clusterMap.values());


	}

        //map old2new and new2old key-value pair
        public void setMap(int old_index, int new_index){

	    
	    new2oldMap.put(new Integer(new_index), new Integer(old_index));
	    old2newMap.put(new Integer(old_index), new Integer(new_index));
	}

       //return value of old_index in new2oldMap. Mapping should invariably exist. 
        public int getMap_old(int new_index){

		return ((Integer)new2oldMap.get(new Integer(new_index))).intValue();
	}

        //return value of new_index in old2newMap. If no such mapping exists, return -1.
        public int getMap_new(int old_index){

	    try{ 
		return ((Integer)old2newMap.get(new Integer(old_index))).intValue(); 
	    }

	    catch(Exception e){ return -1;}

	}

       //Get Connected Components, cluster all components <= |5|, and connect the remaining components with random lowscoring edges
       public DoubleMatrix2D getSMat(DistanceMatrix distanceMatrix){

	   //Matrix prior to filtration modification
	   DoubleMatrix2D unfiltered_mat = distanceMatrix.getDistanceMatrix();

	   //Size of newly created Umat after filtering of small components
	   int sMat_rows = 0;

	   HashMap<Integer, List<CyNode>> filtered_cmap = new HashMap<Integer, List<CyNode>>();

	   //Connected Componets
	   Map<Integer, List<CyNode>> cMap = distanceMatrix.findConnectedComponents();

	   IntArrayList rowList = new IntArrayList();
	   IntArrayList columnList = new IntArrayList();
	   DoubleArrayList valueList = new DoubleArrayList();
	   
	   //Iterate through connected components
	   Iterator it = cMap.entrySet().iterator();
	   
	   while(it.hasNext()){

	       Map.Entry entry = (Map.Entry)it.next();
	       List<CyNode> component = (List<CyNode>)entry.getValue();

	       //Size <= 5. Automatically create cluster and increment clusterCount. 
	       if(component.size() <= 5){
		   
		   NodeCluster iCluster = new NodeCluster(component);
		   //iCluster.add(component,this.clusterCount);
		   this.clusterMap.put(new Integer(clusterCount),iCluster);

		   System.out.println("Component Size " + component.size() + " ClusterCount " + clusterCount);
		   this.clusterCount++;

		 

	       }

	       //iterate through components and assign them index mappings in new uMatrix
	       else{

		   for(int i = 0; i < component.size(); i++){
		       
		       CyNode n = component.get(i);
		       
		    
		       
		       //set mapping of new matrix index to old index
		       setMap(this.nodes.indexOf(n), sMat_rows);

		       sMat_rows++;
		   }

	       }

	   }

	   SparseDoubleMatrix2D sMat = new SparseDoubleMatrix2D(sMat_rows, sMat_rows);

	   //set diagnols of sMat to one
	   for(int i = 0; i < sMat_rows; i++)
	       sMat.set(i,i,1);
              

	   //iterate through nonzero edges. If both nodes in new index map, transfer the edge to new matrix
	   unfiltered_mat.getNonZeros(rowList,columnList,valueList);

	   for(int i = 0; i<rowList.size(); i++){

	       int row_id = rowList.get(i);
	       int column_id = columnList.get(i);

	       int new_row_id = getMap_new(row_id);
	       int new_column_id = getMap_new(column_id);
	       double value = valueList.get(i);

	       if(new_row_id == 37)
		    System.out.println("!!! 37 ROW::" + row_id + " Column:" + column_id + " Value:"+value);

	       //Set symmetrically the values in new matrix
	       if(new_row_id > -1 && new_column_id > -1)
		   {
		       sMat.set(new_row_id,new_column_id,value);
		       sMat.set(new_column_id,new_row_id,value);
		   }

	   }

	   
	   
	   return sMat;

       }
       
         //Calculate negative square root of matrix using singular value decomposition
         public DoubleMatrix2D getNegSqrRoot(DoubleMatrix2D A){

	     //A = USV, where S is Diagnol Matrix
	     SingularValueDecomposition decomp = new SingularValueDecomposition(A);
	     DoubleMatrix2D U = decomp.getU();
	     DoubleMatrix2D S = decomp.getS();
	     DoubleMatrix2D V = decomp.getV();

	     //S^1/2 = Square root of every value in diangol matrix
	     for(int i = 0; i < S.rows(); i++)
		 S.set(i,i,Math.pow(S.get(i,i),.5));
		

	     //A^1/2 = VS^1/2U
	     Algebra alg = new Algebra();
	     DoubleMatrix2D sqrtA = alg.mult(alg.mult(V,S),U);

	    

	     //return A^-1/2
	     return alg.inverse(sqrtA);
	     
	     
	 }

         //Return Negative Sqrt of a Diagnol Matrix
         public DoubleMatrix2D getNegSqrRootDMat(DoubleMatrix2D dMat){

	     for(int i = 0; i < dMat.rows(); i++)
		 dMat.set(i,i,Math.pow(dMat.get(i,i),-.5));

	     return dMat;
	 }


	// L = D^-1/2 * S * D^-1/2
	public DoubleMatrix2D getLMat(DoubleMatrix2D sMat){

	        Algebra alg = new Algebra();
		DoubleMatrix2D dMat = getDMat(sMat);
		DoubleMatrix2D transDMat = getNegSqrRootDMat(dMat);
	
		return alg.mult(transDMat,alg.mult(sMat,transDMat));
	}


	//D is Diagnol Matrix formed of vertex degree Dii = Sum Columns j over row Si
	public DoubleMatrix2D getDMat(DoubleMatrix2D sMat){
	
	
		DoubleMatrix2D dMat = sMat.like();

		for(int i = 0; i < sMat.rows(); i++){

			//set the Diagnal (i,i) to sum of columns over row i
			dMat.set(i,i, sMat.viewRow(i).zSum());
			
			if(dMat.get(i,i) <= 5)
			    System.out.println("DMat row "+ i + " value " + dMat.get(i,i));
		}

		
		return dMat;	

	}


        
	//Get K using eigenvetors of S Matrix
	public int getK(DoubleMatrix2D sMat){

	    	double prevLamb;
		double nextLamb;
		int k;
			
		EigenvalueDecomposition decomp = new EigenvalueDecomposition(sMat);

		//eigenvectors
		DoubleMatrix2D eigenVect = decomp.getV();

		//eigenvalues
		DoubleMatrix1D eigenVal = decomp.getRealEigenvalues();

		//set K to smallest integer such that LambdaK/LambdaK+1 > epsilon
		prevLamb = eigenVal.get(0);

		for(k = 1; k < eigenVal.size(); k++){

		    nextLamb = eigenVal.get(k);

		    System.out.println("k " + k + " PrevLamb " + prevLamb + " nextLamb " + nextLamb +   "prevLamb/nextLamb " + prevLamb/nextLamb);

		    if(prevLamb/nextLamb > this.epsilon)
			break;

		    prevLamb = nextLamb;
		}

		return k;
	}

	//U constructed from top K Eigenvectors of L. After construction, each row of U is normalized to unit length.
	public DoubleMatrix2D getUMat(DoubleMatrix2D LMat, int k){

		DoubleMatrix2D uMat;
	
		IntArrayList indexList = new IntArrayList();
		DoubleArrayList valueList = new DoubleArrayList();
		
		EigenvalueDecomposition decomp = new EigenvalueDecomposition(LMat);

		//eigenvectors
		DoubleMatrix2D eigenVect = decomp.getV();

		//construct matrix U from first K eigenvectors
	 	uMat = eigenVect.viewPart(0,0,eigenVect.rows(),k);
		
	       
		//Normalize each row of matrix U to have unit length
		for(int i = 0; i < uMat.columns(); i++){

			DoubleMatrix1D row = uMat.viewRow(i);
			double rowLength = Math.pow(row.zDotProduct(row),.5);
			row.getNonZeros(indexList,valueList);

			//normalize each Nozero value in row

			for(int j = 0; j < indexList.size(); j ++){

				int index = indexList.get(j);
				double value = valueList.get(j)/rowLength;
				
				uMat.set(i,index,value);
			}
			
		}
		
		
		return uMat;
	}

         public void doKMeansClustering(DoubleMatrix2D uMat){

	     int k = uMat.columns();

	     int[] clusters = new int[uMat.rows()];
	  
	     //do kmeans clustering
	     KCluster.kmeans(k,rnumber,uMat,clusters);

	     //Loop through clustering results, getting the clusters by order

	     for(int cluster_id = 0; cluster_id < k; cluster_id++){
		 
		 NodeCluster iCluster = new NodeCluster();
		 List<CyNode> node_list = new ArrayList();

		 for(int j = 0; j < clusters.length; j++){

		     //node j in uMatrix belongs to cluster #cluster_id
		     if(clusters[j] == cluster_id)
			 node_list.add(this.nodes.get(getMap_old(j)));
		 }

		 iCluster = new NodeCluster(node_list);
		 this.clusterMap.put(new Integer(clusterCount),iCluster);

		 System.out.println("clusterCount " + clusterCount);

		 this.clusterCount++;
	     }

		 
			 
	 }


        /**
         * Normalize normalizes a cell in the matrix
         */
        private class Normalize implements IntIntDoubleFunction {
                private double maxValue;
                private double minValue;
                private double span;
                private double factor;
	        private double minWeight = Double.MAX_VALUE;

                public Normalize(double minValue, double maxValue, double factor) {
                        this.maxValue = maxValue;
                        this.minValue = minValue;
                        this.factor = factor;
                        span = maxValue - minValue;
                }

                public double apply(int row, int column, double value) {
                        return ((value-minWeight)/span)*factor;
                }
        }

	
}
              
