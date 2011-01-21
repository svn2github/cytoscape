package org.cytoscape.webservice.internal.task;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.session.CyApplicationManager;

public class ShowNetworkImportDialogAction extends AbstractCyAction {

	private static final long serialVersionUID = -36712860667900147L;

	public ShowNetworkImportDialogAction(CyApplicationManager applicationManager) {
		super("Import Network from Public Databases...", applicationManager);
		setPreferredMenu("File.Import");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
