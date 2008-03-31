package clusterExplorerPlugin;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


public class Clusters {
	
	
	private Mapping map;
	private Sim sim;
	
	private Vector<Cluster> clusters = new Vector<Cluster>();
	private Hashtable<Integer,Cluster> clusterForGivenElementNumber = new Hashtable<Integer,Cluster>();
	private Hashtable<String,Cluster> clusterForGivenClusterID = new Hashtable<String,Cluster>();
	
	
	public Clusters(Mapping map, Sim sim) {
		this.map = map;
		this.sim = sim;
	}
	
	public Mapping getMapping() {
		return this.map;
	}
	
	public Sim getSim() {
		return this.sim;
	}
	
	public void addCluster(Cluster cluster) {
		
		clusters.add(cluster);
		for (Iterator<Integer> iterator = cluster.getElements().iterator(); iterator.hasNext();) {
			Integer v = iterator.next();
			clusterForGivenElementNumber.put(v, cluster);
		}
		
		this.clusterForGivenClusterID.put(cluster.getID(), cluster);
		
	}
	
	public Cluster getClusterOfElement(int i) {
		return clusterForGivenElementNumber.get(i);
	}
	
	public Cluster getClusterOfElement(String iID) {
		int i = this.map.getNumber(iID);
		return getClusterOfElement(i);
	}
	
	public Cluster getClusterByClusterID(String clusterID) {
		return this.clusterForGivenClusterID.get(clusterID);
	}
	
	public Vector<Cluster> getClusters() {
		return this.clusters;
	}
	
	public int size() {
		return this.clusters.size();
	}
	
	
	public Vector<ClusterElementSimilarity> getOrderedForeignClusterSimilarityListForElement(String id, boolean order) {
		int elementNumber = this.map.getNumber(id);
		return getOrderedForeignClusterSimilarityListForElement(elementNumber, order);
	}
	
	public Vector<ClusterElementSimilarity> getOrderedForeignClusterSimilarityListForElement(int elementNumber, boolean order) {
		
		Vector<ClusterElementSimilarity> sims = new Vector<ClusterElementSimilarity>();
		
		Cluster sourceCluster = this.getClusterOfElement(elementNumber);
		
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster targetCluster = this.clusters.get(i);
			if (!targetCluster.getID().equalsIgnoreCase(sourceCluster.getID())) {
				
				float sum = 0;
				Vector<Integer> elements = targetCluster.getElements();
				for (int j = 0; j < elements.size(); j++) {
					int target = elements.get(j);
					sum = sum + this.sim.get(elementNumber, target);
				}
				
				ClusterElementSimilarity ces = new ClusterElementSimilarity();
				ces.cluster = targetCluster;
				ces.element = elementNumber;
				ces.sim = sum / (elements.size());
				
				sims.add(ces);
			}
		}
		
		Collections.sort(sims, new SimilarityComparator(order));
		
