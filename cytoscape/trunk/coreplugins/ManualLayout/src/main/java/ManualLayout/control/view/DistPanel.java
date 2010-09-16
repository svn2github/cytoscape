
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package ManualLayout.control.view;

import ManualLayout.control.actions.dist.*;

import cytoscape.view.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;


/**
 *
 */
public class DistPanel extends JPanel {
	/**
	 * Creates a new DistPanel object.
	 */
	public DistPanel() {
		ImageIcon hali = new ImageIcon(getClass().getResource("/H_DIST_LEFT.gif"));
		ImageIcon haci = new ImageIcon(getClass().getResource("/H_DIST_CENTER.gif"));
		ImageIcon hari = new ImageIcon(getClass().getResource("/H_DIST_RIGHT.gif"));
		ImageIcon vati = new ImageIcon(getClass().getResource("/V_DIST_TOP.gif"));
		ImageIcon vaci = new ImageIcon(getClass().getResource("/V_DIST_CENTER.gif"));
		ImageIcon vabi = new ImageIcon(getClass().getResource("/V_DIST_BOTTOM.gif"));

		HDistLeft hal = new HDistLeft(hali);
		HDistCenter hac = new HDistCenter(haci);
		HDistRight har = new HDistRight(hari);

		VDistTop vat = new VDistTop(vati);
		VDistCenter vac = new VDistCenter(vaci);
		VDistBottom vab = new VDistBottom(vabi);

		setLayout(new java.awt.GridLayout(1,6));

		add(createJButton(hal, "Horizontal Left"));
		add(createJButton(hac, "Horizontal Center"));
		add(createJButton(har, "Horizontal Right"));
		add(createJButton(vat, "Vertical Top"));
		add(createJButton(vac, "Vertical Center"));
		add(createJButton(vab, "Vertical Bottom"));

		setBorder(new TitledBorder("Distribute"));
	}

	protected JButton createJButton(Action a, String tt) {
		JButton b = new JButton(a);
		b.setToolTipText(tt);
		b.setPreferredSize(new Dimension(27, 18));
		b.setMaximumSize(new Dimension(27, 18));
		b.setBorder(BorderFactory.createEmptyBorder());
		b.setBorderPainted(false);
		b.setOpaque(false);
		b.setContentAreaFilled(false);

		return b;
	}
}
