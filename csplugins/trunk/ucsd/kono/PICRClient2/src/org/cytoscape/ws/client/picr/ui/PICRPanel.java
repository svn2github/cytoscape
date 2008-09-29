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
package org.cytoscape.ws.client.picr.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.Icon;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.CyWebServiceException.WSErrorCode;
import cytoscape.data.webservice.ui.WebServiceClientGUI;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.swing.AttributeImportPanel;
import ebi.picr.AccessionMapperInterface;


/**
 *
 */
public class PICRPanel extends AttributeImportPanel {
	protected static WebServiceClient<AccessionMapperInterface> picr = WebServiceClientManager
	                                                                               .getClient("picr");

	//	private static Icon LOGO;
	private Map<String, String> resMap = new ConcurrentHashMap<String, String>();

	/**
	 * Creates a new NCBIGenePanel object.
	 * @throws CyWebServiceException
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public PICRPanel() throws CyWebServiceException {
		this(((WebServiceClientGUI) picr).getIcon(null),
		     " Protein Identifier Cross-Reference Service", "Available Attributes");
	}

	/**
	 * Creates a new NCBIGenePanel object.
	 *
	 * @param logo  DOCUMENT ME!
	 * @param title  DOCUMENT ME!
	 * @param attrPanelName  DOCUMENT ME!
	 *
	 * @throws IOException  DOCUMENT ME!
	 * @throws CyWebServiceException
	 */
	public PICRPanel(Icon logo, String title, String attrPanelName) throws CyWebServiceException {
		super(logo, title, attrPanelName);
		initDataSources();
	}

	private void initDataSources() throws CyWebServiceException {
		this.databaseComboBox.addItem("PICR");
		databaseComboBox.setEnabled(false);
		setDataType();
	}

	private void setDataType() throws CyWebServiceException {
		this.attributeTypeComboBox.addItem("Any Protein ID");
		attributeTypeComboBox.setEnabled(false);

		model = new DefaultListModel();
		attrList.setModel(model);

		try {
			for (String dispAttrName : picr.getClientStub().getMappedDatabaseNames()) {
				model.addElement(dispAttrName);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			throw new CyWebServiceException(WSErrorCode.REMOTE_EXEC_FAILED);
		}
	}

	protected void importButtonActionPerformed(ActionEvent e) {
		importAttributes();
	}

	@Override
	protected void databaseComboBoxActionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}

	protected void resetButtonActionPerformed(ActionEvent ac) {
		model = new DefaultListModel();
		attrList.setModel(model);

		String[] dbNames = null;

		try {
			dbNames = picr.getClientStub().getMappedDatabaseNames();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String dispAttrName : dbNames)
			model.addElement(dispAttrName);
	}

	@Override
	protected void importAttributes() {
		String keyInHeader = null;

		// Create Task
		final ImportAttributeTask task = new ImportAttributeTask(keyInHeader,
		                                                         attributeComboBox.getSelectedItem()
		                                                                          .toString());

		// Configure JTask Dialog Pop-Up Box
		final JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayCancelButton(false);
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
			// TODO Auto-generated method stub
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void run() {
			taskMonitor.setStatus("Importing annotation from Entrez Gene.\n\nIt may take a while.\nPlease wait...");
			taskMonitor.setPercentCompleted(-1);

			Object[] selectedAttr = attrList.getSelectedValues();
			AttributeImportQuery qObj = new AttributeImportQuery(selectedAttr, keyAttrName, key);

			try {
				WebServiceClientManager.getCyWebServiceEventSupport()
				                       .fireCyWebServiceEvent(new CyWebServiceEvent("picr",
				                                                                    WSEventType.IMPORT_ATTRIBUTE,
				                                                                    qObj));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				taskMonitor.setException(e, "Could not import ids.");

				return;
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
