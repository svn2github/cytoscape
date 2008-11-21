
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

package cytoscape.layout.manual.control.view;

import cytoscape.layout.manual.control.actions.stack.HStackBottom;
import cytoscape.layout.manual.control.actions.stack.HStackCenter;
import cytoscape.layout.manual.control.actions.stack.HStackTop;
import cytoscape.layout.manual.control.actions.stack.VStackCenter;
import cytoscape.layout.manual.control.actions.stack.VStackLeft;
import cytoscape.layout.manual.control.actions.stack.VStackRight;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


/**
 *
 */
public class StackPanel extends JPanel {
	/**
	 * Creates a new StackPanel object.
	 */
	public StackPanel() {

		ImageIcon vali = new ImageIcon(getClass().getResource("/V_STACK_LEFT.gif"));
		ImageIcon vaci = new ImageIcon(getClass().getResource("/V_STACK_CENTER.gif"));
		ImageIcon vari = new ImageIcon(getClass().getResource("/V_STACK_RIGHT.gif"));
		ImageIcon hati = new ImageIcon(getClass().getResource("/H_STACK_TOP.gif"));
		ImageIcon haci = new ImageIcon(getClass().getResource("/H_STACK_CENTER.gif"));
		ImageIcon habi = new ImageIcon(getClass().getResource("/H_STACK_BOTTOM.gif"));

		VStackLeft val = new VStackLeft(vali);
		VStackCenter vac = new VStackCenter(vaci);
		VStackRight var = new VStackRight(vari);

		HStackTop hat = new HStackTop(hati);
		HStackCenter hac = new HStackCenter(haci);
		HStackBottom hab = new HStackBottom(habi);

		setLayout(new GridLayout(1,6,0,0));
		add(createJButton(val, "Vertical Left"));
		add(createJButton(vac, "Vertical Center"));
		add(createJButton(var, "Vertical Right"));
		add(createJButton(hat, "Horizontal Top"));
		add(createJButton(hac, "Horizontal Center"));
		add(createJButton(hab, "Horizontal Bottom"));

		setBorder(new TitledBorder("Stack"));

		System.out.println("StackPanel()");
	}

	protected JButton createJButton(Action a, String tt) {
		JButton b = new JButton(a);
		b.setToolTipText(tt);
		b.setPreferredSize(new Dimension(27, 18));
		b.setMaximumSize(new Dimension(27, 18));
		b.setMinimumSize(new Dimension(27, 18));
		b.setBorder(BorderFactory.createEmptyBorder());
		b.setBorderPainted(false);
		b.setOpaque(false);
		b.setContentAreaFilled(false);

		return b;
	}
}
