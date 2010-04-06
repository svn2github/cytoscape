/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package browser.ui;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;

import browser.DataObjectType;
import browser.DataTableModel;


public class FormulaBuilderDialog extends JDialog {
	private JButton OKButton;


	public FormulaBuilderDialog(final DataTableModel tableModel, final DataObjectType tableObjectType,
	                            final Frame parent)
	{
		super(parent);
		final FormulaBuilderPanel formulaBuilderPanel = new FormulaBuilderPanel(tableModel, tableObjectType);
		this.setTitle("Creating a formula for: " + tableObjectType.getDisplayName());
		this.setAlwaysOnTop(true);
		
		setLayout(new GridBagLayout());
		
		OKButton = new JButton("OK");
		OKButton.setPreferredSize(new Dimension(70, 20));

		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(formulaBuilderPanel, gridBagConstraints);

		pack();
		this.setModal(false);
		
		OKButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
	}
}
