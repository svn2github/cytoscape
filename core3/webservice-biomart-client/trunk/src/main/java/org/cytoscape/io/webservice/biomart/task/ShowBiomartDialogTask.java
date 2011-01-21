package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.webservice.biomart.BiomartClient;
import org.cytoscape.io.webservice.biomart.ui.BiomartMainDialog;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.GUITaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowBiomartDialogTask extends AbstractTask {
	
	private static final Logger logger = LoggerFactory.getLogger(ShowBiomartDialogTask.class);
	
	private BiomartMainDialog dialog;
	
	private final BiomartClient client;
	private final GUITaskManager taskManager;
	private final CyApplicationManager appManager;
	private final CyTableManager tblManager;
	private final CySwingApplication app;
	
	private final LoadRepositoryTask loadTask;
	
	public ShowBiomartDialogTask(final BiomartClient client,
			final TaskManager taskManager,
			final CyApplicationManager appManager,
			final CyTableManager tblManager, final CySwingApplication app, final LoadRepositoryTask loadTask) {
		
		this.app = app;
		this.client = client;
		this.taskManager = (GUITaskManager) taskManager;
		this.appManager = appManager;
		this.tblManager = tblManager;
		this.loadTask = loadTask;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if(dialog == null) {
			final LoadRepositoryResult result = loadTask.getResult();
			dialog = new BiomartMainDialog(client, taskManager, appManager, tblManager, app, result);
			dialog.setLocationRelativeTo(app.getJFrame());
			dialog.setVisible(true);
			
			logger.info("BioMart Client initialized.");
		}
	}
	
	public BiomartMainDialog getDialog() {
		return dialog;
	}
}
