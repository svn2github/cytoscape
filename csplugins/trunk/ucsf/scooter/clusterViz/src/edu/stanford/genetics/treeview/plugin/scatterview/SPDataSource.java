/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: SPDataSource.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:48 $
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
package edu.stanford.genetics.treeview.plugin.scatterview;

import edu.stanford.genetics.treeview.*;

/**
 * a class to define the kind of data I want to make scatter plots with
 */
public interface SPDataSource {
	// general layout.
    public String getTitle();
    public String getXLabel();
    public String getYLabel();

	// information about points
    public int getNumPoints();
    public double getX(int i) throws NoValueException;
    public double getY(int i) throws NoValueException;
    public java.awt.Color getColor(int i);
	public boolean isSelected(int i);
	public String getLabel(int i);

	// stuff with selection.
    public void select(int i);
    public void select(double xL, double yL, double xU, double yU);
    public void deselectAll();
}

