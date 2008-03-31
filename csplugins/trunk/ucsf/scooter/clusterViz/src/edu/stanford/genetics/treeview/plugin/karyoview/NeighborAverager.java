/* BEGIN_HEADER                                              Java TreeView
*
* $Author: rqluk $
* $RCSfile: NeighborAverager.java,v $
* $Revision: 1.1 $
* $Date: 2006/08/16 19:13:50 $
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
  * average by neighbors.
  * k must be an odd integer. Returns the average of 
  *  the locus,
  *  the (k-1)/2 loci on the left,
  *  and the (k-1)/2 loci on the right.
  */
  class NeighborAverager extends GroupAverager {
	  int defaultNum = 5;
	  /** Setter for num */
	  public void setNum(int num) {
		  if (num % 2 == 0) {
			  num++;
		  }
		  getConfigNode().setAttribute("num", num, defaultNum);
	  }
	  /** Getter for num */
	  public int getNum() {
		  return getConfigNode().getAttribute("num", defaultNum);
	  }

	  public NeighborAverager() {
		  super();
	  }
	  public NeighborAverager(int n) {
		  setNum(n);
	  }


	  public int getType() {
		  return Averager.NEIGHBOR;
	  }
	  public String getPre() {
		  return  getNum() + " Neighbors of ";
	  }
	  public String getArg() {
		  return "" + getNum();
	  }

	  protected ChromosomeLocus [] getContributors(ChromosomeLocus locus) {
		  int num = getNum();
		  ChromosomeLocus [] ret = new ChromosomeLocus[num];
		  ChromosomeLocus leftCand  = locus.getLeft();
		  ChromosomeLocus rightCand = locus.getRight();
		  int onAside = (num -1) /2;
		  int ncand = 0;
		  if (isNodata(locus) == false) {
//			  System.out.println("adding self " + locus);
			  ret[ncand++] = locus;
		  }
		  // populate left side...
		  int nOnLeft = 0;
		  while ((leftCand != null) && (nOnLeft < onAside)) {
			  // make sure leftCand is viable...
			  while ((leftCand != null) && isNodata(leftCand)) {
				  leftCand = leftCand.getLeft();
			  }
			  if (leftCand != null) {
				  ret[ncand++] = leftCand;
				  leftCand = leftCand.getLeft();
				  nOnLeft++;
			  }
		  }
		  
		  // populate right side...
		  int nOnRight = 0;
		  while ((rightCand != null) && (nOnRight < onAside)) {
			  // make sure rightCand is viable...
			  while ((rightCand != null) && isNodata(rightCand)) {
				  rightCand = rightCand.getRight();
			  }
			  if (rightCand != null) {
				  ret[ncand++] = rightCand;
				  rightCand = rightCand.getRight();
				  nOnRight++;
			  }
		  }
		  return ret;
	  }
  }
  
  
