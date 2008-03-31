/*
 * Created on 18. December 2007
 * 
 */
package de.layclust.layout.postprocessing;

import java.util.Comparator;
import java.util.Vector;


/**
 * This class compares two Vectors  according to their sizes.
 * @author sita
 *
 */
public class ClusterObjectComparator implements Comparator {

//	@SuppressWarnings("unchecked")
	public int compare(Object o1, Object o2) {
		
		int sizeV1 = ((Vector) o1).size();
		int sizeV2 = ((Vector) o2).size();
		
		if(sizeV1<sizeV2) return -1; 
		else if(sizeV1>sizeV2) return 1;
		else return 0;
	}
}
