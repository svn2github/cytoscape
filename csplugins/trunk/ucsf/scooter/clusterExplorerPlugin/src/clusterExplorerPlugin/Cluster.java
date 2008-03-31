package clusterExplorerPlugin;

import java.util.Collections;
import java.util.Vector;


public class Cluster {
	
	private Mapping map;
	private Sim sim;
	private Vector<Integer> elements = new Vector<Integer>();
	private String id;
	
	public Cluster(Mapping map, Sim sim, String id) {
		this.map = map;
		this.sim = sim;
		this.id = id;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public String getID() {
		return id;
	}
	
	public Mapping getMapping() {
		return this.map;
	}
	
	public Sim getSim() {
		return this.sim;
	}
	
	public void add(int i) {
		this.elements.add(i);
	}
	
	public void add(String iID) {
		int i = this.map.getNumber(iID);
		add(i);
	}
	
	public int size() {
		return this.elements.size();
	}
	
	public int get(int i) {
		return this.elements.get(i);
	}
	
	public int get(String iID) {
		int i = this.map.getNumber(iID);
		return get(i);
	}
	
	public Vector<Integer> getElements() {
		return this.elements;
	}
		
	public Vector<Integer> getCentralElements() {
		
		Vector<Integer> centrals = new Vector<Integer>();
		float best = Integer.MIN_VALUE;
		
		for (int i = 0; i < this.elements.size(); i++) {
			int source = this.elements.get(i);
			
			float sum = 0;
			for (int j = 0; j < this.elements.size(); j++) {
				if (i != j) {
					int target = this.elements.get(j);
					sum = sum + this.sim.get(source, target);
				}
			}
			
			if (sum == best) {
				centrals.add(i);
			} else if (sum > best) {
				centrals.clear();
				centrals.add(source);
				best = sum;
			}
		}
		
		return centrals;
	}
	
	public Vector<ClusterElementSimilarity> getOrderedCentralElementsList(boolean order) {
		
		Vector<ClusterElementSimilarity> centrals = new Vector<ClusterElementSimilarity>();
		
		for (int i = 0; i < this.elements.size(); i++) {
			int source = this.elements.get(i);
			
			float sum = 0;
			for (int j = 0; j < this.elements.size(); j++) {
				if (i != j) {
					int target = this.elements.get(j);
					sum = sum + this.sim.get(source, target);
				}
			}
			
			ClusterElementSimilarity ces = new ClusterElementSimilarity();
			ces.cluster = this;
			ces.element = source;
			ces.sim = sum / (this.elements.size()-1);
			
			centrals.add(ces);
		}
		
		Collections.sort(centrals, new SimilarityComparator(order));
		
		return centrals;
	}
	
	public Vector<Float> getIntraClusterSimilarities() {
		
		Vector<Float> sims = new Vector<Float>();
		
		for (int i = 0; i < this.elements.size(); i++) {
			int source = this.elements.get(i);
			
			for (int j = 0; j < i; j++) {
					int target = this.elements.get(j);
					sims.add(this.sim.get(source, target));
			}
		}
		
		return sims;
	}
	
	public static Vector<Float> getInterClusterSimilarities(Cluster c1, Cluster c2, Clusters cs) {
		
		Vector<Float> sims = new Vector<Float>();
		
		Vector<Integer> c1Elements = c1.getElements();
		Vector<Integer> c2Elements = c2.getElements();
		Sim sim = cs.getSim();
		
		for (int i = 0; i < c1Elements.size(); i++) {
			int source = c1Elements.get(i);
			for (int j = 0; j < c2Elements.size(); j++) {
				int target = c2Elements.get(j);
				
				sims.add(sim.get(source, target));
			}
		}
		
		return sims;
	}
	
	
}






















