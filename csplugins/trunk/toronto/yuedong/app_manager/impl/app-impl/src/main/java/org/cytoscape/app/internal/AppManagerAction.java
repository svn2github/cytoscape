package org.cytoscape.app.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.app.internal.swing.main.AppManagerDialog;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

public class AppManagerAction extends AbstractCyAction {

	/** Long serial version identifier required by the Serializable class */
	private static final long serialVersionUID = -9145570324785249730L;
	
	/**
	 * A reference to the main Cytoscape window used to position the App Manager dialog
	 */
	private CySwingApplication swingApplication;
	
	public AppManagerAction(CySwingApplication swingApplication) {
		super("App Manager2");
		
		setPreferredMenu("Apps");
		setMenuGravity(1.0f);
		
		this.swingApplication = swingApplication;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		AppManagerDialog appManagerDialog = new AppManagerDialog(swingApplication.getJFrame(), true);
		
		appManagerDialog.setVisible(true);
	}

}
