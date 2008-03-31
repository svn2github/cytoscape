package clusterExplorerPlugin;

import java.util.Comparator;


public class FloatComparator implements Comparator<Float> {
	
	private boolean order;
	
	public FloatComparator(boolean order) { // order = true for highest first
		this.order = order;
	}
	
	public int compare(Float i0, Float i1) {
		
		int mul = 1;
		if (!this.order) {
			mul = -1;
		}
		
		if ((i0-i1) >= 0) {
			return 1*mul;
		} else {
			return -1*mul;
		}
	}

}
