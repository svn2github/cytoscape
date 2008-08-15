package cytoscape.actions;

import cytoscape.data.webservice.ui.UnifiedNetworkImportDialog;
import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;

public class WebServiceNetworkImportAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870958212L;
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
