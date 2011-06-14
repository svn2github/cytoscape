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
package org.cytoscape.webservice.ncbi.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;

/**
 * Simple attribute import GUI for Entrez Gene database
 * This UI depends on
 */
public class NCBIGenePanel extends AttributeImportPanel {
	
	private static final long serialVersionUID = -7433578094886775930L;
	
	private static final Icon LOGO = new ImageIcon(NCBIGenePanel.class.getResource("/images/entrez_page_title.gif"));

	private DefaultListModel model;

	private JComboBox attrList;


	public NCBIGenePanel(final CyTableManager tblManager, final CyNetworkManager netManager, String attrPanelName) throws IOException {
		super(tblManager, netManager, LOGO, "NCBI Client", attrPanelName);
		initDataSources();
		this.setPreferredSize(new Dimension(550, 480));
	}

	private void initDataSources() {
		this.databaseComboBox.addItem("NCBI Entrez Gene");
		databaseComboBox.setEnabled(false);
		setDataType();
	}

	private void setDataType() {
		this.attributeTypeComboBox.addItem("Entrez Gene ID");
		attributeTypeComboBox.setEnabled(false);
		
		buildList();
	}

	protected void importButtonActionPerformed(ActionEvent e) {
		importAttributes();
	}
	
	protected void resetButtonActionPerformed(ActionEvent e) {
		buildList();
	}
	
	private void buildList() {
//		model = new DefaultListModel();
//		this.attributeComboBox.setModel((ComboBoxModel) model);
//		for(AnnotationCategory dispAttrName : AnnotationCategory.values()) {
//			model.addElement(dispAttrName.getName());
//		}
	}


	@Override
	protected void databaseComboBoxActionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void importAttributes() {
		
	}

}
