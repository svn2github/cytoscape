package org.cytoscape.app.internal.action;

import java.awt.event.ActionEvent;

import org.cytoscape.app.internal.manager.AppManager;
import org.cytoscape.app.internal.ui.AppManagerDialog;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.FileUtil;

public class AppManagerAction extends AbstractCyAction {

	/** Long serial version identifier required by the Serializable class */
	private static final long serialVersionUID = -9145570324785249730L;
	
	/**
	 * A reference to the main Cytoscape window used to position the App Manager dialog.
	 */
	private CySwingApplication swingApplication;
	
	/**
	 * A reference to the {@link AppManager} service.
	 */
	private AppManager appManager;
	
	/**
	 * A reference to the {@link FileUtil} OSGi service used for displaying a filechooser dialog
	 */
	private FileUtil fileUtil;
	
	/**
	 * Creates and sets up the AbstractCyAction, placing an item into the menu.
	 */
	public AppManagerAction(AppManager appManager, CySwingApplication swingApplication, FileUtil fileUtil) {
		super("App Manager 2");
		
		setPreferredMenu("Apps");
		setMenuGravity(1.0f);
		
		this.appManager = appManager;
		this.swingApplication = swingApplication;
		this.fileUtil = fileUtil;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// Create and display the App Manager dialog
		AppManagerDialog appManagerDialog = new AppManagerDialog(appManager, fileUtil, swingApplication.getJFrame(), false);
	}

}
