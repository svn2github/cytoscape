package de.costmatrixcreation.main;

import de.costmatrixcreation.gui.Console;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import de.costmatrixcreation.dataTypes.Edges;

public class Splitter {
	
	private  float threshold;
		
	public void run(HashMap<Integer, String> proteins2integers, HashMap<String, Integer> integers2proteins) throws IOException {
	
		this.threshold = Config.threshold;
		
		if(Config.gui)	Console.println("Start reading similarityFile ... ");
		else System.out.println("Start reading similarityFile ... ");
		Edges es = InOut.readSimilarityFile(Config.similarityFile, proteins2integers, integers2proteins);
		if(Config.gui)Console.println();
		else System.out.println();
		
		if(Config.gui){
			Console.println("Start splitting ...");
			Console.setBarValue(0);
			Console.setBarText("splitting into connected components");
		}else System.out.println("Start splitting ...");
		
		Vector<Vector<Integer>> clusters = splitIntoConnectedComponents(es, proteins2integers,threshold,false);
		if(Config.gui) Console.println();
		else System.out.println();
		
		if(Config.gui){
			Console.println("Writing costmatrices ...");
			Console.setBarValue(0);
			Console.restartBarTimer();
			Console.setBarText("writing costmatrices");
		}else System.out.println("Writing costmatrices ...");
		
		InOut.writeCostMatrices(es, clusters, proteins2integers, integers2proteins);
		if(Config.gui) Console.println();
		else System.out.println();
		
	}
	
	private static Vector<Vector<Integer>> splitIntoConnectedComponents(Edges es, HashMap<Integer,String> proteins2integers,float threshold,boolean mergenNodes){
		Vector<Vector<Integer>> v = new Vector<Vector<Integer>>();
		int[] distribution = new int[es.size2()+1];
//		HashMap<Integer,Boolean> already = new HashMap<Integer, Boolean>();
		boolean[] already = new boolean[es.size2()];
		
		for (Iterator iter = proteins2integers.keySet()	.iterator(); iter.hasNext();) {
			Integer element = (Integer) iter.next();
			if(!already[element]){
				Vector<Integer> cluster = new Vector<Integer>();
				cluster.add(element);
				already[element] = true;
				findCluster(es, cluster,proteins2integers,element,already,threshold,mergenNodes);
				v.add(cluster);
				distribution[cluster.size()]++;
			}
		}
		
		for (int i = 0; i < distribution.length; i++) {
			if(distribution[i]!=0){
				Console.println(i + "\t" + distribution[i]);
			}
		}
			
		return v;
		
	}
	
	private static void findCluster(Edges es, Vector<Integer> cluster, HashMap<Integer, String> proteins2integers, Integer element, boolean[] already, float threshold,boolean mergeNodes) {

		int startPosition=0;
		int endPosition = 0;

		if(mergeNodes){
			startPosition = es.getStartPosition(Integer.parseInt(proteins2integers.get(element)));
			endPosition = es.getEndPosition(Integer.parseInt(proteins2integers.get(element)));
		}else{
			startPosition = es.getStartPosition(element);
			endPosition = es.getEndPosition(element);
		}
		
		for (int i = startPosition; i < endPosition; i++) {
			
			int target = es.getTarget(i);
			
			if(!already[target]){
				
				double value = es.getValue(i);
				
				if(value>threshold){
					cluster.add(target);
					already[target]= true;
					findCluster(es, cluster,proteins2integers, target, already, threshold,mergeNodes);
				}
				
			}
			
		}
		
	}// end findClusters

}
