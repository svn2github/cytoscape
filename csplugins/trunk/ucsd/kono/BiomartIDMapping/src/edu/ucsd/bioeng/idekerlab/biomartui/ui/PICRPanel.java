package edu.ucsd.bioeng.idekerlab.biomartui.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.Icon;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.swing.AttributeImportPanel;
import edu.ucsd.bioeng.idekerlab.picrclient.PICRClient;
import embl.AccessionMapperInterface;


public class PICRPanel extends AttributeImportPanel {
	protected static WebServiceClient picr = WebServiceClientManager.getClient("picr");
//	private static final Icon LOGO = new ImageIcon(PICRPanel.class.getResource("/images/entrez_page_title.gif"));

//	private static Icon LOGO;
	
	private Map<String, String> resMap = new ConcurrentHashMap<String, String>();
	
	
	/**
	 * Creates a new NCBIGenePanel object.
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public PICRPanel() throws IOException {
		this(null, "PICR", "Available Attributes");
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
	public PICRPanel(Icon logo, String title, String attrPanelName) throws IOException {
		super(logo, title, attrPanelName);
		initDataSources();
	}

	private void initDataSources() {
		this.databaseComboBox.addItem("PICR");
		databaseComboBox.setEnabled(false);
		setDataType();
	}

	private void setDataType() {
		this.attributeTypeComboBox.addItem("Any ACC");
		attributeTypeComboBox.setEnabled(false);
		
		model = new DefaultListModel();
		attrList.setModel(model);
		for(String dispAttrName : ((AccessionMapperInterface)((PICRClient)picr).getClientStub()).getMappedDatabaseNames()) {
			model.addElement(dispAttrName);
		}
	}

	protected void importButtonActionPerformed(ActionEvent e) {
		System.out.println("======================PW Import =================");
		importAttributes();
	}

	

	@Override
	protected void databaseComboBoxActionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void importAttributes() {
		String keyInHeader = null;
		// Create Task
		final ImportAttributeTask task = new ImportAttributeTask(keyInHeader, null);

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

			
			AttributeImportQuery qObj = new AttributeImportQuery(null, key, keyAttrName);

			try {
				WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(new CyWebServiceEvent("picr", WSEventType.IMPORT_ATTRIBUTE, qObj));
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
