package org.cytoscape.application.action;

import java.awt.event.ActionEvent;

import org.cytoscape.application.dialog.webservice.UnifiedNetworkImportDialog;



public class WebServiceNetworkImportAction extends CytoscapeAction {
	/**
	 * Creates a new FitContentAction object.
	 */
	public WebServiceNetworkImportAction() {
		super("Network from web services...");
		setPreferredMenu("File.Import");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		UnifiedNetworkImportDialog.showDialog();
	}
}
