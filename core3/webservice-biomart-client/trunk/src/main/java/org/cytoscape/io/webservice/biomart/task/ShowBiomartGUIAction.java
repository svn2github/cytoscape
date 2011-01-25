package org.cytoscape.io.webservice.biomart.task;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.webservice.biomart.BiomartClient;
import org.cytoscape.io.webservice.biomart.ui.BiomartAttrMappingPanel;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.swing.GUITaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO: Add cancel function
 */
public class ShowBiomartGUIAction extends AbstractCyAction {

	private static final long serialVersionUID = -1329132199540543764L;

	private static final Logger logger = LoggerFactory.getLogger(ShowBiomartGUIAction.class);

	private final GUITaskManager taskManager;
	private final CySwingApplication app;
	
	private ShowBiomartDialogTask showDialogTask;
	
	private final LoadRepositoryTask firstTask;

	public ShowBiomartGUIAction(final BiomartAttrMappingPanel panel, final BiomartClient client,
			final TaskManager taskManager,
			final CyApplicationManager appManager,
			final CySwingApplication app) {
		super("Import Tables from Biomart...", appManager);
		setPreferredMenu("File.Import");

		this.app = app;
		this.taskManager = (GUITaskManager) taskManager;
		
		this.firstTask = new LoadRepositoryTask(client.getRestClient());
		this.showDialogTask = new ShowBiomartDialogTask(panel, client, app, firstTask);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// Lazy instantiation. This process depends on network connection.
		if (showDialogTask.getDialog() == null) {			
			initDialog();
		} else {
			showDialogTask.getDialog().setLocationRelativeTo(app.getJFrame());
			showDialogTask.getDialog().setVisible(true);
		}
	}
	
	
	private void initDialog() {
		
		final BioMartTaskFactory tf = new BioMartTaskFactory(firstTask);
		tf.getTaskIterator().insertTasksAfter(firstTask, showDialogTask);
		((GUITaskManager) taskManager).setParent(app.getJFrame());
		
		taskManager.execute(tf);
	}
}
