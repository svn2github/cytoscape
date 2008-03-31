/*
 *  BEGIN_HEADER                                              Java TreeView
 *
 *  $Author: alokito $
 *  $RCSfile: ProgressTrackable.java,v $
 *  $Revision: 1.2 $
 *  $Date: 2007/02/03 05:20:09 $
 *  $Name:  $
 *
 *  This file is part of Java TreeView
 *  Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 *  This software is provided under the GNU GPL Version 2. In particular,
 *
 *  1) If you modify a source file, make a comment in it containing your name and the date.
 *  2) If you distribute a modified version, you must do it under the GPL 2.
 *  3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 *  A full copy of the license can be found in gpl.txt or online at
 *  http://www.gnu.org/licenses/gpl.txt
 *
 *  END_HEADER
 */
 
 package edu.stanford.genetics.treeview;

/**
 * The idea here is that objects which implement this interface can have their progress depicted
 *by a progress bar. 
 *
 * Typically, classes that implement this interface will use a LoadProgress2 
 * instance to actually display their progress, and will manage the "phases"
 * portion of their behavior. However, they will pass off a pointer to themselves,
 * cast to ProgressTrackable, to various routines within the phase that will
 * then update the within-phase scrollbar.
 *
 * The purpose of this interface is to allow these slow running subroutines to 
 * communicate back to the object that manages the phases of the long running 
 * task, which in turn manages the LoadProgress instance.
 *
 * @author     aloksaldanha
 */
public interface ProgressTrackable {

	/**
	* The length holds the length in bytes of the input stream, or -1 if not known.
	*/
	/** Getter for length */
	public int getLength();
	
	/**
	 * Enables classes to which we delegate loading
	 * to update the length of the progress bar
	 * 
	 * @param i
	 */
	public void setLength(int i);
	
	/**
	* The value holds the current position, which will be between 0 and length, if length is >= 0.
	*/
	/** Getter for value */
	public int getValue();
	/**
	 * sets value of scrollbar
	 * @param i
	 */
	public void setValue(int i);
	/**
	 * increments scrollbar by fixed amount
	 * @param i
	 */
	public void incrValue(int i);

	/**
	 * 
	 * @return true if this task has been cancelled.
	 */
	public boolean getCanceled();

}
