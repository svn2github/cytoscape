package edu.ucsd.bioeng.coreplugin.tableImport.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import edu.ucsd.bioeng.coreplugin.tableImport.ui.ImportTextTableDialog;

public class ImportNetworkTableAction extends CytoscapeAction {
	public ImportNetworkTableAction() {
		super("Network from Table (Text/MS Excel)...");
		setPreferredMenu("File.Import");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ImportTextTableDialog iad;
		try {
			iad = new ImportTextTableDialog(Cytoscape
					.getDesktop(), true, ImportTextTableDialog.NETWORK_IMPORT);
			iad.pack();
			iad.setLocationRelativeTo(Cytoscape.getDesktop());
			iad.setVisible(true);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
