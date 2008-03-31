/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: ColorPanel.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/12/21 03:28:13 $
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
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
/**
 *  encapsulates a panel which can be used to edit a single color within a color
 *  set.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version    @version $Revision: 1.4 $ $Date: 2004/12/21 03:28:13 $
 */
public class ColorPanel extends JPanel {
	ColorIcon colorIcon;
	int type;
	ColorSetI colorSet;


	/**
	 * @param  i       the type of color panel. This is the type as defined in the <code>ColorSetI</code>, and is how this panel knows what color it is supposed to represent.
	 * @param  colorS  the <code>ColorSetI</code> which this panel represents and modifies.
	 */
	public ColorPanel(int i, ColorSetI colorS) {
		colorSet = colorS;
		type = i;
		redoComps();
	}


	/**  refresh the color of the icon from the <code>ColorSetI</code> */
	public void redoColor() {
		colorIcon.setColor(getColor());
	}


	/**  Remake the UI components */
	public void redoComps() {
		removeAll();
		colorIcon = new ColorIcon(10, 10, getColor());
		JButton pushButton  = new JButton(getLabel(), colorIcon);
		pushButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color trial  = JColorChooser.showDialog(ColorPanel.this, "Pick Color for " + getLabel(), getColor());
					if (trial != null) {
						setColor(trial);
					}
				}
			});

		add(pushButton);
	}


	private void setColor(Color c) {
		colorSet.setColor(type, c);
		colorIcon.setColor(getColor());
		repaint();
	}


	private String getLabel() {
		return colorSet.getType(type);
	}


	private Color getColor() {
		return colorSet.getColor(type);
	}
}


