/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: CircularChromosome.java,v $
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


class CircularChromosome extends Chromosome {
	ChromosomeLocus [] circularArm;
	CircularChromosome(int nCircular) {
		circularArm = new ChromosomeLocus[nCircular];
	}
	public void insertLocus(ChromosomeLocus locus) {
		if (locus.getArm() == ChromosomeLocus.CIRCULAR) {
			insertLocusIntoArray(circularArm, locus);
		}
	}
	public double getMaxPosition() {
		return circularArm[circularArm.length - 1].getPosition();
	}
	public double getMaxPosition(int arm) {
		if (arm == ChromosomeLocus.CIRCULAR) {
			return getMaxPosition();
		}
		return 0.0;
	}
	public int getType() {
		return Chromosome.CIRCULAR;
	}
	/**
	* returns locus at 0 min
	*/
	public ChromosomeLocus getLeftEnd() {
		return circularArm[0];
	}
	/**
	* returns locus at latest min
	*/
	public ChromosomeLocus getRightEnd() {
		return circularArm[circularArm.length - 1];
	}

	public ChromosomeLocus getClosestLocus(int arm, double position) {
		if (arm == ChromosomeLocus.CIRCULAR) {
			return getLocusRecursive(position, circularArm, 0, circularArm.length-1);
		}
		return null;
	}
	public ChromosomeLocus getLocus(int arm, int index) {
		if (arm == ChromosomeLocus.CIRCULAR) {
			return circularArm[index];
		}
		return null;
	}
}
