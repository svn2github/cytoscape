
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

package browser;

import cytoscape.data.CyAttributes;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;


/*
 *  This panel will be added to JDialog for attribute modification
 *  Author: Peng-Liang wang
 *  Date: 9/29/2006
 */
/**
 *
 */
public class AttrSelectModPanel extends JPanel {
	/**
	 * 
	 */
	public JButton btnOK = new JButton(" OK ");

	/**
	 * Creates a new AttrSelectModPanel object.
	 *
	 * @param dataTable  DOCUMENT ME!
	 * @param data  DOCUMENT ME!
	 * @param tableModel  DOCUMENT ME!
	 * @param tableObjectType  DOCUMENT ME!
	 */
	public AttrSelectModPanel(DataTable dataTable, CyAttributes data, DataTableModel tableModel,
	                          int tableObjectType) {
		SelectPanel selectionPanel = new SelectPanel(dataTable, tableObjectType);
		ModPanel modPanel = new ModPanel(data, tableModel, tableObjectType);

		setLayout(new java.awt.GridBagLayout());

		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(selectionPanel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(modPanel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
		add(btnOK, gridBagConstraints);

		btnOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JPanel thePanel = (JPanel) getParent();
					JLayeredPane theLayeredPanel = (JLayeredPane) thePanel.getParent();
					JRootPane theRootPanel = (JRootPane) theLayeredPanel.getParent();
					JDialog theDialog = (JDialog) theRootPanel.getParent();

					theDialog.dispose();
				}
			});
	}
}
