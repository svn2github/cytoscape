package org.cytoscape.webservice.client.gui;

import java.awt.event.ActionEvent;

import cytoscape.CyNetworkManager;
import cytoscape.util.CytoscapeAction;

public class WebServiceNetworkImportAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870958212L;
	/**
	 * Creates a new FitContentAction object.
	 */
	private final UnifiedNetworkImportDialog dialog;
	
	public WebServiceNetworkImportAction(CyNetworkManager cyNetworkManager, UnifiedNetworkImportDialog dialog) {
		super("Network from web services...", cyNetworkManager);
		setPreferredMenu("File.Import");
		this.dialog = dialog; 
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		dialog.setVisible(true);
	}
}
