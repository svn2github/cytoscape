package cytoscape.actions;

import cytoscape.data.webservice.ui.UnifiedNetworkImportDialog;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;

import java.awt.event.ActionEvent;

public class WebServiceNetworkImportAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339870958212L;
	/**
	 * Creates a new FitContentAction object.
	 */
	private final CytoscapeDesktop desktop;
	private final UnifiedNetworkImportDialog dialog;
	public WebServiceNetworkImportAction(final CytoscapeDesktop desktop) {
		super("Network from web services...");
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
