/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: ChromosomeLocus.java,v $
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
* The purpose of this class is purely to hold information about the location of
* various loci. It does not hold any exporession data directly, although
* it hold a unique integer, the cdtIndex, which ought to be used to look up any values
* in a table.
*/
class ChromosomeLocus {
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int CIRCULAR = 3;

	private int chromosome; // you should follow organism convention here
	private int arm; //currently, must be linearized genome
	private double position; // distance from centromere. you do the math
	private int cdtIndex; // row index of cdt corresponding to locus.
	private ChromosomeLocus leftLocus = null; // left neightbor or null
	private ChromosomeLocus rightLocus = null; // right neightbor or null
	
	
	// full getters and setters...
	public int  getChromosome() {return chromosome;}
	public int         getArm() {return arm;}
	public double getPosition() {return position;}
	public int    getCdtIndex() {return cdtIndex;}
	public ChromosomeLocus getLeft() {return leftLocus;}
	public ChromosomeLocus getRight() {return rightLocus;}
	
	public void setChromosome(int chr) {this.chromosome = chr;}
	public void        setArm(int arm) {this.arm        = arm;}
	public void   setPosition(double pos) {this.position   = pos;}
	public void   setCdtIndex(int idx) {this.cdtIndex = idx;}
	public void setLeft(ChromosomeLocus left) {leftLocus = left;}
	public void setRight(ChromosomeLocus right) {rightLocus = right;}

	ChromosomeLocus(int chr, int arm, double pos, int index) {
		this.chromosome = chr;
		this.arm = arm;
		this.position = pos;
		this.cdtIndex = index;
	}
	
	public String getText(int arm) {
	  if (arm == LEFT) {
		return "Left";
	  } else {
		return "Right";
	  }
	}
	
	public String toString() {
	  return "Chr " + chromosome + ", arm " + getText(arm) + ", pos " + position + ", row in cdt " + cdtIndex;
	}
	
}
