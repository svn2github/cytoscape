
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

package edu.ucsd.bioeng.idekerlab.biomartui.ui;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.swing.CheckBoxJList;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;


/**
 *
 */
public abstract class AttributeImportPanel extends JPanel {
	protected String panelTitle;
	protected Icon logo;

	protected AttributeImportPanel() {
		this(null, null);
	}

	protected AttributeImportPanel(Icon logo, String title) {
		this.logo = logo;
		this.panelTitle = title;

		initComponents();
		setAttributes();
	}

	protected void initComponents() {
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
		idSetPanel = new javax.swing.JPanel();
		idSetScrollPane = new javax.swing.JScrollPane();
		idListPanel = new javax.swing.JPanel();
		importButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		resetButton = new javax.swing.JButton();

		setBackground(new java.awt.Color(255, 255, 255));
		titleLabel.setBackground(new java.awt.Color(255, 255, 255));
		titleLabel.setIcon(logo);
		titleLabel.setText(panelTitle);

		databasePanel.setBackground(new java.awt.Color(255, 255, 255));
		databasePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Source Database"));

		databaseComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					databaseComboBoxActionPerformed(evt);
				}
			});

		org.jdesktop.layout.GroupLayout databasePanelLayout = new org.jdesktop.layout.GroupLayout(databasePanel);
		databasePanel.setLayout(databasePanelLayout);
		databasePanelLayout.setHorizontalGroup(databasePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                          .add(databasePanelLayout.createSequentialGroup()
		                                                                                  .addContainerGap()
		                                                                                  .add(databaseComboBox,
		                                                                                       0,
		                                                                                       534,
		                                                                                       Short.MAX_VALUE)
		                                                                                  .addContainerGap()));
		databasePanelLayout.setVerticalGroup(databasePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                        .add(databasePanelLayout.createSequentialGroup()
		                                                                                .add(databaseComboBox,
		                                                                                     org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                     org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                                .addContainerGap(14,
		                                                                                                 Short.MAX_VALUE)));

		attributePanel.setBackground(new java.awt.Color(255, 255, 255));
		attributePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapping Attribute"));
		attributeLabel.setText("Attribute:");

		attributeTypeLabel.setText("Data Type:");

		attributeTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					attributeTypeComboBoxActionPerformed(evt);
				}
			});

		org.jdesktop.layout.GroupLayout attributePanelLayout = new org.jdesktop.layout.GroupLayout(attributePanel);
		attributePanel.setLayout(attributePanelLayout);
		attributePanelLayout.setHorizontalGroup(attributePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                            .add(attributePanelLayout.createSequentialGroup()
		                                                                                     .addContainerGap()
		                                                                                     .add(attributePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                                                                              .add(attributeLabel)
		                                                                                                              .add(attributeTypeLabel))
		                                                                                     .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                                     .add(attributePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                                                                              .add(attributeTypeComboBox,
		                                                                                                                   0,
		                                                                                                                   456,
		                                                                                                                   Short.MAX_VALUE)
		                                                                                                              .add(attributeComboBox,
		                                                                                                                   0,
		                                                                                                                   456,
		                                                                                                                   Short.MAX_VALUE))
		                                                                                     .addContainerGap()));
		attributePanelLayout.setVerticalGroup(attributePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                          .add(attributePanelLayout.createSequentialGroup()
		                                                                                   .add(attributePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                                                            .add(attributeLabel)
		                                                                                                            .add(attributeComboBox,
		                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                                                 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                                                                   .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                                   .add(attributePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                                                            .add(attributeTypeLabel)
		                                                                                                            .add(attributeTypeComboBox,
		                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                                                 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                                 org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                                                                   .addContainerGap(13,
		                                                                                                    Short.MAX_VALUE)));

		idSetPanel.setBackground(new java.awt.Color(255, 255, 255));
		idSetPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Available ID Sets"));
		idSetScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//        idListPanel.setBackground(new java.awt.Color(255, 255, 255));
		//        org.jdesktop.layout.GroupLayout idListPanelLayout = new org.jdesktop.layout.GroupLayout(idListPanel);
		//        idListPanel.setLayout(idListPanelLayout);
		//        idListPanelLayout.setHorizontalGroup(
		//            idListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		//            .add(0, 532, Short.MAX_VALUE)
		//        );
		//        idListPanelLayout.setVerticalGroup(
		//            idListPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		//            .add(0, 375, Short.MAX_VALUE)
		//        );
		idSetScrollPane.setViewportView(attrList);

		org.jdesktop.layout.GroupLayout idSetPanelLayout = new org.jdesktop.layout.GroupLayout(idSetPanel);
		idSetPanel.setLayout(idSetPanelLayout);
		idSetPanelLayout.setHorizontalGroup(idSetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                    .add(idSetPanelLayout.createSequentialGroup()
		                                                                         .addContainerGap()
		                                                                         .add(idSetScrollPane,
		                                                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                              534,
		                                                                              Short.MAX_VALUE)
		                                                                         .addContainerGap()));
		idSetPanelLayout.setVerticalGroup(idSetPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                  .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                                       idSetPanelLayout.createSequentialGroup()
		                                                                       .add(idSetScrollPane,
		                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                            362,
		                                                                            Short.MAX_VALUE)
		                                                                       .addContainerGap()));

		importButton.setText("Import");
		importButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				importButtonActionPerformed(evt);
			}
		});

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelButtonActionPerformed(evt);
				}
			});

		resetButton.setText("Reset");
		resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				resetButtonActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                     layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           idSetPanel,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           Short.MAX_VALUE)
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           attributePanel,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           Short.MAX_VALUE)
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           databasePanel,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           Short.MAX_VALUE)
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           titleLabel)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(resetButton)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
		                                                                                  343,
		                                                                                  Short.MAX_VALUE)
		                                                                 .add(cancelButton)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                 .add(importButton)))
		                                           .addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(titleLabel)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(databasePanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(attributePanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(idSetPanel,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              Short.MAX_VALUE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(importButton).add(cancelButton)
		                                                    .add(resetButton)).addContainerGap()));
	} // </editor-fold>

	protected void resetButtonActionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		
	}

	protected void importButtonActionPerformed(ActionEvent evt) {
		importAttributes();
	}

	protected void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.firePropertyChange("CLOSE", null, null);
	}

	private void attributeTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	abstract protected void databaseComboBoxActionPerformed(java.awt.event.ActionEvent evt);

	protected abstract void importAttributes();
	
	protected void setAttributes() {
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		final String[] names = nodeAttr.getAttributeNames();
		
		attributeComboBox.addItem("ID");
		for(String name: names) {
			if(nodeAttr.getUserVisible(name)) {
				attributeComboBox.addItem(name);
			}
		}
	}

	protected javax.swing.JComboBox attributeComboBox;
	protected javax.swing.JLabel attributeLabel;
	protected javax.swing.JPanel attributePanel;
	protected javax.swing.JComboBox attributeTypeComboBox;
	protected javax.swing.JLabel attributeTypeLabel;
	protected javax.swing.JButton cancelButton;
	protected javax.swing.JComboBox databaseComboBox;
	protected javax.swing.JPanel databasePanel;
	protected javax.swing.JPanel idListPanel;
	protected javax.swing.JPanel idSetPanel;
	protected javax.swing.JScrollPane idSetScrollPane;
	protected javax.swing.JButton importButton;
	protected javax.swing.JLabel titleLabel;
	protected javax.swing.JButton resetButton;
	protected CheckBoxJList attrList;
	protected DefaultListModel model;
}
