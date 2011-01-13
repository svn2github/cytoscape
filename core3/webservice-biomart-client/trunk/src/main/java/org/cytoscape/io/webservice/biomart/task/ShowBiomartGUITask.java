package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.BiomartClient;
import org.cytoscape.io.webservice.biomart.ui.BiomartMainDialog;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * TODO: Add cancel function
 */
public class ShowBiomartGUITask extends AbstractTask {

	private BiomartMainDialog dialog;
	
	final BiomartClient client;
	final TaskManager taskManager;
	final CyApplicationManager appManager;
	final CyTableManager tblManager;

	public ShowBiomartGUITask(
			final BiomartClient client,
			final TaskManager taskManager,
			final CyApplicationManager appManager,
			final CyTableManager tblManager) {
		
		this.client = client;
		this.taskManager = taskManager;
		this.appManager = appManager;
		this.tblManager = tblManager;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		// Lazy instantiation.  This process depends on network connection.
		if (dialog == null) {
			taskMonitor
					.setStatusMessage("Checking available Mart services.\n\nThis process may take a while.\nPlease wait...");
			taskMonitor.setProgress(0.0);
			dialog = new BiomartMainDialog(client, taskManager, appManager, tblManager);
			taskMonitor.setProgress(1.0);
		}

		// mainDialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.setVisible(true);
	}

}
