
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.work.tunable.impl.gui;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.tunable.*;

import java.awt.*;
import java.awt.event.*;

import java.lang.reflect.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 * DOCUMENT ME!
  */
public class StringHandler implements GuiHandler, ActionListener {
	Field f;
	Object o;
	Tunable t;
	JTextField tf;

	/**
	 * Creates a new StringHandler object.
	 *
	 * @param f  DOCUMENT ME!
	 * @param o  DOCUMENT ME!
	 * @param t  DOCUMENT ME!
	 */
	public StringHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		this.tf = null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JPanel getJPanel() {
		JPanel ret = new JPanel();

		try {
			ret.add(new JLabel(t.description()));
			tf = new JTextField((String) f.get(o), 20);
			tf.addActionListener(this);
			ret.add(tf);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ae DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent ae) {
		handle();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void handle() {
		if (tf == null)
			return;

		String s = tf.getText();

		try {
			if (s != null)
				f.set(o, s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
