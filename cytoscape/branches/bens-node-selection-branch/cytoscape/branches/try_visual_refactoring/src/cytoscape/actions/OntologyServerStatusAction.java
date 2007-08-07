package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.data.servers.ui.OntologyServerStatusDialog;
import cytoscape.util.CytoscapeAction;

/**
 * <p>
 * Display Ontology Status Dialog.  will be used to 
 * add/remove ontologies from OntologyServer.
 * </p>
 * @version 1.0
 * @since Cytoscape 2.4
 * @author kono
 *
 */
public class OntologyServerStatusAction extends CytoscapeAction {

	public OntologyServerStatusAction() {
		super("Ontology Server Status...");
		setPreferredMenu("File.Import.Ontology");

		setName("load");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		OntologyServerStatusDialog osd = new OntologyServerStatusDialog(
				Cytoscape.getDesktop(), true);
		osd.pack();
		osd.setLocationRelativeTo(Cytoscape.getDesktop());
		osd.setVisible(true);
	}
}
