/* 
* Created on 11. December 2007
 * 
 */
package de.layclust.test;

import java.util.Vector;

import de.layclust.datastructure.ConnectedComponent;
import de.layclust.datastructure.ICCEdges;

/**
 * A Collection of testing methods.
 * @author Sita Lange
 * 2007
 *
 */
public class TestingUtility {
	

	public void setPositionsToValue(double[][] node_pos, int node_no, int dim){
		for( int i=0; i<node_no;i++){
			for(int d=0;d<dim;d++){
				node_pos[i][d] = i;
			}
		}
	}
	
	

	public static synchronized void printCurrentPositions(ConnectedComponent cc){
		StringBuffer posBuff = new StringBuffer();
		posBuff.append("\n==== CURRENT POSITIONS ====\n");
		for(int n=0;n<cc.getNodeNumber();n++){
			posBuff.append("node ");
			posBuff.append(n);
			posBuff.append(": (");
			double[] posn = cc.getCCPostions(n);
			for(int i=0;i<posn.length-1;i++){
				posBuff.append(posn[i]);
				posBuff.append(", ");
			}
			posBuff.append(posn[posn.length-1]);
			posBuff.append(")\n");			
		}
		posBuff.append("====================\n");
		
		System.out.println(posBuff.toString());
		
	}
	

	public static void printDisplacementMatrix(double[][] disArray, int node_no, int dim){
		StringBuffer posBuff = new StringBuffer();
		posBuff.append("\n==== CURRENT DISPLACEMENT POSITIONS ====\n");
		double[][] posn = disArray;
		for(int n=0;n<node_no;n++){
			posBuff.append("node ");
			posBuff.append(n);
			posBuff.append(": (");
			for(int i=0;i<dim-1;i++){
				posBuff.append(posn[n][i]);
				posBuff.append(", ");
			}
			posBuff.append(posn[n][dim-1]);
			posBuff.append(")\n");			
		}
		posBuff.append("====================\n");
		
		System.out.println(posBuff.toString());
		
	}
	
	public static void printEdges(ICCEdges ccEdges, int node_no) {
		System.out.println("==== EDGES ====");
		for (int i = 0; i < node_no; i++) {
			for (int j = 0; j < node_no; j++) {
				System.out.print(ccEdges.getEdgeCost(i, j) + ", ");
			}
			System.out.print("\n");
		}
		System.out.println("=======================");
	}
	
	public static synchronized void printClusteringInformation(ConnectedComponent cc){
		String cmPath = cc.getCcPath();
		int no_of_clusters = cc.getNumberOfClusters();
		int[] info = cc.getClusterInfo();
		double score = cc.getClusteringScore();
		int[] clusters = cc.getClusters();
		
		StringBuffer clusbuf = new StringBuffer();

		
		clusbuf.append("============ Clustering ============\n");
		clusbuf.append("cost matrix: ");
		clusbuf.append(cmPath);
		clusbuf.append("\nscore: ");
		clusbuf.append(score);
		clusbuf.append("\nno of clusters: ");
		clusbuf.append(no_of_clusters);
		clusbuf.append("\ncluster magnitudes: ");
		for (int inf = 0; inf < info.length; inf++) {
			clusbuf.append(info[inf]);
			clusbuf.append("  ");
		}
		clusbuf.append("\n====================================");
		System.out.println(clusbuf.toString());
	}
	
	/**
	 * Removes any Vectors of size 0 and turns the vector form into array form describing
	 * the cluster number for each object (object no = index no, cluster no = array value).
	 * @param clusterObject Vector form of clusters.
	 * @return clusters array.
	 */
	public static int[] clusterObjectToIntArray(Vector<Vector<Integer>> clusterObject, int size){
		int[] clusters = new int[size];
		for (int i = 0; i < clusterObject.size(); i++) {
			Vector<Integer> cluster = clusterObject.get(i);
			if(cluster.size() == 0){
				System.out.println("cluster size ZERO");
				clusterObject.remove(cluster);
			}
		}
		for(int i=0; i<clusterObject.size();i++){
			Vector<Integer> cluster = clusterObject.get(i);
			for (int j = 0; j < cluster.size(); j++) {
				int object = Integer.valueOf(cluster.get(j));
				clusters[object] = i;
			}
		}
		
		return clusters;
	}
	
	public static boolean compareIntArrayToClusterObject(Vector<Vector<Integer>> clusterObject, int[] clusters){
		boolean equal = true;
		for(int i=0; i<clusterObject.size();i++){
			Vector<Integer> cluster = clusterObject.get(i);
			for (int j = 0; j < cluster.size(); j++) {
				int object = Integer.valueOf(cluster.get(j));
				
				if(clusters[object] != i){
					System.out.println("cluster for object "+object +": "+ clusters[object]+"\t"+i);
					equal = false;
				}
			}
		}
		return equal;
	}

}
