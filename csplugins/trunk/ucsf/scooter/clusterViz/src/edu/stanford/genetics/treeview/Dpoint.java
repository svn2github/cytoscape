/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: Dpoint.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/12/21 03:28:14 $
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
package edu.stanford.genetics.treeview;


/**
 *  Class to represent pair of doubles
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version $Revision: 1.4 $ $Date: 2004/12/21 03:28:14 $
 */
public class Dpoint {
	//point who's location is stored in doubles...
	/**
	 *  actual pair of doubles
	 */
	protected double x, y;


	/**
	 *  Constructor for the Dpoint object
	 *
	 * @param  dx  first val in pair
	 * @param  dy  second val in pair
	 */
	public Dpoint(double dx, double dy) {
		x = dx;
		y = dy;
	}


	/**
	 *  Gets the x attribute of the Dpoint object
	 *
	 * @return    The x value
	 */
	public double getX() {
		return x;
	}


	/**
	 *  Gets the y attribute of the Dpoint object
	 *
	 * @return    The y value
	 */
	public double getY() {
		return y;
	}


	/**
	 *  Sometimes we want to scale the x and take the int part
	 *
	 * @param  s  multiplicative scaling factor
	 * @return    int part of product
	 */
	public int scaledX(double s) {
		return (int) (x * s);
	}


	/**
	 *  Sometimes we want to scale the y and take the int part
	 *
	 * @param  s  multiplicative scaling factor
	 * @return    int part of product
	 */
	public int scaledY(double s) {
		return (int) (y * s);
	}


	/**
	 *  Gets the dist to another point
	 *
	 * @param  dp  the other point
	 * @return     The distance to dp
	 */
	public double getDist(Dpoint dp) {
	double dx  = x - dp.getX();
	double dy  = y - dp.getY();
		return dx * dx + dy * dy;
	}
}

