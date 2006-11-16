package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.data.servers.ui.ImportTextTableDialog;
import cytoscape.util.CytoscapeAction;

public class ImportNetworkTableAction extends CytoscapeAction {
	public ImportNetworkTableAction() {
		super("Network from Table (Text/MS Excel\u2122)...");
		setPreferredMenu("File.Import");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ImportTextTableDialog iad = new ImportTextTableDialog(Cytoscape
				.getDesktop(), true, ImportTextTableDialog.NETWORK_IMPORT);
		iad.pack();
		iad.setLocationRelativeTo(Cytoscape.getDesktop());
		iad.setVisible(true);
	}
}
