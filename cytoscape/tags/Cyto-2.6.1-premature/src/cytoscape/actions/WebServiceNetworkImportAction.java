package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.data.webservice.ui.UnifiedNetworkImportDialog;
import cytoscape.util.CytoscapeAction;

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
