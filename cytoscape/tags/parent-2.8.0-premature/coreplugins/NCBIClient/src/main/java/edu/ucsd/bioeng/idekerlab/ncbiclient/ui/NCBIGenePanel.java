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
package edu.ucsd.bioeng.idekerlab.ncbiclient.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.swing.AttributeImportPanel;
import edu.ucsd.bioeng.idekerlab.ncbiclient.NCBIClient.AnnotationCategory;




/**
 * Simple attribute import GUI for Entrez Gene database
 * This UI depends on
 */
public class NCBIGenePanel extends AttributeImportPanel {
	protected static WebServiceClient ncbi = WebServiceClientManager.getClient("ncbi_entrez");
	private static final Icon LOGO = new ImageIcon(NCBIGenePanel.class.getResource("/images/entrez_page_title.gif"));
	
	/**
	 * Creates a new NCBIGenePanel object.
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public NCBIGenePanel() throws IOException {
		this(LOGO, "", "Available Annotation Category");
	}

	/**
	 * Creates a new NCBIGenePanel object.
	 *
	 * @param logo  DOCUMENT ME!
	 * @param title  DOCUMENT ME!
	 * @param attrPanelName  DOCUMENT ME!
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public NCBIGenePanel(Icon logo, String title, String attrPanelName) throws IOException {
		super(logo, title, attrPanelName);
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
		model = new DefaultListModel();
		attrList.setModel(model);
		for(AnnotationCategory dispAttrName : AnnotationCategory.values()) {
			model.addElement(dispAttrName.getName());
		}
	}

	

	@Override
	protected void databaseComboBoxActionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void importAttributes() {
		String keyInHeader = null;
		// Create Task
		final ImportAttributeTask task = new ImportAttributeTask(attributeComboBox.getSelectedItem().toString(), keyInHeader);

		// Configure JTask Dialog Pop-Up Box
		final JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
		
		this.firePropertyChange("CLOSE", null, null);
	}
	
	private class ImportAttributeTask implements Task {
		
		private String key;
		private String keyAttrName;
		private TaskMonitor taskMonitor;

		public ImportAttributeTask(String key, String keyAttrName) {	
			this.key = key;
			this.keyAttrName = keyAttrName;
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public String getTitle() {
			// TODO Auto-generated method stub
			return "Loading Attributes from Web Service";
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void halt() {
			Thread.currentThread().interrupt();		
			taskMonitor.setPercentCompleted(100);

			// Kill the import task.
			CyWebServiceEvent<String> cancelEvent = new CyWebServiceEvent<String>(ncbi.getClientID(), WSEventType.CANCEL,
                     null,
                     null);
			try {
				WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(cancelEvent);
			} catch (CyWebServiceException e) {
				// TODO Auto-generated catch block
				taskMonitor.setException(e, "Cancel Failed.");
			}
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void run() {
			taskMonitor.setStatus("Importing annotation from Entrez Gene.\n\nIt may take a while.\nPlease wait...");
			taskMonitor.setPercentCompleted(-1);

			final Object[] selectedAttr = attrList.getSelectedValues();
		
			AttributeImportQuery qObj = new AttributeImportQuery(selectedAttr, key, keyAttrName);

			try {
				WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(new CyWebServiceEvent("ncbi_entrez", WSEventType.IMPORT_ATTRIBUTE, qObj));
			} catch (Exception e) {
				taskMonitor.setException(e, "Could not load attributes from NCBI.");
			}

			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Attributes successfully loaded.");
			
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @param arg0 DOCUMENT ME!
		 *
		 * @throws IllegalThreadStateException DOCUMENT ME!
		 */
		public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
			this.taskMonitor = taskMonitor;
		}
	}
	
	
	
}
