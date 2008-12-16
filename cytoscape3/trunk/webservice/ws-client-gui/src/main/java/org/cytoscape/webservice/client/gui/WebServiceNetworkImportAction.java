package org.cytoscape.webservice.client.gui;

import java.awt.event.ActionEvent;

import cytoscape.CyNetworkManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CySwingApplication;
import cytoscape.view.CytoscapeDesktop;

public class WebServiceNetworkImportAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870958212L;
	/**
	 * Creates a new FitContentAction object.
	 */
	private final CySwingApplication desktop;
	private final UnifiedNetworkImportDialog dialog;
	
	public WebServiceNetworkImportAction(final CySwingApplication desktop, CyNetworkManager cyNetworkManager) {
		super("Network from web services...", cyNetworkManager);
		setPreferredMenu("File.Import");
		this.desktop = desktop;
		dialog = new UnifiedNetworkImportDialog(desktop,false);
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
