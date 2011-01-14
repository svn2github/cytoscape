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
package org.cytoscape.io.webservice.biomart.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

/**
 * General GUI component for importing attributes.<br>
 * Maybe used by Web Service Clients to import attributes.
 * 
 * This UI accepts title and icon. Usually, those are from source database.
 * 
 * TODO: is this the right place for this class?
 * 
 */
public abstract class AttributeImportPanel extends JPanel {

	private static final long serialVersionUID = 8665197023334496167L;

	/**
	 * Will be caught by parent object (usually a dialog.)
	 */
	public static final String CLOSE_EVENT = "CLOSE";

	// Default title of this panel.
	private static final String DEF_TITLE = "Attribute Import Utility";

	// Labels for the sub-panels.
	private static final String DATASOURCE = "Data Source";
	private static final String KEY_ATTR = "Key Attribute in Cytoscape";
	private static final String ATTR_PANEL_TITLE = "Available Annotations";

	// Title of the panel.
	protected String panelTitle;

	// Icon for this panel title.
	protected Icon logo;

	// Attribute panel border title
	protected String attributePanelTitle;

	protected AttributeImportPanel() {
		this(null, DEF_TITLE, ATTR_PANEL_TITLE);
	}

	protected AttributeImportPanel(Icon logo, String title,
			String attrPanelTitle) {
		this.logo = logo;
		this.panelTitle = title;
		this.attributePanelTitle = attrPanelTitle;

		initComponents();
	}

	private void initComponents() {
		attrList = new CheckBoxJList();
		model = new DefaultListModel();
		attrList.setModel(model);

		titleLabel = new javax.swing.JLabel();
		databasePanel = new javax.swing.JPanel();
		databaseComboBox = new javax.swing.JComboBox();
		attributePanel = new javax.swing.JPanel();
		attributeLabel = new javax.swing.JLabel();
		attributeComboBox = new javax.swing.JComboBox();
		attributeTypeLabel = new javax.swing.JLabel();
		attributeTypeComboBox = new javax.swing.JComboBox();
		availableAttrPanel = new javax.swing.JPanel();
		availableAttrScrollPane = new javax.swing.JScrollPane();
		attrListPanel = new javax.swing.JPanel();
		importButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		resetButton = new javax.swing.JButton();

		setBackground(new java.awt.Color(255, 255, 255));
		titleLabel.setBackground(new java.awt.Color(255, 255, 255));
		titleLabel.setIcon(logo);
		titleLabel.setText(panelTitle);

		databasePanel.setBackground(new java.awt.Color(255, 255, 255));
		databasePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(DATASOURCE));

		attributeComboBox.setBackground(Color.white);

		databaseComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				databaseComboBoxActionPerformed(evt);
			}
		});
		databaseComboBox.setBackground(Color.white);

		GroupLayout databasePanelLayout = new GroupLayout(databasePanel);
		databasePanel.setLayout(databasePanelLayout);
		databasePanelLayout.setHorizontalGroup(databasePanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						databasePanelLayout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(databaseComboBox, 0, 350,
										Short.MAX_VALUE).addContainerGap()));
		databasePanelLayout.setVerticalGroup(databasePanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						databasePanelLayout
								.createSequentialGroup()
								.addComponent(databaseComboBox,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(14, Short.MAX_VALUE)));

		attributePanel.setBackground(new java.awt.Color(255, 255, 255));
		attributePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(KEY_ATTR));
		attributeLabel.setText("Attribute:");

		attributeTypeLabel.setText("Data Type:");

		attributeTypeComboBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						attributeTypeComboBoxActionPerformed(evt);
					}
				});
		attributeTypeComboBox.setBackground(Color.white);

		GroupLayout attributePanelLayout = new GroupLayout(attributePanel);
		attributePanel.setLayout(attributePanelLayout);
		attributePanelLayout
				.setHorizontalGroup(attributePanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								attributePanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												attributePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																attributeLabel)
														.addComponent(
																attributeTypeLabel))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												attributePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																attributeTypeComboBox,
																0, 350,
																Short.MAX_VALUE)
														.addComponent(
																attributeComboBox,
																0, 350,
																Short.MAX_VALUE))
										.addContainerGap()));
		attributePanelLayout
				.setVerticalGroup(attributePanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								attributePanelLayout
										.createSequentialGroup()
										.addGroup(
												attributePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																attributeLabel)
														.addComponent(
																attributeComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												attributePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																attributeTypeLabel)
														.addComponent(
																attributeTypeComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(13, Short.MAX_VALUE)));

		availableAttrPanel.setBackground(new java.awt.Color(255, 255, 255));
		availableAttrPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(attributePanelTitle));
		availableAttrScrollPane
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		availableAttrScrollPane.setViewportView(attrList);

		GroupLayout availableAttrPanelLayout = new GroupLayout(
				availableAttrPanel);
		availableAttrPanel.setLayout(availableAttrPanelLayout);
		availableAttrPanelLayout.setHorizontalGroup(availableAttrPanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						availableAttrPanelLayout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(availableAttrScrollPane,
										GroupLayout.DEFAULT_SIZE, 350,
										Short.MAX_VALUE).addContainerGap()));
		availableAttrPanelLayout.setVerticalGroup(availableAttrPanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						GroupLayout.Alignment.TRAILING,
						availableAttrPanelLayout
								.createSequentialGroup()
								.addComponent(availableAttrScrollPane,
										GroupLayout.DEFAULT_SIZE, 362,
										Short.MAX_VALUE).addContainerGap()));

		importButton.setText("Import");
		importButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				importButtonActionPerformed(evt);
			}
		});
		importButton.setBackground(Color.white);

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		cancelButton.setBackground(Color.white);

		resetButton.setText("Reset");
		resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetButtonActionPerformed(evt);
			}
		});
		resetButton.setBackground(Color.white);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.TRAILING)
												.addComponent(
														availableAttrPanel,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														attributePanel,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														databasePanel,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(titleLabel)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		resetButton)
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED,
																		343,
																		Short.MAX_VALUE)
																.addComponent(
																		cancelButton)
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		importButton)))
								.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(titleLabel)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(databasePanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(attributePanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(availableAttrPanel,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
												.addComponent(importButton)
												.addComponent(cancelButton)
												.addComponent(resetButton))
								.addContainerGap()));
	} // </editor-fold>

	protected void resetButtonActionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
	}

	protected void importButtonActionPerformed(ActionEvent evt) {
		importAttributes();
	}

	protected void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// Close parent
	}

	private void attributeTypeComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	abstract protected void databaseComboBoxActionPerformed(
			java.awt.event.ActionEvent evt);

	protected abstract void importAttributes();

	
	protected void addAttribute(final String attributeName) {
		if (attributeName == null)
			return;

		final SortedSet<String> attrNameSet = new TreeSet<String>();
		attrNameSet.add(attributeName);
		for (int i = 0; i < model.getSize(); i++)
			attrNameSet.add(model.get(i).toString());

		attributeComboBox.removeAllItems();

		for (String name : attrNameSet)
			attributeComboBox.addItem(name);
	}

	protected void removeAttribute(final String attributeName) {
		if (attributeName != null) {
			attributeComboBox.removeItem(attributeName);
			return;
		}
	}

	// Swing components. Maybe accessed from child classes.
	protected javax.swing.JComboBox attributeComboBox;
	protected javax.swing.JLabel attributeLabel;
	protected javax.swing.JPanel attributePanel;
	protected javax.swing.JComboBox attributeTypeComboBox;
	protected javax.swing.JLabel attributeTypeLabel;
	protected javax.swing.JButton cancelButton;
	protected javax.swing.JComboBox databaseComboBox;
	protected javax.swing.JPanel databasePanel;
	protected javax.swing.JPanel attrListPanel;
	protected javax.swing.JPanel availableAttrPanel;
	protected javax.swing.JScrollPane availableAttrScrollPane;
	protected javax.swing.JButton importButton;
	protected javax.swing.JLabel titleLabel;
	protected javax.swing.JButton resetButton;
	protected CheckBoxJList attrList;

	protected DefaultListModel model;
}
