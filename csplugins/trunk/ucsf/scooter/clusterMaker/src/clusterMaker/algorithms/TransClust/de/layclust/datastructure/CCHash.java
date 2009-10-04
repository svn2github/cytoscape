/*
 * Created on 25. September 2007
 * 
 */

package de.layclust.datastructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import de.layclust.taskmanaging.TaskConfig;

/**
 * This class is a realisation of the ICCEdges interface. It creates a HashMap
 * with the nodes i and j of each edge as keys and the cost for adding or
 * deleting this edge as values. Since the edges are undirected, (i,j) is equal
 * to (i,j) is equal to (j,i), so only one entry exist for both (also i!=j). So the 
 * keys look like this i#j and i>j (not i<j, so as not to get confused with 
 * CCSymmetricArray.java).
 * 
 * 
 * IDEA: use an integer for the key, which looks like this: node1 + size*node2
 * 
 * @author Sita Lange
 */
public class CCHash implements ICCEdges {

	/* edgeCostHash is where the edge costs are saved as values */
	/* to save redundant storage, i>=j and the key is a string "i#j" */
	private HashMap<String, Float> edgeCostHash;

	public CCHash(int size) {
		initCCEdges(size);
	}

	/**
	 * Here the data structure for the symmetric array is initialised, but costs
	 * still need to be added.
	 * 
	 * @param size
	 *            The number of nodes in the component.
	 */
	public void initCCEdges(int size) {
		/* optimal initial capacity > max no. of entries / load factor */
		/* default load factor is 0.75 */
		double minInitialCapacity = size / 0.75;
		System.out.println("=== size:"+size+" ===");
		/* 10 more than the minimal - is that number suitable? */
		/* note that all edges with minimal costs are not saved, so the no. of entries is <= size */
		int initialCapacity = Math.round((float) minInitialCapacity) + 10;
		System.out.println("=== initial capacity:"+initialCapacity+" ===");
		edgeCostHash = new HashMap<String, Float>(initialCapacity);
	}

	/**
	 * Sets the cost for deleting the edge between node i and node j.
	 * 
	 * @param node_i
	 *            The value of the first edge node.
	 * @param node_j
	 *            The value of the seciond edge node.
	 * @param cost
	 *            The cost of adding or deleting the edge (i,j).
	 */
	public void setEdgeCost(int node_i, int node_j, float cost) {

		/* if the cost is the minimum, then it is ignored and not saved */
		/* this means if an edge is not in the hash, it has the minimum cost */
		/* this is to save space!*/
		if (cost > TaskConfig.cost_minimum) {

			/* if i>j stay in that order, otherwise swap the values for */
			/* symmetry. Ignore the case i==j. */
			if (node_i > node_j) {
				edgeCostHash.put(node_i+"#"+node_j, Float.valueOf(cost));
			} else if (node_i < node_j){
				edgeCostHash.put(node_j+"#"+node_i, Float.valueOf(cost));
			}
			/* TODO take this else out, just for testing purposes */
		} else if (cost < TaskConfig.cost_minimum) {
			System.out.println("ERROR: the cost is lower than the minimum "
					+ "cost and this is not allowed to happen!");
		}
	}

	/**
	 * Gets the cost for adding or deleting the edge (i,j).
	 * 
	 * @param node_i
	 *            The value of the first edge node.
	 * @param node_j
	 *            The value of the seciond edge node.
	 * @return cost The cost of adding or deleting the edge (i,j).
	 */
	public float getEdgeCost(int node_i, int node_j) {
		
		Float cost;
		
		/* if i>j stay in that order, otherwise swap the values for */
		/* symmetry */
		if (node_i > node_j) {			
			cost = edgeCostHash.get(node_i+"#"+node_j);
		} else if (node_i < node_j){
			
			cost = edgeCostHash.get(node_j+"#"+node_i);
		} else {
			System.out.println("ERROR: Edge i==j, does not exist!!");
			return 0;
			} //this is when i==j, shouldn't happen
		if (cost == null){
			return TaskConfig.cost_minimum;
		}
		return cost.floatValue();
	}
	
	/**
	 * Normalises the values between 0 and 1.
	 */
	public void normalise() {
		//find min / max: 
		if(edgeCostHash.size() < 1) return;
		float min = this.getEdgeCost(0, 1);
		float max = min;
		Set<String> keySet = edgeCostHash.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			float x = edgeCostHash.get(element);
			if(x > max) max = x;
			if(x < min) min = x;
		}
		float range = max - min;
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			float x = edgeCostHash.get(element);
			x = (x - min) / range;
			edgeCostHash.put(element, x);
		}
	}
	
	/**
	 * Normalises the values between -1 and 1.
	 */
	public void normaliseWithThreshold(double alpha) {
		//find min / max: 
		if(edgeCostHash.size() < 1) return;
		float min = this.getEdgeCost(0, 1);
		float max = min;
		Set<String> keySet = edgeCostHash.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			float x = edgeCostHash.get(element);
			if(x > max) max = x;
			if(x < min) min = x;
		}
		//range of the normalised values: l & r
		double l, r;
		if(Math.abs(min) < Math.abs(max)) {
			l = -alpha * Math.abs(min) / max;
			r = 1;
		} else {
			l = -1;
			r = alpha * max / Math.abs(min);
		}
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			float x = edgeCostHash.get(element);
			if(x > 0) {
				x = (float) r * x / max;
			} else {
				x = (float) l * x / min;
			}
			edgeCostHash.put(element, x);
		}
	}
	
	/**
	 * Undo the normalisation done by normalise()
	 */
	public void denormalise() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Undo the normalisation done by normaliseWithThreshold
	 */
	public void denormaliseWithThreshold() {
		// TODO Auto-generated method stub
		
	}
	
	
}