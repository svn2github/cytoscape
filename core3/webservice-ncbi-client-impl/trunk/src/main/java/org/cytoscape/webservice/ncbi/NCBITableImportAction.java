package org.cytoscape.webservice.ncbi;

import java.awt.event.ActionEvent;
import java.util.Map;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.webservice.ncbi.ui.NCBIGeneDialog;

public class NCBITableImportAction extends AbstractCyAction {
	
	private static final long serialVersionUID = 3101400401346193602L;
	
	final CyTableManager tblManager;
	final CyNetworkManager netManager;

	public NCBITableImportAction(final CyTableManager tblManager, final CyNetworkManager netManager, CyApplicationManager applicationManager) {
		super("Import Data Table from NCBI...", applicationManager);
		setPreferredMenu("File.Import");
		this.tblManager = tblManager;
		this.netManager = netManager;
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		NCBIGeneDialog dialog = new NCBIGeneDialog(tblManager, netManager);
		dialog.setVisible(true);
	}

}
