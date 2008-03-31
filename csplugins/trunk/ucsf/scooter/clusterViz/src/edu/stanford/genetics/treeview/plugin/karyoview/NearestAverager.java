/* BEGIN_HEADER                                              Java TreeView
*
* $Author: rqluk $
* $RCSfile: NearestAverager.java,v $
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
  * average k-nearest neighbors of point. If two points are equally close, an
  * unspecified one will be picked.
  */
  class NearestAverager extends GroupAverager {
	  int defaultNum = 5;
	  /** Setter for num */
	  public void setNum(int num) {
		  getConfigNode().setAttribute("num", num, defaultNum);
	  }
	  /** Getter for num */
	  public int getNum() {
		  return getConfigNode().getAttribute("num", defaultNum);
	  }
	  public NearestAverager() {
		  super();
	  }
	  public NearestAverager(int n) {
		  setNum(n);
	  }
	  public int getType() {
		  return Averager.NEAREST;
	  }
	  public String getArg() {
		  return "" + getNum();
	  }

	  protected String getPre() {
		  return "Nearest " + getNum() + " of ";
	  }
	  protected ChromosomeLocus [] getContributors(ChromosomeLocus locus) {
		  ChromosomeLocus [] ret = new ChromosomeLocus[getNum()];
		  ChromosomeLocus leftCand  = locus.getLeft();
		  ChromosomeLocus rightCand = locus.getRight();
		  int num = getNum();
		  int ncand = 0;
		  if (isNodata(locus) == false) {
//			  System.out.println("adding self " + locus);
			  ret[ncand++] = locus;
		  }
		  // the leftCand != rightCand works for both linear and circular chromosomes,
		  // since with a circle they will wrap around, and with a linear they will both
		  // eventually become null.
		  while ((ncand < num) && (leftCand != rightCand)) {
			  // make sure leftCand is viable...
			  while ((leftCand != null) && isNodata(leftCand)) {
				  leftCand = leftCand.getLeft();
			  }
			  
			  // make sure rightCand is viable...
			  while ((rightCand != null) && isNodata(rightCand)) {
				  rightCand = rightCand.getRight();
			  }
			  
			  if ((leftCand == null) && (rightCand != null)) {
				  ret[ncand++] = rightCand;
				  rightCand = rightCand.getRight();
			  }
			  
			  if ((leftCand != null) && (rightCand == null)) {
				  ret[ncand++] = leftCand;
				  leftCand = leftCand.getLeft();
			  }
			  
			  if ((leftCand != null) && (rightCand != null)) {
				  double leftDist =  getDist(leftCand, locus);
				  double rightDist = getDist(rightCand, locus);
				  if (leftDist < rightDist) {
//					  System.out.println("adding left " + leftCand);
					  ret[ncand++] = leftCand;
					  leftCand = leftCand.getLeft();
					  if (leftCand != null) leftDist =  getDist(leftCand, locus);
				  } else {
//					  System.out.println("adding right " + rightCand);
					  ret[ncand++] = rightCand;
					  rightCand = rightCand.getRight();
					  if (rightCand != null) rightDist = getDist(rightCand, locus);
				  }
			  }
		  }
		  return ret;
	  }
	  
  }
  
