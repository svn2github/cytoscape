package de.layclust.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.datastructure.CostMatrixReader;

public class TestCosts {
	
	public static void main (String[] args){
//		String inputcm = "de/layclust/data/cm/cost_matrix_component_nr_1_size_9_cutoff_20.0.cm";
//		String resultClusters = "nr_1_size_9.cls";
		
//		String inputcm = "/vol/assb/graph_cluster_files/FORCEnD_tests_sita/all_of_cog/NotReduced_threshold10_BeH_cm/normal_cm_BeH10_dir/costMatrix_size_24_nr_429.cm";
		String inputcm = "/vol/assb/graph_cluster_files/FORCEnD_tests_sita/all_of_cog/NotReduced_threshold10_BeH_cm/normal_cm_BeH10_dir/costMatrix_size_8_nr_23.cm";
		//		String resultClusters = "nr_429_size_24.cls";
		String resultClusters = "nr_23_size_8_FP.cls";
//		String resultClusters = "nr_10_size_41_FP.cls";
		
		int noOfClusters = 9;
		
		CostMatrixReader cmReader = new CostMatrixReader(new File(inputcm));
		ConnectedComponent cc = cmReader.getConnectedComponent();
		
		TestCosts testCosts = new TestCosts();
		
		Vector<Vector<String>> clusterObject = testCosts.parseClustering(resultClusters, noOfClusters);
		Vector<Vector<Integer>> clusterObjectInt = new Vector<Vector<Integer>>();
		String[] objectIDs = cc.getObjectIDs();
		
		/* turn string vector object into integer vector object */
		for (int i = 0; i < clusterObject.size(); i++) {
			Vector<Integer> v = new Vector<Integer>();
			clusterObjectInt.add(v);
			Vector<String> oldV = clusterObject.get(i);
			for (int j = 0; j < oldV.size(); j++) {
				int objectInt = testCosts.getObjectNoFromID(oldV.get(j), objectIDs);
				v.add(objectInt);
			}		
		}
		
		int[] clusters = testCosts.getClustersArrayFromVectorObject(clusterObjectInt, cc.getNodeNumber());
		cc.setClusters(clusters);
		double score = cc.calculateClusteringScore(clusters);

		

//		double score = PostProcessingUtility.updateClusterInfoInCC(clusterObjectInt, cc);
		System.out.println("The calculated score: "+score);
	}
	
	
	public Vector<Vector<String>> parseClustering(String resultClusters, int noOfclusters){
		
		Vector<Vector<String>> clusterObject = new Vector<Vector<String>>();
		for (int i = 0; i < noOfclusters; i++) {
			clusterObject.add(new Vector<String>());		
		}
		
		
		BufferedReader clsBuffer;
		try {
			/* read file */
			clsBuffer = new BufferedReader(new FileReader(new File(resultClusters)));

			String line = "";
			String object = "";
			String clusterNo = "";
			/* only read lines in this format */
			Pattern objectLine = Pattern.compile("(.+)\\t(\\d+)");
			Matcher objectMatcher;
			while ((line = clsBuffer.readLine()) != null) {
				objectMatcher = objectLine.matcher(line);
				if (objectMatcher.find()) {
					object = objectMatcher.group(1);
					clusterNo = objectMatcher.group(2);
					clusterObject.get(Integer.parseInt(clusterNo) -1).add(object);
				}
			}



		} catch (IOException e) {
			System.err.println("Unable to read this file:  " + resultClusters);
			e.printStackTrace();
			System.exit(-1);
		}
		
		return clusterObject;
		
	}
	
	public int getObjectNoFromID(String objectID, String[] allobjects){
		for (int i = 0; i < allobjects.length; i++) {
			if(allobjects[i].equals(objectID)){
				return i;
			}
		}
		return -1;
	}
	
	public int[] getClustersArrayFromVectorObject(Vector<Vector<Integer>> clusterObject, int noOfNodes){
		int[] clusters = new int[noOfNodes];
		for (int i = 0; i < clusterObject.size(); i++) {
			Vector<Integer> v = clusterObject.get(i);
			for (int j = 0; j < v.size(); j++) {
				int object = Integer.valueOf(v.get(j));
				clusters[object] = i;
			}
		}
		return clusters;
	}

}
