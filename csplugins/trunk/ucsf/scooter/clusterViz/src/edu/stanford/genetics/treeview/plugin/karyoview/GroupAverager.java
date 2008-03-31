/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: GroupAverager.java,v $
* $Revision: 1.2 $
* $Date: 2007/02/03 07:26:48 $
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

import edu.stanford.genetics.treeview.*;

/**
* encapsulates routines common to averagers of groups of things
*/
abstract class GroupAverager extends Averager {
	/**
	* the sole method to be implemented by subclasses, which things to average?
	*/
	abstract protected ChromosomeLocus [] getContributors(ChromosomeLocus locus);
	abstract protected String getPre();
	public String[] getDescription(ChromosomeLocus locus, int col) {
		HeaderInfo geneInfo = karyoView.getGeneInfo();
		if (locus == null) {
			message[0] = "Locus is null";
			message[1] = "";
		} else {
			ChromosomeLocus []cont = getContributors(locus);
			message[0] = getPre() + " locus " + geneInfo.getHeader(locus.getCdtIndex(), "YORF");
			message[1] = "Mean of " + cont.length + " loci: " + summarizeYorf(cont);
		}
		return message;
	}
	
	private String summarizeYorf(ChromosomeLocus [] loci) {
		HeaderInfo geneInfo = karyoView.getGeneInfo();
		if (loci.length == 0) {
			return "No loci contribute";
		}
		try {
			if (loci.length < 101 ) {
				String ret = geneInfo.getHeader(loci[0].getCdtIndex(), "YORF");
				for(int i = 1; i < loci.length; i++) {
					if (loci[i] != null) {
						ret += ", " + geneInfo.getHeader(loci[i].getCdtIndex(), "YORF");
					}
				}
				return ret;
			} else {
				return "more than 100 loci contribute";
			}
		} catch (Exception e) {
			LogBuffer.println("failed to summarize loci in GroupAverager!: " + e.toString());
			return "failed to summarize loci!: " + e.toString();
		}
	}
	
	public double getValue(ChromosomeLocus locus, int col) {
		if (locus == null) return 0.0;
		int row = locus.getCdtIndex();
		ChromosomeLocus [] loci = getContributors(locus);
		double sum = 0.0;
		int n = 0;
		DataMatrix dataMatrix = karyoView.getDataMatrix();
		int numCol = karyoView.getNumCol();
		for (int i = 0; i < loci.length; i++) {
			if (loci[i] == null) continue;
			int index = loci[i].getCdtIndex();
			if (index == -1) continue;

			sum += dataMatrix.getValue(col,  index);
			n++;
		}
		return sum/n;
	}
	protected boolean isNodata(ChromosomeLocus locus) {
		DataMatrix dataMatrix = karyoView.getDataMatrix();
		int numCol = karyoView.getNumCol();
		try {
			double value = dataMatrix.getValue( karyoView.getCurrentCol() , locus.getCdtIndex());
			return (karyoView.getNodata() == value);
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
// 			LogPanel.println("GroupAverager.isNoData() got cdt index " + locus.getCdtIndex() + " out of bounds ");
			return true;
		}
	}
	protected double getDist(ChromosomeLocus first, ChromosomeLocus second) {
		int firstArm = first.getArm();
		int secondArm = second.getArm();
		if (firstArm == secondArm) {
			return Math.abs(first.getPosition() - second.getPosition());
		} else {
			return Math.abs(first.getPosition() + second.getPosition());
		}
	}
	protected double square(double pos) {
		return pos * pos;
	}
	
}


