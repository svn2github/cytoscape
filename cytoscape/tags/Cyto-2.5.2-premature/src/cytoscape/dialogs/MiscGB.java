/*
 File: MiscGB.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */


package cytoscape.dialogs;


import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * We DO need to replace this with swing-layout!
 * Miscellaneous static GridBagLayout utilities
 */
public class MiscGB {
	// sets GridBagConstraints.
	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 * @param padx DOCUMENT ME!
	 * @param pady DOCUMENT ME!
	 */
	public static void pad(GridBagConstraints c, int padx, int pady) {
		c.ipadx = padx;
		c.ipady = pady;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 * @param b DOCUMENT ME!
	 * @param l DOCUMENT ME!
	 * @param r DOCUMENT ME!
	 * @param t DOCUMENT ME!
	 */
	public static void inset(GridBagConstraints c, int b, int l, int r, int t) {
		c.insets = new Insets(b, l, r, t);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 * @param s DOCUMENT ME!
	 */
	public static void inset(GridBagConstraints c, int s) {
		inset(c, s, s, s, s);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 * @param w DOCUMENT ME!
	 * @param h DOCUMENT ME!
	 * @param weightx DOCUMENT ME!
	 * @param weighty DOCUMENT ME!
	 * @param f DOCUMENT ME!
	 */
	public static void set(GridBagConstraints c, int x, int y, int w, int h, int weightx,
	                       int weighty, int f) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = w;
		c.gridheight = h;
		c.weightx = weightx;
		c.weighty = weighty;
		c.fill = f;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 * @param w DOCUMENT ME!
	 * @param h DOCUMENT ME!
	 */
	public static void set(GridBagConstraints c, int x, int y, int w, int h) {
		set(c, x, y, w, h, 0, 0, GridBagConstraints.NONE);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public static void set(GridBagConstraints c, int x, int y) {
		set(c, x, y, 1, 1, 0, 0, GridBagConstraints.NONE);
	}

	// inserts a component into a panel with a GridBagLayout.
	/**
	 *  DOCUMENT ME!
	 *
	 * @param panel DOCUMENT ME!
	 * @param comp DOCUMENT ME!
	 * @param bag DOCUMENT ME!
	 * @param c DOCUMENT ME!
	 */
	public static void insert(JPanel panel, Component comp, GridBagLayout bag, GridBagConstraints c) {
		if (bag == null)
			System.out.println("bag is null");

		if (comp == null)
			System.out.println("comp is null");

		if (c == null)
			System.out.println("c is null");

		if (panel == null)
			System.out.println("panel is null");

		bag.setConstraints(comp, c);
		panel.add(comp);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param gbg DOCUMENT ME!
	 * @param comp DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public static void insert(GridBagGroup gbg, Component comp, int x, int y) {
		set(gbg.constraints, x, y);
		insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param gbg DOCUMENT ME!
	 * @param comp DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 * @param w DOCUMENT ME!
	 * @param h DOCUMENT ME!
	 */
	public static void insert(GridBagGroup gbg, Component comp, int x, int y, int w, int h) {
		set(gbg.constraints, x, y, w, h);
		insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param gbg DOCUMENT ME!
	 * @param comp DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 * @param w DOCUMENT ME!
	 * @param h DOCUMENT ME!
	 * @param f DOCUMENT ME!
	 */
	public static void insert(GridBagGroup gbg, Component comp, int x, int y, int w, int h, int f) {
		set(gbg.constraints, x, y, w, h, 0, 0, f);
		insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param gbg DOCUMENT ME!
	 * @param comp DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 * @param w DOCUMENT ME!
	 * @param h DOCUMENT ME!
	 * @param weightx DOCUMENT ME!
	 * @param weighty DOCUMENT ME!
	 * @param f DOCUMENT ME!
	 */
	public static void insert(GridBagGroup gbg, Component comp, int x, int y, int w, int h,
	                          int weightx, int weighty, int f) {
		set(gbg.constraints, x, y, w, h, weightx, weighty, f);
		insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static JLabel createColorLabel(Color c) {
		JLabel label = new JLabel("    ");
		label.setOpaque(true);
		label.setBackground(c);

		return label;
	}
}
