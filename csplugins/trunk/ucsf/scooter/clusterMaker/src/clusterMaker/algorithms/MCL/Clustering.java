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
package clusterMaker.algorithms.MCL;

import java.util.Vector;

public class Clustering {

	
	private double clusteringThresh; 
	private Vector <Vector> clusters;
	public int numClusters = -1;
	
	public Clustering(int size,double clusteringThresh)
	{
		clusters = new Vector(size);
		
		//initally, each element is in its own cluster
		for(int i=0; i<size; i++)
		{
			Vector <Integer> v = new Vector();
			v.add(new Integer(i));
			clusters.add(v);
		}
		
		clusteringThresh = this.clusteringThresh;
	}
	
	//Find the cluster that contains a particular element
	private int getClusterIndex(int element)
	{
		for(int i=0; i<clusters.size(); i++)
		{
			Vector v = (Vector)clusters.elementAt(i);
			
			if (v.contains(new Integer(element)))
				return i;
		}
		
		return -1;
	}
	
	//combine two clusters together into a single cluster
	private void combineClusters(int index1,int index2)
	{
		Vector <Integer> cluster1 = (Vector<Integer>)clusters.elementAt(index1);
		Vector <Integer> cluster2 = (Vector<Integer>)clusters.elementAt(index2);
		Vector <Integer> cluster3 = new Vector<Integer>(cluster1);
		cluster3.addAll(cluster2);
		clusters.add(cluster3);
		clusters.removeElementAt(index1);
		
		//take into account shift in indices after removal of first element
		if(index2 < index1)
			clusters.removeElementAt(index2);
		else
			clusters.removeElementAt(index2-1);
		
	
	}
	
	//checks indeces of two elements and clusters them together if they are not clustered allready
	private void clusterElements(int element1,int element2)
	{
		int index1 = getClusterIndex(element1);
		int index2 = getClusterIndex(element2);
		
		
		if (index1 != index2)
			if((index1 != -1) && (index2 != -1))
				combineClusters(index1,index2);
	}
	
	//Takes as input double array representing network and clusters the nodes. Returns an array mapping each node to a specific cluster
	public double[] clusterMatrix(double[][] graph)
	{
		double[] clusterArray = new double[graph.length];
		int element;
		
		//go through matrix and cluster connected elements
		for(int i=0; i<graph.length; i++)
			for(int j=0; j<graph.length; j++)
			{
				if(i == j)
					continue;
				
				if(graph[i][j] > clusteringThresh)
					clusterElements(i,j);
			}
		
		
		numClusters = clusters.size();
		
		//Assign each node to a cluster
		for(int i=0; i<clusters.size(); i++)
		{
			Vector v = (Vector)clusters.elementAt(i);
			
			for(int j=0; j < v.size(); j++)
			{
				element = ((Integer)v.elementAt(j)).intValue();
				clusterArray[element] = i;
			}
		}
		
		return clusterArray;
	}
	
}
