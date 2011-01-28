package org.cytoscape.webservice.internal.task;

import java.awt.Window;
import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.webservice.internal.ui.UnifiedNetworkImportDialog;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

/**
 * Add menu item to open Unified Network Import GUI.
 *
 */
public class ShowNetworkImportDialogAction extends AbstractCyAction {

	private static final long serialVersionUID = -36712860667900147L;
	
	private UnifiedNetworkImportDialog dialog;
	private final TaskManager taskManager;
	
	private final Window parent;

	public ShowNetworkImportDialogAction(final CyApplicationManager applicationManager, final CySwingApplication app, final TaskManager taskManager) {
		super("Public Databases...", applicationManager);
		setPreferredMenu("File.Import.Network");
		
		if(taskManager == null)
			throw new NullPointerException("TaskMonitor is null.");
		
		this.taskManager = taskManager;
		this.parent = app.getJFrame();
		dialog = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(dialog == null)
			dialog = new UnifiedNetworkImportDialog(taskManager);
		
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		
	}

}
