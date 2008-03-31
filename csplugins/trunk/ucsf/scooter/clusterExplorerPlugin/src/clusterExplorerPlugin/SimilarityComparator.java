package clusterExplorerPlugin;

import java.util.Comparator;


public class SimilarityComparator implements Comparator<Similarity> {
	
	private boolean order;
	
	public SimilarityComparator(boolean order) { // order = true for highest first
		this.order = order;
	}
	
	public int compare(Similarity i0, Similarity i1) {
		
		int mul = 1;
		if (!this.order) {
			mul = -1;
		}
		
		if ((i0.sim-i1.sim) <= 0) {
			return 1*mul;
		} else {
			return -1*mul;
		}
		
	}

}
