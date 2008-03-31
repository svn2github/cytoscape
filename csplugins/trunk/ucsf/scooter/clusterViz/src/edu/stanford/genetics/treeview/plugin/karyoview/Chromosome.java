/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: Chromosome.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:49 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview.plugin.karyoview;

import edu.stanford.genetics.treeview.LogBuffer;

/**
* represents the loci in just one chromosome...
*/
abstract class Chromosome {
	final public static int LINEAR = 1;
	final public static int CIRCULAR = 1;
	
	abstract public ChromosomeLocus getLeftEnd();
	abstract public ChromosomeLocus getRightEnd();

	public boolean isEmpty() {
		if (getLeftEnd() == getRightEnd() && getRightEnd() == null) {
			return true;
		} else {
			return false;
		}
	}
	abstract public int getType();
	
	abstract public double getMaxPosition();
	abstract public double getMaxPosition(int arm);
	abstract public ChromosomeLocus getClosestLocus(int arm, double position);
	abstract public ChromosomeLocus getLocus(int arm, int index);
	abstract public void insertLocus(ChromosomeLocus locus);
	/**
  * this internal routine is used to insert a locus into an array, maintaining the property that a
  * locus with minimal position is at index 0, and that there is a non-decreasing position as the
  * indexes increase. The array may include null values.
  *
  * @return the index inserted into or -1 on failure to insert.
  */
  protected int insertLocusIntoArray(ChromosomeLocus [] array, ChromosomeLocus locus) {
//	System.out.println("Inserting " + locus.toString());
	for (int point = 0; point < array.length; point++) {
//	  System.out.print("Checking " + point + "... ");
	  if (array[point] == null) {
		// easy case, insert and return.
//	  System.out.println("inserting");
		
		array[point] = locus;
 		return point;
	  }
//	  System.out.print("Found " + array[point].toString());
	  if (array[point].getPosition() > locus.getPosition()) {
//		System.out.println(", decided to backtrack ");
		// we need to push everyone up...
		for (int j = array.length-1; j > point; j--) {
///		  System.out.println("moving " + (j-1) + " to " + j);
		  array[j] = array[j-1];
		}
		array[point] = locus;
		return point;
	  }
//		System.out.println(", decided to continue ");
	}
	System.out.println(" array " + array  ); 
	LogBuffer.println("Error in Genome.insertLocusIntoArray(): we weren't about to fit locus " + locus + " into data structure on account of not allocating enough space");
	return -1;
  }

  /**
  * just bisect and recurse. Bottoms out when min == max....
  */
  
  protected ChromosomeLocus getLocusRecursive(double position, ChromosomeLocus [] array, int min, int max) {
//	System.out.println("Recursing " + min +" , " + max + " for " + position);
	if (min  == max) {
	//  System.out.println("bottomed out at "+min+"... ");
	  return array[min]; // bottom out
	}
	int midL = (max + min) /2; // rightmost member of left interval
	int midR = midL + 1;       // leftmost member of right interval.
	if (array[midL].getPosition() > position) { // we're on the left for sure...
	  return getLocusRecursive(position, array, min, midL);
	}
	if (array[midR].getPosition() < position) { // we're on the right for sure...
	  return getLocusRecursive(position, array, midR, max);
	}
//	  System.out.println("struck gold at "+midL+"... ");
	// we've struck gold! We're between the midL and midR!
	double distL = Math.abs(array[midL].getPosition() - position); 
	double distR = Math.abs(array[midR].getPosition() - position); 
	if (distL > distR) {
	  return array[midR];
	} else  {
	  return array[midL];
	}
  }
}


