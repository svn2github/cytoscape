/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: LinearChromosome.java,v $
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


class LinearChromosome extends Chromosome {
	ChromosomeLocus [] leftArm;
	ChromosomeLocus [] rightArm;
	LinearChromosome(int nLeft, int nRight) {
//		System.out.println("Linear chromosome (" + nLeft + ", " + nRight + ")");
		leftArm = new ChromosomeLocus[nLeft];
		rightArm = new ChromosomeLocus[nRight];
	}
	public void insertLocus(ChromosomeLocus locus) {
		ChromosomeLocus rightLocus = null;
		ChromosomeLocus leftLocus = null;
		if (locus.getArm() == ChromosomeLocus.LEFT) {
			int point = insertLocusIntoArray(leftArm, locus);
			if (point == -1) {
				System.out.println("could not fit locus on right arm which has length " + rightArm.length);
			} else {
				// find right...
				if (point == 0) {
					rightLocus = (rightArm.length == 0)? null: rightArm[0];
				} else {
					rightLocus = leftArm[point-1];
				}
				
				//find left ...
				if (point == leftArm.length-1) {
					leftLocus = null;
				} else {
					leftLocus = leftArm[point+1];
				}
			}
		} else if (locus.getArm() == ChromosomeLocus.RIGHT) {
			int point = insertLocusIntoArray(rightArm, locus);
			if (point == -1) {
				System.out.println("could not fit locus on right arm which has length " + rightArm.length + " which contains ");
				for (int i = 0; i < rightArm.length; i++) {
					System.out.println(rightArm[i].toString());
				}
				
			} else {
				// find left...
				if (point == 0) {
					leftLocus = (leftArm.length == 0)? null : leftArm[0];
				} else {
					leftLocus = rightArm[point-1];
				}
				
				//find right ...
				if (point == rightArm.length-1) {
					rightLocus = null;
				} else {
					rightLocus = rightArm[point+1];
				}
			}
		}
		locus.setLeft(leftLocus);
		locus.setRight(rightLocus);
		if (leftLocus != null) leftLocus.setRight(locus);
		if (rightLocus != null) rightLocus.setLeft(locus);

	}
	public double getMaxPosition() {
		double leftMax = (leftArm.length == 0)? 0: 
			leftArm[leftArm.length - 1].getPosition();
		double rightMax = (rightArm.length == 0)? 0: 
			rightArm[rightArm.length - 1].getPosition();
		return (leftMax > rightMax) ? leftMax : rightMax;
	}
	public double getMaxPosition(int arm) {
		ChromosomeLocus end = null;
		if (arm == ChromosomeLocus.LEFT) {
			end = getLeftEnd();
		} else if (arm == ChromosomeLocus.RIGHT) {
			end = getRightEnd();
		}
		return (end == null) ? 0.0 : end.getPosition();
	}
	public ChromosomeLocus getClosestLocus(int arm, double position) {
		if (arm == ChromosomeLocus.LEFT) {
			return getLocusRecursive(position, leftArm, 0, leftArm.length-1);
		} else if (arm == ChromosomeLocus.RIGHT) {
			return getLocusRecursive(position, rightArm, 0, rightArm.length-1);
		}
		return null;
	}
	public int getType() {
		return Chromosome.LINEAR;
	}
	public ChromosomeLocus getLeftEnd() {
		if (leftArm.length != 0) {
			return leftArm[leftArm.length - 1];
		}
		if (rightArm.length != 0) {
			return rightArm[0];
		}
		return null;
	}
	public ChromosomeLocus getRightEnd() {
		if (rightArm.length != 0) {
			return rightArm[rightArm.length - 1];
		}
		if (leftArm.length != 0) {
			return leftArm[0];
		}
		return null;
	}
	public ChromosomeLocus getLocus(int arm, int index) {
		if (arm == ChromosomeLocus.LEFT) {
			return leftArm[index];
		} else if (arm == ChromosomeLocus.RIGHT) {
			return rightArm[index];
		}
		return null;
	}
}
