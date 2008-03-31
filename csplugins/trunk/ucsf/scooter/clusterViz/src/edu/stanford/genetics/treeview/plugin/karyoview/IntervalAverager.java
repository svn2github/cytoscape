/* BEGIN_HEADER                                              Java TreeView
*
* $Author: rqluk $
* $RCSfile: IntervalAverager.java,v $
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



/**
* average all points which are closer than width/2 units away.
*/
class IntervalAverager extends GroupAverager {
	double defaultWidth = 1.0;
	/** Setter for width */
	public void setWidth(double width) {
		getConfigNode().setAttribute("width", width, defaultWidth);
	}
	/** Getter for width */
	public double getWidth() {
		return getConfigNode().getAttribute("width", defaultWidth);
	}
	
	public IntervalAverager() {
		super();
	}
	public IntervalAverager(double width) {
		super();
		setWidth(width);
	}
	
	
	public int getType() {
		return Averager.INTERVAL;
	}
	public String getArg() {
		return "" + getWidth();
	}
	
	protected String getPre() {
		return  "Interval size " + getWidth() + " units around ";
	}
	
	protected ChromosomeLocus [] getContributors(ChromosomeLocus locus) {
		ChromosomeLocus leftCand  = locus.getLeft();
		ChromosomeLocus rightCand = locus.getRight();
		int totalCand = 0;
		// first, calculate number of candidates...
		if (isNodata(locus) == false) {
			totalCand++;
		}
		double radius = getWidth()/2;
		while ((leftCand != null) && (getDist(leftCand, locus) < radius)) {
			if (isNodata(leftCand) == false) {
				totalCand++;
			}
			leftCand = leftCand.getLeft();
		}
		while ((rightCand != null) && (getDist(rightCand, locus) < radius)) {
			if (isNodata(rightCand) == false) {
				totalCand++;
			}
			rightCand = rightCand.getRight();
		}
		
		
		//okay, let's allocate...
		ChromosomeLocus [] ret = new ChromosomeLocus[totalCand];
		leftCand  = locus.getLeft();
		rightCand = locus.getRight();
		totalCand = 0;
		
		// populate...
		if (isNodata(locus) == false) {
			ret[totalCand++] = locus;
		}
		while ((leftCand != null) && (getDist(leftCand, locus) < radius)) {
			if (isNodata(leftCand) == false) {
				ret[totalCand++] = leftCand;
			}
			leftCand = leftCand.getLeft();
		}
		while ((rightCand != null) && (getDist(rightCand, locus) < radius)) {
			if (isNodata(rightCand) == false) {
				ret[totalCand++] = rightCand;
			}
			rightCand = rightCand.getRight();
		}
		return ret;
	}
}


