
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

package cytoscape.browser.ui;

import cytoscape.browser.DataObjectType;
import cytoscape.browser.DataTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/*
 *  This panel will be added to JDialog for attribute modification
 *  Author: Peng-Liang wang
 *  Date: 9/29/2006
 */
/**
 *
 */
public class ModDialog extends JDialog {
	/**
	 * 
	 */
	public JButton OKButton;

	/**
	 * Creates a new AttrSelectModPanel object.
	 *
	 * @param dataTable  DOCUMENT ME!
	 * @param data  DOCUMENT ME!
	 * @param tableModel  DOCUMENT ME!
	 * @param tableObjectType  DOCUMENT ME!
	 */
	public ModDialog(final DataTableModel tableModel,
	                          final DataObjectType tableObjectType) {
		//SelectPanel selectionPanel = new SelectPanel(dataTable, tableObjectType);
		final ModPanel modPanel = new ModPanel(tableModel, tableObjectType);
		this.setTitle(tableObjectType.getDislayName() + " Attribute Batch Editor");
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
		add(modPanel, gridBagConstraints);


		pack();
		this.setModal(false);
		
		OKButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
	}
}
