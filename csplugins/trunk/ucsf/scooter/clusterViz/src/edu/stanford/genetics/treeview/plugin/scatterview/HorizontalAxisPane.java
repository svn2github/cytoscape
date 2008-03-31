/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: HorizontalAxisPane.java,v $
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
package edu.stanford.genetics.treeview.plugin.scatterview;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * a class that plots a vertical axis given an axis info and a scatter color set.
 */
class HorizontalAxisPane extends JPanel  {
	
	private AxisInfo axisInfo;
	/** Setter for axisInfo */
	public void setAxisInfo(AxisInfo axisInfo) {
		this.axisInfo = axisInfo;
		titleLabel.setText(axisInfo.getTitle());
		System.out.println("setting label text to " + axisInfo.getTitle());
		titleLabel.invalidate();
		titleLabel.revalidate();
	}
	/** Getter for axisInfo */
	public AxisInfo getAxisInfo() {
		return axisInfo;
	}
	private ScatterColorSet colorSet;
	/** Setter for colorSet */
	public void setColorSet(ScatterColorSet colorSet) {
		this.colorSet = colorSet;
	}
	/** Getter for colorSet */
	public ScatterColorSet getColorSet() {
		return colorSet;
	}
	JLabel titleLabel = new JLabel();
	/**
	* You'll want to create this after you set the config node for the scatterPane, since it keeps
	* it's own pointers to the axis info and color set.
	*/
	HorizontalAxisPane(AxisInfo axisInfo, ScatterColorSet colorSet) {
		setAxisInfo(axisInfo);
		setColorSet(colorSet);
		add(titleLabel);
	}
	
	public void paintComponent(Graphics g) {
		titleLabel.setForeground(colorSet.getColor("Axis"));
	    titleLabel.setBackground(colorSet.getColor("Background"));
		Dimension size = getSize();
	    g.setColor(colorSet.getColor("Background"));
	    g.fillRect(0,0,size.width, size.height);
	    g.setColor(colorSet.getColor("Axis"));
		g.drawLine(0, size.height/2, size.width, size.height/2);
	}
}