		return sims;
	}
	
	public Vector<ElementElementSimilarity> getOrderedElementSimilarityListForElement(String id, boolean order) {
		int elementNumber = this.map.getNumber(id);
		return getOrderedElementSimilarityListForElement(elementNumber, order);
	}
	
	public Vector<ElementElementSimilarity> getOrderedElementSimilarityListForElement(int elementNumber, boolean order) {
		
		Vector<ElementElementSimilarity> sims = new Vector<ElementElementSimilarity>();
		
		int elementA = elementNumber;
		Cluster clusterA = this.getClusterOfElement(elementNumber);
		
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster clusterB = this.clusters.get(i);
			Vector<Integer> elements = clusterB.getElements();
			for (int j = 0; j < elements.size(); j++) {
				int elementB = elements.get(j);
				if (elementA != elementB) {
					ElementElementSimilarity ces = new ElementElementSimilarity();
					ces.sourceCluster = clusterA;
					ces.sourceElement = elementA;
					ces.targetCluster = clusterB;
					ces.targetElement = elementB;
					ces.sim = this.sim.get(elementA, elementB);
					sims.add(ces);
				}
			}
			
		}
		
		Collections.sort(sims, new SimilarityComparator(order));
		
		return sims;
	}
	
	public Vector<ClusterElementSimilarity> getOrderedForeignElementSimilarityListForCluster(String clusterID, boolean order) {
		
		Cluster c = this.getClusterByClusterID(clusterID);
		
		return getOrderedForeignElementSimilarityListForCluster(c, order);
	}

	public Vector<ClusterElementSimilarity> getOrderedForeignElementSimilarityListForCluster(Cluster sourceCluster, boolean order) {
		
		Vector<ClusterElementSimilarity> sims = new Vector<ClusterElementSimilarity>();
		
		Vector<Integer> sourceElements = sourceCluster.getElements();
		
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster targetCluster = this.clusters.get(i);
			if (!targetCluster.getID().equalsIgnoreCase(sourceCluster.getID())) {
				
				Vector<Integer> targetElements = targetCluster.getElements();
				for (int j = 0; j < targetElements.size(); j++) {
					int target = targetElements.get(j);
					
					float sum = 0;
					for (int k = 0; k < sourceElements.size(); k++) {
						int source = sourceElements.get(k);
						sum = sum + this.sim.get(source, target);
					}
					
					ClusterElementSimilarity ces = new ClusterElementSimilarity();
					ces.cluster = targetCluster;
					ces.element = target;
					ces.sim = sum / (sourceElements.size());
					
					sims.add(ces);
					
				}
				
			}
			
		}
		
		Collections.sort(sims, new SimilarityComparator(order));
		
		return sims;
	}
	
	
	public Vector<ClusterClusterSimilarity> getOrderedClusterSimilarityListForCluster(String clusterID, boolean order) {
		
		Cluster c = this.getClusterByClusterID(clusterID);
		
		return getOrderedClusterSimilarityListForCluster(c, order);
	}

	public Vector<ClusterClusterSimilarity> getOrderedClusterSimilarityListForCluster(Cluster sourceCluster, boolean order) {
		
		Vector<ClusterClusterSimilarity> sims = new Vector<ClusterClusterSimilarity>();
		
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster targetCluster = this.clusters.get(i);
			if (!targetCluster.getID().equalsIgnoreCase(sourceCluster.getID())) {
				
				ClusterClusterSimilarity ccs = new ClusterClusterSimilarity();
				ccs.sourceCluster = sourceCluster;
				ccs.targetCluster = targetCluster;
				ccs.sim = getInterClusterSimilarity(sourceCluster, targetCluster);
				
				sims.add(ccs);
			}
		}
		
		Collections.sort(sims, new SimilarityComparator(order));
		
		return sims;
	}
	
	public float getInterClusterSimilarity(String clusterID1, String clusterID2) {
		Cluster c1 = this.getClusterByClusterID(clusterID1);
		Cluster c2 = this.getClusterByClusterID(clusterID2);
		
		return getInterClusterSimilarity(c1, c2);
	}
	
	public float getInterClusterSimilarity(Cluster sourceCluster, Cluster targetCluster) {
		
		Vector<Integer> sourceElements = sourceCluster.getElements();
		Vector<Integer> targetElements = targetCluster.getElements();
		
		float sum = 0;
		
		for (int i = 0; i < sourceElements.size(); i++) {
			int source = sourceElements.get(i);
			for (int j = 0; j < targetElements.size(); j++) {
				int target = targetElements.get(j);
				sum = sum + this.sim.get(source, target);	
			}
		}
		
		int num = sourceElements.size() * targetElements.size();
		
		return sum / num;
	}
	
	public Vector<Float> getIntraClusterSimilarities() {
		
		Vector<Float> sims = new Vector<Float>(); 
		
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster c = this.clusters.get(i);
			sims.addAll(c.getIntraClusterSimilarities());
		}
		
		return sims;
	}
	
	public Vector<Float> getInterClusterSimilarities() {
		
		Vector<Float> sims = new Vector<Float>(); 
		
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster c1 = this.clusters.get(i);
			for (int j = 0; j < i; j++) {
				Cluster c2 = this.clusters.get(j);
				
				sims.addAll(Cluster.getInterClusterSimilarities(c1, c2, this));
				
			}
			
		}
		
		return sims;
	}
	
	public Vector<Float> getClusterSizes() {
		
		Vector<Float> sizes = new Vector<Float>(); 
		
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster c = this.clusters.get(i);
			
			sizes.add((float) c.size());
			
		}
		
		return sizes;
	}
	
	
	

}







































