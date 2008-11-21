
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

package cytoscape.filter.view;

import cytoscape.filter.model.Filter;
import cytoscape.filter.model.FilterEditorManager;
import cytoscape.filter.model.FilterManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 */
public class CreateFilterDialog extends JDialog {
	FilterEditorManager filterManager;
	JList filterEditorList;
	JTextField nameField;

	/**
	 * Creates a new CreateFilterDialog object.
	 *
	 * @param filterManager  DOCUMENT ME!
	 */
	public CreateFilterDialog(FilterEditorManager filterManager) {
		this.filterManager = filterManager;
		initializeDialog();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void initializeDialog() {
		setModal(true);
		setTitle("Filter Creation Dialog");
		getContentPane().setLayout(new BorderLayout());
		filterEditorList = new JList();

		Vector listItems = new Vector();

		for (Iterator editorIt = filterManager.getEditors(); editorIt.hasNext();) {
			listItems.add(editorIt.next());
		}

		filterEditorList = new JList(listItems);

		JPanel westPanel = new JPanel(new BorderLayout());
		westPanel.add(new JScrollPane(filterEditorList), BorderLayout.CENTER);
		westPanel.setBorder(new TitledBorder("Filter types"));
		getContentPane().add(westPanel, BorderLayout.WEST);

		//westPanel.setPreferredSize(new Dimension(100,150));
		DescriptionPanel descriptionPanel = new DescriptionPanel(filterEditorList);
		filterEditorList.addListSelectionListener(descriptionPanel);
		getContentPane().add(descriptionPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		JButton goButton = new JButton("OK");
		//getContentPane().add(goButton,BorderLayout.SOUTH);
		goButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					//first get the selected editor type
					FilterEditor selectedEditor = (FilterEditor) filterEditorList.getSelectedValue();
					Filter newFilter = selectedEditor.createDefaultFilter();
					FilterManager.defaultManager().addFilter(newFilter);
					CreateFilterDialog.this.setVisible(false);

					//NodeTopologyFilterEditor is a special case, since there is only one instance for each editor,
					//and the model for the combobox in NodeTopologyFilterEditor must keep update if more than one filter
					//is created
					if (selectedEditor.getClass().getName()
					                  .equals("cytoscape.filter.cytoscape.NodeTopologyFilterEditor")) {
						((cytoscape.filter.cytoscape.NodeTopologyFilterEditor) filterEditorList
						                                                                                                  .getSelectedValue())
						.resetFilterBoxModel();
					}
				}
			});
		southPanel.add(goButton);
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		pack();
		filterEditorList.setSelectedIndex(0);
	}

	class DescriptionPanel extends JPanel implements ListSelectionListener {
		JTextArea descriptionField;

		public DescriptionPanel(JList filterEditorList) {
			filterEditorList.addListSelectionListener(this);
			descriptionField = new JTextArea();
			descriptionField.setLineWrap(true);
			descriptionField.setWrapStyleWord(true);
			descriptionField.setBackground(getBackground());
			setLayout(new BorderLayout());
			add(descriptionField, BorderLayout.CENTER);
			setBorder(new TitledBorder("Filter Type Description"));
			setPreferredSize(new Dimension(200, 150));
		}

		public void valueChanged(ListSelectionEvent e) {
			JList filterEditorList = (JList) e.getSource();
			FilterEditor currentEditor = (FilterEditor) filterEditorList.getSelectedValue();
			descriptionField.setText(currentEditor.getDescription());
		}
	}
}
