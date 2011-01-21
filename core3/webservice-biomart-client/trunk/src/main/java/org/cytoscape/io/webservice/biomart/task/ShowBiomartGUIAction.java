package org.cytoscape.io.webservice.biomart.task;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.webservice.biomart.BiomartClient;
import org.cytoscape.io.webservice.biomart.ui.BiomartMainDialog;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.ValuedTask;
import org.cytoscape.work.ValuedTaskExecutor;
import org.cytoscape.work.swing.GUITaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO: Add cancel function
 */
public class ShowBiomartGUIAction extends AbstractCyAction {

	private static final long serialVersionUID = -1329132199540543764L;

	private static final Logger logger = LoggerFactory
			.getLogger(ShowBiomartGUIAction.class);

	private BiomartMainDialog dialog;

	private final BiomartClient client;
	private final GUITaskManager taskManager;
	private final CyApplicationManager appManager;
	private final CyTableManager tblManager;
	private final CySwingApplication app;

	public ShowBiomartGUIAction(final BiomartClient client,
			final TaskManager taskManager,
			final CyApplicationManager appManager,
			final CyTableManager tblManager, final CySwingApplication app) {
		super("Import Tables from Biomart...", appManager);
		setPreferredMenu("File.Import");

		this.app = app;
		this.client = client;
		this.taskManager = (GUITaskManager) taskManager;
		this.appManager = appManager;
		this.tblManager = tblManager;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// Lazy instantiation. This process depends on network connection.
		if (dialog == null) {			
			logger.debug("BioMart Dialog initialization process start.");
			dialog = new BiomartMainDialog(client, taskManager, appManager, tblManager, app);
			logger.info("BioMart Client dialog initialized.");
		}
		
		dialog.setLocationRelativeTo(app.getJFrame());
		dialog.setVisible(true);
		
		logger.info("BioMart Client initialized.");
	}
}
